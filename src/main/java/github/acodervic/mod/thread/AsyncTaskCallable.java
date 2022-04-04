package github.acodervic.mod.thread;

import java.util.concurrent.Callable;

import github.acodervic.mod.data.Opt;

/**
 * 一个支持Callable异步管理任务
 */
public class AsyncTaskCallable<R> extends AsyncTask<Callable<R>,R> {

    public AsyncTaskCallable(Callable<R> callFun) {
        super(callFun);
    }

    @Override
    public Opt<R>   executeFun() {
        Opt<R>   ret=new Opt<>();
        if (hasFun()) {
            try {
                ret.of(get().call());
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                countDown();
            }
        }
        return ret;
    }

    @Override
    @Deprecated
    public Opt<R> executeFun(Object... parms) {
        throw new RuntimeException("AsyncTaskCallable只支持executeFun()");
    }

    @Override
    public Opt<R> executeFun(Object parm) {
        throw new RuntimeException("AsyncTaskCallable只支持executeFun()");

    }


}
