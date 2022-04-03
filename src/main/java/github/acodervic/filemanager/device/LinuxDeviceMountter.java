package github.acodervic.filemanager.device;

import java.util.ArrayList;
import java.util.List;

import github.acodervic.filemanager.model.Device;
import github.acodervic.mod.data.DirRes;
import github.acodervic.mod.data.FileRes;
import github.acodervic.mod.data.str;
import github.acodervic.mod.data.list.AList;

public class LinuxDeviceMountter implements DeviceMounter {

    List<Device> devices;
    public LinuxDeviceMountter() {
        getAllDevices();
    }
    @Override
    public List<Device> getAllDevices() {
        if (isNull(devices)) {
            devices=new ArrayList<>();
            AList<str>  uuids = str(exec2String("gio mount --list --detail | grep -i '     uuid'")).splitLines();//获取每个驱动的uuid
            AList<str> devicesString = str(exec2String("gio mount --list --detail | grep -i '    unix-devic'")).splitLines();
            if (uuids.size()>0&&uuids.size()==devicesString.size()) {
                for (int i = 0; i < devicesString.size(); i++) {
                    str deviceStr = devicesString.get(i);
                    str trimLeft = deviceStr.trimLeft().trimLeft();
                    trimLeft=trimLeft.subBetween("'", "'");
                    if (trimLeft.notEmpty()&&trimLeft.startWith("/dev")) {
                        FileRes sourceDirRes = new FileRes(trimLeft.to_s());
                        String uuid = uuids.get(i).replaceAll("'", "").trim().replaceAll("uuid", "").replaceAll(":", "").replaceAll(" ", "").to_s();
                        //Device device=new Device(sourceDirRes.getFileName() , sourceDirRes, new DirRes("/media/"+getUser()+"/"+uuid));
                        //devices.add(device);
                    }                    
                }

            }else{
                 logInfo("错误,尝试获取驱动uuid和驱动返回不一致的数量!   uuids="+uuids+"  devices="+devicesString);
            }

        }
         return devices;
    }

    @Override
    public void mount(Device device) {
        /**
         *         if (notNull(device)&&notNull(device.getSourceFIleRes())&&notNull(device.getDistDirRes())) {
            String exec2String = exec2String("gio mount  -d   '"+device.getSourceFIleRes().getAbsolutePath()+"'");
            sleep(1000);
            //判断目标是否存在
            if (device.getDistDirRes().exists()) {
                logInfo("成功挂载device"+device.getSourceFIleRes().getAbsolutePath());
                device.getOnMountFuns().forEach(run  ->{
                        try {
                            run.run();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                });
            }
        }
         */
    }

    @Override
    public void unmount(Device device) {
        /**
         *         if (notNull(device) && notNull(device.getSourceFIleRes()) && notNull(device.getDistDirRes())) {
            String exec2String = exec2String("gio mount  -u  '" + device.getDistDirRes().getAbsolutePath() + "'");
            sleep(1000);
            // 判断目标是否存在
            if (!device.getDistDirRes().exists()) {
                logInfo("成功卸载device" + device.getSourceFIleRes().getAbsolutePath());
                device.getOnUmountFuns().forEach(run  ->{
                    try {
                        run.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            });
            }else{
                 throw new RuntimeException("un mount "+device.getName()+"error!");
            }
        }
         */
    }

    public static void main(String[] args) {
        LinuxDeviceMountter a=new LinuxDeviceMountter();
        List<Device> allDevices = a.getAllDevices();
        for (int i = 0; i < allDevices.size(); i++) {
            a.mount(allDevices.get(i));
        }
        for (int i = 0; i < allDevices.size(); i++) {
            a.unmount(allDevices.get(i));
        }
    }
    @Override
    public void forceUmount(Device device) throws Exception {
        /**
         *        if (notNull(device) && notNull(device.getSourceFIleRes()) && notNull(device.getDistDirRes())) {
            String exec2String = exec2String("gio mount  -u -f  '" + device.getDistDirRes().getAbsolutePath() + "'");
            sleep(1000);
            // 判断目标是否存在
            if (!device.getDistDirRes().exists()) {
                logInfo("成功卸载device" + device.getSourceFIleRes().getAbsolutePath());
                device.getOnUmountFuns().forEach(run  ->{
                    try {
                        run.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
            });
            }else{
                 throw new RuntimeException("un mount "+device.getName()+"error!");
            }
        }
         */
    }
    
}
