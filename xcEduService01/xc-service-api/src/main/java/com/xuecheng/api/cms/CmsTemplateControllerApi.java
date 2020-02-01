package com.xuecheng.api.cms;


import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="cms模板接口",description="cms模板管理接口，提供页面的增、删、改、查")
public interface CmsTemplateControllerApi {

    @ApiOperation("查询模板所有信息")
   public QueryResponseResult findList(int page, int size);

    @ApiOperation("根据id删除模板信息")
    public ResponseResult deleteById(String id);

    @ApiOperation("新增模板")
    public ResponseResult addTemplate(CmsTemplate cmsTemplate);



}
