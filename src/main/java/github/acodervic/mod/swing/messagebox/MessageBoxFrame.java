package github.acodervic.mod.swing.messagebox;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Function;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import github.acodervic.mod.data.Opt;
import github.acodervic.mod.swing.MyComponent;
import github.acodervic.mod.swing.notify.model.Message;
import net.miginfocom.swing.MigLayout;

public class MessageBoxFrame extends JFrame {

    boolean dontCose = false;
    JPanel msgPanel;//整个消息面板包含图标 标题和消息体
    JPanel msgTitlePanel;//显示title的Panel
    JScrollPane msgBodyTextAreScroll;//显示title的Panel
    Message message;
    Color splitLineColor=Color.gray;


    /**
     * @return the splitLineColor
     */
    public Color getSplitLineColor() {
        return splitLineColor;
    }

    /**
     * @param splitLineColor the splitLineColor to set
     */
    public void setSplitLineColor(Color splitLineColor) {
        this.splitLineColor = splitLineColor;
    }

    /**
     * @return the msgPanel
     */
    public JPanel getMsgPanel() {
        return msgPanel;
    }

    /**
     * @return the message
     */
    public Message getMessage() {
        return message;
    }

/**
 * @return the msgBodyTextAreScroll
 */
public JScrollPane getMsgBodyTextAreScroll() {
    return msgBodyTextAreScroll;
}
    /**
     * @return the msgTitlePanel
     */
    public JPanel getMsgTitlePanel() {
        return msgTitlePanel;
    }
    /**
     * @return the dontCose
     */
    public boolean isDontCose() {
        return dontCose;
    }

    /**
     * @return the dontCose
     */
    public void dontClose() {
        dontCose = true;
        ;
    }

    /**
     * @return the dontCose
     */
    public void needClose() {
        dontCose = false;
        ;
    }

    public MessageBoxFrame(Message message, int messageFrameWidth, int messageFrameHight, int iconWidth,
            ImageIcon defaultIcon) {
        super();
        this.message=message;
        MessageBoxFrame me=this;
        setAutoRequestFocus(false);//
        setAlwaysOnTop(true);
        setSize(messageFrameWidth, messageFrameHight);
        setTitle("_dialog");
        setUndecorated(true);// 无边框无标题
        setResizable(true);// 锁死大小
        JPanel messageTitleAndBodypanel = new JPanel(new MigLayout());
         msgPanel = new JPanel(new MigLayout());
        JPanel IconPanel = new JPanel(new GridBagLayout());
        // 重新调整图标大小
        ImageIcon imageIcon = message.getIcon().get();
        if (imageIcon == null) {
            imageIcon = defaultIcon;
        }
        Image newimg = imageIcon.getImage().getScaledInstance(iconWidth, iconWidth, java.awt.Image.SCALE_SMOOTH);
        imageIcon = new ImageIcon(newimg);
        JLabel iconLable = new JLabel(imageIcon);
        iconLable.setBackground(IconPanel.getBackground());
        IconPanel.add(iconLable);
        msgTitlePanel=new JPanel();
        msgBodyTextAreScroll = new JScrollPane();
        MessageBoxFrame apply=null;
        Opt<Function<MessageBoxFrame,MessageBoxFrame>> randerMessageBoxBodyJpanlFun = message.getRanderMessageBoxBodyJpanlFun();
        if (randerMessageBoxBodyJpanlFun.notNull_()) {
            //自定义Ui
              apply = randerMessageBoxBodyJpanlFun.get().apply(me);

        }
        if (apply==null) {
            //默认还是生成body样式
            JTextArea bodyTextArea = new JTextArea();
            bodyTextArea.setLineWrap(true);
            bodyTextArea.setFont(new Font("Serif", Font.BOLD, 14));
            bodyTextArea.setText(message.getBody());
            bodyTextArea.setWrapStyleWord(true);
            bodyTextArea.setBackground(messageTitleAndBodypanel.getBackground());
            bodyTextArea.setEditable(false);
            msgBodyTextAreScroll.setViewportView(bodyTextArea);
        }


        int rightWidth = (messageFrameWidth - iconWidth - 20);
        int rightHeight = (messageFrameHight - 60);
        JLabel titleLable = new JLabel(message.getTitle());
        titleLable.setFont(new Font("Serif", Font.BOLD, 18));
        msgTitlePanel.add(titleLable);
        messageTitleAndBodypanel.add(msgTitlePanel, "wrap");



        msgBodyTextAreScroll.getVerticalScrollBar().setPreferredSize(new Dimension(10, 0));
        msgBodyTextAreScroll.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, splitLineColor));
        messageTitleAndBodypanel.add(msgBodyTextAreScroll, "wrap,width " + rightWidth + "!,h  " + rightHeight + "!");
        msgPanel.add(IconPanel, "width " + iconWidth + "!,h 90%");
        msgPanel.add(messageTitleAndBodypanel, "width " + rightWidth + "!,h 90%!");
        addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (MyComponent.isMouseWithinComponent(me)) {
                    dontClose();
                 };
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!MyComponent.isMouseWithinComponent(me)) {
                    needClose();
                 };

            }

        });
        add(msgPanel);

    }


}
