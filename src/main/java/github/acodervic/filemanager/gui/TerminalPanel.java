package github.acodervic.filemanager.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DebugGraphics;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;

import com.jediterm.pty.PtyProcessTtyConnector;
import com.jediterm.terminal.Terminal;
import com.jediterm.terminal.TerminalColor;
import com.jediterm.terminal.TextStyle;
import com.jediterm.terminal.TtyConnector;
import com.jediterm.terminal.ui.JediTermWidget;
import com.jediterm.terminal.ui.UIUtil;
import com.pty4j.PtyProcess;
import com.pty4j.PtyProcessBuilder;

import github.acodervic.mod.data.Opt;

//TODO: Keep a global StringBuilder to decrease memory footprint

public class TerminalPanel extends JPanel {
    Thread ptyThread;
    Opt<JediTermWidget> terminal=new Opt<>();

    public TerminalPanel() {
        setLayout(new BorderLayout());
        ptyThread = new Thread(() -> {
            CmdSettingsProvider cmdSettingsProvider = new CmdSettingsProvider();
            cmdSettingsProvider.setDefaultStyle(new TextStyle(
                    TerminalColor.rgb(
                            UIManager.getColor("Panel.foreground").getRed(),
                            UIManager.getColor("Panel.foreground").getGreen(),
                            UIManager.getColor("Panel.foreground").getBlue()),
                    TerminalColor.rgb(
                            UIManager.getColor("Table.background").getRed(),
                            UIManager.getColor("Table.background").getGreen(),
                            UIManager.getColor("Table.background").getBlue())));
            JediTermWidget terminalPanel = new JediTermWidget(cmdSettingsProvider);
 
            terminalPanel.setDebugGraphicsOptions( DebugGraphics.NONE_OPTION);
            terminalPanel.setTtyConnector(createTtyConnector());
            terminalPanel.start();
            terminal.of(terminalPanel);
            int debugGraphicsOptions = terminalPanel.getDebugGraphicsOptions();
            add(terminalPanel, BorderLayout.CENTER);
        });
        

        setBorder(BorderFactory.createLineBorder(Color.gray));
    }

    public void open() {
        ptyThread.start();
    }

    public JediTermWidget getTerminal() {
        return terminal.get();
    }

    private static TtyConnector createTtyConnector() {
        try {
            Map<String, String> envs = System.getenv();
            // log.debug(envs.get("Path"));
            String[] command;
            if (UIUtil.isWindows) {
                command = new String[] { "cmd.exe" };
            } else {
                command = new String[] { "/bin/bash" };
                envs = new HashMap<>(System.getenv());
                envs.put("TERM", "xterm-256color");
            }
            PtyProcess process = new PtyProcessBuilder().setDirectory("/home/w").setCommand(command)
                    .setEnvironment(envs).start();

            PtyProcessTtyConnector ptyProcessTtyConnector = new PtyProcessTtyConnector(process, StandardCharsets.UTF_8);
            
            return ptyProcessTtyConnector;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        CmdSettingsProvider cmdSettingsProvider = new CmdSettingsProvider();
        cmdSettingsProvider.setDefaultStyle(new TextStyle(
            TerminalColor.rgb(
                    UIManager.getColor("Panel.foreground").getRed(),
                    UIManager.getColor("Panel.foreground").getGreen(),
                    UIManager.getColor("Panel.foreground").getBlue()),
            TerminalColor.rgb(
                    UIManager.getColor("Table.background").getRed(),
                    UIManager.getColor("Table.background").getGreen(),
                    UIManager.getColor("Table.background").getBlue())));
        JediTermWidget terminalPanel = new JediTermWidget(cmdSettingsProvider);

        terminalPanel.setDebugGraphicsOptions( DebugGraphics.NONE_OPTION);
        terminalPanel.setTtyConnector(createTtyConnector());
        terminalPanel.start();
        int debugGraphicsOptions = terminalPanel.getDebugGraphicsOptions();
        jFrame.add(terminalPanel, BorderLayout.CENTER);
        jFrame.setVisible(true);
    }
}
