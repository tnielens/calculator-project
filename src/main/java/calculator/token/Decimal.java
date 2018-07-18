package calculator.token;

public class Decimal extends Token {
	
	public final double value;

	public Decimal(String lexeme, double value) {
		super(lexeme);
		this.value = value;
	}
}
