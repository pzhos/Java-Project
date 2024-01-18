package com.example.semproj;

import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartCalculator {
    private static final Stack<Double> valuesStack = new Stack<>();
    private static final Stack<String> operatorsStack = new Stack<>();

    public static String calculate(String expression) {
        String memory = expression;
        expression = parse(expression);
        if (isCorrect(expression)) {
            toRPN(expression);
            return " " + calculate() + " ";
        }
        return memory;
    }

    private static void toRPN(String expression) {
        StringTokenizer stringTokenizer = new StringTokenizer(expression, "/*-+^()", true);
        while (stringTokenizer.hasMoreTokens()) {
            String currentToken = stringTokenizer.nextToken();
            if (Pattern.matches("\\d+(\\.\\d+)?", currentToken)) valuesStack.push(Double.parseDouble(currentToken));
            else if (operatorsStack.isEmpty()) operatorsStack.push(currentToken);
            else if (operationPriority(currentToken) > operationPriority(operatorsStack.peek()) || currentToken.equals("(")) operatorsStack.push(currentToken);
            else {
                while (operationPriority(currentToken) <= operationPriority(operatorsStack.peek())) {
                    if (operatorsStack.peek().equals("(") && currentToken.equals(")")) {
                        operatorsStack.pop();
                        break;
                    }
                    double SecondValue = valuesStack.pop();
                    double FirstValue = valuesStack.pop();
                    if (operatorsStack.isEmpty()) {
                        valuesStack.push(intermediateResult(FirstValue, SecondValue, currentToken));
                    } else {
                        valuesStack.push(intermediateResult(FirstValue, SecondValue, operatorsStack.pop()));
                    }

                    if (operatorsStack.isEmpty()) {
                        break;
                    }
                }
                if (!currentToken.equals(")")) operatorsStack.push(currentToken);
            }
        }
    }

    public static String parse(String expression) {
        expression = expression.replaceAll(" ", "");
        if (expression.charAt(0) == '-') expression = "0" + expression;
        return expression.replaceAll("\\(-", "(0-").
                replaceAll("(\\d)\\(", "$1*(").
                replaceAll("\\)\\(", ")*(").
                replaceAll("\\)(\\d)", ")*$1");
    }

    private static int operationPriority(String operation) {
        return switch (operation) {
            case "(", ")" -> 1;
            case "+", "-" -> 2;
            case "/", "*" -> 3;
            case "^" -> 4;
            default -> -1;
        };
    }

    private static double intermediateResult(double firstValue, double secondValue, String operation) throws IllegalArgumentException {
        switch (operation) {
            case "+":
                return firstValue + secondValue;
            case "-":
                return firstValue - secondValue;
            case "^": {
                return Math.pow(firstValue, secondValue);
            }
            case "/": {
                if (secondValue != 0d) return firstValue / secondValue;
                else throw new IllegalArgumentException("Can't divide on 0");
            }
            case "*":
                return firstValue * secondValue;
            default:
                return 0d;
        }
    }

    private static boolean isCorrect(String tested) {
        return isParenthesesLocationCorrect(tested) && isOperatorsAreSingle(tested) && isNumberOfOperatorsCorrect(tested);
    }

    public static boolean isParenthesesLocationCorrect(String tested) {
        while(!operatorsStack.isEmpty()) {
            operatorsStack.pop();
        }
        for (int i = 0; i < tested.length(); ++i) {
            if (tested.charAt(i) == '(') operatorsStack.push("(");
            else if (tested.charAt(i) == ')') {
                if (operatorsStack.isEmpty()) return false;
                else operatorsStack.pop();
            }
        }
        return operatorsStack.isEmpty();
    }

    public static boolean isOperatorsAreSingle(String tested) {
        Pattern doubleOperator = Pattern.compile("[/*\\-+^]{2}");
        Matcher matcher = doubleOperator.matcher(tested);
        return !matcher.find();
    }

    public static boolean isNumberOfOperatorsCorrect(String tested) {
        Pattern digit = Pattern.compile("\\d+(\\.\\d+)?");
        Pattern operator = Pattern.compile("[/*\\-+^]");
        Matcher digitFinder = digit.matcher(tested);
        Matcher operatorFinder = operator.matcher(tested);
        int digitCounter = 0;
        int operatorCounter = 0;
        while (digitFinder.find()) {
            digitCounter++;
        }
        while (operatorFinder.find()) {
            operatorCounter++;
        }
        return digitCounter > operatorCounter;
    }

    private static String calculate() {
        while (!operatorsStack.isEmpty()) {
            double secondValue = valuesStack.pop();
            double firstValue = valuesStack.pop();
            valuesStack.push(intermediateResult(firstValue, secondValue, operatorsStack.pop()));
        }
        double result = valuesStack.pop();
        return String.format("%.2f", result);
    }
}
