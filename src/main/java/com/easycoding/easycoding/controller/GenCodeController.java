package com.easycoding.easycoding.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easycoding.easycoding.dao.MysqlDao;
import com.easycoding.easycoding.pojo.DBInfo;
import com.easycoding.easycoding.service.GenCodeService;
import com.easycoding.easycoding.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
* 生成代码
* */
@RestController
public class GenCodeController {
    @Autowired
    GenCodeService genCodeService;

    @PostMapping("/gettable")
    public JSONObject getAllTables(@RequestBody  DBInfo dbInfo ){
        List<String> tables = new ArrayList<String>();
        String db_type = dbInfo.getDb_type();
        // 从数据库获取表
        if(!StringUtil.isEmpty(db_type)){
            if(db_type.equals("Oracle")){
                //OracleUtil.init(db_url, db_user, db_pw);
                //tables = OracleUtil.getTables();
            }
            if(db_type.equals("Mysql")){
                MysqlDao.init(dbInfo);
                tables = MysqlDao.getTables();
            }
        }
        JSONObject objJson = new JSONObject();
        objJson.put("tables", tables);
        System.out.println("我来过！");
        return objJson;
    }

    @PostMapping("/getcode")
    public JSONObject getAllCodes(@RequestBody DBInfo dbInfo ) {

        String entityName = dbInfo.getEntityName();
        //默认实体名为表名
        if(null==entityName||entityName.trim().equals("")){
            entityName =dbInfo.getDb_table();//表名
            char[] cs = entityName.toCharArray();
            cs[0] -= 32;
            entityName = String.valueOf(cs);
            //首字母小写转大写
        }
        String interfaceName = dbInfo.getInterfaceName() ;
        String fieldListStr = dbInfo.getFieldList();
        if(null == interfaceName || interfaceName.equals("")){
            interfaceName = entityName;
        }
        List<Map<String,String>> fieldList = (List<Map<String, String>>) JSON.parse(fieldListStr);
        Map<String, String> bzClass = GenCodeService.getAllClass(dbInfo, entityName, interfaceName, fieldList);


        JSONObject objJson = new JSONObject();
        objJson.put("bzClass", bzClass);
        if (bzClass.keySet().size()<=1){
            objJson.put("code","error");
        }
        return objJson ;
    }

    public void init(){
        // 初始化模板资源到内存
        //String tempServletContext = this.getServletConfig().getServletContext().getRealPath("/");
        GenCodeService.init("/");
        System.out.println("templates init success...");
    }

    //获取模版
    @PostMapping(value ="/gettemplate")
    public JSONObject getTemplate(){
        init();
        JSONObject objJson = new JSONObject();
        objJson.put("templates", GenCodeService.getAllTemplateStr());
        return objJson;
    }
}
