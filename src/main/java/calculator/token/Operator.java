package calculator.token;

public class Operator extends Token {

	public static final Operator SUBSTRACTION = new Operator("-", 0); 
	public static final Operator MULTIPLICATION = new Operator("*", 1); 
	public static final Operator ADDITION = new Operator("+",  0);
	public static final Operator DIVISION = new Operator("/", 1); 

	public final int precedence;

	public Operator(String lexeme, int precedence) {
		super(lexeme);
		this.precedence = precedence;
	}
}
