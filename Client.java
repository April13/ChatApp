import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * The Client class is used to log onto the client host, 
 * create a UDP connection to contact the server, and 
 * create the more reliable TCP connection once access 
 * has been granted by the server.
 *
 */
class Client {

	private ClientUDPConnection clientUDP; 	//UDP
	private ClientTCPConnection clientTCP; 	//TCP
	
	private String clientID; 			//The clientID is used to determine the identity of the users.	
	private String clientKey;			//The secret key used in authenticating the client.
	
	protected boolean authSucc = false;
	protected String randCookie; 		//Sent from server, used to establish a TCP connection to the server.
	protected int TCPServerPort; 		//Sent from server in auth_success message.
	

	
	/**
	 * Constructor
	 */
	public Client(String clientID) {
		this.clientID = clientID; 					//Set ClientID
		this.clientKey = KeyTable.getKey(clientID); //Set ClientKey
		
	}
	
	/**
	 * Constructor
	 */
	public Client() {
		// default constructor
	}
	
	
	
	/**
	 * Runs the client application.
	 */
	public void runClient() throws Exception 
	{	
		
		try
		{
			
			
			this.logon(); 	//Prompt user to log on.
			
			
			//Create a new UDP connection.
			clientUDP = new ClientUDPConnection(clientID, clientKey);
			
			//Send UDP connection request to server. 
			//Wait on server to send a challenge; timeout possible.
			clientUDP.hello(); 		//****Server sends a challenge in response.****	
			
	//=============
						
			//Client sends response() to authenticate itself.
			//Wait on server to return authSucc or authFail; timeout possible.
			clientUDP.response();
			
			
			
			//****Server sends an AUTH_FAIL or AUTH_SUCC.****	
			
			
			//****UDP socket has already been closed.****	
			
			
			
			if(authSucc=clientUDP.authSucc == true) 
			{
				
				randCookie = clientUDP.randCookie;
				TCPServerPort = clientUDP.TCPServerPort;
				
				
								
				//Establish a new TCP connection, and encrypt messages from this point on.
				clientTCP = new ClientTCPConnection(clientID, randCookie, TCPServerPort);
				
				
				//Begin chat initialization when a "CONNECTED" message is received. (Will be decrypted.)
				clientTCP.connect();
				
				
				
				
				//****Chat session has ended.****
				
				//****TCP connection has already been closed.**** 
				
			}
			
			
			
			//****An authFail message was received, or client has finished chat session.****
			
			
		
			
		}
		finally 
		{
			
			//*****Close TCP connection.******
			
			//Handled by garbage collection, no action required on the Client object's part.
			
		}
	    
	}
	

	
	
	/**
	 * The user must log on before the client can attempt to connect 
	 * to the server.
	 * 
	 */
	private void logon() throws IOException {
		
		String logon = "log on";	//Keyword entered to log onto the client application.
		String userInput;
		
		
		//Buffer for reading input from command line.
		BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
		
		
		//Wait for user to log on.
		while(true) 
		{
			System.out.print("To log onto the client, enter Log on : ");
			
			//Read user input from command line.
			userInput = consoleInput.readLine();
			
			//Logs onto the application if the user has correctly entered 'log on'.
			if(userInput.equalsIgnoreCase(logon)) {
				break;
			}
		}
		
	}//End of logon.
	

}
