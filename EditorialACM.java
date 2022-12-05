import java.util.ArrayList;
import java.util.HashMap;

/**
 * An ACM-inheriting class that implements the editorial management specific details for the ACM
 */
public final class EditorialACM extends ACM {
    //region Variables
    public HashMap<String, String> helpMessages = new HashMap<String, String>();
    //A hashmap of functionName - helpMessages for the cli
    private final ArrayList<String> editorRoles = new ArrayList<String>();
    //An arraylist of the editor roles
    private final ArrayList<String> reviewerRoles = new ArrayList<String>();
    //An arraylist of the reviewer roles
    private final ArrayList<String> considerOptions = new ArrayList<String>();
    //An arraylist of the Consider_Reviews options
    private LogEntry currentLog;
    //The current log entry
    private ArrayList<String> logData;
    //The extra data for the log entry
    //endregion
    //region Constructor
    /**
     * The default EditorialACM constructor
     */
    public EditorialACM(){
        super();
        editorRoles.add("Associate_Editor");
        editorRoles.add("Author/Associate_Editor");
        editorRoles.add("Administrator");
        reviewerRoles.add("Reviewer");
        reviewerRoles.add("Author/Reviewer");
        reviewerRoles.add("Administrator");
        considerOptions.add("Accept");
        considerOptions.add("Accept_Minor");
        considerOptions.add("Accept_Major");
        considerOptions.add("Reject");
        considerOptions.add("Report");

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
                Consider_Reviews {objectName}, {subjectName} {decision}
                The subject must have Consider_Review access to {objectName}
                Decision options are "Accept", "Accept_Minor", "Accept_Major", "Reject", and "Report"
                This removes all access unless the subject is the owner""");
        helpMessages.put("Print", "Print\nThis prints the ACM");
        helpMessages.put("PrintUsers", "PrintUsers\nThis prints the users and their roles");
        helpMessages.put("PrintLog", "PrintLog\nThis prints the entire log of ACM usage");
        helpMessages.put("PrintLogUser", "PrintLogUser {subjectIn}\nThis prints the log data where the log.subject=subjectIn");
        helpMessages.put("PrintLogPermission", """
                PrintLogPermission {permitted}
                This prints the log data where the log.permitted=permitted
                {permitted} should equal either "true" or "false\"""");
        helpMessages.put("PrintCapabilities", "PrintCapabilities\nLists all the possible capabilities in the system");
        helpMessages.put("PrintCapabilitiesRole", "PrintCapabilities {roleName}\nLists all the possible capabilities of {roleName}");

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
    private boolean CreateManuscript(String objectIn, String subjectIn){
        String capabilityRequested = "Create";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;

        if (!defaultCapabilities.get(subjectRoles.get(subjects.indexOf(subjectIn))).ContainsCapability("Owner")){
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
                        new Capabilities(defaultCapabilities.get(subjectRoles.get(subjects.indexOf(subjectIn)))));
                capabilityList.get(x).get(capabilityList.get(x).size() - 1).AddOverlappedAccess(defaultCapabilities.get("Author")); //give only relevant access
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
    private boolean EditManuscript(String objectIn, String subjectIn){
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
    private boolean ReadManuscript(String objectIn, String subjectIn){
        String capabilityRequested = "Read";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;
        AddLog(currentLog);


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
    private boolean SubmitManuscript(String objectIn, String subjectIn){
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
                capabilityList.get(x).get(objectIndex).AddOverlappedAccess(defaultCapabilities.get("Editor"));
                capabilityList.get(x).get(objectIndex).SetAccess("Consider_Reviews", false); //must review first
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
    private boolean SendInvite(String objectIn, String subjectIn, String targetIn){
        String capabilityRequested = "Send";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;

        if (!RoleOf(targetIn).equals("Reviewer") && !RoleOf(targetIn).equals("Associate_Editor") &&
                !RoleOf(targetIn).equals("Author/Reviewer") && !RoleOf(targetIn).equals("Author/Associate_Editor")){
            logData.add("Incorrect role for invitations".concat(RoleOf(subjectIn)).concat(" cannot invite Reviewers"));
            return false;
        }

        if (HasCapability(objectIn, subjectIn, capabilityRequested)){
            currentLog.permitted = true;

            GetCapability(objectIn, targetIn).SetAccess("Accept", true);
            logData.add("Gave: ".concat(targetIn).concat(" Accept"));
            return true;
        }

        return false;
    }
    /**
     * Accepts an invitation for a manuscript as a given subject
     * @param objectIn The manuscript to accept an invitation for
     * @param subjectIn The subject accepting the invitation
     * @return Whether the action was successful
     */
    private boolean AcceptInvite(String objectIn, String subjectIn){
        String capabilityRequested = "Accept";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;

        if (HasCapability(objectIn, subjectIn, capabilityRequested)){
            currentLog.permitted = true;
            if (editorRoles.contains(subjectRoles.get(subjects.indexOf(subjectIn)))) //associate_editors
                GetCapability(objectIn, subjectIn).
                        AddOverlappedAccess(defaultCapabilities.get("Associate_Editor"));
            else if (reviewerRoles.contains(subjectRoles.get(subjects.indexOf(subjectIn)))) //reviewers
                GetCapability(objectIn, subjectIn).
                        AddOverlappedAccess(defaultCapabilities.get("Reviewer"));
            else //administrators
                return true;

            GetCapability(objectIn, subjectIn).SetAccess(capabilityRequested, false);
            //remove ability to accept invite
            logData.add("Removed: ".concat(subjectIn).concat(" Accept"));
            logData.add("Gave: ".concat(subjectIn).concat(" ").concat(GetCapability(objectIn, subjectIn).toString()));

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
    private boolean Review(String objectIn, String subjectIn){
        String capabilityRequested = "Review";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;

        if (HasCapability(objectIn, subjectIn, capabilityRequested)){
            currentLog.permitted = true;
            if (subjectRoles.get(subjects.indexOf(subjectIn)).equals("Administrator"))
                return true;

            GetCapability(objectIn, subjectIn).SetAccess(capabilityRequested, false);
            GetCapability(objectIn, subjectIn).SetAccess("Send", false);
            GetCapability(objectIn, subjectIn).SetAccess("Consider Reviews", true);
            if (!GetCapability(objectIn, subjectIn).HasAccess("Owner"))
                GetCapability(objectIn, subjectIn).SetAccess("Read", false);
            //cannot review more than once
            //if the subject is a reviewer they will lose all access. SetAccess only sets the capability if it exists
            logData.add("Removed: ".concat(subjectIn).concat(" Review"));
            logData.add("Gave: ".concat(subjectIn).concat(GetCapability(objectIn, subjectIn).toString()));

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
    private boolean ConsiderReviews(String objectIn, String subjectIn, String decisionIn){
        String capabilityRequested = "Consider_Reviews";
        currentLog.subject = subjectIn;
        currentLog.capabilityRequested = capabilityRequested;

        if (!considerOptions.contains(decisionIn)){
            logData.add("Consider_Reviews option: ".concat(decisionIn).concat(" does not exist"));
            return false;
        }

        if (HasCapability(objectIn, subjectIn, capabilityRequested)){
            logData.add(subjectIn.concat(" reviewed ".concat(objectIn)).concat(" and gave the decision: ").concat(decisionIn));
            if (subjectRoles.get(subjects.indexOf(subjectIn)).equals("Administrator"))
                return true;

            logData.add("Removed: ".concat(subjectIn).concat(GetCapability(objectIn, subjectIn).toString()));
            GetCapability(objectIn, subjectIn).GiveNoAccess();
            GetCapability(objectIn, subjectIn).SetAccess("Owner", true);

            if (HasCapability(objectIn, subjectIn, "Owner")){
                GetCapability(objectIn, subjectIn).AddOverlappedAccess(defaultCapabilities.get("Author"));
                logData.add("Gave: ".concat(subjectIn).concat(GetCapability(objectIn, subjectIn).toString()));
            }
            //No access after final reviews (unless owner)

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
        currentLog = new LogEntry(false, logData);
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
                    case "PrintCapabilities":
                        PrintCapabilities();
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
                    case "PrintCapabilitiesRole":
                        PrintCapabilities(args[1]);
                        return true;
                    default:
                        return false;
                }
            case 3:
                AddLog(currentLog);
                currentLog.capabilityRequested = args[0];

                if (args[0].equals("Create")){
                    if (objects.contains(args[1])){
                        currentLog.subject = args[2];
                        logData.add("The manuscript already exists");
                        return false;
                    }
                    if (!subjects.contains(args[2])){
                        logData.add("The subject does not exist");
                        return false;
                    }
                }
                else if (args[0].equals("Add")){
                    if (subjects.contains(args[2])){
                        currentLog.subject = args[1];
                        logData.add("The subject already exists");
                        return false;
                    }
                    else
                        currentLog.permitted = true;
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
                    case "Add" -> AddSubject(args[1], args[2]);
                    case "Create" -> CreateManuscript(args[1], args[2]);
                    case "Edit" -> EditManuscript(args[1], args[2]);
                    case "Read" -> ReadManuscript(args[1], args[2]);
                    case "Submit" -> SubmitManuscript(args[1], args[2]);
                    case "Accept" -> AcceptInvite(args[1], args[2]);
                    case "Review" -> Review(args[1], args[2]);
                    default -> false;
                };
            case 4:
                AddLog(currentLog);
                if (!objects.contains(args[1])){
                    logData.add("The manuscript does not exist");
                    return false;
                }
                if (!subjects.contains(args[2])){
                    logData.add("The first subject does not exist");
                    return false;
                }
                if (args[0].equals("Consider_Reviews"))
                    return ConsiderReviews(args[1], args[2], args[3]);

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