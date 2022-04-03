package github.acodervic.filemanager.treetable;

import java.util.function.Function;

import javax.swing.table.DefaultTableCellRenderer;

import github.acodervic.filemanager.model.RESWallper;

/**
 * T是return getRESWallperValueBy的类型
 */
public class Column<T> {
    String name;
    Class classType=String.class;
    DefaultTableCellRenderer rander=new DefaultTableCellRenderer();//默认的列渲染器
    Function<RESWallper,T> getRESWallperValueBy;//通过 RESWallper来拿到列值
    /**
     * @param name
     * @param classType
     */
    public Column(String name, Class classType,Function<RESWallper,T> getRESWallperValueByFUn) {
        this.name = name;
        this.classType = classType;
        this.getRESWallperValueBy=getRESWallperValueByFUn;
    }
    
    
    /**
     * @param name
     * @param classType
     * @param rander
     */
    public Column(String name, Class classType, DefaultTableCellRenderer rander,Function<RESWallper,T> getRESWallperValueByFUn) {
        this.name = name;
        this.classType = classType;
        this.rander = rander;
        this.getRESWallperValueBy=getRESWallperValueByFUn;

    }


    /**
     * @return the rander
     */
    public DefaultTableCellRenderer getRander() {
        return rander;
    }


    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    /**
     * @return the classType
     */
    public Class getClassType() {
        return classType;
    }

    /**
     * @param rander the rander to set
     */
    public void setRander(DefaultTableCellRenderer rander) {
        this.rander = rander;
    }

    public T getValueByRESWallper(RESWallper resWallper) {
        return getRESWallperValueBy.apply(resWallper);
    }
}
