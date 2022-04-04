package github.acodervic.mod.thread;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.print;
import static github.acodervic.mod.utilFun.sleep;

import java.util.Hashtable;

import github.acodervic.mod.shell.SystemUtil;

/**
 * 一个任务队列 T=任务对象类型
 */
public class TaskQueue {
    Integer maxRuningTaskCount = 1;
    // 等待运行的任务 key=任务的uuid,如果不定义则自动生成
    Hashtable<String, Runnable> waitStartTasks = new Hashtable<String, Runnable>();

    // 正在运行的任务 key=任务的uuid,如果不定义则自动生成
    Hashtable<String, Runnable> runningTasks = new Hashtable<String, Runnable>();

    // 运行完成的任务 key=任务的uuid,如果不定义则自动生成
    Hashtable<String, Runnable> finishTasks = new Hashtable<String, Runnable>();

    /**
     * 从等待任务队列中读取一个新任务
     *
     * @return
     */
    public synchronized TaskMapWallper getNewTask() {
        // 如果当前运行的任务数量,,大于等于最大并行任务数,或者waitStartTasks中没有任务 则阻塞等待
        while (runningTasks.size() >= maxRuningTaskCount || waitStartTasks.size() == 0) {
            sleep(100);
        }
        String taskKey = (String) waitStartTasks.keySet().toArray()[0];
        Runnable taskRunnable = (Runnable) waitStartTasks.get(taskKey);
        print("读取到新任务" + taskKey);
        waitStartTasks.remove( taskKey);
        return new TaskMapWallper(taskKey, taskRunnable);
    }

    /**
     * @return the maxRuningTaskCount
     */
    public Integer getMaxRuningTaskCount() {
        return maxRuningTaskCount;
    }

    /**
     * @param maxRuningTaskCount the maxRuningTaskCount to set
     */
    public void setMaxRuningTaskCount(Integer maxRuningTaskCount) {
        this.maxRuningTaskCount = maxRuningTaskCount;
    }

    /** 
     * 添加一个任务
     *
     * @param runnable
     * @throws InterruptedException
     */
    public void addTask(Runnable runnable) {
        nullCheck(runnable);
        addTask(SystemUtil.getUUID().toString(), runnable);
    }

    /**
     * 添加一个任务
     *
     * @param runnable
     * @throws InterruptedException
     */
    public void addTask(String key, Runnable runnable) {
        nullCheck(runnable, key);
        waitStartTasks.put(key, runnable);
    }

    /**
     * 开始任务,
     */
    public void listenAndStartUpTasks() {
        new Thread(() -> {
            while (true) {
                // 会不断的根据当前运行的任务数量,和任务队列启动新的线程执行任务
                try {
                    // 拿出并任务
                    TaskMapWallper taskMapWallper = getNewTask();
                    runningTasks.put(taskMapWallper.getTaskKey(), taskMapWallper.getRunnable());

                    MyTask myTask = new MyTask(taskMapWallper.getRunnable());
                    // 在运行之钱添加任务到正在运行的任务map中
             //       myTask.setOnBeforeRun(() -> {
               //     });
                    // 在任务后删除此key对应的 运行任务map中,并放入finishMap
                    myTask.setOnAfterRun(() -> {
                        runningTasks.remove(taskMapWallper.getTaskKey());
                        finishTasks.put(taskMapWallper.getTaskKey(), taskMapWallper.getRunnable());
                    });
                    // 启动任务
                    new Thread(myTask).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }).start();
        print("任务启动线程已经启动");
    }

    /**
     * task的map的key和value的包装
     */
    public class TaskMapWallper {
        String taskKey;
        Runnable runnable;

        /**
         * @return the taskKey
         */
        public String getTaskKey() {
            return taskKey;
        }

        /**
         * @param taskKey the taskKey to set
         */
        public void setTaskKey(String taskKey) {
            this.taskKey = taskKey;
        }

        /**
         * @return the runnable
         */
        public Runnable getRunnable() {
            return runnable;
        }

        /**
         * @param runnable the runnable to set
         */
        public void setRunnable(Runnable runnable) {
            this.runnable = runnable;
        }

        /**
         * @param taskKey
         * @param runnable
         */
        public TaskMapWallper(String taskKey, Runnable runnable) {
            this.taskKey = taskKey;
            this.runnable = runnable;
        }

    }

    /**
     * @param maxRuningTaskCount
     */
    public TaskQueue(Integer maxRuningTaskCount) {
        this.maxRuningTaskCount = maxRuningTaskCount;
    }




    public static void main(String[] args) {
        TaskQueue ta=new TaskQueue(2);
        ta.listenAndStartUpTasks();
        ta.addTask(()  ->{
            for (int i = 0; i < 30; i++) {
                sleep(1000);
                System.out.println("a"+i);
            }
        });
        ta.addTask(()  ->{
            for (int i = 0; i < 30; i++) {
                sleep(1000);
                System.out.println("b"+i);
            }
        });
        ta.addTask(()  ->{
            for (int i = 0; i < 30; i++) {
                sleep(1000);
                System.out.println("c"+i);
            }
        });
        ta.addTask(()  ->{
            for (int i = 0; i < 30; i++) {
                sleep(1000);
                System.out.println("d"+i);
            }
        });
        ta.addTask(()  ->{
            for (int i = 0; i < 30; i++) {
                sleep(1000);
                System.out.println("e"+i);
            }
        });


        sleep(99999);

    }
}