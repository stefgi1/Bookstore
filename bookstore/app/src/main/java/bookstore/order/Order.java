package bookstore.order;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import java.util.*;

import bookstore.users.*;
import bookstore.Actor;
import bookstore.books.*;

public class Order{
	private static int nextId = 1;

	public static void setNextId(int id) {
		Order.nextId = id;
	}

	private int id;
	private Book book;
	private double cost;
	private LocalDate date;
	private User soldBy;

	public Order(User soldBy, Book book, double cost){
		this.id = nextId ++;
		this.soldBy = soldBy;
		this.book = book;
		this.cost = cost;
		this.date = LocalDate.now();
	}

	public Order(Scanner scanner, BookStore books, UserStore users){
		fromStream(scanner, books, users);
	}

	public int getId() {
		return id;
	}

	public Book getBook() {
		return book;
	}

	public double getCost() {
		return cost;
	}

	public LocalDate getDate() {
		return date;
	}

	public User getSoldBy() {
		return soldBy;
	}

	public void toStream(PrintStream stream){
		stream.println(id);
		stream.println(book.getISBN());
		stream.println(cost);
		stream.println(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		stream.println(soldBy.getUsername());
	}

	public void fromStream(Scanner scanner, BookStore books, UserStore users){
		id = Integer.parseInt(scanner.nextLine());
		book = books.getBooks().get(books.getIndex(scanner.nextLine()));
		cost = Double.parseDouble(scanner.nextLine());
		date = LocalDate.parse(scanner.nextLine(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		soldBy = users.getUser(scanner.nextLine());
	}
}
