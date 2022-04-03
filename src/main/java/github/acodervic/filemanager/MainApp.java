package github.acodervic.filemanager;

import java.io.IOException;

import javax.swing.UIManager;

import org.oxbow.swingbits.dialog.task.TaskDialogs;
import org.sqlite.SQLiteDataSource;

import github.acodervic.filemanager.device.LinuxDeviceMountter;
import github.acodervic.filemanager.exception.FileManagerException;
import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.theme.MyDark;
import github.acodervic.filemanager.thread.ApiCallHttpServer;
import github.acodervic.filemanager.thread.BackgroundTaskManager;
import github.acodervic.mod.db.anima.Anima;
import github.acodervic.mod.db.anima.core.JDBC;
import github.acodervic.mod.db.anima.core.imlps.JdbiJDBC;

public class MainApp  {
    static {
        System.setProperty("org.apache.commons.logging.Log",
                           "org.apache.commons.logging.impl.NoOpLog");
     }
    public static void main(String[] args) throws IOException {
        System.setProperty("sun.java2d.opengl", "true");//必须要开opengl加速,不然终端会非常卡

			//设置主题			
            MyDark.setup();
            UIManager.put("Tree.collapsedIcon", Icons.collapsed);
            UIManager.put("Tree.expandedIcon", Icons.expanded);
            UIManager.put("Tree.closedIcon", Icons.folder);
            UIManager.put("Tree.openIcon", Icons.folderOpen);
            UIManager.put("Tree.leafIcon", Icons.whiteFile);
            UIManager.put("Tree.drawHorizontalLines", true);
            UIManager.put("Tree.drawVerticalLines", true);
            UIManager.put("Tree.paintLines", Boolean.TRUE);


            try {
                JDBC dataBase=new JdbiJDBC();
                SQLiteDataSource ds = new SQLiteDataSource();
                ds.setUrl("jdbc:sqlite:/home/w/Documents/localgitserver/filemanager/db");
                Boolean open = dataBase.open(ds);
                if (!open) {
                    TaskDialogs.showException(new FileManagerException("Cant load dataBase file "));
                }else{
                    FSUtil.db=new Anima();
                    FSUtil.db.setJdbc(dataBase);
                    
                }
            } catch (Exception e) {
                e.printStackTrace();
                TaskDialogs.showException(new FileManagerException("Cant open database file "));
            }

            
            
            BackgroundTaskManager backgroundTaskManager = new BackgroundTaskManager();
            //MainFrame mainFrame = new MainFrame(new DirRes("/"), backgroundTaskManager);
            ApiCallHttpServer  apiCallHttpServer=new ApiCallHttpServer(64213,backgroundTaskManager,new LinuxDeviceMountter());
            apiCallHttpServer.start();
            //sftp://root@122.114.250.153/
            //sftp://w:abc147268.@192.168.1.118/
    }
}

