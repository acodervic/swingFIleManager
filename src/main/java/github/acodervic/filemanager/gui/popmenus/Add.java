package github.acodervic.filemanager.gui.popmenus;

import java.util.List;
import java.util.function.Consumer;

import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

import org.apache.commons.vfs2.FileObject;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.gui.MainPanel;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.thread.PublicThreadPool;
import github.acodervic.filemanager.treetable.JTreeTable;
import github.acodervic.mod.data.Value;
import github.acodervic.mod.data.str;
import github.acodervic.mod.swing.MessageBox;
import github.acodervic.mod.thread.TimePool;

public class Add extends MyPopMenuItem {

    @Override
    public List<MyPopMenuItem> getSubItems() {
        return newList(new MyPopMenuItem() {

            @Override
            public List<MyPopMenuItem> getSubItems() {
                return null;
            }

            @Override
            public String getName() {
                return "Create File on Root";
            }

            @Override
            public Icon getIcon() {
                return Icons.getIconByName(Icons.refreshIconName);
            }

            @Override
            public String getTip() {
                return null;
            }

            @Override
            public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                str name = str(MessageBox.showInputDialog(this, "输入", "输入文件名", null, -1));
                if (name.notEmpty()) {
                    try {
                        String nameString = name.to_s();
                        FileObject resolveFile = nowDir.getFileObj().resolveFile(nameString);
                        resolveFile.createFile();
                        if (resolveFile.exists()) {
                            Value val = new Value();
                            Consumer<RESWallper> action = res -> {
                                if (res.getName().equals(nameString)) {
                                    TimePool.getStaticTimePool().setTimeOut(300, () -> {
                                        SwingUtilities.invokeLater(() -> {
                                            // 选中这个文件
                                            JTreeTable jTreeTable = (JTreeTable) nowDir.getTable().get();
                                            jTreeTable.setSelectedResWallpers(newList(res));
                                            jTreeTable.scrollToRes(res);
                                        });
                                    });
                                    //删除这个监听器
                                    nowDir.removeOnNewChildAddedListener(val.get(Consumer.class));
                                }
                            };
                            val.setValue(action);
                            
                            nowDir.addOnNewChildAddedListener(action);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

        }, new MyPopMenuItem() {

            @Override
            public List<MyPopMenuItem> getSubItems() {
                return null;
            }

            @Override
            public String getName() {
                return "Create Dir on Root";
            }

            @Override
            public Icon getIcon() {
                return Icons.getIconByName(Icons.refreshIconName);
            }

            @Override
            public String getTip() {
                return null;
            }

            @Override
            public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
                str name = str(MessageBox.showInputDialog(this, "输入", "输入目录名", null, -1));
                if (name.notEmpty()) {
                    try {
                        nowDir.getFileObj().resolveFile(name.toString()).createFolder();
                    } catch (Exception e) {
                        MessageBox.showErrorMessageDialog(mainPanel, "创建文件夹错误", e.getMessage());
                    }

                }
            }

        });
    }

    @Override
    public String getName() {
        return "AddFile";
    }

    @Override
    public Icon getIcon() {
        return Icons.getIconByName(Icons.add);
    }

    @Override
    public String getTip() {
        return "add file/Dirs";
    }

    @Override
    public void action(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {

    }

    @Override
    public Boolean needDisplay(List<RESWallper> seletedResWallpers, RESWallper nowDir, MainPanel mainPanel) {
        if (nowDir.isWritable()) {
            return true;
        }
        return false;
    }

}
