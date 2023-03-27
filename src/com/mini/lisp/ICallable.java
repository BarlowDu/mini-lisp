package com.mini.lisp;

import java.util.List;

public interface ICallable {

    //isVarArgs
    //> callable-arity
    int arity();

    //是否为动态参数
    boolean isVarArgs();
    //< callable-arity
    Object call(Interpreter interpreter, List<Object> arguments);
}
