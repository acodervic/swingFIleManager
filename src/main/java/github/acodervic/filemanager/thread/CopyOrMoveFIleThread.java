package github.acodervic.filemanager.thread;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSelectInfo;
import org.apache.commons.vfs2.FileSelector;
import org.apache.commons.vfs2.provider.local.LocalFile;

import github.acodervic.filemanager.FSUtil;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.ByteUtil;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.TimeUtil;
import github.acodervic.mod.data.Value;
import github.acodervic.mod.io.IoType;
import net.miginfocom.swing.MigLayout;

public class CopyOrMoveFIleThread extends BackgroundTask  implements GuiUtil {
    FileOperation operation;// 文件操作 是否复制和还是移动

    List<FileObject> targeFileOrDirs ;
    List<FileObject> finishedTargeFileOrDirs = new ArrayList<>();
    FileObject targeFileOrDirsSouceDir;//被复制文件/夹的起始目录 
   Boolean replace=true;//复制和移动的时候是替换还是重命民



    FileObject targetDirFile;
    Consumer<FileOperationResult> resultProcessFun;
    List<RESWallper> copyOrMoveTargetRes;

    public CopyOrMoveFIleThread(FileOperation operation,FileObject targeFileOrDirsSouceDir, FileObject targetDir,Boolean replace,List<RESWallper> needCopyOrMoveTargetRes) {
        this.operation = operation;
        this.targeFileOrDirsSouceDir=targeFileOrDirsSouceDir;
        this.targetDirFile = targetDir;
        this.replace=replace;
        this.copyOrMoveTargetRes=needCopyOrMoveTargetRes;
    }

  
    @Override
    public void action() {
        long allByteSize=0;
        //计算需要被复制的资源
        if (isNull(targeFileOrDirs)) {
            targeFileOrDirs=new ArrayList<>();
            status=STATUS_CALC;
            if (operation == FileOperation.COPY) {
                for (int i = 0; i < copyOrMoveTargetRes.size(); i++) {
                    RESWallper resWallper = copyOrMoveTargetRes.get(i);
                    try {
                        if (resWallper.isDir()) {
                            targeFileOrDirs.add(resWallper.getFileObj());
                            // continue;
                            // 复制目录
                            FSUtil.getAllDirAndFileByDir(targeFileOrDirs, resWallper.getFileObj(), 9999,
                                    -1, "*", true, IoType.FILE_AND_DIR);
        
                        } else {
                            // 复制文件
                            targeFileOrDirs.add(resWallper.getFileObj());
                        }
                        taskInfoTextLable.setText("正在从目录计算要复制的子文件.完成"+i+"个 "+resWallper.getAbsolutePathWithoutProtocol());
                        getTaskDIsplayPanel().updateUI();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (operation == FileOperation.MOVE) {
                for (int i = 0; i < copyOrMoveTargetRes.size(); i++) {
                    FileObject file = copyOrMoveTargetRes.get(i).getFileObj();
                    targeFileOrDirs.add(file);
                }
            }
        }
            for(int i=0;i<targeFileOrDirs.size();i++){
                
                FileObject fileObject = targeFileOrDirs.get(i);
                try {
                    taskInfoTextLable.setText("正在从计算即将复制的文件大小.完成"+i+"个 "+fileObject.getName().getBaseName());
                    getTaskDIsplayPanel().updateUI();
                    allByteSize+=fileObject.getContent().getSize();
                } catch (Exception e) {
                }                
            }


            status=STATUS_RUNNING;


            long downByteSize=0;

        taskInfoTextLable.setText("准备处理"+targeFileOrDirs.size()+"个资源.");
        for (int i = 0; i < targeFileOrDirs.size(); i++) {
            try {
                FileObject sourceFile = targeFileOrDirs.get(i);
                String info=""+i+"/"+targeFileOrDirs.size()+"  "+ByteUtil.getPrintSize(downByteSize)+"/"+ByteUtil.getPrintSize(allByteSize);

                if (pauseIng) {
                    long pauseTime=System.currentTimeMillis();
                    status=STATUS_PAUSEING;
                    while (pauseIng) {
                        sleep(1000);
                        taskInfoTextLable.setText("暂停中("+TimeUtil.getBetweenPrintMaxTime(System.currentTimeMillis()-pauseTime)+").进度:"+getProgress()+"% INFI:"+info);
                        if (stop) {
                            taskInfoTextLable.setText("已经手动停止.进度:"+getProgress()+"%  INFO:"+info);
                            status=STATUS_FINISH_WITH_STOP;
                            return ;
                        }
                    }
                }
                if (stop) {
                    taskInfoTextLable.setText("已经手动停止.进度:"+getProgress()+"%  INFO:"+info);
                    status=STATUS_FINISH_WITH_STOP;
                    return ;
                }


                status=STATUS_RUNNING;
                if (operation == FileOperation.COPY) {
                    copy(sourceFile);
                    taskInfoTextLable.setText(info+" Copy "+sourceFile.getName().getBaseName()+" to "+targetDirFile.toString());
                } else if (operation == FileOperation.MOVE) {
                    move(sourceFile);
                    taskInfoTextLable.setText(info+" Move "+sourceFile.getName().getBaseName()+" to "+targetDirFile.toString());
                }
                try {
                    downByteSize+=sourceFile.getContent().getSize();
                } catch (Exception e) {
                }      
                finishedTargeFileOrDirs.add(sourceFile);
                SwingUtilities.invokeLater(()  ->{
                    getTaskDIsplayPanel().updateUI();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
                status=STATUS_FINISH;
    }
 
    /**
     * 开始操作
     * 
     * @param tar
     */
    public void setResultProcessFun(Consumer<FileOperationResult> resultProcessFun) {
        this.resultProcessFun=resultProcessFun;
    }

    public void stopDo() {
 
    }
 

    

    HashMap<String,String>  reNameDirMap=new HashMap<>();//key = 被重命名的目录/文件,value =重命名之后的目录/文件

    /**
     * 复制文件
     * 
     * @param sourceFile
     * @param targetDir
     * @return
     */
    public FileOperationResult copy(FileObject sourceFile) {
        FileOperationResult ret = new FileOperationResult(true,sourceFile, targetDirFile, FileOperation.COPY);
        try {
            Boolean isDir=sourceFile.isFolder();
            FileObject distFile = targetDirFile.resolveFile(sourceFile.getName().getBaseName());
            String targetDirFilePath = targetDirFile.toString();
            if (!targetDirFilePath.endsWith("/")) {
                targetDirFilePath+="/";
            }
            String srouceAbsolutePath = sourceFile.toString();
            if (distFile instanceof LocalFile) {
                String targeFileOrDirsSouceDirAbsolutePath = targeFileOrDirsSouceDir.toString();
                int indexOf = srouceAbsolutePath.indexOf(targeFileOrDirsSouceDirAbsolutePath);
                if (indexOf > -1) {
                    // 截取路径
                    String reallTargetFilePath = srouceAbsolutePath
                            .substring(indexOf + targeFileOrDirsSouceDirAbsolutePath.length());
                    reallTargetFilePath = (targetDirFilePath + reallTargetFilePath);
                    distFile = targetDirFile.resolveFile(reallTargetFilePath);
                }
            }
            // 先创建dir

            FileObject parentDir = distFile.getParent();
            if (notNull(parentDir)) {
                String parentDirPath = parentDir.toString();
                String keyPath=null;
                for (String key : reNameDirMap.keySet()) {
                    if ( parentDirPath.startsWith(key)) {
                        keyPath=key;
                        break;
                    }
                }

                if ( notNull(keyPath)) {
                    //代表目录已经被改名

                    String fpath = distFile.toString();
                    // 目录已经被更名,则将子文件改变为文件夹
                    // 要求替换路径
                    String fpath2 = fpath.replace(keyPath, reNameDirMap.get(keyPath));
                    // 替换原来的文件
                    //asdasdsad
                    distFile=sourceFile.resolveFile(fpath2);
                }
                if (!parentDir.exists()) {
                    parentDir.createFolder();
                }
            }
            if (distFile.exists()) {
                //自动创建一个新的文件
                if (replace) {
                    
                }else{
                     //不要求替换自动重命名
                     distFile = getFileName(distFile);
                     //if ( isDir) {
                        reNameDirMap.put(srouceAbsolutePath, distFile.toString());  
                     //}
                }
                
            }
            
            
            ret.setDistResources(distFile);
            Opt<FileObject> df=new Opt<FileObject>(distFile);
            String targetDirPath= targetDirFile.toString();
            Value targetDirPathVal=new Value();
            if (! targetDirPath.endsWith("/")) {
                targetDirPath+="/";
            }
            targetDirPathVal.setValue(targetDirPath);
            distFile.copyFrom(sourceFile, new FileSelector() {

                @Override
                public boolean includeFile(FileSelectInfo fileInfo) throws Exception {
                    System.out.println("123123");
                    /**
                     *                      String targeFileOrDirsSouceDirPath = targeFileOrDirsSouceDir.toString();
                     if (!targeFileOrDirsSouceDirPath.endsWith("/")) {
                        targeFileOrDirsSouceDirPath+="/";
                     }
                    String absPath = fileInfo.getFile().toString();
                    String r1 = absPath.replace(targeFileOrDirsSouceDirPath, "");
                    String r2 = df.get().toString().replace(targetDirPathVal.getString(), "");
                    boolean copy = r1.equals(r2);
                    if (copy) {
                        logInfo("copy "+r2);
                    }else{
                        if (reNameDirMap.containsKey(absPath)) {
                            copy=true;
                        }else{
                            logInfo("skiped copy "+r2);                         
                        }
                    }
                     */
                    return true;
                }

                @Override
                public boolean traverseDescendents(FileSelectInfo fileInfo) throws Exception {
                     return false;
                }
                
            });
            //Files.copy(Paths.get(sourceFile.toURI()), distFilePath,
              //      StandardCopyOption.REPLACE_EXISTING);

        } catch (Exception e) {
            e.printStackTrace();
            ret.setResult(e.getMessage());
        }
        if (notNull(resultProcessFun)) {
            resultProcessFun.accept(ret);// 通知结果
        }
        return ret;
    }

    public FileObject getFileName(FileObject distFile) {
        return getFileName(distFile, 1);
    }

    public FileObject getFileName(FileObject distFile,int count) {
      
        try {
            RESWallper resWallper = new RESWallper(distFile);
            String file_baseName = resWallper.getBaseName();
            String file_extName = resWallper.getFileExtName();
            String a = distFile.getParent().getPublicURIString();
            a=(a+File.separator+file_baseName+" ("+count+")")+(notNull(file_extName)?("."+file_extName):"");
            FileObject file = distFile.resolveFile(a);
            if (file.exists()) {
                return getFileName(distFile, count+1);
            }
            return file;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return file;
 
        return distFile;
    }

    /**
     * 复制文件
     * 
     * @param sourceFile
     * @param targetDir
     * @return
     */
    public FileOperationResult move(FileObject sourceFile) {
        FileOperationResult ret = new FileOperationResult(true,sourceFile, targetDirFile, FileOperation.MOVE);
        try {

            FileObject distFile = targetDirFile.resolveFile(sourceFile.getName().getBaseName());
            sourceFile.moveTo(distFile);
        } catch (Exception e) {
            ret.setResult(e.getMessage());
        }
        resultProcessFun.accept(ret);// 通知结果
        return ret;
    }
 

    /**
     * InnerCopyOrMoveFIleThread
     */
    public class FileOperationResult {
        public static String SUCESS = "SUCESS";
        public static String ERROR_CANT_READ = "ERROR_CANT_READ";// 操作错误,sourceFile没有读权限
        public static String ERROR_CANT_WRITE = "ERROR_CANT_WRITE";// 目标目录不可写
        public static String ERROR_TARGET_DIR_NOT = "ERROR_TARGET_DIR_NOT";// 目标目录不存在

        FileOperation operation;// 文件操作 是否复制和还是移动
        FileObject source;
        Boolean sourceIsFile=false;
        FileObject targetDir;
        FileObject distResources;
        String result = SUCESS;

        public FileOperationResult(Boolean isFile,FileObject sourceFile, FileObject targetDir, FileOperation operation) {
            this.operation = operation;
            this.source = sourceFile;
            this.targetDir = targetDir;
            this.sourceIsFile=isFile;
        }


        /**
         * @return the distResources
         */
        public FileObject getDistResources() {
            return distResources;
        }

        
        /**
         * @param distResources the distResources to set
         */
        public void setDistResources(FileObject distResources) {   
                this.distResources = distResources;
        }

        /**
         * @return the sourceFile
         */
        public FileObject getSourceFile() {
            return source;
        }
        /**
         * @return the targetDir
         */
        public FileObject getTargetDir() {
            return targetDir;
        }
        /**
         * @param result the result to set
         */
        public void setResult(String result) {
            this.result = result;
        }

        public boolean isSucess() {
            return this.result!=null&&this.result==SUCESS;
        }

        /**
         * @return the operation
         */
        public FileOperation getOperation() {
            return operation;
        }
    }


    @Override
    public int getProgress() {
        if (isNull(targeFileOrDirs)||targeFileOrDirs.size()==0) {
            return 0;
        }
        int diliverNum = finishedTargeFileOrDirs.size();// 举例子的变量
        int queryMailNum = targeFileOrDirs.size();// 举例子的变量
        return  calc(diliverNum,queryMailNum);
    }

    @Override
    public String getName() {
        String actionName="复制";
        if (operation==FileOperation.MOVE) {
            actionName="移动";
        }
         return actionName+(isNull(targeFileOrDirs)?"未知":targeFileOrDirs.size())+"个目标到"+targetDirFile.getName();
    }


    JPanel taskDIsplayPanel;
    JLabel taskInfoTextLable=new JLabel();
    @Override
    public synchronized JPanel getTaskDIsplayPanel() {
        if (isNull(taskDIsplayPanel)) {
            taskDIsplayPanel=new JPanel(new MigLayout());
            taskDIsplayPanel.add(taskInfoTextLable);
            taskInfoTextLable.setText(getName());
        }
        return taskDIsplayPanel;
    }

 


}
