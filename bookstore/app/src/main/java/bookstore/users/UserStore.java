package bookstore.users;

import java.io.*;
import java.util.*;

public class UserStore{
	private List<User> users;

	public UserStore(){
		users = new ArrayList<>();
	}

	public int getIndex(String username){
		for (int i = 0; i < users.size(); i ++){
			if (users.get(i).getUsername().equals(username))
				return i;
		}
		return -1;
	}

	public User getUser(String username){
		int index = getIndex(username);
		if (index < 0)
			return null;
		return users.get(index);
	}

	public boolean login(String username, String  password){
		User user = getUser(username);
		if (user == null)
			return false;
		return user.getPassword().equals(password);
	}

	public List<User> getUsers(){
		return users;
	}

	public void add(User user) throws Exception{
		// username be unique!
		for(User u : users){
			if (u.getUsername().equals(user.getUsername()))
				throw new Exception("Username `" + u.getUsername() +
						"` is already used");
		}
		users.add(user);
	}

	public void save(String filename) {
		try (PrintStream stream = new PrintStream(new File(filename))){
			for (User user : users)
				user.toStream(stream);
		} catch (IOException e) {
			System.err.println("Error saving privileges to file: " + e.getMessage());
		}
	}

	public void load(String filename) {
		users = new ArrayList<>();
		try (Scanner scanner = new Scanner(new File(filename))) {
			while (scanner.hasNextLine()){
				users.add(new User(scanner));
			}
		} catch (IOException e) {
			System.err.println("Error loading privileges from file: " +
					e.getMessage());
		}
	}
}
