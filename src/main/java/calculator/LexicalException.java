package calculator;

public class LexicalException extends RuntimeException {

	public LexicalException(String message, Throwable cause) {
		super(message, cause);
	}

	public LexicalException(String message) {
		super(message);
	}
	
	public LexicalException(int i){
		this(String.format("Lexical Error at index %s", i));
	}
}
