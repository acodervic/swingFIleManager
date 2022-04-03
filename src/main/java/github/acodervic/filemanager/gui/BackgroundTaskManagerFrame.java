package github.acodervic.filemanager.gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import github.acodervic.filemanager.thread.BackgroundTask;
import github.acodervic.filemanager.thread.BackgroundTaskManager;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.swing.panel.MyJScrollPane;
import net.miginfocom.swing.MigLayout;

public class BackgroundTaskManagerFrame extends JFrame implements GuiUtil {

    BackgroundTaskManager backgroundTaskManager;
    JPanel mainPanel = new JPanel(new MigLayout());
    MyJScrollPane scrollPane = new MyJScrollPane(mainPanel);
    Map<BackgroundTask, BackgroundTaskPanel> backgroundTaskPanelMap = new HashMap<>();

    Thread updateUiThread;

    public BackgroundTaskManagerFrame(BackgroundTaskManager backgroundTaskManager) {
        this.backgroundTaskManager = backgroundTaskManager;
        this.backgroundTaskManager.addOnBackGroundAddedFuns(newTask -> {
            BackgroundTaskPanel randerPanelByTask = randerPanelByTask(newTask);
            mainPanel.add(randerPanelByTask, "wrap,width 300");
            backgroundTaskPanelMap.put(newTask, randerPanelByTask);
            getRootPane().updateUI();
        });
        intGui();
    }

    public void intGui() {
        setLayout(new MigLayout());
        add(new JLabel("任务信息"), "wrap");
        add(scrollPane, "width 100%,height 90%");
        updateUiThread = new Thread(() -> {
            while (true) {
                try {
                    sleep(1000);
                    List<BackgroundTask> needDeleteTask = new ArrayList<>();
                    backgroundTaskPanelMap.keySet().forEach(task -> {
                        BackgroundTaskPanel backgroundTaskPanel = backgroundTaskPanelMap.get(task);
                        if (task.isFinish()) {
                            needDeleteTask.add(task);
                            // 禁用按钮
                            backgroundTaskPanel.getPauseButton().setEnabled(false);
                            backgroundTaskPanel.getStopButton().setEnabled(false);
                            backgroundTaskPanel.getResumeButton().setEnabled(false);
                        } else if (task.isRunning()) {
                            // 启用按钮
                            backgroundTaskPanel.getPauseButton().setEnabled(true);
                            backgroundTaskPanel.getStopButton().setEnabled(true);
                        } else if (task.isRunning()) {
                            // 启用按钮
                            backgroundTaskPanel.getPauseButton().setEnabled(false);
                            backgroundTaskPanel.getStopButton().setEnabled(true);
                            backgroundTaskPanel.getResumeButton().setEnabled(false);
                        }
                        backgroundTaskPanel.getStatusLable().setText(  task.getStatuString());
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        updateUiThread.start();
    }

    private BackgroundTaskPanel randerPanelByTask(BackgroundTask backgroundTask) {
        return new BackgroundTaskPanel(backgroundTaskManager, backgroundTask);
    }

}
