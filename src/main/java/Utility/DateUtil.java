package Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import static Main.Main.bot;
import static Main.Main.groupID;
import static java.lang.Math.floor;
import static java.lang.Math.random;

public class DateUtil {
    static int timeCnt;
    static int[][] msgTime;
    static String[] msg;
    static final String sayTextName="./cfg/DateText.txt";
    static Logger logger= LoggerFactory.getLogger(DateUtil.class);
    public static Thread threadDate;
    public static void say()
    {

        int curLine=0;
        String tmp="";
        try {
            BufferedReader in;
            in = Files.newBufferedReader(Paths.get(sayTextName), StandardCharsets.UTF_8);
            curLine++;
            tmp=in.readLine();
            timeCnt = Integer.parseInt(tmp);
            msgTime=new int[timeCnt][2];
            msg=new String[timeCnt];
            for(int i=0;i<timeCnt;i++)
            {
                curLine++;
                tmp=in.readLine();
                msgTime[i][0]=Integer.parseInt(tmp.substring(0,tmp.indexOf(" ")));
                msgTime[i][1]=Integer.parseInt(tmp.substring(tmp.indexOf(" ")+1));
                curLine++;
                tmp=in.readLine();
                msg[i]=tmp;
            }
        }catch(Exception e)
        {
            logger.error("Failed to read ./DateText.txt!");
            logger.error(e.toString());
            logger.error("At line: "+curLine);
            logger.error("Content: "+tmp);
        }
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(groupID);
        TimeZone tz=TimeZone.getDefault();
        int offset=tz.getOffset(Instant.now().getEpochSecond()*1000);
        threadDate=new Thread(){
            public void run()
            {
                while(true)
                {
                    try
                    {
                        for(int i=0;i<timeCnt;i++)
                        {
                            if(Date.from(Instant.now()).getMonth()==msgTime[i][0]-1
                                    &&Date.from(Instant.now()).getDate()==msgTime[i][1]
                                    &&(Instant.now().getEpochSecond()+offset/1000)%86400<=10)
                            {
                                sendMessage.setText(msg[i]);
                                bot.execute(sendMessage);
                                logger.info("Message sent: "+msg[i]);
                                Thread.sleep(11000);
                            }
                        }
                    }
                    catch(Exception e)
                    {
                        logger.error("Failed to send message!");
                        logger.error(e.toString());
                    }
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ignored){}
                }
            }
        };
        threadDate.start();
    }
}
