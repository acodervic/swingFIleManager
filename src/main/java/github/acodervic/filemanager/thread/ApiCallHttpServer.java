package github.acodervic.filemanager.thread;

 
import org.oxbow.swingbits.dialog.task.TaskDialogs;

import github.acodervic.filemanager.device.DeviceMounter;
import github.acodervic.filemanager.gui.MainFrame;
import github.acodervic.filemanager.util.GuiUtil;
import github.acodervic.mod.net.server.httpd.HttpServer;

public class ApiCallHttpServer extends HttpServer implements GuiUtil {
    
    BackgroundTaskManager backgroundTaskManager;
    DeviceMounter deviceMounter;
    public ApiCallHttpServer(int port,BackgroundTaskManager backgroundTaskManager,DeviceMounter deviceMounter) {
        super(port);
        this.backgroundTaskManager=backgroundTaskManager;
        this.deviceMounter=deviceMounter;
        addHandler("newWindow", session -> {
            try {
                MainFrame mainFrame = new MainFrame(newLocalFIle("/").get(),backgroundTaskManager,deviceMounter);
                mainFrame.setSize(1200, 800);
                mainFrame.setVisible(true);
            } catch (Exception e) {
                TaskDialogs.showException(e);

            }
            return newFixedLengthResponse("ok");
        });
    }

}
