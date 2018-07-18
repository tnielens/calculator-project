package calculator.token;

import calculator.Evaluator;

/**
 * Lexical unit the {@link Evaluator} can parse
 */
public class Token {

	/**
	 * token original string
	 */
	public final String lexeme;
	
	public Token(String lexeme) {
		this.lexeme = lexeme;
	}

	@Override
	public String toString() {
		return "Token [lexeme=" + lexeme + "]";
	}
}
