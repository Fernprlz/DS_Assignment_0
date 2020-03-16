/**
 * List of transactions.
 * @author Fer
 *
 */
import java.util.ArrayList;
import java.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Block {
	private Block previousBlock;
	private byte[] previousHash;
	private ArrayList<Transaction> transactions;

	public Block() { transactions = new ArrayList<>(); }

	public Block(ArrayList<Transaction> transactions) {
		this.transactions = transactions;
	}
	
	public Block(Block previousBlock, byte[] previousHash, ArrayList<Transaction> transactions) {
		this.previousBlock = previousBlock;
		this.previousHash = previousHash;
		this.transactions = transactions;
	}
	// getters and setters
	public Block getPreviousBlock() { return previousBlock; }
	public byte[] getPreviousHash() { return previousHash; }
	public ArrayList<Transaction> getTransactions() { return transactions; }
	public void setPreviousBlock(Block previousBlock) { this.previousBlock = previousBlock; }
	public void setPreviousHash(byte[] previousHash) { this.previousHash = previousHash; }
	public void setTransactions(ArrayList<Transaction> transactions) {
		this.transactions = transactions;
	}

	public String toString() {
		String cutOffRule = new String(new char[81]).replace("\0", "-") + "\n";
		String prevHashString = String.format("|PreviousHash:|%65s|\n",
				Base64.getEncoder().encodeToString(previousHash));
		String hashString = String.format("|CurrentHash:|%66s|\n",
				Base64.getEncoder().encodeToString(calculateHash()));
		String transactionsString = "";
		for (Transaction tx : transactions) {
			transactionsString += tx.toString();
		}
		return "Block:\n"
		+ cutOffRule
		+ hashString
		+ cutOffRule
		+ transactionsString
		+ cutOffRule
		+ prevHashString
		+ cutOffRule;
	}

	// to calculate the hash of current block.
	public byte[] calculateHash() {
		byte[] result = new byte[32];
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);

			// 1. hash the previous block hash [previousHash] -> dos.write()
			dos.write(previousHash);
			// 2. hash each transaction in the list -> dos.writeUTF()
			for (int ii = 0; ii < transactions.size(); ii++) {
				// Transactions hashed as Strings with format:
				// "tx|sender|content"			
				dos.writeUTF(transactions.get(ii).getHashableString());
			}
			
			byte[] bytes = baos.toByteArray();
			result = digest.digest(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		return result;
	}

}
