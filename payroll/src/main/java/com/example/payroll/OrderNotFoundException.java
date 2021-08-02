package com.example.payroll;

public class OrderNotFoundException extends RuntimeException {
    OrderNotFoundException(Long id) {
        super("Order not found " + id);
    }
}
