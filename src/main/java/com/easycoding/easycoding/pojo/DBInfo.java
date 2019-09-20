package com.easycoding.easycoding.pojo;
//数据库信息类
public class DBInfo{
    String db_type ; //数据库类型
    String db_url ; //数据库url
    String db_user ; //数据库用户名
    String db_pw ; //数据库密码
    String db_table ; //数据库表名
    String entityName ;//实体名
    String interfaceName ;//接口名
    String fieldList ;//页面字段
    public String getFieldList() {
        return fieldList;
    }

    public void setFieldList(String fieldList) {
        this.fieldList = fieldList;
    }



    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }



    public DBInfo(String db_type, String db_url, String db_user, String db_pw, String db_table) {
        this.db_type = db_type;
        this.db_url = db_url;
        this.db_user = db_user;
        this.db_pw = db_pw;
        this.db_table = db_table;
    }

    public String getDb_type() {
        return db_type;
    }

    public void setDb_type(String db_type) {
        this.db_type = db_type;
    }

    public String getDb_url() {
        return db_url;
    }

    public void setDb_url(String db_url) {
        this.db_url = db_url;
    }

    public String getDb_user() {
        return db_user;
    }

    public void setDb_user(String db_user) {
        this.db_user = db_user;
    }

    public String getDb_pw() {
        return db_pw;
    }

    public void setDb_pw(String db_pw) {
        this.db_pw = db_pw;
    }

    public String getDb_table() {
        return db_table;
    }

    public void setDb_table(String db_table) {
        this.db_table = db_table;
    }
}