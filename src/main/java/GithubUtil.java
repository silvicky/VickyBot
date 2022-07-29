import org.kohsuke.github.GHCommit;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.time.Instant;
import java.util.Date;
import java.util.List;

public class GithubUtil {
    static String[] repoName;
    static int len;
    public static String githubToken;
    static String githubName="./cfg/Github.txt";
    static Logger logger= LoggerFactory.getLogger(GithubUtil.class);
    public static void github() throws IOException {
        BufferedReader in;
        in = new BufferedReader(new FileReader(githubName));
        githubToken=in.readLine();
        len=Integer.parseInt(in.readLine());
        repoName=new String[len];
        for(int i=0;i<len;i++)repoName[i]=in.readLine();
        GitHub github=new GitHubBuilder().withOAuthToken(githubToken).build();
        Thread threadGithub=new Thread()
        {
            public void run()
            {
                GHRepository[] repo=new GHRepository[repoName.length];
                String[] lastSha=new String[repoName.length];
                Date[] lastDate=new Date[repoName.length];
                List<GHCommit>[] listCur=new List[repoName.length];
                Date date;
                SendMessage msg=new SendMessage();
                msg.setChatId(Main.groupID);
                String curMsg;
                for(int i=0;i<repoName.length;i++)
                try {
                    repo[i]=github.getRepository(repoName[i]);
                    logger.info("Fetched repo info: "+repoName[i]);
                    lastDate[i]=Date.from(Instant.now());
                    lastSha[i]="";
                    for(int j=0;j<40;j++)lastSha[i]+="0";
                } catch (Exception e) {
                    logger.error("Failed to get repo "+repoName[i]+" info!");
                    logger.error(e.toString());
                }
                logger.info("Github utility initialized successfully.");
                while(true)
                {
                    try
                    {
                    for(int i=0;i<repoName.length;i++)
                    {
                        listCur[i]=repo[i].queryCommits().since(lastDate[i]).list().toList();
                        logger.info("Fetched repo "+repoName[i]+" info, length="+listCur[i].size());
                        for(int j=0;j<listCur[i].size();j++)
                        {
                            if(listCur[i].get(j).getSHA1().startsWith(lastSha[i]))break;
                            date=listCur[i].get(j).getCommitDate();
                            curMsg=listCur[i].get(j).getCommitShortInfo().getMessage();
                            msg.setText(    "New commit to "+repoName[i]+":\n"+
                                            "Date:"+date.toString()+
                                            "\nHash:"+listCur[i].get(j).getSHA1()+
                                            "\nInfo:"+curMsg);
                            logger.info("New commit to "+repoName[i]);
                            logger.info("Hash: "+listCur[i].get(j).getSHA1());
                            logger.info("Info: curMsg");
                            Main.bot.execute(msg);
                        }
                        if(listCur[i].size()!=0)
                        {
                            lastDate[i]=listCur[i].get(0).getCommitDate();
                            lastSha[i]=listCur[i].get(0).getSHA1();
                        }
                    }

                }
                catch(Exception e)
                {
                    logger.error("Failed to fetch latest commits info!");
                    logger.error(e.toString());
                }
                    finally {
                        try {
                            Thread.sleep(60000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        };
        threadGithub.start();

    }
}
