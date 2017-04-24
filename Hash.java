import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.*;



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
        
        String product = new String(byteArrayToHex(hash));
        
        
        return product;
    }
    
    /**
     * Translates the byte array into hexadecimal format before 
     * it is returned as a String in IDCheck.
     * 
     * Credit to: stackoverflow.com user Pointer Null
     * https://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
     */
    
    public static String byteArrayToHex(byte[] a) {
    	   StringBuilder sb = new StringBuilder(a.length * 2);
    	   for(byte b: a)
    	      sb.append(String.format("%02x", b));
    	   return sb.toString();
    	}
    
    
    
    
}
