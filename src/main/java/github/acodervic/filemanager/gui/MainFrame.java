package github.acodervic.filemanager.gui;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import github.acodervic.filemanager.config.XdgOpenConfigManager;
import github.acodervic.filemanager.device.DeviceMounter;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.model.TempFileOperation;
import github.acodervic.filemanager.thread.BackgroundTaskManager;
import github.acodervic.filemanager.treetable.FileSystemModel;
import github.acodervic.filemanager.treetable.JTreeTable;
import github.acodervic.filemanager.treetable.TreeTableCellRenderer;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.Value;
import github.acodervic.mod.thread.TimePool;

public class MainFrame extends JFrame implements WindowListener, GuiUtil {
    FileObject startDIr;// 起始目录
    MainPanel mainPanel;
    WatchService dirWatchService;
    List<Runnable> onFrameShowedFun = new ArrayList<>();
    HashMap<WatchKey, List<RESWallper>> watchServicesMap = new HashMap<>();
    BackgroundTaskManager backgroundTaskManager;
    XdgOpenConfigManager xdgOpenConfigManager=new XdgOpenConfigManager();  
    DeviceMounter deviceMounter;
    Thread jtreeTableRefreshUiThread;


    /**
     * @param deviceMounter the deviceMounter to set
     */
    public void setDeviceMounter(DeviceMounter deviceMounter) {
        this.deviceMounter = deviceMounter;
    }

    /**
     * @return the deviceMounter
     */
    public DeviceMounter getDeviceMounter() {
        return deviceMounter;
    }
    
 
    /**
     * @return the tempFileOperation
     */
    public TempFileOperation getTempFileOperation() {
        return backgroundTaskManager.getTempFileOperation();
    }

    public boolean isWatching(RESWallper targetDir) {
        Collection<List<RESWallper>> values = watchServicesMap.values();
        for (List<RESWallper> dirs : values) {
            for (int i = 0; i < dirs.size(); i++) {
                if (dirs.get(i).getAbsolutePath().equals(targetDir.getAbsolutePath())) {
                    //添加一个关联的res
                    dirs.add(targetDir);
                    return true;
                }
            }
        }
        
        return false;
    }

    public MainFrame(FileObject dirRes,BackgroundTaskManager backgroundTaskManager,DeviceMounter deviceMounter) {
        this.startDIr = dirRes;
        this.backgroundTaskManager=backgroundTaskManager;
        this.deviceMounter=deviceMounter;
        this.backgroundTaskManager.addMainFrame(this);
        mainPanel = new MainPanel(this);
        add(mainPanel);
        addWindowListener(this);
        startWatchThread();
        TimePool.getStaticTimePool().Interval(1000, ()  ->{
            print("当前监控的目录:"+watchServicesMap.size()+"个_"+watchServicesMap.values().toString());
            
        });
        //startJtreeTableRefreshUiThread();
    }

    public void addOnonFrameShowedFun(Runnable run) {
        if (!onFrameShowedFun.contains(run)) {
            onFrameShowedFun.add(run);
        }
    }

    /**
     * @return the mainPanel
     */
    public MainPanel getMainPanel() {
        return mainPanel;
    }

    /**
     * @return the dirWatchService
     */
    public synchronized WatchService getDirWatchService() {
        if (isNull(dirWatchService)) {
            try {
                dirWatchService = FileSystems.getDefault().newWatchService();// 监控目录更改
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return dirWatchService;
    }

    /**
     * 删除注册dir
     * 
     * @param dir
     */
    public synchronized void unRegisterWatchDir(RESWallper dir) {
        if (dir.isFile()) {
            return ;
        }
        List<WatchKey > needunRegisterList=new ArrayList<>();
        watchServicesMap.keySet().forEach(key  ->{
            List<RESWallper> list = watchServicesMap.get(key);
            list.removeIf(d  ->{
                return d.sameRefObj(dir);
            });
           
            if (list.size()==0) {
                needunRegisterList.add(key);

            }
        });

        for (int i = 0; i < needunRegisterList.size(); i++) {
            WatchKey watchKey = needunRegisterList.get(i);
            logInfo("取消目录监控:"+dir.getAbsolutePath()+"  ");
            watchKey.cancel();
            watchServicesMap.remove(watchKey);
        }
        logInfo("当前监控的目录数量:"+watchServicesMap.size());

        

    }

    public synchronized void registerWatchDir(RESWallper dir) {
        if (dir.isFile()) {
            return;
        }
        if (!dir.isLocalFile()) {
            return ;//远程文件不可监控
        }
        if (isWatching(dir)) {
            logInfo("目录已经被监控" + dir.getAbsolutePath() + "无需重复监控");
            return ; 
        }
        if (dir.isLocalFile()) {
            try {

                WatchKey key = dir.getNioPath().register(getDirWatchService(), StandardWatchEventKinds.ENTRY_CREATE,
                        StandardWatchEventKinds.ENTRY_DELETE,StandardWatchEventKinds.ENTRY_MODIFY);
                    if (watchServicesMap.containsKey(key)) {
                        watchServicesMap.get(key).add(dir);
                    }else{
                        watchServicesMap.put(key, newList(dir));
                    }
                logInfo("开始监控" + dir.getAbsolutePath() + "目录更改");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    
        logInfo("当前监控的目录数量:"+watchServicesMap.size());
    }

    public void startWatchThread() {
        new Thread(() -> {

            WatchKey key;

            while (true) {
                try {
                    key = getDirWatchService().take();
                    CountDownLatch countDownLatch = new CountDownLatch(1);
                    Value keyval = new Value(key);
                    // 不管是新建还是删除,统一要在swing处理线程中处理,否则会出现各种bug
                    SwingUtilities.invokeLater(() -> {
                        try {
                            List<FileTableTab> fIleTableTabsList = getMainPanel().getCenterTabsPanel()
                                    .getFIleTableTabsList();
                            WatchKey wk = keyval.get(WatchKey.class);
                            for (WatchEvent event : wk.pollEvents()) {
                                Path up = (Path) event.context();
                                if (isNull(up)) {
                                    continue;
                                }
                                File file = up.toFile();
                                RESWallper parentDir = watchServicesMap.get(wk).get(0);
                                String fileName = file.getName();
                                FileObject reallFile = parentDir.getFileObj().resolveFile(fileName);
                                // up.resolve(up)
                                if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                                    logInfo(Thread.currentThread().getName() + "ENTRY_DELETE=>  "
                                            + reallFile.toString());
                                    if (reallFile.exists()) {
                                        continue;
                                    }
                                    // 更新表格
                                    for (int i = 0; i < fIleTableTabsList.size(); i++) {
                                        FileTableTab fileTableTab = fIleTableTabsList.get(i);
  
                                            FileTreeTablePanel fileTreeTablePanel = fileTableTab.getFileTreeTablePanel();
                                            try {
                                                JTreeTable treeTable = fileTreeTablePanel.getTreeTable();
                                                if (treeTable .getRootDir().get().getAbsolutePath().startsWith(reallFile.getParent().toString())) {
                                                    // 尝试更新所有表格
                                                    if (treeTable.getTree().getModel() instanceof FileSystemModel fsm) {
                                                        Opt<RESWallper> searchNodeByFile = fsm
                                                                .searchNodeByFile(new RESWallper(reallFile), true);
                                                        if (searchNodeByFile.notNull_()) {
                                                            // treeTable.get
                                                            logInfo("fireTreeNodesRemoved=" + reallFile);
                                                            if (reallFile.isFolder()) {
                                                                treeTable.fireTreeNodesRemoved(
                                                                        newList(new RESWallper(
                                                                                reallFile,
                                                                                fileName)));
                                                            } else {
                                                                treeTable.fireTreeNodesRemoved(
                                                                        newList(new RESWallper(reallFile,
                                                                                fileName)));
                                                            }
                                                            // 删除的时候不要刷新ui,因为可能出现一瞬间大量的删除操作,会卡死ui
                                                            treeTable.refreshUi();
                                                        } else {
                                                            System.out.println("error");
                                                        }
                                                    }
                                                }
                                            } catch (FileSystemException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }
                                  
                                    }
                                } else if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                                    if (!reallFile.exists()) {
                                        continue;
                                    }
                                    logInfo(Thread.currentThread().getName() + "ENTRY_CREATE=>  " + reallFile.toString()
                                            + " exists=" + reallFile.exists());
                                    // 更新表格

                                    for (int i = 0; i < fIleTableTabsList.size(); i++) {
                                        FileTableTab fileTableTab = fIleTableTabsList.get(i);
    
     
                                            FileTreeTablePanel fileTreeTablePanel = fileTableTab.getFileTreeTablePanel();
                                            try {
                                                JTreeTable treeTable = fileTreeTablePanel.getTreeTable();
                                                if (treeTable .getRootDir().get().getAbsolutePath().startsWith(reallFile.getParent().toString())) {
                                                    TreeTableCellRenderer tree = treeTable.getTree();
                                                    if (tree.getModel() instanceof FileSystemModel fsm) {
                                                        Opt<RESWallper> searchNodeByFile = fsm
                                                                .searchNodeByFile(new RESWallper(reallFile.getParent()),
                                                                        true);
                                                        if (searchNodeByFile.notNull_()) {
                                                            Opt childResByName = searchNodeByFile.get()
                                                                    .getChildResByName(
                                                                            reallFile.getName().getBaseName(),
                                                                            reallFile.isFile());
                                                            if (childResByName.isNull_()) {
                                                                // 代表还有添加这个资源到树
                                                                // 尝试更新所有表格
                                                                logInfo("fireTreeNodesInserted=" + reallFile);
                                                                if (reallFile.isFolder()) {
                                                                    RESWallper newDir = new RESWallper(reallFile,
                                                                            fileName);
                                                                    // 先将这个资源插入到父目录中
                                                                    if (searchNodeByFile.get().addChild(newDir)) {
                                                                        treeTable.fireTreeNodesInserted(
                                                                                parentDir, newList(newDir));
                                                                        treeTable.refreshUi();
                                                                    }
                                                                } else {
                                                                    RESWallper newFile = new RESWallper(reallFile,
                                                                            fileName);
                                                                    // 先将这个资源插入到父目录中
                                                                    if (searchNodeByFile.get().addChild(newFile)) {
                                                                        treeTable.fireTreeNodesInserted(
                                                                                parentDir,
                                                                                newList(newFile));
                                                                        treeTable.refreshUi();
                                                                    }
                                                                }
                                                                if (file.getAbsolutePath()
                                                                        .startsWith(FileTableTab.trashPath)) {
                                                                    // 垃圾箱的增加就不刷新了
                                                                    System.out.println("123123");

                                                                } else {
                                                                }

                                                            } else {
                                                                System.out.println("error");
                                                            }

                                                        }
                                                    }
                                                }
                                            } catch (FileSystemException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                    }

                                } else if (event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                                    //logInfo(Thread.currentThread().getName() + "ENTRY_MODIFY=>  " + reallFile.toString()
                                    //+ " exists=" + reallFile.exists());
                                    for (int i = 0; i < fIleTableTabsList.size(); i++) {
                                        FileTableTab fileTableTab = fIleTableTabsList.get(i);
 
                                        FileTreeTablePanel fileTreeTablePanel = fileTableTab.getFileTreeTablePanel();
                                            try {
                                                if (fileTreeTablePanel.getTreeTable().getRootDir().get().getAbsolutePath().startsWith(reallFile.getParent().toString())) {
                                                    JTreeTable treeTable = fileTreeTablePanel.getTreeTable();
                                                    TreeTableCellRenderer tree = treeTable.getTree();
                                                    if (tree.getModel() instanceof FileSystemModel fsm) {
                                                        Opt<RESWallper> searchNodeByFile = fsm
                                                                .searchNodeByFile(new RESWallper(reallFile.getParent()),
                                                                        true);
                                                        if (searchNodeByFile.notNull_()) {
                                                            Opt childResByName = searchNodeByFile.get()
                                                                    .getChildResByName(
                                                                            reallFile.getName().getBaseName(),
                                                                            reallFile.isFile());
                                                            if (childResByName.isNull_()) {
                                                            } else {
                                                                treeTable.refreshUi();
                                                            }
                                                        }
                                                    }
                                                }
                                            } catch (FileSystemException e) {
                                                e.printStackTrace();
                                            }

     
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        countDownLatch.countDown();
                    });
                    //等待swing处理完成
                    countDownLatch.await();
                    key.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
  

        }).start();
    }

    public void nodeChange(RESWallper resWallper) {
        List<FileTableTab> fIleTableTabsList = getMainPanel().getCenterTabsPanel().getFIleTableTabsList();
        for (int i = 0; i < fIleTableTabsList.size(); i++) {
            FileTableTab fileTableTab = fIleTableTabsList.get(i);
                 JTreeTable treeTable = fileTableTab.getFileTreeTablePanel().getTreeTable();
                TreeTableCellRenderer tree = treeTable.getTree();
                if (tree.getModel() instanceof FileSystemModel fsm) {
                    Opt<RESWallper> searchNodeByFile = fsm
                            .searchNodeByFile(resWallper,true);
                    if (searchNodeByFile.notNull_()) {
                        SwingUtilities.invokeLater(() -> {
                            // 尝试更新所有表格
                           treeTable.fireTreeNodesChanged(newList(resWallper));
                        });
                    }
                }
  
        }
        
    }
    @Override
    public void windowOpened(WindowEvent e) {
        TimePool.getStaticTimePool().setTimeOut(300, () -> {
            for (int i = 0; i < onFrameShowedFun.size(); i++) {
                try {
                    onFrameShowedFun.get(i).run();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    @Override
    public void windowClosing(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowActivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void windowDeactivated(WindowEvent e) {
        // TODO Auto-generated method stub

    }
    public  void changeMainFrameTitle() {
        try {
            TimePool.getStaticTimePool().setTimeOut(100, ()  ->{
                try {
                    setTitle(mainPanel.getCenterTabsPanel().getNowSeletedTabPanel().get().getNowShowingTable().getRootDir().get().getAbsolutePath());
                } catch (Exception e) {

                }
            });
        } catch (Exception e) {
        }
    }


    /**
     * @return the backgroundTaskManager
     */
    public BackgroundTaskManager getBackgroundTaskManager() {
        return backgroundTaskManager;
    }

    /**
     * @return the xdgOpenConfigManager
     */
    public XdgOpenConfigManager getXdgOpenConfigManager() {
        return xdgOpenConfigManager;
    }
}
