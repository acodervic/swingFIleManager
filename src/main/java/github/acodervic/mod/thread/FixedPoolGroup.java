package github.acodervic.mod.thread;

import java.util.LinkedList;
import java.util.List;

/**
 * FixedPool的组,可以将多个FixedPool联合起来进行操作,如读取任务数量,停止任务等
 */
public class FixedPoolGroup {

    List<FixedPool> fixedPools = new LinkedList<FixedPool>();

    /**
     * @return the fixedPools
     */
    public List<FixedPool> getFixedPools() {
        return fixedPools;
    }
    /**
     * 添加线程池
     * 
     * @param pool
     */
    public void addFixedPool(FixedPool pool) {
        if (!this.fixedPools.contains(pool)) {
            this.fixedPools.add(pool);
        }
    }

    /**
     * 读取当前组中正在等待的任务
     *
     * @return
     */
    public synchronized List<Task<?>> getWaitingTasks() {
        List<Task<?>> waitingTasks = new LinkedList<Task<?>>();
        fixedPools.forEach(pool -> {
            waitingTasks.addAll(pool.getWaitingTasks());
        });
        return waitingTasks;
    }

    /**
     * 读取当前组中运行完成的任务
     *
     * @return
     */
    public synchronized List<Task<?>> getFinshedTasks() {
        List<Task<?>> finshedTasks = new LinkedList<Task<?>>();
        fixedPools.forEach(pool -> {
            finshedTasks.addAll(pool.getFinishedTasks());
        });
        return finshedTasks;
    }

    /**
     * 读取当前组正在运行的任务列表
     *
     * @return
     */
    public synchronized List<Task<?>> getRuningTasks() {
        List<Task<?>> runingTasks = new LinkedList<Task<?>>();
        fixedPools.forEach(pool -> {
            runingTasks.addAll(pool.getRuningTasks());
        });
        return runingTasks;
    }

}
