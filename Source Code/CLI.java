import java.util.Collection;
import java.util.Scanner;

/**
 * A simple CLI to run the EditorialACM class
 */
public class CLI {
    //region Variables
    private String command = "";
    //the current command (pre-allocated memory instead of re-allocating for each iteration)
    private final Scanner scanner = new Scanner(System.in);
    //the command prompt scanner
    private final EditorialACM system;
    //the editorial management ACM
    //endregion
    //region Constructor
    /**
     * The default CLI constructor
     */
    public CLI(){
        system = new EditorialACM();
    }
    //endregion
    //region CLI Options
    /**
     * The CLI loop
     * @param escape The String to enter to close the CLI
     */
    public void RunCLI(String escape){
        while (true){
            ReadLine(escape);
        }
    }
    /**
     * Reads the current line in the CLI and runs the command
     * @param escape The String to enter to close the CLI
     */
    private void ReadLine(String escape){
        System.out.println("Input a command. Type help for help or ".concat(escape).concat(" to exit"));
        command = scanner.nextLine();
        String[] args = command.split(" ");

        if (args.length == 1){
            String helpMessage = """
                    To check a command, type "help {commandName}"
                    To see all the commands, type "list"
                    To see all the role names, type "roles\"""";
            if (command.equalsIgnoreCase("help"))
                System.out.println(helpMessage);
            else if (command.equalsIgnoreCase("list")){
                for (String commandName : system.helpMessages.keySet())
                    System.out.println(commandName);
            }
            else if (command.equalsIgnoreCase("roles"))
                system.PrintRoles();
            else{
                if (!system.RunCommand(command))
                    System.out.println("The command failed");
            }
            if (!system.RunCommand(command))
                System.out.println("The command failed");
        }
        else if (args.length == 2){
            if (args[0].toLowerCase().equals("help")){
                if (system.helpMessages.containsKey(args[1]))
                    System.out.println(system.helpMessages.get(args[1]));
                else
                    System.out.println("Command \"".concat(args[1].concat("\" does not exist")));
            }
            else
            if (!system.RunCommand(command))
                System.out.println("The command failed");
        }
        else
        if (!system.RunCommand(command))
            System.out.println("The command failed");
    }
    //endregion
}