package github.acodervic.filemanager.gui;

import java.awt.*;
import javax.swing.*;

public class CircularProgressTest {
  public JComponent makeUI() {
    JProgressBar progress = new JProgressBar();
    // use JProgressBar#setUI(...) method
    progress.setUI(new ProgressCircleUI());
    progress.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    progress.setStringPainted(true);
    progress.setFont(progress.getFont().deriveFont(24f));
    progress.setForeground(Color.ORANGE);

    (new Timer(50, e -> {
      int iv = Math.min(100, progress.getValue() + 1);
      progress.setValue(iv);
    })).start();

    JPanel p = new JPanel();
    p.add(progress);
    return p;
  }
  public static void main(String... args) {
    EventQueue.invokeLater(() -> {
      JFrame f = new JFrame();
      f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      f.getContentPane().add(new CircularProgressTest().makeUI());
      f.setSize(30, 30);
      f.setLocationRelativeTo(null);
      f.setVisible(true);
    });
  }
}