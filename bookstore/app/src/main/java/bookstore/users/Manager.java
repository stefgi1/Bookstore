package	bookstore.users;

public class Manager {
	public static User create(String username, String password){
		return new User(username, password, "manager");
	}
}
