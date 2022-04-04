package github.acodervic.mod.db.anima.core.imlps;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import org.jdbi.v3.core.Handle;
import org.jdbi.v3.core.statement.SqlStatement;
import org.jdbi.v3.core.statement.Update;

import github.acodervic.mod.data.Opt;
import github.acodervic.mod.db.anima.core.AnimaCache;
import github.acodervic.mod.db.anima.core.Connection;
import github.acodervic.mod.reflect.ReflectUtil;
import io.github.classgraph.MethodInfo;

public class JdbiConnection extends Connection {
    Handle handle;

    static Map<String, Map<String, MethodInfo>> classFiledsInfo = new HashMap<>();
    static Map<String, Method> setMethodCache = new HashMap<>();

    public <M> Map<String, MethodInfo> getClassMehotds(Class<M> c) {
        if (classFiledsInfo.containsKey(c.getName())) {
            return classFiledsInfo.get(c.getName());
        }
        Map<String, MethodInfo> classGetMethods = new HashMap<>();
        // 读字段信息
        List<MethodInfo> ci = ReflectUtil.geClassMehods(c);
        for (int i = 0; i < ci.size(); i++) {
            MethodInfo methodInfo = ci.get(i);
            String lowerCaseMehodName = methodInfo.getName().toLowerCase();
            if (lowerCaseMehodName.startsWith("set")) {// 只需要set方法
                classGetMethods.put(methodInfo.getName(), methodInfo);
            }
        }
        classFiledsInfo.put(c.getName(), classGetMethods);
        return classGetMethods;
    }

    public <M> Opt<MethodInfo> getClassMethodInfoByName(Class<M> modelClass, String name) {
        MethodInfo methodInfo = getClassMehotds(modelClass).get(name);
        return new Opt<MethodInfo>(methodInfo);
    }

    public <M> Opt<Method> getSetMehodByMethodInfo(Class<M> modelClass, String name) {
        Opt<MethodInfo> classMethodInfoByName = getClassMethodInfoByName(modelClass, name);
        if (classMethodInfoByName.isNull_()) {
            return new Opt<>();
        }
        MethodInfo mehodInfo = classMethodInfoByName.get();
        String key = mehodInfo.getName() + mehodInfo.getClassInfo().getName();
        if (setMethodCache.containsKey(key)) {
            return new Opt<Method>(setMethodCache.get(key));
        } else {
            // 尝试获取method
            Method loadClassAndGetMethod = mehodInfo.loadClassAndGetMethod();
            setMethodCache.put(key, loadClassAndGetMethod);
            return new Opt<Method>(setMethodCache.get(key));
        }
    }

    public void invokeSetMethod(MethodInfo setMehodInfo, Object newInstance, Object value) {
        if (setMehodInfo.isStatic()) {
            // TODO log error 是static方法
            return;
        }
        if (!setMehodInfo.isPublic()) {
            // TODO log error 不是public方法
            return;
        }
        Opt<Method> setMethod = getSetMehodByMethodInfo(newInstance.getClass(), setMehodInfo.getName());
        if (setMethod.notNull_()) {
            Method method = setMethod.get();
            // 准备set
            try {
                method.invoke(newInstance, value);
            } catch (Exception e) {
                // TODO log error set失败
            }
        }

    }

    @Override
    public <M> List<M> queryList(Class<M> modelClass, String sql, Object[] params) {
        List<M> ret = new ArrayList<>();
        List<Map<String, Object>> rows = setParms(handle.createQuery(sql), params).mapToMap().list();
        // 自动使用反射填充数据类型
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> rowMap = rows.get(i);
            try {
                M newInstance = modelClass.newInstance();
                rowMap.keySet().forEach(cname -> {
                    // 判断数据库列名是否是使用的
                    String setMehodName = AnimaCache.getSetterName(cname);
                    MethodInfo setMehodInfo = getClassMethodInfoByName(modelClass, setMehodName).get();
                    // 只有一个参数的方法是正确的
                    if (setMehodInfo != null && setMehodInfo.getParameterInfo().length == 1) {
                        invokeSetMethod(setMehodInfo, newInstance, rowMap.get(cname));

                    } else {
                        Map<String, String> computeModelColumnMappings = AnimaCache
                                .computeModelColumnMappings(modelClass);
                        if (computeModelColumnMappings.containsKey(cname)) {
                            // 代表使用 Column 注解了列 字段
                            String filedName = computeModelColumnMappings.get(cname);// 获取实际字段名
                            String setMehodName2 = AnimaCache.getSetterName(filedName);
                            MethodInfo setMehodInfo2 = getClassMethodInfoByName(modelClass, setMehodName2).get();
                            if (setMehodInfo2 != null && setMehodInfo2.getParameterInfo().length == 1) {
                                invokeSetMethod(setMehodInfo2, newInstance, rowMap.get(cname));
                            } else {
                                // 没有可用的set方法/set方法的参数不对

                            }
                        } else {
                            // 类没有可用的set方法

                        }
                    }
                });
                ret.add(newInstance);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(modelClass.getName() + "构造实例失败,请确认有默认构造函数!");
            }

        }

        return ret;
    }

    @Override
    public Long queryCount(String sql, Object[] params) {
        return setParms(handle.createQuery(sql), params).mapTo(Long.class).one();
    }

    @Override
    public List<Map<String, Object>> queryListMap(String sql, Object[] params) {
        // TODO 这个可能有bug
        return setParms(handle.createQuery(sql), params).mapToMap().list();
    }

    public <S extends SqlStatement> S setParms(S sqlStatement, Object[] params) {
        for (int i = 0; i < params.length; i++) {
            sqlStatement.bind(i, params[i]);
        }
        return sqlStatement;
    }

    @Override
    public <M> M queryOne(Class<M> modelClass, String sql, Object[] params) {
        return setParms(handle.createQuery(sql), params).mapTo(modelClass).one();
    }

    @Override
    public int execute(String sql, Object[] params) {
        return setParms(handle.createUpdate(sql), params).execute();
    }

    @Override
    public Object insert(String sql, String returnKeyName_opt, Object[] params) {
        Update update = setParms(handle.createUpdate(sql), params);
        if (returnKeyName_opt != null) {
            // 代表查询要求返回Key
            Map<String, Object> first = update.executeAndReturnGeneratedKeys(returnKeyName_opt).mapToMap().first();
            return first.get(returnKeyName_opt);
        } else {
            return update.execute();
        }
    }

    /**
     * @param handle
     */
    public JdbiConnection(Handle handle) {
        this.handle = handle;
    }

    @Override
    public <T> Boolean executeTransaction(Consumer<Connection> runnable, Boolean isRollback) {
        if (runnable != null) {
            try {
                runnable.accept(this);
                this.handle.commit();// 提交数据
                return true;
            } catch (Exception e) {
                if (isRollback) {
                    this.handle.rollback();// 回滚
                }
            }
        }
        return false;
    }

    @Override
    public void close() {
        handle.close();
    }

}
