import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;



class Hash {
    
    /**
     * randNumber
     * Used to calculated Client's response message.
     */
    static String IDCheck(String source){
        
        byte[] temp=source.getBytes();
        byte[] hash = null;
        
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            hash=md.digest(temp);
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(Hash.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        String product = new String(hash);
        
        
        return product;
    }
    
    
    
    
    /**
     * Encryption algorithm for TCP messages.
     */
    
    
    
    
}
