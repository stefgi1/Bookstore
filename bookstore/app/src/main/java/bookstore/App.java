package bookstore;

import java.time.LocalDate;
import java.util.*;

import bookstore.books.*;
import bookstore.order.*;
import bookstore.users.*;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class App extends Application {

	private UserStore users = new UserStore();
	private Privs privs = new Privs();
	private OrderStore orders = new OrderStore();
	private BookStore books = new BookStore();
	private Actor actor = new Actor(null, users, privs, orders, books);

	private TableView<Book> booksTable;
	private ObservableList<Book> booksData;
	private TextField stockUpdateField;
	private Button updateStockButton;

	private TextField isbnField;
	private TextField titleField;
	private TextField categoryField;
	private TextField supplierField;
	private DatePicker purchaseDateDatePicker;
	private TextField purchasePriceField;
	private TextField originalPriceField;
	private TextField sellingPriceField;
	private TextField authorField;
	private TextField stockField;

	private TableView<User> usersTable;
	private ObservableList<User> usersData;

	private TextField usernameField;
	private PasswordField passwordField;
	private TextField roleField;

	private CheckBox librarianCheckoutCheckbox;
	private CheckBox librarianManageStockCheckbox;
	private CheckBox librarianPerfReviewCheckbox;
	private CheckBox librarianManageUsersCheckbox;
	private CheckBox managerCheckoutCheckbox;
	private CheckBox managerManageStockCheckbox;
	private CheckBox managerPerfReviewCheckbox;
	private CheckBox managerManageUsersCheckbox;
	private Button applyPrivilegesButton;

	private Stage primaryStage;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Login Form");
		this.primaryStage.setOnCloseRequest(event -> {
			actor.save();
		});

		showLoginForm();
	}

	private void showLoginForm() {
		GridPane grid = new GridPane();
		grid.setAlignment(javafx.geometry.Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		Label usernameLabel = new Label("Username:");
		grid.add(usernameLabel, 0, 1);

		TextField usernameField = new TextField();
		grid.add(usernameField, 1, 1);

		Label passwordLabel = new Label("Password:");
		grid.add(passwordLabel, 0, 2);

		PasswordField passwordField = new PasswordField();
		grid.add(passwordField, 1, 2);

		Button loginButton = new Button("Login");
		grid.add(loginButton, 1, 3);

		loginButton.setOnAction(e -> {
			try {
				// Call actor.login(username, password)
				actor.login(usernameField.getText(), passwordField.getText());

				// If successful, show the other form
				showSuccessForm();
			} catch (Exception ex) {
				// If login fails, show error message
				showErrorAlert("Login Failed", ex.getMessage());
				ex.printStackTrace();
			}
		});

		Scene scene = new Scene(grid, 300, 200);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private void showSuccessForm() {
		primaryStage.setTitle("Library Management");
		TabPane tabPane = new TabPane();

		// Assuming actor.getPrivs() returns a Set<String> of privileges
		Set<String> privileges = actor.getPrivs();
		System.out.println(privileges == null);

		if (privileges.contains("checkout")) {
			Tab checkoutTab = createCheckoutTab();
			// Add content for the "Checkout" tab if needed
			tabPane.getTabs().add(checkoutTab);
		}

		if (privileges.contains("manageStock")) {
			Tab booksTab = createBooksTab();
			Tab addBookTab = createAddBookTab();
			tabPane.getTabs().addAll(booksTab, addBookTab);
		}

		if (privileges.contains("manageUsers")) {
			Tab usersTab = createUsersTab();
			Tab addUserTab = createAddUserTab();
			Tab privsTab = createPrivilegesTab();
			usersTab.setClosable(false);
			addUserTab.setClosable(false);
			privsTab.setClosable(false);
			tabPane.getTabs().addAll(usersTab, addUserTab, privsTab);
		}
		Scene scene = new Scene(tabPane, 640, 400);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private Tab createCheckoutTab() {
		Tab checkoutTab = new Tab("Checkout");
		checkoutTab.setClosable(false); // Make the tab not closable

		// Create UI elements for the "Checkout" tab
		Label isbnLabel = new Label("ISBN:");
		TextField isbnTextField = new TextField();

		//Label priceLabel = new Label("Price:");
		//Spinner<Double> priceSpinner = new Spinner<>(0.0, Double.MAX_VALUE, 0.0, 0.1);

		Button checkoutButton = new Button("Checkout");

		// Set the layout of the UI elements
		GridPane gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(20, 20, 20, 20));

		gridPane.add(isbnLabel, 0, 0);
		gridPane.add(isbnTextField, 1, 0);
		//gridPane.add(priceLabel, 0, 1);
		//gridPane.add(priceSpinner, 1, 1);
		gridPane.add(checkoutButton, 0, 2, 2, 1);

		checkoutTab.setContent(gridPane);

		// Add an event handler for the checkout button
		checkoutButton.setOnAction(e -> {
			// Perform checkout logic here
			String isbn = isbnTextField.getText();
			//double price = priceSpinner.getValue();
			int index = books.getIndex(isbn);
			if (index < 0){
				showErrorAlert("Error", "Book with isbn `" + isbn +
						"` does not exist");
				return;
			}
			Book book = books.getBooks().get(index);
			try{
				actor.bookCheckout(book, book.getSellingPrice());
			} catch (Exception ex){
				showErrorAlert("Error", "Book with isbn `" + isbn +
						"` does not exist");
				ex.printStackTrace();
				return;
			}
			Order order = orders.getOrders().get(orders.getOrders().size() - 1);
			showSuccessAlert("Checked Out", "Book was checked out. Order id: " +
					order.getId());
			System.out.println("Checkout - ISBN: " + isbn );
		});

		return checkoutTab;
	}

	private Tab createBooksTab(){
		Tab booksTab = new Tab("Books");
		booksTab.setClosable(false); // Make the tab not closable

		// Create UI elements for the "Books" tab
		booksTable = new TableView<>();
		booksData = FXCollections.observableArrayList();

		TableColumn<Book, String> isbnColumn = new TableColumn<>("ISBN");
		isbnColumn.setCellValueFactory(new PropertyValueFactory<>("ISBN"));

		TableColumn<Book, String> titleColumn = new TableColumn<>("Title");
		titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

		TableColumn<Book, String> categoryColumn = new TableColumn<>("Category");
		categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));

		TableColumn<Book, String> supplierColumn = new TableColumn<>("Supplier");
		supplierColumn.setCellValueFactory(new PropertyValueFactory<>("supplier"));

		TableColumn<Book, LocalDate> purchaseDateColumn = new TableColumn<>("Purchase Date");
		purchaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseDate"));

		TableColumn<Book, Double> purchasePriceColumn = new TableColumn<>("Purchase Price");
		purchasePriceColumn.setCellValueFactory(new PropertyValueFactory<>("purchasePrice"));

		TableColumn<Book, Double> originalPriceColumn = new TableColumn<>("Original Price");
		originalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("originalPrice"));

		TableColumn<Book, Double> sellingPriceColumn = new TableColumn<>("Selling Price");
		sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("sellingPrice"));

		TableColumn<Book, String> authorColumn = new TableColumn<>("Author");
		authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

		TableColumn<Book, Integer> stockColumn = new TableColumn<>("Stock");
		stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

		booksTable.getColumns().addAll(isbnColumn, titleColumn, categoryColumn, supplierColumn,
				purchaseDateColumn, purchasePriceColumn, originalPriceColumn, sellingPriceColumn,
				authorColumn, stockColumn);

		// Add a button for deleting the selected book
		Button deleteButton = new Button("Delete");
		deleteButton.setOnAction(e -> {
			Book selectedBook = booksTable.getSelectionModel().getSelectedItem();
			if (selectedBook != null) {
				// Delete the selected book using actor.bookRemove(isbn)
				try{
					actor.bookRemove(selectedBook);
				} catch (Exception ex){
					showErrorAlert("Error", ex.getMessage());
				ex.printStackTrace();
					return;
				}

				// Refresh the table after deletion
				refreshBooksTable();
			} else {
				// No book selected, show an alert
				showErrorAlert("No Book Selected", "Please select a book to delete.");
			}
		});

		stockUpdateField = new TextField();
		updateStockButton = new Button("Update Stock");

		VBox vBox = new VBox(10, booksTable, deleteButton, stockUpdateField, updateStockButton);
		vBox.setPadding(new Insets(20));

		updateStockButton.setOnAction(e -> handleUpdateStockButton(booksTable.getSelectionModel().getSelectedItem()));

		booksTab.setContent(vBox);

		// Fetch and display the initial list of books
		refreshBooksTable();

		return booksTab;
	}

	private void handleUpdateStockButton(Book selectedBook) {
		if (selectedBook != null) {
			try {
				int newStock = Integer.parseInt(stockUpdateField.getText());
				selectedBook.setStock(newStock);

				// Show a success message
				showAlert("Success", "Stock updated successfully");

				// Refresh the books table
				refreshBooksTable();
			} catch (NumberFormatException e) {
				// Show an error message for invalid input
				showAlert("Error", "Please enter a valid stock amount");
			} catch (Exception e) {
				// Show an error message for other exceptions
				showAlert("Error", "Failed to update stock: " + e.getMessage());
			}
		} else {
			// Show an error message if no book is selected
			showAlert("Error", "Please select a book to update its stock");
		}
	}

	private void refreshBooksTable() {
		// Fetch the list of books using books.getBooks()
		List<Book> bookList = books.getBooks();

		// Clear and update the data in the table
		booksData.clear();
		booksData.addAll(bookList);
		booksTable.setItems(booksData);
	}

	private Tab createAddBookTab() {
		Tab addBookTab = new Tab("Add Book");
		addBookTab.setClosable(false); // Make the tab not closable

		// Create UI elements for the "Add Book" tab
		isbnField = new TextField();
		titleField = new TextField();
		categoryField = new TextField();
		supplierField = new TextField();
		purchaseDateDatePicker = new DatePicker();
		purchasePriceField = new TextField();
		originalPriceField = new TextField();
		sellingPriceField = new TextField();
		authorField = new TextField();
		stockField = new TextField();

		Button addButton = new Button("Add");

		// Set the layout of the UI elements
		GridPane gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(20, 20, 20, 20));

		gridPane.add(new Label("ISBN:"), 0, 0);
		gridPane.add(isbnField, 1, 0);
		gridPane.add(new Label("Title:"), 0, 1);
		gridPane.add(titleField, 1, 1);
		gridPane.add(new Label("Category:"), 0, 2);
		gridPane.add(categoryField, 1, 2);
		gridPane.add(new Label("Supplier:"), 0, 3);
		gridPane.add(supplierField, 1, 3);
		gridPane.add(new Label("Purchase Date:"), 0, 4);
		gridPane.add(purchaseDateDatePicker, 1, 4);
		gridPane.add(new Label("Purchase Price:"), 0, 5);
		gridPane.add(purchasePriceField, 1, 5);
		gridPane.add(new Label("Original Price:"), 0, 6);
		gridPane.add(originalPriceField, 1, 6);
		gridPane.add(new Label("Selling Price:"), 0, 7);
		gridPane.add(sellingPriceField, 1, 7);
		gridPane.add(new Label("Author:"), 0, 8);
		gridPane.add(authorField, 1, 8);
		gridPane.add(new Label("Stock:"), 0, 9);
		gridPane.add(stockField, 1, 9);
		//gridPane.add(addButton, 0, 10, 2, 1);
    gridPane.add(addButton, 0, 11, 2, 1);

		// Add an event handler for the add button
		addButton.setOnAction(e -> {
		try {
			// Create a Book object with the provided information
			Book newBook = createBookFromInput();

			// Get the amount in stock from the input field
			int amountInStock = Integer.parseInt(stockField.getText());

			// Attempt to add the book using actor.bookAdd(book, amountInStock)
			actor.bookAdd(newBook, amountInStock);

			// Show a success message
			showSuccessAlert("Success", "Book added successfully");
			refreshBooksTable();

			// Clear the input fields
			isbnField.clear();
			titleField.clear();
			categoryField.clear();
			supplierField.clear();
			purchaseDateDatePicker.setValue(null);
			purchasePriceField.clear();
			originalPriceField.clear();
			sellingPriceField.clear();
			authorField.clear();
			stockField.clear();
		} catch (Exception ex) {
			// Show an error message
			showSuccessAlert("Error", "Failed to add book: " + ex.getMessage());
			ex.printStackTrace();
		}
	});

		addBookTab.setContent(gridPane);

		return addBookTab;
	}

	private Book createBookFromInput() {
		String isbn = isbnField.getText();
		String title = titleField.getText();
		String category = categoryField.getText();
		String supplier = supplierField.getText();
		LocalDate purchaseDate = purchaseDateDatePicker.getValue();
		double purchasePrice = Double.parseDouble(purchasePriceField.getText());
		double originalPrice = Double.parseDouble(originalPriceField.getText());
		double sellingPrice = Double.parseDouble(sellingPriceField.getText());
		String author = authorField.getText();
		int stock = Integer.parseInt(stockField.getText());

		return new Book(isbn, title, category, supplier, purchaseDate, purchasePrice,
				originalPrice, sellingPrice, author, stock);
	}

	private Tab createUsersTab() {
		Tab usersTab = new Tab("Users");
		usersTab.setClosable(false); // Make the tab not closable

		// Create UI elements for the "Users" tab
		usersTable = new TableView<>();
		usersData = FXCollections.observableArrayList();

		TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
		usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

		TableColumn<User, String> passwordColumn = new TableColumn<>("Password");
		passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));

		TableColumn<User, String> roleColumn = new TableColumn<>("Role");
		roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));

		usersTable.getColumns().addAll(usernameColumn, passwordColumn, roleColumn);

		// Add a button for refreshing the user list
		Button refreshButton = new Button("Refresh");
		refreshButton.setOnAction(e -> refreshUsersTable());

		VBox vBox = new VBox(10, usersTable, refreshButton);
		vBox.setPadding(new Insets(20));

		usersTab.setContent(vBox);

		// Fetch and display the initial list of users
		refreshUsersTable();

		return usersTab;
	}

	private void refreshUsersTable() {
		// Fetch the list of users using users.getUsers()
		List<User> userList = users.getUsers();

		// Clear and update the data in the table
		usersData.clear();
		usersData.addAll(userList);
		usersTable.setItems(usersData);
	}

	private Tab createAddUserTab() {
		Tab addUserTab = new Tab("Add User");
		addUserTab.setClosable(false); // Make the tab not closable

		// Create UI elements for the "Add User" tab
		usernameField = new TextField();
		passwordField = new PasswordField();
		roleField = new TextField();

		Button addButton = new Button("Add");

		// Set the layout of the UI elements
		GridPane gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(20, 20, 20, 20));

		gridPane.add(new Label("Username:"), 0, 0);
		gridPane.add(usernameField, 1, 0);
		gridPane.add(new Label("Password:"), 0, 1);
		gridPane.add(passwordField, 1, 1);
		gridPane.add(new Label("Role:"), 0, 2);
		gridPane.add(roleField, 1, 2);
		gridPane.add(addButton, 0, 3, 2, 1);

		// Add an event handler for the add button
		addButton.setOnAction(e -> handleAddUserButton());

		addUserTab.setContent(gridPane);

		return addUserTab;
	}

	private void handleAddUserButton() {
		try {
			User newUser = createUserFromInput();

			users.add(newUser);
			showSuccessAlert("Success", "User added successfully");

			usernameField.clear();
			passwordField.clear();
			roleField.clear();
			refreshUsersTable();
		} catch (Exception e) {
			// Show an error message
			showErrorAlert("Error", "Failed to add user: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private User createUserFromInput() {
		String username = usernameField.getText();
		String password = passwordField.getText();
		String role = roleField.getText();

		return new User(username, password, role);
	}

	private Tab createPrivilegesTab() {
		Tab privilegesTab = new Tab("Privileges");
		privilegesTab.setClosable(false); // Make the tab not closable

		// Create UI elements for the "Privileges" tab
		librarianCheckoutCheckbox = new CheckBox("checkout");
		librarianManageStockCheckbox = new CheckBox("manageStock");
		librarianPerfReviewCheckbox = new CheckBox("perfReview");
		librarianManageUsersCheckbox = new CheckBox("manageUsers");

		managerCheckoutCheckbox = new CheckBox("checkout");
		managerManageStockCheckbox = new CheckBox("manageStock");
		managerPerfReviewCheckbox = new CheckBox("perfReview");
		managerManageUsersCheckbox = new CheckBox("manageUsers");

		applyPrivilegesButton = new Button("Apply");

		// Set the layout of the UI elements
		GridPane gridPane = new GridPane();
		gridPane.setVgap(10);
		gridPane.setHgap(10);
		gridPane.setPadding(new Insets(20, 20, 20, 20));

		gridPane.add(new Label("Librarian Privileges:"), 0, 0);
		gridPane.add(librarianCheckoutCheckbox, 0, 1);
		gridPane.add(librarianManageStockCheckbox, 0, 2);
		gridPane.add(librarianPerfReviewCheckbox, 0, 3);
		gridPane.add(librarianManageUsersCheckbox, 0, 4);

		gridPane.add(new Label("Manager Privileges:"), 1, 0);
		gridPane.add(managerCheckoutCheckbox, 1, 1);
		gridPane.add(managerManageStockCheckbox, 1, 2);
		gridPane.add(managerPerfReviewCheckbox, 1, 3);
		gridPane.add(managerManageUsersCheckbox, 1, 4);

		gridPane.add(applyPrivilegesButton, 0, 5, 2, 1);

		// Add an event handler for the apply privileges button
		applyPrivilegesButton.setOnAction(e -> handleApplyPrivilegesButton());

		privilegesTab.setContent(gridPane);

		// Initialize the checkboxes based on current privileges
		initializeCheckboxes();

		return privilegesTab;
	}

	private void initializeCheckboxes() {
		// Assuming actor.getPrivileges(role) returns the current privileges for a role
		Set<String> librarianPrivileges = privs.getPrivileges("librarian");
		Set<String> managerPrivileges = privs.getPrivileges("manager");

		librarianCheckoutCheckbox.setSelected(librarianPrivileges.contains("checkout"));
		librarianManageStockCheckbox.setSelected(librarianPrivileges.contains("manageStock"));
		librarianPerfReviewCheckbox.setSelected(librarianPrivileges.contains("perfReview"));
		librarianManageUsersCheckbox.setSelected(librarianPrivileges.contains("manageUsers"));

		managerCheckoutCheckbox.setSelected(managerPrivileges.contains("checkout"));
		managerManageStockCheckbox.setSelected(managerPrivileges.contains("manageStock"));
		managerPerfReviewCheckbox.setSelected(managerPrivileges.contains("perfReview"));
		managerManageUsersCheckbox.setSelected(managerPrivileges.contains("manageUsers"));
	}

	private void handleApplyPrivilegesButton() {
		// Get the selected privileges for each role
		Set<String> librarianPrivileges = getSelectedPrivileges(
				librarianCheckoutCheckbox,
				librarianManageStockCheckbox,
				librarianPerfReviewCheckbox,
				librarianManageUsersCheckbox
				);

		Set<String> managerPrivileges = getSelectedPrivileges(
				managerCheckoutCheckbox,
				managerManageStockCheckbox,
				managerPerfReviewCheckbox,
				managerManageUsersCheckbox
				);

		// Apply the selected privileges using privs.setPrivileges(role, Set<String>)
		privs.setPrivileges("librarian", librarianPrivileges);
		privs.setPrivileges("manager", managerPrivileges);

		// Show a success message
		showAlert("Success", "Privileges applied successfully");
	}

	private Set<String> getSelectedPrivileges(CheckBox... checkboxes) {
		Set<String> selectedPrivileges = new HashSet<>();
		for (CheckBox checkbox : checkboxes) {
			if (checkbox.isSelected()) {
				selectedPrivileges.add(checkbox.getText());
			}
		}
		return selectedPrivileges;
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	private void showErrorAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}

	private void showSuccessAlert(String title, String message) {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
