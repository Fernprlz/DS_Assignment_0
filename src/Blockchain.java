/**
 * 
 * @author Fer
 *
 */
import java.util.ArrayList;
import java.util.Base64;

public class Blockchain {

	private Block head;
	private ArrayList<Transaction> pool;
	private int length;

	private final int poolLimit = 3;

	public Blockchain() {
		pool = new ArrayList<>();
		length = 0;
	}

	// getters and setters
	public Block getHead() { return head; }
	public ArrayList<Transaction> getPool() { return pool; }
	public int getLength() { return length; }
	public void setHead(Block head) { this.head = head; }
	public void setPool(ArrayList<Transaction> pool) { this.pool = pool; }
	public void setLength(int length) { this.length = length; }

	// add a transaction
	public int addTransaction(String txString) {

		int result = 0;
		// Parse the transaction details
		String[] transactionFields = parseTransactionString(txString);

		// Check validity of the proposed transaction

		if(transactionFields[0] != null && validateTransaction(transactionFields) != false) {
			// We create the transaction	
			Transaction newTransaction = new Transaction(transactionFields[0], transactionFields[1]);
 
			if(pool.size() + 1 == poolLimit) {
				result = 2;
				
				pool.add(newTransaction);

				// Create a block with the uncommited transactions
				ArrayList<Transaction> transactionsToCommit = new ArrayList<Transaction>();
				for (int ii = 0; ii < pool.size(); ii++) {
					transactionsToCommit.add(pool.get(ii));
				}
				Block newBlock;
				if (head == null) {
					newBlock = new Block(head, new byte[32], transactionsToCommit);
				} else {
					newBlock = new Block(head, head.calculateHash(), transactionsToCommit);
				}
				// Add it to the blockchain
				head = newBlock;
				// Increase blockchain length
				length++;
				// Empty the pool and add the new transaction
				ArrayList<Transaction> newPool = new ArrayList<Transaction>();
				pool = newPool;

			}  else {
				result = 1;
				// Add the transaction to the pool
				pool.add(newTransaction);
			}
		}

		return result;
	}

	public String[] parseTransactionString(String txString) {
		String[] tmp = txString.split("\\|");
		String[] result = new String[2];

		if (tmp.length == 3){
			if (tmp[0].equals("tx")) {
				result[0] = tmp[1]; 
				result[1] = tmp[2];	
			} 
		}
		return result;
	}

	public static boolean validateTransaction(String[] txString) {
		boolean validSender = txString[0].matches("[a-z]{4}[0-9]{4}");
		boolean validContent = !txString[1].contains("|") && txString[1].matches("([a-zA-Z0-9 ])*") && txString[1].length() <= 70;
		return validSender && validContent;
	}

	public String toString() {
		String cutOffRule = new String(new char[81]).replace("\0", "-") + "\n";
		String poolString = "";
		for (Transaction tx : pool) {
			poolString += tx.toString();
		}

		String blockString = "";
		Block bl = head;
		while (bl != null) {
			blockString += bl.toString();
			bl = bl.getPreviousBlock();
		}

		return "Pool:\n"
		+ cutOffRule
		+ poolString
		+ cutOffRule
		+ blockString;
	}
}

