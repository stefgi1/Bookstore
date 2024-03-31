package bookstore;

import java.util.*;
import java.io.*;
import java.time.*;

import bookstore.order.*;
import bookstore.users.*;
import bookstore.books.*;

public class Actor{
	private User user;
	private UserStore users;
	private Privs privs;
	private OrderStore orders;
	private BookStore books;

	public Actor(User user, UserStore users, Privs privs, OrderStore orders,
			BookStore books){
		this.user = user;
		this.users = users;
		this.privs = privs;
		this.orders = orders;
		this.books = books;
		load();
		if (users.getUsers().size() == 0){
			// add admin user
			User admin = Admin.create("admin", "abcd");
			users.getUsers().add(admin);
			privs.toDefault();
		}
	}

	public Actor(){
		this.user = null;
		this.users = new UserStore();
		this.privs = new Privs();
		this.orders = new OrderStore();
		this.books = new BookStore();
		load();
		if (users.getUsers().size() == 0){
			// add admin user
			User admin = Admin.create("admin", "abcd");
			users.getUsers().add(admin);
			privs.toDefault();
		}
	}

	public void save(){
		users.save("users");
		privs.save("privs");
		orders.save("orders");
		books.save("books");
	}

	public void load(){
		users.load("users");
		privs.load("privs");
		books.load("books");
		orders.load("orders", books, users);
	}

	public Set<String> getPrivs(){
		if (user == null)
			return new HashSet<>();
		return privs.getPrivileges(user);
	}

	public void login(String username, String password)
			throws Exception {
		if (!users.login(username, password))
			throw new Exception("Invalid credentials");
		user = users.getUser(username);
	}

	public void bookCheckout(Book book, double cost)
			throws PrivException, Exception {
		privs.assertPriv(user, "checkout");
		if (books.getStock(book) == 0)
			throw new Exception("Out of stock");
		Order order = new Order(user, book, cost);
		orders.getOrders().add(order);
		books.setStock(book, books.getStock(book) - 1);
	}

	public void bookAdd(Book book, int count)
			throws PrivException {
		privs.assertPriv(user, "manageStock");
		books.add(book, count);
	}

	public void bookRemove(Book book)
			throws PrivException{
		privs.assertPriv(user, "manageStock");
		books.remove(books.getIndex(book.getISBN()));
	}

	public void bookSetStock(Book book, int count)
			throws PrivException {
		privs.assertPriv(user, "manageStock");
		books.setStock(book, count);
	}

	public Map<String, Double> perfReview(LocalDate after, LocalDate before){
		Map<String, Double> ret = new HashMap<>();
		OrderFilter filter = new OrderFilter(orders.getOrders());
		filter.setAfter(after);
		filter.setBefore(before);
		for (User user : users.getUsers()){
			if (!user.getRole().equals("librarian"))
				continue;
			filter.setSoldBy(user);
			double sales = 0;
			for (Order order : filter.filter())
				sales += order.getCost();
			ret.put(user.getUsername(), sales);
		}
		return ret;
	}

	public void userAdd(User user)
			throws PrivException{
		privs.assertPriv(user, "manageUsers");
		users.getUsers().add(user);
	}

	public void userRemove(User user)
			throws PrivException{
		privs.assertPriv(user, "manageUsers");
		users.getUsers().remove(users.getIndex(user.getUsername()));
	}
}
