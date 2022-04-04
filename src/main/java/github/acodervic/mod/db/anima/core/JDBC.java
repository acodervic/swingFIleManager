package github.acodervic.mod.db.anima.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.sql.DataSource;

import github.acodervic.mod.db.anima.Model;

public abstract class JDBC {
    
    public abstract Connection  newConnection() ;

    Map<String,Function<Map<String, Object>, Model>> resultRowDataConvert=new HashMap<>();//行到对象的转换
    Map<String,Function< Object, Object>> resultCloumnDataConvert=new HashMap<>();//列转换器 key= moduleName+列名 输入一个数据库过来的列obj返回一个obj


        /**
     * 打开数据库
     * @param url
     * @param user
     * @param pass
     * @return
     */
    public abstract  Boolean open( String url, String user, String pass) throws Exception;

        /**
     * 打开数据库
     * @param url
     * @param user
     * @param pass
     * @return
     */
    public abstract  Boolean open( DataSource dataSource) throws Exception;



    /**
     * 注册一个行数据转换器,用于rowData到特定model的转换
     * @param <M>
     * @param resultRowData
     */
    public  <M>  void addRowConvert(Class<M> moduleClass,Function<Map<String,Object>,Model> fun) {
        resultRowDataConvert.put(moduleClass.getName(), fun);
    }


    public  <M>  void addCloumnConvert(Class<M> moduleClass,Function< Object, Object> fun) {
        
    }


    

}
