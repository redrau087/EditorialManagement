import java.util.ArrayList;
import java.util.HashMap;

/**
 * An ACM-inheriting class that implements the editorial management specific details for the ACM
 */
public final class EditorialACM extends ACM {
    //region Variables
    public HashMap<String, String> helpMessages = new HashMap<String, String>();
    private final ArrayList<String> createRoles = new ArrayList<String>();
    private ACMUse currentLog;
    private ArrayList<String> logData;
    //A hashmap of functionName - helpMessages for the cli
    //endregion
    //region Constructor
    /**
     * The default EditorialACM constructor
     */
    public EditorialACM(){
        super();
        createRoles.add("Author");
        createRoles.add("Author/Associate_Editor");
        createRoles.add("Author/Reviewer");
        createRoles.add("Administrator");

        helpMessages.put("Add", """
                Add {subjectName} {role}
                The subject must not already exist and the role must be a valid role
                This creates a new subject in the ACM with the given role""");
        helpMessages.put("Create", """
                Create {objectName} {subjectName}
                The object owner must already be enrolled in the system as either an author or administrator
                This command creates a new object in the ACM""");
        helpMessages.put("Edit", "Edit {objectName} {subjectName}\n" +
                "The subject must be an owner of {objectName}");
        helpMessages.put("Read", "Read {objectName} {subjectName}\n" +
                "The subject must have read access to {objectName}");
        helpMessages.put("Submit", "Submit {objectName} {subjectName}\n" +
                "The subject must be an owner of {objectName}\n" +
                "This gives editors ".concat(defaultCapabilities.get("Editor").ListAllCapabilities()));
        helpMessages.put("Send", """
                Send {objectName} {senderSubjectName} {receiverSubjectName}
                The sender must have send access to {objectName} and the receiver must be capable of accepting invitations
                This gives Associate_Editors and Reviewers Accept""");
        helpMessages.put("Accept", """
                Accept {objectName} {receiverSubjectName}
                The accepter must have accept access to {objectName}
                This gives Associate_Editors Read/Send/Review/Consider_Reviews
                This gives Reviewers Read/Review""");
        helpMessages.put("Review", """
                Review {objectName} {subjectName}
                The subject must have review access to {objectName}
                This removes all access except for Consider_Reviews (if the subject can have that)""");
        helpMessages.put("Consider_Reviews", """
                Consider_Reviews {objectName}, {subjectName}
                The subject must have Consider_Review access to {objectName}
                This removes all access""");

        logData = new ArrayList<String>();
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
                        new Capabilities(new String[] {"Owner", "Edit", "Read", "Submit"}, new boolean[] {false, false, false, false}),
                        new Capabilities(new String[] {"Read", "Send", "Review", "Consider_Reviews"},
                                new boolean[] {false, false, false, false}),
                        new Capabilities(new String[] {"Read", "Send", "Accept", "Review", "Consider_Reviews"},
                                new boolean[] {false, false, false, false, false}),
                        new Capabilities(new String[] {"Read", "Accept", "Review"}, new boolean[] {false, false, false}),
                        new Capabilities(new String[] {"Owner", "Edit", "Read", "Submit", "Send", "Accept", "Review", "Consider_Reviews"},
                                new boolean[] {true, true, true, true, true, true, true, true}),
                        new Capabilities(new String[] {"Owner", "Edit", "Read", "Submit", "Send", "Accept", "Review", "Consider_Reviews"},
                                new boolean[] {false, false, false, false, false, false, false, false}),
                        new Capabilities(new String[] {"Owner", "Edit", "Read", "Submit", "Accept", "Review"},
                                new boolean[] {false, false, false, false, false, false})
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
        return new String[] {"Author", "Editor", "Associate_Editor",
                "Reviewer", "Administrator", "Author/Associate_Editor", "Author/Reviewer"};
    }
    //endregion
    //region Using ACM
    /**
     * Creates a manuscript with the given name owned by the given subject
     * @param objectIn The manuscript name to create
     * @param subjectIn The author's name
     * @return Whether the action was successful
     */
    public boolean CreateManuscript(String objectIn, String subjectIn){
        String capabilityRequested = "Create";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;

        if (!createRoles.contains(subjectRoles.get(subjects.indexOf(subjectIn)))){
            logData.add("User does not have the correct role to create a manuscript");
            return false;
        }

        logData.add(subjectIn.concat(" created manuscript: ".concat(objectIn)));
        currentLog.permitted = true;
        int index = subjects.indexOf(subjectIn);
        objects.add(objectIn);

        for (int x = 0; x < subjects.size(); x++){
            if (x != index){
                if (subjectRoles.get(x).equals("Administrator")){
                    capabilityList.get(x).add(new Capabilities(defaultCapabilities.get("Administrator")));
                    logData.add("Gave administrator \"".concat(subjects.get(x)).concat("\" full access to \"").concat(objectIn).concat("\""));
                }
                else
                    capabilityList.get(x).add(new Capabilities(defaultCapabilities.get(RoleOf(subjects.get(x)))));
            }

            else{
                capabilityList.get(x).add(
                        new Capabilities(new String[] {"Owner", "Write", "Read", "Submit"},
                                new boolean[] {true, true, true, true}));
                logData.add("Gave subject \"".concat(subjects.get(x)).concat("\" owner access to \"").concat(objectIn).concat("\""));
            }
        }

        return true;
    }
    /**
     * Edits a manuscript with the given name
     * @param objectIn The manuscript to edit
     * @param subjectIn The subject attempting to edit the manuscript
     * @return Whether the action was successful
     */
    public boolean EditManuscript(String objectIn, String subjectIn){
        String capabilityRequested = "Edit";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;

        if (!HasCapability(objectIn, subjectIn, capabilityRequested)){
            logData.add("Subject does not have access to Edit the manuscript");
            return false;
        }
        else{
            currentLog.permitted = true;
            logData.add("Subject edited the manuscript");
            return true;
        }
    }
    /**
     * Reads a manuscript under the given name
     * @param objectIn The name of the manuscript to read
     * @param subjectIn The name of the subject attempting to read the manuscript
     * @return Whether the action was successful
     */
    public boolean ReadManuscript(String objectIn, String subjectIn){
        String capabilityRequested = "Read";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;
        AddACMUse(currentLog);


        if (HasCapability(objectIn, subjectIn, capabilityRequested)){
            currentLog.permitted = true;
            logData.add(subjectIn.concat(" read manuscript: ".concat(objectIn)));
            return true;
        }

        logData.add(subjectIn.concat(" attempted to read manuscript: ".concat(objectIn)));
        return false;
    }
    /**
     * Submits a given manuscript as a given subject
     * @param objectIn The manuscript to submit
     * @param subjectIn The subject attempting to submit the manuscript
     * @return Whether the action was successful
     */
    public boolean SubmitManuscript(String objectIn, String subjectIn){
        String capabilityRequested = "Submit";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;


        if (!HasCapability(objectIn, subjectIn, capabilityRequested)){
            logData.add(subjectIn.concat(" attempted to submit: ".concat(objectIn)).concat(" without submission permissions"));
            return false;
        }

        currentLog.permitted = true;
        logData.add(subjectIn.concat(" submitted: ").concat(objectIn));
        int objectIndex = objects.indexOf(objectIn);
        for (int x = 0; x < subjects.size(); x++){
            if (subjectRoles.get(x).equals("Editor")){
                capabilityList.get(x).get(objectIndex).GiveFullAccess();
                logData.add("Gave ".concat(subjects.get(x)).concat(": ").concat(capabilityList.get(x).get(objectIndex).toString()));
            }
        }

        return true;
    }
    /**
     * Sends an invitation for a manuscript to a target subject as a given subject
     * @param objectIn The manuscript to send an invitation for
     * @param subjectIn The subject sending an invitation
     * @param targetIn The subject to receive an invitation
     * @return Whether the action was successful
     */
    public boolean SendInvite(String objectIn, String subjectIn, String targetIn){
        String capabilityRequested = "Send";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;


        if (RoleOf(targetIn).equals("Associate_Editor")){
            if (!RoleOf(subjectIn).equals("Editor") && !RoleOf(subjectIn).equals("Administrator")){
                logData.add("Incorrect role for invitations. ".concat(RoleOf(subjectIn)).concat(" cannot invite Associate Editors"));
                return false; //associate editors can only be invited by editors and administrators
            }
        }
        if (RoleOf(targetIn).equals("Reviewer")){
            if (!RoleOf(subjectIn).equals("Associate_Editor") && !RoleOf(subjectIn).equals("Administrator")){
                logData.add("Incorrect role for invitations".concat(RoleOf(subjectIn)).concat(" cannot invite Reviewers"));
                return false; //reviewers can only be invited by associate editors and administrators
            }
        }
        currentLog.permitted = true;

        capabilityList.get(subjects.indexOf(targetIn)).get(objects.indexOf(objectIn)).SetAccess("Accept", true);
        logData.add("Gave: ".concat(targetIn).concat(" Accept"));
        return true;
    }
    /**
     * Accepts an invitation for a manuscript as a given subject
     * @param objectIn The manuscript to accept an invitation for
     * @param subjectIn The subject accepting the invitation
     * @return Whether the action was successful
     */
    public boolean AcceptInvite(String objectIn, String subjectIn){
        String capabilityRequested = "Accept";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;

        if (HasCapability(objectIn, subjectIn, capabilityRequested)){
            currentLog.permitted = true;
            capabilityList.get(subjects.indexOf(subjectIn)).get(objects.indexOf(objectIn)).GiveFullAccess();
            capabilityList.get(subjects.indexOf(subjectIn)).get(objects.indexOf(objectIn)).SetAccess(capabilityRequested, false);
            //remove ability to accept invite
            logData.add("Removed: ".concat(subjectIn).concat(" Accept"));
            logData.add("Gave: ".concat(subjectIn).concat(" ").concat(capabilityList.get(subjects.indexOf(subjectIn)).get(objects.indexOf(objectIn)).toString()));

            return true;
        }
        logData.add(subjectIn.concat(" does not have Accept"));

        return false;
    }
    /**
     * Reviews a manuscript as a given subject
     * @param objectIn The manuscript to review
     * @param subjectIn The subject reviewing the manuscript
     * @return Whether the action was successful
     */
    public boolean Review(String objectIn, String subjectIn){
        String capabilityRequested = "Review";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;

        if (capabilityList.get(subjects.indexOf(subjectIn)).get(objects.indexOf(objectIn)).HasAccess(capabilityRequested)){
            currentLog.permitted = true;
            capabilityList.get(subjects.indexOf(subjectIn)).get(objects.indexOf(objectIn)).GiveNoAccess();
            capabilityList.get(subjects.indexOf(subjectIn)).get(objects.indexOf(objectIn)).SetAccess("Consider Reviews", true);
            //cannot review more than once
            //if the subject is a reviewer they will lose all access. SetAccess only sets the capability if it exists
            logData.add("Removed: ".concat(subjectIn).concat(" Review"));
            logData.add("Gave: ".concat(subjectIn).concat(capabilityList.get(subjects.indexOf(subjectIn)).get(objects.indexOf(objectIn)).toString()));

            return true;
        }
        logData.add(subjectIn.concat(" does not have Review"));
        return false;
    }
    /**
     * Makes final review decisions on a manuscript as a given subject
     * @param objectIn The manuscript to make final reviews on
     * @param subjectIn The subject reviewing the manuscript
     * @return Whether the action was successful
     */
    public boolean ConsiderReviews(String objectIn, String subjectIn){
        String capabilityRequested = "Consider_Reviews";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;

        if (HasCapability(objectIn, subjectIn, capabilityRequested)){
            //options for reviewing later
            logData.add("Removed: ".concat(subjectIn).concat(capabilityList.get(subjects.indexOf(subjectIn)).get(objects.indexOf(objectIn)).toString()));
            capabilityList.get(subjects.indexOf(subjectIn)).get(objects.indexOf(objectIn)).GiveNoAccess();
            //No access after final reviews

            return true;
        }
        logData.add(subjectIn.concat(" does not have Consider Reviews"));
        return false;
    }
    /**
     * Runs a command String
     * @param command A command string to run. {action} {object} {subject1} {subject2} (optional)
     * @return Whether the command was successful
     */
    public boolean RunCommand(String command){
        logData = new ArrayList<String>();
        currentLog = new ACMUse(false, logData);
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
                    case "PrintLogUser":
                        PrintLog(args[1]);
                        return true;
                    case "PrintLogPermission":
                        PrintLog(Boolean.parseBoolean(args[1]));
                        return true;
                    default:
                        return false;
                }
            case 3:
                AddACMUse(currentLog);
                currentLog.capabilityRequested = args[0];

                if (args[0].equals("Create")){
                    if (objects.contains(args[1])){
                        currentLog.subject = args[2];
                        logData.add("The manuscript already exists");
                        return false;
                    }
                }
                else if (args[0].equals("Add")){
                    if (subjects.contains(args[2])){
                        currentLog.subject = args[1];
                        logData.add("The subject already exists");
                        AddACMUse(currentLog);
                        return false;
                    }
                }
                else{
                    currentLog.subject = args[2];
                    if (!objects.contains(args[1])){
                        logData.add("The manuscript does not exist");
                        return false;
                    }
                    if (!subjects.contains(args[2])){
                        logData.add("The subject does not exist");
                        return false;
                    }
                }

                return switch (args[0]) {
                    case "Add" -> AddUser(args[1], args[2]);
                    case "Create" -> CreateManuscript(args[1], args[2]);
                    case "Edit" -> EditManuscript(args[1], args[2]);
                    case "Read" -> ReadManuscript(args[1], args[2]);
                    case "Submit" -> SubmitManuscript(args[1], args[2]);
                    case "Accept" -> AcceptInvite(args[1], args[2]);
                    case "Review" -> Review(args[1], args[2]);
                    case "Consider Reviews" -> ConsiderReviews(args[1], args[2]);
                    default -> false;
                };
            case 4:
                AddACMUse(currentLog);
                if (!objects.contains(args[1])){
                    logData.add("The manuscript does not exist");
                    return false;
                }
                if (!subjects.contains(args[2])){
                    logData.add("The first subject does not exist");
                    return false;
                }
                if (!subjects.contains(args[3])){
                    logData.add("The second subject does not exist");
                    return false;
                }
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