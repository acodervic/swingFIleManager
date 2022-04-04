package github.acodervic.mod.data;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * java.util.Optional的替代类,有一些增强的函数
 */
public class Opt<T> implements Serializable {
    private static final long serialVersionUID = -3014355280483489480L;
    T value;// 包装的值
    boolean hasValue = false;// 代表是否填充了值
    str message=new str("");//消息
    Exception exception;//异常对象



    /**
     * 判断是否有消息
     * @return
     */
    public boolean hasMessage() {
        return this.message.notEmpty();
    }

    /**
     * 判断是否存在异常
     * @return
     */
    public boolean hasException() {
        return exception!=null;
    }
    /**
     * @return the message
     */
    public str getMessage() {
        return message;
    }
    /**
     * @param message the message to set
     */
    public void setMessage(str message) {
        if (message!=null) {
            this.message = message;
        }
    }
        /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message.setString(message);
    }

    /**
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }
    /**
     * @param exception the exception to set
     */
    public void setException(Exception exception) {
        this.exception = exception;
    }
    /**
     * 设置内部值为null
     *
     * @param value
     * @return
     */
    public Opt<T> ofNull() {
        this.value = null;
        return this;
    }

    /**
     * 填充内部值包装,可以为null
     *
     * @param value
     * @return
     */
    public Opt<T> of(T value) {
        this.value = value;
        this.hasValue = true;
        return this;
    }


    /**
     * 填充内部值包装,可以为null
     *
     * @param value
     * @return
     */
    public Opt<T> of(Opt<T> opt) {
        of(opt.get());
        setException(opt.getException());
        setMessage(opt.getMessage().to_s());
        return this;
    }


    /**
     * 填充内部值包装,可以为null
     *
     * @param valueFun 要求返回一个结果(不会报告任何错误)
     * @return
     */
    public Opt<T> of(Supplier<T> valueFun) {
        if (valueFun != null) {
            try {
                this.value = valueFun.get();
            } catch (Exception e) {
            }
            if (this.value != null) {
                this.hasValue = true;
            }
        }
        return this;
    }

    /**
     * 填充内部值包装,不可以为null,否则则会抛出运行时异常
     *
     * sadasd data
     *
     * @param value
     * @return
     */
    public Opt<T> ofNotNull(T value) {
        nullCheck(value);
        this.value = value;
        this.hasValue = true;
        return this;
    }

    /**
     * 返回内部包装值
     *
     * @data
     * @return
     */
    public T get() {
        return this.value;
    }

    /**
     * 内部值是否为null
     *
     * @return
     */
    public boolean isNull_() {
        return this.value == null;
    }

    /**
     * 内部值是否不为null
     *
     * @return
     */
    public boolean notNull_() {
        return !isNull_();
    }

    /**
     * 如果为null的时候执行函数
     *
     * @param action
     * @return
     */
    public Opt<T> ifNull_(Runnable action) {
        nullCheck(action);
        if (isNull_()) {
            action.run();
        }
        return this;
    }

    /**
     * 如果不为null的时候执行函数
     *
     * @param action
     * @return
     */
    public Opt<T> ifNotNull_(Consumer<T> action) {
        nullCheck(action);
        if (notNull_()) {
            action.accept(value);
        }
        return this;
    }

    /**
     * 如果当前内部有值则填充刷新
     * 
     * @param value
     * @return
     */
    public Opt<T> ifNotNullOf(T value) {
        if (notNull_()) {
            of(value);
        }
        return this;
    }

    /**
     * 如果不为null的时候执行函数,并返回一个结果
     *
     * @param <R>
     *
     * @param action
     * @return
     */
    public <R> R ifNotNullReturnOrElse_(Function<T, R> ret1Action, R ret2) {
        nullCheck(ret1Action);
        if (notNull_()) {
            return ret1Action.apply(get());
        }
        return ret2;
    }

    /**
     * @param value
     */
    public Opt(T value) {
        this.value = value;
    }

    /**
     * 
     */
    public Opt() {
    }

    /**
     * 如果内部值为null则重新设置值
     *
     * @param value
     */
    public void ifNullSet_(T value) {
        if (this.value == null) {
            this.value = value;
        }
    }

    /**
     * 如果内部值为null则返回某个值,是否则使用get方法读取内部值
     *
     * @param value
     * @return
     */
    public T ifNullRet_(T value) {
        if (this.isNull_()) {
            return value;
        }
        return get();
    }

    /**
     * 如果内部为null则抛null异常
     * 
     * @param msg
     * @throws NullPointerException
     */
    public void ifNullThowNPE(String msg) throws NullPointerException {
        if (isNull_()) {
            throw new NullPointerException(msg != null ? msg : "Opt包装的值允许为null!并主动抛出异常!");
        }
    }

    /**
     * 如果内部为null则抛null异常
     * 
     * @param msg
     * @throws NullPointerException
     */
    public void ifNullThowNPE() throws NullPointerException {
        if (isNull_()) {
            ifNullThowNPE(null);
        }
    }

    /**
     * 如果内部为null则抛运行时异常
     * 
     * @param msg
     * @throws NullPointerException
     */
    public void ifNullThowRunTimeNPE(String msg) {
        if (isNull_()) {
            throw new RuntimeException(msg != null ? msg : "Opt包装的值允许为null!并主动抛出异常!");
        }
    }

    /**
     * 如果内部为null则抛运行时异常
     * 
     * @param msg
     * @throws NullPointerException
     */
    public void ifNullThowRunTimeNPE() {
        if (isNull_()) {
            ifNullThowNPE(null);
        }
    }

}