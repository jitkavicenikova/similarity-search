package thesis.utils;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Utility class for evaluating mathematical formulas.
 */
public final class FormulaEvaluator {
    private FormulaEvaluator() {
    }

    /**
     * Checks if the given formula is valid.
     *
     * @param formula the formula to check
     * @return true if the formula is valid, false otherwise
     */
    public static boolean isValidFormula(String formula) {
        try {
            evaluateFormula(formula, 1);

            return true;
        } catch (IllegalArgumentException | UnsupportedOperationException e) {
            return false;
        }
    }

    /**
     * Evaluates the given formula with the specified variable value.
     *
     * @param formula the formula to evaluate
     * @param x       the value of the variable in the formula
     * @return the result of the evaluation
     * @throws IllegalArgumentException      if the formula is invalid
     * @throws UnsupportedOperationException if the formula contains unsupported operations
     * @throws ArithmeticException           if the formula causes an arithmetic error (e.g., division by zero)
     */
    public static double evaluateFormula(String formula, double x) {
        Expression expression = new ExpressionBuilder(formula)
                .variable("x")
                .build()
                .setVariable("x", x);

        return expression.evaluate();
    }
}