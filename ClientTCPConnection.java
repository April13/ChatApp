import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


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
    
    //Used to establish a TCP connection to the server.
    private Socket clientTCPSocket;
    private InetAddress serverIPAddress;
    BufferedReader bufferedReader = null;
    PrintWriter printWriter = null;
    //
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    
    
    /**
     * Constructor for ClientTCPConnection.
     * Parameters are provided by the Client instance.
     */
    protected ClientTCPConnection(String clientID, String randCookie, int TCPServerPort) {
        this.clientID = clientID;
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
        
        
        //Send randCookie to server.
        printWriter = new PrintWriter(clientTCPSocket.getOutputStream());
        printWriter.println(randCookie);
        printWriter.flush();
        System.out.println("TCP - randCookie: " + randCookie);
        
        //
        // send connect req message to server w/ clientID
        printWriter.println("CONNECT_REQUEST,"+clientID);
        printWriter.flush();
        
        
        //Receive message from server.
        bufferedReader = new BufferedReader(new InputStreamReader(clientTCPSocket.getInputStream()));
        fromServer = bufferedReader.readLine();
        
        
        //****Decrypt message.****
        //fromServer = [decrypted message];
        
        //Receive CONNECTED message from server.
        if(fromServer.equalsIgnoreCase("CONNECTED"))
        {
            
            System.out.println("Your are now connected to the server and can initiate a chat session.");
            connected();
        }
        
        
    } //End of connect().
    
    //
    //
    protected void connected()
    {
        String userInput="";
        String fromServer = "";
        String logoff = "log off"; //For chat sessions.
        
        //Client is now connected to the chat server, and can try to initialize a chat.
        //Is "Activity Timer" the same thing as a regular timeout?
        
        try
        {
            sOutput = new ObjectOutputStream(clientTCPSocket.getOutputStream());
            sInput  = new ObjectInputStream(clientTCPSocket.getInputStream());
        }
        catch (IOException eIO) {
            System.out.println("Exception creating new Input/output Streams: " + eIO);
        }
        sendMessage(5,"");
        
        while(true)
        {
            
            //Prepare buffer for reading input from command line.
            BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));
            
            
            //Receive message from server.
            try
            {
                ChatMessage cm;
                cm = (ChatMessage) sInput.readObject();
                
                int type = cm.getType();
                String msg = cm.getMessage();
                
                switch (type)
                {
                    case 0: // chat request
                        break;
                    case 1: // chat msg
                        break;
                    case 2: // log off
                        clientTCPSocket.close();
                        break;
                    case 4: // show online users
                        System.out.println(msg);
                        break;
                    case 5: // connected
                        System.out.println(msg);
                        break;
                    default:
                        break;
                }
            }
            catch (IOException e) {
                System.out.println("Exception reading Streams: " + e);
            }
            catch(ClassNotFoundException e2) {
            }
            
            
            System.out.print("Please enter a command: ");
            
            try
            {
                //Read user input from command line.
                userInput = consoleInput.readLine();
                
                
                //If end user enters "Log off" correctly, client closes TCP connection.
                if(userInput.equalsIgnoreCase(logoff))
                {
                    sendMessage(2, "");
                    clientTCPSocket.close();
                    break;
                }
                
                //If end user enters "Chat Client-ID-B", attempt chat initiation.
                //If Client B is available, chatting begins.
                //If Client B is unreachable, prompt end user for a command. (loop while)
                // CHAT REQUEST
                else if(userInput.startsWith("chat ") || userInput.startsWith("Chat "))
                {
                    
                    String temp[] = userInput.split(" ");
                    String userB = temp[1];
                    
                    Chat c = new Chat(userB);
                    
                    
                }
                else if(userInput.equalsIgnoreCase("show online"))
                {
                    sendMessage(4,"");
                }
            }catch(IOException e){}
            
        }
        
    } // end of connected()
    
    class Chat extends Thread
    {
        ChatMessage cm;
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        
        String userB;
        
        public Chat(String userB)
        {
            this.userB = userB;
            
            try
            {
                sOutput = new ObjectOutputStream(clientTCPSocket.getOutputStream());
                sInput  = new ObjectInputStream(clientTCPSocket.getInputStream());
            }
            catch(IOException e){}
        }
        
        public void run()
        {
            
            // cm = (ChatMessage) sInput.readObject();
            
            // Chat Request
            sendMessage(0, userB);
            
            // to loop until LOGOUT
            boolean keepGoing = true;
            while(keepGoing)
            {
                try
                {
                    cm = (ChatMessage) sInput.readObject();
                }
                catch (IOException e) {
                    System.out.println(" Exception reading Streams: " + e);
                    break;
                }
                catch(ClassNotFoundException e2) {
                    break;
                }
                // the messaage part of the ChatMessage
                String message = cm.getMessage();
                int type = cm.getType();
                // chat req reply from server
                if(type==0 && message == "CHAT_STARTED")
                {
                    
                }
                else if(type==0 && message == "UNREACHABLE")
                {
                    keepGoing=false;
                }
            }
            
            close();
        }
        
        // try to close everything
        private void close() {
            // try to close the connection
            try {
                if(sOutput != null) sOutput.close();
            }
            catch(Exception e) {}
            try {
                if(sInput != null) sInput.close();
            }
            catch(Exception e) {};
        }
        
    }// END of Chat
    

    void sendMessage(int type, String msg)
    {
        ChatMessage newM = new ChatMessage(type, msg);
        try {
            ObjectOutputStream sOutput;
            sOutput = new ObjectOutputStream(clientTCPSocket.getOutputStream());
            sOutput.writeObject(newM);
        }
        catch(IOException e) {
            System.out.println("Exception writing to server: " + e);
        }
    }
    
}
