package github.acodervic.mod.thread;

import java.util.function.Consumer;

import github.acodervic.mod.data.Opt;
/**
 * 一个支持Runnable异步管理的任务,返回值是一个Object(其实没有返回值)
 */
public class AsyncTaskConsumer extends AsyncTask<Consumer,Object> {

    public AsyncTaskConsumer(Consumer callFun) {
        super(callFun);
    }

    /**
     * 执行函数,注意此函数会返回null
     */
    @Override
    public Opt<Object> executeFun(Object... parms) {
        if (hasFun()) {
            try {
                get().accept(parms);
            } catch (Exception e) {
                throw e;
            }
            countDown();
        }
        return null;
    }

    @Override
    public Opt<Object> executeFun(Object parm) {
        if (hasFun()) {
            try {
                get().accept(parm);
            } catch (Exception e) {
                throw e;
            }
            countDown();
        }
        return null;
    }
    
    @Override
    @Deprecated
    public Opt<Object> executeFun() {
        throw new RuntimeException("AsyncTaskCallable只支持executeFun(参数)");
    }

}
