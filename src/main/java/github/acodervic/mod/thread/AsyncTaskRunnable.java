package github.acodervic.mod.thread;

import java.util.concurrent.Callable;

import github.acodervic.mod.data.Opt;

/**
 * 一个支持Runnable异步管理的任务,返回值是一个Object(其实没有返回值)
 */
public class AsyncTaskRunnable extends AsyncTask<Runnable,Object> {

    public AsyncTaskRunnable(Runnable callFun) {
        super(callFun);
    }

    @Override
    public Opt<Object> executeFun(Object... parms) {
        if (hasFun()) {
            try {
                get().run();
            } catch (Exception e) {
            }
            countDown();
        }
        return null;
    }

    @Override
    public Opt<Object> executeFun() {
        try {
            get().run();
        } catch (Exception e) {
            throw e;
        }finally{
            countDown();
        }
        return null;//没有返回值
    }

 



    @Override
    public Opt<Object> executeFun(Object parm) {
        throw new RuntimeException("AsyncTaskRunnable只支持executeFun(参数)");

    }

}
