package com.xuecheng.manage_cms.controller;

import com.xuecheng.api.cms.SysDicthinaryControllerApi;
import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.SysDichinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sys")
public class SysDichinaryController implements SysDicthinaryControllerApi {

    @Autowired
    SysDichinaryService sysDichinaryService;

    //根据字典的分类id查询
    @GetMapping("/dictionary/get/{id}")
    public SysDictionary findByType(@PathVariable("id") String id) {
        return sysDichinaryService.findBydType(id);
    }
}
