import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A simple CLI to run the EditorialACM class
 */
public class CLI {
    //region Variables
    private final Scanner scanner = new Scanner(System.in);
    //the command prompt scanner
    private final EditorialACM system;
    //the editorial management ACM
    private boolean loop;
    //the CLI loop condition
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
     * The CLI loop
     * @param escape The String to enter to close the CLI
     */
    public void RunCLI(String escape){
        String command;
        loop = true;
        while (loop){
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
                else if (command.equalsIgnoreCase(escape))
                    loop = false;
                else{
                    if (!system.RunCommand(command))
                        System.out.println("The command failed");
                }
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
    }
    public void RunCLI(String escape, ArrayList<String> commands) {
        for (String command : commands){
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
                else if (command.equalsIgnoreCase(escape))
                    loop = false;
                else{
                    if (!system.RunCommand(command))
                        System.out.println("The command failed");
                }
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
    }
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

        fileLines.add("exit");
        RunCLI("exit", fileLines);
    }
    //endregion
}