package com.xuecheng.manage_course;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_course.client.CmsPageClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRibbon {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    CmsPageClient cmsPageClient;

    @Test
    public void Test(){
        //要确定获取的服务名
        String serverId="XC-SERVICE-MANAGE-CMS";
        //ribbon客户端从eurekaServer中获取服务列表
        ResponseEntity<Map> forEntity = restTemplate.getForEntity("http://" + serverId + "/cms/page/get/5e035c197b17ecf7087d34b8", Map.class);
        Map body = forEntity.getBody();
        System.out.println(body);
    }

    @Test
    public void test2(){
        CmsPage cmsPageById = cmsPageClient.findCmsPageById("5e035c197b17ecf7087d34b8");
        System.out.println(cmsPageById);

    }
}
