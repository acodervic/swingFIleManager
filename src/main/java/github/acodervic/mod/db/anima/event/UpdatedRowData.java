package github.acodervic.mod.db.anima.event;

public class UpdatedRowData<T> {
    T oldRowObj;
    T newRowObj;

    /**
     * @return the newRowObj
     */
    public T getNewRowObj() {
        return newRowObj;
    }
    /**
     * @return the oldRowObj
     */
    public T getOldRowObj() {
        return oldRowObj;
    }

    /**
     * @param oldRowObj
     * @param newRowObj
     */
    public UpdatedRowData(T oldRowObj, T newRowObj) {
        this.oldRowObj = oldRowObj;
        this.newRowObj = newRowObj;
    }
}