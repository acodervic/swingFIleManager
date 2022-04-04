package github.acodervic.mod.thread;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import github.acodervic.mod.UtilFunInter;
import github.acodervic.mod.data.Opt;

/**
 * F=被执行的函数类型 R=函数执行完成后返回的结果类型
 */
public abstract class AsyncTask<F, R> implements UtilFunInter {
    F callFun;// 函数式接口
    CountDownLatch executedCount;// 用于指定此任务被执行的目标次数,用于异步非阻塞

    protected AsyncTask(F callFun, Integer wantExecutedCount) {
        if (notNull(callFun)) {
            this.callFun = callFun;
            this.executedCount = new CountDownLatch(wantExecutedCount);// 执行之后就会被填充,代表被统计计数
        }
    }

    /**
     * 阻塞线程直到任务完成(必须完成固定的次数,如果没有指定则是1
     *
     * @throws InterruptedException
     */

    public void syncWaitFinish() throws InterruptedException {
        this.executedCount.await();
    }

    /**
     * 阻塞线程直到任务完成(必须完成固定的次数,如果没有指定则是1
     * 
     * @param timeout
     * @param unit
     * @throws InterruptedException
     */

    public void syncWaitFinish(long timeout, TimeUnit unit) throws InterruptedException {
        this.executedCount.await(timeout, unit);
    }

    /**
     * 进行一次计数
     */
    public void countDown() {
        this.executedCount.countDown();
    }

    /**
     * 执行任务
     * 
     * @param parms 如果运行任务时候传递的参数
     * @return 执行失败则返回null 并设置消息
     */
    public abstract Opt<R> executeFun(Object... parms);
    
    /**
     * 执行任务
     * 
     * @param parms 如果运行任务时候传递的参数
     * @return 执行失败则返回null 并设置消息
     */
    public abstract Opt<R> executeFun();

    public abstract Opt<R> executeFun(Object parm);

    
    public AsyncTask(F callFun) {
        this(callFun, 1);
    }

    public boolean hasFun() {
        return notNull(this.callFun);
    }

    /**
     * 获取调用函数
     *
     * @return
     */
    public F get() {
        return this.callFun;
    }

    public static void main(String[] args) {
        AsyncTask at = new AsyncTaskRunnable(() -> {
            try {
                System.out.println("开始");
                Thread.sleep(3000);
                System.out.println("结束");
            } catch (Exception e) {

            }
        });

        new Thread(() -> {
            at.executeFun();
        }).start();
        try {
            at.syncWaitFinish();
            System.out.println("1231");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
