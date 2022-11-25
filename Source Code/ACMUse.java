import java.util.ArrayList;

/**
 * A basic class to log ACM usage for testing / checking
 */
public final class ACMUse {
    //region Variables
    final String user;
    //The user requesting the action
    final String capabilityRequested;
    //The action requested
    boolean permitted;
    //Whether the action was permitted
    final ArrayList<String> effects;
    //Extra information
    //endregion
    //region Constructor
    /**
     * The default constructor
     * @param userIn The user requesting the action
     * @param capabilityRequestedIn The action requested
     * @param permittedIn Whether the action was permitted
     * @param effectsIn Extra information
     */
    public ACMUse(String userIn, String capabilityRequestedIn, boolean permittedIn, ArrayList<String> effectsIn){
        user = userIn;
        capabilityRequested = capabilityRequestedIn;
        permitted = permittedIn;
        effects = effectsIn;
    }
    //endregion
    //region Override
    /**
     * A toString override used to read log data
     * @return The String of data representing the log entry
     */
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();

        sb.append("User \"");
        sb.append(user);
        sb.append("\" requested \"");
        sb.append(capabilityRequested);
        sb.append("\". The request was ");
        sb.append((permitted) ? "granted" : "denied");
        sb.append("\n");
        for (String effect : effects){
            sb.append(effect);
            sb.append("\n");
        }

        return sb.toString();
    }
    //endregion
}