public class IrrationalFunction {
    String functionStr;

    private void parseFunction(String functionStr) {
        //The grammar of a mathematical function is:
        //S -> ID (VAR) = EXPRESSION
        //ID -> any alpha char
        //VAR -> any alpha char
        //EXPRESSION -> (EXPRESSION) | EXPRESSION OP TERM | TERM
        //TERM -> VAR | CONST
        //CONST -> any numerical const (1,0,e,pi,ect.)
        //OP -> + | - | * | / | ^
    }

    public double factorial(int val){
        int ret = val;
        for(int i = val; i != 1; i--){
            ret *= i-1;
        }
        return ret;
    }

    private float calcFunc(String func, double val){
        return 0;
    }

    private String derivative(String func){
        return "";
    }

    private double findReference(float value){
        return Math.floor(value);
    }

    private float partialSum_starter(float value, int tDegreeMax) {
        //Find reference
        double reference = findReference(value);
        return partialSum(this.functionStr, value, reference, 0, tDegreeMax);
    }

    private float partialSum(String func, float value, double refernce, int tDegree, int tDegreeMax) {
        float val = 0;
        //Recursive calculation
        //Find f'(a)
        String funcPrime = derivative(func);
        //Find n!
        val += calcFunc(funcPrime, refernce) / factorial(tDegree);
        // f'(a)/n! * (x - a)^n
        if(tDegree!=tDegreeMax){
            val += partialSum(funcPrime, value, refernce, tDegree++, tDegreeMax);
        }
        return val;
    }

    public float aproxValue(float value, int tDegree) {
        //Find reference point
        //Calculate partial sum

        return 0;
    }

    public IrrationalFunction(String functionStr) {
        this.functionStr = functionStr;
    }

    public static void main(String[] args) {
        IrrationalFunction myFunc = new IrrationalFunction("f(x)=2+x");
        System.out.println(myFunc.factorial(15));
    }
}
