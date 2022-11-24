import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class ACM {
    //region Variables
    protected final ArrayList<String> users;
    //List of the names of subjects
    protected final ArrayList<String> items;
    //List of the names of the objects
    protected final ArrayList<ArrayList<Capabilities>> capabilityList;
    //A 2-dimensional list that contains the capability data for each subject with each object
    //First dimension is user, second dimension is object
    final protected HashMap<String, Capabilities> defaultCapabilities;
    //The default capabilities to be given to each role
    final protected ArrayList<String> userRoles;
    //List of the roles for each subject
    final private ArrayList<ACMUse> ACMUseList;
    //List of log data for testing the ACM
    //endregion
    //region Constructor
    /**
     * The default ACM Constructor
     */
    public ACM(){
        HashMap<String, Capabilities> defaultCapabilitiesIn = GetCapabilities();
        users = new ArrayList<String>();
        items = new ArrayList<String>();
        capabilityList = new ArrayList<ArrayList<Capabilities>>();
        defaultCapabilities = defaultCapabilitiesIn;
        userRoles = new ArrayList<String>();
        ACMUseList = new ArrayList<ACMUse>();
    }
    //endregion
    //region View ACM Data
    /**
     * Prints the ACM as a table
     */
    public void PrintACM(){
        StringBuilder sb = new StringBuilder();
        //region Find Max size for each column for clean formatting
        int[] maxSize = new int[items.size() + 1];

        for (String user : users)
            if (user.length() > maxSize[0])
                maxSize[0] = user.length();

        for (int x = 0; x < items.size(); x++)
            maxSize[x + 1] = items.get(x).length();

        int temp = 0;
        for (int x = 0; x < users.size(); x++){
            for (int y = 0; y < items.size(); y++){
                temp = capabilityList.get(x).get(y).toString().length();
                if (temp > maxSize[y + 1])
                    maxSize[y + 1] = temp;
            }
        }
        //endregion
        //region Create Horizontal Line for formatting
        int numberOfColumns = items.size() + 2;
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
        for (int x = 0; x < items.size(); x++){
            sb.append(items.get(x));
            sb.append(" ".repeat(maxSize[x + 1] - items.get(x).length()));
            sb.append('|');
        }
        System.out.println(sb.toString());
        //endregion
        //region Print Users and capabilities
        String user = "";
        String capability = "";
        for (int x = 0; x < users.size(); x++){
            sb.setLength(0);
            sb.append('|');
            user = users.get(x);
            sb.append(user);
            sb.append(" ".repeat(maxSize[0] - user.length()));
            sb.append('|');
            for (int y = 0; y < items.size(); y++){
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
    public void PrintUsers(){
        int[] maxSize = new int[2];

        for (String role : userRoles)
            if (role.length() > maxSize[0])
                maxSize[0] = role.length();
        for (String name : users)
            if (name.length() > maxSize[1])
                maxSize[1] = name.length();

        StringBuilder sb = new StringBuilder();
        sb.append("=".repeat(maxSize[0] + maxSize[1] + 3));
        String horizontalLine = sb.toString();
        System.out.println(horizontalLine);

        for (int x = 0; x < users.size(); x++){
            sb.setLength(0);
            sb.append('|');
            sb.append(users.get(x));
            sb.append(" ".repeat(maxSize[0] - users.get(x).length()));
            sb.append('|');
            sb.append(userRoles.get(x));
            sb.append(" ".repeat(maxSize[1] - userRoles.get(x).length()));
            sb.append('|');
            System.out.println(sb.toString());
        }
        System.out.println(horizontalLine);
    }
    /**
     * Prints the entire log
     */
    public void PrintLog(){
        for (ACMUse use : ACMUseList)
            System.out.println(use);
    }
    /**
     * Prints the log data where the specified subject was involved
     * @param userIn The subject to search for in the log
     */
    public void PrintLog(String userIn){
        for (ACMUse use : ACMUseList)
            if (use.user.equals(userIn))
                System.out.println(use);
    }
    /**
     * Prints the log data where the permission status is the same as the specified status
     * @param permittedIn The status of the request. True is granted - False is denied
     */
    public void PrintLog(boolean permittedIn){
        for (ACMUse use : ACMUseList)
            if (use.permitted == permittedIn)
                System.out.println(use);
    }
    /**
     * Finds the role of a specified subject
     * @param userIn The subject to find the role for
     * @return The role of the subject
     */
    protected String RoleOf(String userIn){
        int index = users.indexOf(userIn);
        if (index == -1)
            return "";

        return userRoles.get(index);
    }
    //endregion
    //region Modify ACM
    /**
     * Add a new subject to the ACM with the given role
     * @param userIn The subject to add
     * @param roleIn The role to be given to the subject
     * @return Whether the addition of the subject was successful or not. True is success - False is fail
     */
    public boolean AddUser(String userIn, String roleIn){
        ArrayList<String> data = new ArrayList<String>();
        data.add("User to Add: ".concat(userIn));
        data.add("Role to Give: ".concat(roleIn));
        //region Check if user exists and role exists
        if (users.contains(userIn) || !defaultCapabilities.containsKey(roleIn)){
            data.add("Attempted to add user that already existed");

            ACMUseList.add(new ACMUse("", "Add User", false, data));
            return false; //replace later with exception
        }
        //endregion
        users.add(userIn);
        userRoles.add(roleIn);
        capabilityList.add(new ArrayList<Capabilities>());
        for (int x = 0; x < items.size(); x++)
            capabilityList.get(users.size() - 1).add(new Capabilities(defaultCapabilities.get(roleIn)));

        data.add("Successfully added user");
        ACMUseList.add(new ACMUse("", "Add User", true, data));
        return true;
    }
    /**
     * Adds ACMUse data to the log
     * @param acmUseIn The data to add to the log
     */
    protected void AddACMUse(ACMUse acmUseIn){
        ACMUseList.add(acmUseIn);
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