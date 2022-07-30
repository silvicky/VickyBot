package Utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static Main.Main.bot;
import static Main.Main.groupID;

class JiraClient
{
    String username;
    String password;
    String jiraUrl;
    public JiraClient(String username, String password, String jiraUrl) {
        this.username = username;
        this.password = password;
        this.jiraUrl = jiraUrl;
    }
}
public class JiraUtil
{
    static int repoCnt;
    static JiraClient[] jiraCli;
    static String[] lastId;
    static String jiraName="./cfg/Jira.txt";
    static Logger logger= LoggerFactory.getLogger(JiraUtil.class);
    static int findPkg(String source)
    {
        int lev=1,ind=1;
        for(;ind<source.length();ind++)
        {
            if(source.charAt(ind)=='<')lev++;
            if(source.charAt(ind)=='>')lev--;
            if(lev==0)return ind;
        }
        return -1;
    }
    static String deTag(String source)
    {
        String ans="";
        while(source.length()>0)
        {
            if(source.charAt(0)=='<')source=source.substring(findPkg(source)+1);
            if(!source.contains("<"))
            {
                ans+=source;
                return ans;
            }
            else
            {
                ans+=source.substring(0,source.indexOf("<"));
                source=source.substring(source.indexOf("<"));
            }
        }
        return ans;
    }
    public static void jira() throws IOException, ParserConfigurationException {
        BufferedReader in;
        in = new BufferedReader(new FileReader(jiraName));
        DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        repoCnt=Integer.parseInt(in.readLine());
        jiraCli=new JiraClient[repoCnt];
        lastId=new String[repoCnt];
        String uri,username,password;
        try
        {
            for(int i=0;i<repoCnt;i++)
            {
                uri=in.readLine();
                username=in.readLine();
                password=in.readLine();
                jiraCli[i]=new JiraClient(username,password,uri);
            }
        }
        catch(Exception e)
        {
            logger.error("Failed to read ./cfg/Jira.txt!");
            logger.error(e.toString());
        }
        Thread jira=new Thread()
        {
            public void run()
            {
                Document doc;
                NodeList entries;
                Element element;
                try {
                    for (int i = 0; i < repoCnt; i++) {
                        doc = builder.parse(jiraCli[i].jiraUrl + "activity");
                        entries = doc.getElementsByTagName("entry");
                        element = (Element) entries.item(0);
                        String s = element.getElementsByTagName("id").item(0).getTextContent();
                        lastId[i] = s;
                    }
                }
                catch(Exception e)
                {
                    logger.error("Failed to initialize repos!");
                    logger.error(e.toString());
                }
                logger.info("Jira utility initialized successfully.");
                SendMessage msg=new SendMessage();
                msg.setChatId(groupID);
                while(true)
                {
                    try
                    {
                        for(int i=0;i<repoCnt;i++)
                        {
                            doc= builder.parse(jiraCli[i].jiraUrl+"activity");
                            entries=doc.getElementsByTagName("entry");
                            for(int j=0;j< entries.getLength();j++)
                            {
                                element=(Element)entries.item(j);
                                String s=element.getElementsByTagName("id").item(0).getTextContent();
                                String t=element.getElementsByTagName("title").item(0).getTextContent();
                                if(s.startsWith(lastId[i]))break;

                                msg.setText("New activity from:"+jiraCli[i].jiraUrl+"\nInfo: "+deTag(t));
                                logger.info("New activity from:"+jiraCli[i].jiraUrl+",Info: "+deTag(t));
                                bot.execute(msg);
                            }
                            logger.info("Fetched latest activity info from:"+jiraCli[i].jiraUrl);
                            lastId[i]=((Element)entries.item(0)).getElementsByTagName("id").item(0).getTextContent();

                        }
                    }
                    catch(Exception e)
                    {
                        logger.error("Failed to fetch latest activity info!");
                        logger.error(e.toString());
                    }
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        };
        jira.start();
    }
}