package github.acodervic.filemanager.gui.popmenus;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.tree.TreePath;

import github.acodervic.filemanager.gui.FileTableTab;
import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.thread.BackgroundTask;
import github.acodervic.filemanager.treetable.FileSystemModel;
import github.acodervic.filemanager.treetable.JTreeTable;
import github.acodervic.filemanager.treetable.TreeTableModelAdapter;

public class PutInTrash extends MyPopMenuItem {

    @Override

    public List<MyPopMenuItem> getSubItems() {
        return null;
    }

    @Override
    public String getName() {
        return "Put file/dir to trash";
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName(Icons.putTrash);
    }

    @Override
    public String getTip() {
        return "Send to Trash";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        FileTableTab fileTableTab = mainPanel.getCenterTabsPanel().getNowSeletedTabPanel().get();
        JTreeTable treeTable = fileTableTab.getNowShowingTable();
        mainPanel.getMainFrame().getBackgroundTaskManager().execTask(new BackgroundTask() {
            List<RESWallper> deleted = new ArrayList<>();

            @Override
            public int getProgress() {
                return calc(deleted.size(), seletedResWallpers.size());
            }

            @Override
            public String getName() {
                return getTip();
            }

            @Override
            public void action() {
                for (int i = 0; i < seletedResWallpers.size(); i++) {
                    RESWallper resWallper = seletedResWallpers.get(i);
                    if (resWallper.exists()) {
                        resWallper.putToTrash();
                        deleted.add(resWallper);
                    } else {
                        logInfo("错误:" + resWallper.getAbsolutePath() + "不存在!无法放入垃圾箱");
                    }
                }
            }

            @Override
            public JPanel getTaskDIsplayPanel() {
                return null;
            }

            @Override
            public void onStop() {
                
            }

            @Override
            public void onPause() {
                
            }

            @Override
            public void onResum() {
                // TODO Auto-generated method stub
                
            }

        });

    }

    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        if (seletedResWallpers.size() == 0) {
            return false;
        }
        return true;
    }

}
