import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.time.Instant;
import java.util.TimeZone;
import java.util.Vector;

import static java.lang.Math.floor;
import static java.lang.Math.random;

public class SayUtil {
    static int timeCnt;
    static int[] msgCnt;
    static int[] msgTime;
    static Vector<String>[] msg;
    static final String sayTextName="./cfg/SayText.txt";
    static Logger logger= LoggerFactory.getLogger(SayUtil.class);
    public static void say()
    {
        int curLine=0;
        String tmp="";
        try {
            BufferedReader in;
            in = new BufferedReader(new FileReader(sayTextName));
            curLine++;
            tmp=in.readLine();
            timeCnt = Integer.parseInt(tmp);
            msgCnt=new int[timeCnt];
            msgTime=new int[timeCnt];
            msg=new Vector[timeCnt];
            for(int i=0;i<timeCnt;i++)
            {
                curLine++;
                tmp=in.readLine();
                msgCnt[i]= Integer.parseInt(tmp);
                curLine++;
                tmp=in.readLine();
                msgTime[i]=Integer.parseInt(tmp);
                msg[i]=new Vector<>(msgCnt[i]);
                for(int j=0;j<msgCnt[i];j++)
                {
                    curLine++;
                    tmp=in.readLine();
                    msg[i].add(tmp);
                }
            }
        }catch(Exception e)
        {
            logger.error("Failed to read ./SayText.txt!");
            logger.error(e.toString());
            logger.error("At line: "+curLine);
            logger.error("Content: "+tmp);
        }
        SendMessage sendMessage=new SendMessage();
        sendMessage.setChatId(Main.groupID);
        TimeZone tz=TimeZone.getDefault();
        int offset=tz.getOffset(Instant.now().getEpochSecond()*1000);
        Thread threadSay=new Thread(){
            public void run()
            {
                while(true)
                {
                    try
                    {
                        for(int i=0;i<timeCnt;i++)
                        {
                            if((Instant.now().getEpochSecond()-msgTime[i]+offset/1000)%86400<=10)
                            {
                                String txt=msg[i].get((int) floor(random()*msgCnt[i]));
                                sendMessage.setText(txt);
                                Main.bot.execute(sendMessage);
                                logger.info("Message sent: "+txt);
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
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        threadSay.start();
    }
}
