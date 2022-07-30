package Picture;

import org.checkerframework.checker.units.qual.C;
import org.glassfish.jersey.internal.util.collection.StringIgnoreCaseKeyComparator;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class Screenshot
{
    static int fontHeight=12;
    static int width=512;
    static int split=4;
    static int avatarSize=32;
    static int padding=12;
    static int msgSplit=2;
    static int height;
    public static BufferedImage screenshot(List<Message> msg, long target) throws IOException, FontFormatException {
        //Font font=Font.createFont(Font.TRUETYPE_FONT, new File("C:\\Users\\Vicky\\Desktop\\bot-jar\\cfg\\DroidSansFallback.ttf")).deriveFont(Font.PLAIN,fontHeight);
        //Font font=Font.createFont(Font.TYPE1_FONT, new File("C:\\Users\\Vicky\\Desktop\\bot-jar\\cfg\\simsun.ttc")).deriveFont(Font.PLAIN,fontHeight);
        Font font=new Font("SimSun",Font.PLAIN,12);
        height=msgSplit;
        List<String>[] frag=new List[msg.size()];
        int[] msgWidth=new int[msg.size()];
        int maxWidth=0;
        StringFrag stringFrag=new StringFrag(font,width-avatarSize-padding*5);
        long lastUser=0L;
        String name;
        TextLayout textLayout;
        BufferedImage imgX=new BufferedImage(100,100,2);
        Graphics2D graphicX=GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(imgX);
        FontRenderContext fontRenderContext=graphicX.getFontRenderContext();
        for(int i=0;i<msg.size();i++)
        {
            ListAndVal tmp=stringFrag.stringFrag(msg.get(i).getText());
            frag[i]=tmp.list;
            msgWidth[i]=tmp.val;
            if(msg.get(i).getFrom().getId()!=lastUser)
            {
                name=msg.get(i).getFrom().getFirstName();
                if(msg.get(i).getFrom().getLastName()!=null)name+=" "+msg.get(i).getFrom().getLastName();
                textLayout=new TextLayout(name,font,fontRenderContext);
                Rectangle2D rectangle2D=textLayout.getBounds();
                double scope=font.getSize()/textLayout.getBounds().getHeight();
                msgWidth[i]=Math.max(msgWidth[i],(int)(rectangle2D.getWidth()*scope));
                height+=fontHeight+split;
            }
            lastUser=msg.get(i).getFrom().getId();
            maxWidth=Math.max(maxWidth,msgWidth[i]);
            height+=padding*2-split+frag[i].size()*(fontHeight+split)+msgSplit;
        }
        width=maxWidth+padding*5+avatarSize;
        BufferedImage image=new BufferedImage(width,height,2);
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        Graphics2D graphic=ge.createGraphics(image);
        graphic.setBackground(Color.orange);
        graphic.clearRect(0,0,width,height);
        graphic.setBackground(Color.white);
        graphic.setPaint(Color.black);
        graphic.setFont(font);
        int cumuH,curH;
        cumuH=msgSplit;
        lastUser=0L;
        for(int i=0;i<msg.size();i++)
        {
            curH=padding*2-split+frag[i].size()*(fontHeight+split);
            if(msg.get(i).getFrom().getId()!=lastUser)
            {
                curH+=fontHeight+split;
            }
            graphic.clearRect(avatarSize+padding*2,cumuH,msgWidth[i]+2*padding,curH);
            int ix=avatarSize+padding*3,iy=padding+fontHeight+cumuH-1;
            if(msg.get(i).getFrom().getId()!=lastUser)
            {
                graphic.setPaint(Color.magenta);
                name=msg.get(i).getFrom().getFirstName();
                if(msg.get(i).getFrom().getLastName()!=null)name+=" "+msg.get(i).getFrom().getLastName();
                graphic.drawString(name,ix,iy);
                graphic.setPaint(Color.black);
                iy+=fontHeight+split;
            }
            lastUser=msg.get(i).getFrom().getId();
            for(int j=0;j<frag[i].size();j++)
            {
                graphic.drawString(frag[i].get(j),ix,iy);
                iy+=fontHeight+split;
            }
            cumuH+=curH+msgSplit;
        }
        ImageIO.write(image,"png",new File("C:\\Users\\Vicky\\Desktop\\bot-jar\\cfg\\a.png"));
        return image;
    }
}