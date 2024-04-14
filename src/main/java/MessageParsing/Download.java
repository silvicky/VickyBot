package MessageParsing;

import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.GetUserProfilePhotos;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import static Main.Main.bot;
import static Main.Main.token;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class Download {
    public static String obtainAvatar(long userID) throws TelegramApiException, IOException {
        String ans;
        try{ans=bot.execute(new GetUserProfilePhotos(userID,0,1)).getPhotos().get(0).get(0).getFileId();
        if(!new File("./cache/"+ans).exists())
        {
            GetFile getFile;
            getFile=new GetFile(ans);
            Files.copy(new URL("https://api.telegram.org/file/bot"+token+"/"+bot.execute(getFile).getFilePath()).openStream(), Paths.get("./cache/"+ans),REPLACE_EXISTING);
        }
        return ans;}
        catch(Exception e){return "";}
    }
    public static void obtainPicture(String picID) throws IOException, TelegramApiException {
        if(!new File("./cache/"+picID).exists())
        {
            GetFile getFile;
            getFile=new GetFile(picID);
            Files.copy(new URL("https://api.telegram.org/file/bot"+token+"/"+bot.execute(getFile).getFilePath()).openStream(), Paths.get("./cache/"+picID),REPLACE_EXISTING);
        }
    }
}
