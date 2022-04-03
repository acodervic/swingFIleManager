package github.acodervic.filemanager.gui.popmenus;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;

import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.oxbow.swingbits.dialog.task.TaskDialogs;

import github.acodervic.filemanager.gui.FileTableTab;
import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.treetable.JTreeTable;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;

public class OpenContainingFolder extends MyPopMenuItem {

    @Override
    public String getName() {
         return "Open Containing Folder";
    }

    @Override
    public Icon getIcon() {
         return Icons.getIconByName(Icons.tab);
    }

    @Override
    public String getTip() {
         return null;
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        openNewTab(seletedResWallpers, mainPanel,true);
    }

    public  static void  openNewTab(List<RESWallper> seletedResWallpers, MainPanel mainPanel,boolean selected) {
        for (int i = 0; i < seletedResWallpers.size(); i++) {
            RESWallper resWallper = seletedResWallpers.get(i);
            FileObject targetDir=null;
            if (resWallper.isDir()) {
                targetDir=resWallper.getFileObj();
            }else{
                try {
                    targetDir = resWallper.getFileObj().getParent();
                } catch (FileSystemException e) {
                    e.printStackTrace();
                }

            }
            if (targetDir!=null) {
                FileTableTab newTab;
                try {
                    newTab = mainPanel.getCenterTabsPanel().addFilesTableTab(new RESWallper(targetDir), selected);
                    JTreeTable nowShowingTable = newTab.getNowShowingTable();
                    ArrayList<RESWallper> arrayList = new ArrayList<>();
                    arrayList.add(resWallper);
                    nowShowingTable.setSelectedResWallpers(arrayList );
                    nowShowingTable.scrollToRes(resWallper);
                } catch (FileSystemException e) {
                    e.printStackTrace();
                    TaskDialogs.showException(e);
                }

                }
            }
 
    }
    
    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        if (seletedResWallpers.size() == 0) {
            return false;
        }
        if (((JTreeTable)nowDir.getTable().get()).inSearchModel()) {
            return true;
        }
        seletedResWallpers.removeIf(res -> {
            return !res.isDir();
        });
        return false;
    }

}
