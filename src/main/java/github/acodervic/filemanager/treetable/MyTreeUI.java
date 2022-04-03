package github.acodervic.filemanager.treetable;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

import com.formdev.flatlaf.ui.FlatTreeUI;

public class MyTreeUI extends FlatTreeUI {
    public  MyTreeUI() {
        super();
    }
    @Override
    protected void paintVerticalLine(Graphics g, JComponent c, int x, int top, int bottom) {//画节点垂直线
        g.setColor(Color.DARK_GRAY);
        super.paintVerticalLine(g, c, x, top, bottom);
    }
    @Override
    protected void paintHorizontalLine(Graphics g, JComponent c, int y, int left, int right) {
        g.setColor(Color.darkGray);
        super.paintHorizontalLine(g, c, y, left, right);
    }

    @Override
    public  void setRowHeight(int rowHeight) {
        // TODO Auto-generated method stub
        super.setRowHeight(rowHeight);
    }
    
}
