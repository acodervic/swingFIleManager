package github.acodervic.filemanager.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.vfs2.FileObject;

import github.acodervic.filemanager.FSUtil;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.list.LList;
import github.acodervic.mod.io.FileUtil;
import github.acodervic.mod.io.IoType;
import github.acodervic.mod.thread.AsyncTaskRunnable;

/**
 * 这是一个搜索线程,用于搜索目录下的资源和文件
 */
public class DeepSreachThread extends Thread implements GuiUtil {
    Consumer<Integer> progressCall;// 计算进度的方法还未知
    Consumer<RESWallper> onNewFoundCall;// 当新找到资源时候的回调
    Consumer<List<RESWallper>> onFinishCall;// 当新找到资源时候的回调
    FileObject startDir;
    Integer progress = 0;// 进度信息
    Integer searchResources = 0;// 已经搜索的数量
    List<RESWallper> foundResources = new ArrayList<>();// 已经找到的资源列表
    String seachRegex;
    Integer maxReslut=5000;//最大结果数量

    public DeepSreachThread(FileObject startDir, Consumer<RESWallper> onNewFoundCall,String seachRegex) {
        super();
        setName("DeepSreachThread_" + startDir.getName());
        this.startDir = startDir;
        this.onNewFoundCall = onNewFoundCall;
        this.seachRegex=seachRegex;
    }

    /**
     * @param onFinishCall the onFinishCall to set
     */
    public void setOnFinishCall(Consumer<List<RESWallper>> onFinishCall) {
        this.onFinishCall = onFinishCall;
    }

    AsyncTaskRunnable searchAsyncTask;

    @Override
    public void run() {
        logInfo("开始搜索" + startDir.toString());
        searchAsyncTask = new AsyncTaskRunnable(() -> {
            List<FileObject> outDIrOrFiles = new LList<>();
            // 通知调用1
            ((LList<FileObject>) outDIrOrFiles).setOnAddCallFun(file -> {
                try {
                    RESWallper res=new RESWallper(file);
                    foundResources.add(res);
                    onNewFoundCall.accept(res);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            try {
                FSUtil.getAllDirAndFileByDir(outDIrOrFiles, startDir, 9999, -1, seachRegex, true, IoType.FILE_AND_DIR);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });
        searchAsyncTask.executeFun();
        searchFIshed();
    }

    public void searchFIshed() {
        if (notNull(onFinishCall)) {
            onFinishCall.accept(foundResources);
        }
        logInfo("搜索完成" + startDir.toString());
        
    }

    public void startSearch() {
        stopSearch();
        super.start();
    }

    public void stopSearch() {
        if (this.isAlive()) {
            stop();
            searchFIshed();
        }
    }

    public void syncWaitFinish() throws InterruptedException {
        searchAsyncTask.syncWaitFinish();
    }
}
