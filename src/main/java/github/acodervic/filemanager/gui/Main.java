package github.acodervic.filemanager.gui;

import java.awt.*;
import javax.swing.*;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        SwingUtilities.invokeLater(() ->
        {

            JFrame frame = new JFrame("Tabs text");
            JTabbedPane tabs = new JTabbedPane()
            {
                @Override
                public Dimension getPreferredSize()
                {
                    int tabsWidth = 0;

                    for (int i = 0; i < getTabCount(); i++) {
                        tabsWidth += getBoundsAt(i).width;
                    }

                    Dimension preferred = super.getPreferredSize();

                    preferred.width = Math.max(preferred.width, tabsWidth);

                    return preferred;
                }
            };

            tabs.addTab("Tab1", new JLabel("Content1"));
            tabs.addTab("Tab2", new JLabel("Content2"));
            tabs.addTab("Tab3", new JLabel("Content3"));
            tabs.addTab("Tab4", new JLabel("Content4"));


            frame.add(tabs);

            frame.pack();
            frame.pack();
            frame.setVisible(true);
        });
    }
}