import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * The Client class is used to initiate the creation of a Client and
 * prompts the end user for a clientID in order to log on,
 * create a UDP connection to contact the server, and
 * create the more reliable TCP connection once access
 * has been granted by the server. 
 * 
 * The end user must know their password (the Client's secret key)
 * as well in order to be able to authenticate themselves later
 * when the server sends a challenge to the Client.
 *
 */
class Client {
    
    private ClientUDPConnection clientUDP; 	//UDP
    private ClientTCPConnection clientTCP; 	//TCP
    int UDPServerPort = 8756;
    
    private String clientID; 			//The clientID is used to determine the identity of the users.
    private String clientKey;			//The secret key used in authenticating the client.
    
    protected boolean authSucc = false;
    protected String randCookie; 		//Sent from server, used to establish a TCP connection to the server.
    protected int TCPServerPort; 		//Sent from server in auth_success message.
    
    
    
    /**
     * Constructor
     */
    public Client(String clientID) {
        this.clientID = clientID; 						//Set ClientID
        this.clientKey = KeyTable.getKey(clientID); 	//Set ClientKey
    }
    
    /**
     * Constructor
     */
    public Client() {
        // default constructor
    }
    
    
    /**
     * The end user is required to have a valid clientID to begin.
     */
    public static void main(String args[]) throws Exception {
        
        
        String userInput = null;
        
        
        //Buffer for reading input from command line.
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        
        
        while(true) {

        	System.out.println();
            System.out.print("Please enter a valid ClientID to log on: ");
            
            //Read user input from command line.
            userInput = consoleInput.readLine();
            
            
            //Break out of while loop if it's a valid clientID.
            if(KeyTable.testClientID(userInput) == true) {
                break;
            }
            
        }
        
        
        Client client1 = new Client(userInput); //ClientID confirmed, creates a new Client object.
        client1.runClient();
        
    }//End of main.
    
    
    /**
     * Runs the client application.
     */
    public void runClient() throws Exception
    {
        
        try
        {
          
            //Create a new UDP connection.
            clientUDP = new ClientUDPConnection(clientID, clientKey, UDPServerPort);
            
            //Send UDP connection request to server.
            //Wait on server to send a challenge; timeout possible.
            clientUDP.hello(); 		
            
            //****Server sends a challenge in response.****

            
            //Client sends response() to authenticate itself.
            //Wait on server to return authSucc or authFail; timeout possible.
            clientUDP.response();
                        
            
            //****Server sends an AUTH_FAIL or AUTH_SUCC.****
            
            if(authSucc=clientUDP.authSucc == true)
            {
                
                randCookie = clientUDP.randCookie;
                
                TCPServerPort = clientUDP.TCPServerPort;
                
                //Establish a new TCP connection, and encrypt messages from this point on.
                clientTCP = new ClientTCPConnection(clientID, randCookie, TCPServerPort);
                
                
                //Begin chat initialization when a "CONNECTED" message is received. (Will be decrypted.)
                clientTCP.connect();	

                
                //****Chat session has ended.****
                
                //****The server's TCP socket to this particular client should have already been closed.****
                
            }
            
            
            
            //****An authFail message was received, or client has finished chat session.****
            
           
        }
        finally
        {
            
            //*****Close welcoming TCP connection.******
            
            //Handled by garbage collection, no action required on the Client object's part.
            
        }
        
    }//End of runClient().

    
}//End of Client.java