
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Aditya
 */
public class Server {
    
    private static int port;
    private static int ports = 7689;
    private static String user;
    private static String hashm;
    private static String hashs;
    private static String[][] keyTable={{"UserA","123A"},{"UserB","123B"},
    {"UserC","123C"},{"UserD","123D"},{"UserE","123E"},{"UserF","123F"},
    {"UserG","123G"},{"UserH","123H"},{"UserI","123I"},{"UserJ","123J"}};
    private static String[] sHash = new String[10];
    
    //
    // TCP
    private static int tcpPort = 7689;
    private ServerSocket connectSocket;
    private ArrayList<ClientThread> users; // an ArrayList to keep the list of the ClientHost
    private static int uniqueId;    // a unique ID for each connection
    private SimpleDateFormat sdf;
    private static int sessionID;
    
    public static void main(String[] args) throws IOException
    {
        Server server = new Server();
        server.start();
    }
    
    public Server() {
        //
        // TCP
        try
        {
            connectSocket = new ServerSocket(tcpPort);
            users = new ArrayList<ClientThread>();
            sdf = new SimpleDateFormat("HH:mm:ss");
        }
        catch(IOException e){System.out.println("Server Start Error: "+e);}
    }
    
    
    public void start() throws SocketException, IOException {
        
        DatagramSocket dr = new DatagramSocket(8756);
        reciveU(dr);
        int f = makeRand();
        String hash;
        System.out.println("Num: " + f);
        switch(user){
            case "UserA":
                hash= Integer.toString(f)+keyTable[0][1];
                System.out.println(hash);
                hash= Hash.IDCheck(hash);
                sHash[0]=hash;
                //System.out.println("end of case UserA");
                break;
                
            case "UserB":
                hash= Integer.toString(f)+keyTable[1][1];
                hash=Hash.IDCheck(hash);
                sHash[1]=hash;
                break;
                
            case "UserC":
                hash= Integer.toString(f)+keyTable[2][1];
                hash=Hash.IDCheck(hash);
                sHash[2]=hash;
                break;
                
            case "UserD":
                hash= Integer.toString(f)+keyTable[3][1];
                hash=Hash.IDCheck(hash);
                sHash[3]=hash;
                break;
                
            case "UserE":
                hash= Integer.toString(f)+keyTable[4][1];
                hash=Hash.IDCheck(hash);
                sHash[4]=hash;
                break;
                
            case "UserF":
                hash= Integer.toString(f)+keyTable[5][1];
                hash=Hash.IDCheck(hash);
                sHash[5]=hash;
                break;
                
            case "UserG":
                hash= Integer.toString(f)+keyTable[6][1];
                hash=Hash.IDCheck(hash);
                sHash[6]=hash;
                break;
                
            case "UserH":
                hash= Integer.toString(f)+keyTable[7][1];
                hash=Hash.IDCheck(hash);
                sHash[7]=hash;
                break;
                
            case "UserI":
                hash= Integer.toString(f)+keyTable[8][1];
                hash=Hash.IDCheck(hash);
                sHash[8]=hash;
                break;
                
            case "UserJ":
                hash= Integer.toString(f)+keyTable[9][1];
                hash=Hash.IDCheck(hash);
                sHash[9]=hash;
                break;
	               
        }
        CHALLENGE(f, dr);
        reciveS(dr);
        
        //TCP starts
        //
        //
        Socket cSocket = connectSocket.accept();
        
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(cSocket.getInputStream()));
        String userMsg = inFromClient.readLine();
        System.out.println("Received: " + userMsg);
        String uMsg[] = userMsg.split(",");
        // Server sends CONNECTED
        if (uMsg[0].equalsIgnoreCase("CONNECT_REQUEST"))
        {
            String tcpMsg = "CONNECTED";
            
            PrintWriter printWriter = new PrintWriter(cSocket.getOutputStream());
            printWriter.println(tcpMsg);
            printWriter.flush();
            
            String userID = uMsg[1];
            ClientThread t = new ClientThread(cSocket, userID);
            users.add(t);   // save it in the ArrayList
            t.start();
        }
        
        
        
    }
    
    public static void reciveU(DatagramSocket Sock) throws IOException {
        byte[] hold = new byte[1024];
        DatagramPacket r = new DatagramPacket(hold, hold.length);
        Sock.receive(r);
        String str = new String(r.getData());
        port = r.getPort();
        String trim = str.trim();
        user = trim;
        System.out.println("sent: " + trim);
    }
    
    public static void reciveS(DatagramSocket Sock) throws IOException {
        byte[] hold = new byte[1024];
        DatagramPacket r = new DatagramPacket(hold, hold.length);
        Sock.receive(r);
        String str = new String(r.getData());
        port = r.getPort();
        String trim = str.trim();
        //
        String delims = "[,]+";
        String[] tokens = trim.split(",");
        switch(tokens[0])
        {
            case "UserA":
                //
                System.out.println("HASH: "+sHash[0]);
                System.out.println("TOKEN: "+tokens[1]);
                if(sHash[0].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f, ports, Sock);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserB":
                if(sHash[1].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports,Sock);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserC":
                if(sHash[2].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports,Sock);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserD":
                if(sHash[3].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports,Sock);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserE":
                if(sHash[4].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports,Sock);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserF":
                if(sHash[5].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports,Sock);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserG":
                if(sHash[6].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports,Sock);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
            case "UserH":
                if(sHash[7].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports,Sock);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserI":
                if(sHash[8].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports,Sock);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
            case "UserJ":
                if(sHash[9].equals(tokens[1])){
                    int f = makeRand();
                    AUTH_SUCCESS(f,ports,Sock);
                }else
                {
                    AUTH_FAIL(Sock);
                }
                break;
                
        }
        System.out.println("sent: " + trim);
    }
    
    public static int makeRand() {
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
    
    public static void AUTH_SUCCESS(int rand_cookie, int port_number, DatagramSocket Sock) throws UnknownHostException, IOException
    {
        byte[] sb = (rand_cookie + "," + port_number).getBytes();
        InetAddress net = InetAddress.getLocalHost();
        DatagramPacket ss = new DatagramPacket(sb, sb.length, net, port);
        Sock.send(ss);
    }
    
    public static void AUTH_FAIL(DatagramSocket Sock) throws IOException {
        byte[] dr = ("authentication failed".getBytes());
        InetAddress net = InetAddress.getLocalHost();
        DatagramPacket ss = new DatagramPacket(dr, dr.length, net, port);
        Sock.send(ss);
    }
    
    
    //
    //
    //  TCP
    
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
    
    //
    //
    public void CHAT_REQUEST(String userA, String userB)
    {
        for(int i = 0; i < users.size(); ++i)
        {
            ClientThread ct = users.get(i);
            if(ct.userID == userB && ct.inChatSession == false)
            {
                int session = ++sessionID;
                // CHAT_STARTED message sent
                ct.sendMsg(0, "CHAT_STARTED");
                ct.inChatSession = true;
                
                ClientThread ct2 = users.get(getUserIndexUsingID(userA));
                ct2.sendMsg(0, "CHAT_STARTED");
                ct2.inChatSession = true;
                
                ct.sendChatMsg(session, userA, "Now Chatting with "+ ct2.userID);
                
                ct2.sendChatMsg(session, userA, "Now Chatting with "+ ct.userID);
                
                return;
            }
            else
            {
                // UNREACHABLE message sent
                ct.sendMsg(0, "UNREACHABLE");
            }
        }
    }
    
    public int getUserIndexUsingID(String userID)
    {
        for(int i = 0; i < users.size(); ++i)
        {
            ClientThread ct = users.get(i);
            if(ct.userID == userID)
            {
                return i;
            }
        }
        
        return 9999;
    }
    
    public void END_REQUEST(String userA, String userB)
    {
        //END NOTIFY
        
    }
    
    public void CHAT(int session, String userB, String msg)
    {
        ClientThread ct = users.get(getUserIndexUsingID(userB));
        ct.sendChatMsg(session, userB, msg);
        
    }
    
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
        
        ChatMessage cm;
        
        public ClientThread(Socket socket, String userID)
        {
            this.userID = userID;
            this.socket = socket;
            id = ++uniqueId;
            inChatSession = false;
            
            // Thread trying to create Object Input/Output Streams
            try
            {
                sOutput = new ObjectOutputStream(socket.getOutputStream());
                sInput  = new ObjectInputStream(socket.getInputStream());
                
                display(userID + " just connected.");
            }
            catch (IOException e) {
                display("Exception creating new Input/output Streams: " + e);
                return;
            }
            
            date = new Date().toString() + "\n";
            
            run();
        }
        
        // what will run forever
        public void run() {
            // to loop until LOGOUT
            boolean keepGoing = true;
            while(keepGoing) {
                // read a String (which is an object)
                try
                {
                    cm = (ChatMessage) sInput.readObject();
                }
                catch (IOException e) {
                    display(userID + " Exception reading Streams: " + e);
                    break;
                }
                catch(ClassNotFoundException e2) {
                    break;
                }
                // the messaage part of the ChatMessage
                String message = cm.getMessage();
                
                // Switch on the type of message receive
                switch(cm.getType()) {
                        
                    case 0: // chat req
                        CHAT_REQUEST(userID, message);
                        break;
                    case 1: // message
                        
                        break;
                    case 2: // log out
                        display(userID + " disconnected with a LOGOUT message.");
                        keepGoing = false;
                        break;
                    case 3: // end req
                        
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
                    case 5:
                        sendMsg(5, "You are now connected to the Server.");
                        break;
                }
            }
            // remove myself from the arrayList containing the list of the
            // connected Clients
            remove(id);
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
            try {
                if(socket != null) socket.close();
            }
            catch (Exception e) {}
        }
        
        /*
         * Write a String to the Client output stream
         */
        private boolean writeMsg(String msg) {
            // if Client is still connected send the message to it
            if(!socket.isConnected()) {
                close();
                return false;
            }
            // write the message to the stream
            try {
                sOutput.writeObject(msg);
            }
            // if an error occurs, do not abort just inform the user
            catch(IOException e) {
                display("Error sending message to " + userID);
                display(e.toString());
            }
            return true;
        }
        
        public void sendMsg(int type, String msg)
        {
            
            ChatMessage newMsg = new ChatMessage(type, msg);
            try {
                sOutput.writeObject(newMsg);
            }
            catch(IOException e) {
                display("Exception writing to server: " + e);
            }
        }
        
        public void sendChatMsg(int session, String userB, String msg)
        {
            
            ChatMessage newMsg = new ChatMessage(1, msg);
            newMsg.setSessionID(session);
            newMsg.setUserB(userB);
            try {
                sOutput.writeObject(newMsg);
            }
            catch(IOException e) {
                display("Exception writing to server: " + e);
            }
        }
    }
    
} // END of Server
