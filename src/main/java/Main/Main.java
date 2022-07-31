package Main;

import Cmd.CmdIO;
import Utility.DateUtil;
import Utility.GithubUtil;
import Utility.JiraUtil;
import Utility.SayUtil;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

import static Cmd.CmdIO.readFromConsole;
import static LispStyle.LispStyle.deBlank;

public class Main {

    public static String token;
    static final String infoPathname="./cfg/BotInfo.txt";
    static final String cfgPathname="./cfg/reload4j.properties";
    public static String name;
    public static long creatorId;
    public static long startTime;
    public static String groupID;
    public static VickyBotA bot;
    public static boolean isCMD;
    static Logger logger= LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        try
        {
            isCMD= CmdIO.isCMD();
        }
        catch(Exception e)
        {
            logger.error(e.toString());
            isCMD=false;
        }
        File cacheFolder=new File("./cache/");
        if(!cacheFolder.exists())cacheFolder.mkdir();
        startTime= Instant.now().getEpochSecond();
        PropertyConfigurator.configure(cfgPathname);
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
        BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(System.in));
        while(true)
        {
            try {
                if(isCMD)cur =readFromConsole();
                else cur=bufferedReader.readLine();
                if(cur==null)cur=" ";
                logger.info("Received server-side command: " + cur);
                if (cur.startsWith("/")) cur = cur.substring(1);
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
            }
            catch(Exception e)
            {
                logger.error("Error while parsing/executing command: "+cur);
                logger.error(e.toString());
            }
        }
    }
}
