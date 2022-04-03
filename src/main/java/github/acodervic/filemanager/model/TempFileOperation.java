package github.acodervic.filemanager.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import github.acodervic.filemanager.thread.FileOperation;
import github.acodervic.mod.data.Opt;

/**
 * 用来描述临时操作文件的行为.比如复制和剪切
 */
public class TempFileOperation {
    List<RESWallper> fileOperationTargetRes=new ArrayList<>();//操作的资源对象
    RESWallper targetFileOrDirsSouceDir;//被复制/移动文件/夹的起始目录 
    FileOperation  fileOperation;//针对fileOperationTargetRes的操作 一般是move还是copy
    Boolean recovered=false;//是否已经了撤回操作
    Opt<Runnable> recoverFun=new Opt<>();;//撤回函数


    /**
     * 撤回操作
     */
    public void recover() {
        if (recovered) {
            return ;
        }
        if (recoverFun.notNull_()) {
            recoverFun.get().run();;
        }
    }

    /**
     * @return the recoverFun
     */
    public Opt<Runnable> getRecoverFun() {
        return recoverFun;
    }
    /**
     * @return the fileOperation
     */
    public FileOperation getFileOperation() {
        return fileOperation;
    }

    /**
     * @param fileOperation the fileOperation to set
     */
    public void setFileOperation(FileOperation fileOperation) {
        this.fileOperation = fileOperation;
    }

    /**
     * @return the fileOperationTargetRes
     */
    public List<RESWallper> getFileOperationTargetRes() {
        return fileOperationTargetRes;
    }


    /**
     * @param targetFileOrDirsSouceDir the targetFileOrDirsSouceDir to set
     */
    public void setTargetFileOrDirsSouceDir(RESWallper targetFileOrDirsSouceDir) {
        this.targetFileOrDirsSouceDir = targetFileOrDirsSouceDir;
    }
    /**
     * @return the targetFileOrDirsSouceDir
     */
    public RESWallper getTargetFileOrDirsSouceDir() {
        return targetFileOrDirsSouceDir;
    }


    public TempFileOperation clone() {
          TempFileOperation tempFileOperation = new TempFileOperation();
          tempFileOperation.setFileOperation(fileOperation);
          tempFileOperation.setTargetFileOrDirsSouceDir(targetFileOrDirsSouceDir);
          return  tempFileOperation;
    }
    
}
