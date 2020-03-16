/**
 * Blueprint for a transaction. 
 * @author Fer
 *
 */
import java.util.regex.*;

public class Transaction {

	private String sender;
	private String content;

	// getters and setters
	public void setSender(String sender) { this.sender = sender; }
	public void setContent(String content) { this.content = content; }
	public String getSender() { return sender; }
	public String getContent() { return content; }

	/**
	 * Constructor for Transaction objects
	 * @param sender
	 * @param content
	 */
	public Transaction(String sender, String content) {
		this.sender = sender;
		this.content = content;
	}
	
	/**
	 * Returns a String containing the values of the transaction.
	 */
	public String toString() {
		return String.format("|%s|%70s|\n", sender, content);
	}
	
	/**
	 * Like toString, but with a hashable format.
	 * @return
	 */
	public String getHashableString() {
		return "tx|"+sender+"|"+content;
	}
	
	/**
	 * Performs a validity text on the fields of the transaction.
	 * @return
	 */
	public boolean isValid() {
		boolean validSender = sender.matches("[a-z]{4}[0-9]{4}");
		boolean validContent = !content.contains("|") && content.matches("([a-zA-Z0-9])*") && content.length() <= 70;
		
		return validSender && validContent;
	}
}
