package github.acodervic.filemanager.gui.popmenus;

import java.util.List;

import javax.swing.Icon;
import javax.swing.SwingUtilities;

import github.acodervic.filemanager.FSUtil;
import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainLeftPanel;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;

public class AddBookMark extends MyPopMenuItem {

    @Override
    public List<MyPopMenuItem> getSubItems() {
        return  null;
    }

    @Override
    public String getName() {
        return "Add To BookMark";
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName("bookmark");
    }

    @Override
    public String getTip() {
        return "add dir to bookMark";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        seletedResWallpers.removeIf(res  ->{
            return res.isFile(); 
         });
         if (seletedResWallpers.size()==0) {
             return ;
         }
         //添加到书签
         seletedResWallpers.forEach(dir  ->{
             if (dir.isDir()) {
                FSUtil.AddBookMark(dir);
             }
         });

         //刷新所有书签树
         MainLeftPanel leftPanel = mainPanel.getMainFrame().getMainPanel().getLeftPanel();
        leftPanel.randerBookMarksTree();
         SwingUtilities.invokeLater(()  ->{
            leftPanel.reloadTree();
        });
    }

    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        seletedResWallpers.removeIf(res  ->{
           return res.isFile(); 
        });
        return seletedResWallpers.size()>0;
    }

}
