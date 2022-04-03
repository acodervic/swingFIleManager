package github.acodervic.filemanager.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import javax.swing.JProgressBar;

import github.acodervic.filemanager.device.DeviceMounter;
import github.acodervic.filemanager.gui.BackgroundTaskManagerFrame;
import github.acodervic.filemanager.gui.MainFrame;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.model.TempFileOperation;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.swing.MyComponent;
import net.coobird.thumbnailator.Thumbnails;

/**
 * 全局BackgroundTaskManager 系统运行后只有一个
 */
public class BackgroundTaskManager implements GuiUtil {
    List<BackgroundTask> allTasks=new ArrayList<>();
    List<MainFrame>  allMainFrames=new ArrayList();;//所有注册的文件窗口
    List<Consumer<BackgroundTask>> onBackGroundFishedFuns=new ArrayList<>();//当任务结束
    List<Consumer<BackgroundTask>> onBackGroundAddedFuns=new ArrayList<>();//当任务被添加
    List<Consumer<BackgroundTask>> onBackGroundStartedFuns=new ArrayList<>();//当任务启动

    Stack<TempFileOperation> tempFileOperationStack=new Stack<>(); //存储 已经操作过的TempFileOperation对象
    TempFileOperation tempFileOperation=new TempFileOperation();//当前的临时操作对象
    



    /**
     * @return the tempFileOperation
     */
    public TempFileOperation getTempFileOperation() {
        return tempFileOperation;
    }
    

    /**
     * @return the tempFileOperationStack
     */
    public Stack<TempFileOperation> getTempFileOperationStack() {
        return tempFileOperationStack;
    }
    
    BackgroundTaskManagerFrame backgroundTaskManagerFrame=new BackgroundTaskManagerFrame(this);

    int allProgressInt=100;//全部任务的进度信息
    BackgroundTaskManager me;



    /**
     * @return the onBackGroundStartedFuns
     */
    public List<Consumer<BackgroundTask>> getOnBackGroundStartedFuns() {
        return onBackGroundStartedFuns;
    }
    /**
     * @return the onBackGroundAddFuns
     */
    public List<Consumer<BackgroundTask>> getOnBackGroundAddedFuns() {
        return onBackGroundAddedFuns;
    }

    /**
     * @return the onBackGroundFishedFuns
     */
    public List<Consumer<BackgroundTask>> getOnBackGroundFishedFuns() {
        return onBackGroundFishedFuns;
    }

    public void addOnBackGroundFishedFuns(Consumer<BackgroundTask> fun) {
        if (!onBackGroundFishedFuns.contains(fun)) {
            onBackGroundFishedFuns.add(fun);
        }
    }

    public void addOnBackGroundAddedFuns(Consumer<BackgroundTask> fun) {
        if (!onBackGroundAddedFuns.contains(fun)) {
            onBackGroundAddedFuns.add(fun);
        }
    }



    public void addOnBackGroundStaredFuns(Consumer<BackgroundTask> fun) {
        if (!onBackGroundStartedFuns.contains(fun)) {
            onBackGroundStartedFuns.add(fun);
        }
    }

    public void addMainFrame(MainFrame mainFrame) {
        if (!allMainFrames.contains(mainFrame)) {
            allMainFrames.add(mainFrame);
        }
    }
    public BackgroundTaskManager() {
        me=this;
        //启动用于计算全部任务进度的线程
        new Thread(()  ->{
            logInfo("BackgroundTaskManager-进度统计线程已经启动");
            while (true) {
                try {
                    sleep(150);
                    List<BackgroundTask> runningTasks = getRunningTasks();
                    if (runningTasks.size() == 0) {
                        me.allProgressInt = 100;
                        updateAllBackroundTsaskProgressBar(me.allProgressInt);
                    } else {
                        // 计算总体进度
                        int all = runningTasks.size() * 100;
                        int count = 0;
                        for (int i = 0; i < runningTasks.size(); i++) {
                            BackgroundTask task = runningTasks.get(i);
                            count += task.getProgress();
                        }
                        int newCount = calc(count,all) ;
                        if (newCount != me.allProgressInt) {
                            me.allProgressInt = newCount;
                            updateAllBackroundTsaskProgressBar(me.allProgressInt);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "BackgroundTaskManager-AllProgressInt-Count-Thread").start();
    }
    private void updateAllBackroundTsaskProgressBar(int newCount) {
        // 更新每个窗口的总体进度图标
        for (int i = 0; i < me.allMainFrames.size(); i++) {
            MainFrame mainFrame = me.allMainFrames.get(i);
            MainPanel mainPanel = mainFrame.getMainPanel();
            if (notNull(mainPanel)) {
                JProgressBar allBackgoundTaskProgressBar = mainFrame.getMainPanel().getTopToolsPanel()
                        .getAllBackgoundTaskProgressBar();
                allBackgoundTaskProgressBar.setValue(newCount);
            }
        }
    }

    /**
     * @return the allTask
     */
    public List<BackgroundTask> getAllTasks() {
        return allTasks;
    }

    /**
     * 获得已经完成的任务列表
     * 
     * @return
     */
    public List<BackgroundTask> getFinishedTasks() {
        List<BackgroundTask> s = new ArrayList<>();
        s.addAll(getAllTasks());
        s.removeIf(t -> {
            return !t.isFinish();
        });
        return s;
    }

    /**
     * 获得运行中的任务列表
     * 
     * @return
     */
    public List<BackgroundTask> getRunningTasks() {
        List<BackgroundTask> s = new ArrayList<>();
        s.addAll(getAllTasks());
        s.removeIf(t -> {
            return !t.isRunning();
        });
        return s;
    }


    public void execTask(BackgroundTask task) {
        if (notNull(task)) {
            //注册任务
            allTasks.add(task);
            List<Consumer<BackgroundTask>> onBackGroundAddedFuns = getOnBackGroundAddedFuns();
            for (int i = 0; i < onBackGroundAddedFuns.size(); i++) {
                Consumer<BackgroundTask> consumer = onBackGroundAddedFuns.get(i);
                try {
                    consumer.accept(task);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }
            task.setManager(this);
            task.start();
        }
    }

    public void showBackgroundTaskManagerFrame() {
        backgroundTaskManagerFrame.setTitle("backgroundTaskManagerFrame_dialog");
        backgroundTaskManagerFrame.setSize(300, 400);
        backgroundTaskManagerFrame.setVisible(true);
        MyComponent.moveFrameCenter(backgroundTaskManagerFrame);
    }




}
