package com.mini.lisp.exception;

public class SyntaxException extends Exception {
    public SyntaxException(String message) {
        super(message);
    }

    public SyntaxException(String message,int line){
        super(message+" at line:"+line);
    }

}
