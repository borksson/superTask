import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.Stack;
import java.text.*;

public class Function {
    String ifFunctionStr;
    String pfFunctionStr;
    ArrayList<String> varList = new ArrayList<String>();
    //Post fix notation
    ArrayList<Term> termList = new ArrayList<Term>();

    private int precedence(Character ch){
        if(ch == '+' || ch == '-'){
            return 1;
        }
        else if(ch == '*' || ch == '/'){
            return 2;
        }
        else if(ch == '^'){
            return 3;
        }
        else {
            return 0;
        }
    }

    private String postFixToInFix(String postfix) {
        StringBuilder inFix = new StringBuilder();
        Stack<Character> stk = new Stack<Character>();
        for(CharacterIterator it = new StringCharacterIterator(postfix); it.current() != CharacterIterator.DONE; it.next()) {
            if(Character.isLetterOrDigit(it.current())){
                stk.push(it.current());
            }
            else{
                inFix.append("(").append(stk.peek()).append(it.current());
                stk.pop();
                inFix.append(stk.peek()).append(")");
                stk.pop();
            }
        }
        return inFix.toString();
    }

    private String infixToPostFix(String infix) {
        Stack<Character> stk = new Stack<Character>();
        stk.push('#');
        StringBuilder postfix = new StringBuilder();

        for (CharacterIterator it = new StringCharacterIterator(infix); it.current() != CharacterIterator.DONE; it.next()) {
            if (Character.isLetterOrDigit(it.current())) {
                //TODO: Insert letters into var list
                postfix.append(it.current());
            }
            //TODO: Add decimal numbers
            else if (it.current() == '(') {
                stk.push(it.current());
            } else if (it.current() == '^') {
                stk.push(it.current());
            } else if (it.current() == ')') {
                while (stk.peek() != '#' && stk.peek() != '(') {
                    postfix.append(stk.peek());
                    stk.pop();
                }
                stk.pop();
            } else {
                if (precedence(it.current()) <= precedence(stk.peek())) {
                    while (stk.peek() != '#' && precedence(it.current()) <= precedence(stk.peek())) {
                        postfix.append(stk.peek());
                        stk.pop();
                    }
                }
                stk.push(it.current());
            }
        }
        while (stk.peek() != '#') {
            postfix.append(stk.peek());
            stk.pop();
        }

        return postfix.toString();
    }

    private String powerRule(String term){
        Double valA;
        Double valPow;
        Character var;
        if(Character.isLetter(term.charAt(0))){
            //x2^
            valA = 1.0;
            valPow = Double.parseDouble(Character.toString(term.charAt(1)));
            var = term.charAt(0);
        } else{
            //1x2^*
            valA = Double.parseDouble(Character.toString(term.charAt(0)));
            valPow = Double.parseDouble(Character.toString(term.charAt(2)));
            var = term.charAt(1);
        }
        valA = valPow*valA;
        valPow--;
        //TODO: Change to postfix
        return (valA+"")+Character.toString(var)+(valPow+"")+"^*";
    }

    public String toString(){
        String exportStr = "In-fix: ";
        exportStr += this.ifFunctionStr;
        exportStr += "\nPost-fix: ";
        exportStr += this.pfFunctionStr;
        return exportStr;
    }

    public double evaluate(double value){
        //TODO: evaluation based on var
        double result = 0;
        Stack<Double> stk = new Stack<Double>();
        for(CharacterIterator it = new StringCharacterIterator(this.pfFunctionStr); it.current() != CharacterIterator.DONE; it.next()){
            if(it.current() == 'x'){
                stk.push(value);
            }
            else if(Character.isDigit(it.current())){
                stk.push(Double.parseDouble(Character.toString(it.current())));
            }
            else{
                Double operandA = stk.peek();
                stk.pop();
                Double operandB = stk.peek();
                stk.pop();
                Double operation = switch (it.current()) {
                    case '+' -> operandB + operandA;
                    case '-' -> operandB - operandA;
                    case '*' -> operandB * operandA;
                    case '/' -> operandB / operandA;
                    case '^' -> Math.pow(operandB, operandA);
                    default -> (double) 0;
                };
                System.out.printf("%f %c %f\n", operandB, it.current(), operandA);
                stk.push(operation);
            }
        }
        return stk.peek();
    }

    public Function derivative(){
        String pfDerivative = "";
        try {
            pfDerivative = powerRule(this.pfFunctionStr);
            return new Function("", pfDerivative);
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    };

    public Function(String ifFunctionStr, String pfFunctionStr) throws Exception{
        if(ifFunctionStr!=null){
            this.ifFunctionStr = ifFunctionStr;
            this.pfFunctionStr = infixToPostFix(this.ifFunctionStr);
        }
        else if(pfFunctionStr!=null){
            this.pfFunctionStr = pfFunctionStr;
            this.ifFunctionStr = postFixToInFix(this.pfFunctionStr);
        }
        else {
            throw new Exception("No function string provided.");
        }
    }

    public Function(String ifFunctionStr){
        //Parse ifFunctionStr into terms
        //Translate ifTermsList to pfTermsList
        //Assign to function term list
    }

    public static void main(String[] args) {
        try {
            Function func = new Function("4+4-2/3*x^2", null);
            System.out.println(func.toString());
            //Function funcPrime = func.derivative();
            //System.out.println(funcPrime.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
