import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

public class Function {
    private ArrayList<TermType> NEG_PRETERMS = new ArrayList<TermType>(Arrays.asList(TermType.LEFT_PAREN, TermType.RIGHT_PAREN, TermType.OPERATOR));
    private String ifFunctionStr = null;
    private ArrayList<Term> pfTermList = new ArrayList<Term>();
    private Node funcNode;

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
                if (precedence(term.getTerm().charAt(0)) <= precedence(stk.peek().getTerm().charAt(0))) {
                    while (stk.peek().getTermType() != TermType.EOF && precedence(term.getTerm().charAt(0)) <= precedence(stk.peek().getTerm().charAt(0))) {
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
        for (CharacterIterator it = new StringCharacterIterator(ifFunctionStr); it.current() != CharacterIterator.DONE; it.next()) {
            if(Character.isDigit(it.current())){
                charStk += it.current();
            }
            else if(it.current()=='-'&&(ifTermList.isEmpty()||(NEG_PRETERMS.contains(ifTermList.get(ifTermList.size()-1).getTermType())))&&charStk.isEmpty()){
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
                    charStk = "";
                }
                Term newTerm = new Term(Character.toString(it.current()));
                ifTermList.add(newTerm);
            }
            else if(it.current()==')'){
                if(!charStk.isEmpty()){
                    Term newTerm = new Term(charStk);
                    ifTermList.add(newTerm);
                    charStk = "";
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

    private ArrayList<Term> nodeToPfTermList(Node funcNode){
        ArrayList<Term> pfTermList = new ArrayList<Term>();
        //Right/left operand
        Stack<Node> nodeStkB = (Stack<Node>) funcNode.nodeStk.clone();
        if(nodeStkB.isEmpty()){
            pfTermList.add(funcNode.operand);
        }
        while (!nodeStkB.isEmpty()){
            if(nodeStkB.peek().operand!=null){
                pfTermList.add(nodeStkB.peek().operand);
            }
            else{
                pfTermList.addAll(nodeToPfTermList(nodeStkB.peek()));
            }
            nodeStkB.pop();
        }
        if(!nodeStkB.isEmpty()){
            pfTermList.add(funcNode.operator);
        }
        return pfTermList;
    }

    private Function simplify(Function func){
        Node simpleNode = func.getFuncNode().simplify();
        return new Function(simpleNode);
    }

    public double evaluate(double value){
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
                Double operation = switch (term.getOperatorType()) {
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

    public Function derivative(){
        //Simplification
        Node nodePrime = this.funcNode.simplify().derivative();
        //Start with outermost operand, determine rule
        //Follow rules for nodes based on rule
        //Perform clean up
        Function funcPrime = new Function(nodePrime);
        funcPrime = funcPrime.simplify(funcPrime);
        return funcPrime;
    }

    public String pfListToString(){
        StringBuilder str = new StringBuilder();
        for(Term term: this.pfTermList){
            str.append(term.getTerm());
            str.append(" ");
        }
        return str.toString();
    }

    @Override
    public String toString() {
        return "Function{" +
                "NEG_PRETERMS=" + NEG_PRETERMS +
                ", ifFunctionStr='" + ifFunctionStr + '\'' +
                ", pfTermList=" + pfTermList +
                ", funcNode=" + funcNode +
                '}';
    }

    public ArrayList<TermType> getNEG_PRETERMS() {
        return NEG_PRETERMS;
    }

    public String getIfFunctionStr() {
        return ifFunctionStr;
    }

    public ArrayList<Term> getPfTermList() {
        return pfTermList;
    }

    public Node getFuncNode() {
        return funcNode;
    }

    Function(String ifFunctionStr){
        this.ifFunctionStr = ifFunctionStr;
        //Parse ifFunctionStr into terms
        ArrayList<Term> ifTermsList = parseIfFunctionStr(ifFunctionStr);
        //Translate ifTermsList to pfTermsList
        //Assign to function term list
        this.pfTermList = inFixToPostFix(ifTermsList);
        //Create function node
        this.funcNode = new Node(this.pfTermList);
    }

    Function(Node funcNode){
        this.funcNode = funcNode;
        this.pfTermList = nodeToPfTermList(funcNode);
    }

    Function(ArrayList<Term> pfTermList){
        this.pfTermList = pfTermList;
        this.funcNode = new Node(this.pfTermList);
    }

    Function(){}

    public static void main(String[] args) {
        try {
            //TODO: add 3x as an alternate to 3*x, add trig functions, add log and ln function, add constants pi and e, absolute value
            Function func = new Function("(3+x)*x");
            System.out.println(func.pfListToString());
            System.out.println(func.evaluate(2));
            Function funcPrime = func.derivative();
            System.out.println(funcPrime.pfListToString());
            System.out.println(funcPrime.evaluate(2));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
