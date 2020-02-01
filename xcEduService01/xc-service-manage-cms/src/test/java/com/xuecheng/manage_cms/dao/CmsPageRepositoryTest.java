package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.manage_cms.service.PageService;
import org.bson.io.BsonOutput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@RunWith(SpringRunner.class)
public class CmsPageRepositoryTest{

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private PageService pageService;
    @Test
    public void test(){
       /* //分页查询返回的结果Page
        PageRequest pageRequest= PageRequest.of(1, 10);
        Page<CmsPage> all = cmsPageRepository.findAll(pageRequest);
        System.out.println(all);*/
      /*  Optional<CmsPage> optional = cmsPageRepository.findById("5a754adf6abb500ad05688d9");

        if(optional.isPresent()) {
            CmsPage cmsPage = optional.get();
            System.out.println(cmsPage);
        }*/
      //自定义条件查询
      int page=0;
      int size=10;
      Pageable pageable = PageRequest.of(page, size);
      CmsPage cmsPage=new CmsPage();
      cmsPage.setSiteId("5a751fab6abb5044e0d19ea1");

        //条件匹配器
      ExampleMatcher exampleMatcher=ExampleMatcher.matching()
              .withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());
      //实例化匹配器
      Example<CmsPage> example=Example.of(cmsPage,exampleMatcher);
      Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);
      List<CmsPage> contents = all.getContent();
      System.out.println(contents);



    }

}
