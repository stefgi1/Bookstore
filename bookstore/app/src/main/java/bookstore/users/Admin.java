package bookstore.users;

public class Admin {
	public static User create(String username, String password){
		return new User(username, password, "admin");
	}
}
