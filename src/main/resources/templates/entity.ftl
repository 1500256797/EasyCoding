package com.tenny.${interfaceName?lower_case}.entity;

import java.util.Date;
/*
* TODO
* @Author
* @Date
*/
public class ${entityName} {
    
<#list params as param>

    <#if param.fieldComment?trim == "">
    //TODO
    <#else>
    //${param.fieldComment}
    </#if>
    private ${param.fieldType} ${param.fieldName};
    
</#list>
<#list params as param>
	public void set${param.fieldName?cap_first}(${param.fieldType} ${param.fieldName}){
        this.${param.fieldName} = ${param.fieldName};
    }
    
    public ${param.fieldType} get${param.fieldName?cap_first}(){
        return this.${param.fieldName};
    }
</#list>
    @Override
    public void toString(){
        System.out.println(" "
        <#list params as param>
            +"${param.fieldName}:"+${param.fieldName}
        </#list>
    );
}

}