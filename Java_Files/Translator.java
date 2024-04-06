import java.util.regex.Pattern;
import java.util.Scanner;
import Parser.java;
import java.util.ArrayList;
import java.util.List;

public class Translator {

    // Parser Instance
    private Parser parser = new Parser();

    // Contains every parsed command
    private List<String[]> parsedCommandsList = new ArrayList<>();

    // Stores Variable Names along with their value
    private List<Tuple<String, Object>> tupleList = new ArrayList<>();

    // Not needed for program, just for testing
    public static void main (String[] args){
        // Initializes a scanner to read user input
        Scanner in = new Scanner(System.in);
        System.out.println("Enter Input: ");
        String command = in.nextLine();

        // While the user has not typed exit, ask for next line
        // and parse the line, adding it to the list of commands
        while(!command.equals("exit")){
            parsedCommandsList.add(parser.parseCommand(command));
            System.out.println("Enter Input: ");
            command = in.nextLine();
        }
        
        in.close();
    }

    // Runs all commands that were parsed, in the order they were parsed
    public static void runAllParsedCommands(){
        for (String[] str : parsedCommandsList) {
            if(str[0].equals("iffy")){

            } 
            else if (str[0].equals("*input_c")){

            } 
            else if (str[0].equals("var_a")){
                newVariableAssignment(str);
            }
            // Need to add the rest
        }
    }

    // Assigns a new variable within the tuple list
    private static void newVariableAssignment(String command) {
    	String[] variables = command.split(" = ");
    	String var = variables[0];
        Object value = null;
        if(checkTupleList(var, variables[1])){
            return;
        } else{
            Tuple<String, Integer> t = new Tuple<>(var, checkValue(variables[1]));
            tupleList.add()
        }
    }

    // Checks the tuple list to find if the variable already exists, then updates it
    private static boolean checkTupleList(String searchString, String newVal){
        for (Tuple<String, Object> tuple : tupleList) {
            if (tuple.first.equals(searchString)) {
                tuple.second = checkValue(newVal);
                return true;
            }
        }
        return false;
    }

    // Determines if the value is a string, int, or boolean
    private static Object checkValue(String s){
        if (parser.isInteger(s)) {
            return Integer.parseInt(s);
        } else if (parser.isBoolean(s)) {
            return Boolean.parseBoolean(s);
        } else {
            return s;
        }
    }
}

// Used for storing variables
public class Tuple<X, Y> {
    public final X first;
    public final Y second;

    public Tuple(X first, Y second) {
        this.first = first;
        this.second = second;
    }
}