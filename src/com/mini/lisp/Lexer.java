package com.mini.lisp;



import com.mini.lisp.exception.LangException;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {
    public static String regexPat
            = "\\s*((;.*)|([0-9]+\\.[0-9]+)|([0-9]+[l]?)|(\"(\\\\\"|\\\\\\\\|\\\\n|[^\"])*\")|(\\(|\\))|(<=|>=|\\+|\\-|\\*|\\/|<|>|=)|[A-Z_a-z][A-Z_\\-a-z0-9]*)?";
    private Pattern pattern = Pattern.compile(regexPat);

    private String source;
    private LineNumberReader reader;

    private final List<Token> tokens = new ArrayList<>();

    public Lexer(String source) {
        this.source = source;
        this.reader = new LineNumberReader(new StringReader(source));
    }

    public List<Token> scanTokens() throws Exception {
        String line;
        while ((line = reader.readLine()) != null) {
            int lineNo = reader.getLineNumber();
            Matcher matcher = pattern.matcher(line);
            matcher.useTransparentBounds(true).useAnchoringBounds(false);
            int pos = 0;
            int endPos = line.length();
            while (pos < endPos) {
                if(!matchSplit(line,pos)){
                    throw new LangException("bad token at line " + lineNo);
                }
                matcher.region(pos, endPos);
                if (matcher.lookingAt()) {
                    addToken(lineNo, matcher);
                    if (pos == matcher.end()) {
                        throw new LangException("bad token at line " + lineNo);
                    }
                    pos = matcher.end();
                } else
                    throw new LangException("bad token at line " + lineNo);
            }

        }
        return tokens;
    }

    /**
     * 判断两个token间是否包含隔离符
     * @param pos
     * @return
     */
    private boolean matchSplit(String line,int pos){
        if(pos<=0){
            return true;
        }
        char prev=line.charAt(pos-1);
        char curren=line.charAt(pos);
        if(prev=='('||prev==')'){
            return true;
        }
        if(curren=='('||curren==')'||curren==' '||curren=='\t'||curren=='\r'||curren=='\n'){
            return true;
        }
        return false;
    }
    protected void addToken(int lineNo, Matcher matcher) {
        String m = matcher.group(1);
        if (m != null) // if not a space
            if (matcher.group(2) == null) { // if not a comment
                Token token;
                if (matcher.group(3) != null)
                    token = new Token(TokenType.DECIMAL, m, new BigDecimal(m), lineNo);
                else if (matcher.group(4) != null)
                    token = getIntegerToken(m,lineNo);
                else if (matcher.group(5) != null)
                    token = new Token(TokenType.STRING, m, toStringLiteral(m), lineNo);
                else if (matcher.group(7) != null)
                    token = getParenToken(m,lineNo);
                else if (matcher.group(8) != null)
                    token = new Token(TokenType.IDENTIFIER, m, m, lineNo);
                else
                    token = getIdentifierToken(m,lineNo);
                tokens.add(token);
            }
    }

    private Token getIntegerToken(String lexeme, int lineNo){
        if(lexeme.endsWith("l")){
            return new Token(TokenType.LONG, lexeme, Long.parseLong(lexeme.substring(0,lexeme.length()-1)), lineNo);
        }
        return new Token(TokenType.INTEGER, lexeme, Integer.parseInt(lexeme), lineNo);
    }

    private Token getParenToken(String lexeme, int lineNo) {
        if ("(".equals(lexeme)) {
            return new Token(TokenType.LEFT_PAREN, lexeme, null, lineNo);
        }
        return new Token(TokenType.RIGHT_PAREN, lexeme, null, lineNo);

    }

    private Token getIdentifierToken(String lexeme, int lineNo) {
        if ("true".equals(lexeme)) {
            return new Token(TokenType.TRUE, lexeme, true, lineNo);
        }
        if ("false".equals(lexeme)) {
            return new Token(TokenType.FALSE, lexeme, false, lineNo);
        }
        if ("null".equals(lexeme)) {
            return new Token(TokenType.NULL, lexeme, null, lineNo);
        }
        return new Token(TokenType.IDENTIFIER, lexeme, lexeme, lineNo);
    }


    protected String toStringLiteral(String s) {
        StringBuilder sb = new StringBuilder();
        int len = s.length() - 1;
        for (int i = 1; i < len; i++) {
            char c = s.charAt(i);
            if (c == '\\' && i + 1 < len) {
                char c2 = s.charAt(i + 1);
                if (c2 == '"' || c2 == '\\')
                    c = s.charAt(++i);
                else if (c2 == 'n') {
                    ++i;
                    c = '\n';
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }


}
