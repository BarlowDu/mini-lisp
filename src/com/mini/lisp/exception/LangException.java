package com.mini.lisp.exception;

public class LangException extends Exception{

    public LangException(String message) {
    super(message);
}

    public LangException(String message,int line){
        super(message+" at line:"+line);
    }
}
