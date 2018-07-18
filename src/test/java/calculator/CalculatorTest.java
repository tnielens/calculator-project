package calculator;


import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Integration test for the Calculator.
 */

public class CalculatorTest
{

	private static Double epsilon = 0.00000001;
	private Calculator calc;
	
	@Before
	public void setup(){
		calc = new Calculator();
	}
	
	@Test
    public void integerToken()
    {	
        assertEquals(2.0, calc.eval("2"), epsilon);
    }
	
	@Test
    public void decimalToken()
    {	
        assertEquals(2.0, calc.eval("2.0"), epsilon);
    }
	
	@Test
    public void additionOperator()
    {	
        assertEquals(2.0, calc.eval("1+1"), epsilon);
    }
	
	@Test
    public void subsOperator()
    {	
        assertEquals(4.0, calc.eval("4-0"), epsilon);
    }
	
	@Test
    public void multOperator()
    {	
        assertEquals(2.0, calc.eval("0.5*4"), epsilon);
    }
	
	@Test
    public void divOperator()
    {	
        assertEquals(4.0, calc.eval("4-0"), epsilon);
    }    
	
	@Test
    public void functionSqrt()
    {	
        assertEquals(2.0, calc.eval("sqrt(4)"), epsilon);
    } 
	

	@Test
    public void functionSinCos()
    {	
        assertEquals(1.0, calc.eval("cos(1)*cos(1)+sin(1)*sin(1)"), epsilon);
    } 
	
	@Test
    public void functionLog()
    {	
        assertEquals(2.0, calc.eval("log(4)/log(2)"), epsilon);
    } 

	@Test
    public void operatorPrecedence()
    {	
        assertEquals(4.0, calc.eval("1+2*3-4/2-1"), epsilon);
    } 
	
	@Test
    public void parenthesis()
    {	
        assertEquals(0.5, calc.eval("9/((2+1)*6)"), epsilon);
    } 
	
	@Test
    public void variable()
    {	calc.eval("test=9/((2+1)*6)");
        assertEquals(0.5, calc.eval("test"), epsilon);
    } 
	
	@Test
    public void lastExpressionVariable()
    {	
        assertEquals(0.5, calc.eval("9/((2+1)*6)"), epsilon);
        assertEquals(0.5, calc.bindings().get("_"), epsilon);
    } 
	
    @Test(expected=LexicalException.class)
    public void wrongIdentifier(){
    	calc.eval("erü+1");
    }
    
    @Test(expected=LexicalException.class)
    public void unfinishedDecimal(){
    	calc.eval("0.");
    }
    
    @Test(expected=LexicalException.class)
    public void wrongNumber(){
    	calc.eval("2a");
    }
    
    @Test(expected=LexicalException.class)
    public void integerMustStartWithANonZeroDigit(){
    	calc.eval("01");
    }
    
    @Test(expected=ParsingException.class)
    public void unbalancedLeftParenthesis(){
    	calc.eval("((1)");
    }
    
    @Test(expected=ParsingException.class)
    public void unbalancedRightParenthesis(){
    	calc.eval("((1)))");
    }
    
    @Test(expected=ParsingException.class)
    public void missingOperand(){
    	calc.eval("1+");
    }
    
    @Test(expected=ParsingException.class)
    public void missingOperator(){
    	calc.eval("(1)2");
    }
    
}
