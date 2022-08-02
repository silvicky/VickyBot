package MessageParsing;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static MessageParsing.Download.*;
import static Picture.Screenshot.*;

public class FakeMsg {
    public FakeUser user;
    public int type;
    public static final int TEXT=0;
    public static final int PICTURE=1;
    public String text;
    public String picture;
    public int height;
    public int width;
    public FakeMsg(FakeUser user,int type)
    {
        this.user=user;
        this.type=type;
    }
    public void setText(String text){this.text=text;}
    public void setPicture(String picture,int height,int width){this.picture=picture;this.height=height;this.width=width;}
    public static List<FakeMsg> parse(Message msg) throws TelegramApiException, IOException {
        List<FakeMsg> ans=new ArrayList<>();
        User user=msg.getFrom();
        String name=user.getFirstName();
        long id=user.getId();
        if(user.getLastName()!=null)name+=" "+user.getLastName();
        String avatar="./cache/"+obtainAvatar(id);
        FakeUser fakeUser=new FakeUser(user.getId(),name,avatar);
        if (msg.hasPhoto()) {
                FakeMsg fakeMsg = new FakeMsg(fakeUser, PICTURE);
                List<PhotoSize> photo = msg.getPhoto();
                String pic = photo.get(photo.size() - 1).getFileId();
                obtainPicture(pic);
                fakeMsg.setPicture("./cache/" + pic, photo.get(photo.size() - 1).getHeight(), photo.get(photo.size() - 1).getWidth());
                ans.add(fakeMsg);
        }
        if (msg.hasDocument()) {
                String mime = msg.getDocument().getMimeType();
                if (mime.startsWith("image")) {
                    FakeMsg fakeMsg = new FakeMsg(fakeUser, PICTURE);
                    String pic = msg.getDocument().getFileId();
                    obtainPicture(pic);
                    BufferedImage tmp = ImageIO.read(new File("./cache/" + pic));
                    fakeMsg.setPicture("./cache/" + pic, tmp.getHeight(), tmp.getWidth());
                    ans.add(fakeMsg);
                }
        }
        if (msg.hasSticker()) {
                if (!msg.getSticker().getIsAnimated()) {
                    FakeMsg fakeMsg = new FakeMsg(fakeUser, PICTURE);
                    String pic = msg.getSticker().getFileId();
                    obtainSticker(pic,"./cache/");
                    fakeMsg.setPicture("./cache/" + pic, msg.getSticker().getHeight(), msg.getSticker().getWidth());
                    ans.add(fakeMsg);
                } else {
                    FakeMsg fakeMsg = new FakeMsg(fakeUser, TEXT);
                    fakeMsg.setText("<AnimatedSticker>");
                    ans.add(fakeMsg);
                }
            }
        if (msg.hasVideo()) {
                FakeMsg fakeMsg = new FakeMsg(fakeUser, TEXT);
                fakeMsg.setText("<Video>");
                ans.add(fakeMsg);
            }
        if (msg.getCaption() != null) {
                FakeMsg fakeMsg = new FakeMsg(fakeUser, TEXT);
                fakeMsg.setText(msg.getCaption());
                ans.add(fakeMsg);
            }
        if (msg.hasText()) {
                FakeMsg fakeMsg = new FakeMsg(fakeUser, TEXT);
                fakeMsg.setText(msg.getText());
                ans.add(fakeMsg);
            }
        return ans;
    }
}
