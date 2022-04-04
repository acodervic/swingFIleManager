package github.acodervic.mod.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import cn.hutool.json.JSONArray;
import github.acodervic.mod.Constant;
import github.acodervic.mod.data.ArrayUtil;
import github.acodervic.mod.data.CharUtil;
import github.acodervic.mod.data.JSONUtil;

/**
 * DBC
 */
public class DBcoon {
    public static enum DBtype {
        SQLLITE("org.sqlite.JDBC", "jdbc:sqlite:"), MYSQL("com.mysql.cj.jdbc.Driver", "jdbc:mysql://"),
        SQLSERVER("SQLSERVER", ""), POSTGRESQL("org.postgresql.Driver", "jdbc:postgresql://");

        public String className;
        public String DB_URL;

        // 构造方法
        DBtype(String className, String DB_URL) {
            this.className = className;
            this.DB_URL = DB_URL;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public String getDB_URL() {
            return DB_URL;
        }

        public void setDB_URL(String dB_URL) {
            DB_URL = dB_URL;
        }

    }

    PreparedStatement excuteps = null;
    Statement querys = null;
    Connection ct = null;
    ResultSet rs = null;
    String Class_Name = null;
    String DB_URL = null;
    String DBname = null;

    /**
     * 远程数据库
     * 
     * @param type
     * @param serverIP
     * @param serverPort
     * @param user
     * @param pass
     * @param DBname
     */
    public DBcoon(DBcoon.DBtype type, String serverIP, int  serverPort, String user, String pass, String DBname,boolean SSL,String charSet) {
        if (charSet==null||CharUtil.trim(charSet).length()==0) {
            charSet = Constant.defultCharsetStr;
        }
        // 初始化数据库连接器
        Class_Name = type.getClassName();
        DB_URL = type.getDB_URL() + serverIP + ":" + serverPort + "/" + DBname+"?useSSL="+SSL+"&serverTimezone=UTC&characterEncoding="+charSet;
        try {
            // 加载驱动
            Class.forName(Class_Name);
            // 创建链接
            System.out.println("数据库连接url="+DB_URL);
            this.ct = DriverManager.getConnection(DB_URL, user, pass);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void close() {
        if (this.ct!=null) {
            try {
                this.ct.close();
            } catch (SQLException e) {

                e.printStackTrace();

            }
        }
    }
    /**
     * 文件数据库
     * 
     * @param type
     * @param DBfilePath
     * @param user
     * @param pass
     * @param DBname
     */
    public DBcoon(DBcoon.DBtype type, String DBfilePath, String user, String pass, String DBname) {
        try {
            // 初始化数据库连接器
            Class_Name = type.getClassName();
            DB_URL = type.getDB_URL();
            DB_URL += DBfilePath;
            // 加载驱动
            Class.forName(Class_Name);
            // 创建链接
           this.ct= DriverManager.getConnection(DB_URL);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    /**
     * 执行查询操作 如果查询错误返回null
     * 
     * @param sql
     * @param parms
     * @return
     */
    public JSONArray query(String sql, Object[] parms) {
        try {
            PreparedStatement pt = this.ct.prepareStatement(sql);
            int index =1;
            if (parms != null) {
                for (Object parm : parms) {
                    if (parm instanceof Integer) {
                        pt.setInt(index, (Integer) parm);
                    } else if (parm instanceof String) {
                        pt.setString(index, parm.toString());
                    } else {
                        pt.setObject(index, parm);

                    }
                    index += 1;
                }

            }

            System.out.println("执行sql:"+sql+"   参数");
            ArrayUtil.printData(parms, "    ");
            return JSONUtil.resultSetToJsonArry(pt.executeQuery());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 执行插入操作,插入成功后返回主键id,如果执行错误则返回null
     */
    public JSONArray insert(String sql, Object[] parms) {
        try {
            PreparedStatement exc = this.ct.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            if (parms != null) {
                int index =1;
                for (Object parm : parms) {
                    if (parm instanceof Integer) {
                        exc.setInt(index, (Integer) parm);
                    } else if (parm instanceof String) {
                        exc.setString(index, parm.toString());
                    } else {
                        exc.setObject(index, parm);

                    }
                    index += 1;
                }
            }
            System.out.println("执行sql:"+sql+"   参数");
            ArrayUtil.printData(parms, "    ");
            if (exc.executeUpdate() != 0) {
                return JSONUtil.resultSetToJsonArry(exc.getGeneratedKeys());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * 执行删除语句,如果出错则返回-1,正常则返回删除的行数
     * 
     * @param sql
     * @param parms
     * @return
     */
    public int delete(String sql, Object[] parms) {
        try {
            PreparedStatement pt = this.ct.prepareStatement(sql);
            if (parms != null) {
                int index = 1;
                for (Object parm : parms) {
                    if (parm instanceof Integer) {
                        pt.setInt(index, (Integer) parm);
                    } else if (parm instanceof String) {
                        pt.setString(index, parm.toString());
                    } else {
                        pt.setObject(index, parm);

                    }
                    index += 1;
                }
            }
            System.out.println("执行sql:"+sql+"   参数");
            ArrayUtil.printData(parms, "    ");
            return pt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

    /**
     * 执行删除语句,删除成功返回删除的行数,删除失败返回-1
     * 
     * @param sql
     * @param parms
     * @return
     */
    public int update(String sql, Object[] parms) {
        try {
            PreparedStatement pt = this.ct.prepareStatement(sql);
            if (parms != null) {
                int index = 1;
                for (Object parm : parms) {
                    if (parm instanceof Integer) {
                        pt.setInt(index, (Integer) parm);
                    } else if (parm instanceof String) {
                        pt.setString(index, parm.toString());
                    } else {
                        pt.setObject(index, parm);

                    }
                    index += 1;
                }
            }
            return pt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }

    }

 
}
 