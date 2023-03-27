import com.mini.lisp.*;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        String source="(define cons  (lambda (x y) (lambda (p) (p x y))))\n" +
                "(define car (lambda (p) (p (lambda (x y) x))))\n" +
                "(define cdr (lambda (p) (p (lambda (x y) y))))\n" +
                "(define p (cons 10 22))\n" +
                "(+ (car p) (cdr p))";
        Lexer lexer=new Lexer(source);
        List<Expr> exprs=new Parser().stmt(lexer.scanTokens());
        Interpreter interpreter=new Interpreter(getBaseMethods());
        Resolver resolver=new Resolver(interpreter);
        resolver.resolve(exprs);
        Object result=interpreter.interpret(exprs);
        System.out.println(result);

        lexer=new Lexer("(car p)");
        exprs=new Parser().stmt(lexer.scanTokens());
        resolver.resolve(exprs);
        result=interpreter.interpret(exprs);
        System.out.println(result);
    }

    protected static Map<String,Object> getBaseMethods(){
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
}