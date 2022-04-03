package github.acodervic.filemanager.treetable;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventObject;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.table.TableCellEditor;

import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.thread.PublicThreadPool;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.str;
import github.acodervic.mod.swing.MessageBox;
import github.acodervic.mod.thread.TimePool;

public class TreeTableCellEditor extends AbstractCellEditor implements TableCellEditor, GuiUtil {
    JTreeTable treeTable;

    public TreeTableCellEditor(JTreeTable treeTable) {
        this.treeTable = treeTable;

        jTextField.setBorder(null);
        jTextField.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {// 回车修改重命名
                    str newName = new str(jTextField.getText());
                    RESWallper resWallper = treeTable.getNowEditingRes().get();
                    if (newName.notEmpty()) {
                        if (!newName.eq(resWallper.getName())) {
                            try {
                                String newNameS = newName.toString();
                                if (resWallper.reName(newNameS)) {
                                    treeTable.stopEditRes();
                                    if (resWallper.isLocalFile()) {
                                        TimePool.getStaticTimePool().setTimeOut(50, () -> {

                                            Opt<RESWallper> childResByName = resWallper.getParentResWallper()
                                                    .getChildResByName(newNameS, resWallper.isFile());
                                            if (childResByName.notNull_()) {
                                                SwingUtilities.invokeLater(() -> {
                                                    treeTable.setSelectedResWallpers(newList(childResByName.get()));
                                                    treeTable.scrollToRes(childResByName.get());
                                                    treeTable.refreshUi();
                                                 });
                                            }
    
                                        });
                                    }else{
                                         //刷新整个文件夹
                                         treeTable.updateTable();
                                    }
                                }else{
                                     MessageBox.showErrorMessageDialog(jTextField, "错误", "修改文件名失败!");
                                     treeTable.startEditRes(resWallper);
                                }
                            } catch (Exception e2) {
                                e2.printStackTrace();
                                MessageBox.showErrorMessageDialog(null, "错误", "重命名失败," + e2.getMessage());
                                treeTable.startEditRes(resWallper);
                            }

                        }
                    } else {
                        MessageBox.showErrorMessageDialog(null, "错误", "空的名称!");
                        treeTable.startEditRes(resWallper);
                    }

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }

        });
    }

    JTextField jTextField = new JTextField();
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int r, int c) {
                String name = value.toString();
                jTextField.setText(name);
                jTextField.setSelectionStart(0);
                jTextField.setSelectionEnd(name.length());
        return jTextField;
    }

}