package github.acodervic.mod.thread;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

/**
 * MyTask 用于包裹runnable接口,以便在runnable的 运行前和运行后执行函数
 */
public class MyTask implements Runnable {
    Runnable runnable;
    Runnable onBeforeRun;
    Runnable onAfterRun;

    /**
     * @param runnable
     */
    public MyTask(Runnable runnable) {
        nullCheck(runnable);
        this.runnable = runnable;
    }

    @Override
    public void run() {
        if (onBeforeRun != null) {
            onBeforeRun.run();
         }
        runnable.run();

        if (onAfterRun != null) {
            onAfterRun.run();
         }

    }

    /**
     * @return the onBeforeRun
     */
    public Runnable getOnBeforeRun() {
        return onBeforeRun;
    }

    /**
     * @param onBeforeRun the onBeforeRun to set
     */
    public void setOnBeforeRun(Runnable onBeforeRun) {
        this.onBeforeRun = onBeforeRun;
    }

    /**
     * 在run函数u运行之后执行的代码
     * 
     * @return the onAfterRun
     */
    public Runnable getOnAfterRun() {
        return onAfterRun;
    }

    /**
     * 在run函数u运行之前执行的代码
     * 
     * @param onAfterRun the onAfterRun to set
     */
    public void setOnAfterRun(Runnable onAfterRun) {
        this.onAfterRun = onAfterRun;
    }

}