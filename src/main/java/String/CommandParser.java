package String;

import java.util.ArrayList;
import java.util.List;

public class CommandParser {
    public static List<String> parseStr(String s) {
        List<String> ans=new ArrayList<>();
        StringBuilder cur= new StringBuilder();
        boolean isQuote=false;
        for(int i=0;i<s.length();i++)
        {
            if(s.charAt(i)=='\"')
            {
                if(isQuote)
                {
                    ans.add(cur.toString());
                    cur = new StringBuilder();
                    isQuote=false;
                    if(i+1<s.length()&&s.charAt(i+1)!=' ')throw new RuntimeException("Parts must be separated with spaces.");
                    i++;
                }
                else isQuote=true;

            }
            else if(s.charAt(i)=='\\')
            {
                if(i+1==s.length()) cur.append('\\');
                else
                {
                    switch(s.charAt(i+1))
                    {
                        case 'n':
                            cur.append('\n');
                            i++;
                            break;
                        case 't':
                            cur.append('\t');
                            i++;
                            break;
                        case 'b':
                            cur.append('\b');
                            i++;
                            break;
                        case 'r':
                            cur.append('\r');
                            i++;
                            break;
                        case 'f':
                            cur.append('\f');
                            i++;
                            break;
                        case '\\':
                            cur.append('\\');
                            i++;
                            break;
                        case '\"':
                            cur.append('\"');
                            i++;
                            break;
                        case '\'':
                            cur.append('\'');
                            i++;
                            break;
                        default:
                            cur.append('\\');
                    }
                }
            }
            else if(s.charAt(i)==' ')
            {
                if(isQuote) cur.append(' ');
                else {
                    if(cur.length() > 0)ans.add(cur.toString());
                    cur = new StringBuilder();
                }
            }
            else
            {
                cur.append(s.charAt(i));
            }
        }
        if(isQuote)throw new RuntimeException("Incomplete quotes!");
        if(cur.length() > 0)ans.add(cur.toString());
        return ans;
    }
}
