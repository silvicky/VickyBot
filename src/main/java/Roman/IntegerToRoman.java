package Roman;
public class IntegerToRoman {
    public static String intToRoman(int num) throws ExpressionErr {
        if(num>=4000){throw new ExpressionErr("Too large! Integer must be smaller than 4000.");}
        String ans="";
        int div=num/1000;
        for(int i=0;i<div;i++)
        {
            ans+="M";
        }
        num-=div*1000;
        if(num>=900)
        {
            ans+="CM";
            num-=900;
        }
        else if(num>=500)
        {
            ans+="D";
            num-=500;
        }
        else if(num>=400)
        {
            ans+="CD";
            num-=400;
        }

        div=num/100;
        for(int i=0;i<div;i++)
        {
            ans+="C";
        }
        num-=div*100;
        if(num>=90)
        {
            ans+="XC";
            num-=90;
        }
        else if(num>=50)
        {
            ans+="L";
            num-=50;
        }
        else if(num>=40)
        {
            ans+="XL";
            num-=40;
        }

        div=num/10;
        for(int i=0;i<div;i++)
        {
            ans+="X";
        }
        num-=div*10;
        if(num>=9)
        {
            ans+="IX";
            num-=9;
        }
        else if(num>=5)
        {
            ans+="V";
            num-=5;
        }
        else if(num>=4)
        {
            ans+="IV";
            num-=4;
        }

        for(int i=0;i<num;i++)
        {
            ans+="I";
        }

        return ans;
    }
}