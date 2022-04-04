package github.acodervic.mod.swing.notify.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.JXTaskPane;

import github.acodervic.mod.UtilFunInter;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.TimeUtil;
import github.acodervic.mod.swing.messagebox.MessageBoxFrame;

public class Message  implements UtilFunInter {
     Opt<ImageIcon> icon=new Opt<>();
    String header="";
    String title="";
    String body="";
    Date createdDate=new Date();
    Date checkedDate;//查看时间
    Date showedDate;//弹窗显示i的时间,
    JXTaskPane messagePane;
    Map<String,Object> dataMap=new Hashtable<>();//用来存放数据的dataMap
    //用来渲染消息体的函数
    Opt<Function<Message,JScrollPane> >  randerMessageManagerBodyJpanelFun=new Opt<>();//用来渲染messageManager的详细Panel内容的函数
    Opt<Function<MessageBoxFrame,MessageBoxFrame> >  randerMessageBoxBodyJpanelFun=new Opt<>();//用来渲染messageManager的详细Panel内容的函数,如果函数返回null,则依然会构造默认的消息Body,否则则使用函数内部渲染的结果
    MessageManager manager;


    /**
     * 设置渲染消息管理器展开面板的函数
     * @param randerMessageBoxBodyJpanlFun the randerMessageBoxBodyJpanlFun to set
     */
    public void setRanderMessageBoxBodyJpanelFun(Function<MessageBoxFrame,MessageBoxFrame> randerMessageBoxBodyJpanlFun) {
        this.randerMessageBoxBodyJpanelFun.of(randerMessageBoxBodyJpanlFun);
    }



    /**
     * 设置渲染弹窗消息的函数,如果函数返回null,则依然会构造默认的消息Body,否则则使用函数内部渲染的结果
     * @param randerMessageManagerBodyJpanlFun the randerMessageManagerBodyJpanlFun to set
     */
    public void setRanderMessageManagerBodyJpanlFun(Function<Message, JScrollPane> randerMessageManagerBodyJpanlFun) {
        this.randerMessageManagerBodyJpanelFun.of(randerMessageManagerBodyJpanlFun);
    }
    /**
     * @return the showedDate
     */
    public Date getShowedDate() {
        return showedDate;
    }
    /**
     * @param showedDate the showedDate to set
     */
    public void setShowedDate(Date showedDate) {
        this.showedDate = showedDate;
    }
    /**
     * 是否已经显示
     * @return
     */
    public boolean isShowed() {
        return this.showedDate!=null;
    }
    /**
     * 是否已经检查
     * @return
     */
    public boolean isChecked() {
        return  checkedDate!=null;
    }

        /**
     * 是否已经检查
     * @return
     */
    public void  checked() {
        checkedDate=new Date();
    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }



    /**
     * @param icon the icon to set
     */
    public void setIcon(ImageIcon icon) {
        this.icon.of(icon);
    }

    /**
     * @return the icon
     */
    public Opt<ImageIcon> getIcon() {
        return icon;
    }

    /**
     * @return the randerMessageBoxBodyJpanlFun
     */
    public Opt<Function<MessageBoxFrame,MessageBoxFrame>> getRanderMessageBoxBodyJpanlFun() {
        return randerMessageBoxBodyJpanelFun;
    }
    /**
     * @return the randerMessageManagerBodyJpanlFun
     */
    public Opt<Function<Message, JScrollPane>> getRanderMessageManagerBodyJpanelFun() {
        return randerMessageManagerBodyJpanelFun;
    }


    /**
     * @return the header
     */
    public String getHeader() {
        return header;
    }

    /**
     * @param header the header to set
     */
    public void setHeader(String header) {
        this.header = header;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the body
     */
    public String getBody() {
        return body;
    }

    /**
     * @param body the body to set
     */
    public void setBody(String body) {
        this.body = body;
    }



    /**
     * @return the messagePane
     */
    public JXTaskPane getMessagePane() {
        return messagePane;
    }

    /**
     * @param messagePane the messagePane to set
     */
    public void setMessagePane(JXTaskPane messagePane) {
        this.messagePane = messagePane;
    }

    public void updateBewttenPrintTimeString() {
        if (messagePane!=null) {
            try {
                String betweenPrintMinTime = TimeUtil.getBetweenPrintMaxTime(time_nowLong()-createdDate.getTime());
                if (!betweenPrintMinTime.equals("")) {
                    betweenPrintMinTime= ("("+betweenPrintMinTime.trim()+"前)   ");
                }
                messagePane.setTitle(betweenPrintMinTime+title);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @return the dataMap
     */
    public Map<String, Object> getDataMap() {
        return dataMap;
    }


    /**
     * @param manager the manager to set
     */
    public void setManager(MessageManager manager) {
        this.manager = manager;
    }
    /**
     * @return the manager
     */
    public MessageManager getManager() {
        return manager;
    }

            /**
     * 不要再显示
     */
    public void hideMessage(Message message) {
        manager.hideMessage(this);
    }


}
