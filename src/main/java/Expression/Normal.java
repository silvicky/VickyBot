package Expression;

import java.util.Stack;

public class Normal {
    public static double calculate(String s) throws ExpressionErr {
        Stack<Double> ans=new Stack<>();
        Stack<Double> last=new Stack<>();
        Stack<Boolean> mode=new Stack<>();
        ans.push(0.0);
        last.push(1.0);
        mode.push(false);
        double cur=0;
        char c;
        boolean isAfterDot=false;
        double curScale=0;
        for(int i=0;i<s.length();i++)
        {
            c=s.charAt(i);
            if(c==' '||c=='\n'||c=='\r'||c=='\t')continue;
            if(c<='9'&&c>='0')
            {
                if(isAfterDot)
                {
                    c -= '0';
                    cur+=curScale*c;
                    curScale/=10;
                }
                else
                {
                    c -= '0';
                    cur *= 10;
                    cur += c;
                }
            }
            else if(c=='.')
            {
                if(isAfterDot)throw new ExpressionErr("Excessive dots!");
                isAfterDot=true;
                curScale=0.1;
            }
            else if(c=='(')
            {
                isAfterDot=false;
                ans.push(0.0);
                last.push(1.0);
                mode.push(false);
            }
            else
            {
                isAfterDot=false;
                if(mode.peek()) last.push(last.pop()/cur);
                else last.push(last.pop()*cur);
                cur=0;
                if(c==')')
                {
                    cur=ans.pop()+last.pop();
                    mode.pop();
                    if(ans.empty())throw new ExpressionErr("Incomplete brackets!");
                }
                else if(c=='+')
                {
                    ans.push(ans.pop()+last.pop());
                    last.push(1.0);
                    mode.pop();
                    mode.push(false);
                }
                else if(c=='-')
                {
                    ans.push(ans.pop()+last.pop());
                    last.push(-1.0);
                    mode.pop();
                    mode.push(false);
                }
                else if(c=='*')
                {
                    mode.pop();
                    mode.push(false);
                }
                else if(c=='/')
                {
                    mode.pop();
                    mode.push(true);
                }
            }
        }
        if(mode.peek()) last.push(last.pop()/cur);
        else last.push(last.pop()*cur);
        return ans.peek()+last.peek();
    }
}
