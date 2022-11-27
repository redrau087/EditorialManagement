import java.util.Arrays;

/**
 * A class to hold the names and permissions for each ACM entry
 */
public final class Capabilities {
    //region Variables
    private final String[] capabilityNames;
    //The names of the capabilities
    private final boolean[] capabilityAccess;
    //Whether the capabilities are allowed
    //endregion
    //region Constructors
    /**
     * The default Capabilities constructor
     * @param capabilityNamesIn The names of the capabilities
     * @param capabilityAccessIn Whether the capabilities are allowed
     */
    public Capabilities(final String[] capabilityNamesIn, final boolean[] capabilityAccessIn){
        //make exception for not same size
        capabilityNames = new String[capabilityNamesIn.length];
        capabilityAccess = new boolean[capabilityAccessIn.length];
        System.arraycopy(capabilityNamesIn, 0, capabilityNames, 0, capabilityAccessIn.length);
        System.arraycopy(capabilityAccessIn, 0, capabilityAccess, 0, capabilityAccessIn.length);
    }
    /**
     * The copy constructor
     * @param capabilitiesIn The object to copy
     */
    public Capabilities(Capabilities capabilitiesIn){
        Capabilities temp = new Capabilities(capabilitiesIn.capabilityNames, capabilitiesIn.capabilityAccess);
        this.capabilityNames = temp.capabilityNames;
        this.capabilityAccess = temp.capabilityAccess;
    }
    //endregion
    //region Override
    /**
     * A toString override for testing
     * @return A string to represent the Capabilities object
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int x = 0; x < capabilityNames.length; x++){
            if (capabilityAccess[x]){
                if (!sb.isEmpty())
                    sb.append('/');

                sb.append(capabilityNames[x]);
            }
        }

        return sb.toString();
    }
    //endregion
    //region Access Methods
    /**
     * Lists the names of each capability even if it is not permitted
     * @return A string containing the names of all the capabilities
     */
    public String ListAllCapabilities(){
        StringBuilder sb = new StringBuilder();
        for (String capabilityName : capabilityNames) {
            if (!sb.isEmpty())
                sb.append('/');
            sb.append(capabilityName);
        }
        return sb.toString();
    }
    /**
     * Determines if a given capability is allowed
     * @param capabilityName The capability to check
     * @return Whether there is access for the capability. True is enabled - False is disabled
     */
    public boolean HasAccess(String capabilityName){
        int index = -1;
        for (int x = 0; x < capabilityNames.length; x++){
            if (capabilityNames[x].equals(capabilityName)){
                index = x;
                break;
            }
        }
        if (index == -1)
            return false;

        return capabilityAccess[index];
    }
    /**
     * Enables access to all capabilities
     */
    public void GiveFullAccess(){
        Arrays.fill(capabilityAccess, true);
    }
    /**
     * Disables access to all capabilities
     */
    public void GiveNoAccess(){
        Arrays.fill(capabilityAccess, false);
    }
    /**
     * Sets a specific capability to the given access
     * @param capabilityNameIn The capability to set
     * @param access The permission to give the capability
     */
    public void SetAccess(String capabilityNameIn, boolean access){
        for (int x = 0; x < capabilityNames.length; x++)
            if (capabilityNames[x].equals(capabilityNameIn))
                capabilityAccess[x] = access;
    }
    //endregion
}