package com.mini.lisp;

import java.util.List;

public class LambdaCallable implements ICallable{

    private int _arity;
    private Expr.Lambda lambda;

    private Environment closure;

    public LambdaCallable(Expr.Lambda lambda,Environment closure){
        this.lambda=lambda;
        this.closure=closure;
        this._arity=lambda.arguments.size();
    }

    @Override
    public int arity() {
        return _arity;
    }

    @Override
    public boolean isVarArgs() {
        return false;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
//< call-closure
        for (int i = 0; i < lambda.arguments.size(); i++) {
            environment.define(lambda.arguments.get(i).lexeme,
                    arguments.get(i));
        }
        return interpreter.executeBlock(lambda.body, environment);
    }
}
