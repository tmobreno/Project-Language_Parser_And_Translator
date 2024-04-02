import java.util.regex.Pattern;
import java.util.Scanner;

public class Parser {
    // Pattern matches statement to <variable> = <value> (not used)
    private static Pattern variable_asgn_pattern = Pattern.compile("^(.+) = (.+)\\;$");

    // Pattern matches statement to any word
    private static Pattern name_value = Pattern.compile("\\w+$");

    // Pattern matches to an integer
    private static Pattern integer_value = Pattern.compile("^\\d+$");

    // Pattern matches statement to true or false
    private static Pattern boolean_value = Pattern.compile("^t$|^f$");

    // Pattern matches to an operator
    private static Pattern operator_value = Pattern.compile("^[+\\-*]$");

    // Pattern matches to comparison operator
    private static Pattern comparison_value = Pattern.compile("^(<<<|>>>)$|^===$|^>>==$|^<<==$");

    // Pattern matches to print command
    private static Pattern print_value = Pattern.compile("^(prnt('\\s'))$");
    
    // Pattern matches to input command
    private static Pattern input_command = Pattern.compile("^(inpt(\\s))$");
    
    public static void main (String[] args){
        // Initializes a scanner to read user input
        Scanner in = new Scanner(System.in);
        System.out.println("Enter Input: ");
        String command = in.nextLine();

        // While the user has not typed exit, ask for next line
        while(!command.equals("exit")){
            parseCommand(command);
            System.out.println("Enter Input: ");
            command = in.nextLine();
        }
        
        in.close();
    }
    
    // Checks for variable assignments
    private static boolean varAssignment(String command) {
    	String[] variables = command.split(" = ");
    	String var = variables[0];
    	if (isInteger(variables[1])) {
    		int value = Integer.parseInt(variables[1]);
    	} else if (isBoolean(variables[1])) {
    		Boolean value = Boolean.parseBoolean(variables[1]);
    	} else {
    		String value = variables[1];
    	}
    	return variable_asgn_pattern.matcher(command).matches();
    }
    
    // Parses the input command and checks for type of expression
    private static void parseCommand(String command){
    	
    }
    
    // Checks for input command
    private static boolean isInputCommand(String command) {
    	
    }
    
    // Checks for print command
    private static boolean isPrintCommand(String command) {
    	
    }

    // Checks for operator expression pattern match (+ - *)
    private static boolean isOperatorExpression(String command){
        String[] tokens = command.split("\\s+");
        if(tokens.length != 3){
            return false;
        }
        String var1 = tokens[0];
        String operator = tokens[1];
        String var2 = tokens[2];

        return (isVariable(var1) && isOperator(operator) && isVariable(var2));
    }

    // Checks for comparison expression pattern match (< > ==)
    private static boolean isComparisonExpression(String command){
        String[] tokens = command.split("\\s+");
        if(tokens.length != 3){
            return false;
        }
        String var1 = tokens[0];
        String operator = tokens[1];
        String var2 = tokens[2];

        return (isVariable(var1) && isComparisonOperator(operator) && isVariable(var2));
    }

    // Maps "t" or "f" to 1 or 0
    private static int mapBooleanToInt(String str){
        if(isBoolean(str)){
            if(str.equals("t")){
                return 1;
            } else if (str.equals("f")){
                return 0;
            }
        }
        else {
            throw new IllegalArgumentException("Invalid boolean value");
        }
        // Error code if it is neither the options above
        return 404;
    }

    // if name or integer
    private static boolean isVariable(String str){
        return (isName(str) || isInteger(str));
    }

    // if any named variable
    private static boolean isName(String str){
        return name_value.matcher(str).matches();
    }

    // if any integer
    private static boolean isInteger(String str){
        return integer_value.matcher(str).matches();
    }

    // if "t" or "f"
    private static boolean isBoolean(String str){
        return boolean_value.matcher(str).matches();
    }

    // if +, -, or *
    private static boolean isOperator(String str){
        return operator_value.matcher(str).matches();
    }

    // if <<<, >>>, ===, >>==, or <<==
    private static boolean isComparisonOperator(String str){
        return comparison_value.matcher(str).matches();
    }
   
}
    
