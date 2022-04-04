package github.acodervic.mod.db.anima.core;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 实现对多种数据操作的封装接口
 * @param <M>
 * @param <T>
 * @param <M> H是handel 用于操作数据库
 */
public abstract class Connection  {



    //query
    public abstract <M> List<M> queryList(Class<M> modelClass,String sql, Object[] params );
    public abstract <M> M queryOne(Class<M> modelClass,String sql, Object[] params );
    public abstract  Long queryCount( String sql, Object[] params);
    public abstract  List<Map<String, Object>> queryListMap( String sql, Object[] params);

    
    //insert
    /**
     * 执行插入返回生成的key
     * @param sql
     * @param params
     * @return
     */
    public abstract  Object insert( String sql, String returnKeyName_opt,Object[] params);



    //execute
    public abstract  int execute( String sql, Object[] params);


    public abstract   <T> Boolean executeTransaction(Consumer<Connection> runnable,Boolean isRollback) ;

    
    
    /**
     * 释放连接
     */
    public abstract   void close() ;
}
