package Expression;

import java.util.Stack;

public class Normal {
    public static long calculate(String s) throws ExpressionErr {
        Stack<Long> ans=new Stack<>();
        Stack<Long> last=new Stack<>();
        Stack<Boolean> mode=new Stack<>();
        ans.push(0L);
        last.push(1L);
        mode.push(false);
        long cur=0;
        char c;
        for(int i=0;i<s.length();i++)
        {
            c=s.charAt(i);
            if(c==' '||c=='\n'||c=='\r'||c=='\t')continue;
            if(c<='9'&&c>='0')
            {
                c-='0';
                cur*=10;
                cur+=c;
            }
            else if(c=='(')
            {
                ans.push(0L);
                last.push(1L);
                mode.push(false);
            }
            else
            {
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
                    last.push(1L);
                    mode.pop();
                    mode.push(false);
                }
                else if(c=='-')
                {
                    ans.push(ans.pop()+last.pop());
                    last.push(-1L);
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
