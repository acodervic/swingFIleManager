package github.acodervic.mod.function;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.util.Optional;
import java.util.function.Supplier;
import java.util.logging.Logger;

/**
 * 一些方便的函数式编程函数
 */
public class FunctionUtil {
    static final Logger log = Logger.getLogger(FunctionUtil.class.getName());

    /**
     * 如果k为null则抛出异常e
     * 
     * @param <K>
     * @param <T>
     * @param k_opt   检测为null的对象
     * @param e   如果k为null则抛出的异常对象
     * @throws T
     */
    public static <K, T extends Exception> void ifNullThrow(K k_opt, T e) throws T {
        ifThrow(k_opt == null, e);
    }

    /**
     * 如果flag为true则抛出异常e
     * 
     * @param <T>
     * @param flag 标记判断
     * @param e    被抛出的异常
     * @throws T
     */
    public static <T extends Exception> void ifThrow(boolean flag, T e) throws T {
        nullCheck(flag, e);
        if (flag) {
            throw e;
        }
    }

    /**
     * 如果value为null则调用runnable的run函数,注意是调用run而不是新建线程,会同步阻塞当前线程
     * 
     * @param value_opt    判断是否为null的值
     * @param runnable 如果为null则执行的runnale的对象
     */
    public static void ifNullThen(Object value_opt, Runnable runnable) {
        ifThen(value_opt == null, runnable);
    }

    /**
     * 如果value不为null则调用runnable的run函数,注意是调用run而不是新建线程,会同步阻塞当前线程
     * 
     * @param value_opt    判断是否为null的值
     * @param runnable 如果为null则执行的runnale的对象
     */
    public static void ifNotNullThen(Object value_opt, Runnable runnable) {
        ifThen(value_opt != null, runnable);
    }

    /**
     * 如果flag为true,则调用t对象的run方法
     * 
     * @param <T>
     * @param flag 判断标记
     * @param t    执行run函数的对象,一般为runnable实例
     */
    public static <T extends Runnable> void ifThen(boolean flag, T t) {
        nullCheck(flag, t);
        if (flag) {
            t.run();
        }
    }

    /**
     * 如果flag为true,则调用t1对象的run方法,否则则调用t2对象的run方法
     * 
     * @param <T>
     * @param flag 判断标记
     * @param t1   执行run函数的对象,一般为runnable实例
     * @param t2   执行run函数的对象,一般为runnable实例
     */
    public static <T extends Runnable> void ifThen(boolean flag, T t1, T t2) {
        nullCheck(flag, t1, t2);
        if (flag) {
            t1.run();
        } else {
            t2.run();
        }
    }

    /**
     * 如果value不为null则返回t1否则返回t2,t1和t2必须类型相同
     * 
     * @param <K>
     * @param <T>
     * @param value_opt 被判断是否为null的值
     * @param t1    null时候的返回
     * @param t2    不为null时候的返回
     * @return
     */
    public static <K, T> T ifNotNullReturn(K value_opt, T t1, T t2) {
        return ifReturn(value_opt != null, t1, t2);
    }

    /**
     * 如果value不为null则返回supplier函数式提供的结果
     * 
     * @param <T>
     * @param value_opt    判断不为null的变量
     * @param supplier 提供的结果的supplier函数式
     * @return
     */
    public static <T> T ifNotNullReturn(T value_opt, Supplier<T> supplier) {
        nullCheck(supplier);
        if (null != value_opt) {
            return value_opt;
        }
        return supplier.get();
    }

    /**
     * 如果flag为true则返回t1否则返回t2.t1和t2必须类型相同
     * 
     * @param <T>
     * @param flag 判断变量
     * @param t1
     * @param t2
     * @return
     */
    public static <T> T ifReturn(boolean flag, T t1, T t2) {
        nullCheck(flag, t1, t2);
        return (flag ? t1 : t2);
    }

    /**
     * 如果flag为true则返回t1否则返回t2.t1和t2必须类型相同
     * 
     * @param <T>
     * @param flag 判断变量
     * @param s1   s1的返回值函数式
     * @param s2   s2的返回函数式
     * @return
     */
    public static <T> T ifReturn(boolean flag, Supplier<T> s1, Supplier<T> s2) {
        nullCheck(flag, s1, s2);
        if (flag) {
            return s1.get();
        }
        return s2.get();
    }

    /**
     * 如果flag为true则返回t1,否则抛出一个RuntimeException
     * 
     * @param <T>
     * @param flag   判断的标志变量
     * @param t1_opt flag为null返回的变量
     * @param e      flag为flase时候抛出的运行时异常
     * @return
     */
    public static <T> T ifReturnOrThrow(boolean flag, T t1_opt, RuntimeException e) {
        nullCheck(flag, e);
        if (flag)
            return t1_opt;
        throw e;
    }



/**
 * 尝试做一些任务,而无需处理异常,当内部发生异常则返回异常,否则返回null
 * 
 * @param task
 * @param reportTheError 是否自动报告异常?
 * @return
 */
public static Exception tryDo(TrySupplierFun task, boolean reportTheError) {
    nullCheck(task, reportTheError);
        try {
            task.get();
        } catch (Exception e) {
            if (reportTheError) {
                e.printStackTrace();
                return e;
            }
            return null;

        }
        return null;
    }
    
 
        /**
         * 执行表达式并尝试返回结果,如果出现异常则返回errorVal,默认自动报告异常printStackTrace()
         *
         * @param <T>              类型
         * @param supplier         有返回值的表达式
         * @param errorVal_opt     执行表达式异常之后的返回
         * @param errorMessage_opt 一段错误信息
         * @return 一个返回值
         */
        public static <T> T tryReturn(TrySupplierReturnFun<T> supplier, T errorVal_opt, String errorMessage_opt) {
            return tryReturn(supplier, errorVal_opt, errorMessage_opt, true);
    }

            /**
             * 执行表达式并尝试返回结果,如果出现异常则返回errorVal
             *
             * @param <T>              类型
             * @param supplier         有返回值的表达式
             * @param errorVal_opt     执行表达式异常之后的返回
             * @param errorMessage_opt 一段错误信息
             * @param reportException  是否报告异常(printStackTrace())?
             * @return 一个返回值
             */
            public static <T> T tryReturn(TrySupplierReturnFun<T> supplier, T errorVal_opt, String errorMessage_opt,
                    boolean reportException) {
                nullCheck(supplier, reportException);
        try {
            return supplier.get();
        } catch (Exception ex) {
            System.out.print(get(() -> errorMessage_opt).orElse(""));
            if (reportException) {
                ex.printStackTrace();
            }
            return errorVal_opt;
        }
    }


    /**
     * 执行表达式并尝试返回结果,如果出现异常则返回errorVal,默认自动报告异常printStackTrace()
     * 
     * @param <T>          类型
     * @param supplier     有返回值的表达式
     * @param errorVal_opt 执行表达式异常之后的返回(可选)可以为null
     * @return 一个返回值
     */
    public static <T> T tryReturn(TrySupplierReturnFun<T> supplier, T errorVal_opt) {
        return tryReturn(supplier, errorVal_opt, null);
    }

    /**
     * 读取值,内部对值进行非空验证 使用方法 get( ()->str.toString()).orElse("null");
     * 通过str.toString()获取值,如果出现空值则返回null
     * 
     * @param <T>      类型
     * @param resolver 一段有返回值的lambda表达式
     * @return 一个值
     */
    public static <T> Optional<T> get(Supplier<T> resolver) {
        try {
            nullCheck(resolver);
            T result = resolver.get();
            return Optional.of(result);
        } catch (NullPointerException e) {
            // 可能会抛出空指针异常，直接返回一个空的 Optional 对象
            return Optional.empty();
        }
    }
}