package calculator.token;

public class SpecialCharacter extends Token {

	public static final SpecialCharacter LEFT_PAREN = new SpecialCharacter("("); 
	public static final SpecialCharacter RIGHT_PAREN = new SpecialCharacter(")"); 
	public static final SpecialCharacter BINDING = new SpecialCharacter("="); 
	
	public SpecialCharacter(String lexeme) {
		super(lexeme);
	}
	
}
