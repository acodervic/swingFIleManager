package github.acodervic.mod.swing.tree.filter;

import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import github.acodervic.mod.data.str;

/**
 * 悬浮可以显示tip的Jpanel
 */
public class TipJPanel extends JPanel {
    private static final long serialVersionUID = -176084650836472139L;
    private static   Border hoverBorder = BorderFactory.createLineBorder(Color.blue);

    public boolean hasChildrenJComponentByName(String name) {
        if (name==null||name.equals("")) {
            return false;
        }
        for (Component com : getComponents()) {
            if (new str(com.getName()).eq(name)) {
                return true;
            }
        }
        return false;
    }
    @Override
    public String getToolTipText(MouseEvent event) {
 
        return null;
    }
 
    public TipJPanel() {
        super(new FlowLayout(FlowLayout.LEFT));
    }
    /**
     * @param layout
     */
    public TipJPanel(LayoutManager layout) {
        super(layout);
    }
 
    @Override
    public    Component add(Component comp) {
        if (comp!=null) {
         return super.add(comp);
        }
        return null;
    }
}
