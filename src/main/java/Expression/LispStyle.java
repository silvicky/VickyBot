package Expression;

import java.util.HashMap;
import java.util.Map;

public class LispStyle {
    static Map<String,Long> valMap;
    public static int obtainPkg(String expression,int index) throws ExpressionErr {
        int bracketLev=1;
        for(int i=index+1;i<expression.length();i++)
        {
            if(expression.charAt(i)=='(')bracketLev++;
            if(expression.charAt(i)==')')bracketLev--;
            if(bracketLev==0)
            {
                return i;
            }
        }
        throw new ExpressionErr("Incomplete brackets!");
    }
    public static long parsePkg(String expression) throws ExpressionErr {
        if(expression.substring(1).startsWith("add"))return parseAdd(expression);
        if(expression.substring(1).startsWith("mult"))return parseMult(expression);
        if(expression.substring(1).startsWith("let"))return parseLet(expression);
        throw new ExpressionErr("Unknown command: "+expression.substring(1,expression.indexOf(" ")));
    }
    public static long parseFirst(String expression) throws ExpressionErr {
        if(expression.charAt(0)=='(')return parsePkg(expression.substring(0,obtainPkg(expression,0)+1));
        else if((expression.charAt(0)>='a'&&expression.charAt(0)<='z')||(expression.charAt(0)>='A'&&expression.charAt(0)<='Z'))
        {
            if(expression.contains(" "))return valMap.get(expression.substring(0,expression.indexOf(" ")));
            else return valMap.get(expression.substring(0,expression.indexOf(")")));
        }
        else if(expression.charAt(0)>='0'&&expression.charAt(0)<='9')
        {
            if(expression.contains(" "))return Long.parseLong(expression.substring(0,expression.indexOf(" ")));
            else return Long.parseLong(expression.substring(0,expression.indexOf(")")));
        }
        throw new ExpressionErr("Unknown expression: "+expression.substring(0,expression.indexOf(" ")));
    }
    public static long parseAdd(String expression) throws ExpressionErr {
        long val1,val2;
        expression=expression.substring(5);
        val1=parseFirst(expression);
        if(expression.charAt(0)=='(')expression=expression.substring(obtainPkg(expression,0)+2);
        else expression=expression.substring(expression.indexOf(" ")+1);
        val2=parseFirst(expression);
        return val1+val2;
    }
    public static long parseMult(String expression) throws ExpressionErr {
        long val1,val2;
        expression=expression.substring(6);
        val1=parseFirst(expression);
        if(expression.charAt(0)=='(')expression=expression.substring(obtainPkg(expression,0)+2);
        else expression=expression.substring(expression.indexOf(" ")+1);
        val2=parseFirst(expression);
        return val1*val2;
    }
    public static long parseLet(String expression) throws ExpressionErr {
        expression=expression.substring(5);
        Map<String, Long> bkpMap = new HashMap<>(valMap);
        while (!(expression.startsWith("(") && (obtainPkg(expression, 0) == expression.length() - 2||obtainPkg(expression, 0) == expression.length() - 3)) && expression.contains(" ")) {
            String valName = expression.substring(0, expression.indexOf(" "));
            expression = expression.substring(expression.indexOf(" ") + 1);
            valMap.put(valName, parseFirst(expression));
            if (expression.charAt(0) == '(') expression = expression.substring(obtainPkg(expression, 0) + 2);
            else expression = expression.substring(expression.indexOf(" ") + 1);
        }
        long ans=parseFirst(expression);
        valMap.clear();
        valMap.putAll(bkpMap);
        return ans;
    }
    public static String deBlank(String src)
    {
        StringBuilder ans= new StringBuilder();
        for(int i=0;i<src.length();)
        {
            ans.append(src.charAt(i));
            if(src.charAt(i)=='('||src.charAt(i)==' ')
            {
                do{i++;}
                while(src.charAt(i)==' ');
            }
            else i++;
        }
        return ans.toString();
    }
    public static long evaluate(String expression) throws ExpressionErr {
        valMap = new HashMap<>();
        return parsePkg(deBlank(expression));
    }
}