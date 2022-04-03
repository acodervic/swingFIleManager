package github.acodervic.filemanager.thread;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JPanel;

/**
 * 代表一个后台任务,会作为一个线程后台运行
 */
public abstract class BackgroundTask {
    final static String STATUS_RUNNING="STATUS_RUNNING"; 
    final static String STATUS_WAIT="STATUS_WAIT"; 
    final static String STATUS_PAUSEING="STATUS_PAUSEING"; 
    final static String STATUS_FINISH="STATUS_FINISH"; 
    final static String STATUS_FINISH_WITH_STOP="STATUS_FINISH_WITH_STOP"; 
    final static String STATUS_CALC="STATUS_CALC"; 
    String status=STATUS_WAIT;
    HashMap<String,String> logs=new HashMap<>();//key=当前时间撮字符串 value=msg
    

    Boolean pauseIng=false;
    Boolean stop=false;

    Date createdTIme=new Date();
    Thread execTaskThread;
    BackgroundTask me;
    BackgroundTaskManager manager;
 


    /**
     * @param manager the manager to set
     */
    public void setManager(BackgroundTaskManager manager) {
        this.manager = manager;
    }

    /**
     * @return the manager
     */
    public BackgroundTaskManager getManager() {
        return manager;
    }
 
    public  synchronized Thread getThread(){
        execTaskThread=new Thread(()  ->{
            me.status=STATUS_RUNNING;
            log("启动后台任务"+getName());
            try {
                List<Consumer<BackgroundTask>> onBackGroundStartedFuns = getManager().getOnBackGroundStartedFuns();
                for (int i = 0; i < onBackGroundStartedFuns.size(); i++) {
                    Consumer<BackgroundTask> consumer = onBackGroundStartedFuns.get(i);
                    try {
                        consumer.accept(me);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    
                }
                action();
            } catch (Exception e) {
                e.printStackTrace();
            }
            me.status=STATUS_FINISH;
            List<Consumer<BackgroundTask>> onBackGroundFinishFuns = getManager().getOnBackGroundFishedFuns();
            for (int i = 0; i < onBackGroundFinishFuns.size(); i++) {
                Consumer<BackgroundTask> consumer = onBackGroundFinishFuns.get(i);
                try {
                    consumer.accept(me);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }
        });
        execTaskThread.setName("BackgroundTask-Thread-"+getName());
        return execTaskThread;
    }
    /**
     * @return the createdTIme
     */
    public Date getCreatedTIme() {
        return createdTIme;
    }
    /**
     * 获取当前任务的进度 1-100之间的数值
     * @return
     */
    public abstract int  getProgress();

    /**
     * 任务名称
     * @return
     */
    public abstract String   getName();


    /**
     * @return the logs
     */
    public HashMap<String, String> getLogs() {
        return logs;
    }
    

    /**
     * 记录日志
     * @param data
     */
    public void log(String data ) {
        this.logs.put(System.currentTimeMillis()+"", data);
    }
    
    
    /**
     * 执行任务的主函数
     */
    public abstract void action();

    public boolean isRunning(){
        return  status.equals(STATUS_RUNNING);
    }
    public boolean isFinish(){
        return  status.equals(STATUS_FINISH);
    }
    
    public boolean isWaiting(){
        return  status.equals(STATUS_WAIT);
    }
    
    public boolean isStarted(){
        return !isWaiting();
    }

    public void start() {
        me=this;
        getThread().start();
        
    }
    public  void  onStop(){
        this.stop=true;
    };

    public  void  onPause(){
        this.pauseIng=true;
    };
    public  void  onResum(){
        this.pauseIng=false;
    };

    public abstract JPanel getTaskDIsplayPanel();

    public String getStatuString(){
        switch (status) {
            case STATUS_FINISH:
                return "已完成";
                case STATUS_RUNNING:
                return "运行中"+getProgress()+"%";
                case STATUS_WAIT:
                return "等待中";
                case STATUS_CALC:
                return "计算中";
                case STATUS_FINISH_WITH_STOP:
                return "手动停止";
                case STATUS_PAUSEING:
                return "暂停中";
            default:
                return  "未知";
        }
    }
}
