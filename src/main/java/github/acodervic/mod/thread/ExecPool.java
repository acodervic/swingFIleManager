package github.acodervic.mod.thread;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

/**
 * pool
 */
public class ExecPool {
    /**
     * 获取定长线程池(核心线程为1,最大线程为poolLength,空闲回收时间为60s)
     *
     * @param poolLength
     * @return
     */
    public static FixedPool getFixedPool(int poolLength, String poolNamePrefix) {
        nullCheck(poolNamePrefix);
        return new FixedPool(poolLength, 60,poolNamePrefix);
    }

        /**
     * 获取定长线程池
     * @param poolLength
     * @return
     */
    public static  TimePool getTimerPool(int poolLength) {
        return new TimePool(poolLength);
    }
}