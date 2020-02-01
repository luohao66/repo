package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmsSiteControllerApi;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QuerySiteRequest;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.SiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms/site")
public class CmsSiteController implements CmsSiteControllerApi {

    @Autowired
    SiteService siteService;

    @GetMapping("/list/{page}/{size}")
    //查询所有站点
    public QueryResponseResult findList(@PathVariable("page") int page,@PathVariable("size") int size, QuerySiteRequest querySiteRequest) {
        return siteService.findList(page,size,querySiteRequest);
    }

    @DeleteMapping("/del/{id}")
    //根据id删除站点
    public ResponseResult deleteById(@PathVariable("id") String id) {
        return siteService.deleteById(id);
    }

    //新增站点
    @PostMapping("/add")
    public ResponseResult addSite(@RequestBody CmsSite cmsSite) {
        return siteService.add(cmsSite);
    }

    //回显siteId数据
    @GetMapping("/find/{id}")
    public CmsSite findById(@PathVariable("id") String id) {
        return siteService.findById(id);
    }

    @PostMapping("/update/{id}")
    public ResponseResult updateSite(@PathVariable("id")String id,@RequestBody CmsSite cmsSite) {
        return siteService.updateSite(id,cmsSite);
    }
}
