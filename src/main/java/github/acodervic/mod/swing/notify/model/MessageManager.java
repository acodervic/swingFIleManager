package github.acodervic.mod.swing.notify.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import org.jdesktop.swingx.JXCollapsiblePane;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import github.acodervic.mod.UtilFunInter;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.TimeUtil;
import github.acodervic.mod.swing.messagebox.MessagePoster;
import github.acodervic.mod.thread.TimePool;
import net.miginfocom.swing.MigLayout;


/**
 * 通知消息管理器,本质上也是一个swing组建,支持消息下拉
 */
public class MessageManager extends JXTaskPaneContainer implements UtilFunInter {

    List<Message>  messages=new ArrayList<>();
    List<Consumer<Message>>  onExpandedFuns=new ArrayList<>();//当有节点被关闭时候调用
    List<Consumer<Message>>  onCollapsedFuns=new ArrayList<>();//当有节点被展开时候调用
    Boolean onlyShowNotCheckedMessage=false;
    Boolean autoExpandedMessage=false;//是否自动展开消息
    MessagePoster messagePoster;//用于投递通知的通知处理器
    public synchronized void addMessage(Message msg,Boolean show) {
        messages.add(msg);
        generateMessageJXTaskPanel(msg);//先生成一个消息tab
        if (show) {
            showBaseMessage(msg);
        }
        updateTitle();//启动轮讯
    }

    public void updateTitle() {
        TimePool.getStaticTimePool().Interval(5000, ()  ->{
            for (int i = 0; i < messages.size(); i++) {
                messages.get(i).updateBewttenPrintTimeString();
            }
        });
    }

    /**
     * 设置是否只显示已经查看的消息,设置完成后要update
     * @param onlyShowNotCheckedMessage the onlyShowNotCheckedMessage to set
     */
    public void setOnlyShowNotCheckedMessage(Boolean onlyShowNotCheckedMessage) {
        this.onlyShowNotCheckedMessage = onlyShowNotCheckedMessage;
    }


    /**
     * 设置所有未读消息为已读,
     * @return 返回成功修改消息的状态
     */
    public int  setReadedAll() {
        int count=0;
        for (Message message : messages) {
            if (message.isChecked()) {
                message.checked();;
                count+=1;
            }
        }
        return count;
    }
    /**
     * 添加一个当消息被展开时候的监听器
     * @param fun
     */
    void addOnMessageExpandedFuns(Consumer<Message> fun){
        this.onExpandedFuns.add(fun);
    }
    /**
     * 添加一个当消息被折叠时候的监听器
     * @param fun
     */
    void addOnMessageCollapsedFuns(Consumer<Message> fun){
        this.onCollapsedFuns.add(fun);
    }


    /**
     * 初始化消息查看的监听器
     */
    public void initMessageCheckListener() {
        //当消息被展开则变为check=true状态
        addOnMessageExpandedFuns(msg  ->{
            msg.checked();
        });
    }

      synchronized  JXTaskPane generateMessageJXTaskPanel(Message msg) {
        JXTaskPane jxTaskPane=null;
        if (notNull(msg)) {
              jxTaskPane = new JXTaskPane();
            msg.setMessagePane(jxTaskPane);
            msg.setManager(this);
            jxTaskPane.setTitle(msg.getTitle()+"           ("+TimeUtil.getBetweenPrintTime(time_nowLong()-msg.getCreatedDate().getTime()));
            jxTaskPane.setIcon(msg.getIcon().get());
            jxTaskPane.addPropertyChangeListener(JXCollapsiblePane.ANIMATION_STATE_KEY, new PropertyChangeListener() {
                //当被折叠展开十行调用
				@Override
				public void propertyChange(PropertyChangeEvent e) {
                    if (e.getNewValue().equals("expanded")) {
                        for (int i = 0; i < onExpandedFuns.size(); i++) {
                            onExpandedFuns.get(i).accept(msg);;

                        }
                    } else if (e.getNewValue().equals("collapsed")) {
                        for (int i = 0; i < onCollapsedFuns.size(); i++) {
                            onCollapsedFuns.get(i).accept(msg);
                        }
                    }
				}
               });
               if (!autoExpandedMessage) {
                jxTaskPane.setCollapsed(true);
               }
            Opt<Function<Message, JScrollPane>> randerBodyJpanlFun = msg.getRanderMessageManagerBodyJpanelFun();
            JScrollPane randerPanel=null;
            if (randerBodyJpanlFun.notNull_()) {
                  randerPanel = randerBodyJpanlFun.get().apply(msg);
            }

            if (isNull(randerPanel)) {
                //如果没有设置详细面板则自动构建一个以Jlable的面板
                randerPanel=new  JScrollPane();
                JTextArea textArea = new JTextArea();
                textArea.setWrapStyleWord(true);

                textArea.setLineWrap(true);
                textArea.setFont(new Font("Serif", Font.BOLD, 14));
                textArea.setText(msg.getBody());
                textArea.setWrapStyleWord(true);
                textArea.setBackground(randerPanel.getBackground());
                textArea.setEditable(false);
                randerPanel.setViewportView(textArea);
            }
            jxTaskPane.add(randerPanel);
            add(jxTaskPane,"wrap,width 100%");
        }
        return jxTaskPane;
    }
    public synchronized void addMessageAndShow(Message msg) {
        addMessage(msg, true);
    }



    /**
     * 显示一个消息弹窗
     * @param msg
     */
    public   void showBaseMessage(Message msg) {
        if (notNull(this.messagePoster)) {
            this.messagePoster.show(msg);
        }else{
             throw runtimeException("没有配置messagePoster!");
        }
    }

    /**
     * 显示一个包含更多消息的消息弹窗
     * @param msg
     */
    public void showMoreMessage(Message msg) {

    }

    public static void main(String[] args) {
        JFrame jf=new JFrame();
        MessageManager manager=new MessageManager();
        jf.add(manager);
        for (int i = 0; i < 10; i++) {
            Message message=new Message();
            message.setIcon(new ImageIcon(message.getClass().getClassLoader().getResource("notfiy1.png")));
            message.setTitle("test"+i);
            message.setBody("body"+i);
            manager.addMessageAndShow(message);
        }
        jf.show();
    }

    /**
     * @param autoExpandedMessage 是否自动展开消息
     */
    public MessageManager(Boolean autoExpandedMessage,MessagePoster messagePoster) {
        super();
        this.messagePoster=messagePoster;
        this.autoExpandedMessage=autoExpandedMessage;
        setLayout(new MigLayout("debug","[]"));
        initMessageCheckListener();
    }

     /**
     * @param autoExpandedMessage 是否自动展开消息
     */
    public MessageManager() {
        this(false, null);
    }


        /**
     * 不要再显示
     */
    public void hideMessage(Message message) {
        if (message.getMessagePane()!=null) {
            remove(message.getMessagePane());
            updateUI();//刷新
        }
    }


    /**
     * 获取所有已经查看了的消息数量
     * @return
     */
    public int  getCheckedMessagesCount() {
        int count=0;
        for (int i = 0; i < messages.size(); i++) {
            if (messages.get(i).isChecked()) {
                count+=1;
            }
        }
        return count;
    }


    /**
     * 获取所有消息数量
     * @return
     */
    public int  getAllMessagesCount() {
        return this.messages.size();
    }

    /**
     * 获取所有未查看了的消息数量
     * @return
     */
    public int  getUnCheckedMessagesCount() {
        int count=0;
        for (int i = 0; i < messages.size(); i++) {
            if (!messages.get(i).isChecked()) {
                count+=1;
            }
        }
        return count;
    }

    /**
     * 更新ui视图
     */
    public void update() {
        updateUI();;
    }



}
