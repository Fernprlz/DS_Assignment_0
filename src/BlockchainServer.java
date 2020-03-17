import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class BlockchainServer {

	private Blockchain blockchain;
	public ServerSocket ss;
	
	public BlockchainServer() { blockchain = new Blockchain(); }
	

	// getters and setters
	public void setBlockchain(Blockchain blockchain) { this.blockchain = blockchain; }
	public Blockchain getBlockchain() { return blockchain; }
	public void setServerSocket(ServerSocket ss) { this.ss = ss; }
	public ServerSocket getServerSocket() { return ss; }
	
	
	
	
	public static void main(String[] args) {
		// Check for correct amount of arguments
		if (args.length != 1) {
			return;
		}
		int portNumber = Integer.parseInt(args[0]);
		BlockchainServer bcs = new BlockchainServer();

		while(true) {
			try {
				// Create a server socket with the given port number
				bcs.setServerSocket(new ServerSocket(portNumber));
				// Wait for a connection to be made and set a socket to listen to request
				Socket portSocket = bcs.getServerSocket().accept();
				// Define streams of the communication
				InputStream clientInputStream =  portSocket.getInputStream();
				OutputStream clientOutputStream = portSocket.getOutputStream();
				// Handle requests
				bcs.serverHandler(clientInputStream, clientOutputStream);
				clientInputStream.close();
				clientOutputStream.close();
			}catch(IOException e){
				e.printStackTrace();
			}		
		}
	}


	public void serverHandler(InputStream clientInputStream, OutputStream clientOutputStream) {

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientInputStream));
		PrintWriter outWriter = new PrintWriter(clientOutputStream, true);
		String request = "";
		
		try {
		// Parse request from inputReader
		request = (inputReader.readLine() != null) ? inputReader.readLine() : "error";
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		switch(validateRequest(request)) {
			case "tx":
				handleTransactionRequest(request, outWriter);
				break;
			case "pb": 
				handlePrintBlockchain(outWriter);
				break;
			case "cc":
				handleCloseConnection();
			default:
				outWriter.print("Error\n\n");
		}
	}

	public static String validateRequest(String request) {
		String result = "error";
		if (request == null) {
			return result;
		}
		
		if (request.equals("pb")) {
			// Check for pb
			result = "pb";
		} else if (request.equals("cc")) {
			// Check for cc
			result = "cc";
		} else if (request.substring(0, 1).equals("tx")){
			// If it looks like a tx request, validate format
			String[] parsedRequest = Blockchain.parseTransactionString(request);
			result = (Blockchain.validateTransaction(parsedRequest)) ? "tx" : "error"; 
		}

		return result;
	}

	public void handleTransactionRequest(String txString, PrintWriter outWriter) {
		int result = getBlockchain().addTransaction(txString);
		if (result > 0) {
			outWriter.print("Accepted\n\n");
		} else {
			outWriter.print("Rejected\n\n");
		}
	}

	public void handlePrintBlockchain(PrintWriter outWriter) {

		// Printing complying with the given format
		System.out.print("Pool:\n");
		String cutOffRule = new String(new char[81]).replace("\0", "-") + "\n";
		String transactionsString = "";
		for (int jj = 0; jj < getBlockchain().getPool().size(); jj++) {
			transactionsString += getBlockchain().getPool().get(jj).toString();
		}
		outWriter.print(cutOffRule + transactionsString + cutOffRule + "\n");

		if (getBlockchain().getHead() != null) {
			outWriter.print(getBlockchain().getHead().toString() + "\n");
		}
	}

	public void handleCloseConnection() {
		try{
			ss.close();
		} catch (SocketException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}