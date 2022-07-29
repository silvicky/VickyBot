package Roman;
public class RomanToInteger {
    public static int romanToInt(String s) {
        int[] dat=new int[20];
        for(int i=0;i<s.length();i++)
        {
            switch(s.charAt(i))
            {
                case 'I':
                    dat[i]=1;
                    break;
                case 'V':
                    dat[i]=5;
                    break;
                case 'X':
                    dat[i]=10;
                    break;
                case 'L':
                    dat[i]=50;
                    break;
                case 'C':
                    dat[i]=100;
                    break;
                case 'D':
                    dat[i]=500;
                    break;
                case 'M':
                    dat[i]=1000;
            }
        }
        for(int i=0;i<s.length()-1;i++)
        {
            if(dat[i]<dat[i+1])
            {
                dat[i+1]-=dat[i];
                dat[i]=0;
            }
        }
        int ans=0;
        for(int i=0;i<s.length();i++)
        {
            ans+=dat[i];
        }
        return ans;
    }
}