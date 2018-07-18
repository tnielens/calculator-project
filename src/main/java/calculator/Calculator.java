package calculator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A simple calculator program reading arithmetic expressions from the standard
 * input and printing their values on the standard output.
 */
public class Calculator {
	/**
	 * Evaluates an arithmetic expression. The grammar of accepted expressions
	 * is the following:
	 * <p>
	 * <code>
	 *   expr ::= expr binop expr | '-' expr | '(' expr ')' | binding | term | function <br>
	 *   binop ::= '+' | '-' | '*' | '/' <br>
	 *   binding ::= identifier '=' expr <br>
	 *   term ::= number | identifier <br>
	 *   number ::= integer | decimal <br>
	 *   integer ::= '0' | ('1' - '9') ('0' - '9')* <br>
	 *   decimal ::= ( integer )? '.' ('0' - '9')* <br>
	 *   identifier ::= ('a' - 'z' | 'A' - 'Z') ('a' - 'z' | 'A' - 'Z' | '0' - '9')* <br>
	 *   function ::= ('sqrt' | 'log' | 'sin' | 'cos') '(' expr ')' <br>
	 * </code>
	 * <p>
	 * To keep things simple, whitespace is not allowed in expressions.
	 * <p>
	 * Evaluation of binary operators follows the convention that multiplication
	 * and division take precedence over addition and subtraction.
	 * <p>
	 * Functions are implemented in terms of the respective static methods in
	 * java.lang.Math.
	 * <p>
	 * The bindings produced during the evaluation of the given expression
	 * are stored in a map, where they remain available for the evaluation
	 * of subsequent expressions.
	 * <p>
	 * Before leaving this method, the value of the given expression is bound
	 * to the special variable named "_".
	 * 
	 * @param expr well-formed arithmetic expression
	 * @return the value of the given expression
	 */
	public double eval(String expr) {
		
		Evaluator evaluator = new Evaluator(bindings);
		return evaluator.eval(expr);
	}
	
	public Map<String,Double> bindings() {
		return bindings;
	}
	
	private final Map<String,Double> bindings = new LinkedHashMap<>();
	
	public static void main(String[] args) throws IOException {
		Calculator calc = new Calculator();
		try (BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
				PrintWriter out = new PrintWriter(System.out, true)) {
			while (true) {
				String line = in.readLine();
				if (line == null) {
					break;
				}
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				try {
					if (!line.startsWith(":")) {
						// handle expression
						out.println(calc.eval(line));
					} else {
						// handle command
						String[] command = line.split("\\s+", 2);
						switch (command[0]) {
							case ":vars":
								calc.bindings().forEach((name, value) ->
										out.println(name + " = " + value));
								break;
							case ":clear":
								if (command.length == 1) {
									// clear all
									calc.bindings().clear();
								} else {
									// clear requested
									calc.bindings().keySet().removeAll(Arrays.asList(command[1].split("\\s+")));
								}
								break;
							case ":exit":
							case ":quit":
								System.exit(0);
								break;
							default:
								throw new RuntimeException("unrecognized command: " + line);
						}
					}
				} catch (Exception ex) {
					System.err.println("*** ERROR: " + ex.getMessage());
				}
			}
		}
	}
}
