package github.acodervic.filemanager.gui.popmenus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.thread.BackgroundTask;
import github.acodervic.filemanager.thread.BackgroundTaskManager;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.swing.MessageBox;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.miginfocom.swing.MigLayout;

/**
 * 解压缩
 */
public class DeCompression extends MyPopMenuItem {

 

    @Override
    public String getName() {
        return "Extract Here";
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName(Icons.deCompression);
    }

    @Override
    public String getTip() {
        return "Extract Here";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        BackgroundTaskManager backgroundTaskManager = mainPanel.getMainFrame().getBackgroundTaskManager();
        backgroundTaskManager.execTask(new BackgroundTask() {

            @Override
            public int getProgress() {
                 return 0;
            }

            @Override
            public String getName() {
                 return null;
            }

            @Override
            public void action() {
                RESWallper resWallper = seletedResWallpers.get(0);
                RESWallper dir = resWallper.getParentResWallper();
                if (!dir.isWritable()) {
                    MessageBox.showErrorMessageDialog(mainPanel, "错误", "当前目录不可写!");
                    return ;
                }

                String fileExtName = resWallper.getFileExtName().toLowerCase();
                try {
                    switch (fileExtName) {
                        case "zip":
                            unzip(resWallper, dir);
                            break;
                        case "jar":
                            unzip(resWallper, dir);
                            break;
                        case "tar":
                            //unzip(resWallper, dir);
                            break;
                        case "gz":
                            //unzip(resWallper, dir);
                            break;
                        case "xz":
                            //unzip(resWallper, dir);
                            break;

                        default:
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    MessageBox.showErrorMessageDialog(mainPanel, "错误", "解压失败"+e.getMessage()+"!");
                }

            }

            @Override
            public void onStop() {
                 
            }

            @Override
            public void onPause() {
                 
            }

            @Override
            public JPanel getTaskDIsplayPanel() {
                return null;
            }

            @Override
            public void onResum() {
                // TODO Auto-generated method stub
                
            }
            
        });
    }

    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        //只单处理一个
        if (seletedResWallpers.size() == 0) {
            return false;
        }
        if (seletedResWallpers.size() == 1&& seletedResWallpers.get(0).isReadable()&&str(seletedResWallpers.get(0).getFileExtName()).eqAnyIgnoreCase("zip","tar","jar","7z","gz")){
            return true;
        }
        return false;
    }


    public void unzip(RESWallper resWallper,RESWallper dir) throws ZipException {
        ZipFile zipFile = new ZipFile(resWallper.getAbsolutePathWithoutProtocol());
        List<FileHeader> fileHeaders = zipFile.getFileHeaders();
        Boolean oneDir = true;
        if (fileHeaders.size() > 0) {
            FileHeader file = fileHeaders.get(0);
            String dirName = file.getFileName().split("/")[0];
            for (int i = 1; i < fileHeaders.size(); i++) {
                if (!fileHeaders.get(i).getFileName().startsWith(dirName)) {
                    oneDir = false;// 代表zip是多目录的
                    break;
                }
            }
        }
        FileObject targetDir = null;
        if (!oneDir) {
             try {
                targetDir = dir.getFileObj().resolveFile(resWallper.getBaseName());
            } catch (FileSystemException e) {
                e.printStackTrace();
            }
            // 如果zip中不止一个根目录则创建一个 统一的文件夹
            try {
                if (!targetDir.exists()) {
                    MessageBox.showErrorMessageDialog(null, "错误",
                            "无法创建目录" + targetDir.toString() + "!");
                    return;
                }
            } catch (FileSystemException e) {
                e.printStackTrace();
            }
        } else {
            // 否则直接解压到当前目录
            targetDir = resWallper.getParentResWallper().getFileObj();
        }
        zipFile.extractAll(targetDir.getPath().toString());
    }
 

    public void gz(RESWallper resWallper,RESWallper dir) throws ZipException {
//        new ProcessBuilder("/usr/bin/gnome-terminal",   "--window" ,"-x", "bash" ,"-c","echo  '解压"+resWallper.getAbsolutePath() +"'   &&  sudogui dpkg -i "+absolutePath+"  &&  sleep 10 ").start();
    }
}
