import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BlockchainClient {

	public boolean closeClient = false;

	public static void main(String[] args) {

		if (args.length != 2) {
			return;
		}
		String serverName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		BlockchainClient bcc = new BlockchainClient();

		try {
			// Connect to the server's port
			Socket portSocket = new Socket(serverName, portNumber);
			InputStream serverInputStream = portSocket.getInputStream();
			OutputStream serverOutputStream = portSocket.getOutputStream();
			// Handle client's activity
			bcc.clientHandler(serverInputStream, serverOutputStream);
			
			// Once finished, close port
			portSocket.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void clientHandler(InputStream serverInputStream, OutputStream serverOutputStream) {
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(serverInputStream));
		PrintWriter outWriter = new PrintWriter(serverOutputStream, true);
		Scanner sc = new Scanner(System.in);
		while (sc.hasNextLine()) {
			try {
				// Read from user
				String readLine = sc.nextLine();
				// Send the server the user's input
				outWriter.print(readLine + "\n");
				outWriter.flush();
				
				// Receive and print the server's output
				String serverOutput = "";
				String tmp = "";
				while ((tmp = inputReader.readLine()) != null && inputReader.ready()) {
				    if (tmp.isEmpty()) {
				        break;
				    }
				    serverOutput += tmp + "\n";
				    tmp += tmp.length();
				}
				
				System.out.print(serverOutput);
				
				// If cc is read, exit the loop
				if (readLine.contentEquals("cc")) {
					sc.close();
					break;
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			

		}
	}
}