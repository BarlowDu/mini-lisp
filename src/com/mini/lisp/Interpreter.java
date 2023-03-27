package com.mini.lisp;



import com.mini.lisp.exception.RuntimeError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Interpreter implements Expr.Visitor<Object> {

    final Environment globals = new Environment();
    private Environment environment = globals;
    //< Functions global-environment
//> Resolving and Binding locals-field
    private final Map<Expr, Integer> locals = new HashMap<>();


    public Interpreter(){}

    public Interpreter(Map<String,Object> values){
        for(Map.Entry<String,Object> value:values.entrySet()){
            globals.define(value.getKey(),value.getValue());
        }
    }
    public Interpreter(Map<String,Object>... values){
        for(Map<String,Object> vals:values) {
            for (Map.Entry<String, Object> value : vals.entrySet()) {
                globals.define(value.getKey(), value.getValue());
            }
        }
    }
    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return lookUpVariable(expr.name, expr);
    }

    @Override
    public Object visitCallExpr(Expr.Call expr) {
        Object callable = evaluate(expr.callee);
        if (!(callable instanceof ICallable)) {
            throw new RuntimeError(expr.paren,
                    "Can only call functions.");
        }
        ICallable function = (ICallable) callable;
        List<Object> arguments = new ArrayList<>();
        for (Expr arg : expr.arguments) {
            arguments.add(evaluate(arg));
        }
        //动态参数
        if(function.isVarArgs()){
            if(arguments.size()<function.arity()){
            throw new RuntimeError(expr.paren, "Expected more" +
                    function.arity() + " arguments but got " +
                    arguments.size() + ".");
            }
        }else {
            if (arguments.size() != function.arity()) {
                throw new RuntimeError(expr.paren, "Expected " +
                        function.arity() + " arguments but got " +
                        arguments.size() + ".");
            }
        }
        return function.call(this, arguments);
    }

    @Override
    public Object visitDefineExpr(Expr.Define expr) {
        Object value = evaluate(expr.value);
        environment.define(expr.name.lexeme, value);
        return null;
    }

    @Override
    public Object visitLambdaExpr(Expr.Lambda expr) {
        return new LambdaCallable(expr,environment);
    }

    public Object interpret(List<Expr> exprs) {
        Object result = null;
        for (Expr expr : exprs) {
            result = evaluate(expr);
        }
        return result;
    }


    Object executeBlock(List<Expr> exprs,
                      Environment environment) {
        Environment previous = this.environment;
        Object result = null;
        try {
            this.environment = environment;

            for (Expr expr : exprs) {
                result = evaluate(expr);
            }
            return result;
        }catch (Exception e){
            throw new RuntimeError(e);
        }
        finally {
            this.environment = previous;
        }
    }

    private Object evaluate(Expr expr) {
        return expr.accept(this);
    }

    void resolve(Expr expr, int depth) {
        locals.put(expr, depth);
    }

    private Object lookUpVariable(Token name, Expr expr) {
        Integer distance = locals.get(expr);
        if (distance != null) {
            return environment.getAt(distance, name.lexeme);
        } else {
            return globals.get(name);
        }
    }

    void define(String name,Object value){
        environment.define(name,value);
    }
}
