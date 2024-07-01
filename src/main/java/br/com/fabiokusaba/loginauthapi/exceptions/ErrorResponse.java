package br.com.fabiokusaba.loginauthapi.exceptions;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus status, String message) {
}
