package com.mini.lisp.utils;

public class ObjectUtil {

    public static boolean equal(Object obj1, Object obj2) {
        return (obj1 == obj2) || (obj1 != null && obj1.equals(obj2));
    }
}
