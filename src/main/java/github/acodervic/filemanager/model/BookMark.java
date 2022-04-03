package github.acodervic.filemanager.model;

import org.apache.commons.vfs2.FileSystemException;

import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.db.anima.Model;
import github.acodervic.mod.db.anima.annotation.Ignore;

public class BookMark extends Model implements GuiUtil {
    @Ignore
    transient RESWallper dirRes;
    Integer id;
    String path;
    String name;


    public BookMark() {
 
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }
    /**
     * @param id the id to set
     */
    public void setId(Integer id) {
        this.id = id;
    }
    /**
     * @param dirRes
     * @param name
     */
    public BookMark(RESWallper dirRes, String name) {
        this.dirRes = dirRes;
        this.name = name;
        this.path=this.dirRes.getAbsolutePath();
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }
    /**
     * @param path the path to set
     */
    public void setPath(String path) {
        this.path = path;
    }
    /**
     * @return the name
     */
    public String getName() {
        if (isNull(name)) {
            name=dirRes.getName();
        }
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

 

    /**
     * @return the dirRes
     * @throws FileSystemException
     */
    public synchronized  RESWallper getDirRes() throws FileSystemException {
        if (isNull(dirRes)) {
            if (path.startsWith("file://")) {
                path=path.replace("file://", "");
                dirRes=new RESWallper(newLocalFIle(path).get());
            }
        }
        return dirRes;
    }

    /**
     * @param dirRes the dirRes to set
     */
    public void setDirRes(RESWallper dirRes) {
        this.dirRes = dirRes;
        this.dirRes.setIsBookMark(true);
    }


    @Override
    public String toString() {
        return getName();
    }


    
}
