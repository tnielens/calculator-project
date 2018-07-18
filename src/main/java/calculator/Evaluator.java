package calculator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import calculator.token.Function;
import calculator.token.Identifier;
import calculator.token.SpecialCharacter;
import calculator.token.Token;
import calculator.token.Decimal;
import calculator.token.Operator;

/**
 * Evaluates a mathematical expression.
 * <p>
 * Grammar defined at {@link Calculator#eval}.
 * <p>
 * Lexical analysis done by the {@link Tokenizer}.
 * <p>
 * Parsing done in {@link #shuntTokens(Iterator)}.
 * <p>
 * Final evaluaton done in {@link #evaluateRPN(Iterator)}.
 *
 */
public class Evaluator {
	private Map<String, Double> bindings;

	public Evaluator(Map<String, Double> bindings) {
		super();
		this.bindings = bindings;
	}

	/**
	 * Evaluation entry point
	 * @return evaluation result
	 * 
	 * @throws {@link LexicalException}
	 * @throws {@link ParsingException}
	 */
	public Double eval(String expr) {
		if (expr.isEmpty()){
			throw new IllegalArgumentException("Cannot evaluate the empty string");
		}
		
		Tokenizer tokenizer = new Tokenizer(expr);
		List<Token> tokens = tokenizer.analyze();
		Iterator<Token> tokenIterator = tokens.iterator();
		
		//variable assignment manual parsing
		String assignedVariable = null;
		if (tokens.size() >= 3 && tokens.get(1) == SpecialCharacter.BINDING){
			if (!(tokens.get(0) instanceof Identifier)){
				throw new ParsingException("The left side of the assignment character must be an indentifier");
			}
			assignedVariable = tokenIterator.next().lexeme;
			tokenIterator.next();
		}

		// reverse polish notation aka postfix notation
		List<Token> postfixExpression = shuntTokens(tokenIterator);
		Double value = evaluateRPN(postfixExpression.iterator());
		
		if (assignedVariable != null){
			bindings.put(assignedVariable, value);
		} 
		bindings.put("_", value);
		
		return value;
	}

	/**
	 * Shunting-yard algorithm --
	 * https://en.wikipedia.org/wiki/Shunting-yard_algorithm
	 * <p>
	 * Rearrange infix mathematical expression into a Reverse Polish Notation.
	 * <p>
	 * Iterate through tokens.
	 * <ul>
	 * <li>If the token is a number, then push it to the output queue.
	 * <li>If the token is a function token, then push it onto the stack.
	 * <li>If the token is an operator, o1: while there is an operator token o2,
	 * at the top of the operator stack and its precedence is less than or equal
	 * to that of o2, pop o2 off the operator stack, onto the output queue; at
	 * the end of iteration push o1 onto the operator stack.
	 * <li>If the token is a left parenthesis (i.e. "("), then push it onto the
	 * stack.
	 * <li>If the token is a right parenthesis (i.e. ")"): until the token at
	 * the top of the stack is a left parenthesis, pop operators off the stack
	 * onto the output queue. Pop the left parenthesis from the stack, but not
	 * onto the output queue, if the next token at the top of the stack is a function
	 * token, pop it onto the output queue. If the stack runs out without
	 * finding a left parenthesis, then there are mismatched parentheses.
	 * </ul>
	 * <p>
	 * When there are no more tokens to read and while there are still operator
	 * tokens in the stack: if the operator token on the top of the stack is a
	 * parenthesis, then there are mismatched parentheses, otherwise pop the
	 * operator onto the output queue.
	 * 
	 */
	private List<Token> shuntTokens(Iterator<Token> tokens) {
		List<Token> rpnTokens = new LinkedList<>();
		Stack<Token> stack = new Stack<>();
		ParsingState state = ParsingState.EXPECT_OPERAND;
		while (tokens.hasNext()) {
			Token token = tokens.next();
			if (token == SpecialCharacter.BINDING){
				throw new ParsingException("The \"=\" character is not supported by the shunt-yard algorithm");
			} else if (token instanceof Decimal || token instanceof Identifier) {
				checkState(ParsingState.EXPECT_OPERAND, state);
				rpnTokens.add(token);
				state = ParsingState.EXPECT_OPERATOR;
			} else if (token instanceof Function) {
				checkState(ParsingState.EXPECT_OPERAND, state);
				stack.push(token);
			} else if (token instanceof Operator) {
				checkState(ParsingState.EXPECT_OPERATOR, state);
				while (!stack.empty() && stack.peek() instanceof Operator
						&& ((Operator) token).precedence <= ((Operator) stack.peek()).precedence) {
					rpnTokens.add(stack.pop());
				}
				stack.push(token);
				state = ParsingState.EXPECT_OPERAND;
			} else if (token == SpecialCharacter.LEFT_PAREN) {
				checkState(ParsingState.EXPECT_OPERAND, state);
				stack.push(token);
			} else if (token == SpecialCharacter.RIGHT_PAREN) {
				checkState(ParsingState.EXPECT_OPERATOR, state);
				while (true) {
					if (stack.empty()) {
						throw new ParsingException("Mismatched parenthesis");
					} else if (stack.peek() == SpecialCharacter.LEFT_PAREN) {
						stack.pop();
						if(!stack.empty() && stack.peek() instanceof Function){
							rpnTokens.add(stack.pop());
						}
						break;
					} else {
						rpnTokens.add(stack.pop());
					}
				}
			} else {
				assert false : "all cases should be covered";
			}
		}

		while (!stack.isEmpty()) {
			if (stack.peek() == SpecialCharacter.LEFT_PAREN) {
				throw new ParsingException("Mismatched parenthesis");
			}
			rpnTokens.add(stack.pop());
		}
		if (state == ParsingState.EXPECT_OPERAND){
			throw new ParsingException("end of input reached whereas operand expected");
		}
		return rpnTokens;
	}

	/**
	 * Evaluate valid reverse polish notation
	 */
	private Double evaluateRPN(Iterator<Token> tokens) {

		Stack<Double> stack = new Stack<>();
		while (tokens.hasNext()) {
			Token token = tokens.next();
			if (token instanceof Operator) {
				if (token == Operator.ADDITION){
					Double arg2 = stack.pop();
					Double arg1 = stack.pop();
					stack.push(arg1 + arg2);
				} else if (token == Operator.SUBSTRACTION){
					Double arg2 = stack.pop();
					Double arg1 = stack.pop();
					stack.push(arg1 - arg2);
				} else if (token == Operator.MULTIPLICATION){
					Double arg2 = stack.pop();
					Double arg1 = stack.pop();
					stack.push(arg1 * arg2);
				} else if (token == Operator.DIVISION){
					Double arg2 = stack.pop();
					Double arg1 = stack.pop();
					stack.push(arg1 / arg2);
				}
			} else if (token instanceof Function) {
				Double arg;
				if (token == Function.SQRT) {
					arg = stack.pop();
					stack.push(Math.sqrt(arg));
				} else if (token == Function.LOG) {
					arg = stack.pop();
					stack.push(Math.log(arg));
				} else if (token == Function.SIN) {
					arg = stack.pop();
					stack.push(Math.sin(arg));
				} else if (token == Function.COS) {
					arg = stack.pop();
					stack.push(Math.cos(arg));
				}
			} else if (token instanceof Identifier) {
				stack.push(bindings.get(token.lexeme));
			} else if (token instanceof Decimal) {
				stack.push(((Decimal) token).value);
			} else {
				assert false;
			}
		}
		return stack.pop();
	}
	
	/**
	 * Basic expression syntax validation. The parser expects either an operand
	 * (function call, identifiers, left parenthesis or number) or an operator 
	 * (operator or right parenthesis).
	 */
	private void checkState(ParsingState expected, ParsingState state){
		if (expected != state){
			if (state == ParsingState.EXPECT_OPERAND) {
				throw new ParsingException("An operator or right parenthesis was parsed whereas an operand "
						+ "(function call, identifiers, left parenthesis, number) was expected");
			} else if (state == ParsingState.EXPECT_OPERATOR) {
				throw new ParsingException(
						"An operand (function call, identifiers, left parenthesis, number)"
						+ " was parsed whereas an operator or right parenthesis was expected");
			}
		}
	}
	
	public static enum ParsingState { EXPECT_OPERAND, EXPECT_OPERATOR}
}

