package github.acodervic.filemanager.thread;

import github.acodervic.mod.thread.FixedPool;
import github.acodervic.mod.thread.Task;

public class PublicThreadPool {
    
    static FixedPool filePorcessPool;
    static FixedPool swingEventPool;


        /**
     * @return the filePorcessPool
     */
    synchronized static FixedPool getSwingEventPool() {
        if (filePorcessPool==null) {
            filePorcessPool=new FixedPool(2, 60, "swingEventPool");
        }
        return filePorcessPool;
    }
    /**
     * 执行一些文件操作,如 复制文件删除文件移动文件
     * @param taskName
     * @param runnable
     */
    public static void SwingEventPool_Exec(String taskName,Runnable runnable) {
        getSwingEventPool().exec(new Task<>(taskName,() ->{
            runnable.run();
            return null;
        }));
    }

    /**
     * @return the filePorcessPool
     */
     synchronized static FixedPool getFilePorcessPool() {
        if (filePorcessPool==null) {
            filePorcessPool=new FixedPool(4, 60, "filePorcessPool");
        }
        return filePorcessPool;
    }

    /**
     * 执行一些文件操作,如 复制文件删除文件移动文件
     * @param taskName
     * @param runnable
     * @return 
     */
    public static Task<Object> FilePorcessPool_Exec(String taskName,Runnable runnable) {
        return getFilePorcessPool().exec(new Task<>(taskName,() ->{
            runnable.run();
            return null;
        }));
    }

    
}
