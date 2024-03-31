package bookstore.users;

public class PrivException extends Exception{
	public PrivException(String priv){
		super("You do not have privilege " + priv);
	}
}
