package com.thejas.ca_billing_system.exception;

public class InvoiceHasItemsException extends RuntimeException {
    public InvoiceHasItemsException(String message) {
        super(message);
    }
}