package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.system.SysDictionary;
import com.xuecheng.manage_cms.service.SysDichinaryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SysDichinaryTest {

    @Autowired
    SysDichinaryService sysDichinaryService;

    @Test
    public void test(){
        SysDictionary byType = sysDichinaryService.findBydType("200");
        System.out.println(byType);
    }
}
