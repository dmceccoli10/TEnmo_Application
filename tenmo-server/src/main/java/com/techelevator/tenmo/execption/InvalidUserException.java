package com.techelevator.tenmo.execption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "You cannot send money to yourself. Please make sure both to and from accounts are unique.")
public class InvalidUserException extends Exception {

  public InvalidUserException() {
      super("Invalid user.");
  }

}
