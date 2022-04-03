package github.acodervic.filemanager.device;

import java.util.List;

import github.acodervic.filemanager.model.Device;
import github.acodervic.filemanager.util.GuiUtil;

public interface DeviceMounter extends GuiUtil {

    
    public  List<Device> getAllDevices();


    public void mount(Device device) throws Exception;

    /**
     * 强制卸载
     * @param device
     * @throws Exception
     */
    public void forceUmount(Device device) throws Exception;


    public void unmount(Device device)throws Exception;

    
    
}
