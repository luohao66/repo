package com.xuecheng.manage_cms.service;

import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.request.QuerySiteRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SiteService {
    @Autowired
    CmsSiteRepository cmsSiteRepository;

     //查询所有站点信息
    public QueryResponseResult findList(int page, int size, QuerySiteRequest querySiteRequest) {

        if(querySiteRequest==null){
            querySiteRequest =new QuerySiteRequest();
        }
        //条件匹配器
        ExampleMatcher exampleMatcher=ExampleMatcher.matching()
                .withMatcher("siteName",ExampleMatcher.GenericPropertyMatchers.contains());

        //将条件放到设置条件对象中
        CmsSite cmsSite=new CmsSite();
        //设置站点Id作为条件查询
        if(StringUtils.isNotEmpty(querySiteRequest.getSiteId())){
            cmsSite.setSiteId(querySiteRequest.getSiteId());
        }
        //设置站点名称作为条件查询
        if(StringUtils.isNotEmpty(querySiteRequest.getSiteName())){
            cmsSite.setSiteId(querySiteRequest.getSiteName());
        }
        //创建实例化条件查询
        Example<CmsSite> example=Example.of(cmsSite,exampleMatcher);
        //注意这里page从0开始
        if(page<=0){
            page=1;
        }
        page=page-1;

        if(size<5){
            size=5;
        }

        //分页
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsSite> all = cmsSiteRepository.findAll(example,pageable);
        QueryResult<CmsSite>queryResult=new QueryResult<>();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());

        QueryResponseResult queryResponseResult=new QueryResponseResult (CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    //新增站点
    public ResponseResult add(CmsSite cmsSite) {
        CmsSite byId = this.findById(cmsSite.getSiteId());
        if(byId!=null){
            ExceptionCast.cast(CmsCode.CMS_SITE_TEXISTS);
        }
        cmsSiteRepository.save(cmsSite);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    //回显站点信息
    public CmsSite findById(String id) {
        Optional<CmsSite> optional = cmsSiteRepository.findById(id);
        if(optional.isPresent()){
            CmsSite cmsSite = optional.get();
            return cmsSite;
        }
        return null;
    }

    //删除站点
    public ResponseResult deleteById(String id) {
        Optional<CmsSite> optional= cmsSiteRepository.findById(id);
        if(optional.isPresent()){
            cmsSiteRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    //更新站点信息
    public ResponseResult updateSite(String id, CmsSite cmsSite) {
        CmsSite cmsSite1= this.findById(id);
        if(cmsSite1==null){
            BeanUtils.copyProperties(cmsSite,cmsSite1);
        }
        cmsSite1.setSiteCreateTime(cmsSite.getSiteCreateTime());
        cmsSite1.setSiteDomain(cmsSite.getSiteDomain());
        cmsSite1.setSiteName(cmsSite.getSiteName());
        cmsSite1.setSiteWebPath(cmsSite.getSiteWebPath());
        cmsSite1.setSitePort(cmsSite.getSitePort());
        cmsSite1.setSitePhysicalPath(cmsSite.getSitePhysicalPath());

        cmsSiteRepository.save(cmsSite1);
        return new ResponseResult(CommonCode.SUCCESS);
    }
}
