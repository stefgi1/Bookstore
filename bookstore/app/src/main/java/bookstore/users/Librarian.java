package bookstore.users;

public class Librarian {
	public static User create(String username, String password){
		return new User(username, password, "librarian");
	}
}
