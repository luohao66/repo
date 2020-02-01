package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.CmspageControllerApi;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.PageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/cms/page")
public class CmsPageController implements CmspageControllerApi {

    @Autowired
    private PageService pageService;

    //Resfult参数风格
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page") int page, @PathVariable("size") int size, QueryPageRequest queryPageRequest) {

        return pageService.findList(page,size,queryPageRequest);
    }

    @PutMapping("/add")
    public CmsPageResult add(@RequestBody CmsPage cmsPage) {
        return pageService.add(cmsPage);
    }

    @GetMapping("/get/{id}")
    public CmsPage findById(@PathVariable("id") String id) {
        return pageService.getById(id);
    }

    @PutMapping ("/edit/{id}")
    public CmsPageResult edit(@PathVariable("id") String id,@RequestBody CmsPage cmsPage) {
        return pageService.edit(id,cmsPage);
    }

    @DeleteMapping("/del/{id}")
    public ResponseResult del(@PathVariable("id") String id) {
        return pageService.del(id);
    }

    @PostMapping("/postPage/{pageId}")
    public ResponseResult post(@PathVariable("pageId") String pageId) {
        return pageService.post(pageId);
    }

    @PostMapping("/save")
    public CmsPageResult save(@RequestBody CmsPage cmsPage) {
        return pageService.save(cmsPage);
    }

    @PostMapping("/postPageQuick")
    public CmsPostPageResult postPageQuick(@RequestBody CmsPage cmsPage) {

        return pageService.postPageQuick(cmsPage);
    }
}
