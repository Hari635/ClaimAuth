package com.cts.claimauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserNotFoundException extends RuntimeException {
      public UserNotFoundException(String message,String id) {
    	  super(String.format("Filed",id,message));
      }
}
