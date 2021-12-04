import java.util.ArrayList;
import java.util.ListIterator;
import java.util.Stack;

public class Node {
    //Node pair, left operand, right operand
    //[Right operand, Left operand] iterate forward for eval
    //TODO: Change to lnode rnode
    Stack<Node> nodeStk = new Stack<Node>();
    Term operand;
    Term operator;

    //Derivation rules
    private Node constRule(Node node){
        Node nodePrime = new Node();
        nodePrime.operand = new Term("0");
        return nodePrime;
    }
    private Node varRule(Node node){
        Node nodePrime = new Node();
        nodePrime.operand = new Term(node.operand.value + "");
        return nodePrime;
    }
    private Node basicOperatorRule(Node node) {
        Node nodePrime = new Node();
        Stack<Node> nodeStk = (Stack<Node>) node.nodeStk.clone();
        while (!nodeStk.isEmpty()) {
            nodePrime.nodeStk.push(nodeStk.peek().derivative());
            nodeStk.pop();
        }
        nodePrime.operator = node.operator;
        return nodePrime;
    }

    private Node productRule(Node node) {
        Node nodePrime = new Node();
        //[Right operand, Left operand]
        //f(x)g(x)dx/dy = [f'(x)g(x)]A + [f(x)g'(x)]B
        //AB+
        Node nodeA = new Node();
        Node nodeB = new Node();
        nodeA.nodeStk.push(node.nodeStk.get(0)); //g(x)
        nodeA.nodeStk.push(node.nodeStk.get(1).derivative()); //f'(x)
        nodeA.operator = new Term("*");
        nodeB.nodeStk.push(node.nodeStk.get(0).derivative()); //g'(x)
        nodeB.nodeStk.push(node.nodeStk.get(1)); //f(x)
        nodeB.operator = new Term("*");
        nodePrime.nodeStk.push(nodeB); //[f(x)g'(x)]B
        nodePrime.nodeStk.push(nodeA); //[f'(x)g(x)]A
        nodePrime.operator = new Term("+");
        return nodePrime;
    }

    public Node derivative(){
        Node nodePrime = new Node();
        if(this.operator!=null) {
            switch (this.operator.getOperatorType()) {
                case ADD,SUBTRACT -> nodePrime = basicOperatorRule(this);
                case MULTIPLY -> nodePrime = productRule(this);
            }
        }
        else{
            switch (this.operand.getTermType()){
                case VALUE -> nodePrime = constRule(this);
                case VARIABLE -> nodePrime = varRule(this);
            }
        }
        return nodePrime;
    }

    public Node simplify(){
        Node simpleNode = new Node();
        //TODO:Add transitive operations, foil and multiplication
        //Evaluate and find multiply/divide by 1 or 0
        //If node is const
        if(this.nodeStk.isEmpty()){
            return this;
        }
        Node lNode = nodeStk.get(1).simplify();
        Node rNode = nodeStk.get(0).simplify();
        //3+4 = 7
        if(lNode.nodeStk.isEmpty()&&rNode.nodeStk.isEmpty()&&(lNode.operand.getTermType()==TermType.VALUE&&rNode.operand.getTermType()==TermType.VALUE)){
            Double operandR = rNode.operand.getValue();
            Double operandL = lNode.operand.getValue();
            Double operation = switch (this.operator.getOperatorType()) {
                case ADD -> operandL + operandR;
                case SUBTRACT -> operandL - operandR;
                case MULTIPLY -> operandL * operandR;
                case DIVIDE -> operandL / operandR;
                case POWER -> Math.pow(operandL, operandR);
            };
            Term operationTerm = new Term(operation + "");
            simpleNode = new Node(operationTerm);
        }
        //x*1 OR 1*x = x
        //x*0 OR 0*x = 0
        else if(this.operator.getOperatorType()==OperatorType.MULTIPLY){
            if(lNode.operand!=null && lNode.operand.getValue()==1.0 && lNode.operand.getTermType()==TermType.VALUE){
                simpleNode = rNode;
            }
            else if(rNode.operand!=null && rNode.operand.getValue()==1.0 && rNode.operand.getTermType()==TermType.VALUE){
                simpleNode = lNode;
            }
            else if(lNode.operand!=null && lNode.operand.getValue()==0.0){
                simpleNode = new Node(new Term("0"));
            }
            else if(rNode.operand!=null && rNode.operand.getValue()==0.0){
                simpleNode = new Node(new Term("0"));
            }
            else{
                return this;
            }
        }
        //x/1 = x
        else if(this.operator.getOperatorType()==OperatorType.DIVIDE){
            if(rNode.operand!=null && rNode.operand.getValue()==1.0 && rNode.operand.getTermType()==TermType.VALUE){
                simpleNode = lNode;
            }
        }
        else {
            return this;
        }
        return simpleNode;
    }

    @Override
    public String toString() {
        return "Node{" +
                "nodeList=" + nodeStk +
                ", operand=" + operand +
                ", operator=" + operator +
                '}';
    }

    public ListIterator<Term> nodeRecursion(ListIterator<Term> it, Term operator){
        //Assign operator
        this.operator = operator;
        //Exit recursion if nodeList is full
        while (nodeStk.size()<2){
            //If it is an operand, create const node and add to right then left
            Term currTerm = it.previous();
            if(currTerm.getTermType()==TermType.VALUE||currTerm.getTermType()==TermType.VARIABLE){
                Node constNode = new Node(currTerm);
                nodeStk.push(constNode);
            }
            //If it is an operator, create new node
            else{
                Node newNode = new Node();
                it = newNode.nodeRecursion(it,currTerm);
                nodeStk.push(newNode);
            }
        }
        return it;
    }

    Node(Term operand){
        this.operand = operand;
    }

    Node(ArrayList<Term> pfFunctionList){
        //Start with last operator
        ListIterator<Term> it = pfFunctionList.listIterator(pfFunctionList.size());
        nodeRecursion(it, it.previous());
    }

    Node(){}

    public static void main(String[] args){
        Node newNode = new Node();
        ArrayList<Term> pfTermList = new ArrayList<Term>();
        pfTermList.add(new Term("x"));
        pfTermList.add(new Term("1"));
        pfTermList.add(new Term("/"));
        newNode = new Node(pfTermList);
        newNode = newNode.simplify();
        System.out.println(newNode.toString());

    }
}
