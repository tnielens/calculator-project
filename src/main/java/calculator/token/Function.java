package calculator.token;


public class Function extends Token {

	public static final Function COS = new Function("cos");
	public static final Function SIN = new Function("sin");
	public static final Function SQRT = new Function("sqrt");
	public static final Function LOG = new Function("log");
	
	public Function(String lexeme) {
		super(lexeme);
	}
}
