package Main;

import Cmd.CmdIO;
import Cmd.Kernel32;
import Picture.FakeMsg;
import Picture.FakeUser;
import Utility.DateUtil;
import Utility.GithubUtil;
import Utility.JiraUtil;
import Utility.SayUtil;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Cmd.CmdIO.isCMD;
import static Cmd.CmdIO.readFromConsole;
import static LispStyle.LispStyle.deBlank;
import static Picture.Screenshot.fakeSS;

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
                cur = cur.replace("\\n", "\n");
                cur = cur.replace("\\t", "\t");
                cur = deBlank(cur);
                if (cur.startsWith("send")) {
                    SendMessage sendMessage = new SendMessage();
                    cur = cur.substring(5);
                    long cid = Long.parseLong(cur.substring(0, cur.indexOf(" ")));
                    cur = cur.substring(cur.indexOf(" ") + 1);
                    sendMessage.setText(cur);
                    sendMessage.setChatId(Long.toString(cid));
                    bot.execute(sendMessage);
                }
                 else if (cur.startsWith("fake")) {
                    cur = cur.substring(5);
                    if(cur.startsWith("init"))
                    {
                        fakeMsgList.clear();
                    }
                    else if(cur.startsWith("user"))
                    {
                        cur=cur.substring(5);
                        long userID=Long.parseLong(cur.substring(0,cur.indexOf(" ")));
                        cur=cur.substring(cur.indexOf(" ")+2);
                        String userName=cur.substring(0,cur.indexOf("\""));
                        cur=cur.substring(cur.indexOf("\"")+3);
                        String avatar=cur.substring(0,cur.indexOf("\""));
                        FakeUser fakeUser=new FakeUser(userID,userName,avatar);
                        fakeUserMap.put(userID,fakeUser);
                    }
                    else if(cur.startsWith("add"))
                    {
                        cur=cur.substring(4);
                        long userID=Long.parseLong(cur.substring(0,cur.indexOf(" ")));
                        cur=cur.substring(cur.indexOf(" ")+1);
                        FakeMsg fakeMsg=new FakeMsg(fakeUserMap.get(userID));
                        fakeMsg.setText(cur);
                        fakeMsgList.add(fakeMsg);
                    }
                    else if(cur.startsWith("send"))
                    {
                        cur=cur.substring(5);
                        fakeSS(fakeMsgList,Long.parseLong(cur));
                    }
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
