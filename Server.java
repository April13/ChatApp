
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Random;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.nio.file.*;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.File;
import java.util.concurrent.TimeUnit;


/**
 * The server must be online and listening for the client's contact 
 * first over UDP, and then must be able to relay messages between 
 * 5 pairs of clients on TCP after the TCP connection has been 
 * established for each client.
 */
public class Server {
    
    private static int port;
    private static int ports[] = {5000, 5011, 5002, 5033, 5004, 5005, 5006, 5007, 5008, 5009};
    private static String user;
    private static String hashm;
    private static String hashs;
    private static String[][] keyTable = {{"UserA", "123A"}, {"UserB", "123B"},
    {"UserC", "123C"}, {"UserD", "123D"}, {"UserE", "123E"}, {"UserF", "123F"},
    {"UserG", "123G"}, {"UserH", "123H"}, {"UserI", "123I"}, {"UserJ", "123J"}};
    private static String[] sHash = new String[10];
    private static String[] randArr = new String[10];
    
    private static int tcpPort = 7689;	// TCP universal welcoming port
    
    private static ServerSocket connectSocket; 	//Made connectSocket and users static
    private static ArrayList<ClientThread> users; // an ArrayList to keep the list of the  connected Clients
    
    private static int uniqueId;    // a unique ID for each connection
    private static SimpleDateFormat sdf;
    private static int sessionID;
    private static DatagramSocket dr;
    
    static Server server;
    // chat history stores session id and users that chatted int session
    private Vector<ChatHistory> chatLogs;
    
    //If this is false, then that means the client's TCP connection attempt has failed in startUDP(),
    //and a new ClientThread will not be created as a result.
    static boolean isAuthSucc = false;
    
    /**
     * constructor
     */
    public Server() {
        try {
            connectSocket = new ServerSocket(tcpPort);
            users = new ArrayList<ClientThread>();
            sdf = new SimpleDateFormat("HH:mm:ss");
        } catch (IOException e) {
            System.out.println("Server Start Error: " + e);
        }
        chatLogs = new Vector<ChatHistory>();
    }
    
    /**
     * Run the application.
     *
     */
    public static void main(String[] args) throws IOException {
        
        server = new Server();
        
        //int i = 0;
        //Listen for incoming UDP datagrams.
        while (true) {
            //System.out.println("number of times inside while loop: "+ ++i);
            
            System.out.println();
            
            //A new UDP socket is created for every client that attempts to connect to the server.
            dr = new DatagramSocket(8756);
            
            server.startUDP(); 	//Start UDP connection.
            
            //TCP connection starts at the end of startUDP(), in which a call is made to startTCP().
        }
        
    }
    
    public void startUDP() throws SocketException, IOException {
        
        receiveClientHello(dr);
        int f = makeRand();
        String hash;
        System.out.println("Num: " + f);
        switch (user) {
            case "UserA":
                hash = Integer.toString(f) + keyTable[0][1];
                randArr[0] = hash;
                hash = Hash.IDCheck(hash);
                sHash[0] = hash;
                break;
                
            case "UserB":
                hash = Integer.toString(f) + keyTable[1][1];
                randArr[1] = hash;
                hash = Hash.IDCheck(hash);
                sHash[1] = hash;
                break;
                
            case "UserC":
                hash = Integer.toString(f) + keyTable[2][1];
                randArr[2] = hash;
                hash = Hash.IDCheck(hash);
                sHash[2] = hash;
                break;
                
            case "UserD":
                hash = Integer.toString(f) + keyTable[3][1];
                randArr[3] = hash;
                hash = Hash.IDCheck(hash);
                sHash[3] = hash;
                break;
                
            case "UserE":
                hash = Integer.toString(f) + keyTable[4][1];
                randArr[4] = hash;
                hash = Hash.IDCheck(hash);
                sHash[4] = hash;
                break;
                
            case "UserF":
                hash = Integer.toString(f) + keyTable[5][1];
                randArr[5] = hash;
                hash = Hash.IDCheck(hash);
                sHash[5] = hash;
                break;
                
            case "UserG":
                hash = Integer.toString(f) + keyTable[6][1];
                randArr[6] = hash;
                hash = Hash.IDCheck(hash);
                sHash[6] = hash;
                break;
                
            case "UserH":
                hash = Integer.toString(f) + keyTable[7][1];
                randArr[7] = hash;
                hash = Hash.IDCheck(hash);
                sHash[7] = hash;
                break;
                
            case "UserI":
                hash = Integer.toString(f) + keyTable[8][1];
                randArr[8] = hash;
                hash = Hash.IDCheck(hash);
                sHash[8] = hash;
                break;
                
            case "UserJ":
                hash = Integer.toString(f) + keyTable[9][1];
                randArr[9] = hash;
                hash = Hash.IDCheck(hash);
                sHash[9] = hash;
                break;
                
        }
        
        CHALLENGE(f, dr);
        receiveClientResp(dr);
        
        //Close the DatagramSocket so that other clients can connect afterwards.
        dr.close();
        System.out.println("The DatagramSocket has been closed.");
        //System.out.println();
        
        //*****Only create a new ClientThread if the TCP connection request from the client was successful.*****
        if (isAuthSucc) {
            //Set isAuthSucc back to false for the next client that attempts to create a TCP connection.
            isAuthSucc = false;
            
            server.startTCP(); 	//Start TCP connection.
        } else {
            System.out.println("***Client TCP connection was unsuccessful.***");
        }
        
    }// END of startUDP
    
    
    /** 
     * Creates a new dedicated listener for each successfully 
     * connected client. 
     */
    public void startTCP() throws IOException {
        
        //client socket accepts server welcoming socket
        Socket cSocket = connectSocket.accept();
        
        //Waiting for a connection request message from client.
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
        
        String userMsg = inFromClient.readLine();
        
        //Expecting "CONNECT_REQUEST,clientID"
        System.out.println("Received: " + userMsg);
        
        String uMsg[] = userMsg.split(",");
        // Server sends CONNECTED
        if (uMsg[0].equalsIgnoreCase("CONNECT_REQUEST")) {
            //Sent to client to let it know that it's now connected.
            String tcpMsg = "CONNECTED";
            
            PrintWriter printWriter = new PrintWriter(cSocket.getOutputStream());
            printWriter.println(tcpMsg);
            printWriter.flush();
            
            String userID = uMsg[1];
            
            //New client socket for a specific thread's serverSocket.
            Socket uSocket;
            String encryptKey = null;
            switch (userID) {
                case "UserA":
                    uSocket = new ServerSocket(ports[0]).accept();
                    encryptKey = randArr[0];
                    break;
                case "UserB":
                    uSocket = new ServerSocket(ports[1]).accept();
                    encryptKey = randArr[1];
                    break;
                case "UserC":
                    uSocket = new ServerSocket(ports[2]).accept();
                    encryptKey = randArr[2];
                    break;
                case "UserD":
                    uSocket = new ServerSocket(ports[3]).accept();
                    encryptKey = randArr[3];
                    break;
                case "UserE":
                    uSocket = new ServerSocket(ports[4]).accept();
                    encryptKey = randArr[4];
                    break;
                case "UserF":
                    uSocket = new ServerSocket(ports[5]).accept();
                    encryptKey = randArr[5];
                    break;
                case "UserG":
                    uSocket = new ServerSocket(ports[6]).accept();
                    encryptKey = randArr[6];
                    break;
                case "UserH":
                    uSocket = new ServerSocket(ports[7]).accept();
                    encryptKey = randArr[7];
                    break;
                case "UserI":
                    uSocket = new ServerSocket(ports[8]).accept();
                    encryptKey = randArr[8];
                    break;
                case "UserJ":
                    uSocket = new ServerSocket(ports[9]).accept();
                    encryptKey = randArr[9];
                    break;
                default:
                    uSocket = new ServerSocket(6000).accept();
                    break;
            }
            
            ClientThread t = new ClientThread(uSocket, userID, encryptKey);
            
            users.add(t);   // save it in the ArrayList
            
            t.start(); //start new thread
            
        }
    }//End of startTCP***********************************
    
    //Start of startUDP() related methods.=========================================
    public static void receiveClientHello(DatagramSocket Sock) throws IOException {
        byte[] hold = new byte[1024];
        DatagramPacket r = new DatagramPacket(hold, hold.length);
        Sock.receive(r);
        String str = new String(r.getData());
        port = r.getPort();
        String trim = str.trim();
        user = trim;
        System.out.println("receiveU sent: " + trim);
    }
    
    public static void receiveClientResp(DatagramSocket Sock) throws IOException {
        byte[] hold = new byte[1024];
        DatagramPacket r = new DatagramPacket(hold, hold.length);
        Sock.receive(r);
        String str = new String(r.getData());
        port = r.getPort();
        String trim = str.trim();
        //
        String delims = "[#]+";
        String[] tokens = trim.split(delims);
        
        /*
        *****TEST AUTHeNTICATION TIMEOUT*****
        try
        {
            TimeUnit.MINUTES.sleep(1);
        }
        catch(Exception e){}
        */
        
        
        switch (tokens[0]) {
            case "UserA":
                //
                // System.out.println("HASH: "+sHash[0]);
                System.out.println("TOKEN: " + tokens[1]);
                if (sHash[0].equals(tokens[1])) {
                    int f = makeRand();
                    AUTH_SUCCESS(f, ports[0], Sock, randArr[0]);
                } else {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserB":
                if (sHash[1].equals(tokens[1])) {
                    int f = makeRand();
                    AUTH_SUCCESS(f, ports[1], Sock, randArr[1]);
                } else {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserC":
                if (sHash[2].equals(tokens[1])) {
                    int f = makeRand();
                    AUTH_SUCCESS(f, ports[2], Sock, randArr[2]);
                } else {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserD":
                if (sHash[3].equals(tokens[1])) {
                    int f = makeRand();
                    AUTH_SUCCESS(f, ports[3], Sock, randArr[3]);
                } else {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserE":
                if (sHash[4].equals(tokens[1])) {
                    int f = makeRand();
                    AUTH_SUCCESS(f, ports[4], Sock, randArr[4]
                                 
                                 );
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
                
                
            case "UserF":
                if(sHash[5].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports[5],Sock,randArr[5]);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserG":
                if(sHash[6].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports[6],Sock,randArr[6]);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
            case "UserH":
                if(sHash[7].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports[7],Sock,randArr[7]);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserI":
                if(sHash[8].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports[8],Sock,randArr[8]);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserJ":
                if(sHash[9].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports[9],Sock,randArr[9]);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
        }
        System.out.println ("receiveS sent: " + trim);
    }
    
    
    
    public static int makeRand(){
        Random rnd = new Random();
        int n = 10000000 + rnd.nextInt(90000000);
        return n;
    }
    
    public static void CHALLENGE(int rand, DatagramSocket Sock) throws UnknownHostException, IOException {
        byte[] sb = (rand + "").getBytes();
        InetAddress net = InetAddress.getLocalHost();
        DatagramPacket ss = new DatagramPacket(sb, sb.length, net, port);
        Sock.send(ss);
    }
    
    public static void AUTH_SUCCESS(int rand_cookie, int port_number, DatagramSocket Sock,String cryKey) throws UnknownHostException, IOException
    {
        //Set isAuthSucc to true so that startTCP() will be called.
        isAuthSucc = true;
        String sec= rand_cookie + "," + port_number;
        byte[] sb = (Crypt.encrypt(sec,cryKey)).getBytes();
        InetAddress net = InetAddress.getLocalHost();
        DatagramPacket ss = new DatagramPacket(sb, sb.length, net, port);
        Sock.send(ss);
    }
    
    public static void AUTH_FAIL(DatagramSocket Sock) throws IOException {
        
        //isAuthSucc should be set to false so that a TCP thread will not be made.
        
        byte[] dr = ("authentication failed".getBytes());
        InetAddress net = InetAddress.getLocalHost();
        DatagramPacket ss = new DatagramPacket(dr, dr.length, net, port);
        Sock.send(ss);
    }
    //End of startUDP() related methods.=========================================
    
    
    

    class ClientThread extends Thread
    {
        // the socket where to listen/talk
        
        Socket socket;
        
        ObjectInputStream sInput;
        ObjectOutputStream sOutput;
        
        boolean inChatSession;
        int id;
        String userID;
        String date;
        String encryptionKey;
        
        String chatPartner;
        int currentChatSession;
        
        ChatMessage cm;
        
        
        /**
         * constructor
         */
        public ClientThread(Socket socket, String userID, String encryptionKey) {
            this.userID = userID;
            this.socket = socket;
            id = ++uniqueId;
            inChatSession = false;
            this.encryptionKey = encryptionKey;
            
            // Thread trying to create Object Input/Output Streams
            try {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput = new ObjectInputStream(socket.getInputStream());
                
                display(userID + " just connected.");
            } catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            }
            
            date = new Date().toString() + "\n";
        }
        
        
        
        // what will run forever
        public void run()
        {
            
            // to loop until LOGOUT
            boolean keepGoing = true;
            
            while(keepGoing)
            {
                // try read incoming ChatMessage object from users
                try
                {
                    cm = (ChatMessage) sInput.readObject();
                }
                catch (IOException e) {
                    display(userID + " is disconnected");
                    if (inChatSession)
                    {
                        ClientThread endUser = users.get(getUserIndexUsingID(chatPartner));
                        
                        // send chat end notification w/ info of chatting partner
                        endUser.sendMsg(7, "Chat with "+ userID + " ended.");
                        sendMsg(7, "Chat with "+ chatPartner + " ended.");
                        
                        endUser.inChatSession = false;
                        inChatSession = false;
                        endUser.chatPartner = null;
                        chatPartner = null;
                    }
                    keepGoing=false;
                    break;
                }
                catch(ClassNotFoundException e2) {break;}
                
                // getting the messaage part of the ChatMessage
                String encryptedMsg = cm.getMessage();
                int sessionNum = cm.getSessionID();
                int type = cm.getType();
                String user2 = cm.getUserB();
                // decrypts message
                String message = Crypt.decrypt(encryptedMsg, encryptionKey);
                
                
                // Switch on the type of message receive
                switch(type)
                {
                    case 0: // chat req
                        CHAT_REQUEST(userID, message);
                        break;
                    case 1: // received chat message from a user & send it to  intended recipient user
                        System.out.println("CHAT_MESSAGE received from "+ userID + " to " + user2);
                        
                        ClientThread sendtoClient = users.get(getUserIndexUsingID(user2));
                        String formatedMsg = sdf.format(new Date()) + " " + userID+ ": " + message;
                        sendtoClient.sendChatMsg(sessionNum, userID, formatedMsg);
                        RecordChat(currentChatSession, userID, user2, formatedMsg);
                        break;
                    case 2: // log out
                        display(userID + " disconnected.");
                        keepGoing = false;
                        break;
                    case 3: // end req received
                        //END NOTIFY
                        System.out.println("END_REQUEST from " + userID + " to " + message);
                        ClientThread endUser = users.get(getUserIndexUsingID(message));
                        
                        // send chat end notification w/ info of chatting partner
                        endUser.sendMsg(7, "Chat with "+ userID + " ended.");
                        sendMsg(7, "Chat with "+ message + " ended.");
                        
                        endUser.inChatSession = false;
                        inChatSession = false;
                        endUser.chatPartner = null;
                        chatPartner = null;
                        break;
                    case 4: // see online users
                        String names = "";
                        
                        for(int i = 0; i < users.size(); ++i)
                        {
                            ClientThread ct = users.get(i);
                            names += "\t" + ct.userID + "\n";
                        }
                        String msg ="List of the users connected at " + sdf.format(new Date()) + "\n" + names;
                        sendMsg(4, msg);
                        break;
                    case 5: // Connected to server message
                        sendMsg(5, "You are now connected to the Server.");
                        break;
                    case 6: // history request
                        String his = HISTORY(userID, message);
                        sendMsg(8, his);
                        break;
                    default:
                        break;
                }
                
                // User Disconnected
                if(!socket.isConnected())
                {
                    display(userID + " is disconnected");
                    keepGoing = false;
                }
            }// end of while loop
            
            // remove user from the arrayList containing the list of the connected Clients
            close();
            
            remove(id);
            
        }
        
        // try to close everything
        private void close() {
            // try to close the connection
            try {
                if (sOutput != null) {
                    sOutput.close();
                }
            } catch (Exception e) {
            }
            try {
                if (sInput != null) {
                    sInput.close();
                }
            } catch (Exception e) {
            };
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception e) {
            }
        }
        
        public void sendMsg(int type, String msg)
        {
            
            
            String encryptedMsg = Crypt.encrypt(msg, encryptionKey);
            
            ChatMessage newMsg = new ChatMessage(type, encryptedMsg);
            try {
                sOutput.writeObject(newMsg);
            } catch (IOException e) {
                display("Exception writing to server: " + e);
            }
        }
        // send received message from userB to User
        public void sendChatMsg(int session, String userB, String msg)
        {
            String encryptedMsg = Crypt.encrypt(msg, encryptionKey);
            
            ChatMessage newMsg = new ChatMessage(1, encryptedMsg);
            newMsg.setSessionID(session);
            newMsg.setUserB(userB);
            try {
                sOutput.writeObject(newMsg);
            } catch (IOException e) {
                display("Exception writing to server: " + e);
            }
        }
        
    }//End of ClientThread
    
    
    // displays string with time stamp
    private void display(String msg)
    {
        String time = sdf.format(new Date()) + " " + msg;
        System.out.println(time);
    }
    
    // for a client who logoff using the LOGOUT message
    synchronized void remove(int id)
    {
        // scan the array list until we found the Id
        for(int i = 0; i < users.size(); ++i)
        {
            ClientThread ct = users.get(i);
            if(ct.id == id)
            {
                users.remove(i);
                return;
            }
        }
    }
    
    
    public void CHAT_REQUEST(String userA, String userB)
    {
        System.out.println("CHAT_REQUEST from " + userA + " to " + userB);
        boolean startedChat = false;
        
        for(int i = 0; i < users.size(); ++i)
        {
            ClientThread ct = users.get(i);
            if(userB.equalsIgnoreCase(ct.userID) && ct.inChatSession == false)
            {
                int session = ++sessionID;
                
                ct.sendMsg(0, "CHAT_STARTED");
                ct.chatPartner = userA;
                ct.inChatSession = true;
                ct.currentChatSession = session;
                ct.sendChatMsg(session, userA, "Now Chatting with "+ userA);
                
                
                
                ClientThread ct2 = users.get(getUserIndexUsingID(userA));
                ct2.sendMsg(0, "CHAT_STARTED");
                ct.chatPartner = userB;
                ct2.inChatSession = true;
                ct2.currentChatSession = session;
                ct2.sendChatMsg(session, userB, "Now Chatting with "+ userB);
                // store chat session info
                //ChatHistory record = new ChatHistory(session, userA, userB);
                //chatLogs.add(record);
                // send chat start notification w/ info of chatting partner
                
                startedChat = true;
            }
        }
        // UNREACHABLE message sent if userB was not reached
        if(!startedChat)
        {
            ClientThread ct2 = users.get(getUserIndexUsingID(userA));
            ct2.sendMsg(0, "UNREACHABLE");
        }

    }
    
    
    public int getUserIndexUsingID(String userID)
    {
        for(int i = 0; i < users.size(); i++)
        {
            ClientThread ct = users.get(i);
            if(userID.equalsIgnoreCase(ct.userID))
            {
                return i;
            }
        }
        
        return 9999;
    }
    
    
    public String HISTORY(String requestingUser, String partner)
    {
        System.out.println("HISTORY_REQUEST from " + requestingUser + " with " + partner);
        String newline = System.getProperty("line.separator");
        boolean logFound = false;
        int sID;
        String CL = "";
        for(int i=chatLogs.size(); i>0; i--)
        {
            ChatHistory ch = chatLogs.get(i-1);
            if ((requestingUser.equalsIgnoreCase(ch.getUser1()) && partner.equalsIgnoreCase(ch.getUser2())) || (partner.equalsIgnoreCase(ch.getUser1()) && requestingUser.equalsIgnoreCase(ch.getUser2())))
            {
                sID = ch.getSessionID();
                try
                {
                    String fileName = Integer.toString(sID) + ".txt";
                    //File file = new File(fileName);
                    FileReader read = new FileReader(fileName);
                    BufferedReader br = new BufferedReader(read);
                    String line;
                    while ((line = br.readLine()) != null)
                    {
                        CL += line + "\r\n";
                    }
                }
                catch (IOException e){return ("Chat Log file could not be read");}
                logFound = true;
                break;
            }
        }
        
        if (!logFound)
            return ("No History");
        
        return CL;
    }
    
    public void RecordChat(int s, String u1, String u2, String msg)
    {
        System.out.println("RECORDING chat message from " + u1 + " with " + u2);
        String fileName = Integer.toString(s) + ".txt";
        File f = new File(fileName);
        if(f.exists() && !f.isDirectory())
        {
            try
            {
                FileWriter fw = new FileWriter(fileName, true);
                fw.write(s + "- " + msg);
                fw.write("\r\n");
                fw.close();
            }
            catch (IOException e) {System.out.println("Chat not recorded");
            }
        }
        else if (!f.exists())
        {
            chatLogs.add(new ChatHistory(s, u1, u2));
            //File file = new File(s + ".txt");
            try
            {
                FileWriter fw = new FileWriter(fileName, true);
                fw.write(s + "- " + msg);
                fw.write("\r\n");
                fw.close();
            }
            catch (IOException e) {System.out.println("Chat not recorded");
            }
        }
    }
    
} // END of Server

