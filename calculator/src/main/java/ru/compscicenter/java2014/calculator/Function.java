package ru.compscicenter.java2014.calculator;

// класс функций с одним аргументом
public enum Function {
    ABS {
        public double value(double argument) {
            return Math.abs(argument);
        }
    },
    SIN {
        public double value(double argument) {
            return Math.sin(argument);
        }
    },
    COS {
        public double value(double argument) {
            return Math.cos(argument);
        }
    };

    public abstract double value(double argument);
}
