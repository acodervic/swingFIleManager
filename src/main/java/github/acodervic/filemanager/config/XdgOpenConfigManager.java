package github.acodervic.filemanager.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.swing.SwingUtilities;

import org.apache.commons.vfs2.FileSystemException;

import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.utilFun;
import github.acodervic.mod.swing.MessageBox;
import github.acodervic.mod.thread.FixedPool;
import github.acodervic.mod.thread.Task;

/**
 * 双击打开默认管理器
 * OpenConfigManager
 */
public class XdgOpenConfigManager implements GuiUtil {
    Map<String,Object>  openExtCommandMap=new HashMap<>();

    XdgOpenConfig codium=new XdgOpenConfig("codium", resWallper  ->{
        String absolutePath = resWallper.getAbsolutePathWithoutProtocol();
        try {
            new ProcessBuilder("/usr/bin/codium",absolutePath).start();
        } catch (Exception e) {
            MessageBox.showErrorMessageDialog(null, "codium打开错误", e.getMessage());
        }
    });


    XdgOpenConfig onlyoffice=new XdgOpenConfig("onlyoffice", resWallper  ->{
        String absolutePath = resWallper.getAbsolutePathWithoutProtocol();
        try {
            new ProcessBuilder("/usr/bin/onlyoffice-desktopeditors",absolutePath).start();
        } catch (Exception e) {
            MessageBox.showErrorMessageDialog(null, "onlyoffice打开错误", e.getMessage());
        }
    });

    XdgOpenConfig kate=new XdgOpenConfig("kate", resWallper  ->{
        String absolutePath = resWallper.getAbsolutePathWithoutProtocol();
        try {
            new ProcessBuilder("/usr/bin/kate",absolutePath).start();
        } catch (Exception e) {
            MessageBox.showErrorMessageDialog(null, "kate打开错误", e.getMessage());
        }
    });
    XdgOpenConfig chrome=new XdgOpenConfig("chrome", resWallper  ->{
        String absolutePath = resWallper.getAbsolutePathWithoutProtocol();
        try {
            new ProcessBuilder("/usr/bin/google-chrome",absolutePath).start();
        } catch (Exception e) {
            MessageBox.showErrorMessageDialog(null, "chrome打开错误", e.getMessage());
        }
    });

    XdgOpenConfig installdeb=new XdgOpenConfig("installdeb", resWallper  ->{
        String absolutePath = resWallper.getAbsolutePathWithoutProtocol();
        try {
            new ProcessBuilder("/usr/bin/gnome-terminal",   "--window" ,"-x", "bash" ,"-c","echo  '安装"+absolutePath +"'   &&  sudogui dpkg -i "+absolutePath+"  &&  sleep 10 ").start();
        } catch (Exception e) {
            MessageBox.showErrorMessageDialog(null, "chrome打开错误", e.getMessage());
        }
    });



    XdgOpenConfig file_roller=new XdgOpenConfig("file_roller", resWallper  ->{
        String absolutePath = resWallper.getAbsolutePathWithoutProtocol();
             try {
            new ProcessBuilder("/usr/bin/file-roller",   absolutePath).start();
        } catch (Exception e) {
            MessageBox.showErrorMessageDialog(null, "chrome打开错误", e.getMessage());
        } 
    });

    XdgOpenConfig openArchive=new XdgOpenConfig("openArchive", resWallper  ->{
        String absolutePath = resWallper.getAbsolutePathWithoutProtocol();
        try {
            resWallper.getTable().get().getFileTableTab().getTabsPanel().addFilesTableTab(new RESWallper(newVFS2FIle(resWallper.getFileExtName()+"://"+absolutePath).get()), true);
        } catch (FileSystemException e) {
            e.printStackTrace();
        }
    });


    XdgOpenConfig video_totem=new XdgOpenConfig("video_totem", resWallper  ->{
        String absolutePath = resWallper.getAbsolutePathWithoutProtocol();
        try {
            new ProcessBuilder("/usr/bin/totem",   absolutePath).start();
        } catch (Exception e) {
            MessageBox.showErrorMessageDialog(null, "totem打开错误", e.getMessage());
        }
    });
    


    /**
     * @return the video_totem
     */
    public XdgOpenConfig getVideo_totem() {
        return video_totem;
    }

    /**
     * @return the file_roller
     */
    public XdgOpenConfig getFile_roller() {
        return file_roller;
    }



    /**
     * @return the installdeb
     */
    public XdgOpenConfig getInstalldeb() {
        return installdeb;
    }
    /**
     * @return the kate
     */
    public XdgOpenConfig getKate() {
        return kate;
    }

    /**
     * @return the chrome
     */
    public XdgOpenConfig getChrome() {
        return chrome;
    }
    /**
     * @return the codium
     */
    public XdgOpenConfig getCodium() {
        return codium;
    }

    /**
     * @return the onlyoffice
     */
    public XdgOpenConfig getOnlyoffice() {
        return onlyoffice;
    }


    public XdgOpenConfigManager() {
        openExtCommandMap.put("zip", openArchive);
        openExtCommandMap.put("jar", openArchive);
        openExtCommandMap.put("jmod", file_roller);
        openExtCommandMap.put("7z", openArchive);
        openExtCommandMap.put("gz", openArchive);
        openExtCommandMap.put("rar", openArchive);
        openExtCommandMap.put("xz", openArchive);
        openExtCommandMap.put("deb", openArchive);
        openExtCommandMap.put("apk", openArchive);
        openExtCommandMap.put("rpm", openArchive);

        openExtCommandMap.put("txt", codium);
        openExtCommandMap.put("html", codium);
        openExtCommandMap.put("json", codium);
        openExtCommandMap.put("java", codium);
        openExtCommandMap.put("js", codium);
        openExtCommandMap.put("css", codium);
        openExtCommandMap.put("md", codium);
        openExtCommandMap.put("php", codium);
        openExtCommandMap.put("c", codium);
        openExtCommandMap.put("sql", codium);
        openExtCommandMap.put("ini", codium);
        openExtCommandMap.put("xml", codium);
        openExtCommandMap.put("profile", codium);
        openExtCommandMap.put("log", codium);
        openExtCommandMap.put("conf", codium);
        openExtCommandMap.put("sh", codium);
        openExtCommandMap.put("py", codium);
        openExtCommandMap.put("text", codium);
        openExtCommandMap.put("rb", codium);
        openExtCommandMap.put("md", codium);
        openExtCommandMap.put("vsix", codium);




        openExtCommandMap.put("docx", onlyoffice);
        openExtCommandMap.put("doc", onlyoffice);
        openExtCommandMap.put("xls", onlyoffice);
        openExtCommandMap.put("xlsx", onlyoffice);



        //作为进程启动
        openExtCommandMap.put("appimage", null);
        openExtCommandMap.put("bin", null);

        
        
    }

    public void open(RESWallper resWallper) {
        String fileExtName = resWallper.getFileExtName().toLowerCase();
        if (openExtCommandMap.containsKey(fileExtName)) {
            Object object = openExtCommandMap.get(fileExtName);
            FixedPool.getStaticFixedPool().exec(new Task<>(()  ->{
                if(object==null){
                    new ProcessBuilder(resWallper.getAbsolutePath()).start();//作为进程启动
                }else{
                    if (object instanceof String) {
                        String cmd=object.toString();
                            exec2String(cmd.replaceAll("\\{FILE\\}", resWallper.getAbsolutePath()));
                    }else if(object instanceof XdgOpenConfig open ){
                        try {
                            if(open.getAction() .notNull_()){
                                open.getAction().get().accept(resWallper);
                            }
                        } catch (Exception e) {
                            
                        }
                         
                    }
                }


                return null;
            }));
        }else{
             resWallper.xdgOpen();
        }
        
    }
    

}