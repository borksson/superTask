import java.rmi.server.ExportException;
import java.text.CharacterIterator;
import java.util.*;
import java.text.*;

public class Function {
    ArrayList<TermType> NEG_PRETERMS = new ArrayList<TermType>(Arrays.asList(TermType.LEFT_PAREN, TermType.RIGHT_PAREN, TermType.OPERATOR));
    String ifFunctionStr = null;
    ArrayList<Term> pfTermList = new ArrayList<Term>();

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

    private ArrayList<Term> inFixToPostFix(ArrayList<Term> ifTermsList){
        ArrayList<Term> pfTermsList = new ArrayList<Term>();
        Stack<Term> stk = new Stack<Term>();
        stk.push(new Term("#"));
        for (Term term : ifTermsList) {
            if (term.getTermType() == TermType.VALUE || term.getTermType() == TermType.VARIABLE) {
                pfTermsList.add(term);
            } else if (term.getTermType() == TermType.LEFT_PAREN) {
                stk.push(term);
            } else if (term.getTermType() == TermType.RIGHT_PAREN) {
                while (stk.peek().getTermType() != TermType.EOF && stk.peek().getTermType() != TermType.LEFT_PAREN) {
                    pfTermsList.add(stk.peek());
                    stk.pop();
                }
                stk.pop();
            } else {
                if (precedence(term.getTermStr().charAt(0)) <= precedence(stk.peek().getTermStr().charAt(0))) {
                    while (stk.peek().getTermType() != TermType.EOF && precedence(term.getTermStr().charAt(0)) <= precedence(stk.peek().getTermStr().charAt(0))) {
                        pfTermsList.add(stk.peek());
                        stk.pop();
                    }
                }
                stk.push(term);
            }
        }
        while(stk.peek().getTermType()!=TermType.EOF){
            pfTermsList.add(stk.peek());
            stk.pop();
        }
        return pfTermsList;
    }

    private ArrayList<Term> parseIfFunctionStr(String ifFunctionStr){
        ArrayList<Term> ifTermList = new ArrayList<Term>();
        String charStk = "";
        //TODO: FIX NEGATIVES
        for (CharacterIterator it = new StringCharacterIterator(ifFunctionStr); it.current() != CharacterIterator.DONE; it.next()) {
            if(Character.isDigit(it.current())){
                charStk += it.current();
            }
            else if(it.current()=='-'&&(ifTermList.isEmpty()||NEG_PRETERMS.contains(ifTermList.get(ifTermList.size()-1).getTermType()))){
                charStk += it.current();
            }
            else if(Character.isLetter(it.current())){
                Term newTerm = new Term(charStk+Character.toString(it.current()));
                ifTermList.add(newTerm);
            }
            else if(it.current()=='.'){
                charStk += it.current();
            }
            else if(it.current()=='('){
                if(!charStk.isEmpty()){
                    Term newTerm = new Term(charStk);
                    ifTermList.add(newTerm);
                }
                Term newTerm = new Term(Character.toString(it.current()));
                ifTermList.add(newTerm);
            }
            else if(it.current()==')'){
                if(!charStk.isEmpty()){
                    Term newTerm = new Term(charStk);
                    ifTermList.add(newTerm);
                }
                Term newTerm = new Term(Character.toString(it.current()));
                ifTermList.add(newTerm);
            }
            else{
                Term newTerm;
                if(!charStk.isEmpty()) {
                    newTerm = new Term(charStk);
                    charStk = "";
                    ifTermList.add(newTerm);
                }
                newTerm = new Term(Character.toString(it.current()));
                ifTermList.add(newTerm);
            }
        }
        if(!charStk.isEmpty()) {
            Term newTerm = new Term(charStk);
            charStk = "";
            ifTermList.add(newTerm);
        }
        return  ifTermList;
    }

    public double evaluate(double value){
        //TODO: evaluation based on var
        double result = 0;
        Stack<Double> stk = new Stack<Double>();
        for(Term term: this.pfTermList){
            if(term.getTermType()==TermType.VARIABLE){
                stk.push(term.getValue()*value);
            }
            else if(term.getTermType()==TermType.VALUE){
                stk.push(term.getValue());
            }
            else{
                Double operandA = stk.peek();
                stk.pop();
                Double operandB = stk.peek();
                stk.pop();
                Double operation = switch (term.getOperandType()) {
                    case ADD -> operandB + operandA;
                    case SUBTRACT -> operandB - operandA;
                    case MULTIPLY -> operandB * operandA;
                    case DIVIDE -> operandB / operandA;
                    case POWER -> Math.pow(operandB, operandA);
                };
                stk.push(operation);
            }
        }
        return stk.peek();
    }

    public void initFuncWifStr(String ifFunctionStr){
        this.ifFunctionStr = ifFunctionStr;
        //Parse ifFunctionStr into terms
        ArrayList<Term> ifTermsList = parseIfFunctionStr(ifFunctionStr);
        //Translate ifTermsList to pfTermsList
        //Assign to function term list
        this.pfTermList = inFixToPostFix(ifTermsList);
    }

    public void initFuncWpfList(ArrayList<Term> pfTermList){
        this.pfTermList = pfTermList;
    }

    public static void main(String[] args) {
        try {
            Function func = new Function();
            func.initFuncWifStr("x^2+2*x+52");
            System.out.println(func.evaluate(4.5));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
