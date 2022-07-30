package Main;

import Utility.DateUtil;
import Utility.GithubUtil;
import Utility.JiraUtil;
import Utility.SayUtil;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.time.Instant;

public class Main {

    public static String token;
    static final String infoPathname="./cfg/BotInfo.txt";
    static final String cfgPathname="./cfg/reload4j.properties";
    public static String name;
    public static long creatorId;
    public static long startTime;
    public static String groupID;
    public static VickyBotA bot;
    static Logger logger= LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
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
    }
}
