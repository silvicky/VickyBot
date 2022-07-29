package Picture;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StringFrag {
    Font font;
    int maxLen;
    public StringFrag(Font font,int maxLen)
    {
        this.font=font;
        this.maxLen=maxLen;
    }
    List<String> stringFragPerLine(String source)
    {
        List<String>ans=new ArrayList<>();
        int lastRul=0;
        BufferedImage img=new BufferedImage(100,100,2);
        Graphics2D graphic=GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(img);
        FontRenderContext fontRenderContext=graphic.getFontRenderContext();
        TextLayout textLayout=new TextLayout(source,font,fontRenderContext);
        double scope=font.getSize()/textLayout.getBounds().getHeight();
        for(int i=0;i<source.length();i++)
        {
            textLayout=new TextLayout(source.substring(lastRul,i+1),font,fontRenderContext);
            Rectangle2D rectangle2D=textLayout.getBounds();
            if(Math.floor(rectangle2D.getWidth()*scope)>maxLen)
            {
                ans.add(source.substring(lastRul,i));
                lastRul=i;
            }
        }
        ans.add(source.substring(lastRul));
        return ans;
    }
    public List<String> stringFrag(String source)
    {
        List<String>ans=new ArrayList<>();
        List<String>tmp;
        while(source.contains("\n"))
        {
            tmp=stringFragPerLine(source.substring(0,source.indexOf("\n")));
            ans.addAll(tmp);
            source=source.substring(source.indexOf("\n")+1);
        }
        tmp=stringFragPerLine(source);
        ans.addAll(tmp);
        return ans;
    }
}
