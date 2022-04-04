package github.acodervic.mod.thread;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * FixedPool
 */
public class FixedPool {
    static FixedPool threadPool;// 全局线程池,
    //线程池中的线程列表,注意 列表中的线程对象是会被线程池重用的,不会因为调用了stop()方法停止后就被线程池丢弃,当有新任务的时候线程对象依然会被重新启动
    LinkedList<Thread>  threadList=new LinkedList<Thread>();
    // 当前执行过的所有任务
    List<Task<?>> tasks = new LinkedList<Task<?>>();
    /**
     * 读取全局线程池,默认有一个线程可用,用于执行临时时间片任务.会创建一个核心线程数量为1,最大线程数量为5,30秒之内没有使用就会被回收的全局线程池
     *
     * @return
     */
    public static FixedPool getStaticFixedPool() {
        if (threadPool == null) {
            threadPool = new FixedPool(2, 60, "myModStaticFixedPool");
        }
        return threadPool;
    }

    /**
     * 读取所有任务,所有状态的
     * 
     * @return the tasks
     */
    public List<Task<?>> getAllTasks() {
        return tasks;
    }

    int concurrencyLength = 1;
    ThreadPoolExecutor pool = null;

    /**
     *
     * 创建一个定长线程池
     *
     * @param poolSize      核心线程数量(永远不会回收的线程数)
     * @param keepAivesTime 空闲时间秒,超过时间则会回收
     */
    public FixedPool(int poolSize, int keepAivesTime, String poolNamePrefix) {
        nullCheck(poolNamePrefix);
        this.concurrencyLength = poolSize;
        this.pool = new NamedThreadPoolExecutor(poolSize, poolSize, keepAivesTime, TimeUnit.SECONDS,poolNamePrefix,
                new LinkedBlockingDeque<Runnable>(),threadList);
        this.pool.allowCoreThreadTimeOut(true);// 允许回收核心线程
     }

    /**
     * 读取当前正在运行的任务列表
     *
     * @return
     */
    public synchronized List<Task<?>> getRuningTasks() {
        List<Task<?>> runingTasks = new LinkedList<Task<?>>();
        this.tasks.forEach(task -> {
            if (task.isRunning()) {
                runingTasks.add(task);
            }
        });
        return runingTasks;
    }

    /**
     * 读取当前线程池中已经完成的任务列表
     *
     * @return
     */
    public synchronized List<Task<?>> getFinishedTasks() {
        List<Task<?>> finishedTasks = new LinkedList<Task<?>>();
        this.tasks.forEach(task -> {
            if (task.isFinished()) {
                finishedTasks.add(task);
            }
        });
        return finishedTasks;
    }

    /**
     * 读取当前线程池中正在等待的任务列表
     *
     * @return
     */
    public synchronized List<Task<?>> getWaitingTasks() {
        List<Task<?>> waitingTasks = new LinkedList<Task<?>>();
        this.tasks.forEach(task -> {
            if (!task.isStarted()) {
                waitingTasks.add(task);
            }
        });
        return waitingTasks;
    }

    public synchronized int clearFinishedTask() {
        int size1 = this.tasks.size();
        List<Task<?>> needRemovelist = new LinkedList<Task<?>>();
        for (int i = 0; i < this.tasks.size(); i++) {
            Task<?> task = this.tasks.get(i);
            if (task.isFinished()) {
                needRemovelist.add(task);
            }
        }
        // 再进行删除
        for (int i = 0; i < needRemovelist.size(); i++) {
            try {
                this.tasks.remove(needRemovelist.get(i));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return size1 - this.tasks.size();
    }

    /**
     * 执行代码片段,task内部会填充future,再将task重新返回
     *
     * @param <T>
     *
     * @param <T>
     *
     * @param task
     * @return
     * @return
     */
    public synchronized <T>  Task<T> exec(Task<T> task) {
        nullCheck(task);
        Future<T> future = this.pool.submit(task);
        // 绑定future
        task.setFuture(future);
        this.tasks.add(task);
        return task;
    }

/**
 * 停止所有线程,如果后面继续提交task,内部线程池会自动启动
 */
    public synchronized void stopAllThread() {
        for (int i = 0; i < this.threadList.size(); i++) {
            try {
                this.threadList.get(i).stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * @return the threadList
     */
    public LinkedList<Thread> getThreadList() {
        return threadList;
    }
    /**
     * 强行杀死所有任务并释放资源
     */
    public synchronized void killAllTaskAndShutdown() {
        if (this.pool != null && !this.pool.isShutdown()) {
            this.pool.shutdownNow();
        }
    }

    /**
     * 有序关闭任务并释放,不接受新任务提交,同步函数
     */
    public synchronized void shutdownAllTask() {
        if (this.pool != null && !this.pool.isShutdown()) {
            this.pool.shutdown();
        }
    }

    /**
     * 判断任务是否全部执行完毕,当当前运行的线程数==0且任务队列的线程数量==0的时候返回true
     *
     * @return
     */
    public boolean isAllDone() {
        if (this.pool != null) {
            return this.getRuningTasks().size() == 0;
        }
        return true;
    }


    /**
     * @return the pool
     */
    public ThreadPoolExecutor getPool() {
        return pool;
    }

    /**
     * 读取的当前池中还未回收的线程
     *
     * @return
     */
    public int getNowAlivePoolSize() {
        return this.pool.getPoolSize();
    }
    /**
     * @param pool the pool to set
     */
    public void setPool(ThreadPoolExecutor pool) {
        this.pool = pool;
    }

    /**
     * 读取当前存活的线程数量
     *
     * @return
     */
    public int getActiveThreadCount() {
        return this.pool.getActiveCount();
    }
}