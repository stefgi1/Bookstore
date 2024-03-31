package bookstore.order;

import java.util.*;

import bookstore.books.BookStore;
import bookstore.users.UserStore;

import java.io.*;

public class OrderStore{
	private List<Order> orders;

	public OrderStore(){
		orders = new ArrayList<>();
	}

	public List<Order> getOrders(){
		return orders;
	}

	public void save(String filename) {
		try (PrintStream stream = new PrintStream(new File(filename))){
			for (Order order : orders)
				order.toStream(stream);
		} catch (IOException e) {
			System.err.println("Error saving privileges to file: " + e.getMessage());
		}
	}

	public void load(String filename, BookStore books, UserStore users) {
		orders = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File(filename))) {
			while (scanner.hasNextLine()){
				orders.add(new Order(scanner, books, users));
			}
		} catch (IOException e) {
			System.err.println("Error loading privileges from file: " +
					e.getMessage());
		}
	}
}
