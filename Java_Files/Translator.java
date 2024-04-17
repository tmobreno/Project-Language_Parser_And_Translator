import java.util.Scanner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Translator {

    // Parser Instance
    private static Parser parser = new Parser();

    // Contains every parsed command
    private static List<String[]> parsedCommandsList = new ArrayList<>();

    // Stores Variable Names along with their value
    private static List<Tuple<String, Object>> tupleList = new ArrayList<>();

    // Not needed for program, just for testing
    public static void main (String[] args){
        if (args.length != 1) {
            System.out.println("Usage: java Main <inputFile>");
            return;
        }
        
        String inputFileName = args[0];
        
        translateAndExecuteProgram(inputFileName);
        
    }
    
    private static void translateAndExecuteProgram(String inputFileName) {
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
            	if(Parser.isFunctionCreation(line)) {
                    StringBuilder resultBuilder = new StringBuilder();
                    resultBuilder.append(line.trim()).append(" ");
                    while ((line = reader.readLine()) != null) {
                        if (line.trim().equals("end")) {
                            resultBuilder.append(line.trim()).append(";");
                            break;
                        }                
                        resultBuilder.append(line.trim()).append(";");
                    }
                    String input = resultBuilder.toString().trim();

                	String[] parsing = Parser.parseFunction(input);

            	}
            	else {
                    // Translate each line and write to the output file
                	String[] parsing = Parser.parseCommand(line);

                    if(parsing != null) {
        				parsedCommandsList.add(parsing);
                	}
            	}

            }
            runAllParsedCommands();

        } catch (IOException e) {
            e.printStackTrace();
        }
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
            else if (head.equals("*comp_e")) {
                boolean result = newComparisonCommand(command);
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
            else if (head.equals("*b_var_op_a")){
                String[] expr = Arrays.copyOfRange(command, 2, str.length);
                boolean res;

                if (expr[0].equals("~")) { 
                    res = newBoolNotCommand(expr);
                }
                else if (!(expr[1].equals("&")) && !(expr[1].equals("/"))) {
                    res = newComparisonCommand(expr);
                }
                else{                         // bool operator expression!
                    res = newBoolOperatorCommand(expr);
                }
                command[2] = Boolean.toString(res);
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
		
		if(str[1].equals("/")) {
			return first / second;
		}
		
		if(str[1].equals("%")) {
			return first % second;
		}
		
		return 0;
    }

    // Performs boolean opreation evaluation (or(/) + and(&)).
    private static Boolean newBoolOperatorCommand(String[] cmd){
        boolean b_var1;
        boolean b_var2;

        // change 1st boolean var to a boolean obj...
        if (isBool(cmd[0])) {
            b_var1 = tf_to_fullBool(cmd[0]); 
        } else {
            b_var1 = getObjectAsBool(cmd[0]);
        }
        // change 2nd boolean var to a boolean obj...
        if (isBool(cmd[2])){
            b_var2 = tf_to_fullBool(cmd[2]);
        } else {
            b_var2 = getObjectAsBool(cmd[2]);
        }
        // now evaluate, given the two boolean objects
        if (cmd[1].equals("&")){   // AND
            return (b_var1 && b_var2);
        }
        if (cmd[1].equals("/")){   // OR
            return (b_var1 || b_var2);
        }
        return null;  // shouldn't be possible to reach
    }

    // Performs 'not' command (bools). 
    private static boolean newBoolNotCommand(String[] cmd){
        boolean start_bool;

        if (isBool(cmd[1])) {
            start_bool = tf_to_fullBool(cmd[1]);
        }
        else{
            start_bool = getObjectAsBool(cmd[1]);
        }
        return (!start_bool);
    }

    // Performs a comparison (on ints)
    private static Boolean newComparisonCommand(String cmd[]){
        int first;
        int second;

        if(isInteger(cmd[0])) {
    		first = Integer.parseInt(cmd[0]);
    	} else {
    		first = getObjectAsInteger(cmd[0]);
    	}
    	
    	if(isInteger(cmd[2])) {
    		second = Integer.parseInt(cmd[2]);
    	} else {
    		second = getObjectAsInteger(cmd[2]);
    	}

        if (cmd[1].equals("<<<")){
            return (first < second);
        } 
        if (cmd[1].equals("<<==")){
            return (first <= second);
        }
        if (cmd[1].equals(">>>")){
            return (first > second);
        }
        if (cmd[1].equals(">>==")){
            return (first >= second);
        }
        if (cmd[1].equals("===")){
            return (first == second);
        }
        return null;
    }

    // converts 't' and 'f' to actual boolean objects
    private static Boolean tf_to_fullBool(String str) {
        if (str.equals("t")){
            return true;
        }
        if (str.equals("f")) {
            return false;
        }
        return null;
    }
    
    public static boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isBool(String str) {
        if (str.length() != 1){
            return false;
        }
        Boolean asBool = tf_to_fullBool(str);
        if (asBool == null){    // couldn't parse it into a bool
            return false;
        }
        return true;
    }

    // Accesses & returns the integer value associated with the given key
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

    // Accesses & returns the boolean value associated with the given key
    public static Boolean getObjectAsBool(String searchString) {
        for (Tuple<String, Object> tuple : tupleList) {
            if (tuple.first.equals(searchString)) {
                return Boolean.parseBoolean(tuple.second.toString());
            }
        }
        return null;
    }

    // Accesses & returns the object associated with the given key
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
            return tf_to_fullBool(s);
        } else {
            return s;
        }
    }
} 