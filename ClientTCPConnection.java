import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;


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
    BufferedReader inFromServer = null;
    PrintWriter printWriter = null;
    //
    ObjectInputStream sInput;
    ObjectOutputStream sOutput;
    private static int tcpPort = 7689;
    private boolean inChat;
    
    
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
        
        //Create a new TCP client socket connection. Server is already waiting for a connection request.
        clientTCPSocket = new Socket(serverIPAddress, tcpPort);
        
        
        //****Encrypt message.****
        
        
        //*******Weren't we supposed to send the randCookie to the server?*******
        
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
    
    //
    //
    protected void connected()
    {
        String userInput="";
        String fromServer = "";
        String logoff = "log off"; //For chat sessions.
        inChat = false;
        
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
        
        sendMessage(5,"");
        
        boolean logOut = false;
        boolean invalidCommand = false;
        
        Scanner scan = new Scanner(System.in);
        System.out.println("Please enter a command: ");
        //do
        //{
        while (true)
        {
            System.out.print("> ");
            // read message from user
            String uInput = scan.nextLine();
            
            //try
            //{
            //Read user input from command line.
            //userInput = consoleInput.readLine();
            
            //If end user enters "Log off" correctly, client closes TCP connection.
            if(uInput.equalsIgnoreCase(logoff))
            {
                sendMessage(2, "");
                
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
                
                // Chat Request
                sendMessage(0, userB);
                
                //Chat(userB);
            }
            else if(uInput.equalsIgnoreCase("show online"))
            {
                sendMessage(4,"");
            }
            else if(uInput.equalsIgnoreCase("end chat"))
            {
                sendMessage(3, recipientUser);
            }
            else
            {
                if (recipientUser != null && inChat)
                    sendChatMessage(recipientUser, uInput, chatSession);
                if (!inChat)
                    System.out.println("Invalid Command");
            }
        }
        //}catch(IOException e){}
        //} while (invalidCommand);
        
        if (logOut)
            disconnect();
        
    } // end of connected()
    
   
    class ChatListener extends Thread
    {
        ChatMessage receivedChat;
        boolean keepListening;
        
        public ChatListener()
        {
            keepListening = true;
        }
        
        // to loop until LOGOUT
        public void run()
        {
            //Receive message from server.
            
            while(keepListening)
            {
                try
                {
                    receivedChat = (ChatMessage) sInput.readObject();
                    
                    int type = receivedChat.getType();
                    String msg = receivedChat.getMessage();
                    int cSession = receivedChat.getSessionID();
                    String user2 = receivedChat.getUserB();
                    
                    switch (type)
                    {
                        case 0: // chat request
                            if (msg.equalsIgnoreCase("CHAT_STARTED"))
                            {
                                inChat = true;
                                System.out.println(msg);
                                System.out.print("> ");
                            }
                            else if(msg.equalsIgnoreCase("UNREACHABLE"))
                            {
                                System.out.println("User is unreachable");
                                System.out.print("> ");
                            }
                            break;
                        case 1: // chat msg
                            if (!inChat)
                            {
                                recipientUser = user2;
                                chatSession = cSession;
                                inChat = true;
                            }
                            System.out.println(msg);
                            System.out.print("> ");
                            break;
                        case 2: // log off
                            //clientTCPSocket.close();
                            break;
                        case 4: // show online users
                            System.out.println(msg);
                            System.out.print("> ");
                            break;
                        case 5: // connected
                            // prints connected message and commands
                            System.out.println(msg);
                            System.out.println("\n********************************************************");
                            System.out.println("COMMANDS:");
                            System.out.println("\tchat <USERID>: initiate chat with another user");
                            System.out.println("\tlog off: log user off and ends program");
                            System.out.println("\tshow online: shows all online users");
                            System.out.println("\tend chat: ends current chat session");
                            System.out.println("********************************************************");
                            System.out.print("> ");
                            break;
                        case 7: // end chat notification
                            System.out.println(chatMsg);
                            System.out.print("> ");
                            recipientUser = null;
                            inChat = false;
                            break;
                        default:
                            break;
                    }
                }
                catch (IOException e) {
                    System.out.println(" Exception reading Streams: " + e);
                    break;
                }
                catch(ClassNotFoundException e2) {break;}
            }
        }
    }// END of ChatListener


    void sendMessage(int type, String msg)
    {
        ChatMessage newM = new ChatMessage(type, msg);
        try {
            sOutput.writeObject(newM);
        }
        catch(IOException e) {
            System.out.println("Exception writing to server: " + e);
        }
    }
    
    void sendChatMessage(String toUser, String msg, int session)
    {
        ChatMessage newM = new ChatMessage(1, msg);
        newM.setSessionID(session);
        newM.setUserB(toUser);
        try {
            sOutput.writeObject(newM);
            System.out.println ("Message sent");
        }
        catch(IOException e) {
            System.out.println("Exception writing to server: " + e);
        }
    }
    
    /*
     * Closes the input and output streams and disconnect
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
