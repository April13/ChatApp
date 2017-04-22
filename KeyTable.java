
/**
 *
 * Protected class.
 *
 */

class KeyTable {
    
    
    //Stores the client IDs and their associated secret keys for 10 end users.
    //Used to validate that a pre-existing Client is logging on.
    
    private static String[][] keyTable={{"UserA","123A"},{"UserB","123B"},
    {"UserC","123C"},{"UserD","123D"},{"UserE","123E"},{"UserF","123F"},
    {"UserG","123G"},{"UserH","123H"},{"UserI","123I"},{"UserJ","123J"}};
    
    
    /**
     * Called by ClientHost to verify the Client exists.
     * The test compares userInput with actual clientIDs
     * stored in keyTable and returns true if found.
     *
     */
    protected static boolean testClientID(String userInput) {
        
        for(int i = 0; i < 10; i++) {
            if(userInput.equals(keyTable[i][0])) {	//Match client ID to one of the user IDs in keyTable.
                return true;
            }
        }
        
        return false;
    }
    
    
    
    /**
     * Called by Client to retrieve its secret key.
     *
     */
    protected static String getKey(String clientID) {
        
        String key = null;
        
        
        for(int i = 0; i < 10; i++) {
            
            if(clientID.equals(keyTable[i][0])) {	//Match client ID to one of the user IDs in keyTable.
                key = keyTable[i][1];
            }
        }
        
        return key;
    }

}
