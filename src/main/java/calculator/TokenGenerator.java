package calculator;

import java.util.Iterator;

import calculator.token.Function;
import calculator.token.SpecialCharacter;
import calculator.token.Token;
import calculator.token.Decimal;
import calculator.token.Operator;

/**
 * Lazy Token Generator, implements the Iterator interface. Lexical Analysis
 * logic is implemented here.
 */
public class TokenGenerator implements Iterator<Token> {

	private final String input;
	/**
	 * start index of the ongoing token analysis
	 */
	private int startIndex = 0;
	
	/**
	 * next character to be analyzed
	 */
	private int nextCharIndex = 0;
	/**
	 * lexical analysis state
	 */
	private GeneratorState state = new StartState();
	/**
	 * produced token
	 * <p>
	 * Will be non-null when current token analysis is finished. 
	 */
	private Token token = null;

	public TokenGenerator(String input) {
		this.input = input;
	}

	@Override
	public boolean hasNext() {
		return startIndex != input.length();
	}

	/**
	 * Produce the next token in the analyzed input string.
	 */
	@Override
	public Token next() {

		state = new StartState();

		while (token == null) {
			if (nextCharIndex == input.length()){
				state.buildToken();
			} else {
				state.analyzeNextCharacter();
			}
		}
		startIndex = nextCharIndex;
		Token returnToken = token;
		token = null;
		return returnToken;
	}

	/**
	 * State Design Pattern. Each {@link GeneratorState} implementation
	 * encapsulates behavior. They represent token analysis state.
	 */
	private interface GeneratorState {
		/**
		 * Analyze next character, change parent fields state and nextCharIndex
		 * when appropriate. {@code nextCharIndex} is guaranteed to be a valid 
		 * index. Call {@link #buildToken()} to build a token when the next character
		 * is not part of the current token.
		 * 
		 * @throws LexicalException
		 */
		void analyzeNextCharacter();

		/**
		 * Set the token field of the parent class with collected characters
		 * 
		 * @throws LexicalException
		 */
		void buildToken();
	}

	private class StartState implements GeneratorState {
		@Override
		public void analyzeNextCharacter() {

			char nextChar = input.charAt(startIndex);

			switch (nextChar) {
			case '/':
				TokenGenerator.this.token = Operator.DIVISION;
				nextCharIndex++;
				return;
			case '*':
				TokenGenerator.this.token = Operator.MULTIPLICATION;
				nextCharIndex++;
				return;
			case '+':
				TokenGenerator.this.token = Operator.ADDITION;
				nextCharIndex++;
				return;
			case '-':
				TokenGenerator.this.token = Operator.SUBSTRACTION;
				nextCharIndex++;
				return;
			case '(':
				TokenGenerator.this.token = SpecialCharacter.LEFT_PAREN;
				nextCharIndex++;
				return;
			case ')':
				TokenGenerator.this.token = SpecialCharacter.RIGHT_PAREN;
				nextCharIndex++;
				return;
			case '=':
				TokenGenerator.this.token = SpecialCharacter.BINDING;
				nextCharIndex++;
				return;
			}
			
			if (isAlphabetical(nextChar)) {
				state = new Identifier();
				nextCharIndex++;
				return;
			}

			if (nextChar == '.') {
				nextCharIndex++;
				state = new DecimalAfterPoint();
				return;
			}

			if (nextChar == '0') {
				nextCharIndex++;
				state = new ZeroOrDecimal();
				return;
			}

			if (isNumerical(nextChar)) {
				assert nextChar != '0';
				nextCharIndex++;
				state = new IntegerOrDecimal();
				return;
			}
			
			throw new LexicalException(nextCharIndex);

		}

		@Override
		public void buildToken() {
			throw new UnsupportedOperationException();
		}
	}

	private class Identifier implements GeneratorState {
		@Override
		public void analyzeNextCharacter() {
			if (isAlphaNum(input.charAt(nextCharIndex))) {
				nextCharIndex++;
			} else {
				buildToken();
			}
		}

		@Override
		public void buildToken() {
			String lexeme = input.substring(startIndex, nextCharIndex);
			
			switch(lexeme){
			case "log": 
				token = Function.LOG;
				break;
			case "sin": 
				token = Function.SIN; 
				break;
			case "cos": 
				token = Function.COS;
				break;
			case "sqrt":
				token = Function.SQRT;
				break;
			default: 
				token = new calculator.token.Identifier(lexeme);
				break;
			}
			
		}
	}

	private class DecimalAfterPoint implements GeneratorState {
		@Override
		public void analyzeNextCharacter() {
			if (isNumerical(input.charAt(nextCharIndex))) {
				nextCharIndex++;
			} else {
				buildToken();
			}
		}

		@Override
		public void buildToken() {
			if ('.' == input.charAt(nextCharIndex - 1)) {
				throw new LexicalException(String.format("No digit after point at index %s", nextCharIndex - 1));
			}
			String lexeme = input.substring(startIndex, nextCharIndex);
			token = new Decimal(lexeme, Double.valueOf(lexeme));
		}
	}

	private class ZeroOrDecimal implements GeneratorState {
		@Override
		public void analyzeNextCharacter() {
			char nextChar = input.charAt(nextCharIndex);
			if (isNumerical(nextChar)) {
				throw new LexicalException(String.format("0 at index %s cannot be fallowed by a digit", nextCharIndex));
			} else if (nextChar == '.') {
				nextCharIndex++;
				state = new DecimalAfterPoint();
			} else {
				buildToken();
			}
		}

		@Override
		public void buildToken() {
			token = new Decimal("0", 0);
		}
	}

	private class IntegerOrDecimal implements GeneratorState {

		@Override
		public void analyzeNextCharacter() {
			char nextChar = input.charAt(nextCharIndex);
			if (isAlphabetical(nextChar)){
				throw new LexicalException(nextCharIndex);
			} else if (isNumerical(nextChar)) {
				nextCharIndex++;
				// keep same state
			} else if (nextChar == '.') {
				nextCharIndex++;
				state = new DecimalAfterPoint();
			} else {
				buildToken();
			}
		}

		@Override
		public void buildToken() {
			String lexeme = input.substring(startIndex, nextCharIndex);
			token = new Decimal(lexeme, Double.valueOf(lexeme));
		}
	}

	public static boolean isAlphabetical(char character) {
		if ((character >= 'a' && character <= 'z') || (character >= 'A' && character <= 'Z')) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isAlphaNum(char character) {
		if (isAlphabetical(character) || (character >= '0' && character <= '9')) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isNumerical(char character) {
		if (character >= '0' && character <= '9') {
			return true;
		} else {
			return false;
		}
	}
}
