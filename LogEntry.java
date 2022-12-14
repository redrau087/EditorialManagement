import java.util.ArrayList;

/**
 * A basic class to log ACM usage for testing / checking
 */
public final class LogEntry {
    //region Variables
    String subject;
    //The user requesting the action
    String capabilityRequested;
    //The action requested
    boolean permitted;
    //Whether the action was permitted
    ArrayList<String> effects;
    //Extra information
    //endregion
    //region Constructor
    /**
     * The partial constructor
     * @param permittedIn Whether the action was permitted
     * @param effectsIn Extra information
     */
    public LogEntry(boolean permittedIn, ArrayList<String> effectsIn){
        subject = "";
        capabilityRequested = "";
        permitted = permittedIn;
        effects = effectsIn;
    }
    /**
     * The complete constructor
     * @param subjectIn The subject requesting a capability
     * @param capabilityRequestedIn The capability requested
     * @param permittedIn Whether the action was permitted
     * @param effectsIn Extra information
     */
    public LogEntry(String subjectIn, String capabilityRequestedIn, boolean permittedIn, ArrayList<String> effectsIn){
        subject = subjectIn;
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
        sb.append(subject);
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