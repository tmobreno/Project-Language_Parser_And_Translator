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
    
    private static int stored_loop_num = 1;
    private static int run_loop_num = 1;
    private static String loop_statement_name = "Function_Loop_Name_" + stored_loop_num;

    // Contains every parsed command
    private static List<String[]> parsedCommandsList = new ArrayList<>();
    
    private static List<String[]> functionsList = new ArrayList<>();

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
                if (Parser.isLoopStatement(line)) {
                	String[] parsing = Parser.parseCommand(line);

                    if(parsing != null) {
        				parsedCommandsList.add(parsing);
                	}
                                       
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

                	String[] func_parsing = Parser.parseLoop(loop_statement_name, input);
                	
                	if(func_parsing != null) {
                		functionsList.add(func_parsing);
                	}
                	
                	stored_loop_num += 1;
                	loop_statement_name = "Function_Loop_Name_" + stored_loop_num;
                }
                else if(Parser.isFunctionCreation(line)) {
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
                	
                	if(parsing != null) {
                		functionsList.add(parsing);
                	}
            	}
                else if (Parser.isIffyCommand(line)) {
                    StringBuilder allLines = new StringBuilder();
                    allLines.append(line.trim()).append(";;;");

                    while ((line = reader.readLine()) != null) {
                        if (line.trim().equals("end")) {
                            break; 
                        } 
                        allLines.append(line.trim()).append(";;;");
                    }

                    String allLines_str = allLines.toString().trim();
                    String[] parsing = Parser.parseIffy(allLines_str);

                    if (parsing != null){
                        parsedCommandsList.add(parsing);
                    }
                } 
            	else {
                    // Translate each line and write to the output file
                	String[] parsing = Parser.parseCommand(line);

                    if(parsing != null) {
        				parsedCommandsList.add(parsing);
                	}
            	}

            }
            runAllParsedCommands(parsedCommandsList);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Runs all commands that were parsed, in the order they were parsed
    public static void runAllParsedCommands(List<String[]> commandsList){
        for (String[] str : commandsList) {
            runCommand(str);
        }
    }

    private static void runCommand(String[] str){
        String head = str[0];
        // Create an array without the indicator head
        // Run functions based on this since it contains the actual full parsed command
        String[] command = Arrays.copyOfRange(str, 1, str.length);

        if(head.equals("*func_c")) {
        	performFunction(command);
        }
        if(head.equals("*iffy_c")){
            performIffy(command);
        } 
        else if (head.equals("*loop_s")){
        	runLoop(command);
        	run_loop_num += 1;
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

            boolean res = doBoolCommand(expr);
            
            command[2] = Boolean.toString(res);
            newVariableAssignment(command);
        }
    }

    // Executes a full iffy statement
    private static void performIffy(String allLines[]){
        // allLines is a str list of each UNPARSED line
        if (allLines.length < 2){ 
            return;
        }

        String startIffyLine = allLines[0];
        String[] bodyToEnd = Arrays.copyOfRange(allLines, 1, allLines.length);
        // bodyToEnd is all the lines after the first initial "iffy" line.

        // FIND BOOL EXPR:
        String bool_expr = getBoolExprFromIffy(startIffyLine);
        boolean eval = doBoolCommand(bool_expr.split("\\s+"));
        // INITIALIZE:
        boolean ignore = !eval;
        boolean hasHadTrue = eval;
        // ITERATE THROUGH "bodyToEnd" AND RUN COMMANDS:
        for (String line : bodyToEnd) {
            if (Parser.isElseIffyCommand(line.trim())){
                if (hasHadTrue){
                    ignore = true;
                } else{
                    bool_expr = getBoolExprFromIffy(line);
                    eval = doBoolCommand(bool_expr.split("\\s+"));
                    if (eval){
                        ignore = false;
                        hasHadTrue = true;
                    } else {
                        ignore = true;
                    }
                }
            } else if (Parser.isElseCommand(line)){
                    ignore = hasHadTrue;
            } else{
                if (!ignore){
                    String[] parsing = Parser.parseCommand(line);
                    if (parsing != null){
                        runCommand(parsing);
                    }
                }
            }
        }
    }
    
    private static void performFunction(String[] str) {
    	String funcName = str[0];
    	
    	for (String[] function : functionsList) {
    	    String firstWord = function[0].trim().split("\\s+")[0].replaceAll(";", "");
    	    if (firstWord.equals(funcName)) {
    	    	String[] parametersPart = function[0].split(";");
    	    	String[] parametersArray = parametersPart[1].split(",");
    	    	for (int i = 0; i < parametersArray.length; i++) {
    	    	    parametersArray[i] = parametersArray[i].trim();
    	    	}
    	    	for (int i = 0; i < parametersArray.length; i++) {
    	    		String[] command = new String[3];
    	    		command[0] = parametersArray[i];
    	    		command[1] = null;
    	    		command[2] = str[2+i].replaceAll(",", "");
    	    		newVariableAssignment(command);
    	    	}
    	    	
    	    	String functionBody = function[1];
    	    	String[] lines = functionBody.split(";");
	    	    List<String[]> tempCommandsList = new ArrayList<>();

    	    	for (String line : lines) {
    	    	    line = line.trim();
                	String[] parsing = Parser.parseCommand(line);
                    if(parsing != null) {
                    	tempCommandsList.add(parsing);
                	}
    	    	}
	    	    runAllParsedCommands(tempCommandsList);
    	        break;
    	    }
    	}
    }
    
    private static void runLoop(String[] str) {
    	String funcName = "Function_Loop_Name_" + run_loop_num;
    	for (String[] function : functionsList) {
    		String firstWord = function[0].trim().split("\\s+")[0].replaceAll(";", "");
    	    if (firstWord.equals(funcName)) {
    	    	String functionBody = function[1];
    	    	String[] lines = functionBody.split(";");
	    	    List<String[]> tempCommandsList = new ArrayList<>();

    	    	for (String line : lines) {
    	    	    line = line.trim();
    	    	    if(line.equals("loop")) {
    	    	    	break;
    	    	    }
                	String[] parsing = Parser.parseCommand(line);
                    if(parsing != null) {
                    	tempCommandsList.add(parsing);
                	}
    	    	}
	    	    runAllParsedCommands(tempCommandsList);
	    	    if(checkLoop(lines[lines.length-1].trim())) {
	    	    	runLoop(str);
	    	    }
    	    }
    	}
    }
    
    private static boolean checkLoop(String s) {
    	String[] str = Parser.parseCommand(s);
        String[] command = Arrays.copyOfRange(str, 1, str.length);

    	return newComparisonCommand(command);
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
        if (cmd[1].equals("//")){   // OR
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
    		if(str[2].charAt(0) == '"') {
                StringBuilder resultBuilder = new StringBuilder();
    			for(int i = 2; i < str.length; i++) {
    				String toAdd = str[i].replace("\"", "");
    				if (i+1 != str.length) {
        				resultBuilder.append(toAdd + " ");
    				} else {
        				resultBuilder.append(toAdd);
    				}
    			}
    			System.out.print(resultBuilder.toString());
    			return;
    		}
    		Object found = getObjectFromTuples(str[2]);
    		if(found.toString().charAt(0) == '"') {
    			found = found.toString().substring(1, found.toString().length() - 1);
    		}

    		System.out.print(found);
    	}
    }
    
    // prntln @ will just print a new line
    public static void printLineFunction(String [] str) {
    	if(str.length == 2) {
    		System.out.println("");
    	}
    	else {
    		if(str[2].charAt(0) == '"') {
                StringBuilder resultBuilder = new StringBuilder();
    			for(int i = 2; i < str.length; i++) {
    				String toAdd = str[i].replace("\"", "");
    				resultBuilder.append(toAdd + " ");
    			}
    			System.out.println(resultBuilder.toString());
    			return;
    		}
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

    // calls correct boolean function and returns evaluated return val
    private static boolean doBoolCommand(String[] command){
        boolean res;

        if (command.length == 1){
            // then it's either t/f or a var
            if (command[0].equals("t") || command[0].equals("f")){
                res = tf_to_fullBool(command[0]);
            }
            else{
                // it's a var you need to go access. 
                res = getObjectAsBool(command[0]);
            }
        } else if (command[0].equals("~")) { 
            res = newBoolNotCommand(command);
        } else if (!(command[1].equals("&")) && !(command[1].equals("//"))) {
            res = newComparisonCommand(command);
        } else { 
            res = newBoolOperatorCommand(command);
        }        
        return res;
    }

    // returns the boolean expression from EITHER 'iffy' OR 'else iffy'
    private static String getBoolExprFromIffy(String iffy_command){
        String[] command = iffy_command.split("\\s+");
        int i;
        if (command[0].equals("iffy")){   // it's only "iffy"
            i = 1;
        } else                        {   // then it's "else iffy"
            i = 2;
        }
        String expr = "";
        while ((i < command.length) && (!command[i].equals("then"))){
            expr += command[i] + " ";
            i += 1;
        }
        return expr;
    }
} 