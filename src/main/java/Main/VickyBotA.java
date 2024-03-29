package Main;

import Eliza.ElizaMain;
import Expression.Normal;
import Picture.Screenshot;
import Utility.*;
import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.abilitybots.api.bot.AbilityBot;
import org.telegram.abilitybots.api.objects.Ability;
import org.telegram.telegrambots.meta.api.methods.ForwardMessage;
import org.telegram.telegrambots.meta.api.methods.groupadministration.BanChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.RestrictChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.stickers.DeleteStickerFromSet;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.json.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.time.Instant;
import java.util.*;

import static Expression.LispStyle.evaluate;
import static Expression.Normal.calculate;
import static Main.Main.*;
import static Roman.IntegerToEnglish.numberToWords;
import static Roman.IntegerToRoman.intToRoman;
import static Roman.RomanToInteger.romanToInt;
import static String.StringExp.decodeString;
import static Utility.ThreadStatus.threadStatus;
import static java.lang.Math.random;
import static org.telegram.abilitybots.api.objects.Locality.*;
import static org.telegram.abilitybots.api.objects.Privacy.ADMIN;
import static org.telegram.abilitybots.api.objects.Privacy.PUBLIC;

public class VickyBotA extends AbilityBot {
    static final int maxEliza=1000;
    static final long sth=3827381L;
    static final String scriptPathname = "./cfg/ElizaScript.txt";
    static final int maxSS=1000;
    static Logger logger= LoggerFactory.getLogger(VickyBotA.class);
    ElizaMain[] eliza;
    boolean[] ssMode;
    List<Message>[] ssTmp;
    Map<Long,Integer> elizaMap;
    Map<Long,Integer> ssMap;
    boolean[] isElizaOccupied;
    boolean[] isSSOccupied;
    long curTime,timeH,timeM,timeS;
    public Queue<Message> msgToBeDel;
    public void onUpdateReceived(Update update)
    {
        if(update.hasMessage()&&update.getMessage().getChatId()==Long.parseLong(Main.groupID))
        {
            msgToBeDel.add(update.getMessage());
        }
        super.onUpdateReceived(update);
    }
    public VickyBotA()
    {
        super(Main.token,Main.name);
        msgToBeDel=new ArrayDeque<>();
        eliza=new ElizaMain[maxEliza];
        elizaMap=new HashMap<>();
        isElizaOccupied=new boolean[maxEliza];
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
    public Ability shutMe()
    {
        return Ability.builder()
                .name("shutme")
                .info("Shut up yourself.")
                .input(0)
                .locality(GROUP)
                .privacy(PUBLIC)
                .action(ctx->
                {
                    ChatPermissions chatPermShut=new ChatPermissions(
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false);

                    long extratime=100+(long)(random()*200);
                    long unixTimeShut= Instant.now().getEpochSecond()+extratime;
                    RestrictChatMember restrictChatMember=new RestrictChatMember(ctx.chatId().toString(),ctx.user().getId(),chatPermShut,(int)unixTimeShut);
                    try
                    {
                        this.execute(restrictChatMember);
                        silent.send(ctx.user().getFirstName()+" has been shut for "+ extratime +"s",ctx.chatId());
                    }
                    catch(Exception e){silent.send("I'm not admin or you are admin!",ctx.chatId());}

                })
                .build();
    }
    public Ability shut()
    {
        return Ability.builder()
                .name("shut")
                .info("Shut up somebody.")
                .input(0)
                .locality(GROUP)
                .privacy(ADMIN)
                .action(ctx->
                {
                    ChatPermissions chatPermShut=new ChatPermissions(
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false,
                            false);

                    long extratime=100+(long)(random()*200);
                    long unixTimeShut= Instant.now().getEpochSecond()+extratime;
                    long uid;
                    if(ctx.update().getMessage().isReply())uid=ctx.update().getMessage().getReplyToMessage().getFrom().getId();
                    else if(ctx.arguments().length>0)uid=userIds().get(ctx.firstArg().substring(1).toLowerCase());
                    else
                    {
                        silent.send("reply or add an @...",ctx.chatId());
                        return;
                    }
                    RestrictChatMember restrictChatMember=new RestrictChatMember(ctx.chatId().toString(),uid,chatPermShut,(int)unixTimeShut);
                    GetChatMember getChatMember = new GetChatMember(ctx.chatId().toString(), uid);
                    ChatMember chatMember = silent.execute(getChatMember).get();
                    try
                    {
                        this.execute(restrictChatMember);
                        silent.send(chatMember.getUser().getFirstName() + " has been shut for " + extratime + "s", ctx.chatId());
                    }
                    catch(Exception e){silent.send("I'm not admin or "+chatMember.getUser().getFirstName()+" is admin!",ctx.chatId());}
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
    public Ability romanToInteger()
    {
        return Ability.builder()
                .name("r2i")
                .info("Turn roman to integer.")
                .input(1)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->{
                        try{silent.send(Integer.toString(romanToInt(ctx.firstArg().toUpperCase())),ctx.chatId());}
                        catch(Exception e){silent.send("ERR: "+e,ctx.chatId());}
                })
                .build();
    }
    public Ability integerToRoman()
    {
        return Ability.builder()
                .name("i2r")
                .info("Turn integer to roman.")
                .input(1)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->{
                    try{silent.send(intToRoman(Integer.parseInt(ctx.firstArg())),ctx.chatId());}
                    catch(Exception e){silent.send("ERR: "+e,ctx.chatId());}
                })
                .build();
    }
    public Ability integerToEnglish()
    {
        return Ability.builder()
                .name("i2e")
                .info("Turn integer to English.")
                .input(1)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->{
                    try{silent.send(numberToWords(Integer.parseInt(ctx.firstArg())),ctx.chatId());}
                    catch(Exception e){silent.send("ERR: "+e,ctx.chatId());}
                })
                .build();
    }
    public Ability exp()
    {
        return Ability.builder()
                .name("exp")
                .info("Expand string that has ...<integer>[...]... structure.")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->{
                    DeleteMessage deleteMessage=new DeleteMessage();
                    deleteMessage.setMessageId(ctx.update().getMessage().getMessageId());
                    deleteMessage.setChatId(String.valueOf(ctx.chatId()));
                    if(ctx.arguments().length==0)
                    {
                        silent.send(
                                "Example: /exp 3[a2[b]]\n" +
                                "Tips: Commands other than this will be deleted.",ctx.chatId());
                    }
                    else
                    {
                        try{this.execute(deleteMessage);}catch(Exception ignored){}
                        String msg=ctx.update().getMessage().getText();
                        try
                        {
                            silent.send(decodeString(msg.substring(msg.indexOf(" ")+1)),ctx.chatId());
                        }
                        catch(Exception e)
                        {
                            silent.send("ERR: "+e,ctx.chatId());
                        }
                    }
                })
                .build();
    }
    public Ability rep()
    {
        return Ability.builder()
                .name("rep")
                .info("Repeat the message.")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->{
                    DeleteMessage deleteMessage=new DeleteMessage();
                    deleteMessage.setMessageId(ctx.update().getMessage().getMessageId());
                    deleteMessage.setChatId(String.valueOf(ctx.chatId()));
                    if(ctx.arguments().length==0||ctx.arguments().length==1)
                    {
                        silent.send(
                                "Example: /rep 3 aaa\n" +
                                "Tips: Commands other than this will be deleted.",ctx.chatId());
                    }
                    else
                    {
                        try{this.execute(deleteMessage);}catch(Exception ignored){}
                        String msg=ctx.update().getMessage().getText();

                        try
                        {
                            int times;
                            msg=msg.substring(msg.indexOf(" ")+1);
                            times=Integer.parseInt(msg.substring(0,msg.indexOf(" ")));
                            msg=msg.substring(msg.indexOf(" ")+1);
                            if(times>100)throw new Exception("dON'T BE SO HUNGRY");
                            for(int i=0;i<times;i++) silent.send(msg,ctx.chatId());
                        }
                        catch(Exception e)
                        {
                            silent.send("ERR: "+e,ctx.chatId());
                        }
                    }
                })
                .build();
    }
    public Ability eliza()
    {
        return Ability.builder()
                .name("eliza")
                .info("Mimics ELIZA.")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->
                {
                    String msg0=ctx.update().getMessage().getText();
                    String msg=msg0.substring(msg0.indexOf(" ")+1).toLowerCase();
                    if(ctx.arguments().length==0)
                    {
                        silent.send("This is Eliza, a chat program.\n"+
                                            "Start with Hello.\n"+
                                            "Please close it after usage.\n"+
                                            "No need to initialize at the beginning.\n"+
                                            "Talk with her in a separate chat for better experience.\n"+
                                            "/eliza eliza.init - Initialize.\n"+
                                            "/eliza eliza.close - Close this Eliza.\n"+
                                            "/eliza ... - Talk with her.",ctx.chatId());
                    }
                    else
                    {
                        int firstFree=0;
                        if(elizaMap.get(ctx.chatId())==null)
                        {

                            for(;firstFree<maxEliza;firstFree++)
                            {
                                if(!isElizaOccupied[firstFree])break;
                            }
                            if(firstFree==maxEliza)
                            {
                                silent.send("There are too many Elizas!\n"+
                                                    "Please wait for a while...",ctx.chatId());
                            }
                            else
                            {
                                isElizaOccupied[firstFree]=true;
                                elizaMap.put(ctx.chatId(),firstFree);
                                eliza[firstFree]=new ElizaMain();
                                eliza[firstFree].readScript(true, scriptPathname);
                                silent.send("Generated new Eliza.",ctx.chatId());
                            }
                        }
                        else firstFree=elizaMap.get(ctx.chatId());
                        if(firstFree!=maxEliza)
                        {
                            if(msg.toLowerCase().startsWith("eliza.init"))
                            {
                                eliza[firstFree]=new ElizaMain();
                                eliza[firstFree].readScript(true, scriptPathname);
                                silent.send("Initialized your Eliza.",ctx.chatId());
                            }
                            else if(msg.toLowerCase().startsWith("eliza.close"))
                            {
                                isElizaOccupied[firstFree]=false;
                                elizaMap.remove(ctx.chatId());
                                silent.send("Closed your Eliza.",ctx.chatId());
                            }
                            else
                            {
                                String ans = eliza[firstFree].processInput(msg);
                                silent.send(ans, ctx.chatId());
                            }
                        }
                    }
                })
                .build();
    }
    public Ability lispStyle()
    {
        return Ability.builder()
                .name("lisp")
                .info("Process Lisp-style expression.")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->
                {
                    if(ctx.arguments().length==0)silent.send(
                            "Support:\n" +
                                    "\tlet: assign values and calculate.\n" +
                                    "\t\t(let v1 e1 v2 e2 ... expr)\n"+
                                    "\tadd: add two values.\n" +
                                    "\t\t(add x y)\n"+
                                    "\tmult: multiply two values.\n" +
                                    "\t\t(mult x y)\n"+
                                    "Still not completely implemented.",ctx.chatId());
                    else
                    {
                        String msg=ctx.update().getMessage().getText().toLowerCase();
                        try{silent.send(Long.toString(evaluate(msg.substring(msg.indexOf(" ")+1))),ctx.chatId());}
                        catch(Exception e){silent.send("ERR: "+e,ctx.chatId());}
                    }
                })
                .build();
    }
    public Ability calc()
    {
        return Ability.builder()
                .name("calc")
                .info("Process normal expression.")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->
                {
                    if(ctx.arguments().length==0)silent.send(
                            "/calc ...\n" +
                                    "Support:+_*/()",ctx.chatId());
                    else
                    {
                        String msg=ctx.update().getMessage().getText().toLowerCase();
                        try{silent.send(Double.toString(calculate(msg.substring(msg.indexOf(" ")+1))),ctx.chatId());}
                        catch(Exception e){silent.send("ERR: "+e,ctx.chatId());}
                    }
                })
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
                            if(!ssMode[cur])ssTmp[cur]=new ArrayList<>();;
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
    public Ability deleteSticker()
    {
        return Ability.builder()
                .name("dels")
                .info("Delete a sticker.")
                .input(0)
                .locality(ALL)
                .privacy(ADMIN)
                .action(ctx->
                {
                    Message reply=ctx.update().getMessage().getReplyToMessage();
                    if(reply==null||!reply.hasSticker())
                    {
                        silent.send("Reply to a sticker to use it.",ctx.chatId());
                    }
                    else
                    {
                        DeleteStickerFromSet deleteStickerFromSet=new DeleteStickerFromSet();
                        deleteStickerFromSet.setSticker(reply.getSticker().getFileId());
                        try
                        {
                            execute(deleteStickerFromSet);
                            silent.send("Done.",ctx.chatId());
                        }
                        catch(TelegramApiException e)
                        {
                            silent.send("It's not my sticker set!",ctx.chatId());
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
    public Ability uploadCpy()
    {
        return Ability.builder()
                .name("uploadc")
                .info("Call bot to copy and upload sth.")
                .input(0)
                .locality(USER)
                .privacy(ADMIN)
                .action(ctx->
                {
                    String msg=ctx.update().getMessage().getText();
                    String path=msg.substring(msg.indexOf(" ")+1);
                    File origin=new File(path);
                    File dest=new File(path+".tmp");
                    try {

                        Files.copy(origin.toPath(),dest.toPath());
                        SendDocument sendDocument=new SendDocument(ctx.chatId().toString(),new InputFile(dest));
                        execute(sendDocument);
                        dest.delete();
                    } catch (Exception e) {
                        silent.send("Upload failed!",ctx.chatId());
                        logger.error("Upload failed!");
                        logger.error(path);
                        logger.error(e.toString());
                    }
                })
                .build();
    }
    public Ability askMe()
    {
        return Ability.builder()
                .name("ask")
                .info("Ask Vicky a question.")
                .input(0)
                .locality(ALL)
                .privacy(PUBLIC)
                .action(ctx->
                        {
                            if(ctx.arguments().length==0){silent.send("Ask something!",ctx.chatId());}
                            else
                            {
                                String msg=ctx.update().getMessage().getText();
                                silent.send("#ask "+msg.substring(msg.indexOf(" ")+1),Main.creatorId);
                                silent.send("Done.",ctx.chatId());
                            }
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
                            String keys[]={"handle","country","city","organization","rating","rank","maxRating","maxRank","avatar"};
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
                .locality(USER)
                .privacy(ADMIN)
                .action(ctx->
                {
                    try {
                            BanChatMember banChatMember=new BanChatMember(groupID,Long.parseLong(ctx.firstArg()));
                            silent.execute(banChatMember);
                            banChatMember=new BanChatMember(channelID,Long.parseLong(ctx.firstArg()));
                            silent.execute(banChatMember);
                            silent.execute(new SendMessage(ctx.chatId().toString(),"SAYONARA"));
                        } catch (Exception e) {
                            silent.execute(new SendMessage(ctx.chatId().toString(),"ERR:"+e));
                        }
                })
                .build();
    }
    public Ability id()
    {
        return Ability.builder()
                .name("id")
                .info("Codeforces API.")
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
