import java.io.BufferedReader;
import java.io.InputStreamReader;



/**
 *
 * ClientHost is used to  
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
    
    
}