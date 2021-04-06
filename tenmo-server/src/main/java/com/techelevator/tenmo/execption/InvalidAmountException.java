package com.techelevator.tenmo.execption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Please enter a valid dollar amount.")
public class InvalidAmountException extends Exception{
    public InvalidAmountException() {
        super("Invalid dollar amount");
    }
}
