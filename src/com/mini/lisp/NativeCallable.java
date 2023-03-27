package com.mini.lisp;



import com.mini.lisp.exception.RuntimeError;

import java.lang.reflect.Method;
import java.util.List;

public class NativeCallable implements ICallable {

    private int _arity;

    private boolean _isVarArgs;
    private Method method;

    private Object instance;

    public NativeCallable(Method method) {
        this(method, false);
    }

    public NativeCallable(Method method, boolean _isVarArgs) {
        _arity = method.getParameterCount();
        this._isVarArgs = _isVarArgs;
        this.method = method;
    }

    public NativeCallable(Object instance, Method method) {
        _arity = method.getParameterCount();
        this.method = method;
        this.instance = instance;
    }


    @Override
    public int arity() {
        return 0;
    }

    @Override
    public boolean isVarArgs() {
        return _isVarArgs;
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> arguments) {
        try {
            Object[] args=isVarArgs()?new Object[]{arguments}:arguments.toArray();
            return method.invoke(instance, args);
        } catch (Exception e) {
            throw new RuntimeError(e);
        }
    }
}
