
package github.acodervic.mod.db.anima.core;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 * Atomic
 * <p>
 * Used to save the exception information after the end of a transaction. There
 * is currently no other storage. You can catch and handle them after an
 * exception occurs.
 *
 * @author biezhi
 * @date 2018/3/15
 */
public class Atomic {
 
    private Exception e;
    private boolean   isRollback;

    public Atomic(Exception e) {
        this.e = e;
    }

    public static Atomic ok() {
        return new Atomic();
    }

    public static Atomic error(Exception e) {
        return new Atomic(e);
    }

    public Atomic rollback(boolean isRollback) {
        this.isRollback = isRollback;
        return this;
    }

    public boolean isRollback() {
        return isRollback;
    }

    public Atomic catchException(Consumer<Exception> consumer) {
        if (null != e) {
            consumer.accept(e);
        }
        return this;
    }

    public <R> R catchAndReturn(Function<Exception, R> function) {
        if (null != e) {
            return function.apply(e);
        }
        return null;
    }

    /**
     * 
     */
    public Atomic() {
    }

}
