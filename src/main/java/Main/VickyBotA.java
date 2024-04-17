package Main;

import Picture.Screenshot;
import com.sun.jna.Platform;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.net.URL;
import java.time.Instant;
import java.util.*;

import static Main.Main.*;
import static org.telegram.abilitybots.api.objects.Locality.*;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

public class VickyBotA extends AbilityBot {
    static final long sth=3827381L;
    static final int maxSS=1000;
    static final Logger logger= LoggerFactory.getLogger(VickyBotA.class);
    final boolean[] ssMode;
    final List<Message>[] ssTmp;
    final Map<Long,Integer> ssMap;
    final boolean[] isSSOccupied;
    long curTime,timeH,timeM,timeS;
    public final Queue<Message> msgToBeDel;
    public void onUpdateReceived(Update update)
    {
        super.onUpdateReceived(update);
    }
    public VickyBotA()
    {
        super(Main.token,Main.name);
        msgToBeDel=new ArrayDeque<>();
        ssMode=new boolean[maxSS];
        ssTmp=new List[maxSS];
        ssMap=new HashMap<>();
        isSSOccupied=new boolean[maxSS];
    }
    @Override
    public long creatorId()
    {
        return Main.creatorId;
    }

    public Ability about()
    {
        return Ability.builder()
                .name("about")
                .info("Returns the bot environment.")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->
                {
                    curTime=Instant.now().getEpochSecond();
                    timeH=(curTime- startTime)/3600;
                    timeM=((curTime- startTime)%3600)/60;
                    timeS=(curTime- startTime)%60;
                    String aboutMsg="A bot by Vicky Silviana.\nOS: "
                            +System.getProperty("os.name")
                            +"\nVer: "
                            +System.getProperty("os.version")
                            +"\nArch: "
                            +System.getProperty("os.arch")
                            +"\n";
                    if(Platform.isWindows())
                    {
                        aboutMsg+="Kernel Version:\n";
                        try {
                            BufferedReader ver=new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("cmd /c ver").getInputStream()));
                            String verTmp=ver.readLine();
                            while(verTmp!=null)
                            {
                                aboutMsg+=verTmp;
                                aboutMsg+="\n";
                                verTmp=ver.readLine();
                            }
                        } catch (IOException e) {
                            aboutMsg+="Failed to get kernel version!";
                        }
                    }
                    silent.send(
                                aboutMsg+"JavaVer: "
                                +System.getProperty("java.version")
                                +"\nUptime:\n"
                                +timeH+"h"+timeM+"m"+timeS+"s", ctx.chatId());
                })
                .build();

    }
    public Ability javaCup()
    {
        return Ability.builder()
                .name("java")
                .info("Have a cup of java.")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->
                        silent.send(ctx.user().getFirstName()+  " thanks for your hard work. Have a cup of java and relax!☕\n" +
                                                                        "This bot is written in java.",ctx.chatId()))
                .build();
    }
    public Ability screenshot()
    {
        return Ability.builder()
                .name("ss")
                .info("Take a screenshot.")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->
                {
                    Message reply=ctx.update().getMessage().getReplyToMessage();
                    DeleteMessage deleteMessage=new DeleteMessage();
                    deleteMessage.setMessageId(ctx.update().getMessage().getMessageId());
                    deleteMessage.setChatId(String.valueOf(ctx.chatId()));
                    if(reply==null)
                    {
                        silent.send(
                                "Reply to something to use it.\n" +
                                        "/ss - Capture this only.\n" +
                                        "/ss begin - Capture from this. Use /ss after this.\n" +
                                        "/ss end - Capture up to this.\n" +
                                        "Tips: Commands other than this will be deleted.",ctx.chatId());
                    }
                    else
                    {
                        try{this.execute(deleteMessage);}catch(Exception ignored){}
                        long pair=ctx.chatId()*sth+ctx.update().getMessage().getFrom().getId();
                        if(!ssMap.containsKey(pair))
                        {
                            for(int i=0;i<maxSS;i++)if(!isSSOccupied[i])
                            {
                                isSSOccupied[i]=true;
                                ssMap.put(pair,i);
                                break;
                            }
                        }
                        int cur=ssMap.get(pair);
                        if(ctx.arguments().length==0)
                        {
                            if(ssMode[cur])
                            {
                                ssTmp[cur].add(reply);
                            }
                            else
                            {
                                List<Message> ssTmpT=new ArrayList<>();
                                isSSOccupied[cur]=false;
                                ssMap.remove(pair);
                                ssTmpT.add(reply);
                                try {
                                    Screenshot.screenshot(ssTmpT,ctx.chatId());
                                } catch (Exception e) {
                                    silent.send("ERR: "+e,ctx.chatId());
                                    logger.error(e.toString());
                                }
                            }
                        }
                        else if(ctx.firstArg().toLowerCase().startsWith("begin"))
                        {
                            ssMode[cur]=true;
                            ssTmp[cur]=new ArrayList<>();
                            ssTmp[cur].add(reply);
                        }
                        else if(ctx.firstArg().toLowerCase().startsWith("end"))
                        {
                            if(!ssMode[cur])ssTmp[cur]=new ArrayList<>();
                            ssTmp[cur].add(reply);
                            ssMode[cur]=false;
                            isSSOccupied[cur]=false;
                            ssMap.remove(pair);
                            try {
                                Screenshot.screenshot(ssTmp[cur],ctx.chatId());
                            } catch (Exception e) {
                                silent.send("ERR: "+e,ctx.chatId());
                                logger.error(e.toString());
                            }
                        }
                        else
                        {
                            silent.send("Unknown argument.",ctx.chatId());
                        }
                    }
                })
                .build();
    }
    public Ability upload()
    {
        return Ability.builder()
                .name("upload")
                .info("Call bot to upload sth.")
                .input(0)
                .locality(USER)
                .privacy(ADMIN)
                .action(ctx->
                {
                    Thread uploadIt=new Thread(){
                    public void run() {
                            String msg=ctx.update().getMessage().getText();
                            String path=msg.substring(msg.indexOf(" ")+1);
                            SendDocument sendDocument=new SendDocument(ctx.chatId().toString(),new InputFile(new File(path)));
                            try {
                                execute(sendDocument);
                            } catch (Exception e) {
                                silent.send("Upload failed!",ctx.chatId());
                                logger.error("Upload failed!");
                                logger.error(path);
                                logger.error(e.toString());
                            }
                        }
                    };
                    uploadIt.start();
                })
                .build();
    }
    public Ability pumpMsg()
    {
        return Ability.builder()
                .name("pump")
                .info("\"Pump\" a message out the group.")
                .input(0)
                .locality(GROUP)
                .privacy(ADMIN)
                .action(ctx->
                {
                    if(!ctx.update().getMessage().isReply())silent.send("Reply to sth.",ctx.chatId());
                    else
                    {
                        Message msg=ctx.update().getMessage().getReplyToMessage();
                        silent.execute(new SendMessage(ctx.user().getId().toString(),msg.getText()));
                        silent.execute(new ForwardMessage(ctx.user().getId().toString(),ctx.chatId().toString(),msg.getMessageId()));
                        silent.execute(new DeleteMessage(ctx.chatId().toString(),ctx.update().getMessage().getMessageId()));
                    }
                })
                .build();
    }
    public Ability codeforces()
    {
        return Ability.builder()
                .name("cf")
                .info("Codeforces API.")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->
                {
                    if(ctx.arguments().length==0)silent.send("Usage:\n/cf <name>",ctx.chatId());
                    else
                    {

                        try {
                            Scanner sc=new Scanner(new URL("https://codeforces.com/api/user.info?handles="+ctx.firstArg()).openStream());
                            String str="";
                            while(sc.hasNext())str+=sc.nextLine();
                            sc.close();
                            JSONObject obj=new JSONObject(str).getJSONArray("result").getJSONObject(0);
                            String ret="";
                            String[] keys={"handle","country","city","organization","rating","rank","maxRating","maxRank","avatar"};
                            for(String s:keys)
                            {
                                if(obj.has(s))ret+=s+": "+obj.get(s).toString()+"\n";
                            }
                            silent.execute(new SendMessage(ctx.chatId().toString(),ret));
                        } catch (Exception e) {
                            silent.execute(new SendMessage(ctx.chatId().toString(),"ERR:"+e));
                        }

                    }
                })
                .build();
    }
    public Ability bye()
    {
        return Ability.builder()
                .name("sayonara")
                .info("Codeforces API.")
                .input(1)
                .locality(ALL)
                .privacy(ADMIN)
                .action(ctx->
                {
                    if(ctx.firstArg().startsWith("-"))
                    {
                        try{Long.parseLong(ctx.firstArg());}catch (Exception e){return;}
                        try {
                            BANNED_LIST.add(ctx.firstArg());
                            BufferedWriter writ=new BufferedWriter(new FileWriter(banPathname));
                            writ.newLine();
                            writ.write(ctx.firstArg());
                            writ.close();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    String err="";
                    for(String shid:BANNED_LIST)
                    {
                        try {
                            for(String id:JTSN_FEDERATION)execute(new BanChatMember(id,Long.parseLong(shid)));
                        } catch (TelegramApiException e) {
                            err+=shid;
                            err+=" ";
                        }
                    }
                    silent.execute(new SendMessage(ctx.chatId().toString(),"SAYONARA"));
                    silent.execute(new SendMessage(ctx.chatId().toString(),err));
                })
                .build();
    }
    public Ability id()
    {
        return Ability.builder()
                .name("id")
                .info("getid.")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->
                {
                    silent.execute(new SendMessage(ctx.chatId().toString(),
                            "group: "+ctx.chatId().toString()+
                            "\nyou: "+ctx.update().getMessage().getFrom().getId()+
                            "\nreplyto: "+(ctx.update().getMessage().isReply()?(ctx.update().getMessage().getReplyToMessage().getFrom().getId()==777000?ctx.update().getMessage().getReplyToMessage().getForwardFromChat().getId():ctx.update().getMessage().getReplyToMessage().getFrom().getId()):"nop")));
                })
                .build();
    }
}
