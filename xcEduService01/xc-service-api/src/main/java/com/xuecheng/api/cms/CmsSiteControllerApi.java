package com.xuecheng.api.cms;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QuerySiteRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(value="cms站点接口",description="cms模板管理接口，提供页面的增、删、改、查")
public interface CmsSiteControllerApi {

    @ApiOperation("查询所有站点")
    public QueryResponseResult findList(int page, int size,QuerySiteRequest querySiteRequest);

    @ApiOperation("根据id删除站点")
    public ResponseResult deleteById(String id);

    @ApiOperation("新增站点")
    public ResponseResult addSite(CmsSite cmsSite);

    @ApiOperation("根据id查询站点")
    public CmsSite findById(String id);

    @ApiOperation("更新站点信息")
    public ResponseResult updateSite(String id, CmsSite cmsSite);

}
