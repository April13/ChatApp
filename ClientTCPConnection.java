import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;


/**
 * After a UDP connection has been established, it will be 
 * closed, and a TCP connection will be created between the 
 * client and the server after a series of authentication 
 * mechanisms are performed to ensure the identity of the client 
 * logging into the server.
 */

class ClientTCPConnection {
    
    private String clientID; //Passed by Client.
    private String randCookie;
    private int TCPServerPort;
    
    //Used to establish a TCP connection to the server.
    private Socket clientTCPSocket;
    private InetAddress serverIPAddress;
    BufferedReader inFromServer = null;
    PrintWriter printWriter = null;
    //
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    private static int tcpPort = 7689;
    private boolean inChat;
    private String encryptionKey;
    boolean isChatting;
    boolean isTimeout;
    boolean logOut;
    
    /**
     * Constructor for ClientTCPConnection.
     * Parameters are provided by the Client instance.
     */
    protected ClientTCPConnection(String clientID, String randCookie, int TCPServerPort, String encryptionKey) {
        this.clientID = clientID;
        this.randCookie = randCookie;
        this.TCPServerPort = TCPServerPort;
        this.encryptionKey = encryptionKey;
    }
    
    
    /**
     * After receiving authSuccess, client must establish a TCP
     * connection at TCPServerPort with the server, and all
     * forward communication is encrypted and exchanged over the
     * TCP connection until it is closed.
     * @throws IOException
     */
    protected void connect() throws IOException
    {
        String userInput;
        String toServer = "";
        String fromServer = "";
        String logoff = "log off"; //For chat sessions.
        
        
        
        //Retrieve destination server IP address.
        serverIPAddress = InetAddress.getByName("localhost");
        
        //Create a new TCP client socket connection. Server is already waiting for a connection request.
        clientTCPSocket = new Socket(serverIPAddress, tcpPort);
        
       
        //Send "CONNECT_REQUEST,clientID" to server.
        printWriter = new PrintWriter(clientTCPSocket.getOutputStream());
        printWriter.println("CONNECT_REQUEST," + clientID);
        printWriter.flush();
        
        //****Wait for "connected" message from server.****
        
        
        
        //Receive message from server.
        inFromServer = new BufferedReader(new InputStreamReader(clientTCPSocket.getInputStream()));
        String fromS = inFromServer.readLine();
        
        System.out.println("fromServer: " + fromS);
        
            
        //Receive CONNECTED message from server.
        if(fromS.equalsIgnoreCase("CONNECTED"))
        {
            //System.out.println("Your are now connected to the server and can initiate a chat session.");
            connected();
        }
        else
        {
            System.out.println("NOT CONNECT");
            
        }
        
        
    } //End of connect().
    
    
    private String chatMsg, recipientUser;
    private int chatSession;
    

    /**
     * Once the TCP connection has been established, the client 
     * will be able to send chats to other clients that are also 
     * online and not currently engaged in other chat sessions. 
     * The client can also check to see the chat history between it 
     * and other users that it has already chatted with.
     */
    protected void connected()
    {
        String userInput="";
        String fromServer = "";
        String logoff = "log off"; //For chat sessions.
        inChat = false;
        isChatting = false;
        isTimeout = false;
        
        //Client is now connected to the chat server, and can try to initialize a chat.
        //Is "Activity Timer" the same thing as a regular timeout?
        System.out.println("Trying to connect to server...");
        
        try
        {
            clientTCPSocket = new Socket(serverIPAddress, TCPServerPort);
            sOutput = new ObjectOutputStream(clientTCPSocket.getOutputStream());
            sInput  = new ObjectInputStream(clientTCPSocket.getInputStream());
        }
        catch (IOException eIO) {
            System.out.println("Exception creating new Input/output Streams: " + eIO);
        }
        
        
        new ChatListener().start();
        
        // sendMessage(5,"");
        
        logOut = false;
        
        Scanner scan = new Scanner(System.in);
        
        // sets activity timeout to 10 min
        int time = 600000;
        
        String uInput = "";
        
        while(!logOut)
        {
            try
            {
                clientTCPSocket.setSoTimeout(time);
                sendMessage(5,"");  // sends connected message to server (for server to add to list of online clients)
                while(!logOut)
                {
                    System.out.print("> ");
                    // read message from user
                    uInput = scan.nextLine();
                    
                    //If end user enters "Log off" correctly, client closes TCP connection.
                    if(uInput.equalsIgnoreCase(logoff))
                    {
                        // sends end chat to server if in chat
                        if(inChat)
                        {
                            sendMessage(3, recipientUser);
                        }
                        logOut = true;
                        break;
                    }
                    
                    //If end user enters "Chat Client-ID-B", attempt chat initiation.
                    //If Client B is available, chatting begins.
                    //If Client B is unreachable, prompt end user for a command. (loop while)
                    // CHAT REQUEST
                    else if(uInput.startsWith("chat ") || uInput.startsWith("Chat "))
                    {
                        
                        String temp[] = uInput.split(" ");
                        String userB = temp[1];
                        
                        // Chat Request message sent to server
                        sendMessage(0, userB);
                        
                    }
                    else if(uInput.equalsIgnoreCase("show online"))
                    {
                        sendMessage(4,"");  // show online request sent to server
                    }
                    else if(uInput.equalsIgnoreCase("end chat"))
                    {
                        if(inChat)  // sends end chat to server
                            sendMessage(3, recipientUser);
                        else
                        {
                            System.out.println("No chat to end");
                        }
                    }
                    else if(uInput.startsWith("history ") || uInput.startsWith("History "))
                    {
                        String temp[] = uInput.split(" ");
                        String userB = temp[1];
                        sendMessage(6, userB);  // sends history request to server
                    }
                    else
                    {
                        if (inChat)
                        {
                            sendChatMessage(recipientUser, uInput, chatSession);
                        }
                        else if (!inChat)
                        {
                            System.out.println("Invalid Command");
                        }
                        
                    }
                    
                } // end inner while looop
            }
            catch(SocketException s)
            {
                if (isChatting)
                {
                    System.out.println("Trying to restart Timer");
                }
                else
                {
                    isTimeout = true;
                    break;
                }
                //System.out.println ("What do you want from me: " + s );

            }
            if (logOut)
            {
                sendMessage(2, ""); // sends log off/ disconnect message to server
                System.out.println("OFFLINE");
                disconnect();       // closes socket n reading/writing streams
                break;
            }
        }
        

        
        
    } // end of connected()
    
    
    /**
     * ChatListener only listens for incoming messages from the server 
     * and doesn't manage the timer. It checks the isTimeout flag set 
     * by connected() to see whether or not it needs to halt execution. 
     * Otherwise, it listens for packets on loop.
     */
    class ChatListener extends Thread
    {
        ChatMessage receivedChat;
        boolean keepListening;
        boolean timedout;
        
        public ChatListener()
        {
            keepListening = true;
            timedout = false;
            
        }
        
        // to loop until LOGOUT
        public void run()
        {
            
            
            while(keepListening)
            {
                if (isTimeout)
                    keepListening = false;
                
                boolean noRepeat = false;
                
                try{
                    // try to get messages from server
                    receivedChat = (ChatMessage) sInput.readObject();
                }
                catch(IOException e)
                {
                    // Exception happens when socket times out
                    // if user isChatting, noRepeat is used so the ChatMessage object read before is not show again
                    noRepeat = true;
                    // if not user is not chatting, it disconnects users
                    if(!isChatting)
                    {
                        keepListening = false;
                        System.out.println("You were disconnected");
                        sendMessage(2, "");
                        logOut = true;
                        isTimeout = true;
                        disconnect();
                    }
                    
                }
                catch(ClassNotFoundException c){}
                
                if(!noRepeat)
                {
                    int type = receivedChat.getType();
                    String encryptedMsg = receivedChat.getMessage();
                    int cSession = receivedChat.getSessionID();
                    String user2 = receivedChat.getUserB();
                    
                    String msg = Crypt.decrypt(encryptedMsg, encryptionKey);
                    
                    switch (type)
                    {
                        case 0: // chat request message received from server
                            if (msg.equalsIgnoreCase("CHAT_STARTED"))
                            {
                                System.out.println(msg);
                                System.out.print("> ");
                            }
                            else if(msg.equalsIgnoreCase("UNREACHABLE"))
                            {
                                System.out.println("User is unreachable");
                                System.out.print("> ");
                            }
                            break;
                        case 1: // chat msg received from server from a client
                            if (!inChat)    // sets chatting status if not yet set
                            {
                                recipientUser = user2;
                                chatSession = cSession;
                                inChat = true;
                                isChatting = true;
                            }
                            System.out.println(msg);
                            System.out.print("> ");
                            break;
                        case 4: // prints show online users message from server
                            System.out.println(msg);
                            System.out.print("> ");
                            break;
                        case 5: // connected to server message receieved
                            // prints connected message and commands
                            System.out.println(msg);
                            System.out.println("\n*************************************************************");
                            System.out.println("COMMANDS:");
                            System.out.println("\t" + "chat <USERID>" 	+ "\t   " 	+ "initiate chat with another user");
                            System.out.println("\t" + "history <USERID>"+ "   "		+ "initiate chat with another user");
                            System.out.println("\t" + "log off" 		+ "\t\t   "	+ "log user off and ends program");
                            System.out.println("\t" + "show online" 	+ "\t   " 	+ "shows all online users");
                            System.out.println("\t" + "end chat" 		+ "\t   " 	+ "ends current chat session");
                            System.out.println("*************************************************************");
                            System.out.print("> ");
                            break;
                        case 7: // end chat notification from server
                            System.out.println(msg);
                            System.out.print("> ");
                            recipientUser = null;
                            inChat = false;
                            isChatting = false;
                            break;
                        case 8: // prints out chat history message from server
                            System.out.println(msg);
                            System.out.print("> ");
                            break;
                        default:
                            break;
                    }
                    
                }
            
                
                if(isTimeout || logOut)
                {
                    System.out.println("You were disconnected");
                    sendMessage(2, "");
                    logOut = true;
                    keepListening = false;
                    disconnect();
                }
                
            }
        }
    }// END of ChatListener


    void sendMessage(int type, String msg)
    {
        String encryptedMsg = Crypt.encrypt(msg, encryptionKey);
        
        ChatMessage newM = new ChatMessage(type, encryptedMsg);
        
        try {
            sOutput.writeObject(newM);
        }
        catch(IOException e) {
            //System.out.println("Exception writing to server: " + e);
        }
    }
    
    void sendChatMessage(String toUser, String msg, int session)
    {
        String encryptedMsg = Crypt.encrypt(msg, encryptionKey);
        
        ChatMessage newM = new ChatMessage(1, encryptedMsg);
        newM.setSessionID(session);
        newM.setUserB(recipientUser);
        try {
            sOutput.writeObject(newM);
            //System.out.println ("Message sent");
        }
        catch(IOException e) {
            System.out.println("Exception writing to server: " + e);
        }
    }
    
    /**
     * Closes the input and output streams and disconnects
     */
    private void disconnect()
    {
        try {
            if(sInput != null) sInput.close();
        }
        catch(Exception e) {}
        try {
            if(sOutput != null) sOutput.close();
        }
        catch(Exception e) {}
        try{
            if(clientTCPSocket != null) clientTCPSocket.close();
        }
        catch(Exception e) {}
    }
    
}
