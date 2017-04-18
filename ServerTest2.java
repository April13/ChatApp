
	import java.io.IOException;
	import java.net.*;
	import java.nio.ByteBuffer;
	import java.util.Random;

	/**
	 * Used to test ClientUDPConnection application.
	 * UDP server socket is bound to port number 9876.
	 * Listens for requests from clients.
	 *
	 */
	public class ServerTest2
	{
		
		private final static int BUFFER_SIZE = 1024;	//Size of the server's sending and receiving buffers.
		private final static int PORT = 9876;			//Server port number.
		private static DatagramSocket serverSocket;
		private static byte[] sendBuffer; 
		private static byte[] receiveBuffer;
        private static String[][] keyTable={{"UserA","123A"},{"UserB","123B"},
        {"UserC","123C"},{"UserD","123D"},{"UserE","123E"},{"UserF","123F"},
        {"UserG","123G"},{"UserH","123H"},{"UserI","123I"},{"UserJ","123J"}};
        private static String[] sHash = new String[10];
		
		public static void main(String args[]) throws IOException {	
			
			try
			{
				//Create new server socket with port number PORT.
				serverSocket = new DatagramSocket(PORT);
					
				//Create sending and receiving buffers for the server.
				sendBuffer = new byte[BUFFER_SIZE];
				receiveBuffer = new byte[BUFFER_SIZE];		
				
				
				//Listen for incoming messages from clients. 
				while(true)
				{		
					//Create packet to extract client message from the receive buffer.
					DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
					
					//Receive packet from server socket.
					serverSocket.receive(receivePacket);
					
					//Retrieve the client's message from the receive packet.
					String clientMsg = new String (receivePacket.getData());
					System.out.println("Server received: " + clientMsg);
					
					//Parse client message, which is 1024 characters long.
					clientMsg = clientMsg.substring(0, 5);
					
					//System.out.println("length of clientMsg: " + clientMsg.length());
					
					//Create server's response message to client which contains the random number generated.
//					String serverMsg = "12345678";
	                if(clientMsg.length() < 6){
	                	
	                	//System.out.println("inside if statement");
	                	
	                	int f = makeRand();
	                    String hash;                                
	                   
	                    switch(clientMsg){
	                        case "UserA":
	                        hash= Integer.toString(f)+keyTable[0][1];
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
	               
	                    } //End of switch statement.
	                    
	                    
	                    CHALLENGE(f, receivePacket);
	                }
	                
	                
                    String delims = "[,]+";
                    String[] tokens = clientMsg.split(delims);
                    
                    switch(tokens[0]){
                        case "UserA":
                        if(sHash[0].equals(tokens[1])){
	                        int f = makeRand(); 
	                        AUTH_SUCCESS(f, PORT);
                        }else 
                        {
                        	AUTH_FAIL();
                        }
                        break;
                        
                        case "UserB":
                        if(sHash[1].equals(tokens[1])){
	                        int f = makeRand(); 
	                        AUTH_SUCCESS(f, PORT);
                        }else
                        {
                        	AUTH_FAIL();
                        }
                        break;
                        
                        case "UserC":
                        if(sHash[2].equals(tokens[1])){
	                        int f = makeRand(); 
	                        AUTH_SUCCESS(f, PORT);
                        }else
                        {
                        	AUTH_FAIL();
                        }
                        break;
                        
                        case "UserD":
                        if(sHash[3].equals(tokens[1])){
	                        int f = makeRand(); 
	                        AUTH_SUCCESS(f, PORT);
                        }else
                        {
                        	AUTH_FAIL();
                        }
                        break;
                       
                        case "UserE":
                        if(sHash[4].equals(tokens[1])){
	                        int f = makeRand(); 
	                        AUTH_SUCCESS(f, PORT);
                        }else
                        {
                        	AUTH_FAIL();
                        }
                        break;
                        
                        case "UserF":
                        if(sHash[5].equals(tokens[1])){
	                        int f = makeRand(); 
	                        AUTH_SUCCESS(f, PORT);
                        }else
                        {
                        	AUTH_FAIL();
                        }
                        break;
                        
                        case "UserG":
                        if(sHash[6].equals(tokens[1])){
                        int f = makeRand(); 
                        AUTH_SUCCESS(f, PORT);
                        }else
                        {
                        AUTH_FAIL();
                        }
                        break;
                        case "UserH":
                        if(sHash[7].equals(tokens[1])){
                        int f = makeRand(); 
                        AUTH_SUCCESS(f, PORT);
                        }else
                        {
                        AUTH_FAIL();
                        }
                        break;
                        
                        case "UserI":
                        if(sHash[8].equals(tokens[1])){
                        int f = makeRand(); 
                        AUTH_SUCCESS(f, PORT);
                        }else
                        {
                        AUTH_FAIL();
                        }
                        break;
                        
                        case "UserJ":
                        if(sHash[9].equals(tokens[1])){
                        int f = makeRand(); 
                        AUTH_SUCCESS(f, PORT);
                        }else
                        {
                        AUTH_FAIL();
                        }
                        break;
                     
                    }
                    
                    
//					//Retrieve IP address and port number of packet source.
//					InetAddress sourceIPAddress = receivePacket.getAddress();
//					int sourcePort = receivePacket.getPort();
//					
//					//Buffer server message in send buffer.
//					sendBuffer = serverMsg.getBytes();
//					
//					//Create new packet to send a message back to the client at sourceIPAddress, sourcePort.
//					DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, sourceIPAddress, sourcePort);
//					
//					serverSocket.send(sendPacket);
				}
			}
			finally 
			{
				//
			} 
			
		} //End of main method.

	public static int makeRand(){
		Random rnd = new Random();
		int n = 10000000 + rnd.nextInt(90000000);
		return n;
	}        
	        
	public static void CHALLENGE (int rand, DatagramPacket receivePacket) throws IOException{
		//DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
		//serverSocket.receive(receivePacket);
		InetAddress sourceIPAddress = receivePacket.getAddress();
		int sourcePort = receivePacket.getPort();
		sendBuffer=ByteBuffer.allocate(4).putInt(rand).array();
		DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, sourceIPAddress, sourcePort);
		serverSocket.send(sendPacket);
		
		System.out.println("end of challenge");
	}

	public static void AUTH_SUCCESS(int rand_cookie, int port_number) throws IOException{
	DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
	serverSocket.receive(receivePacket);
	InetAddress sourceIPAddress = receivePacket.getAddress();
	int sourcePort = receivePacket.getPort();
	String cookie= Integer.toString(rand_cookie);
	String portNum=Integer.toString(port_number);
	String cookiePort= cookie+","+portNum;
	sendBuffer=cookiePort.getBytes();
	DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, sourceIPAddress, sourcePort);
	serverSocket.send(sendPacket);
	}

	public static void AUTH_FAIL() throws IOException{
	DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
	serverSocket.receive(receivePacket);
	InetAddress sourceIPAddress = receivePacket.getAddress();
	int sourcePort = receivePacket.getPort();
	String fail = "Authentication Failed";
	sendBuffer=fail.getBytes();
	DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length, sourceIPAddress, sourcePort);
	serverSocket.send(sendPacket);
	}
	        

	}


