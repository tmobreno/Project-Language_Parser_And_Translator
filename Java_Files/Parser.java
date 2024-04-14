import java.util.regex.Pattern;

public class Parser {
    // Pattern matches statement to any word
    private static Pattern name_value = Pattern.compile("\\w+$");

    // Pattern matches to an integer
    private static Pattern integer_value = Pattern.compile("^\\d+$");

    // Pattern matches statement to true or false
    private static Pattern boolean_value = Pattern.compile("^t$|^f$");

    // Pattern matches to an operator
    private static Pattern operator_value = Pattern.compile("^[+\\-*]$");

    // Pattern matches to single equivalence
    private static Pattern equivalency_value =  Pattern.compile("^=$");

    // Pattern matches to comparison operator
    private static Pattern comparison_value = Pattern.compile("^(<<<|>>>)$|^===$|^>>==$|^<<==$");

    // Pattern matches to print command
    private static Pattern print_value = Pattern.compile("^(prnt('\\s'))$");
    
    // Pattern matches to input command
    private static Pattern input_command = Pattern.compile("^(inpt(\\s))$");
    
    // Parses the input command and checks for type of expression
    // Returns an array of tokens which contains the entire split expression
    // Inserts a header string to indicate what was entered and successfully parsed
    // This is used by the translator to figure out what to do
    public static String[] parseCommand(String command){
        String[] tokens = command.split("\\s+");
        String[] newTokens = new String[tokens.length + 1];

    	if(isIffyCommand(command)){
            newTokens[0] = "*iffy_c";
            System.out.println("Iffy Command");
        }
        else if(isInputCommand(command)){
            newTokens[0] = "*input_c";
            System.out.println("Input Command");
        }
        else if(isPrintFunction(command)){
            newTokens[0] = "*print_f";
            System.out.println("Print Command");
        }
        else if(isFunctionCall(command)){
            newTokens[0] = "*func_c";
            System.out.println("Function Call");
        }
        else if(isComparisonExpression(command)){
            newTokens[0] = "*comp_e";
            System.out.println("Comparison Expression");
        }
        else if(isOperatorExpression(command)){
            newTokens[0] = "*op_e";
            System.out.println("Operator Expression");
        } 
        else if(isVariableAssignment(command)){
            newTokens[0] = "*var_a";
            System.out.println("Variable Assignment");
        } 
        else{
            System.out.println("Ignored");
            return null;
        }

        System.arraycopy(tokens, 0, newTokens, 1, tokens.length);
        return newTokens;
    }
    
    // Checks for input command
    private static boolean isInputCommand(String command) {
    	return false;
    }
    
    // Checks for print command
    private static boolean isPrintFunction(String command) {
        String[] tokens = command.split("\\s+");
        String prnt = tokens[0];
        
        return (isPrintWord(prnt) && isFunctionCall(command));
    }

    // Checks for the word "print"
    private static boolean isPrintWord(String command){
        return command.equals("prnt");
    }

    // Checks for a function call
    private static boolean isFunctionCall(String command){
        String[] tokens = command.split("\\s+");
        if(tokens.length < 2){
        	return false;
        }
        String name = tokens[0];
        String at = tokens[1];

        return(isAtSymbol(at));
    }

    // Checks for the "@" symbol
    private static boolean isAtSymbol(String command){
        return command.equals("@");
    }

    // Checks for an Iffy Command
    private static boolean isIffyCommand(String command){
        String[] tokens = command.split("\\s+");
        if(tokens.length < 5){
            return false;
        }
        String iffy = tokens[0];
        String compExpr = tokens[1] + " " + tokens[2] + " " + tokens[3];
        String then = tokens[4];
        return(isIffyWord(iffy) && isComparisonExpression(compExpr) && isThenWord(then));
    }

    // Checks for the word "iffy"
    private static boolean isIffyWord(String command){
        return command.equals("iffy");
    }

    // Checks for the word "then"
    private static boolean isThenWord(String command){
        return command.equals("then");
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

        return (isVariable(var1) && isOperator(operator) && isVarOrInt(var2));
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

        return (isVariable(var1) && isComparisonOperator(operator) && isVarOrIntOrBool(var2));
    }

    // Checks for variable assignments
    private static boolean isVariableAssignment(String command){
        String[] tokens = command.split("\\s+");
        if(tokens.length != 3){
            return false;
        }
        String var1 = tokens[0];
        String operator = tokens[1];
        String var2 = tokens[2];
        return (isVariable(var1) && isEquivalencyOperator(operator) && isVarOrIntOrBool(var2));
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
    private static boolean isVarOrInt(String str){
        return (isVariable(str) || isInteger(str));
    }

    // if name or integer or boolean
    private static boolean isVarOrIntOrBool(String str){
        return (isVariable(str) || isInteger(str) || isBoolean(str));
    }

    // if any named variable
    private static boolean isVariable(String str){
        return name_value.matcher(str).matches();
    }

    // if any integer
    public static boolean isInteger(String str){
        return integer_value.matcher(str).matches();
    }

    // if "t" or "f"
    public static boolean isBoolean(String str){
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

    // if =
    private static boolean isEquivalencyOperator(String str){
        return equivalency_value.matcher(str).matches();
    }
}
    
