package github.acodervic.mod.data.mode;

/**
 * 用来存储两个被比对的对象
 * 
 * @param <Object>
 * @param <D>
 */
public class CompareObj {
    Object objA;
    Object objB;

    public <T> T getObjA(Class<T> c) {
        return (T) this.objA;
    }

    public <T> T getObjB(Class<T> c) {
        return (T) this.objB;
    }
    public Object getObjA() {
        return objA;
    }

    public void setObjA(Object objA) {
        this.objA = objA;
    }

    public Object getObjB() {
        return objB;
    }

    public void setObjB(Object objB) {
        this.objB = objB;
    }

    public CompareObj(Object objA, Object objB) {
        this.objA = objA;
        this.objB = objB;
    }

 
    
    
}