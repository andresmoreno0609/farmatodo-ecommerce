package com.farmatodo.ecommerce.exceptions;

public class NotFoundException extends RuntimeException {
    private final String code;
    public NotFoundException(String code){ super(code); this.code = code; }
    public String getCode(){ return code; }
}
