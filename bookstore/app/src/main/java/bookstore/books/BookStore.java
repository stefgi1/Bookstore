package bookstore.books;

import java.io.*;
import java.util.*;

public class BookStore{
	private List<Book> books;
	private List<Integer> stock;

	public BookStore(){
		books = new ArrayList<>();
		stock = new ArrayList<>();
	}

	public List<Book> getBooks(){
		return books;
	}

	public List<Integer> getStock(){
		return stock;
	}

	public int getIndex(String isbn){
		for (int i = 0; i < books.size(); i ++){
			if (books.get(i).getISBN().equals(isbn))
				return i;
		}
		return -1;
	}

	public int getStock(Book book){
		int index = getIndex(book.getISBN());
		if (index < 0)
			return 0;
		return stock.get(index);
	}

	public void setStock(Book book, int count){
		int index = getIndex(book.getISBN());
		if (index < 0)
			return;
		stock.set(index, count);
	}

	public void add(Book book, int count){
		books.add(book);
		stock.add(count);
	}

	public void remove(int index){
		books.remove(index);
		stock.remove(index);
	}

	public void save(String filename) {
		try (PrintStream stream = new PrintStream(new File(filename))){
			for (Book book : books)
				book.toStream(stream);
		} catch (IOException e) {
			System.err.println("Error saving privileges to file: " + e.getMessage());
		}
	}

	public void load(String filename) {
		books = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File(filename))) {
			while (scanner.hasNextLine()){
				books.add(new Book(scanner));
			}
		} catch (IOException e) {
			System.err.println("Error loading privileges from file: " +
					e.getMessage());
		}
	}
}
