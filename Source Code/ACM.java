import java.util.ArrayList;
import java.util.HashMap;

/**
 * An abstract ACM class that holds basic operation instructions
 */
public abstract class ACM {
    //region Variables
    protected final ArrayList<String> subjects;
    //List of the names of subjects
    protected final ArrayList<String> objects;
    //List of the names of the objects
    protected final ArrayList<ArrayList<Capabilities>> capabilityList;
    //A 2-dimensional list that contains the capability data for each subject with each object
    //First dimension is subject, second dimension is object
    final protected HashMap<String, Capabilities> defaultCapabilities;
    //The default capabilities to be given to each role
    final protected ArrayList<String> subjectRoles;
    //List of the roles for each subject
    final private ArrayList<LogEntry> log;
    //List of log data for testing the ACM
    //endregion
    //region Constructor
    /**
     * The default ACM Constructor
     */
    public ACM(){
        HashMap<String, Capabilities> defaultCapabilitiesIn = GetCapabilities();
        subjects = new ArrayList<String>();
        objects = new ArrayList<String>();
        capabilityList = new ArrayList<ArrayList<Capabilities>>();
        defaultCapabilities = defaultCapabilitiesIn;
        subjectRoles = new ArrayList<String>();
        log = new ArrayList<LogEntry>();
    }
    //endregion
    //region Read ACM Data
    /**
     * Prints the ACM as a table
     */
    protected void PrintACM(){
        StringBuilder sb = new StringBuilder();
        //region Find Max size for each column for clean formatting
        int[] maxSize = new int[objects.size() + 1];

        for (String subject : subjects)
            if (subject.length() > maxSize[0])
                maxSize[0] = subject.length();

        for (int x = 0; x < objects.size(); x++)
            maxSize[x + 1] = objects.get(x).length();

        int temp = 0;
        for (int x = 0; x < subjects.size(); x++){
            for (int y = 0; y < objects.size(); y++){
                temp = capabilityList.get(x).get(y).toString().length();
                if (temp > maxSize[y + 1])
                    maxSize[y + 1] = temp;
            }
        }
        //endregion
        //region Create Horizontal Line for formatting
        int numberOfColumns = objects.size() + 2;
        for (int size : maxSize)
            numberOfColumns += size;
        String horizontalLine = "=".repeat(numberOfColumns);

        System.out.println(horizontalLine);
        //endregion
        //region Print Items
        sb.setLength(0);
        sb.append('|');
        sb.append(" ".repeat(maxSize[0]));
        sb.append('|');
        for (int x = 0; x < objects.size(); x++){
            sb.append(objects.get(x));
            sb.append(" ".repeat(maxSize[x + 1] - objects.get(x).length()));
            sb.append('|');
        }
        System.out.println(sb.toString());
        //endregion
        //region Print Users and capabilities
        String subject = "";
        String capability = "";
        for (int x = 0; x < subjects.size(); x++){
            sb.setLength(0);
            sb.append('|');
            subject = subjects.get(x);
            sb.append(subject);
            sb.append(" ".repeat(maxSize[0] - subject.length()));
            sb.append('|');
            for (int y = 0; y < objects.size(); y++){
                capability = capabilityList.get(x).get(y).toString();
                sb.append(capability);
                sb.append(" ".repeat(maxSize[y + 1] - capability.length()));
                sb.append('|');
            }
            System.out.println(sb.toString());
        }
        //endregion
        System.out.println(horizontalLine);
    }
    /**
     * Prints the Subject names and their respective roles
     */
    protected void PrintUsers(){
        int[] maxSize = new int[2];

        for (String role : subjects)
            if (role.length() > maxSize[0])
                maxSize[0] = role.length();
        for (String name : subjectRoles)
            if (name.length() > maxSize[1])
                maxSize[1] = name.length();

        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(maxSize[0] + maxSize[1] + 3));
        String horizontalLine = sb.toString();
        System.out.println(horizontalLine);

        for (int x = 0; x < subjects.size(); x++){
            sb.setLength(0);
            sb.append('|');
            sb.append(subjects.get(x));
            sb.append(" ".repeat(maxSize[0] - subjects.get(x).length()));
            sb.append('|');
            sb.append(subjectRoles.get(x));
            sb.append(" ".repeat(maxSize[1] - subjectRoles.get(x).length()));
            sb.append('|');
            System.out.println(sb.toString());
        }
        System.out.println(horizontalLine);
    }
    /**
     * Prints the entire log
     */
    protected void PrintLog(){
        for (LogEntry use : log)
            System.out.println(use);
    }
    /**
     * Prints the log data where the specified subject was involved
     * @param subjectIn The subject to search for in the log
     */
    protected void PrintLog(String subjectIn){
        for (LogEntry use : log)
            if (use.subject.equals(subjectIn))
                System.out.println(use);
    }
    /**
     * Prints the log data where the permission status is the same as the specified status
     * @param permittedIn The status of the request. True is granted - False is denied
     */
    protected void PrintLog(boolean permittedIn){
        for (LogEntry use : log)
            if (use.permitted == permittedIn)
                System.out.println(use);
    }
    /**
     * Prints all possible roles
     */
    protected void PrintRoles(){
        for (String role : defaultCapabilities.keySet())
            System.out.println(role);
    }
    /**
     * Prints all possible capabilities
     */
    protected void PrintCapabilities(){
        //print all possible capabilities
        System.out.println(defaultCapabilities.get("Administrator"));
        //administrator always has every capability
    }
    /**
     * Prints all possible capabilities of a given role
     * @param roleIn The role to list capabilities for
     */
    protected void PrintCapabilities(String roleIn){
        if (defaultCapabilities.containsKey(roleIn))
            System.out.println(defaultCapabilities.get(roleIn).ListAllCapabilities());
        else
            System.out.println("Role: ".concat(roleIn).concat(" does not exist"));
    }
    /**
     * Finds the role of a specified subject
     * @param subjectIn The subject to find the role for
     * @return The role of the subject
     */
    protected String RoleOf(String subjectIn){
        int index = subjects.indexOf(subjectIn);
        if (index == -1)
            return "";

        return subjectRoles.get(index);
    }
    /**
     * Determines if a subject has a given capability on the specified object
     * @param objectIn The object to check for the capability
     * @param subjectIn The subject trying to perform an action
     * @param capabilityIn The capability being requested
     * @return Whether the subject has the capability on the object
     */
    protected boolean HasCapability(String objectIn, String subjectIn, String capabilityIn){
        if (objects.contains(objectIn) && subjects.contains(subjectIn))
            return capabilityList.get(subjects.indexOf(subjectIn)).get(objects.indexOf(objectIn)).HasAccess(capabilityIn);
        else
            return false;
    }
    //endregion
    //region Modify ACM
    /**
     * Add a new subject to the ACM with the given role
     * @param subjectIn The subject to add
     * @param roleIn The role to be given to the subject
     * @return Whether the addition of the subject was successful or not. True is success - False is fail
     */
    protected boolean AddSubject(String subjectIn, String roleIn){
        ArrayList<String> data = new ArrayList<String>();
        data.add("User to Add: ".concat(subjectIn));
        data.add("Role to Give: ".concat(roleIn));
        //region Check if subject exists and role exists
        if (subjects.contains(subjectIn)){
            data.add("Attempted to add subject that already existed");

            log.add(new LogEntry("", "Add Subject", false, data));
            return false;
        }
        if (!defaultCapabilities.containsKey(roleIn)){
            data.add("Role does not exist");

            log.add(new LogEntry("", "Add Subject", false, data));
            return false;
        }
        //endregion
        subjects.add(subjectIn);
        subjectRoles.add(roleIn);
        capabilityList.add(new ArrayList<Capabilities>());
        for (int x = 0; x < objects.size(); x++)
            capabilityList.get(subjects.size() - 1).add(new Capabilities(defaultCapabilities.get(roleIn)));

        data.add("Successfully added subject");
        log.add(new LogEntry("", "Add Subject", true, data));
        return true;
    }
    /**
     * Adds LogEntry data to the log
     * @param currentLogIn The data to add to the log
     */
    protected void AddLog(LogEntry currentLogIn){
        log.add(currentLogIn);
    }
    //endregion
    //region Abstract Methods
    /**
     * Generates a hashmap of the role names paired with their default capabilities
     * @return The default capabilities hashmap
     */
    protected abstract HashMap<String, Capabilities> GetCapabilities();
    /**
     * Generates a String array of the role names
     * @return An array containing the names of the roles
     */
    protected abstract String[] GetRoles();
    //endregion
}