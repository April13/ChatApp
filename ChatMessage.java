import java.io.*;

/*
 * Contains the type of messages 
 * sent between clients and server
 */

public class ChatMessage implements Serializable {

    protected static final long serialVersionUID = 1112122200L;

    // Types of message sent by a Client:
    // ONLINEUSERS to receive a list of all the connected users
    // MESSAGE an ordinary message
    // LOGOUT to disconnect from the Server
    static final int CHAT_REQUEST = 0, MESSAGE = 1, LOGOUT = 2, END_REQUEST = 3, SHOW_ONLINE = 4, CONNECTED = 5;
    private int type;
    private String message;
    
    private int sessionID;
    private String userB;
    
    ChatMessage(int type, String message) {
        this.type = type;
        this.message = message;
    }
    
    // getters
    int getType() {
        return type;
    }
    String getMessage() {
        return message;
    }
    
    int getSessionID()
    {
        return sessionID;
    }
    
    void setSessionID(int id)
    {
        this.sessionID = id;
    }
    
    String getUserB()
    {
        return userB;
    }
    
    void setUserB(String id)
    {
        this.userB = id;
    }
}
