package com.farmatodo.ecommerce.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TokenizationRejectedException extends RuntimeException {
    public TokenizationRejectedException(String msg) { super(msg); }
}
