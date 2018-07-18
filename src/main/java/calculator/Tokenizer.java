package calculator;

import java.util.LinkedList;
import java.util.List;

import calculator.token.Token;

/**
 * Splits the input string into a list of tokens to be parsed and evaluated. See
 * {@link Token} for the different token types. See {@link TokenGenerator}
 * for lexical analysis logic.
 */
public class Tokenizer {

	private final String input;

	public Tokenizer(String input) {
		super();
		this.input = input;
	}
	
	public List<Token> analyze(){
		List<Token> tokens = new LinkedList<>();
		TokenGenerator generator = new TokenGenerator(input);
		generator.forEachRemaining(tokens::add);
		return tokens;
	}

}
