package Roman;
public class IntegerToEnglish {
    static String perThousand(int num)
    {
        String ans="";
        switch(num/100)
        {
            case 1:
                ans+="One";
                break;
            case 2:
                ans+="Two";
                break;
            case 3:
                ans+="Three";
                break;
            case 4:
                ans+="Four";
                break;
            case 5:
                ans+="Five";
                break;
            case 6:
                ans+="Six";
                break;
            case 7:
                ans+="Seven";
                break;
            case 8:
                ans+="Eight";
                break;
            case 9:
                ans+="Nine";
        }
        if(num>=100)
        {
            ans+=" Hundred";
        }
        if(num>=100&&num%100!=0)ans+=" ";
        num%=100;

        switch(num/10)
        {
            case 2:
                ans+="Twenty";
                break;
            case 3:
                ans+="Thirty";
                break;
            case 4:
                ans+="Forty";
                break;
            case 5:
                ans+="Fifty";
                break;
            case 6:
                ans+="Sixty";
                break;
            case 7:
                ans+="Seventy";
                break;
            case 8:
                ans+="Eighty";
                break;
            case 9:
                ans+="Ninety";
        }
        if(num>=20&&num%10!=0)ans+=" ";
        if(num>=20)num%=10;


        switch(num)
        {
            case 1:
                ans+="One";
                break;
            case 2:
                ans+="Two";
                break;
            case 3:
                ans+="Three";
                break;
            case 4:
                ans+="Four";
                break;
            case 5:
                ans+="Five";
                break;
            case 6:
                ans+="Six";
                break;
            case 7:
                ans+="Seven";
                break;
            case 8:
                ans+="Eight";
                break;
            case 9:
                ans+="Nine";
                break;
            case 10:
                ans+="Ten";
                break;
            case 11:
                ans+="Eleven";
                break;
            case 12:
                ans+="Twelve";
                break;
            case 13:
                ans+="Thirteen";
                break;
            case 14:
                ans+="Fourteen";
                break;
            case 15:
                ans+="Fifteen";
                break;
            case 16:
                ans+="Sixteen";
                break;
            case 17:
                ans+="Seventeen";
                break;
            case 18:
                ans+="Eighteen";
                break;
            case 19:
                ans+="Nineteen";
        }
        return ans;
    }
    public static String numberToWords(int num) {
        String ans="";
        if(num==0)return "Zero";
        if(num<0)
        {
            ans+="Negative ";
            num=-num;
        }
        if(num/1000000000!=0)
        {
            ans+=perThousand(num/1000000000);
            ans+=" Billion";
            num%=1000000000;
            if(num!=0)ans+=" ";
        }
        if(num/1000000!=0)
        {
            ans+=perThousand(num/1000000);
            ans+=" Million";
            num%=1000000;
            if(num!=0)ans+=" ";
        }
        if(num/1000!=0)
        {
            ans+=perThousand(num/1000);
            ans+=" Thousand";
            num%=1000;
            if(num!=0)ans+=" ";
        }
        ans+=perThousand(num);
        if(ans.length()==0)ans+="Zero";
        return ans;
    }
}