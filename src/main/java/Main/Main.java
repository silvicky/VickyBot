package Main;

import MessageParsing.FakeMsg;
import MessageParsing.FakeUser;
import Utility.*;
import com.hellokaton.webp.WebpIO;
import com.sun.jna.Platform;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatStickerSet;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.stickers.AddStickerToSet;
import org.telegram.telegrambots.meta.api.methods.stickers.CreateNewStickerSet;
import org.telegram.telegrambots.meta.api.methods.stickers.DeleteStickerFromSet;
import org.telegram.telegrambots.meta.api.methods.stickers.GetStickerSet;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.stickers.Sticker;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Cmd.CmdIO.isCMD;
import static Cmd.CmdIO.readFromConsole;
import static MessageParsing.Download.obtainSticker;
import static Picture.Screenshot.fakeSS;
import static Picture.Screenshot.to512;
import static String.CommandParser.parseStr;
import static java.lang.Math.random;

public class Main {

    public static String token;
    static final String infoPathname="./cfg/BotInfo.txt";
    static final String cfgPathname="./cfg/log4j.properties";
    public static String name;
    public static long creatorId;
    public static long startTime;
    public static String groupID;
    public static VickyBotA bot;
    public static boolean isCMD;
    static Logger logger= LoggerFactory.getLogger(Main.class);
    static List<FakeMsg> fakeMsgList;
    static Map<Long, FakeUser> fakeUserMap;
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure(cfgPathname);
        if(Platform.isWindows())isCMD=isCMD();
        else isCMD=false;
        File cacheFolder=new File("./cache/");
        if(!cacheFolder.exists())cacheFolder.mkdir();
        File censorFolder=new File("./censor/");
        if(!censorFolder.exists())censorFolder.mkdir();
        startTime= Instant.now().getEpochSecond();
        TelegramBotsApi botsApi=new TelegramBotsApi(DefaultBotSession.class);
        try {
            BufferedReader in;
            in = new BufferedReader(new FileReader(infoPathname));
            token = in.readLine();
            name = in.readLine();
            creatorId = Long.parseLong(in.readLine());
            groupID = in.readLine();

        }catch(Exception e){logger.info("Failed to read ./BotInfo.txt!");}
        bot=new VickyBotA();
        botsApi.registerBot(bot);
        SayUtil.say();
        GithubUtil.github();
        //JiraUtil.jira();
        DateUtil.say();
        //AutoDel.autoDel();
        String cur="";
        fakeMsgList=new ArrayList<>();
        fakeUserMap=new HashMap<>();
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
        while(true)
        {
            try {
                if(isCMD)cur =readFromConsole();
                else cur=bufferedReader.readLine();
                if(cur==null)cur=" ";
                logger.info("Received server-side command: " + cur);
                if (cur.startsWith("/")) cur = cur.substring(1);
                if (cur.endsWith("\n")) cur = cur.substring(0,cur.length()-1);
                if (cur.endsWith("\r")) cur = cur.substring(0,cur.length()-1);
                List<String> commands=parseStr(cur);
                switch(commands.get(0))
                {
                    case "send":
                        SendMessage sendMessage=new SendMessage(commands.get(1),commands.get(2));
                        bot.execute(sendMessage);
                        break;
                    case "fake":
                        long userID;
                        switch(commands.get(1))
                        {
                            case "init":
                                fakeMsgList.clear();
                                break;
                            case "user":
                                userID=Long.parseLong(commands.get(2));
                                FakeUser fakeUser=new FakeUser(userID, commands.get(3), commands.get(4));
                                fakeUserMap.put(userID,fakeUser);
                                break;
                            case "add":
                                FakeMsg fakeMsg;
                                switch(commands.get(2))
                                {
                                    case "text":
                                        userID = Long.parseLong(commands.get(3));
                                        fakeMsg = new FakeMsg(fakeUserMap.get(userID), FakeMsg.TEXT);
                                        fakeMsg.setText(commands.get(4));
                                        fakeMsgList.add(fakeMsg);
                                        break;
                                    case "pic":
                                        userID = Long.parseLong(commands.get(3));
                                        fakeMsg = new FakeMsg(fakeUserMap.get(userID), FakeMsg.PICTURE);
                                        BufferedImage tmp = ImageIO.read(new File(commands.get(4)));
                                        fakeMsg.setPicture(commands.get(4), tmp.getHeight(), tmp.getWidth());
                                        fakeMsgList.add(fakeMsg);
                                        break;
                                    default:
                                        logger.error("Unknown command!");
                                }
                                break;
                            case "send":
                                fakeSS(fakeMsgList,Long.parseLong(commands.get(2)));
                                break;
                            default:
                                logger.error("Unknown command!");
                        }
                        break;
                    case "sticker":
                        InputFile inputFile;
                        int ran=(int)(random()*1000000000);
                        File file512,fileNorm;
                        WebpIO webpIO;
                        GetStickerSet getStickerSet;
                        switch(commands.get(1))
                        {
                            case "new":
                                getStickerSet=new GetStickerSet(commands.get(2)+"_by_"+name);
                                try
                                {
                                    bot.execute(getStickerSet);
                                    throw new Exception("This sticker set already exists!");
                                }
                                catch(TelegramApiException ignored){}
                                CreateNewStickerSet createNewStickerSet=new CreateNewStickerSet();
                                createNewStickerSet.setUserId(creatorId);
                                createNewStickerSet.setName(commands.get(2)+"_by_"+name);
                                createNewStickerSet.setTitle(commands.get(3));
                                createNewStickerSet.setEmojis(commands.get(4));
                                createNewStickerSet.setContainsMasks(false);
                                file512 = new File(commands.get(5)+"sec");
                                if(commands.get(6)=="webp")
                                {
                                    webpIO=new WebpIO();
                                    fileNorm=new File(commands.get(5)+"norm");
                                    webpIO.toNormalImage(new File(commands.get(5)),fileNorm);
                                    ImageIO.write(to512(ImageIO.read(fileNorm)), "png", file512);
                                }
                                else
                                {
                                    ImageIO.write(to512(ImageIO.read(new File(commands.get(5)))), "png", file512);
                                }
                                inputFile = new InputFile(file512, String.valueOf(ran));
                                createNewStickerSet.setPngSticker(inputFile);
                                bot.execute(createNewStickerSet);
                                file512.delete();
                                break;
                            case "add":
                                AddStickerToSet addStickerToSet=new AddStickerToSet();
                                addStickerToSet.setUserId(creatorId);
                                addStickerToSet.setName(commands.get(2)+"_by_"+name);
                                addStickerToSet.setEmojis(commands.get(3));
                                file512=new File(commands.get(4)+"sec");
                                if(commands.get(5)=="webp")
                                {
                                    webpIO=new WebpIO();
                                    fileNorm=new File(commands.get(4)+"norm");
                                    webpIO.toNormalImage(new File(commands.get(4)),fileNorm);
                                    ImageIO.write(to512(ImageIO.read(fileNorm)), "png", file512);
                                }
                                else
                                {
                                    ImageIO.write(to512(ImageIO.read(new File(commands.get(4)))), "png", file512);
                                }
                                inputFile=new InputFile(file512, String.valueOf(ran));
                                addStickerToSet.setPngSticker(inputFile);
                                bot.execute(addStickerToSet);
                                file512.delete();
                                break;
                            case "set":
                                SetChatStickerSet setChatStickerSet=new SetChatStickerSet();
                                setChatStickerSet.setChatId(commands.get(2));
                                setChatStickerSet.setStickerSetName(commands.get(3)+"_by_"+name);
                                bot.execute(setChatStickerSet);
                                break;
                            default:
                                logger.error("Unknown command!");
                        }
                    default:
                        logger.error("Unknown command!");
                }
            }
            catch(Exception e)
            {
                logger.error("Error while parsing/executing command: "+cur);
                logger.error(e.toString());
            }
        }
    }
}
