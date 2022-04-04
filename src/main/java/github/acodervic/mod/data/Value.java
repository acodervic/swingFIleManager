package github.acodervic.mod.data;

/**
 * u用来封装一些值的对象,以不以final修饰在函数式编程中使用
 */
public class Value {
    Object val;

    public synchronized  int getInt() {
        return (int) val;
    }

    public synchronized  Integer getInteger() {
        return (Integer) this.val;
    }

    public  synchronized String getString() {
        return (String) val;
    }

    public synchronized  Boolean getBoolean() {
        return (Boolean) val;
    }

    public synchronized char getChar() {
        return (char) this.val;
    }

    public synchronized byte getbyte() {
        return (byte) this.val;
    }

    public  synchronized Byte getByte() {
        return (Byte) this.val;
    }

    public  synchronized Long getLong() {
        return (Long) this.val;
    }

    public synchronized  long getlong() {
        return (long) this.val;
    }

    public  synchronized double getdouble() {
        return (double) this.val;
    }

    public synchronized  Double getDouble() {
        return (Double) this.val;
    }

    public synchronized float getfloat() {
        return (float) this.val;
    }

    public synchronized Float getFloat() {
        return (Float) this.val;
    }

    public synchronized <T> T get(Class<T> type) {
        return (T) this.val;
    }

    /**
     * 读取对象
     * 
     * @return
     */
    public Object get() {
        return this.val;
    }

    /**
     * @param val
     */
    public Value(Object val) {
        this.val = val;
    }

    public Value() {
    }

    /**
     * 修改内部值,同步函数
     *
     * @param object
     */
    public synchronized void setValue(Object object) {
        this.val = object;
    }

    public boolean isNull() {
        if (this.val!=null) {
            return  false;
        }else {
            return true;
        }
    }
    public boolean isNotNull() {
        return !isNull();
    }
}