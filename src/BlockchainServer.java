import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockchainServer {

	private Blockchain blockchain;
	private ServerSocket ss;
	private boolean closeServer; 

	public BlockchainServer() { 
		blockchain = new Blockchain();
		closeServer = false;
	}


	// getters and setters
	public void setBlockchain(Blockchain blockchain) { this.blockchain = blockchain; }
	public Blockchain getBlockchain() { return blockchain; }
	public void setServerSocket(ServerSocket ss) { this.ss = ss; }
	public ServerSocket getServerSocket() { return ss; }
	public void setCloseServer(boolean closeServer) { this.closeServer = closeServer; }
	public boolean getCloseServer() { return closeServer; }

	public static void main(String[] args) {
		// Check for correct amount of arguments
		while(true) {
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
					// After termination, close the server
					bcs.getServerSocket().close();
					portSocket.close();

					if (bcs.getCloseServer()) {
						bcs.setCloseServer(false);
						break;
					}
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
	}

	public void serverHandler(InputStream clientInputStream, OutputStream clientOutputStream) {

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientInputStream));
		PrintWriter outWriter = new PrintWriter(clientOutputStream, true);
		String request = "";
		do {
			try {
				// Parse request from inputReader
				request =  inputReader.readLine();
				if (request == null || request.equals("")) {
					request = "null";
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			boolean ccCalled = false;
			String s = (request.contentEquals("null")) ? "null" : validateRequest(request);
			switch(s) {
			case "tx":
				handleTransactionRequest(request, outWriter);
				break;
			case "pb": 
				handlePrintBlockchain(outWriter);
				break;
			case "null":
			case "cc":
				ccCalled = true;
				break;		
			default:
				outWriter.print("Error\n\n");
				outWriter.flush();
			}
			if (ccCalled) {
				break;
			}
		} while (!request.equals("null"));
		setCloseServer(true);
	}

	public static String validateRequest(String request) {
		String result = "error";
		if (request == null || request.contentEquals("")) {
			return result;
		} else {
			if (request.contentEquals("pb")) {
				// Check for pb
				result = "pb";
			} else if (request.contentEquals("cc")) {
				// Check for cc
				result = "cc";
			} else {
				// If it looks like a tx request, validate format
				String[] parsedRequest = Blockchain.parseTransactionString(request);
				//TODO: This seems sketchy, if validation fails, look here
				if (parsedRequest[0] != null || request.charAt(0) == 't') {
					result = "tx";
				}
			}
			return result;
		}
	}

	public void handleTransactionRequest(String txString, PrintWriter outWriter) {
		int result = getBlockchain().addTransaction(txString);
		if (result > 0) {
			outWriter.print("Accepted\n\n");
		} else {
			outWriter.print("Rejected\n\n");
		}
		outWriter.flush();
	}

	public void handlePrintBlockchain(PrintWriter outWriter) {
		// Blockchain's toString is all we need
		outWriter.print(getBlockchain().toString() + "\n");
		outWriter.flush();
	}
}