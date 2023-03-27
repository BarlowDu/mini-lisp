package com.mini.lisp;

import java.util.List;

public interface Engine {


    Object run(List<Expr> exprs);
}
