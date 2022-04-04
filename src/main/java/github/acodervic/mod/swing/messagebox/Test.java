package github.acodervic.mod.swing.messagebox;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import github.acodervic.mod.utilFun;
import github.acodervic.mod.swing.notify.model.Message;

public class Test {

    public static void main(String[] args) {
        MessagePoster poster=new MessagePoster();
        Message msg=new Message();
        for (int i = 0; i < 10; i++) {
            msg.setTitle("test");
            msg.setBody("test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222test11111111111111111111122222222222222222222");
            //msg.setIcon(new ImageIcon(Test.class.getClassLoader().getResource("notfiy1.png")));
            poster.show(msg);
            msg.setRanderMessageBoxBodyJpanelFun(ms  ->{
                JScrollPane msgBodyTextAreScroll = ms.getMsgBodyTextAreScroll();
                msgBodyTextAreScroll.setViewportView(new JLabel(ms.getMessage().getTitle()));
                ms.getMsgPanel().setBorder(BorderFactory.createLineBorder(Color.red));
                return null;
            });
        }
        utilFun.sleep(10000);
    }





}
