import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

public class BlockchainServer {

	private Blockchain blockchain;
	public BlockchainServer() { blockchain = new Blockchain(); }

	// getters and setters
	public void setBlockchain(Blockchain blockchain) { this.blockchain = blockchain; }
	public Blockchain getBlockchain() { return blockchain; }

	public static void main(String[] args) {
		if (args.length != 1) {
			return;
		}
		int portNumber = Integer.parseInt(args[0]);
		BlockchainServer bcs = new BlockchainServer();

		// Keep listening to the port specified by the argument
		// Once it connects, server accepts connection
		// Sends InputStream and OutputStream of the accepted socket to the server handler
		boolean kill = false;
		
		while(!kill) {
		
			//serverHandler();
		}

	}


	public void serverHandler(InputStream clientInputStream, OutputStream clientOutputStream) {

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientInputStream));
		PrintWriter outWriter = new PrintWriter(clientOutputStream, true);

		// Check request
		String request = "";
		String txString = "";
		
		switch(request) {
		case "tx":
			handleTransactionRequest(txString);
			break;
		case "pb": 
			handlePrintBlockchain();
			break;
		case "cc":
			handleCloseConnection();
		}
	}
	
	public void handleTransactionRequest(String txString) {
		int result = blockchain.addTransaction(txString);
		if (result > 0) {
			System.out.print("Accepted\n\n");
		} else {
			System.out.print("Rejected\n\n");
		}
	}
	
	public void handlePrintBlockchain() {
		
		// Printing complying with the given format
		System.out.print("Pool:\n");
		String cutOffRule = new String(new char[81]).replace("\0", "-") + "\n";
		String transactionsString = "";
		for (int jj = 0; jj < blockchain.getPool().size(); jj++) {
			transactionsString += blockchain.getPool().get(jj).toString();
		}
		System.out.println(cutOffRule + transactionsString + cutOffRule);

		if (blockchain.getHead() != null) {
			System.out.print(blockchain.getHead().toString()+"\n");
		}
	}
	
	public void handleCloseConnection() {
		// just close connection
	}

	// implement helper functions here if you need any.
}