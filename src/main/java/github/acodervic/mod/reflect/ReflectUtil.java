package github.acodervic.mod.reflect;

import static github.acodervic.mod.data.BaseUtil.nullCheck;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.FieldInfo;
import io.github.classgraph.MethodInfo;
import io.github.classgraph.MethodInfoList;
import io.github.classgraph.ScanResult;

/**
 * 基于反射的工具类
 */
public class ReflectUtil {

    /**
     * 读取某个包下的class
     *
     * @param <T>
     * @param packagePath             包位置字符串如 "java"
     * @param implementsInterfaceType 已知类的 父类 或者 接口
     * @param classFilter_opt
     * @return
     */
    public static <T> List<Class> getClasssByPackageInterface(String packagePath,
            Class<T> implementsInterfaceType) {
        nullCheck(packagePath);
        List<Class> classList = new ArrayList<Class>();
        ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(packagePath).scan();
        ClassInfoList standardClasses = scanResult.getAllClasses().getStandardClasses();
        for (int i = 0; i < standardClasses.size(); i++) {
            ClassInfo classInfo = standardClasses.get(i);
            if (classInfo.implementsInterface(implementsInterfaceType.getName())) {
                classList.add(classInfo.loadClass());
            }
        }

        return classList;
    }

    /**
     * 读取某个包下的class
     *
     * @param <T>
     * @param packagePath     包位置字符串如 "java"
     * @param superClassType  已知类的 父类 或者 接口
     * @param classFilter_opt
     * @return
     */
    public static <T> List<Class> getClasssByPackageSuperClass(String packagePath, Class<T> superClassType) {
        nullCheck(packagePath);
        List<Class> classList = new ArrayList<Class>();
        ScanResult scanResult = new ClassGraph().enableAllInfo().acceptPackages(packagePath).scan();
        ClassInfoList standardClasses = scanResult.getAllClasses().getStandardClasses();
        for (int i = 0; i < standardClasses.size(); i++) {
            ClassInfo classInfo = standardClasses.get(i);
            if (classInfo.extendsSuperclass(superClassType.getName())) {
                classList.add(classInfo.loadClass());
            }
        }

        return classList;
    }

    /**
     * 读取一个Class的所有信息,可以通过ClassInfo继续读取class的 字段 ,注解,接口等
     *
     * @param <T>
     * @param targetClass
     * @return
     */
    public static <T> ClassInfo getClassInfo(Class<T> targetClass) {
        nullCheck(targetClass);
        ScanResult scanResult = new ClassGraph().whitelistClasses(targetClass.getName()).enableAllInfo()
                .scan();
        ClassInfo classInfo = scanResult.getAllClasses().get(0);
        return classInfo;
    }

    public static <T> List<MethodInfo> geClassMehods(Class<T> targetClass) {
        nullCheck(targetClass);
        MethodInfoList methodInfo = new ClassGraph().whitelistClasses(targetClass.getName())
        .enableMethodInfo().scan().getAllClasses().get(0).getMethodInfo();
        List<MethodInfo> ms=new ArrayList<>();
        for (int i = 0; i < methodInfo.size(); i++) {
            MethodInfo m = methodInfo.get(i);
            ms.add(m);
        }
        return ms;

    }

 

    /**
     * 读取一个Class的所有字段信息,可以通过ClassInfo继续读取class的 字段 ,注解,接口等
     *
     * @param <T>
     * @param targetClass
     * @return
     */
    public static <T> List<FieldInfo> getClassFiledsInfo(Class<T> targetClass) {
        nullCheck(targetClass);
        List<FieldInfo> filedslist = new LinkedList<FieldInfo>();
        ClassInfo classInfo = getClassInfo(targetClass);
        for (int i = 0; i < classInfo.getFieldInfo().size(); i++) {
            FieldInfo fieldInfo = classInfo.getFieldInfo().get(i);
            filedslist.add(fieldInfo);
        }
        return filedslist;
    }

    /**
     * 判断c1是否继承C2
     *
     * @param c1
     * @param c2
     * @return
     */
    public static boolean isExtendFromClass(Class<?> c1, Class<?> c2) {
        nullCheck(c1, c2);
        try {
            c1.isAssignableFrom(c2);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 判断c1是否实现C2
     *
     * @param c1
     * @param c2
     * @return
     */
    public static boolean isImplementsFromClass(Class<?> c1, Class<?> c2) {
        nullCheck(c1, c2);
        try {
            Class<?>[] interfaces = c1.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
                if (interfaces[i] == c2) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * 判断c1是否继承C2
     *
     * @param c1
     * @param c2
     * @return
     */
    public static boolean isExntedFromClass(Class<?> c1, Class<?> c2) {
        try {
            c1.isAssignableFrom(c2);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
