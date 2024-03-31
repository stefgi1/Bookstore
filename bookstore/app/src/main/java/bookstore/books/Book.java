package bookstore.books;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Book {
	private String ISBN;
	private String title;
	private String category;
	private String supplier;
	private LocalDate purchaseDate;
	private double purchasePrice;
	private double originalPrice;
	private double sellingPrice;
	private String author;
	private int stock;

	public Book(){}
	public Book(Scanner scanner){
		fromStream(scanner);
	}

	public Book(String ISBN, String title, String category, String supplier,
			LocalDate purchaseDate, double purchasePrice, double originalPrice,
			double sellingPrice, String author, int stock){
		this.ISBN = ISBN;
		this.title = title;
		this.category = category;
		this.supplier = supplier;
		this.purchaseDate = purchaseDate;
		this.purchasePrice = purchasePrice;
		this.originalPrice = originalPrice;
		this.sellingPrice = sellingPrice;
		this.author = author;
		this.stock = stock;
	}


	public String getISBN() {
		return ISBN;
	}
	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getSupplier() {
		return supplier;
	}
	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}
	public LocalDate getPurchaseDate() {
		return purchaseDate;
	}
	public void setPurchaseDate(LocalDate purchaseDate) {
		this.purchaseDate = purchaseDate;
	}
	public double getPurchasePrice() {
		return purchasePrice;
	}
	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}
	public double getOriginalPrice() {
		return originalPrice;
	}
	public void setOriginalPrice(double originalPrice) {
		this.originalPrice = originalPrice;
	}
	public double getSellingPrice() {
		return sellingPrice;
	}
	public void setSellingPrice(double sellingPrice) {
		this.sellingPrice = sellingPrice;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public int getStock() {
		return stock;
	}
	public void setStock(int stock) {
		this.stock = stock;
	}

	public void toStream(PrintStream stream){
		stream.println(ISBN);
		stream.println(title);
		stream.println(category);
		stream.println(supplier);
		stream.println(purchaseDate.
				format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		stream.println(purchasePrice);
		stream.println(originalPrice);
		stream.println(sellingPrice);
		stream.println(author);
		stream.println(stock);
	}

	public void fromStream(Scanner scanner){
		ISBN = scanner.nextLine();
		title = scanner.nextLine();
		category = scanner.nextLine();
		supplier = scanner.nextLine();
		purchaseDate = LocalDate.parse(scanner.nextLine(),
				DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		purchasePrice = Double.parseDouble(scanner.nextLine());
		originalPrice = Double.parseDouble(scanner.nextLine());
		sellingPrice = Double.parseDouble(scanner.nextLine());
		author = scanner.nextLine();
		stock = Integer.parseInt(scanner.nextLine());
	}
}
