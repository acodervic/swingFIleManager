package github.acodervic.filemanager.thread;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

 
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.treetable.JTreeTable;
import github.acodervic.filemanager.treetable.TreeNameColumnPanel;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.filemanager.util.VideoUtil;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.Opt;
import net.coobird.thumbnailator.Thumbnails;
public class ThumbnailatorThreadManager implements GuiUtil {
    Thread thumbnailatorThread;// 略所图/视频抽针生成线程
    LinkedBlockingQueue<RESWallper> thumbnailatorTasks = new LinkedBlockingQueue<>();
    HashMap<String, File> imageIconCacheDirMap = new HashMap<>();// key为图片的md5,value是file文件
    DirRes imageIconCacheDir = new DirRes("imageIconCacheDir");
    int maxImageByteCount = 1024 * 1024 * 3;// 最大为3m的图片生成略图

      public  ThumbnailatorThreadManager() {
          startThumbnailatorThread();
      }


    /**
     * @return the thumbnailatorTasks
     */
    public LinkedBlockingQueue getThumbnailatorTasks() {
        return thumbnailatorTasks;
    }


    public void startThumbnailatorThread() {
        if (!imageIconCacheDir.exists()) {
            imageIconCacheDir.makeDir();
        }
        thumbnailatorThread=new Thread(()  ->{
            while (true) {
                try {
                    RESWallper targetFile=thumbnailatorTasks.take();
                    String sumMD5 = targetFile.getPathMd5();
                    FileRes newFile = imageIconCacheDir.newFile( sumMD5);
                    String newImage = newFile.getFile()+(targetFile.getLastModified().getTime()+"")+".png";
                    if (targetFile.isImage()) {
                        //开始计算略所图
                        String name = targetFile.getName();

                            Thumbnails.of(targetFile.getFileObj().getPath().toFile()).size(16,16).toFile(newImage);
                        logInfo("成功构建略缩图:" + targetFile.getAbsolutePath());
                    } else if (targetFile.isVideo()) {

                        VideoUtil.getVideoImgByFrameIndex(targetFile.getFileObj().getPath().toFile(),
                                new File(newImage),
                                c -> {
                                    return c - 1;
                                });
                        logInfo("成功构建视频略缩图:" + targetFile.getAbsolutePath());

                    }

                    Opt<ImageIcon> imageIconByResWallper = getImageIconByResWallper(targetFile);
                    if (imageIconByResWallper.notNull_()) {
                        ImageIcon icon = imageIconByResWallper.get();
                        targetFile.setIcon(icon);
                        TreeNameColumnPanel treeNameColumnPanel = targetFile.getTreeNameColumnPanel();
                        treeNameColumnPanel.setBaseIcon(icon);
                        treeNameColumnPanel.reloadPanel();
                        JTreeTable jTreeTable = treeNameColumnPanel.getjTreeTable();
                    if (notNull(jTreeTable)) {
                        SwingUtilities.invokeLater(() -> {
                            jTreeTable.refreshUi();
                        });
                    }
                }
                
                } catch (Exception e) {
                }
            }
        });
        thumbnailatorThread.start();
    }

    public Opt<ImageIcon> getImageIconByResWallper(RESWallper resWallper) {
        Opt<ImageIcon> iconRet = new Opt<>();
        if (!resWallper.isLocalFile()) {
            return iconRet;
        }
        if ((resWallper.isImage()&&resWallper.isVideo())) {
            return iconRet;
        }
        if (resWallper.isImage()&& resWallper.getByteSize() > maxImageByteCount) {
            return iconRet;// 图片太大
        }
        String sumMD5 = resWallper.getPathMd5()+resWallper.getLastModified().getTime();
        FileRes icon = imageIconCacheDir.newFile(sumMD5 + ".png" );
        if (icon.exists()) {
            iconRet.of(Icons.getIconByFile(icon.getFile()));
            return iconRet;
        } else {
            // 还没有生成略缩图
            // 添加生成任务
            try {
                thumbnailatorTasks.put(resWallper);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return iconRet;
        
    } 
 
}
