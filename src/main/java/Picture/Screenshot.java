package Picture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Screenshot
{
    public static void main(String[] args) throws IOException {
        String s="大家，好久不见！\n" +
                "这次的作品将会是很轻松的主题，大家请放心阅读！\n" +
                "如果说OIerDream是希望大家了解我作为OIer的内心世界以及成长历程，那么，这部作品的目的，就是希望大家以我最亲近的人的身份，看一看我这个跨性别者兼精神病人的内心世界。\n" +
                "你一定会注意到，这次的主角们和OIerDream中的是一样的，的确如此，这是有意为之的，角色们的经历有一些不同，但性格是一样的。你可以理解为他们是相同的人，也可以理解为不同的人，问题不大。\n" +
                "请时刻记住，我不能代表任何人。\n" +
                "这次，让我们换一个视角，以Mico的视角，来一段甜甜的恋爱故事吧。\n" +
                "现实中，我们是不可能在一起的，但在这里，一切皆有可能。\n" +
                "我希望创造一个大家都有好结局的世界，这是我的使命。\n" +
                "好啦，废话不多说，开始吧！";
        Font font=new Font("Arial", Font.PLAIN,12);
        StringFrag stringFrag=new StringFrag(font,488);
        List<String> frag=stringFrag.stringFrag(s);
        BufferedImage image=new BufferedImage(512,19+16*frag.size(),2);
        Graphics2D graphic=GraphicsEnvironment.getLocalGraphicsEnvironment().createGraphics(image);
        graphic.setBackground(Color.orange);
        graphic.clearRect(0,0,512,19+16*frag.size());
        graphic.setPaint(Color.black);
        FontRenderContext fontRenderContext=graphic.getFontRenderContext();
        int ix=12,iy=23;
        for(int i=0;i<frag.size();i++)
        {
            LineMetrics metrics=font.getLineMetrics(frag.get(i),fontRenderContext);
            graphic.drawString(frag.get(i),ix,iy);
            iy+=16;
        }
        ImageIO.write(image,"png",new File("C:\\Users\\Vicky\\Desktop\\bot-jar\\cfg\\a.png"));
    }
}