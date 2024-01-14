package Picture;

import MessageParsing.FakeMsg;
import Utility.CustomPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static Main.Main.bot;
import static Picture.StringFrag.stringFrag;
import static java.lang.Math.random;

public class Screenshot
{
    static int fontHeight=36;
    public static int maxWidth=512;
    static int split=8;
    public static int avatarSize=64;
    public static int padding=24;
    static int paddingVertical=12;
    static int msgSplit=4;
    static int maxLen=maxWidth-avatarSize-padding*6;
    static Graphics2D xG2D;
    public static FontRenderContext frc;
    public static Font font;
    static Logger logger= LoggerFactory.getLogger(Screenshot.class);
    public static void sendImageAsSticker(BufferedImage img,long target) throws Exception {
        InputFile inputFile=new InputFile();
        int ran=(int)(random()*1000000000);
        File tmp=new File("./cache/"+ran+".png");
        ImageIO.write(img,"png",tmp);
        SendSticker sendSticker= SendSticker.builder().sticker(inputFile).chatId(Long.toString(target)).build();
        inputFile.setMedia(tmp, String.valueOf(ran));
        try
        {
            bot.execute(sendSticker);
            logger.info("Sent screenshot to: "+target);
        }
        catch(Exception e)
        {
            logger.error(e.toString());
        }
        tmp.delete();
    }
    public static int renderMsg(Graphics2D graphic,List<String> frag,int y,int msgWidth,boolean withHead,String avatar,String name) throws IOException {
        int curH=paddingVertical*2-split+frag.size()*(fontHeight+split);
        if(withHead)
        {
            curH+=fontHeight+split;
        }
        graphic.clearRect(avatarSize+padding*2,y,msgWidth+3*padding,curH);
        int ix=avatarSize+padding*3,iy=paddingVertical+fontHeight+y-1;
        if(withHead)
        {
            BufferedImage avatarCur;
            try {
                avatarCur = ImageIO.read(new File(avatar));
                double avatarScope = (double) avatarSize / avatarCur.getHeight();
                graphic.drawImage(avatarCur, new AffineTransformOp(new AffineTransform(avatarScope, 0, 0, avatarScope, 0, 0), AffineTransformOp.TYPE_NEAREST_NEIGHBOR), padding, y);
            }catch(Exception ignored){}
            graphic.setPaint(Color.magenta);
            graphic.drawString(name,ix,iy);
            graphic.setPaint(Color.black);
            iy+=fontHeight+split;
        }
        for(int j=0;j<frag.size();j++)
        {
            graphic.drawString(frag.get(j),ix,iy);
            iy+=fontHeight+split;
        }
        return curH;
    }
    public static int renderMsg(Graphics2D graphic,String pic,int height,int width,int y,int msgWidth,String avatar) throws IOException {
        double pictureScope=(double)msgWidth/width;
        int curH= (int) (height*pictureScope);
        BufferedImage avatarCur;
        try {
            avatarCur = ImageIO.read(new File(avatar));
            double avatarScope = (double) avatarSize / avatarCur.getHeight();
            graphic.drawImage(avatarCur, new AffineTransformOp(new AffineTransform(avatarScope, 0, 0, avatarScope, 0, 0), AffineTransformOp.TYPE_NEAREST_NEIGHBOR), padding, y);
        }catch(Exception ignored){}
        BufferedImage pictureCur;
        pictureCur=ImageIO.read(new File(pic));
        graphic.drawImage(pictureCur, new AffineTransformOp(new AffineTransform(pictureScope,0,0,pictureScope,0,0),AffineTransformOp.TYPE_NEAREST_NEIGHBOR), padding*2+avatarSize, y);
        return curH;
    }
    public static void screenshot(List<Message> msg, long target) throws Exception {
        List<FakeMsg> ans=new ArrayList<>();
        for(Message i:msg)ans.addAll(FakeMsg.parse(i));
        fakeSS(ans,target);
    }
    public static BufferedImage to512(BufferedImage img)
    {
        int scope=512/img.getWidth();
        BufferedImage ans=new BufferedImage(512,img.getHeight()*scope,2);
        Graphics2D graphics2D=GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(ans);
        graphics2D.drawImage(img,new AffineTransformOp(new AffineTransform(scope,0,0,scope,0,0),AffineTransformOp.TYPE_NEAREST_NEIGHBOR),(512-img.getWidth()*scope)/2,0);
        return ans;
    }
    public static void fakeSS(List<FakeMsg> msg, long target) throws Exception {
        fontHeight=36;
        int height;
        List<String>[] frag;
        int[] msgWidth;
        long lastUser=0L,curID;
        TextLayout textLayout;
        xG2D=GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(new BufferedImage(100,100,2));
        frc=xG2D.getFontRenderContext();
        font=Font.createFont(Font.TRUETYPE_FONT,new File("./cfg/NotoSansSC-Regular.otf")).deriveFont((float)fontHeight);
        height=msgSplit;
        frag=new List[msg.size()];
        msgWidth=new int[msg.size()];
        for(int i=0;i<msg.size();i++)
        {
            curID=msg.get(i).user.userID;
            if(msg.get(i).type==FakeMsg.TEXT) {
                CustomPair<List<String>, Integer> tmp = stringFrag(msg.get(i).text);
                frag[i] = tmp.val1;
                msgWidth[i] = tmp.val2;
                if (curID != lastUser) {
                    String name;
                    name = msg.get(i).user.name;
                    textLayout = new TextLayout(name, font, frc);
                    Rectangle2D rectangle2D = textLayout.getBounds();
                    double scope = font.getSize() / textLayout.getBounds().getHeight();
                    msgWidth[i] = Math.max(msgWidth[i],Math.min(maxLen, (int) (rectangle2D.getWidth() * scope)));
                    height += fontHeight + split;
                }
                lastUser = curID;
                height += paddingVertical * 2 - split + frag[i].size() * (fontHeight + split) + msgSplit;
            }
            else if(msg.get(i).type==FakeMsg.PICTURE)
            {
                msgWidth[i] = maxWidth-avatarSize-padding*3;
                lastUser = 0L;
                height += (double)(msg.get(i).height)*msgWidth[i]/msg.get(i).width + msgSplit;
            }
        }
        if(height>512)
        {
            fontHeight=24;
            xG2D=GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(new BufferedImage(100,100,2));
            frc=xG2D.getFontRenderContext();
            font=Font.createFont(Font.TRUETYPE_FONT,new File("./cfg/NotoSansSC-Regular.otf")).deriveFont((float)fontHeight);
            height=msgSplit;
            frag=new List[msg.size()];
            msgWidth=new int[msg.size()];
            for(int i=0;i<msg.size();i++)
            {
                curID=msg.get(i).user.userID;
                if(msg.get(i).type==FakeMsg.TEXT) {
                    CustomPair<List<String>, Integer> tmp = stringFrag(msg.get(i).text);
                    frag[i] = tmp.val1;
                    msgWidth[i] = tmp.val2;
                    if (curID != lastUser) {
                        String name;
                        name = msg.get(i).user.name;
                        textLayout = new TextLayout(name, font, frc);
                        Rectangle2D rectangle2D = textLayout.getBounds();
                        double scope = font.getSize() / textLayout.getBounds().getHeight();
                        msgWidth[i] = Math.max(msgWidth[i],Math.min(maxLen, (int) (rectangle2D.getWidth() * scope)));
                        height += fontHeight + split;
                    }
                    lastUser = curID;
                    height += paddingVertical * 2 - split + frag[i].size() * (fontHeight + split) + msgSplit;
                }
                else if(msg.get(i).type==FakeMsg.PICTURE)
                {
                    msgWidth[i] = maxWidth-avatarSize-padding*3;
                    lastUser = 0L;
                    height += (double)(msg.get(i).height)*msgWidth[i]/msg.get(i).width + msgSplit;
                }
            }
        }
        BufferedImage image=new BufferedImage(maxWidth,height,2);
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        Graphics2D graphic=ge.createGraphics(image);
        graphic.setBackground(Color.white);
        graphic.setPaint(Color.black);
        graphic.setFont(font);
        int cumuH;
        cumuH=msgSplit;
        lastUser=0L;
        for(int i=0;i<msg.size();i++)
        {
            curID=msg.get(i).user.userID;
            if(msg.get(i).type==FakeMsg.TEXT) {
                if (curID != lastUser) {
                    cumuH += renderMsg(graphic, frag[i], cumuH, msgWidth[i], true, msg.get(i).user.avatar, msg.get(i).user.name);
                } else cumuH += renderMsg(graphic, frag[i], cumuH, msgWidth[i], false, null, null);
                cumuH += msgSplit;
                lastUser = curID;
            }
            else
            {
                cumuH+=renderMsg(graphic,msg.get(i).picture,msg.get(i).height,msg.get(i).width,cumuH,msgWidth[i],msg.get(i).user.avatar);
                cumuH+=msgSplit;
                lastUser=0L;
            }
        }
        sendImageAsSticker(image,target);
    }
}