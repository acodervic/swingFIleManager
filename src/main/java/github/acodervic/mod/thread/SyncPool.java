package github.acodervic.mod.thread;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.*;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

/**
 * 使用foreach来模拟携程
 */
public class SyncPool {
    List<Runnable> threadList = new LinkedList<Runnable>();
    ForkJoinPool forkJoinPool;// 后面可能会使用

    /**
     * 添加任务
     * 
     * @param task
     * @return
     */
    public SyncPool addTask(Runnable task) {
        nullCheck(task);
        threadList.add(task);
        return this;
    }

    /**
     * 阻塞直到 所有任务执行完成
     */
    public void startAwaitAllDone() {
        this.threadList.parallelStream().forEach(task -> {
            task.run();
        });
    }

    public static void main(String[] args) {
        SyncPool syncPool = new SyncPool();
        syncPool.addTask(() -> {
            int a = 1;
            while (a < 30) {
                System.out.println(a);
                a += 1;
                sleep(100);

            }
        });
        syncPool.addTask(() -> {
            int a = 30;
            while (a < 60) {
                System.out.println(a);
                a += 1;
                sleep(100);
            }
        });
        syncPool.startAwaitAllDone();
        System.out.println("全部完成");
    }
}
