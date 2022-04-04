package github.acodervic.mod.swing.tree.filter;

import java.awt.Component;
import java.awt.Font;
import java.util.List;
import java.util.function.Function;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;

import github.acodervic.mod.UtilFunInter;
import github.acodervic.mod.data.Opt;
import github.acodervic.mod.data.str;
import github.acodervic.mod.swing.tree.FilteredTree;


/**
 * Renders bold any tree nodes who's toString() value starts with the filtered
 * text we are filtering on.
 *
 * @author Oliver.Watkins
 * @version $Revision: $ $Date: $ $Author: $
 */
    public class FiltedTreeRenderer<T> extends DefaultTreeCellRenderer implements UtilFunInter {

        private TipJPanel nodeComponent;

        FilteredTree filteredTree;
        public FiltedTreeRenderer( FilteredTree filteredTree) {
            this.filteredTree=filteredTree;
            this.nodeComponent = new TipJPanel();
            this.nodeComponent.setLayout(new BoxLayout(nodeComponent, BoxLayout.X_AXIS));
            nodeComponent.setOpaque(false);//必须要设置这个,不然panel的背景会显示出来一个黑块
            this.setLabelFor(nodeComponent);
        }

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
                boolean leaf, int row, boolean hasfocus) {
                    nodeComponent.removeAll();
            Opt<Function<TreeCellRendererCommponentArgsWarper, List<Component>>> treeCellRendererCommponentFunction = filteredTree
                    .getTreeCellRendererCommponentFunction();
            List<Component> cs = null;


            //组件渲染
            if (treeCellRendererCommponentFunction.notNull_()) {
                TreeCellRendererCommponentArgsWarper treeCellRendererCommponentArgsWarper = new TreeCellRendererCommponentArgsWarper(
                        tree, value, selected, expanded, leaf, row, hasfocus);
                try {
                    cs=treeCellRendererCommponentFunction.get().apply(treeCellRendererCommponentArgsWarper);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //图标渲染函数
            if (filteredTree.getNodeIconRanderFun()!=null) {
                try {
                    Object apply = filteredTree.getNodeIconRanderFun().apply(value);
                    if (apply!=null&&apply instanceof Icon) {
                        //设置图标
                        setLeafIcon((Icon) apply);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (cs == null) {
                cs = newList(super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasfocus));
                //默认渲染的情况下 调用文本提供函数
                if (value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    if (node.getUserObject() != null && !(node.getUserObject() instanceof String)) {
                        // 默认渲染单个文本lable
                        Opt<Function> treeNodeTextFunction = filteredTree
                                .getTreeNodeTextFunction();
                        if (treeNodeTextFunction.notNull_()) {
                            str str = str(treeNodeTextFunction.get().apply(value));
                            if (str.notEmpty()) {
                                JLabel lable = (JLabel) cs.get(0);
                                lable.setText(str.to_s());
                            }
                        }
                    }
                }
            }
            if (cs.size()==1) {
                Component c = cs.get(0);
                if (c instanceof JLabel) {
                    JLabel lable=(JLabel)c;
                    String filteredText = filteredTree.getFilteredText();
                    if (!filteredText.equals("") && value != null  ) {
                        // 过滤
                        Boolean match = false;
                        match = value.toString().toLowerCase().indexOf(filteredText.toLowerCase()) > 0;
                        if (match) {
                            Font f = lable.getFont();
                            f = new Font("Dialog", Font.BOLD, f.getSize());
                            lable.setFont(f);
                        }
    
                    } else {
                        Font f = lable.getFont();
                        f = new Font("Dialog", Font.PLAIN, f.getSize());
                        c.setFont(f);
                    }
                }
            }
            // 多个组件
            for (int i = 0; i < cs.size(); i++) {
                Component component = cs.get(i);
                nodeComponent.add(component);
            }
            return nodeComponent;
        }
    }