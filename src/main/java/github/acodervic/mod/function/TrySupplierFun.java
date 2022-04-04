package github.acodervic.mod.function;
/**
 * 内部捕捉异常的执行表达式返回函数
 * @param <T>
 */
@FunctionalInterface
public interface TrySupplierFun {
    public void  get() throws Exception;
}

