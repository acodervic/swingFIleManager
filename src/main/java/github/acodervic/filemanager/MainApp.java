package github.acodervic.filemanager;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;

import javax.swing.UIManager;

import org.oxbow.swingbits.dialog.task.TaskDialogs;
import org.sqlite.SQLiteDataSource;

import github.acodervic.filemanager.device.LinuxDeviceMountter;
import github.acodervic.filemanager.exception.FileManagerException;
import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainFrame;
import github.acodervic.filemanager.theme.MyDark;
import github.acodervic.filemanager.thread.ApiCallHttpServer;
import github.acodervic.filemanager.thread.BackgroundTaskManager;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.utilFun;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.db.anima.Anima;
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
                JdbiJDBC dataBase=new JdbiJDBC();
                SQLiteDataSource ds = new SQLiteDataSource();
                String url = GuiUtil.getUserDIrStatic()+"/db";
                System.out.println("open database file "+url);
                ds.setUrl("jdbc:sqlite:"+url);
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
            
            if (args.length>1&&args[0].toLowerCase()=="api") {
                System.out.println("启用api模式,请求http://127.0.0.1:64213/newWindow 将自动创建window");
                //MainFrame mainFrame = new MainFrame(new DirRes("/"), backgroundTaskManager);
                ApiCallHttpServer  apiCallHttpServer=new ApiCallHttpServer(64213,backgroundTaskManager,new LinuxDeviceMountter());
                apiCallHttpServer.start();
            }else{
                boolean linux = utilFun.isLinux();
                if (linux) {
                    MainFrame mainFrame = new MainFrame(GuiUtil.newLocalFIleStatic(linux?"/":"C://").get(), backgroundTaskManager,new LinuxDeviceMountter());
                    mainFrame.setSize(1200, 800);
                    mainFrame.setVisible(true);
                    mainFrame.addWindowListener(new WindowListener() {

                        @Override
                        public void windowOpened(WindowEvent e) {
                             System.out.println("asdasd");
                        }

                        @Override
                        public void windowClosing(WindowEvent e) {
                             
                        }

                        @Override
                        public void windowClosed(WindowEvent e) {
                                System.exit(0);
                        }

                        @Override
                        public void windowIconified(WindowEvent e) {
                             
                        }

                        @Override
                        public void windowDeiconified(WindowEvent e) {
                             
                        }

                        @Override
                        public void windowActivated(WindowEvent e) {
                             
                        }

                        @Override
                        public void windowDeactivated(WindowEvent e) {
                            System.exit(0);
                            
                        }
                        
                    });
                }else{
                     System.out.println("我们暂未对windows做好准备");
                }
            }

            

 
    }
}

