import java.util.Arrays;
import java.util.regex.Pattern;

public class Parser {
    // Pattern matches statement to any word
    private static Pattern name_value = Pattern.compile("\\w+$");

    // Pattern matches to an integer
    private static Pattern integer_value = Pattern.compile("^\\d+$");

    // Pattern matches statement to true or false
    private static Pattern boolean_value = Pattern.compile("^t$|^f$");

    // Pattern matches to an operator
    private static Pattern operator_value = Pattern.compile("^[+\\-*/%]$");

    // Pattern matches to single equivalence
    private static Pattern equivalency_value =  Pattern.compile("^=$");

    // Pattern matches to comparison operator
    private static Pattern comparison_value = Pattern.compile("^(<<<|>>>)$|^===$|^>>==$|^<<==$");

    // Pattern matches to comparison operator
    private static Pattern bool_comparison_value = Pattern.compile("^&$|^//$");

    // Pattern matches to print command
    private static Pattern print_value = Pattern.compile("^(prnt('\\s'))$");
    
    // Pattern matches to input command
    private static Pattern input_command = Pattern.compile("^(inpt(\\s))$");

    // Pattern matches to (boolean) not command
    private static Pattern not_value = Pattern.compile("^~$");
    
    // Parses the input command and checks for type of expression
    // Returns an array of tokens which contains the entire split expression
    // Inserts a header string to indicate what was entered and successfully parsed
    // This is used by the translator to figure out what to do
    
    public static String[] parseCommand(String command){    	
        String[] tokens = command.split("\\s+");
        String[] newTokens = new String[tokens.length + 1];

        if(isInputCommand(command)){
            newTokens[0] = "*input_c";
        }
        else if(isLoopStatement(command)){
            newTokens[0] = "*loop_s";
        }
        else if(isPrintFunction(command)){
            newTokens[0] = "*print_f";
        }
        else if(isPrintLineFunction(command)){
            newTokens[0] = "*println_f";
        }
        else if(isFunctionCall(command)){
            newTokens[0] = "*func_c";
        }
        else if(isComparisonExpression(command)){
            newTokens[0] = "*comp_e";
        }
        else if(isOperatorExpression(command)){
            newTokens[0] = "*op_e";
        } 
        else if(isVariableAssignment(command)){
            newTokens[0] = "*var_a";
        } 
        else if(isStringVarAssignment(command)) {
            newTokens[0] = "*s_var_a";
        }
        else if(isVariableOperatorAssignment(command)) {
            newTokens[0] = "*var_op_a";
        }
        else if (isBoolOperatorExpression(command)){
            newTokens[0] = "*b_op_e";
        }
        else if (isNotExpression(command)){
            newTokens[0] = "*not_e";
        }
        else if (isBooleanVariableOperatorAssignment(command)){
            newTokens[0] = "*b_var_op_a";
        }        
        else{
            return null;
        }

        System.arraycopy(tokens, 0, newTokens, 1, tokens.length);

        return newTokens;
    }
    
    public static String[] parseFunction(String function) {
    	int firstSpaceIndex = function.indexOf(" ");
    	String functionName = function.substring(0, firstSpaceIndex).trim();
    	String parameters = function.replaceAll("^.*?\\((.*?)\\).*$", "$1").trim();
    	String functionBody = function.substring(function.indexOf(":") + 1).trim();
    	String firstLine = functionName + "; " + parameters;

    	// Store the two lines in a string array
    	String[] result = {firstLine, functionBody};
        
        System.out.println("Function Creation");
        return result;
    }
    
    public static String[] parseLoop(String first, String loop) {
    	String trimmedInput = loop.replaceFirst("^while\\s*@\\s+", "").replaceAll("\\s*end;$", "loop");
    	String[] parts = trimmedInput.split("\\s*:\\s*", 2);

    	StringBuilder resultBuilder = new StringBuilder();

    	resultBuilder.append(parts[1]).append("; "); 
    	resultBuilder.append(parts[0]).append(";"); 

    	String result = resultBuilder.toString();
    	
    	String[] res = {first, result};

    	return res;
    }

    // "Parses" an ENTIRE iffy-then-else statement (into lines)
    public static String[] parseIffy (String allLines){
        String[] lines = allLines.split(";;;");

        String[] retval = new String[lines.length + 1];
        retval[0] = "*iffy_c";
        System.arraycopy(lines, 0, retval, 1, lines.length);

        return retval;
    }
    
    // Checks for input command
    private static boolean isInputCommand(String command) {
    	return false;
    }
    
    public static boolean isLoopStatement(String command) {
        String[] tokens = command.split("\\s+");
        String prnt = tokens[0];
        return (isLoopWord(prnt) && isFunctionCall(command));
    }
    
    private static boolean isLoopWord(String command){
        return command.equals("while");
    }
    
    
    // Checks for print command
    private static boolean isPrintFunction(String command) {
        String[] tokens = command.split("\\s+");
        String prnt = tokens[0];
        
        return (isPrintWord(prnt) && isFunctionCall(command));
    }
    
    private static boolean isPrintLineFunction(String command) {
        String[] tokens = command.split("\\s+");
        String prnt = tokens[0];
        
        return (isPrintLineWord(prnt) && isFunctionCall(command));
    }

    // Checks for the word "print"
    private static boolean isPrintWord(String command){
        return command.equals("prnt");
    }
    
    private static boolean isPrintLineWord(String command){
        return command.equals("prntln");
    }

    // Checks for a function call
    public static boolean isFunctionCreation(String command){
        String[] tokens = command.split("\\s+");
        if(tokens.length < 2){
        	return false;
        }
        String name = tokens[0];
        String param = tokens[1];
                
        if(param.charAt(0) == '(') {
        	return true;
        }
        return false;
    }
    
    // Checks for a function creation
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
    public static boolean isIffyCommand(String command){
        String[] tokens = command.split("\\s+");
        if(tokens.length < 3){
            return false;
        }
        String iffy = tokens[0];
        int i = 1;
        String bool_e = "";
        while ((i < tokens.length) && !(isThenWord(tokens[i]))){
            bool_e += tokens[i] + " ";
            i += 1;
        }
        return(isIffyWord(iffy) && isBooleanExpression(bool_e));
    }

    // Checks for an 'else iffy' Command
    public static boolean isElseIffyCommand(String command){
        String[] tokens = command.split("\\s+");
        if(tokens.length < 4){
            return false;
        }
        String el = tokens[0];
        String iffy = tokens[1];

        int i = 2;
        String bool_e = "";
        while ((i < tokens.length) && !(isThenWord(tokens[i]))){
            bool_e += tokens[i] + " ";
            i += 1;
        }
        return(isElseWord(el) && isIffyWord(iffy) && isBooleanExpression(bool_e));
    }

    // Checks for an 'else' line/command
    public static boolean isElseCommand(String command){
        String[] tokens = command.split("\\s+");
        if(tokens.length != 1){
            return false;
        }
        return(isElseWord(tokens[0]));
    }

    // Checks for the word "iffy"
    private static boolean isIffyWord(String command){
        return command.equals("iffy");
    }

    // Checks for the word "then"
    private static boolean isThenWord(String command){
        return command.equals("then");
    }

    // Checks for the word "then"
    private static boolean isElseWord(String command){
        return command.equals("else");
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

    // Checks for operator expression pattern match (& /)
    private static boolean isBoolOperatorExpression(String command){
        String[] tokens = command.split("\\s+");
        if (tokens.length != 3){
            return false;
        }
        String bool1 = tokens[0];
        String bool_op = tokens[1];
        String bool2 = tokens[2];

        return (isVarOrIntOrBool(bool1) && isBooleanOperatorExpression(bool_op) && isVarOrIntOrBool(bool2));
        // may need to change the return (specifically checking bool1 and bool2); just want it to work for vars AND bools
    }

    // Checks for a boolean not expression (~)
    private static boolean isNotExpression(String command){
        String[] tokens = command.split("\\s+");

        if (tokens.length != 2){
            return false;
        }
        String not_op = tokens[0];
        String bool_var = tokens[1];

        return (isNotOperator(not_op) && isVarOrIntOrBool(bool_var));
        // may need to change return: isVariable() doesn't check if it's boolean var so could be any type?
    }

    // Assign var based on an operation
    private static boolean isBooleanVariableOperatorAssignment(String command){
        /* 
         * Function checks the var name and '=', then checks the rest
         * using isBooleanExpression().....
         *      - NOTE: the check for just a single bool or single var (from other func)
         *               is NOT relevant here; isVariableAssignment() will cover it first. 
         *      - slices list, rejoins the section of the string command, and checks
         *        if that section (after the '=') evaluates to a boolean by running func
         */
        String[] tokens = command.split("\\s+");
        if(tokens.length < 3){ 
            return false;
        }
        String varname = tokens[0];
        String equals_op = tokens[1];

        String[] expr_after_eq_op = Arrays.copyOfRange(tokens, 2, tokens.length);
        String after_eq_JOINED = String.join(" ", expr_after_eq_op);  // string of everything after the var name and the '='

        return (isVariable(varname) && isEquivalencyOperator(equals_op) && isBooleanExpression(after_eq_JOINED));
        // true if var name is legal, has '=' after, and everything after is a valid boolean expression
    }

    // the given expression results in a boolean. 
    private static boolean isBooleanExpression(String command_trunc){
        /*
         * Returns true if the given section of the command is 
         * something that results in a boolean. 
         * Intended to be used for:
         *      - boolean variable assignment
         *      - iffy statements (iffy <bool_exr> then...)
         * 
         * NOTE: this does NOT mean it's only booleans; 
         *       ALSO RETURNS TRUE WHEN GIVEN A COMPARISON EXPRESSION
         */
        String[] tokens = command_trunc.split("\\s+");

        if (tokens.length < 1){
            System.out.println("PROBLEM! (in isBooleanExpression)");
            return false;
        }
        if (tokens.length == 1){
            // then it's a singular raw bool, or singular bool var, if anything
            // (i.e. "f" or "y", where y is a bool)
            return ( isVariable(tokens[0]) || isBoolean(tokens[0]) );
        }
        if (tokens.length == 2){
            // then it's a NOT expression, if anything (i.e. "~ x" or "~ t")
            return isNotExpression(command_trunc);
        }
        // if none of those, then it's either: a bool operation expr (e.g. "x & y"),
        //      or a comparison operation, of any type (e.g. "4 <<< 7")
        return (isBoolOperatorExpression(command_trunc) || isComparisonExpression(command_trunc));
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
    
    // Assign string with format of " at start of string
    private static boolean isStringVarAssignment(String command){
        String[] tokens = command.split("\\s+");
        String[] tokens2 = command.split("\\s*=\\s*");

        if(tokens.length < 3){
            return false;
        }
        if(tokens2.length < 2){
            return false;
        }
        String var1 = tokens2[0];
        String operator = tokens[1];
        String var2 = tokens2[1];
        
        if(var2.charAt(0) != '"') {
        	return false;
        }
        
        return (isVariable(var1) && isEquivalencyOperator(operator));
    }
    
    // Assign var based on an operation
    private static boolean isVariableOperatorAssignment(String command){
        String[] tokens = command.split("\\s+");
        if(tokens.length < 4){  
            return false;
        }
        String var1 = tokens[0];
        String operator = tokens[1];
        String var2 = tokens[2];
        String operation = tokens[3];
        return (isVariable(var1) && isEquivalencyOperator(operator) && isVarOrIntOrBool(var2) && isOperator(operation));
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

    // if & or / (boolean 'and' or 'or')
    private static boolean isBooleanOperatorExpression(String str){
        return bool_comparison_value.matcher(str).matches();
    }

    // if ~ (boolean not)
    private static boolean isNotOperator (String str){
        return not_value.matcher(str).matches();
    }
    
}
    
