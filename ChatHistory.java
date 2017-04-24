import java.io.*;

/**
 * The ChatHistory class is used by the server to store 
 * information related to chats between each pair of 
 * clients. It saves the clientIDs of the two clients and 
 * the sessionId which identifies each chat session.
 */
class ChatHistory
{
	private String user1, user2;
    private int sessionID;

	public ChatHistory(int sessionID, String user1, String user2)
	{
		this.sessionID = sessionID;
		this.user1 = user1;
		this.user2 = user2;
	}
    
    public ChatHistory(String user1, String user2)
    {
        this.user1 = user1;
        this.user2 = user2;
    }

    public int getSessionID()
    {
        return sessionID;
    }
    
    public String getUser1()
    {
        return user1;
    }
    
    public String getUser2()
    {
        return user2;
    }


}
