public class Term {
    TermType termType = null;
    String term = null;
    Double value = null;
    OperandType operandType = null;

    public String toString(){
        StringBuilder termStr = new StringBuilder();
        termStr.append("Term: ").append(this.term).append("\n");
        termStr.append("TermType: ").append(this.termType).append("\n");
        termStr.append("Value: ").append(this.value).append("\n");
        termStr.append("OperandType: ").append(this.operandType).append("\n");
        return termStr.toString();
    }

    public Term(String term){
        this.term = term;
        if(Character.isLetter(term.charAt(0))){
            this.termType = TermType.VARIABLE;
        }
        //TODO: What if it is negative?
        else if(term.charAt(0)=='-'&&term.length()>1){
            this.termType = TermType.VALUE;
            value = Double.parseDouble(term);
        }
        else if(Character.isDigit(term.charAt(0))){
            this.termType = TermType.VALUE;
            value = Double.parseDouble(term);
        }
        else{
            operandType = switch (term.charAt(0)) {
                case '+' -> OperandType.ADD;
                case '-' -> OperandType.SUBTRACT;
                case '*' -> OperandType.MULTIPLY;
                case '/' -> OperandType.DIVIDE;
                case '^' -> OperandType.POWER;
                case '(', ')' -> OperandType.DELIMITER;
                default -> null;
            };
        }

    }

    public static void main(String[] args){
        System.out.println("Functional");
        Term newTerm = new Term("-2.0");
        System.out.println(newTerm.toString());
    }
}
