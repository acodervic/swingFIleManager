package github.acodervic.filemanager.gui.popmenus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.vfs2.FileSystemException;

import github.acodervic.filemanager.FSUtil;
import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.thread.BackgroundTask;
import github.acodervic.filemanager.thread.BackgroundTaskManager;
import github.acodervic.filemanager.treetable.JTreeTable;
import github.acodervic.mod.data.Value;
import github.acodervic.mod.swing.MessageBox;
import github.acodervic.mod.swing.MyComponent;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.miginfocom.swing.MigLayout;

public class Compression extends MyPopMenuItem {
 
    @Override
    public String getName() {
        return "Compression";
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName(Icons.compression);
    }

    @Override
    public String getTip() {
        return "Compression FIles/Dirs";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        BackgroundTaskManager backgroundTaskManager = mainPanel.getMainFrame()
        .getBackgroundTaskManager();
JFrame optionFrame = new JFrame();
optionFrame.setTitle("_dialog");
JPanel jp = new JPanel(new MigLayout());
JTextField name = new JTextField();
JTextField password = new JTextField();
JButton ok = new JButton("Create");
JButton canel = new JButton("canel");
JComboBox targetDirCombox = new JComboBox<>();
JComboBox fromatCombox = new JComboBox<>();
fromatCombox.addItem("zip");
fromatCombox.addItem("7z");
fromatCombox.addItem("tar");
fromatCombox.addItem("rar");
fromatCombox.setSelectedIndex(0);
targetDirCombox.setEditable(true);
targetDirCombox.addItem(nowDir);
FSUtil.readAllBookMarks().forEach(bookmark  ->{
    try {
        targetDirCombox.addItem(bookmark.getDirRes());
    } catch (FileSystemException e1) {
        e1.printStackTrace();
    }
});
setLinetBoder( name);
setLinetBoder( password);
setLinetBoder( ok);
setLinetBoder( canel);
setLinetBoder( fromatCombox);

jp.add(name, "width 80%");
jp.add(fromatCombox, "width 20%,wrap");
jp.add(new JLabel("Password "), "width  70%,wrap");
jp.add(password, "span 2,width 100%,wrap");
jp.add(new JLabel(), "split 3,width 50%");
jp.add(ok, "width 35%");
jp.add(canel, "width 35%");
optionFrame.setSize(450, 250);
optionFrame.add(jp);
optionFrame.setVisible(true);
MyComponent.moveFrameCenter(optionFrame);
onClick(canel, e  ->{
    optionFrame.dispose();
});
onClick(ok, e -> {

    switch (fromatCombox.getSelectedItem().toString()) {
        case "zip":
            // 在新的线程中压缩目标
            new Thread(() -> {
                optionFrame.dispose();
                Value ZipFileVal = value();

                ZipParameters zipParameters = new ZipParameters();
                zipParameters.setCompressionLevel(CompressionLevel.HIGHER);
                String passText = password.getText();
                char[] password2 = str(passText).isEmpty()?null:passText.toCharArray();
                try  {
                    ZipFile zipFile = null;
                    if (password2==null) {
                        zipFile=    new ZipFile(
                            nowDir.getFileObj().resolveFile(name.getText() + ".zip").getPath().toFile());
                    }else{
                        zipParameters.setEncryptFiles(true);
                        zipParameters.setEncryptionMethod(EncryptionMethod.AES);
                        zipFile=new ZipFile(
                            nowDir.getFileObj().resolveFile(name.getText() + ".zip").getPath().toFile(),
                            password2);
                    }
                    ZipFileVal.setValue(zipFile);

                    ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
                    Thread zipWorkThread = Thread.currentThread();

                    // 创建后台任务
                    backgroundTaskManager.execTask(new BackgroundTask() {
                        JPanel zipProcessPanel;
                        JLabel nowProcessInfoLable = new JLabel();

                        /**
                         * @return the zipProcessPanel
                         */
                        public synchronized JPanel getZipProcessPanel() {
                            if (isNull(zipProcessPanel)) {
                                zipProcessPanel = new JPanel(new MigLayout());
                                zipProcessPanel.add(nowProcessInfoLable);
                            }
                            return zipProcessPanel;
                        }

                        @Override
                        public int getProgress() {
                            if (progressMonitor == null) {
                                return 0;
                            }
                            return progressMonitor.getPercentDone();
                        }

                        @Override
                        public String getName() {
                            return "Add Zip  " + seletedResWallpers.size() + " targets to "
                                    + ZipFileVal.get(ZipFile.class).getFile().getName();
                        }

                        @Override
                        public void action() {
                            try {
                                Thread.sleep(2000);// 等待zipWorkThread启动
                                JPanel taskPanel = getZipProcessPanel();
                                while (!progressMonitor.getState().equals(ProgressMonitor.State.READY)) {
                                    // System.out.println("Percentage done: " +
                                    // progressMonitor.getPercentDone());
                                    // System.out.println("Current file: " + progressMonitor.getFileName());
                                    // System.out.println("Current task: " +
                                    // progressMonitor.getCurrentTask());
                                    File file = new File(progressMonitor.getFileName());
                                    nowProcessInfoLable.setText(
                                            progressMonitor.getCurrentTask().toString() + ":"
                                                    + file.getName());
                                    Thread.sleep(100);
                                }
                                if (progressMonitor.getResult().equals(ProgressMonitor.Result.SUCCESS)) {
                                    System.out.println("Successfully added folder to zip");
                                } else if (progressMonitor.getResult()
                                        .equals(ProgressMonitor.Result.ERROR)) {
                                    System.out.println(
                                            "Error occurred. Error message: "
                                                    + progressMonitor.getException().getMessage());
                                } else if (progressMonitor.getResult()
                                        .equals(ProgressMonitor.Result.CANCELLED)) {
                                    System.out.println("Task cancelled");
                                }
                                ZipFileVal.get(ZipFile.class).close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public JPanel getTaskDIsplayPanel() {
                            return getZipProcessPanel();
                        }

                        @Override
                        public void onStop() {
                            zipWorkThread.stop();
                        }

                        @Override
                        public void onPause() {

                        }

                        @Override
                        public void onResum() {
                            // TODO Auto-generated method stub
                            
                        }

                    });

                    // 真正开始压缩,会阻塞这个线程
                    for (int i = 0; i < seletedResWallpers.size(); i++) {
                        RESWallper resWallper = seletedResWallpers.get(i);
                        try {
                            File file = resWallper.getLocalFIle().getPath().toFile();
                            if (resWallper.isFile()) {
                                zipFile.addFile(file, zipParameters);
                            } else {
                                zipFile.addFolder(file, zipParameters);
                            }
                            logInfo("add " + resWallper.getAbsolutePath() + "to " + zipFile.toString());
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                        sleep(100);
                    }

                    //压缩完成
                    MessageBox.showInfoMessageDialog(fromatCombox, "压缩完成", "压缩完成:   "+zipFile.getFile().getAbsolutePath());
                } catch (IOException e2) {
                    // TODO Auto-generated catch block
                    e2.printStackTrace();
                }
            }, "zipWorkThread").start();
            break;
        case "tar":

            break;
        case "7z":

            break;
        case "xz":

            break;

        default:
            break;
    }
});

    }

    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        if (seletedResWallpers.size() == 0) {
            return false;
        }
        if (nowDir.isReadable()) {
            if (seletedResWallpers.get(0).isLocalFile()) {// 本地文件才能压缩
                return true;
            }
        }

        return false;
    }
 
}
