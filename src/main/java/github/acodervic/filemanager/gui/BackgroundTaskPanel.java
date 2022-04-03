package github.acodervic.filemanager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import github.acodervic.filemanager.thread.BackgroundTask;
import github.acodervic.filemanager.thread.BackgroundTaskManager;
import github.acodervic.filemanager.util.GuiUtil;
import net.miginfocom.swing.MigLayout;

public class BackgroundTaskPanel extends JPanel implements GuiUtil {

    BackgroundTaskManager backgroundTaskManager;
    JLabel statusLable=new JLabel();
    JPanel resumeOrPasueButtonPanel=new JPanel();
    JButton pauseButton = newIconToggleButton(Icons.getIconByName(Icons.pause));
    JButton resumeButton = newIconToggleButton(Icons.getIconByName(Icons.resume));
    JButton stopButton = newIconToggleButton(Icons.getIconByName(Icons.stop));

    public BackgroundTaskPanel(BackgroundTaskManager backgroundTaskManager, BackgroundTask backgroundTask) {
        this.backgroundTaskManager = backgroundTaskManager;
        JPanel centerPanel = new JPanel(new MigLayout());
        JPanel taskDIsplayPanel = backgroundTask.getTaskDIsplayPanel();
        resumeOrPasueButtonPanel.add(pauseButton);
        // 添加停止按钮
        centerPanel.add(resumeOrPasueButtonPanel, "width 5%");
        centerPanel.add(stopButton, "width 5%");
        centerPanel.add(statusLable,"width 5%");
        statusLable.setForeground(Color.green);

        if (isNull(taskDIsplayPanel)) {
            taskDIsplayPanel = new JPanel(new MigLayout());
            taskDIsplayPanel.add(new JLabel(backgroundTask.getName()));
        }

        setLayout(new BorderLayout());
        centerPanel.add(taskDIsplayPanel, "width 75%");
        add(centerPanel, BorderLayout.CENTER);
        onClick(stopButton, e -> {
            backgroundTask.onStop();
        });
        onClick(pauseButton, e -> {
            resumeOrPasueButtonPanel.remove( pauseButton);
            resumeOrPasueButtonPanel.add(resumeButton);
            resumeOrPasueButtonPanel.updateUI();
            backgroundTask.onPause();
        });
        onClick(resumeButton, e -> {
            resumeOrPasueButtonPanel.remove(  resumeButton);
            resumeOrPasueButtonPanel.add(pauseButton);
            resumeOrPasueButtonPanel.updateUI();
            backgroundTask.onResum();
        });

    }

    /**
     * @return the pauseButton
     */
    public JButton getPauseButton() {
        return pauseButton;
    }

    /**
     * @return the stopButton
     */
    public JButton getStopButton() {
        return stopButton;
    }

 

    /**
     * @return the resumeButton
     */
    public JButton getResumeButton() {
        return resumeButton;
    }

    /**
     * @return the statusLable
     */
    public JLabel getStatusLable() {
        return statusLable;
    }
}
