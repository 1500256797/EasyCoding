package com.easycoding.easycoding.service;

import com.easycoding.easycoding.dao.MysqlDao;
import com.easycoding.easycoding.pojo.DBInfo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * ModelTreeDataProvider
 * 最终的模版数据提供服务
 * */
@Service
public class ModelTreeDataProvider {
    // 数据库类型转属性类型
    private static Map<String, String> dataTypeMap;

    //定义数据类型转换规则
    static{
        //TODO 字段类型和属性类型的对应关系有待优化
        dataTypeMap = new HashMap<>();
        //解决bigint无法转换问题-已经解决
        dataTypeMap.put("BIGINT","Integer");
        dataTypeMap.put("bigint","Integer");
        dataTypeMap.put("NUMBER", "Integer");
        dataTypeMap.put("BYTE","Byte");
        dataTypeMap.put("tinyint","Byte");
        dataTypeMap.put("NVARCHAR2", "String");
        dataTypeMap.put("TIMESTAMP", "Date");
        dataTypeMap.put("TIMESTAMP(6)", "Date");
        dataTypeMap.put("varchar", "String");
        dataTypeMap.put("int", "Integer");
        dataTypeMap.put("datetime", "Date");
    }



    /**
     *
     * 产生最终参数数据，转换数据类型将数据封装好后交给freemakerutil；
     * @param dbInfo 数据库信息类
     * @param entityName 实体类名
     * @param interfaceName 接口名
     * @param fieldList 字段属性
     * @return Map<String, Object> 最终模板参数
     */
    public static Map<String, Object> getFinalParam(DBInfo dbInfo ,
                                                    String entityName,
                                                    String interfaceName,
                                                    List<Map<String,String>> fieldList){
        Map<String, Object> beanMap = new HashMap<String, Object>();
        // 从已有数据库表获取字段
        if(!isEmpty(dbInfo.getDb_type()) && !isEmpty(dbInfo.getDb_url()) && !isEmpty(dbInfo.getDb_user()) && !isEmpty(dbInfo.getDb_pw()) && !isEmpty(dbInfo.getDb_table()) && !isEmpty(entityName)){
            //beanMap为封装好的tree-model
            String  db_table = dbInfo.getDb_table() ;
            beanMap = getFinalParam(
                    db_table,
                    entityName,
                    interfaceName,
                    getFieldsFromTable(dbInfo));
        }else if(!isEmpty(entityName)){
            // 从页面表单字段
            beanMap = getFinalParam(entityName, entityName, interfaceName, getFieldsFromForm(fieldList));
        }
        //beanMap 用于填充模版的数据，包含类名，接口名，以及实体类中的属性；
        return beanMap;
    }

    /**
     * 接收表字段list，构造tree-model，内部函数
     * @param entityName
     * @param interfaceName
     * @param fieldList
     * @return
     */
    private static Map<String, Object> getFinalParam(String tableName,
                                                     String entityName,
                                                     String interfaceName,
                                                     List<Map<String,String>> fieldList){
        Map<String, Object> beanMap = new HashMap<String, Object>();
        //beanMap.put("packageName",request.getServletContext().getContextPath())
        beanMap.put("tableName", tableName);// 数据库表名
        beanMap.put("entityName", entityName);// 实体类名
        beanMap.put("interfaceName", interfaceName);// 接口名
        beanMap.put("params", fieldList);//实体类中的属性
        return beanMap;
    }

    /**
     * 将数据库字段解析为模板参数
     * @param
     * @return columnList 某表的所有字段的属性map列表
     */
    private static List<Map<String,String>> getFieldsFromTable(DBInfo dbInfo){
        List<Map<String, String>> columnList = new ArrayList<>();
//        if(db_type.equals("Oracle")){
//            OracleUtil.init(db_url, db_user, db_pw);
//            columnList = OracleUtil.getTableCloumns(db_table);
//        }
        if(dbInfo.getDb_type().equals("Mysql")){
            MysqlDao.init(dbInfo);
            //执行sql语句，获取某个表结构；
            columnList = MysqlDao.getTableCloumns(dbInfo.getDb_table());
        }
        //foreach循环：拿出第一个元素，然后在put到原来的list里，及dataType还在
        //验证成功！
        for(Map<String, String> column : columnList){
            column.put("columnName", column.get("columnName"));// 数据库字段名
            column.put("fieldName", column2Property(column.get("columnName")));// 实体属性名
            column.put("fieldType", columnType2FieldType(column.get("dataType")));// 实体属性类型
            column.put("fieldComment", column.get("comment"));// 字段说明，即属性说明
            column.put("notNull", column.get("notNull"));// 非空
            column.put("isKey", column.get("isKey"));// 是否主键
        }
        return columnList;
    }

    /**
     * 将页面字段解析为模板参数
     * @param fieldList
     * @return
     */
    private static List<Map<String,String>> getFieldsFromForm(List<Map<String,String>> fieldList){
        for(Map<String, String> field : fieldList){
            field.put("columnName", field.get("fieldName"));// 数据库字段名
            // The only legal comparisons are between two numbers, two strings, or two dates.
            field.put("isKey", String.valueOf(field.get("isKey")));// true-->"true", false-->"false"
            field.put("notNull", String.valueOf(field.get("notNull")));// 同上
        }
        return fieldList;
    }

    // 数据库字段转属性【eg：USER_ID --> userId】
    private static String column2Property(String fieldName){
        StringBuffer result = new StringBuffer();

        fieldName = fieldName.toLowerCase();
        String[] fields = fieldName.split("_");
        for(int i=0; i<fields.length; i++){
            String field = fields[i];
            if(i == 0){
                result.append(field);
            }else{
                result.append(toUpString(field));
            }
        }

        return result.toString();
    }

    // 首字母大写【eg：user --> User】
    private static String toUpString(String className) {
        char[] cs = className.toCharArray();
        cs[0] -= 32;
        String ClassName = String.valueOf(cs);
        return ClassName;
    }

    // 首字母小写【eg：User --> user】
    private static String toLowString(String className) {
        char[] cs = className.toCharArray();
        cs[0] += 32;
        String ClassName = String.valueOf(cs);
        return ClassName;
    }

    // 数据库类型转属性类型【eg: NVARCHAR2 --> String】
    private static String columnType2FieldType(String columnType){
        return dataTypeMap.get(columnType);
    }

    public static boolean isEmpty(String value){
        if(null == value || value.equals("")){
            return true;
        }
        return false;
    }

}
