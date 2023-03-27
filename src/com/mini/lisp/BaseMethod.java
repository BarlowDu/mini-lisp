package com.mini.lisp;




import com.mini.lisp.utils.ObjectUtil;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

public class BaseMethod {

    final static Map<Class, Integer> mapNumberPriority;

    static {
        mapNumberPriority = new HashMap<>();
        mapNumberPriority.put(Integer.class, 1);
        mapNumberPriority.put(Long.class, 2);
        mapNumberPriority.put(BigDecimal.class, 3);
    }

    @Func(name = {"+"}, isVarArgs = true)
    public static Object add(List<Object> args) {
        return operateNumberArgs(BigDecimal.ZERO,Integer.class, args, (a, b) -> a.add(b));
    }



    @Func(name = {"-"}, isVarArgs = true)
    public static Object sub(List<Object> args) {
        Object firstArg=args.get(0);
        if(firstArg==null){
            //TODO throw
        }
        List<Object> next=args.subList(1,args.size());
        Integer priority=mapNumberPriority.get(firstArg.getClass());
        if(priority==null){
            //TODO throw
        }
        BigDecimal firstNum=convertDecimalByPriority(priority,firstArg);
        return operateNumberArgs(firstNum,firstArg.getClass(),next,(a,b)->a.subtract(b));

    }

    @Func(name = {"*"}, isVarArgs = true)
    public static Object mul(List<Object> args) {
        return operateNumberArgs(BigDecimal.ONE,Integer.class, args, (a, b) -> a.multiply(b));
    }

    @Func(name = {"/"},  isVarArgs = true)
    public static Object div(List<Object> args) {
        Object firstArg=args.get(0);
        if(firstArg==null){
            //TODO throw
        }
        List<Object> next=args.subList(1,args.size());
        Integer priority=mapNumberPriority.get(firstArg.getClass());
        if(priority==null){
            //TODO throw
        }
        BigDecimal firstNum=convertDecimalByPriority(priority,firstArg);
        return operateNumberArgs(firstNum,firstArg.getClass(),next,(a,b)->a.divide(b,4, BigDecimal.ROUND_HALF_UP).stripTrailingZeros());
    }


    private static Object operateNumberArgs(BigDecimal start,Class  _targetType, List<Object> args, BinaryOperator<BigDecimal> fun) {
        Class targetType = _targetType;
        Integer targetPriority = mapNumberPriority.get(targetType);
        if(targetPriority==null){
            //TODO throw
        }
        BigDecimal result = start;
        for (Object arg : args) {
            if (arg == null) {
                //TODO throw
            }
            Integer priority = mapNumberPriority.get(arg.getClass());
            if (priority == null) {
                //TODO throw
            }
            if (priority > targetPriority) {
                targetPriority = priority;
                targetType = arg.getClass();
            }
            BigDecimal item =convertDecimalByPriority(priority,arg);

            result = fun.apply(result,item);
        }
        if (targetType == Integer.class) {
            return result.intValue();
        }
        if (targetType == Long.class) {
            return result.longValue();
        }
        return result;
    }
    private static BigDecimal convertDecimalByPriority(int priority,Object num){
        switch (priority) {
            case 1:
                return BigDecimal.valueOf((Integer) num);
            case 2:
                return BigDecimal.valueOf((Long) num);
            default:
                return (BigDecimal)num;
        }
    }

    @Func(name = {"=="}, isVarArgs = false)
    public static boolean eq(Object arg1, Object arg2) {
        return ObjectUtil.equal(arg1, arg2);

    }


    @Func(name = {">"}, isVarArgs = false)
    public static boolean gt(Object arg1, Object arg2) {
        if (arg1 == null || arg2 == null) {
            //todo
        }
        if (!(arg1 instanceof Comparable) || !(arg2 instanceof Comparable)) {
            //todo
        }
        int compare = ((Comparable) arg1).compareTo(arg2);
        return compare > 0;
    }

    @Func(name = {"<"}, isVarArgs = false)
    public static boolean lt(Object arg1, Object arg2) {
        if (arg1 == null || arg2 == null) {
            //todo
        }
        if (!(arg1 instanceof Comparable) || !(arg2 instanceof Comparable)) {
            //todo
        }
        int compare = ((Comparable) arg1).compareTo(arg2);
        return compare < 0;

    }

    @Func(name = {">="}, isVarArgs = false)
    public static boolean gte(Object arg1, Object arg2) {
        if (arg1 == null || arg2 == null) {
            //todo
        }
        if (!(arg1 instanceof Comparable) || !(arg2 instanceof Comparable)) {
            //todo
        }
        int compare = ((Comparable) arg1).compareTo(arg2);
        return compare >= 0;

    }

    @Func(name = {"<="}, isVarArgs = false)
    public static boolean lte(Object arg1, Object arg2) {
        if (arg1 == null || arg2 == null) {
            //todo
        }
        if (!(arg1 instanceof Comparable) || !(arg2 instanceof Comparable)) {
            //todo
        }
        int compare = ((Comparable) arg1).compareTo(arg2);
        return compare <= 0;

    }

    @Func(name = {"and"}, isVarArgs = true)
    public static boolean and(List<Object> args) {
        if (args == null || args.size() < 0) {
            //todo
        }
        for (Object condition : args) {
            if (condition == null || !(condition instanceof Boolean)) {
                //todo
            }
            if (ObjectUtil.equal(condition, false)) {
                return false;
            }
        }
        return true;
    }

    @Func(name = {"or"}, isVarArgs = true)
    public static boolean or(List<Object> args) {
        if (args == null || args.size() < 0) {
            //todo
        }
        for (Object condition : args) {
            if (condition == null || !(condition instanceof Boolean)) {
                //todo
            }
            if (ObjectUtil.equal(condition, true)) {
                return true;
            }
        }
        return false;
    }

    @Func(name = {"not"}, isVarArgs = false)
    public static boolean not(Object condition) {
        if (condition == null || !(condition instanceof Boolean)) {
            //todo
        }
        return !((Boolean) condition);
    }

    @Func(name = {"if"}, isVarArgs = false)
    public static Object ifExpr(Object condition, Object factor1, Object factor2) {
        if (!(condition instanceof Boolean)) {
            //todo
        }
        if (ObjectUtil.equal(condition, true)) {
            return factor1;
        }
        return factor2;
    }

    @Func(name = {"is-null"}, isVarArgs = false)
    public static boolean isNull(Object arg){
        return arg==null;
    }

    @Func(name = {"str"}, isVarArgs = false)
    public static String str(Object arg){
        return arg==null?null:arg.toString();
    }


    @Func(name = {"append-str"}, isVarArgs = true)
    public static String appendStr(List<Object> args){
        StringBuilder result=new StringBuilder();
        if(args==null){
            return result.toString();
        }
        for(Object arg:args){
            if(arg!=null) {
                result.append(arg);
            }
        }
        return result.toString();

    }

    @Func(name = {"list"}, isVarArgs = true)
    public static List list(List<Object>  args){
        return args;
    }

    @Func(name = {"in-list"}, isVarArgs = false)
    public static boolean inList(Object item,List list){
        for(Object arg:list){
            if(ObjectUtil.equal(arg,item)){
                return true;
            }
        }
        return false;

    }

    @Func(name = {"now"}, isVarArgs = false)
    public static Date now(){
        return new Date();
    }



}
