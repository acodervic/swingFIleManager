package github.acodervic.mod.swing.table;

import github.acodervic.mod.data.Opt;

public class TipInfo<T> {
    Opt<T> rowObject = new Opt<>();
    int hoverRowIndex = -1;
    int hoverColumnndex = -1;

    /**
     * @param rowObject
     * @param hoverRowIndex
     * @param hoverColumnndex
     */
    public TipInfo(T rowObject, int hoverRowIndex, int hoverColumnndex) {
        this.rowObject.of(rowObject);
        this.hoverRowIndex = hoverRowIndex;
        this.hoverColumnndex = hoverColumnndex;
    }

    /**
     * @return the rowObject
     */
    public Opt<T> getRowObject() {
        return rowObject;
    }

    /**
     * @param rowObject the rowObject to set
     */
    public void setRowObject(Opt<T> rowObject) {
        this.rowObject = rowObject;
    }

    /**
     * @return the hoverRowIndex
     */
    public int getHoverRowIndex() {
        return hoverRowIndex;
    }

    /**
     * @param hoverRowIndex the hoverRowIndex to set
     */
    public void setHoverRowIndex(int hoverRowIndex) {
        this.hoverRowIndex = hoverRowIndex;
    }

    /**
     * @return the hoverColumnndex
     */
    public int getHoverColumnndex() {
        return hoverColumnndex;
    }

    /**
     * @param hoverColumnndex the hoverColumnndex to set
     */
    public void setHoverColumnndex(int hoverColumnndex) {
        this.hoverColumnndex = hoverColumnndex;
    }

}
