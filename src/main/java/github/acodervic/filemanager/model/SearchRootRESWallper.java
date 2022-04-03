package github.acodervic.filemanager.model;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.thread.DeepSreachThread;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.TimeUtil;

public class SearchRootRESWallper  extends RESWallper   implements GuiUtil {
 
    String searchText="";
    List<RESWallper>  foundDirAndFiles=new ArrayList<>();
    Integer tryMathcount=0;//一共翻找的资源数量,未实现
    RESWallper dir;
    Long startSearchTime;
    Long endSearchTime;
    DeepSreachThread searchThread;
    
 
    /**
     * @param searchThread the searchThread to set
     */
    public void setSearchThread(DeepSreachThread searchThread) {
        this.searchThread = searchThread;
    }
    
    /**
     * @param startSearchTime the startSearchTime to set
     */
    public void setStartSearchTime(Long startSearchTime) {
        this.startSearchTime = startSearchTime;
    }

    /**
     * @param endSearchTime the endSearchTime to set
     */
    public void setEndSearchTime(Long endSearchTime) {
        this.endSearchTime = endSearchTime;
    }


    public SearchRootRESWallper(RESWallper dirRes,String search) {
        this.searchText=search;
        this.dir=dirRes;
    }

    /**
     * @return the dir
     */
    public RESWallper getDir() {
        return dir;
    }

    public synchronized void addTryMathcount() {
        tryMathcount+=1;
    }
    @Override
    public String getName() {
        String timeString="";
        if (notNull(endSearchTime)) {
            timeString = TimeUtil.getBetweenPrintTime(endSearchTime - startSearchTime);
        } else {
            timeString = TimeUtil.getBetweenPrintTime(System.currentTimeMillis() - startSearchTime);

        }

        String name = "" + dir.getBaseName() + "   Search: " + searchText + "  Result size:"
                + foundDirAndFiles.size()
                + "  Time:" + timeString + "  Satus:" + (isNull(endSearchTime) ? "Searching" : "Finished");
        return name;
    }

    public boolean stopSearch() {
        if (searchThread.isAlive()) {
            searchThread.stopSearch();
            setEndSearchTime(System.currentTimeMillis());
            logInfo("成功停止查询线程");
            return  true;
        }
        return false;
    }

    public boolean inSearching() {
        return searchThread!=null&&searchThread.isAlive();
    }

    @Override
    public synchronized List getChildResList() {
         return foundDirAndFiles;
    }

    @Override
    public List getChildDirList() {
        return null;//搜索的结果不分目录和文件
    }

    @Override
    public List getChildFileList() {
        return null;//搜索的结果不分目录和文件
    }

 

    @Override
    public long getByteSize() {
        return 0;
    }

    @Override
    public boolean isExecutable() {
        return false;
    }

    @Override
    public boolean isReadable() {
        return false;
    }

    @Override
    public boolean isDir() {
        return true;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isHidden() {
        return false;
    }

    @Override
    public boolean isSymbolicLink() {
        return false;
    }


    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public String getAbsolutePath() {
        return "/tmp";
    }
 
    @Override
    public String getFileExtName() {
        return "";
    }

    @Override
    public String getMatchString() {
        return null;
    }

 

    @Override
    public Date getLastModified() {
        return new Date();
    }
 
    @Override
    public Boolean addChild(RESWallper resWallper) {
        foundDirAndFiles.add(resWallper);
        return  true;
    }

 
    


    
}
