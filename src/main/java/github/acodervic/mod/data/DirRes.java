package github.acodervic.mod.data;

import static github.acodervic.mod.data.BaseUtil.nullCheck;
import static github.acodervic.mod.utilFun.str;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent.Kind;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

import github.acodervic.mod.data.list.ListUtil;
import github.acodervic.mod.io.FileUtil;
import github.acodervic.mod.io.IoType;
import github.acodervic.mod.shell.SystemUtil;

/**
 * DirRes
 */
public class DirRes {
     Opt<File> dir = new Opt<File>();

     /**
      * @param dir
      */
      public DirRes(File dir) {
          nullCheck(dir);
          this.dir.of(dir);
     }

     @Override
     public String toString() {
          return getAbsolutePath();
     }

     /**
      * 是否有文件
      *
      * @return
      */
     public boolean hasFile() {
          if (getFiles().size() > 0) {
               return true;
          }
          return false;
     }

     /**
      * 是否有文件夹
      *
      * @return
      */
     public boolean hasDir() {
          if (getDirs().size() > 0) {
               return true;
          }
          return false;
     }

     /**
      * @param dir
      */
     public DirRes(String dirpath) {
          nullCheck(dirpath);
          // 判断路径是否为绝对路径
          if (str(dirpath).trimLeft().indexOf("/") != 0 && str(dirpath).trimLeft().indexOf(":") != 1) {
               // 说明不是绝对路径
               // 拼接绝对路径
               this.dir.of(new File(SystemUtil.getMyDir() + SystemUtil.getSystemSeparator() + dirpath));
          } else {
               this.dir.of(new File(dirpath));
          }
     }

     /**
      * 读取当前dir的file对象
      *
      * @return the dir
      */
     public File getDir() {
          return dir.get();
     }

     /**
      * @param dir the dir to set
      */
     public void setDir(File dir) {
          nullCheck(dir);
          this.dir.of(dir);
     }

     /**
      * 判断文件夹是否存在
      *
      * @return
      */
     public boolean exists() {
          return getDir().exists();
     }

     /**
      * 判断文件夹是否存在
      *
      * @return
      */
     public boolean notExists() {
          return !exists();
     }

     // 权限相关===========================
     public boolean canExecute() {
          return this.getDir().canExecute();
     }

     public boolean canRead() {
          return this.getDir().canRead();
     }

     public boolean canWrite() {
          return this.getDir().canWrite();
     }

     /**
      * 设置文件是否可执行
      *
      * @param canExecute
      * @return
      */
     public boolean setExecutable(boolean canExecute) {
          return this.getDir().setExecutable(canExecute);
     }

     /**
      * 设置文件是否可读
      * 
      * @param canRead
      * @return
      */
     public boolean setReadable(boolean canRead) {
          return this.getDir().setReadable(canRead);
     }

     /**
      * 设置文件是否可写
      * 
      * @param canWrite
      * @return
      */
     public boolean setWritable(boolean canWrite) {
          return this.getDir().setWritable(canWrite);
     }

     public boolean setWritable(boolean canWrite, boolean onlyMeCanWrite) {
          return this.getDir().setWritable(canWrite, onlyMeCanWrite);
     }

     public boolean setOnlyMeCanWrite(boolean onlyMeCanWrite) {
          return this.getDir().setWritable(canWrite(), onlyMeCanWrite);
     }

     /**
      * 删除文件
      * 
      * @return
      */
     public boolean delete() {
          return getDir().delete();
     }

     /**
      * 在jvm推出的时候删除
      * 
      * @return
      */
     public void deleteOnExit() {
          this.getDir().deleteOnExit();
     }

     /**
      * 清除目录下的所有子文件和目录
      * 
      * @return
      */
     public int deleteAllFiles() {
          int count = 0;
          File[] listFiles = this.getDir().listFiles();
          for (File file : listFiles) {
               try {
                    if (file.delete()) {
                         count += 1;
                    }
               } catch (Exception e) {
                    e.printStackTrace();
               }
          }
          return count;
     }

     /**
      * 是否为隐藏文件夹爱
      * 
      * @return
      */
     public boolean isHidden() {
          return this.getDir().isHidden();
     }

     /**
      * 返回文件的绝对路径
      * 
      * @return
      */
     public String getAbsolutePath() {
          String absolutePath = this.getDir().getAbsolutePath();
          if (absolutePath.equals("/")) {
               return absolutePath;
          }
          return  absolutePath+ "/";
     }

     /**
      * 获取文件后缀名 当没有文件扩展名的时候返回空字符串
      * 
      * @return 返回扩展名
      */
     public String getDirExtensionName() {
          return FileUtil.getExtensionName(getDir());
     }

     /**
      * 获取文件名,不包含后缀
      * 
      * @return 文件名,不包含后缀
      */
     public String getDirBaseName() {
          return FileUtil.getBaseName(getDir());
     }

     /**
      * 读取父层目录路径,如果已经是顶层目录则返回null
      *
      * @return
      */
     public Opt<String> getParentDirPath() {
          Opt<String> dirString=new Opt<>();
          File parentFile = getDir().getParentFile();
          if (parentFile!=null) {
               dirString.of(parentFile.getAbsolutePath());
          }
          return dirString;
     }

     /**
      * 读取父层目录路径对象,如果当前已经是顶层目录则返回null对象
      *
      * @return
      */
     public Opt<DirRes> getParentDir() {
          Opt<DirRes> ret=new Opt<>();
          Opt<String>  parentPath = getParentDirPath();
          if (parentPath.notNull_()) {
               ret.of( new DirRes(parentPath.get()));
          }
          return ret;
     }

     /**
      * 读取父层目录的所有路径对象,如/home/w/Downloads返回/home/w,/home,/
      *
      * @return
      */
     public List<DirRes> getParentDirs() {
          List<DirRes> list = new LinkedList<DirRes>();
          DirRes parentDirRes = getParentDir().get();
          if (parentDirRes == null) {
               return list;
          }
          list.add(parentDirRes);
          for (int i = 0; i < getDirDepthCount(); i++) {
               parentDirRes = parentDirRes.getParentDir().get();
               if (parentDirRes != null) {
                    list.add(parentDirRes);
               }
          }
          return list;
     }

     /**
      * 读取当前目录的深度 /1/2/3或/1/2/3/ 则返回3
      * 
      * @return
      */
     public int getDirDepthCount() {
          DirRes dirRes = new DirRes(this.getAbsolutePath());
          int a = 0;
          while ((dirRes = dirRes.getParentDir().get()) != null) {
               a += 1;
          }
          return a;
     }

     /**
      * 读取文件/夹所在的目录
      * 
      * @return 文件所在的目录
      */
     public String getDirFullPath() {
          return FileUtil.getFullPath(getDir());
     }

     /**
      * 从路径中读取目录名
      * 
      * @return 目录名
      */
     public String getDirName() {
          return getDir().getName();
     }

 

     /**
      * 判断文件是否匹配后缀,如果匹配成功则返回true
      * 
      * @param extension 扩展名
      * @return 成功则返回true
      */
     public boolean isExtension(String extension) {
          nullCheck(extension);
          return FileUtil.isExtension(getDir(), extension);
     }

     /**
      * 复制磁盘文件到目标磁盘文件
      * 
      * @param destFile
      * @return 成功true
      */
     public boolean copyFileTo(File destFile) {
          nullCheck(destFile);
          return FileUtil.copyFileTo(getDir(), destFile);
     }

     /**
      * 在磁盘复制磁盘文件到某个目录
      * 
      * @param destDir 文件目录
      * @return true成功false失败
      */
     public boolean copyFileToDirectory(File destDir) {
          nullCheck(destDir);
          return FileUtil.copyFileToDirectory(getDir(), destDir);
     }

     /**
      * 在磁盘删除磁盘文件/夹
      * 
      * @param srcFileOrDir 文件
      * @return true成功false失败
      */
     public boolean forceDelete() {
          return FileUtil.deleteFileOrDir(getDir());
     }

     /**
      * 在磁盘移动文件或者目录,移动前后文件完全一样,如果目标文件夹不存在则创建。
      * 
      * @param distDir 目标文件夹
      * @return true成功false失败
      */
     public boolean move(DirRes distDir) {
          nullCheck(distDir);
          try {
               return FileUtil.moveFileOrDir(getDir(), distDir.getDir());
          } catch (Exception e) {
               e.printStackTrace();
               return false;
          }
     }

     /**
      * 使用目录创建一个新的文件对象(生成的新文件对象在磁盘中不存在!)
      * 
      * @param filename 文件名
      * @return
      */
     public FileRes newFile(String filename) {
          nullCheck(filename);
          return new FileRes(getDir().getAbsolutePath() + SystemUtil.getSystemSeparator() + filename);
     }

     /**
      * 使用目录创建一个新的文件夹对象(生成的新文件夹对象在磁盘中不存在!)
      * 
      * @param filename 文件名
      * @return
      */
     public DirRes newDir(String filename) {
          nullCheck(filename);
          return new DirRes(getDir().getAbsolutePath() + SystemUtil.getSystemSeparator() + filename);
     }

     /*
      * 在磁盘创建空文件夹,如果文件夹已经存在则返回false,如果不存在则创建文件夹,成功返回true不成功则返回false
      * 
      * @param dir 目录
      * 
      * @return true成功false失败
      */
     public DirRes makeDir() {
          if (FileUtil.createDir(getDir())) {
               return this;
          }
          return new DirRes(getDir());
     }
 
     /**
      * 创建一个文件,Override参数决定是否进行覆盖,如果文件真实被创建成功则返回true,其它情况一律返回false
      * 在写入之前确保文件的中父文件夹是存在的,否则无法创建文件并返回false
      * 
      * @param Override 是否覆盖磁盘已有文件
      * @return
      */
     public boolean mkFile(String filename, boolean override) {
          nullCheck(filename);
          return FileUtil.createFile(newFile(filename).getFile(), override);
     }

     /**
      * 重命名文件,返回新的文件,如果命名失败则返回null
      * 
      * \ * @param newName 新名字
      * 
      * @return
      * @return 操作成功标识
      */
     public FileRes rename(String newName) {
          nullCheck(newName);
          if (FileUtil.renameFileOrDir(getDir(), newName)) {
               return new FileRes(getDir() + SystemUtil.getSystemSeparator() + newName);
          }
          return null;
     }

     /**
      * 获得某个文件夹下面的文件夹或者文件
      * 
      * @param outDirAndFiles   输出参数,使用完毕之后可以直接遍历传入的outPutFiles \
      * @param setDeep          设置发现深度
      * @param deep             起始深度值,必须为-1!
      * @param pattern          匹配文件或文件夹的的正则表达式,*代表所有 例如,匹配后缀
      *                         ([^\\s]+(\\.(?i)(jpg|png))$),
      * @param onlyMathFilename 正则表达式是否只匹配文件/夹名称(包含后缀)默认匹配文件的完整限定名 ,当匹配正则为 *
      *                         的时候,这个参数true和false都不会影响到程序的逻辑
      * @param ioType           设置想要得到的io类型.可选的值FILE文件,DIR文件夹,,FILE_AND_DIR文件夹和文件
      */
     public void getDisrAndFiles(List<File> outDirAndFiles, int setDeep, int deep, String pattern,
               boolean onlyMathFilename, IoType ioType) {
          nullCheck(outDirAndFiles, pattern, onlyMathFilename, ioType);
          getDisrAndFiles(outDirAndFiles, setDeep, deep, pattern, onlyMathFilename, ioType,-1);
     }

     /**
      * 获得某个文件夹下面的文件夹或者文件
      * 
      * @param outDirAndFiles   输出参数,使用完毕之后可以直接遍历传入的outPutFiles \
      * @param setDeep          设置发现深度
      * @param deep             起始深度值,必须为-1!
      * @param pattern          匹配文件或文件夹的的正则表达式,*代表所有 例如,匹配后缀
      *                         ([^\\s]+(\\.(?i)(jpg|png))$),
      * @param onlyMathFilename 正则表达式是否只匹配文件/夹名称(包含后缀)默认匹配文件的完整限定名 ,当匹配正则为 *
      *                         的时候,这个参数true和false都不会影响到程序的逻辑
      * @param ioType           设置想要得到的io类型.可选的值FILE文件,DIR文件夹,,FILE_AND_DIR文件夹和文件
      * @param maxResultCount   最大的就结果数量
      * 
      */
     public void getDisrAndFiles(List<File> outDirAndFiles, int setDeep, int deep, String pattern,
               boolean onlyMathFilename, IoType ioType, Integer maxResultCount) {
          nullCheck(outDirAndFiles, pattern, onlyMathFilename, ioType);
          FileUtil.getAllDirAndFileByDir(outDirAndFiles, getDir(), setDeep, deep, pattern, onlyMathFilename, ioType,
                    maxResultCount);
     }

     /**
      * 从目录中读取文件夹和文件,输出一个list集合,如果类型是fileres则是文件dirres则是文件夹自己进行实例判断,注意只能处理一层!
      *
      * @param fileFilter 过滤器
      * @return
      */
     public List<Object> getDirsAndFiles(FileFilter fileFilter) {
          nullCheck(fileFilter);
          List<File> filelist = ArrayUtil.toList(getDir().listFiles(fileFilter));
          return ListUtil.cast(filelist, new Function<File, Object>() {
               @Override
               public Object apply(File t) {
                    if (t.isFile()) {
                         return new FileRes(t);
                    } else {
                         return new DirRes(t);
                    }
               }
          });
     }

     /**
      * 从目录中读取文件,输出一个list集合,,注意次函数只能单层读取
      *
      * @param fileFilter_opt 过滤器,null为不过滤
      * @return
      */
     public List<FileRes> getFiles(FileFilter fileFilter_opt) {
          List<File> filelist = ArrayUtil.toList(getDir().listFiles(fileFilter_opt));
          return ListUtil.cast(filelist, new Function<File, FileRes>() {
               @Override
               public FileRes apply(File t) {
                    if (t.isFile()) {
                         return new FileRes(t);
                    }
                    return null;
               }
          });
     }

     /**
      * 从目录中读取文件,输出一个list集合,,注意次函数只能单层读取
      *
      * @return
      */
     public List<FileRes> getFiles() {
          return getFiles(null);
     }

     /**
      * 从目录中读取文件夹,输出一个list集合,注意次函数只能单层读取
      *
      * @return
      */
     public List<DirRes> getDirs() {
          return getDirs(null);
     }

     /**
      * 从目录中读取文件夹,输出一个list集合,注意次函数只能单层读取
      *
      * @param fileFilter_opt 过滤器 过滤器,null为不过滤
      * @return
      */
     public List<DirRes> getDirs(FileFilter fileFilter_opt) {
          List<File> filelist = ArrayUtil.toList(getDir().listFiles(fileFilter_opt));
          return ListUtil.cast(filelist, new Function<File, DirRes>() {
               @Override
               public DirRes apply(File t) {
                    if (t.isDirectory()) {
                         return new DirRes(t);
                    }
                    return null;
               }
          });
     }


     /**
      * 听目录文件改变(新建一个线程),注意此目录下文件不可过多
      * 
      * @param onChange
      * @param fileAndDirFilterNameRegex_opt 过滤函数
      * @return
      */
     public boolean watchDir(BiConsumer<Kind, File> onChange, String fileAndDirFilterNameRegex_opt) {
          nullCheck(onChange);
          return FileUtil.watch(getDir(), onChange, fileAndDirFilterNameRegex_opt);
     }

     /**
      * 读取path对象
      * 
      * @return
      */
     public Path toPath() {
          return getDir().toPath();
     }

     /**
      * 判断是目录否有某一个文件
      * 
      * @param filename_opt 若为null则返回false
      * @return
      */
     public boolean hasFile(String filename_opt) {
          if (filename_opt == null) {
               return false;
          }
          if (exists()) {
               if (newFile(filename_opt).exists() && newFile(filename_opt).getFile().isFile()) {
                    return true;
               }
          }
          return false;
     }

     /**
      * 判断是目录否有某一个文件
      * 
      * @param filename_opt 若为null则返回false
      * @return
      */
     public boolean hasDir(String dirname_opt) {
          if (dirname_opt == null) {
               return false;
          }
          if (exists()) {
               if (newDir(dirname_opt).exists() && newDir(dirname_opt).getDir().isDirectory()) {
                    return true;
               }
          }
          return false;
     }

     /**
      * 获取文件,文件可能不存在
      * @param filename
      * @return
      */
     public FileRes getFIle(String filename) {
          return newFile(filename);
     }
}