package com.easycoding.easycoding.dao;

import com.easycoding.easycoding.pojo.DBInfo;
import com.easycoding.easycoding.util.StringUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/*
 * MysqlDao
 * 建立数据库连接，获取当前数据库的所有表名称以及表中的字段信息；
 * */
public class MysqlDao {
    private static String DRIVER_CLASS = "com.mysql.cj.jdbc.Driver";
    //"jdbc:mysql://localhost:3306/news";不需要加编码时区等信息
    private static String DATABASE_URL = "jdbc:mysql://localhost:3306/shuangshengriji";
    private static String DATABASE_URL_PREFIX = "jdbc:mysql://";

    private static String DATABASE_USER = "root";
    private static String DATABASE_PASSWORD = "123456...";
    private static Connection con = null;
    private static DatabaseMetaData dbmd = null;

    /**
     * 初始化数据库链接
     * @ param dbInfo 数据库信息类
     */
    public static void init(DBInfo dbInfo){
        try {
            //若传入，则拼接，否则使用默认url
            DATABASE_URL = DATABASE_URL_PREFIX + dbInfo.getDb_url();
            DATABASE_USER = dbInfo.getDb_user();
            DATABASE_PASSWORD = dbInfo.getDb_pw();

            //TODO
            //Class.forName(DRIVER_CLASS);

            con = DriverManager.getConnection(DATABASE_URL, DATABASE_USER, DATABASE_PASSWORD);
            dbmd = con.getMetaData();
        } catch (Exception e) {
            System.out.println("MysqlUtil工具执行init出错啦！");
            //e.printStackTrace();
        }
    }

    /**
     * TODO
     * 获取当前用户所有表
     * @return tables 数据表名称
     */
    public static List<String> getTables(){

        // 获取当前用户的所有表不对，应该获取某个数据库下的所有表；
        List<String> tables = new ArrayList<String>();

        try {
            //getTables需要四个参数
            //@param catalog ：数据库名称或者直接使用cnn.getCatalog（）;
            //@param schemaParttern :数据库登录名
            //@param tableNamePattern :null-获取所有表
            //@param types :类型标准，"TABLE"、"VIEW"、"SYSTEM TABLE"、"GLOBAL TEMPORARY"、"LOCAL TEMPORARY"、"ALIAS" 和 "SYNONYM"这几个经典的类型，一般使用”TABLE”
            ResultSet rs = dbmd.getTables(con.getCatalog(), DATABASE_USER, null, new String[] { "TABLE" });
            //rs
            //TABLE_CAT String => 表类别（可为 null）
            //
            //TABLE_SCHEM String => 表模式（可为 null）
            //
            //TABLE_NAME String => 表名称
            //
            //TABLE_TYPE String => 表类型。
            //
            //REMARKS String => 表的解释性注释
            //
            //TYPE_CAT String => 类型的类别（可为 null）
            //
            //TYPE_SCHEM String => 类型模式（可为 null）
            //
            //TYPE_NAME String => 类型名称（可为 null）
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
                //System.out.println("打印表名字："+rs.getString("TABLE_NAME"));
            }
            return tables; //如果发生异常，则返回tables = null
        } catch (SQLException e) {
            System.out.println("MysqlUtil工具执行getTables出错啦！");
            tables = null; //发生异常则返回空指针
            //e.printStackTrace();
        }
        return tables;
    }

    /**
     * 获取表字段信息
     * @param tableName
     * @return  某表的所有字段列表（名称，数据类型，null，注释等）
     */
    public static List<Map<String,String>> getTableCloumns(String tableName){
        List<Map<String,String>> columns = new ArrayList<Map<String,String>>();
        try{
            Statement stmt = con.createStatement();
            String sql = "select column_name, data_type, column_key, is_nullable, column_comment from information_schema.columns where table_name='" + tableName + "'and table_schema='" + DATABASE_URL.substring(DATABASE_URL.lastIndexOf("/") + 1, DATABASE_URL.length()) + "'";
            //从sql语句中解析；
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()){
                HashMap<String,String> map = new HashMap<String,String>();
                map.put("columnName", rs.getString("column_name"));
                //System.out.println("表："+tableName+"的字段名为："+map.get("columnName"));
                map.put("dataType", rs.getString("data_type"));
                map.put("isKey", StringUtil.isEmpty(rs.getString("column_key"))?"false":"true");
                map.put("notNull", rs.getString("is_nullable").equals("YES")?"false":"true");
                map.put("comment", rs.getString("column_comment"));
                columns.add(map);
            }
            return columns; //没有异常则正常返回，出现异常返回空指针
        } catch (SQLException e){
            System.out.println("MysqlUtil工具执行getTableCloumns出错啦！");
            columns = null ;
            //e.printStackTrace();
        }finally{
            if(null != con){
                try {
                    con.close();
                } catch (SQLException e) {
                    System.out.println("MysqlUtil工具执行close时出错啦！");
                    //e.printStackTrace();
                }
            }
        }
        return columns;
    }

}
