package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Producto no encontrado")
public class ProductNotFoundException extends Exception {
    public ProductNotFoundException() { super(); }
    public ProductNotFoundException(String message) { super(message); }
}

