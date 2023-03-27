package com.mini.lisp.exception;


import com.mini.lisp.Token;

public class RuntimeError extends RuntimeException{
    final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }

    public RuntimeError(Throwable throwable) {
        super(throwable);
        this.token=null;
    }


}
