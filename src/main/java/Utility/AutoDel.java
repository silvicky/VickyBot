package Utility;

import Main.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.Instant;

public class AutoDel {
    static long curTime;
    static Message curMsg;
    static final long delTime=14400L;
    static Logger logger= LoggerFactory.getLogger(AutoDel.class);
    public static void autoDel()
    {
        Thread thread=new Thread()
        {
            public void run() {
                while(true) {
                    try {
                        curTime = Instant.now().getEpochSecond();
                        while (!Main.bot.msgToBeDel.isEmpty() && curTime-Main.bot.msgToBeDel.peek().getDate()> delTime) {
                            curMsg = Main.bot.msgToBeDel.peek();
                            if (curMsg.getFrom().getId() != Main.creatorId)
                            {
                                DeleteMessage deleteMessage = new DeleteMessage();
                                deleteMessage.setChatId(String.valueOf(curMsg.getChatId()));
                                deleteMessage.setMessageId(curMsg.getMessageId());
                                try
                                {
                                    Main.bot.execute(deleteMessage);
                                    logger.info("Deleted message: "+curMsg.toString());
                                    Main.bot.msgToBeDel.remove();
                                }
                                catch(TelegramApiException e)
                                {
                                    if(e.toString().startsWith("Error deleting message: [400] Bad Request: message to delete not found"))
                                    {
                                        logger.info("Trying to delete deleted message: "+curMsg.toString());
                                        Main.bot.msgToBeDel.remove();
                                    }
                                }
                            }
                            else Main.bot.msgToBeDel.remove();
                        }
                        Thread.sleep(1000);
                    }
                    catch(Exception e)
                    {
                        logger.error(e.toString());
                    }
                }
            }
        };
        thread.start();
    }
}
