package com.easycoding.easycoding.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import com.easycoding.easycoding.pojo.DBInfo;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.utility.StringUtil;
import org.springframework.stereotype.Service;

/*
* 生成最终代码服务
* */
@Service
public class GenCodeService {
        // 公共资源
        private static String TEMP_SERVLET_CONTEXT = ""; // 项目路径[D:\xxxx\autocode\]
        // TODO
        private static String MODEL_ROOTPATH = "/Users/uglycode/Desktop/EasyCoding/src/main/resources/templates/";
        private static String MODEL_SUFFIX = ".ftl";
        // 类模板名
        private static final List<String> modelNameList = new ArrayList<String>();; // 存储所有模板名
        private static final List<String> needTransferNameList = new ArrayList<String>();; // 存储结果需要转换【StringUtil.XMLEnc】的模板名
        private static String SERVICE_MODEL = "service";
        private static String SERVICEIMPL_MODEL = "serviceImpl";
        //TODO
        private static String DAO_MODEL = "dao";
        private static String MAPPING_MODEL = "mapping";
        private static String ENTITY_MODEL = "entity";
        private static String CONTROLLER_MODEL = "controller";
        private static String TEST_MODEL = "test";
        // 工具类配置
        private static String DECODE = "UTF-8";
        private static final Configuration config  = new Configuration();
        private static final StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        private static Template template;
        private static Writer out = null;
        private static Map<String, String> templateMap; // 所有模板的字符串
        public static void init(String tempServletContext) {
            modelNameList.add(SERVICE_MODEL);
            modelNameList.add(SERVICEIMPL_MODEL);
            modelNameList.add(DAO_MODEL);
            modelNameList.add(MAPPING_MODEL);
            modelNameList.add(ENTITY_MODEL);
            modelNameList.add(CONTROLLER_MODEL);
            modelNameList.add(TEST_MODEL);
            needTransferNameList.add(MAPPING_MODEL);
            TEMP_SERVLET_CONTEXT = tempServletContext;
            templateMap = getAllFileStr();
            for(Map.Entry<String,String> entry : templateMap.entrySet()){
                stringTemplateLoader.putTemplate(entry.getKey(), entry.getValue());
            }
            config.setTemplateLoader(stringTemplateLoader);
        }

        /**
         * 产生所有业务类
         * @param dbInfo 数据库信息类
         * @param entityName 实体类名
         * @param interfaceName 接口名
         * @param fieldList 字段属性
         * @return Map<String, String> 业务类集合
         */
        public static Map<String, String> getAllClass(DBInfo dbInfo,  String entityName, String interfaceName, List<Map<String,String>> fieldList){
            //获取Data-Model数据
            Map<String, Object> dataModel = ModelTreeDataProvider.getFinalParam(dbInfo, entityName, interfaceName, fieldList);
            return getAllClass(dataModel);
        }

        /**
         * 产生所有业务类
         * @param dataModel 属性集合
         * @return Map<String, String> 业务类集合
         */
        public static Map<String, String> getAllClass(Map<String, Object> dataModel){
            Map<String, String> bzClassMap = new HashMap<String, String>();
            String entityName = (String) dataModel.get("entityName");
            for(String modelName : modelNameList){
                if(needTransferNameList.contains(modelName)){
                    bzClassMap.put(modelName, StringUtil.XMLEnc(getCLass(modelName, modelName + " of " + entityName, dataModel)));
                }else{
                    bzClassMap.put(modelName, getCLass(modelName, modelName + " of " + entityName, dataModel));
                }
            }

            return bzClassMap;
        }

        /**
         * 产生java类
         * @param model 模板字符串名【eg：entityTemplate】
         * @param target java类名【eg：User】，仅打印调试用，实际产生结果为字符串
         * @param rootMap 参数，包括接口名，实体名，实体属性等
         * @return String 业务类
         */
        public static String getCLass(String model, String target, Object rootMap){
            StringWriter stringWriter = new StringWriter();
            try {
                template = config.getTemplate(model, DECODE);
                out = new BufferedWriter(stringWriter);
                template.process(rootMap, out);
                out.flush();
                out.close();
//			System.out.println( target + "--- is YES ！！！");
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TemplateException e) {
                System.err.println( model+"生成出错啦～");
                e.printStackTrace();
            }
            return stringWriter.toString();
        }

        /**
         * 将所有.ftl文件内容转换为字符串
         * @return
         */
        private static Map<String, String> getAllFileStr(){
            Map<String, String> fileStrMap = new HashMap<String, String>();
            for(String modelName : modelNameList){
                fileStrMap.put(modelName, file2Str(modelName + MODEL_SUFFIX));
            }
            return fileStrMap;
        }

        /**
         * 将模板内容转成字符串
         * @param modle 模板名【eg：entity.ftl】
         * @return
         */
        private static String file2Str(String modle){
            StringBuffer buffer = new StringBuffer();
            try {
                BufferedReader br = new BufferedReader(new FileReader(TEMP_SERVLET_CONTEXT + MODEL_ROOTPATH + modle));
                String line = "";
                while((line = br.readLine()) != null){
                    buffer.append(line).append(System.getProperty("line.separator"));// 保持原有换行格式
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return buffer.toString();
        }

        /**
         * 获取所有模板【为前端能正常显示，部分特殊符号需转换，如"<"转为"&lt;"】
         * @return
         */
        public static Map<String, String> getAllTemplateStr(){
            Map<String, String> templateStrMap = new HashMap<String, String>();
            for(String modelName : modelNameList){
                if(needTransferNameList.contains(modelName)){
                    templateStrMap.put(modelName, StringUtil.XMLEnc(templateMap.get(modelName)));
                }else{
                    templateStrMap.put(modelName, templateMap.get(modelName));
                }
            }
            return templateStrMap;
        }
    }
