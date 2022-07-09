package com.cts.claimauth.advice;

import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.cts.claimauth.exception.TokenException;
import com.cts.claimauth.exception.UserNotFoundException;

@RestControllerAdvice
public class TokenControllerAdvice {
   
	@ExceptionHandler(value=TokenException.class)
	@ResponseStatus(HttpStatus.FORBIDDEN)
	public ErrorResponse handleTokenException(TokenException ex,WebRequest request) {
		return new ErrorResponse(
				HttpStatus.FORBIDDEN.value(),
				new Date(),
				ex.getMessage(),
				request.getDescription(false)
				);
	}
	@ExceptionHandler(value=UserNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleUserNotFound(UserNotFoundException ex,WebRequest request) {
		return new ErrorResponse(
				HttpStatus.BAD_REQUEST.value(),
				new Date(),
				ex.getMessage(),
				request.getDescription(false)
				);
		
	}
}
