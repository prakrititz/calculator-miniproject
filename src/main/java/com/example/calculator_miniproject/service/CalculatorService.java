package com.example.calculator_miniproject.service;
import org.springframework.stereotype.Service;

import com.example.calculator_miniproject.exception.DivideByZeroException;
@Service
public class CalculatorService {

    public double add(double a, double b) {
        return a + b;
    }
    public double subtract(double a, double b) {
        return a - b;
    }
    public double multiply(double a, double b) {
        return a * b;
    }
    public double divide(double a, double b) {
        if (b == 0) {
            throw new DivideByZeroException("Cannot divide by zero");
        }
        return a / b;
    }
    
}
