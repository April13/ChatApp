
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.util.Random;

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
    private static int ports = 9512;
    private static String user;
    private static String hashm;
    private static String hashs;
    private static String[][] keyTable={{"UserA","123A"},{"UserB","123B"},
        {"UserC","123C"},{"UserD","123D"},{"UserE","123E"},{"UserF","123F"},
        {"UserG","123G"},{"UserH","123H"},{"UserI","123I"},{"UserJ","123J"}};
    private static String[] sHash = new String[10];

    public static void main(String arg[]) throws SocketException, IOException {

        DatagramSocket dr = new DatagramSocket(9875);
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
        String delims = "[,]+";
        String[] tokens = trim.split(delims);
        switch(tokens[0]){
                        case "UserA":
                        if(sHash[0].equals(tokens[1])){
	                        int f = makeRand(); 
	                        AUTH_SUCCESS(f,ports, Sock);
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

    public static void AUTH_SUCCESS(int rand_cookie, int port_number, DatagramSocket Sock) throws UnknownHostException, IOException {
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
}
