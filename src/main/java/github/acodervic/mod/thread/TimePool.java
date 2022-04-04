package github.acodervic.mod.thread;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import github.acodervic.mod.Constant;
import github.acodervic.mod.data.TimeUtil;

/**
 * timer
 */
public class TimePool {
    static TimePool threadPool;// 全局时间池.用来执行定时任务
    int concurrencyLength = 1;
    ScheduledExecutorService pool = null;
    TimeUnit timeUnit = TimeUnit.MILLISECONDS;// 毫秒
    Map<Runnable,ScheduledFuture> taskMap=new HashMap<>();

    /**
     * 读取全局时间池
     *
     * @return
     */
    public static TimePool getStaticTimePool() {
        if (threadPool == null) {
            threadPool = new TimePool(1);
        }
        return threadPool;
    }

    public TimePool(int concurrencyLength) {
        if (concurrencyLength < 1) {
            System.out.println("concurrencyLength必须大于等于1");
            return;
        }
        this.concurrencyLength = concurrencyLength;
        // 创建一个单线程顺序池,用来执行三个线程的任务,执行三个任务的先后顺序和定时时间有关
        pool = Executors.newScheduledThreadPool(concurrencyLength);
    }

    /**
     * 定时任务
     *
     * @param timeIntervalMs
     * @param task
     * @return
     */
    public ScheduledFuture<?> Interval(long timeIntervalMs, Runnable task) {

         ScheduledFuture<?> scheduleAtFixedRate = pool.scheduleAtFixedRate(() -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, timeIntervalMs, TimeUnit.MILLISECONDS);
        taskMap.put(task, scheduleAtFixedRate);
        return scheduleAtFixedRate;
    }

    /**
     * 定时执行
     * 
     * @param timeMs
     * @param task
     * @return
     */
    public ScheduledFuture<?> setTimeOut(long timeMs, Runnable task) {
          ScheduledFuture<?> schedule = this.pool.schedule(() -> {
            try {
                task.run();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, timeMs, timeUnit);// 当放入线程池之后 n毫秒之后执行
        taskMap.put(task, schedule);
        return schedule;
    }

    /**
     * 在一个时间点来做这件事
     * 
     * @param time
     * @param task
     * @return
     */
    public  boolean doInTime(Date time, Runnable task) {
        if (TimeUtil.dateToLong(time) <= TimeUtil.dateToLong(new Date())) {
            Constant.dbg("定时时间必须大于当前时间!" + time);
            return false;
        } else {
            long 当前时间到目标点的毫秒差 = time.getTime() - TimeUtil.getNowLong();
            setTimeOut(当前时间到目标点的毫秒差, task);
            return true;
        }
    }

    /**
     * 强行杀死所有任务并释放资源
     */
    public  synchronized void killAllTaskAndShutdown() {
        if (this.pool != null && !this.pool.isShutdown()) {
            this.pool.shutdownNow();
        }
    }

    /**
     * 有序关闭任务并释放,不接受新任务提交
     */
    public  synchronized void shutdownAllTask() {
        if (this.pool != null && !this.pool.isShutdown()) {
            this.pool.shutdown();
        }
    }

        /**
     * 判断任务是否全部执行完毕,当当前运行的线程数==0且任务队列的线程数量==0的时候返回true
     * @return
     */
    public boolean isAllDone() {
        if (this.pool!=null) {
            return  this.getRunningTaskCount()==0&&getWattingTaskCount()==0;
        }
        return true;
}



/**
 * 读取当前等待任务的数量
 *
 * @return
 */
public int getWattingTaskCount() {
    if (this.pool!=null) {
        ThreadPoolExecutor tpe = ((ThreadPoolExecutor) this.pool);
        return tpe.getQueue().size();
    }
    return 0;
}
    /**
     * 此函数必须放在线程池任务中进行检测运行,用于检测整个线程池是否仅有当前任务没有运行完毕
     * @return
     */
    public boolean isOnlyMeRuning() {
        if (this.pool != null) {
            return this.getRunningTaskCount() == 1 && getWattingTaskCount() == 0;
        }
        return true;
    }

/**
 * 读取当前等待任务的数量
 *
 * @return
 */
public int getRunningTaskCount() {
    if (this.pool!=null) {
        ThreadPoolExecutor tpe = ((ThreadPoolExecutor) this.pool);
        return tpe.getActiveCount();
    }
    return 0;
}


/**
 * 读取当前已经完成的任务数量
 * @return
 */
public long getCompletedTaskCount() {
    if (this.pool!=null) {
        ThreadPoolExecutor tpe = ((ThreadPoolExecutor) this.pool);
        return tpe.getCompletedTaskCount();
    }
    return 0;
}


/**
 * 读取当前用户提交的总任务数量
 * @return
 */
public long getAllTaskCount() {
    if (this.pool!=null) {
        ThreadPoolExecutor tpe = ((ThreadPoolExecutor) this.pool);
        return tpe.getTaskCount();
    }
    return 0;
}

/**
 * 停止任务
 * 
 * @param task
 * @return
 */
public boolean stop(Runnable task) {
    if (task != null && taskMap.containsKey(task)) {
        return taskMap.get(task).cancel(false);
    }
    return false;
}

/**
 * 停止任务
 * 
 * @param task
 * @return
 */
public boolean stop(ScheduledFuture task) {
    if (task != null && taskMap.containsValue(task)) {
        return task.cancel(false);
    }
    return false;
}
}