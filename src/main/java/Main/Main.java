package Main;

import MessageParsing.FakeMsg;
import MessageParsing.FakeUser;
import Utility.DateUtil;
import Utility.GithubUtil;
import Utility.JiraUtil;
import Utility.SayUtil;
import com.sun.jna.Platform;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
import static LispStyle.LispStyle.deBlank;
import static Picture.Screenshot.fakeSS;
import static String.CommandParser.parseStr;

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
        JiraUtil.jira();
        DateUtil.say();
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
