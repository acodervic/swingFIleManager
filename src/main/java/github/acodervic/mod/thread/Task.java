package github.acodervic.mod.thread;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.TimeUtil;
import github.acodervic.mod.shell.SystemUtil;

/**
 * 线程池执行任务包装,每个任务都是一个Task,此对象会维护,任务的执行时间,状态,返回值等
 */
public class Task<T> implements Callable<T> {
    String taskName = SystemUtil.getUUID().toString();
    Thread workThread;//执行任务的线程,当任务被线程池调用时候会被填充,当任务执行完毕之后会被置null
    long jobStartTime = 0;
    long jobEndTime = 0;
    Callable<T> task;// 真正执行的任务
    Future<T> future;// 当task任务在线程池运行后,future将会被返回填充,我们可以直接操作task对象来操作future
    T result;// 执行结果,当执行完成后会填充,注意仅仅会存储最新的一次结果,如果一个任务需要多次执行,且不需要结果重复,请使用new进行重复新建任务

    /**
     * 停止任务,当stopRunningJob为true的时候,即使任务已经在线程池中运行依然会发送中断信号请求中止.如果被终止线程存在循环
     * 被执行的任务内部必须对Thread 的最外层函数的中断异常进行捕获,否则无法正常终止程序. 不允许直接杀死线程!如果需要强制杀死线程请使用
     * killworkThread
     * 如果stopRunningJob=false,则会取消还未运行的任务。
     *
     * @param stopRunningJob
     * @return
     */
    public boolean stop(boolean stopRunningJob) {
        if (this.future != null) {
            return this.future.cancel(stopRunningJob);
        }
        return false;
    }

    /**
     * 如果当然任务仍在运行,则强制杀死线程,任务也会被直接中断。但不会影响线程池,当线程池接收到新的任务后,依然可能会开启当前线程并启动
     * @return 如果成功杀死则返回true,否则返回flase(当任务没有在运行中时)
     */
    public boolean killWorkThread() {
        if (this.isRunning()&&getWorkThread()!=null) {
            getWorkThread().stop();
            return true;
        }
        return false;
    }
    /**
     * 构造一个任务
     *
     * @param task
     */
    public Task(String taskName_opt, Callable<T> task) {
        nullCheck(task);
        this.task = task;
        if (taskName_opt != null) {
            this.taskName = taskName_opt;
        }
    }

    /**
     * 构造一个任务
     *
     * @param task
     */
    public Task(Callable<T> task) {
        this(null, task);
    }

    public Callable<T> getJob() {
        return this.task;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * 读取当前任务运行了多少毫秒
     * 
     * @return
     */
    public long getRuningMsTime() {
        // 还没开始
        if (this.jobStartTime == 0) {
            return 0;
        }
        // 已经结束
        if (this.jobEndTime != 0) {
            return this.jobEndTime - this.jobStartTime;
        }
        // 还未结束
        return TimeUtil.getNowLong() - this.jobStartTime;
    }

    /**
     * 任务是否已经启动完成,运行中和已完成都会返回true
     *
     * @return
     */
    public boolean isStarted() {
        return this.jobStartTime != 0;
    }

    /**
     * 读取任务执行消耗的时间,注意返回的结果如果为null,则代表任务未启动或未完成
     */
    public Opt<Long> getJobConsumTime() {
        if (isFinished()) {
            return new Opt<Long>(this.jobEndTime - this.jobStartTime);
        }
        return new Opt<Long>();
    }
    /**
     * @return the workThread
     */
    public Thread getWorkThread() {
        return workThread;
    }

    /**
     * 读取任务结果,注意如果返回Null则代表任务无返回结果,或者任务未执行完成!
     * 
     * @return
     */
    public Opt<T> getResult() {
        return new Opt<T>(result);
    }

    /**
     * 任务是否已经完成(包括异常终止)
     *
     * @return
     */
    public boolean isFinished() {
        if (this.future != null && this.future.isDone()) {
            if (this.jobEndTime == 0) {
                workDone();
            }
            return true;
        }
        return this.jobEndTime != 0;
    }

    /**
     * 任务是否还在运行中
     *
     * @return
     */
    public boolean isRunning() {
        return this.jobStartTime != 0 && this.jobEndTime == 0;
    }

    /**
     * 当任务真正开始执行的入口,阻塞并返回结果,又线程池调用
     */
    @Override
    public T call() throws Exception {
        Exception exce = null;
        workStart();
        try {
            // 会被调用且并阻塞
            result = getJob().call();
        } catch (Exception e) {
            e.printStackTrace();
            // 抛出异常到上层线程池,确保任务状态被捕获
            exce = e;
        }

        // 任务已经完成
        // 记录结束时间
        workDone();
        if (exce != null) {
            throw exce;
        }
        return result;

    }

    /**
     * @param future the future to set
     */
    public void setFuture(Future<T> future) {
        this.future = future;
    }

    /**
     * 工作完成记录结束时间,和置空工作线程
     */
     void workDone() {
        this.workThread=null;
        this.jobEndTime = TimeUtil.getNowLong();
    }
    /**
     * 工作开始记录开始时间,和工作线程
     */
    void workStart() {
             //填充当前执行任务的线程
             this.workThread=Thread.currentThread();
             // 记录开始时间
             this.jobStartTime = TimeUtil.getNowLong();
    }


    /**
     * @return the future
     */
    public Future<T> getFuture() {
        return future;
    }
}
