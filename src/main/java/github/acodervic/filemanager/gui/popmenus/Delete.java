package github.acodervic.filemanager.gui.popmenus;

import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JPanel;

import github.acodervic.filemanager.gui.FileTableTab;
import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.thread.BackgroundTask;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.swing.MessageBox;

public class Delete extends MyPopMenuItem {

    @Override

    public List<MyPopMenuItem> getSubItems() {
        return null;
    }

    @Override
    public String getName() {
        return "Delete";
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName(Icons.delete);
    }

    @Override
    public String getTip() {
        return "delete file or dir";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        mainPanel.getMainFrame().getBackgroundTaskManager().execTask(new BackgroundTask() {

            List<RESWallper> deleted=new ArrayList<>();
            @Override
            public int getProgress() {
                return calc(deleted.size(), seletedResWallpers.size());
            }

            @Override
            public String getName() {
                 return "删除"+seletedResWallpers.size()+"个目标";
            }

            @Override
            public void action() {
                for (int i = 0; i < seletedResWallpers.size(); i++) {
                    RESWallper resWallper = seletedResWallpers.get(i);
                    if (resWallper.exists()) {
                        Opt<Boolean> sucess = resWallper.delete();
                        if (sucess.get()) {
                        } else {
                            MessageBox.showConfirmErrorDialog(null, "删除失败",
                                    "删除失败" + resWallper.getAbsolutePath() + "   Exception=" + sucess.getException());
                        }
                        deleted.add(resWallper);
                    } else {
                        logInfo("错误:" + resWallper.getAbsolutePath() + "不存在!无法放入垃圾箱");
                    }
                }

                if (deleted.size() > 0) {
                    deleted.get(0).getTable().get().updateTable();
                }
            }

            @Override
            public JPanel getTaskDIsplayPanel() {
                 return null;
            }

            @Override
            public void onStop() {
                // TODO Auto-generated method stub
                
            }

            @Override
            public void onPause() {
                // TODO Auto-generated method stub
                
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
