package String;

public class StringExp {
    static final int maxLen=4000;
    static int obtainPkg(String s,int ind) throws ExpressionErr {
        int bracketLvl=0;
        for(int i=ind;i<s.length();i++)
        {
            if(s.charAt(i)=='[')bracketLvl++;
            if(s.charAt(i)==']')bracketLvl--;
            if(bracketLvl==0)return i;
        }
        throw new ExpressionErr("Incomplete brackets!");
    }
    static String parsePkg(String s) throws ExpressionErr {
        //System.out.println(s);
        String ans="",unit;
        int tmp;
        for(;s.length()>0;)
        {
            if(!(s.charAt(0)<='9'&&s.charAt(0)>='0'))
            {
                ans+=s.charAt(0);
                s=s.substring(1);
            }
            else
            {
                tmp=Integer.parseInt(s.substring(0,s.indexOf("[")));
                unit=parsePkg(s.substring(s.indexOf("[")+1,obtainPkg(s,s.indexOf("["))));
                for(int i=0;i<tmp;i++)
                {
                    ans+=unit;
                    if(ans.length()>maxLen)throw new ExpressionErr("Answer is too long!");
                }
                s=s.substring(obtainPkg(s,s.indexOf("["))+1);
            }
        }
        if(ans.length()>maxLen)throw new ExpressionErr("Answer is too long!");
        return ans;
    }
    public static String decodeString(String s) throws ExpressionErr {
        return parsePkg(s);
    }
}