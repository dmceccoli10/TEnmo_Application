package com.techelevator.tenmo.execption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Invalid transaction.")
public class InvalidTransactionException extends Exception {
    public InvalidTransactionException() {
        super("Invalid transaction.");
    }
}
