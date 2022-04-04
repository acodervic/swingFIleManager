package github.acodervic.mod.thread;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class NamedThreadPoolExecutor extends ThreadPoolExecutor {

    private static final String THREAD_NAME_PATTERN = "%s-%d";
    public NamedThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, final TimeUnit unit,
                               final String namePrefix,LinkedBlockingDeque<Runnable>  qDeque,LinkedList<Thread> saveThreadList) {
       super(corePoolSize, maximumPoolSize, keepAliveTime, unit, qDeque,
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger();
                @Override
                public Thread newThread(Runnable r) {
                    final String threadName = String.format(THREAD_NAME_PATTERN, namePrefix, counter.incrementAndGet());
                    Thread thread = new Thread(r, threadName);
                    saveThreadList.add(thread);
                    return  thread;
                }
            });
    }
}
