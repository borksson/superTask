public class Term {
    TermType termType = null;
    String term = null;
    Double value = null;
    OperandType operandType = null;

    public Double getValue(){
        return this.value;
    }

    public String getTermStr(){
        return this.term;
    }

    public OperandType getOperandType(){
        return this.operandType;
    }

    public TermType getTermType(){
        return this.termType;
    }

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
        if(term.contains("x")){
            if(term.charAt(0)=='-'){
                this.value = -1.0;
            }
            else{
                this.value = 1.0;
            }
            this.termType = TermType.VARIABLE;
        }
        else if(term.charAt(0)=='-'&&term.length()>1){
            this.termType = TermType.VALUE;
            value = Double.parseDouble(term);
        }
        else if(Character.isDigit(term.charAt(0))){
            this.termType = TermType.VALUE;
            value = Double.parseDouble(term);
        }
        else if(term.equals("#")){
            this.termType = TermType.EOF;
        }
        else if(term.equals("(")) {
            this.termType = TermType.LEFT_PAREN;
        }
        else if (term.equals(")")) {
            this.termType = TermType.RIGHT_PAREN;
        }
        else{
            this.termType = TermType.OPERATOR;
            operandType = switch (term.charAt(0)) {
                case '+' -> OperandType.ADD;
                case '-' -> OperandType.SUBTRACT;
                case '*' -> OperandType.MULTIPLY;
                case '/' -> OperandType.DIVIDE;
                case '^' -> OperandType.POWER;
                default -> null;
            };
        }

    }

    public static void main(String[] args){
        System.out.println("Functional");
        Term newTerm = new Term("#");
        System.out.println(newTerm.toString());
    }
}
