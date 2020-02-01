package com.xuecheng.manage_cms.controller;


import com.xuecheng.api.cms.CmsTemplateControllerApi;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cms/template")
public class CmsTemplateController implements CmsTemplateControllerApi {

    @Autowired
    TemplateService templateService;

    //查询所有模板
    @GetMapping("/list/{page}/{size}")
    public QueryResponseResult findList(@PathVariable("page")int page,@PathVariable("size") int size) {
        return templateService.findList(page,size);
    }
    //根据Id删除模板
    @DeleteMapping("/del/{id}")
    public ResponseResult deleteById(@PathVariable String id) {
        return templateService.deleteTemplate(id);
    }

    //新增模板
    @PostMapping("/add")
    public ResponseResult addTemplate(@RequestBody CmsTemplate cmsTemplate) {
        return templateService.add(cmsTemplate);
    }

}



