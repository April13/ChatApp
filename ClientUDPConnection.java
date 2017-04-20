import java.net.*;
import java.io.IOException;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Base64;
import java.util.StringTokenizer;



/**
 * Resources referenced:
 * http://www.cs.usfca.edu/~parrt/course/601/lectures/sockets.html
 * http://cs.lmu.edu/~ray/notes/javanetexamples/
 * https://systembash.com/a-simple-java-udp-server-and-udp-client/
 * https://systembash.com/a-simple-java-tcp-server-and-tcp-client/
 */
class ClientUDPConnection {
    
    //Arguments passed in from Client.
    private String clientID;
    private String clientKey;	//Secret key.
    
    //Values provided by the server.
    protected int randNumber; 	//Sent by server in Challenge, used in calculating Client's response.
    protected String authfail = "authentication failed";
    protected String randCookie;	//Sent by server along with authSucc and TCPServerPort.
    //Used by Client later in TCP connection request.
    private int encryptionKey;		//Client uses randCookie to generate the encryption key.
    protected boolean authSucc = false;
    
    //Used to establish a UDP connection to the server.
    private InetAddress serverIPAddress;
    private DatagramSocket clientUDPSocket;
    private byte[] sendBuffer = new byte[1024];
    private byte[] receiveBuffer = new byte[1024];
    
    //Server socket destination port number.
    private int serverPort = 9875;
    protected int TCPServerPort;
    
    
    
    /**
     * Constructor for ClientUDPConnection.
     * Parameters are provided by the Client instance.
     */
    protected ClientUDPConnection(String clientID, String clientKey) {
        this.clientID = clientID;
        this.clientKey = clientKey;
    }
    
    
    
    
    /**
     * Initiates the process for the client to be authenticated by
     * the server. Receives a random number generated by the server
     * (from the server's challenge) to be used in response() method.
     */
    protected void hello() throws IOException
    {
        try
        {
            //Create a new UDP client socket.
            clientUDPSocket = new DatagramSocket();
            
            //Pass client ID to send buffer.
            sendBuffer = (clientID).getBytes();
            
            //Retrieve destination server IP address.
            serverIPAddress = InetAddress.getByName("localhost");
            
            //Send UDP packet to server to initiate connection.
            DatagramPacket sendPacket =
            new DatagramPacket(sendBuffer, sendBuffer.length, serverIPAddress, serverPort);
            clientUDPSocket.send(sendPacket);
            
            
            //Wait for Challenge from server containing rand. Server
            //message should be an 8-digit integer number, eg. 12345678.
		    		    
            
            //Create packet to extract message from the receive buffer.
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            
            //Receive packet from client socket.
            clientUDPSocket.receive(receivePacket);
            
            //Retrieve the client's message from the receive packet.
            String serverMsg = new String (receivePacket.getData()).trim();
            
            //Parse server message and fetch value of randCookie.
            randNumber = Integer.parseInt(serverMsg);
            
            //Testing.
            System.out.println("Server response: " + serverMsg); //Print server message.
            System.out.println("Client's randNumber: " + randNumber);
            
        }
        finally
        {
            //Don't close client socket yet.
        }
    }
    
    
    
    
    
    /**
     * Calculates the client's response value and sends it as the
     * client's response to the server's challenge in order to
     * authenticate itself. Waits for authSuccess(randCookie,
     * TCPPortNumber) or authFail() response. If authSuccess, the
     * server's encryptionKey should be the same and the client's.
     *
     */
    protected void response() throws IOException
    {
        try
        {
            //Calculate the Client's response using the rand sent by the server and the clientKey.
            
            System.out.println("clientKey: " + clientKey);
            
            //**************************************************************************************
            //works up to here
            //**************************************************************************************
            
            //String sendMsg = clientKey + Hash.IDCheck(randNumber + clientKey);
            String sendMsg = clientID + "," + Hash.IDCheck(randNumber + clientKey);
            
            
            
            //Pass response message to send buffer.
            sendBuffer = sendMsg.getBytes();
            
            //Retrieve destination server IP address.
            serverIPAddress = InetAddress.getByName("localhost");
            
            //Send UDP packet to provide the client's response to the server's authentication challenge.
            DatagramPacket sendPacket =
            new DatagramPacket(sendBuffer, sendBuffer.length, serverIPAddress, serverPort);
            clientUDPSocket.send(sendPacket);
            
            
            
            //*****Wait for response from server containing
            //a) authSuccess, randCookie, and TCPPortNumber, or
            //b) authFail.*****
            
            
            //Create packet to extract message from the receive buffer.
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            
            //Receive packet from client socket.
            clientUDPSocket.receive(receivePacket);
            
            //Retrieve the client's message from the receive packet.
            String serverMsg = new String (receivePacket.getData()).trim();
            
            
            //Testing.
            //System.out.println("Server's authSucc/authFail msg: " + serverMsg);
            
            
            
            
            //If the server's response isn't an AUTH_FAIL message,
            //the AUTH_SUCC message should have sent randCookie and TCPServerPort.
            
            if(!serverMsg.equalsIgnoreCase(authfail))
            {
                
                //Parse server message. 
                StringTokenizer tokenizer = new StringTokenizer(serverMsg, ",");
                
                authSucc = true;
                
                //Parse randCookie.
                randCookie = tokenizer.nextToken();
                
                //Testing.
                System.out.println("randCookie: " + randCookie);
                
                //Parse TCPServerPort.
                TCPServerPort = Integer.parseInt((tokenizer.nextToken()).trim());
                
                //Testing.
                System.out.println("TCPServerPort: " + TCPServerPort);
            } 
            else
            {
                //authFail
                System.out.println("Received AUTH_FAIL. Logging off.");
            }
            
            
        }
        finally
        {
            
            
            //*****Close UDP client socket, as future communication will be using TCP.******
            
            clientUDPSocket.close();
        }
    }
    
    
    
}


