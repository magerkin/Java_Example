package ru.compscicenter.java2014.calculator;

import java.lang.Math;

public class Evaluator implements Calculator {

    private int currentPosition = 0;
    private StringBuilder buffer = new StringBuilder();

    // "основной" метод
    public double calculate(String expression) {
        currentPosition = 0; // зануляем для каждого нового использования
        String newExpression = format(expression);
        double value = calculateExpression(newExpression);
        return value;
    }

    // метод убирает пробелы и переводит символы в нижний регистр
    private String format(String expression) {
        String newExpression = expression.toLowerCase().replaceAll(" ", "");
        return  newExpression;
    }

    // разбор: выражение :: = слагаемое [+- слагаемое]
    private double calculateExpression(String expression) {
        double value = calculateTerm(expression);
        outer: while (currentPosition < expression.length()) {
            switch (expression.charAt(currentPosition)) {
                case '-':
                    currentPosition++;
                    value -= calculateTerm(expression);
                    break;
                case '+':
                    currentPosition++;
                    value += calculateTerm(expression);
                    break;
                default:
                    break outer;
            }
        }
        return value;
    }

    // разбор: слагаемое :: = множитель [*/ множитель]
    private double calculateTerm(String expression) {
        double value = calculateFactor(expression);
        outer: while (currentPosition < expression.length()) {
            switch (expression.charAt(currentPosition)) {
                case '/':
                    currentPosition++;
                    value /= calculateFactor(expression);
                    break;
                case '*':
                    currentPosition++;
                    value *= calculateFactor(expression);
                    break;
                default:
                    break outer;
            }
        }
        return value;
    }

    // разбор: множитель :: = [знак] элементраный_множитель [^ множитель]
    private double calculateFactor(String expression) {
        int sign = 1;
        // считаем все плюсы и минусы перед множителем
        while (((expression.charAt(currentPosition) == '+') || ((expression.charAt(currentPosition) == '-'))) && (currentPosition < expression.length())) {
            sign *=  Integer.parseInt(expression.charAt(currentPosition) + "1");
            currentPosition++;
        }
        double value = calculateElementaryFactor(expression);
        while ((currentPosition < expression.length()) && (expression.charAt(currentPosition) == '^')) {
            currentPosition++;
            double power = calculateFactor(expression);
            value = Math.pow(value, power);
        }
        return sign*value;
    }

    // разбор: элементраный_множитель ::= число | функция(выражение) | (выражение)
    private double calculateElementaryFactor(String expression) {
        double value = 0;
        while (currentPosition < expression.length()) {
            if (Character.isDigit(expression.charAt(currentPosition))) {
                value = readNumber(expression);
            } else if (Character.isAlphabetic(expression.charAt(currentPosition))){
                value = readFunction(expression);
            } else if (expression.charAt(currentPosition) == '('){
                currentPosition++;
                value = calculateExpression(expression);
                if (expression.charAt(currentPosition) == ')') {
                    currentPosition++;
                }
            } else
                break;
        }
        return value;
    }

    // считываем число посимвольно
    private double readNumber(String expression) {
        buffer.delete(0, buffer.length());
        while (currentPosition < expression.length()) {
            char liter = expression.charAt(currentPosition);
            if (Character.isDigit(liter)) {
                buffer.append(liter);
            } else if (liter == '.') {
                buffer.append(liter);
            } else if (liter == 'e') {
                buffer.append(liter);
                currentPosition++;
                liter = expression.charAt(currentPosition);
                buffer.append(liter);
            } else {
                break;
            }
            currentPosition++;
        }
        String result = buffer.toString();
        return Double.valueOf(result);
    }

    // считываем имя функции посимвольно
    private double readFunction(String expression) {
        buffer.delete(0, buffer.length());
        char liter = expression.charAt(currentPosition);
        while ((Character.isAlphabetic(liter)) && (currentPosition < expression.length())) {
            buffer.append(liter);
            currentPosition++;
            liter = expression.charAt(currentPosition);
        }
        String functionName = buffer.toString().toUpperCase();
        Function function = Function.valueOf(functionName);
        currentPosition++; // пропустим открывающую скобку
        double argument = calculateExpression(expression);
        currentPosition++; // пропустим закрывающую скобку
        return function.value(argument);
    }
}
