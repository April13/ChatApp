import java.io.BufferedReader;
import java.io.InputStreamReader;



/**
 *
 * ClientHost is used to initiate the creation of a Client and
 * prompts the end user for a clientID in order to log on. The
 * end user must know their password (the Client's secret key)
 * as well in order to be able to authenticate themselves later
 * when the server sends a challenge to the Client.
 *
 */


public class ClientHost {
    
    
    /**
     * The end user is required to have a valid clientID to begin.
     */
    
    public static void main(String args[]) throws Exception {
        
        
        String userInput = null;
        
        
        //Buffer for reading input from command line.
        BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
        
        
        while(true) {
            
            System.out.print("Please enter a valid ClientID: ");
            
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
    
    
}
