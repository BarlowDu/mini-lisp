package com.mini.lisp;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultEngine implements  Engine{

    private Interpreter interpreter;
    private Resolver resolver;

    public DefaultEngine(){
        Map<String,Object> defaultVars=getBaseMethods();
        interpreter=new Interpreter(defaultVars);
        resolver=new Resolver(interpreter);
    }


    protected Map<String,Object> getBaseMethods(){
        Method[] all= BaseMethod.class.getMethods();
        Map<String,Object>result=new HashMap<>();
        for (Method m:all){
            Func func=m.getAnnotation(Func.class);
            if(func==null||func.name()==null){
                continue;
            }
            ICallable callable=new NativeCallable(m,func.isVarArgs());
            for(String name: func.name()){
                result.put(name,callable);
            }
        }
        return result;
    }


    @Override
    public Object run(List<Expr> exprs) {
        if(exprs==null){
            return null;
        }
        resolver.resolve(exprs);
        return interpreter.interpret(exprs);
    }
}
