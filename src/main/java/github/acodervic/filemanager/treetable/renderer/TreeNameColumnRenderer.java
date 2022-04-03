package github.acodervic.filemanager.treetable.renderer;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JToolTip;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;

import github.acodervic.filemanager.gui.Icons;
import github.acodervic.filemanager.model.RESWallper;
import github.acodervic.filemanager.treetable.JTreeTable;
import github.acodervic.filemanager.treetable.TreeNameColumnPanel;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.swing.tree.filter.TipJPanel;
import net.miginfocom.swing.MigLayout;

/**
 * 渲染表格树列的渲染器
 */
public class TreeNameColumnRenderer extends DefaultTreeCellRenderer implements GuiUtil {
    //TipJPanel panel=new TipJPanel();
    JTreeTable treeTable;
    boolean randered=false;

    public  TreeNameColumnRenderer(JTreeTable treeTable) {
        this.treeTable=treeTable;
 
         //setLabelFor(panel);//显示列的面板

        
    }

    /**
     * 渲染行的函数
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf,
            int row, boolean hasFocus) {
        if (value instanceof RESWallper res) {
            TreeNameColumnPanel treeNameColumnPanel = res.getTreeNameColumnPanel();
            treeNameColumnPanel.setjTreeTable(this.treeTable);
            treeNameColumnPanel.changeExpanded(expanded);
            setLabelFor(treeNameColumnPanel);
            treeNameColumnPanel.repaint();
            Opt<JTreeTable> table = res.getTable();
            if (table.notNull_()) {

                
            }else{
                 table.of(treeTable);
            }
            
            

            //只有在,资源在子级节点被删除的时候才会触发这个删除方法

            /**
             *             if (!res.exists()) {
                //检测到节点自动刷新
                try {
                    treeTable.fireTreeNodesRemoved(newList(res));
                } catch (Exception e) {
                    e.printStackTrace();
                    //如果节点之前已经被删除了是不会报错的,这种是正常情况,否则会删除节点并刷新table
                    //直接从ui删除节点
                    
                }
                
                return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            }
             */
            return treeNameColumnPanel;

        }else{
             return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        }

    }


    
}
