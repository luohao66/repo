package com.xuecheng.framework.domain.cms.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class QuerySiteRequest {

    //接收站点查询的查询条件
    //站点id
    //站点id
    @ApiModelProperty("站点id")
    private String siteId;
    //站点名称
    private String siteName;
}
