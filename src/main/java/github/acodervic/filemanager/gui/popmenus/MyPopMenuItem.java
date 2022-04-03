package github.acodervic.filemanager.gui.popmenus;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JMenuItem;

import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.local.LocalFileSystem;

import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.util.GuiUtil;

public abstract class  MyPopMenuItem extends JMenuItem implements GuiUtil {


    public  List<MyPopMenuItem> getSubItems(){
        return null;
    };

    public abstract String getName();

    public abstract Icon getIcon();

    public  abstract String getTip();

    public abstract void action(List<RESWallper> seletedResWallpers,RESWallper nowDir,MainPanel mainPanel ) ;
    
    /**
     * ctrl+x快捷键
     * @return
     */
    public  int  getCtrlAndKeyCpde(){
        return -1;
    };

    public  Boolean needDisplay(List<RESWallper> seletedResWallpers,RESWallper nowDir,MainPanel mainPanel){
        return true;
    };

    /**
     * 代表这个弹出窗口支持哪种文件系统类型
     * @return
     */
    public List<Class> getSupportedFileSystem() {
        return newList(StandardFileSystemManager.class);
    }

}
