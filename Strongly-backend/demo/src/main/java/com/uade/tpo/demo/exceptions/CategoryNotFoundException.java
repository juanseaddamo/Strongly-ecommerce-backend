package com.uade.tpo.demo.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;



@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Categoria no encontrado")
public class CategoryNotFoundException extends Exception {
    public CategoryNotFoundException() { super(); }
    public CategoryNotFoundException(String message) { super(message); }
}