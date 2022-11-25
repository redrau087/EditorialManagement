import javax.management.relation.Role;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * An ACM-inheriting class that implements the editorial management specific details for the ACM
 */
public final class EditorialACM extends ACM {
    //region Constructor
    /**
     * The default EditorialACM constructor
     */
    public EditorialACM(){
        super();
    }
    //endregion
    //region Overrides
    /**
     * Generates a hashmap of the role names paired with their default capabilities
     * @return The default capabilities hashmap
     */
    @Override
    protected HashMap<String, Capabilities> GetCapabilities(){
        Capabilities[] temp = new Capabilities[]
                {
                        new Capabilities(new String[] {"Owner", "Write", "Read", "Submit"}, new boolean[] {false, false, false, false}),
                        new Capabilities(new String[] {"Read", "Send", "Review", "Consider Reviews"},
                                new boolean[] {false, false, false, false}),
                        new Capabilities(new String[] {"Read", "Send", "Accept", "Review", "Consider Reviews"},
                                new boolean[] {false, false, false, false, false}),
                        new Capabilities(new String[] {"Read", "Accept", "Review"}, new boolean[] {false, false, false}),
                        new Capabilities(new String[] {"Write", "Read", "Submit", "Send", "Accept", "Review", "Consider Reviews"},
                                new boolean[] {true, true, true, true, true, true, true})
                };
        HashMap<String, Capabilities> capabilitiesHashMap = new HashMap<String, Capabilities>();
        String[] tempRoles = GetRoles();
        for (int x = 0; x < tempRoles.length; x++)
            capabilitiesHashMap.put(tempRoles[x], temp[x]);

        return capabilitiesHashMap;
    }
    /**
     * Generates a String array of the role names
     * @return An array containing the names of the roles
     */
    @Override
    protected String[] GetRoles(){
        return new String[] {"Author", "Editor", "Associate_Editor", "Reviewer", "Administrator"};
    }
    //endregion
    //region ACM Entry Checks
    /**
     * Determines if the subject exists in the ACM
     * @param userIn The subject to check
     * @return Whether the subject doesn't exist
     */
    private boolean NotExist(String userIn){
        return !users.contains(userIn);
    }
    /**
     * Determines if the subject and object exists in the ACM
     * @param itemIn The object to check
     * @param userIn The subject to check
     * @return Whether the subject or object doesn't exist
     */
    private boolean NotExist(String itemIn, String userIn){
        return !items.contains(itemIn) || !users.contains(userIn);
    }
    /**
     * Determines if both subjects and object exists in the ACM
     * @param itemIn The object to check
     * @param user1In The first subject to check
     * @param user2In The second subject to check
     * @return Whether either subject or the object doesn't exist
     */
    private boolean NotExist(String itemIn, String user1In, String user2In){
        return !items.contains(itemIn) || !users.contains(user1In) || !users.contains(user2In);
    }
    //endregion
    //region Using ACM
    /**
     * Creates a manuscript with the given name owned by the given user
     * @param itemIn The manuscript name to create
     * @param userIn The author's name
     * @return Whether the action was successful
     */
    public boolean WriteManuscript(String itemIn, String userIn){
        ArrayList<String> data = new ArrayList<String>();
        ACMUse acmUse = new ACMUse(userIn, "Write", false, data);
        AddACMUse(acmUse);

        if (NotExist(userIn)){
            data.add("Either the manuscript already exists or the user does not exist");
            return false;
        }

        if (!RoleOf(userIn).equals("Author") && !RoleOf(userIn).equals("Administrator")){
            data.add("User does not have the correct role to create a manuscript");
            return false; //replace with exception
        }

        data.add(userIn.concat(" created manuscript: ".concat(itemIn)));
        acmUse.permitted = true;
        int index = users.indexOf(userIn);
        items.add(itemIn);

        for (int x = 0; x < users.size(); x++){
            if (x != index){
                if (userRoles.get(x).equals("Administrator")){
                    capabilityList.get(x).add(
                            new Capabilities(new String[] {"Write", "Read", "Submit", "Send", "Accept", "Review", "Consider Reviews"},
                                    new boolean[] {true, true, true, true, true, true, true}));
                    data.add("Gave administrator \"".concat(users.get(x)).concat("\" full access to \"").concat(itemIn).concat("\""));
                }
                else
                    capabilityList.get(x).add(new Capabilities(defaultCapabilities.get(RoleOf(users.get(x)))));
            }

            else{
                capabilityList.get(x).add(
                        new Capabilities(new String[] {"Owner", "Write", "Read", "Submit"},
                                new boolean[] {true, true, true, true}));
                data.add("Gave user \"".concat(users.get(x)).concat("\" owner access to \"").concat(itemIn).concat("\""));
            }
        }

        return true;
    }
    /**
     * Reads a manuscript under the given name
     * @param itemIn The name of the manuscript to read
     * @param userIn The name of the user attempting to read the manuscript
     * @return Whether the action was successful
     */
    public boolean ReadManuscript(String itemIn, String userIn){
        ArrayList<String> data = new ArrayList<String>();
        ACMUse acmUse = new ACMUse(userIn, "Read", false, data);
        AddACMUse(acmUse);

        if (NotExist(itemIn, userIn)){
            data.add("Either the manuscript or user does not exist");
            return false;
        }

        acmUse.permitted = true;
        data.add(userIn.concat(" read manuscript: ".concat(itemIn)));
        int userIndex = users.indexOf(userIn);
        int itemIndex = items.indexOf(itemIn);

        return capabilityList.get(userIndex).get(itemIndex).HasAccess("Read");
    }
    /**
     * Submits a given manuscript as a given user
     * @param itemIn The manuscript to submit
     * @param userIn The user attempting to submit the manuscript
     * @return Whether the action was successful
     */
    public boolean SubmitManuscript(String itemIn, String userIn){
        ArrayList<String> data = new ArrayList<String>();
        ACMUse acmUse = new ACMUse(userIn, "Submit", false, data);
        AddACMUse(acmUse);

        if (NotExist(itemIn, userIn)){
            data.add("Either the manuscript or user does not exist");
            return false;
        }

        int userIndex = users.indexOf(userIn);
        int itemIndex = items.indexOf(itemIn);

        if (!capabilityList.get(userIndex).get(itemIndex).HasAccess("Submit")){
            data.add(userIn.concat(" attempted to submit: ".concat(itemIn)).concat(" without submission permissions"));
            return false;
        }

        acmUse.permitted = true;
        data.add(userIn.concat(" submitted: ").concat(itemIn));
        for (int x = 0; x < users.size(); x++){
            if (userRoles.get(x).equals("Editor")){
                capabilityList.get(x).get(itemIndex).GiveFullAccess();
                data.add("Gave ".concat(users.get(x)).concat(": ").concat(capabilityList.get(x).get(itemIndex).toString()));
            }
        }

        return true;
    }
    /**
     * Sends an invitation for a manuscript to a target user as a given user
     * @param itemIn The manuscript to send an invitation for
     * @param userIn The user sending an invitation
     * @param targetIn The user to receive an invitation
     * @return Whether the action was successful
     */
    public boolean SendInvite(String itemIn, String userIn, String targetIn){
        ArrayList<String> data = new ArrayList<String>();
        ACMUse acmUse = new ACMUse(userIn, "Send", false, data);
        AddACMUse(acmUse);

        if (NotExist(itemIn, userIn, targetIn)){
            data.add("Either the manuscript or users do not exist");
            return false;
        }

        if (RoleOf(targetIn).equals("Associate_Editor")){
            if (!RoleOf(userIn).equals("Editor") && !RoleOf(userIn).equals("Administrator")){
                data.add("Incorrect role for invitations. ".concat(RoleOf(userIn)).concat(" cannot invite Associate Editors"));
                return false; //associate editors can only be invited by editors and administrators
            }
        }
        if (RoleOf(targetIn).equals("Reviewer")){
            if (!RoleOf(userIn).equals("Associate_Editor") && !RoleOf(userIn).equals("Administrator")){
                data.add("Incorrect role for invitations".concat(RoleOf(userIn)).concat(" cannot invite Reviewers"));
                return false; //reviewers can only be invited by associate editors and administrators
            }
        }
        acmUse.permitted = true;

        capabilityList.get(users.indexOf(targetIn)).get(items.indexOf(itemIn)).SetAccess("Accept", true);
        data.add("Gave: ".concat(targetIn).concat(" Accept"));
        return true;
    }
    /**
     * Accepts an invitation for a manuscript as a given user
     * @param itemIn The manuscript to accept an invitation for
     * @param userIn The user accepting the invitation
     * @return Whether the action was successful
     */
    public boolean AcceptInvite(String itemIn, String userIn){
        ArrayList<String> data = new ArrayList<String>();
        ACMUse acmUse = new ACMUse(userIn, "Send", false, data);
        AddACMUse(acmUse);

        if (!items.contains(itemIn) || !users.contains(userIn)){
            data.add("Either the manuscript or user does not exist");
            return false;
        }

        if (capabilityList.get(users.indexOf(userIn)).get(items.indexOf(itemIn)).HasAccess("Accept")){
            capabilityList.get(users.indexOf(userIn)).get(items.indexOf(itemIn)).GiveFullAccess();
            capabilityList.get(users.indexOf(userIn)).get(items.indexOf(itemIn)).SetAccess("Accept", false);
            //remove ability to accept invite
            data.add("Removed: ".concat(userIn).concat(" Accept"));
            data.add("Gave: ".concat(userIn).concat(" ").concat(capabilityList.get(users.indexOf(userIn)).get(items.indexOf(itemIn)).toString()));
            acmUse.permitted = true;

            return true;
        }
        data.add(userIn.concat(" does not have Accept"));

        return false;
    }
    /**
     * Reviews a manuscript as a given user
     * @param itemIn The manuscript to review
     * @param userIn The user reviewing the manuscript
     * @return Whether the action was successful
     */
    public boolean Review(String itemIn, String userIn){
        ArrayList<String> data = new ArrayList<String>();
        ACMUse acmUse = new ACMUse(userIn, "Send", false, data);
        AddACMUse(acmUse);

        if (NotExist(itemIn, userIn)){
            data.add("Either the manuscript or user does not exist");
            return false;
        }

        if (capabilityList.get(users.indexOf(userIn)).get(items.indexOf(itemIn)).HasAccess("Review")){
            capabilityList.get(users.indexOf(userIn)).get(items.indexOf(itemIn)).GiveNoAccess();
            capabilityList.get(users.indexOf(userIn)).get(items.indexOf(itemIn)).SetAccess("Consider Reviews", true);
            //cannot review more than once
            data.add("Removed: ".concat(userIn).concat(" Review"));
            data.add("Gave: ".concat(userIn).concat(capabilityList.get(users.indexOf(userIn)).get(items.indexOf(itemIn)).toString()));
            return true;
        }
        data.add(userIn.concat(" does not have Review"));
        return false;
    }
    /**
     * Makes final review decisions on a manuscript as a given user
     * @param itemIn The manuscript to make final reviews on
     * @param userIn The user reviewing the manuscript
     * @return Whether the action was successful
     */
    public boolean ConsiderReviews(String itemIn, String userIn){
        ArrayList<String> data = new ArrayList<String>();
        ACMUse acmUse = new ACMUse(userIn, "Send", false, data);
        AddACMUse(acmUse);

        if (NotExist(itemIn, userIn)){
            data.add("Either the manuscript or user does not exist");
            return false;
        }

        if (capabilityList.get(users.indexOf(userIn)).get(items.indexOf(itemIn)).HasAccess("Consider Reviews")){
            //options for reviewing later
            data.add("Removed: ".concat(userIn).concat(capabilityList.get(users.indexOf(userIn)).get(items.indexOf(itemIn)).toString()));
            capabilityList.get(users.indexOf(userIn)).get(items.indexOf(itemIn)).GiveNoAccess();
            //No access after final reviews

            return true;
        }
        data.add(userIn.concat(" does not have Consider Reviews"));
        return false;
    }
    /**
     * Runs a command String
     * @param command A command string to run. {action} {item} {user1} {user2} (optional)
     * @return Whether the command was successful
     */
    public boolean RunCommand(String command){
        String[] args = command.split(" ");

        switch (args.length){
            case 1:
                switch(args[0]){
                    case "Print":
                        PrintACM();
                        return true;
                    case "PrintUsers":
                        PrintUsers();
                        return true;
                    case "PrintLog":
                        PrintLog();
                        return true;
                    default:
                        return false;
                }
            case 2:
                switch (args[0]){
                    case "PrintLogU":
                        PrintLog(args[1]);
                        return true;
                    case "PrintLogP":
                        PrintLog(Boolean.parseBoolean(args[1]));
                        return true;
                    default:
                        return false;
                }
            case 3:
                return switch (args[0]) {
                    case "Add" -> AddUser(args[1], args[2]);
                    case "Write" -> WriteManuscript(args[1], args[2]);
                    case "Read" -> ReadManuscript(args[1], args[2]);
                    case "Submit" -> SubmitManuscript(args[1], args[2]);
                    case "Accept" -> AcceptInvite(args[1], args[2]);
                    case "Review" -> Review(args[1], args[2]);
                    case "Consider Reviews" -> ConsiderReviews(args[1], args[2]);
                    default -> false;
                };
            case 4:
                if (args[0].equals("Send"))
                    return SendInvite(args[1], args[2], args[3]);
                else
                    return false;
            default:
                return false;
        }
    }
    //endregion
}