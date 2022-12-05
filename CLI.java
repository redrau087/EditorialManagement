import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A simple CLI to run the EditorialACM class
 */
public final class CLI {
    //region Variables
    private final Scanner scanner = new Scanner(System.in);
    //the command prompt scanner
    private final EditorialACM system;
    //the editorial management ACM
    private boolean loop;
    //endregion
    //region Constructor
    /**
     * The default CLI constructor
     */
    public CLI(){
        system = new EditorialACM();
        loop = true;
    }
    //endregion
    //region Running CLI
    /**
     * Runs a single command
     * @param command The command to be executed
     */
    private void RunCommand(String command){
        String[] args = command.split(" ");

        if (args.length == 1){
            //the CLI loop condition
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
                else
                    System.out.println("The command was run successfully");
            }
        }
        else if (args.length == 2){
            if (args[0].equalsIgnoreCase("help")){
                if (system.helpMessages.containsKey(args[1]))
                    System.out.println(system.helpMessages.get(args[1]));
                else
                    System.out.println("Command \"".concat(args[1].concat("\" does not exist")));
            }
            else{
                if (!system.RunCommand(command))
                    System.out.println("The command failed");
                else
                    System.out.println("The command was run successfully");
            }
        }
        else{
            if (!system.RunCommand(command))
                System.out.println("The command failed");
            else
                System.out.println("The command was run successfully");
        }
    }
    /**
     * The CLI Loop
     * @param escape The String to enter to close the CLI
     */
    public void RunCLI(String escape){
        String command;
        loop = true;
        while (loop) {
            System.out.println("Input a command. Type help for help or ".concat(escape).concat(" to exit"));
            command = scanner.nextLine();
            if (command.equalsIgnoreCase(escape))
                loop = false;
            else
                RunCommand(command);
        }
    }
    /**
     * Runs a set of commands listed in a file
     * @param fileName The file containing commands
     */
    public void RunFile(String fileName){
        ArrayList<String> fileLines = new ArrayList<String>();
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                fileLines.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String command : fileLines){
            System.out.println("Running command \"".concat(command).concat("\""));
            RunCommand(command);
        }
    }
    //endregion
}