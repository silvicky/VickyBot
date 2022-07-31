package Picture;

import Main.Main;
import Utility.CustomPair;
import com.hellokaton.webp.WebpIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Main.Main.bot;
import static Main.Main.token;
import static Picture.StringFrag.stringFrag;
import static java.lang.Math.random;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Screenshot
{
    static int fontHeight=24;
    static int maxWidth=512;
    static int split=8;
    static int avatarSize=64;
    static int padding=24;
    static int msgSplit=4;
    static int maxLen=maxWidth-avatarSize-padding*5;
    static Graphics2D xG2D;
    public static FontRenderContext frc;
    public static Font font;
    static Logger logger= LoggerFactory.getLogger(Screenshot.class);
    public static void sendImageAsSticker(BufferedImage img,long target) throws Exception {
        InputFile inputFile=new InputFile();
        int ran=(int)(random()*1000000000);
        File tmp=new File("./cache/"+ran+".png");
        File tmpW=new File("./cache/"+ran+".webp");
        ImageIO.write(img,"png",tmp);
        WebpIO webpIO=new WebpIO();
        webpIO.toWEBP(tmp,tmpW);
        SendSticker sendSticker= SendSticker.builder().sticker(inputFile).chatId(Long.toString(target)).build();
        inputFile.setMedia(tmp,Long.toString(target));
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
        tmpW.delete();
    }
    public static String obtainAvatar(long userID) throws TelegramApiException, IOException {
        String ans=bot.execute(new GetUserProfilePhotos(userID,0,1)).getPhotos().get(0).get(0).getFileId();
        if(!new File("./cache/"+ans).exists())
        {
            GetFile getFile;
            getFile=new GetFile(ans);
            Files.copy(new URL("https://api.telegram.org/file/bot"+token+"/"+bot.execute(getFile).getFilePath()).openStream(),Paths.get("./cache/"+ans),REPLACE_EXISTING);
        }
        return ans;
    }
    public static int renderMsg(Graphics2D graphic,List<String> frag,int y,int msgWidth,boolean withHead,String avatar,String name) throws IOException {
        int curH=padding*2-split+frag.size()*(fontHeight+split);
        if(withHead)
        {
            curH+=fontHeight+split;
        }
        graphic.clearRect(avatarSize+padding*2,y,msgWidth+2*padding,curH);
        int ix=avatarSize+padding*3,iy=padding+fontHeight+y-1;
        if(withHead)
        {
            BufferedImage avatarCur;
            avatarCur=ImageIO.read(new File(avatar));
            double avatarScope=(double)avatarSize/avatarCur.getHeight();
            graphic.drawImage(avatarCur, new AffineTransformOp(new AffineTransform(avatarScope,0,0,avatarScope,0,0),AffineTransformOp.TYPE_NEAREST_NEIGHBOR), padding, y);
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
    public static void screenshot(List<Message> msg, long target) throws Exception {
        int width;
        int height;
        height=msgSplit;
        List<String>[] frag=new List[msg.size()];
        int[] msgWidth=new int[msg.size()];
        Map<Long,String> avatar=new HashMap<>();
        int maxWidth=0;
        long lastUser=0L,curID;
        TextLayout textLayout;
        xG2D=GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(new BufferedImage(100,100,2));
        frc=xG2D.getFontRenderContext();
        font=xG2D.getFont().deriveFont((float)fontHeight);
        for(int i=0;i<msg.size();i++)
        {
            curID=msg.get(i).getFrom().getId();
            CustomPair<List<String>,Integer> tmp=stringFrag(msg.get(i).getText());
            frag[i]=tmp.val1;
            msgWidth[i]=tmp.val2;
            avatar.put(curID,obtainAvatar(curID));
            if(curID!=lastUser)
            {
                String name;
                name=msg.get(i).getFrom().getFirstName();
                if(msg.get(i).getFrom().getLastName()!=null)name+=" "+msg.get(i).getFrom().getLastName();
                textLayout=new TextLayout(name,font,frc);
                Rectangle2D rectangle2D=textLayout.getBounds();
                double scope=font.getSize()/textLayout.getBounds().getHeight();
                msgWidth[i]=Math.max(msgWidth[i],(int)(rectangle2D.getWidth()*scope));
                height+=fontHeight+split;
            }
            lastUser=curID;
            maxWidth=Math.max(maxWidth,msgWidth[i]);
            height+=padding*2-split+frag[i].size()*(fontHeight+split)+msgSplit;
        }
        width=maxWidth+padding*5+avatarSize;
        BufferedImage image=new BufferedImage(width,height,2);
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
            curID=msg.get(i).getFrom().getId();
            if(curID!=lastUser)
            {
                String name;
                name=msg.get(i).getFrom().getFirstName();
                if(msg.get(i).getFrom().getLastName()!=null)name+=" "+msg.get(i).getFrom().getLastName();
                cumuH+=renderMsg(graphic,frag[i],cumuH,msgWidth[i],true,"./cache/"+avatar.get(curID),name);
            }
            else cumuH+=renderMsg(graphic,frag[i],cumuH,msgWidth[i],false,null,null);
            cumuH+=msgSplit;
            lastUser=curID;
        }
        sendImageAsSticker(image,target);
    }
    public static void fakeSS(List<FakeMsg> msg, long target) throws Exception {
        int width;
        int height;
        height=msgSplit;
        List<String>[] frag=new List[msg.size()];
        int[] msgWidth=new int[msg.size()];
        Map<Long,String> avatar=new HashMap<>();
        int maxWidth=0;
        long lastUser=0L,curID;
        TextLayout textLayout;
        xG2D=GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(new BufferedImage(100,100,2));
        frc=xG2D.getFontRenderContext();
        font=xG2D.getFont().deriveFont((float)fontHeight);
        for(int i=0;i<msg.size();i++)
        {
            curID=msg.get(i).user.userID;
            CustomPair<List<String>,Integer> tmp=stringFrag(msg.get(i).text);
            frag[i]=tmp.val1;
            msgWidth[i]=tmp.val2;
            if(curID!=lastUser)
            {
                String name;
                name=msg.get(i).user.name;
                textLayout=new TextLayout(name,font,frc);
                Rectangle2D rectangle2D=textLayout.getBounds();
                double scope=font.getSize()/textLayout.getBounds().getHeight();
                msgWidth[i]=Math.max(msgWidth[i],(int)(rectangle2D.getWidth()*scope));
                height+=fontHeight+split;
            }
            lastUser=curID;
            maxWidth=Math.max(maxWidth,msgWidth[i]);
            height+=padding*2-split+frag[i].size()*(fontHeight+split)+msgSplit;
        }
        width=maxWidth+padding*5+avatarSize;
        BufferedImage image=new BufferedImage(width,height,2);
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
            if(curID!=lastUser)
            {
                cumuH+=renderMsg(graphic,frag[i],cumuH,msgWidth[i],true,msg.get(i).user.avatar,msg.get(i).user.name);
            }
            else cumuH+=renderMsg(graphic,frag[i],cumuH,msgWidth[i],false,null,null);
            cumuH+=msgSplit;
            lastUser=curID;
        }
        sendImageAsSticker(image,target);
    }
}