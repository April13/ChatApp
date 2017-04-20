import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;


/*
 * Still missing:
 * 
 * Encryption/Decryption methods. 
 * 
 * 
 */

class ClientTCPConnection {
	
	private String clientID; //Passed by Client.
	private String randCookie;
	private int TCPServerPort;
	private String randNumber;
	
	//Used to establish a TCP connection to the server.
	private Socket clientTCPSocket;
	private InetAddress serverIPAddress;
	BufferedReader bufferedReader = null;
	PrintWriter printWriter = null;
		
	
	/**
	 * Constructor for ClientTCPConnection.
	 * Parameters are provided by the Client instance.
	 */
	protected ClientTCPConnection(String clientID, String randNumber, String randCookie, int TCPServerPort) {
		this.clientID = clientID;
		this.randNumber = randNumber;
		this.randCookie = randCookie;
		this.TCPServerPort = TCPServerPort;
	}
	
	
	/**
	 * After receiving authSuccess, client must establish a TCP 
	 * connection at TCPServerPort with the server, and all 
	 * forward communication is encrypted and exchanged over the 
	 * TCP connection until it is closed. 
	 * @throws IOException 
	 * 
	 */
	protected void connect() throws IOException 
	{
		String userInput;
		String toServer = "";
		String fromServer = "";
		String logoff = "log off"; //For chat sessions.
		
		
			
		//Retrieve destination server IP address.
	    serverIPAddress = InetAddress.getByName("localhost");
		
		//Create a new TCP client socket.
		clientTCPSocket = new Socket(serverIPAddress, TCPServerPort);
		
		
		
		//****Encrypt message.****
		crypt.encrypt(randCookie, randNumber);
		
		
		
		//Send randCookie to server.
		printWriter = new PrintWriter(clientTCPSocket.getOutputStream());
		printWriter.println(randCookie);
		printWriter.flush();
		System.out.println("TCP - randCookie: " + randCookie);
		
		
		//Receive message from server.
		bufferedReader = new BufferedReader(new InputStreamReader(clientTCPSocket.getInputStream()));
		fromServer = (bufferedReader.readLine()).trim();
		
		
		//****Decrypt message.****
		crypt.decrypt(fromServer, randNumber);
		
		
		
		//Receive CONNECTED message from server.
		if(fromServer.equalsIgnoreCase("CONNECTED")) {
			
			System.out.println("Your are now connected to the server and can initiate a chat session.");
		}
		
		//Not the end of connect().
		
		
		/* =========================================================
		 * ================== CHAT IMPLEMENTATION ==================
		 * =========================================================
		 * 
		 * This part of connect() will be removed.
		 * 
		 * 
		 */
		
		
		
		//Client is now connected to the chat server, and can try to initialize a chat.
		//Is "Activity Timer" the same thing as a regular timeout?
		
		//Prepare buffer for reading input from command line.
		BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
		
		while(true) {
			
			System.out.print("Please enter a command: ");
			
			
			//Read user input from command line.
			userInput = consoleInput.readLine();
			
			
			//If end user enters "Log off" correctly, client closes TCP connection.
			if(userInput.equalsIgnoreCase(logoff)) {
				break;
			}
			
			//If end user enters "Chat Client-ID-B", attempt chat initiation.
				//If Client B is available, chatting begins.
				//If Client B is unreachable, prompt end user for a command. (loop while)
			
		}
		
		
		
		//Close TCP connection.
		
		clientTCPSocket.close();
			
		
	} //End of connect().
	
}
