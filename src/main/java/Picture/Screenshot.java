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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
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
    static BufferedImage xImg;
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
    public static void screenshot(List<Message> msg, long target) throws Exception {
        int width;
        int height;
        height=msgSplit;
        List<String>[] frag=new List[msg.size()];
        int[] msgWidth=new int[msg.size()];
        Map<Long,String> avatar=new HashMap<>();
        int maxWidth=0;
        long lastUser=0L,curID;
        String name;
        TextLayout textLayout;
        xImg=new BufferedImage(100,100,2);
        xG2D=GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(xImg);
        frc=xG2D.getFontRenderContext();
        font=xG2D.getFont().deriveFont((float)fontHeight);
        GetFile getFile;
        for(int i=0;i<msg.size();i++)
        {
            curID=msg.get(i).getFrom().getId();
            CustomPair<List<String>,Integer> tmp=stringFrag(msg.get(i).getText());
            frag[i]=tmp.val1;
            msgWidth[i]=tmp.val2;
            avatar.put(curID, bot.execute(new GetUserProfilePhotos(curID,0,1)).getPhotos().get(0).get(0).getFileId());
            if(curID!=lastUser)
            {
                name=msg.get(i).getFrom().getFirstName();
                if(msg.get(i).getFrom().getLastName()!=null)name+=" "+msg.get(i).getFrom().getLastName();
                textLayout=new TextLayout(name,font,frc);
                Rectangle2D rectangle2D=textLayout.getBounds();
                double scope=font.getSize()/textLayout.getBounds().getHeight();
                msgWidth[i]=Math.max(msgWidth[i],(int)(rectangle2D.getWidth()*scope));
                if(!new File("./cache/"+avatar.get(curID)).exists())
                {
                    getFile=new GetFile(avatar.get(curID));
                    Files.copy(new URL("https://api.telegram.org/file/bot"+token+"/"+bot.execute(getFile).getFilePath()).openStream(),Paths.get("./cache/"+avatar.get(curID)),REPLACE_EXISTING);
                }
                height+=fontHeight+split;
            }
            lastUser=curID;
            maxWidth=Math.max(maxWidth,msgWidth[i]);
            height+=padding*2-split+frag[i].size()*(fontHeight+split)+msgSplit;
        }
        width=maxWidth+padding*5+avatarSize;
        BufferedImage image=new BufferedImage(width,height,2);
        BufferedImage avatarCur;
        GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(font);
        Graphics2D graphic=ge.createGraphics(image);
        graphic.setBackground(new Color(255,255,255,0));
        graphic.clearRect(0,0,width,height);
        graphic.setBackground(Color.white);
        graphic.setPaint(Color.black);
        graphic.setFont(font);
        int cumuH,curH;
        cumuH=msgSplit;
        lastUser=0L;
        for(int i=0;i<msg.size();i++)
        {
            curID=msg.get(i).getFrom().getId();
            curH=padding*2-split+frag[i].size()*(fontHeight+split);
            if(curID!=lastUser)
            {
                curH+=fontHeight+split;
            }
            graphic.clearRect(avatarSize+padding*2,cumuH,msgWidth[i]+2*padding,curH);
            int ix=avatarSize+padding*3,iy=padding+fontHeight+cumuH-1;
            if(curID!=lastUser)
            {
                avatarCur=ImageIO.read(new File("./cache/" + avatar.get(curID)));
                double avatarScope=(double)avatarSize/avatarCur.getHeight();
                graphic.drawImage(avatarCur, new AffineTransformOp(new AffineTransform(avatarScope,0,0,avatarScope,0,0),AffineTransformOp.TYPE_NEAREST_NEIGHBOR), padding, cumuH);
                graphic.setPaint(Color.magenta);
                name=msg.get(i).getFrom().getFirstName();
                if(msg.get(i).getFrom().getLastName()!=null)name+=" "+msg.get(i).getFrom().getLastName();
                graphic.drawString(name,ix,iy);
                graphic.setPaint(Color.black);
                iy+=fontHeight+split;
            }
            lastUser=curID;
            for(int j=0;j<frag[i].size();j++)
            {
                graphic.drawString(frag[i].get(j),ix,iy);
                iy+=fontHeight+split;
            }
            cumuH+=curH+msgSplit;
        }
        sendImageAsSticker(image,target);
    }
}