package github.acodervic.filemanager.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.apache.commons.vfs2.FileObject;

import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;

public class Device {
    String name;
    FileObject sourceFIleRes;
    FileObject distDirRes;
    List<Runnable> onMountFuns=new ArrayList<>();
    List<Runnable> onUmountFuns=new ArrayList<>();
    

    /**
     * @return the sourceFIleRes
     */
    public FileObject getSourceFIleRes() {
        return sourceFIleRes;
    }

    /**
     * @param sourceFIleRes the sourceFIleRes to set
     */
    public void setSourceFIleRes(FileObject sourceFIleRes) {
        this.sourceFIleRes = sourceFIleRes;
    }

    public FileObject getDistDirRes() {
        return distDirRes;
    }

    /**
     * @param distDirRes the distDirRes to set
     */
    public void setDistDirRes(FileObject distDirRes) {
        this.distDirRes = distDirRes;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     * @param sourceDirRes
     * @param distDirRes
     */
    public Device(String name, FileObject sourceFIleRes, FileObject distDirRes) {
        this.name = name;
        this.sourceFIleRes = sourceFIleRes;
        this.distDirRes = distDirRes;
    }

    @Override
    public String toString() {
        return getName();
    }

    public boolean isMounted() {
        try {
            return getDistDirRes().exists();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

   

    /**
     * @return the onMountFuns
     */
    public List<Runnable> getOnMountFuns() {
        return onMountFuns;
    }
    /**
     * @return the onUmountFuns
     */
    public List<Runnable> getOnUmountFuns() {
        return onUmountFuns;
    }
}
