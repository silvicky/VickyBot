package Picture;

import Utility.CustomPair;

import java.awt.*;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import static Picture.Screenshot.*;

public class StringFrag {
    static CustomPair<List<String>,Integer> stringFragPerLine(String source)
    {
        List<String>ans=new ArrayList<>();
        if(source.length()==0)source=" ";
        int lastRul=0,maxVal=0;
        TextLayout textLayout=new TextLayout(source,font, frc);
        double scope=font.getSize()/textLayout.getBounds().getHeight();
        for(int i=0;i<source.length();i++)
        {
            textLayout=new TextLayout(source.substring(lastRul,i+1),font,frc);
            Rectangle2D rectangle2D=textLayout.getBounds();
            if((int)(rectangle2D.getWidth()*scope)>maxLen)
            {
                ans.add(source.substring(lastRul,i));
                lastRul=i;
            }
            else
            {
                maxVal=Math.max(maxVal,(int)(rectangle2D.getWidth()*scope));
            }
        }
        textLayout=new TextLayout(source.substring(lastRul),font,frc);
        Rectangle2D rectangle2D=textLayout.getBounds();
        maxVal=Math.max(maxVal,(int)(rectangle2D.getWidth()*scope));
        ans.add(source.substring(lastRul));
        return new CustomPair<>(ans,maxVal);
    }
    public static CustomPair<List<String>,Integer> stringFrag(String source)
    {
        List<String>ans=new ArrayList<>();
        CustomPair<List<String>,Integer> tmp;
        int maxVal=0;
        if(source==null||source.length()==0)source=" ";
        while(source.contains("\n"))
        {
            tmp=stringFragPerLine(source.substring(0,source.indexOf("\n")));
            ans.addAll(tmp.val1);
            maxVal=Math.max(maxVal,tmp.val2);
            source=source.substring(source.indexOf("\n")+1);
        }
        if(source.length()==0)source=" ";
        tmp=stringFragPerLine(source);
        ans.addAll(tmp.val1);
        maxVal=Math.max(maxVal,tmp.val2);
        return new CustomPair<>(ans,maxVal);
    }
}
