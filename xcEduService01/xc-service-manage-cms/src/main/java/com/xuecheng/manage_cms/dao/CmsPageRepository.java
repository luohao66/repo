package com.xuecheng.manage_cms.dao;

import com.xuecheng.framework.domain.cms.CmsPage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CmsPageRepository extends MongoRepository<CmsPage,String> {
    //根据页面名称查询
    public CmsPage findByPageName();
    //根据站点id ,页面名称，页面webpath查询
    public CmsPage findByPageNameAndSiteIdAndPageWebPath(String pageName,String siteId,String pageWebPath );
    //根据id查询页面
}
