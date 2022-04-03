package github.acodervic.filemanager.model;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class History {
    RESWallper res;
    List<RESWallper> selectedRse=new ArrayList<>();
    Rectangle rectangle;

 

    /**
     * @return the rectangle
     */
    public Rectangle getRectangle() {
        return rectangle;
    }
    /**
     * @param rectangle the rectangle to set
     */
    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    /**
     * @param res
     * @param selectedRse
     */
    public History(RESWallper res, List<RESWallper> selectedRse) {
        this.res = res;
        this.selectedRse = selectedRse;
    }
    /**
     * @return the res
     */
    public RESWallper getRes() {
        return res;
    }
    /**
     * @param res the res to set
     */
    public void setRes(RESWallper res) {
        this.res = res;
    }
    /**
     * @return the selectedRse
     */
    public List<RESWallper> getSelectedRse() {
        return selectedRse;
    }
    /**
     * @param selectedRse the selectedRse to set
     */
    public void setSelectedRse(List<RESWallper> selectedRse) {
        this.selectedRse = selectedRse;
    }
    
}
