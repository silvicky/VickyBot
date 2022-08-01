package String;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {
    static int nextQuote(String s,int ind)
    {
        int ans=s.indexOf('\"',ind);
        while(ans!=-1)
        {
            if(ans==0||s.charAt(ans-1)!='\\')return ans;
            ans=s.indexOf('\"',ans+1);
        }
        return -1;
    }
    static String finalProcess(String s)
    {
        return s.replace("\\\"","\"")
                .replace("\\n","\n")
                .replace("\\t","\t");
    }
    public static List<String> parseStr(String s) throws ExpressionErr {
        List<String> ans=new ArrayList<>();
        int rul=0,tmp;
        while(rul<s.length())
        {
            if(s.charAt(rul)=='\"')
            {
                tmp=nextQuote(s,rul+1);
                if(tmp+1<s.length()&&s.charAt(tmp+1)!=' ')throw new ExpressionErr("Wrong command format at index "+rul);
                ans.add(finalProcess(s.substring(rul+1,tmp)));
                rul=tmp+2;
            }
            else
            {
                if(s.indexOf(" ",rul)==-1) {
                    ans.add(finalProcess(s.substring(rul)));
                    rul=s.length();
                }
                else {
                    tmp = s.indexOf(" ",rul);
                    ans.add(finalProcess(s.substring(rul, tmp)));
                    rul = tmp + 1;
                }
            }
            while(rul<s.length()&&s.charAt(rul)==' ')rul++;
        }
        return ans;
    }
}
