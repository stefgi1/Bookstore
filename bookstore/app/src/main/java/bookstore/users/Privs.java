package bookstore.users;

import java.util.*;
import java.io.*;

/**
 * Valid privileges: checkout, manageStock, perfReview, manageUsers
 */
public class Privs{
	private Map<String, Set<String>> privileges;

	public Privs(){
		privileges = new HashMap<>();
		toDefault();
	}

	public void toDefault(){
		Set<String> set = new HashSet<>();
		set.add("checkout");
		privileges.put("librarian", new HashSet<>(set));

		set.add("manageStock");
		set.add("perfReview");
		privileges.put("manager", new HashSet<>(set));

		set.add("manageUsers");
		privileges.put("admin", new HashSet<>(set));
	}

	public Set<String> getPrivileges(String role){
		if (!privileges.containsKey(role))
			return null;
		return privileges.get(role);
	}

	public Set<String> getPrivileges(User user){
		return getPrivileges(user.getRole());
	}

	public void setPrivileges(String role, Set<String> privs){
		privileges.put(role, privs);
	}

	public void setPrivileges(User user, Set<String> privs){
		setPrivileges(user.getRole(), privs);
	}

	public boolean hasPrivilege(String role, String priv){
		Set<String> privs = getPrivileges(role);
		if (privs == null)
			return false;
		return privs.contains(priv);
	}

	public boolean hasPrivilege(User user, String priv){
		return hasPrivilege(user.getRole(), priv);
	}

	public void assertPriv(User user, String priv) throws PrivException{
		if (user == null || !hasPrivilege(user, priv))
			throw new PrivException("You do not have the privilege " + priv);
	}

	public void save(String filename) {
		try (PrintStream stream = new PrintStream(new File(filename))){
			for (String key : privileges.keySet()){
				stream.println(key);
				stream.println(privileges.get(key).size());
				for (String priv : privileges.get(key))
					stream.println(priv);
			}
		} catch (IOException e) {
			System.err.println("Error saving privileges to file: " + e.getMessage());
		}
	}

	public void load(String filename) {
		privileges = new HashMap<>();
		try (Scanner scanner = new Scanner(new File(filename))) {
			while (scanner.hasNextLine()){
				String key = scanner.nextLine();
				int count = Integer.parseInt(scanner.nextLine());
				Set<String> perms = new HashSet<>();
				for (int i = 0; i < count; i ++){
					String perm = scanner.nextLine();
					perms.add(perm);
					System.out.println("priv[" + key + "]+=" + perm);
				}
				privileges.put(key, perms);
			}
		} catch (IOException e) {
			System.err.println("Error loading privileges from file: " +
					e.getMessage());
		}
	}
}
