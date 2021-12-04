public class Term {
    TermType termType = null;
    String term = null;
    Double value = null;
    OperatorType operatorType = null;

    public TermType getTermType() {
        return termType;
    }

    public String getTerm() {
        return term;
    }

    public Double getValue() {
        return value;
    }

    public OperatorType getOperatorType() {
        return operatorType;
    }

    @Override
    public String toString() {
        return "Term{" +
                "termType=" + termType +
                ", term='" + term + '\'' +
                ", value=" + value +
                ", operandType=" + operatorType +
                '}';
    }

    Term(String term){
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
            operatorType = switch (term.charAt(0)) {
                case '+' -> OperatorType.ADD;
                case '-' -> OperatorType.SUBTRACT;
                case '*' -> OperatorType.MULTIPLY;
                case '/' -> OperatorType.DIVIDE;
                case '^' -> OperatorType.POWER;
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
