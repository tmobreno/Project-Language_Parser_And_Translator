import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Translator {

    // Parser Instance
    private static Parser parser = new Parser();

    // Contains every parsed command
    private static List<String[]> parsedCommandsList = new ArrayList<>();

    // Stores Variable Names along with their value
    private static List<Tuple<String, Object>> tupleList = new ArrayList<>();

    // Not needed for program, just for testing
    public static void main (String[] args){
        // Initializes a scanner to read user input
        Scanner in = new Scanner(System.in);
        System.out.println("Enter Input: ");
        String command = in.nextLine();

        // While the user has not typed exit, ask for next line
        // and parse the line, adding it to the list of commands
        while(!command.equals("exit")){
        	String[] parsing = Parser.parseCommand(command);
        	if(parsing != null) {
				parsedCommandsList.add(parsing);
        	}
            
            System.out.println("Enter Input: ");
            command = in.nextLine();
        }
        runAllParsedCommands();
        in.close();
    }

    // Runs all commands that were parsed, in the order they were parsed
    public static void runAllParsedCommands(){
        for (String[] str : parsedCommandsList) {
            String head = str[0];
            // Create an array without the indicator head
            // Run functions based on this since it contains the actual full parsed command
            String[] command = Arrays.copyOfRange(str, 1, str.length);

            if(head.equals("*iffy_c")){

            } 
            else if (head.equals("*input_c")){

            } 
            else if (head.equals("*print_f")){
            	printFunction(command);
            } 
            else if (head.equals("*println_f")){
            	printLineFunction(command);
            } 
            else if(head.equals("*op_e")) {
            	int result = newOperatorCommand(command);
            	System.out.println(result);
            }
            else if (head.equals("*var_a")){
                newVariableAssignment(command);
            }
            else if (head.equals("*s_var_a")){
            	newStringVariableAssignment(command);
            }
            else if (head.equals("*var_op_a")){
                String[] operation = Arrays.copyOfRange(command, 2, str.length);
            	int result = newOperatorCommand(operation);
            	command[2] = Integer.toString(result);
            	newVariableAssignment(command);
            }
            // Need to add the rest
        }
    }

    // Assigns a new variable within the tuple list
    private static void newStringVariableAssignment(String[] str) {
    	String var = str[0];
        String value = "";
        int i = 2;
    	for(; i < str.length - 1; i++) {
        	value += str[i] + " ";
    	}
    	value += str[i];
    	
        Tuple<String, Object> t = new Tuple(var, value);
        tupleList.add(t);
    }
    
    private static void newVariableAssignment(String[] str) {
    	String var = str[0];
        Object value = null;
        if(checkTupleList(var, str[2])){
            return;
        } else{
            Tuple<String, Object> t = new Tuple(var, checkValue(str[2]));
            tupleList.add(t);
        }
    }
    
    // Performs an expression
    private static Integer newOperatorCommand(String [] str) {
    	int first;
    	int second;
    	
    	
    	if(isInteger(str[0])) {
    		first = Integer.parseInt(str[0]);
    	} else {
    		first = getObjectAsInteger(str[0]);
    	}
    	
    	if(isInteger(str[2])) {
    		second = Integer.parseInt(str[2]);
    	} else {
    		second = getObjectAsInteger(str[2]);
    	}
    	
		if(str[1].equals("+")) {
			return first + second;
		}
    	
		if(str[1].equals("-")) {
			return first - second;
		}
    	
		if(str[1].equals("*")) {
			return first * second;
		}
		return 0;
    }
    
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static Integer getObjectAsInteger(String searchString) {
        for (Tuple<String, Object> tuple : tupleList) {
            if (tuple.first.equals(searchString)) {
                try {
                    return Integer.parseInt(tuple.second.toString());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    public static Object getObjectFromTuples(String searchString) {
        for (Tuple<String, Object> tuple : tupleList) {
            if (tuple.first.equals(searchString)) {
                try {
                    return tuple.second.toString();
                } catch (NumberFormatException e) {
                    return null;
                }
            }
        }
        return null;
    }
    
    // prnt @ will just print a new space
    public static void printFunction(String [] str) {
    	if(str.length == 2) {
    		System.out.print(" ");
    	}
    	else {
    		Object found = getObjectFromTuples(str[2]);
    		if(found.toString().charAt(0) == '"') {
    			found = found.toString().substring(1, found.toString().length() - 1);
    		}

    		System.out.print(found);
    	}
    }
    
    // prnt @ will just print a new line
    public static void printLineFunction(String [] str) {
    	if(str.length == 2) {
    		System.out.println("");
    	}
    	else {
        	Object found = getObjectFromTuples(str[2]);
        	if(found.toString().charAt(0) == '"') {
        		found = found.toString().substring(1, found.toString().length() - 1);
        	}
        	System.out.println(found);
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

    // Determines if the value is a string, integer, or boolean
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