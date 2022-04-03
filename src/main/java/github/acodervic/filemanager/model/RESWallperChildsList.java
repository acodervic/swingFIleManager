package github.acodervic.filemanager.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.vfs2.FileObject;

import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.Opt;

public class RESWallperChildsList implements GuiUtil {
    List<RESWallper> dirsAndFiles=new ArrayList<>();    
    List<RESWallper> dirs=new ArrayList<>();    
    List<RESWallper> files=new ArrayList<>();    
    Map<String,RESWallper>   dirsAndFilesMap=new HashMap<>();
    

    /**
     * @param dirs
     * @param files
     */
    public RESWallperChildsList(List<RESWallper> dirs, List<RESWallper> files) {
        this.dirs = dirs;
        this.files = files;
        resetDirsAnfFiles();
    }


 

    /**
     * 
     */
    public RESWallperChildsList() {
    }


    /**
     * @return the dirs
     */
    public List<RESWallper> getDirs() {
        return dirs;
    }


    /**
     * @return the files
     */
    public List<RESWallper> getFiles() {
        return files;
    }

    /**
     * 重置DirsAnfFiles
     */
    
    public synchronized void resetDirsAnfFiles() {
        dirsAndFiles.clear();
        dirsAndFiles.addAll(getDirs());
        dirsAndFiles.addAll(getFiles());
        dirsAndFilesMap.clear();
        for (int i = 0; i < dirsAndFiles.size(); i++) {
            RESWallper resWallper = dirsAndFiles.get(i);
            dirsAndFilesMap.put(resWallper.getAbsolutePath(), resWallper);
        }
    }

    public Opt<RESWallper> getRESWallperByAbsolutePath  (String absolutePath) {
        return new Opt<RESWallper>(dirsAndFilesMap.get(absolutePath));
    }
    /**
     * @return the dirsAnfFiles
     */
    public  List<RESWallper> getDirsAndFiles() {
        return dirsAndFiles;
    }

    /**
     * @param dirs the dirs to set
     */
    public void setDirs(List<RESWallper> dirs) {
        this.dirs = dirs;
    }

    /**
     * @param files the files to set
     */
    public void setFiles(List<RESWallper> files) {
        this.files = files;
    }

    public int addRes(RESWallper res) {
        if (res.isDir()) {
            getDirs().add(0, res);//最新添加默认添加在最前面
        } else {
            getFiles().add(0,res);//最新添加默认添加在最前面
        }
        resetDirsAnfFiles();
        return getDirsAndFiles().indexOf(res);
    }
    

    public boolean remove(FileObject file) {
        String key = file.toString();
        if (dirsAndFilesMap.containsKey(key)) {
            RESWallper resWallper = dirsAndFilesMap.get(key);
            dirsAndFiles.remove(resWallper);
            dirs.remove(resWallper);
            files.remove(resWallper);
            dirsAndFilesMap.remove(key);
            return true;
        }
        return false;
    }
}
