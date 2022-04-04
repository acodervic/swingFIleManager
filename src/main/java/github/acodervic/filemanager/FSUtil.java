package github.acodervic.filemanager;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.oxbow.swingbits.dialog.task.TaskDialogs;
import org.sqlite.SQLiteDataSource;

import github.acodervic.filemanager.exception.FileManagerException;
import github.acodervic.filemanager.model.BookMark;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.db.anima.Anima;
import github.acodervic.mod.db.anima.core.JDBC;
import github.acodervic.mod.db.anima.core.imlps.JdbiJDBC;
import github.acodervic.mod.io.IoType;

public class FSUtil implements GuiUtil {
 

    static Anima db=null; 
 
    
    
    public static void AddBookMark(RESWallper dir) {
        if (dir.isDir()&&dir.exists()) {
             BookMark bookMark = new BookMark(dir, dir.getName());
             db.save(bookMark);
        }
    }

    public static void delBookMark(BookMark bk) {
        if (bk.getId()!=null) {
            db.deleteById(bk.getClass(), bk.getId());
        }
    }
 

    /**
     * 读取所有标签
     * @return
     */
    public static List<BookMark> readAllBookMarks() {
        List<BookMark> all = db.selectFrom(BookMark.class).all();
        return all;
    }


        /**
     * 获得某个文件夹下面的文件夹或者文件
     * 
     * @param outDirAndFiles   输出参数,使用完毕之后可以直接遍历传入的outPutFiles
     * @param dir              起始目录
     * @param setDeep          设置发现深度
     * @param deep             起始深度值,必须为-1!
     * @param pattern          匹配文件或文件夹的的正则表达式,*代表所有 例如,匹配后缀
     *                         ([^\\s]+(\\.(?i)(jpg|png))$),
     * @param onlyMathFilename 正则表达式是否只匹配文件/夹名称(包含后缀)默认匹配文件的完整限定名 ,当匹配正则为 *
     *                         的时候,这个参数true和false都不会影响到程序的逻辑
     * @param ioType           设置想要得到的io类型.可选的值FILE文件,DIR文件夹,,FILE_AND_DIR文件夹和文件
         * @throws FileSystemException
     */
    public static void getAllDirAndFileByDir(List<FileObject> outDirAndFiles, FileObject dir, int setDeep, int deep, String pattern,
            boolean onlyMathFilename, IoType ioType)  {
        if (dir != null) {
            try {
                            // 是否
            if (dir.isFolder() && deep < setDeep) {
                deep++;// 往深一层
                FileObject f[] = dir.getChildren();
                if (f != null) {
                    int length = f.length;
                    for (int i = 0; i < length; i++) {
                        try {
                                                    // 添加文件
                        // *号默认匹配所有
                        FileObject fileObject = f[i];
                        if (pattern.equals("*")) {
                            if (ioType == ioType.FILE & !fileObject.isFolder()) {
                                outDirAndFiles.add(fileObject);
                            }
                            if (ioType == ioType.DIR & fileObject.isFolder()) {
                                outDirAndFiles.add(fileObject);
                            }
                            if (ioType == ioType.FILE_AND_DIR & fileObject != null) {
                                outDirAndFiles.add(fileObject);
                            }

                        } else {
                            String baseName = fileObject.getName().getBaseName();
                            // 判断是否匹配正则表达式
                            if (onlyMathFilename) {
                                if (Pattern.matches(pattern, baseName)) {
                                    if (ioType == ioType.FILE & !fileObject.isFolder()) {
                                        outDirAndFiles.add(fileObject);
                                    }
                                    if (ioType == ioType.DIR & fileObject.isFolder()) {
                                        outDirAndFiles.add(fileObject);
                                    }
                                    if (ioType == ioType.FILE_AND_DIR & fileObject != null) {
                                        outDirAndFiles.add(fileObject);
                                    }
                                }
                            } else {
                                if (Pattern.matches(pattern, baseName)) {
                                    if (ioType == ioType.FILE & !fileObject.isFolder()) {
                                        outDirAndFiles.add(fileObject);
                                    }
                                    if (ioType == ioType.DIR & fileObject.isFolder()) {
                                        outDirAndFiles.add(fileObject);
                                    }
                                    if (ioType == ioType.FILE_AND_DIR & fileObject != null) {
                                        outDirAndFiles.add(fileObject);
                                    }
                                }
                            }
                        }
                        getAllDirAndFileByDir(outDirAndFiles, fileObject, setDeep, deep, pattern, onlyMathFilename, ioType);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    deep--;// 往上一层
                }
            }
            } catch (Exception e) {
            }

        }
    }
    
}
