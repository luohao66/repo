package com.xuecheng.manage_cms.service;


import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsConfig;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsConfigRepository;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsSiteRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
public class PageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private CmsConfigRepository configRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GridFsTemplate gridFsTemplate;
    
    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    GridFSBucket gridFSBucket;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    CmsSiteRepository cmsSiteRepository;

    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest){

        if(queryPageRequest==null){
            queryPageRequest =new QueryPageRequest();
        }
        //条件匹配器
        ExampleMatcher exampleMatcher=ExampleMatcher.matching()
                .withMatcher("pageAliase",ExampleMatcher.GenericPropertyMatchers.contains());

        //将条件放到设置条件对象中
        CmsPage cmsPage=new CmsPage();
        //设置站点Id作为条件查询
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())){
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }
        //设置页面别名作为条件查询
        if(StringUtils.isNotEmpty(queryPageRequest.getPageAliase())){
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }
        //设置模板id作为条件查询
        if(StringUtils.isNotEmpty(queryPageRequest.getTemplateId())){
            cmsPage.setPageAliase(queryPageRequest.getTemplateId());
        }

        //创建实例化条件查询
        Example<CmsPage> example=Example.of(cmsPage,exampleMatcher);
        //注意这里page从0开始
        if(page<=0){
            page=1;
        }
        page=page-1;

        if(size<10){
            size=10;
        }

        //分页
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsPage> all = cmsPageRepository.findAll(example,pageable);
        QueryResult<CmsPage>queryResult=new QueryResult<>();
        queryResult.setList(all.getContent());
        queryResult.setTotal(all.getTotalElements());

        QueryResponseResult queryResponseResult=new QueryResponseResult (CommonCode.SUCCESS,queryResult);
        return queryResponseResult;
    }

    public CmsPageResult add(CmsPage cmsPage){

        //判断CmsPage是否为空
        if(cmsPage==null){
            //抛出异常
        }
        //页面名称 站点id 页面 webpath是否唯一
        String pageName = cmsPage.getPageName(); //页面名称
        String siteId = cmsPage.getSiteId(); //站点id
        String pageWebPath = cmsPage.getPageWebPath();//路径
        CmsPage cmsPage1= cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(pageName, siteId, pageWebPath);

        if(cmsPage1!=null){
            //页面已经存在
            //抛出异常
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }

        //为了防止mongoBb主键自动添加
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);
        return new CmsPageResult(CommonCode.SUCCESS,cmsPage);
    }

    //根据id查询页面
    public CmsPage getById(String id){
        Optional<CmsPage> optional= cmsPageRepository.findById(id);
        if(optional.isPresent()){
            CmsPage cmsPage = optional.get();
            return cmsPage;
        }
        return null;
    }

    //修改页面
    public CmsPageResult edit(String id,CmsPage cmsPage){
        CmsPage cmsPage1 = getById(id);
        if(cmsPage!=null){
            //设置修改数据
            cmsPage1.setTemplateId(cmsPage.getTemplateId());
           //更新所属站点
            cmsPage1.setSiteId(cmsPage.getSiteId());
            //更新页面别名
            cmsPage1.setPageAliase(cmsPage.getPageAliase());
           //更新页面名称
            cmsPage1.setPageName(cmsPage.getPageName());
            //更新访问路径
            cmsPage1.setPageWebPath(cmsPage.getPageWebPath());
           //更新物理路径
           cmsPage1.setPagePhysicalPath(cmsPage.getPagePhysicalPath());
           //更新数据URL
            cmsPage1.setDataUrl(cmsPage.getDataUrl());
           //提交修改
           CmsPage save = cmsPageRepository.save(cmsPage1);
           if(save !=null){
               //修改成功
               return new CmsPageResult(CommonCode.SUCCESS,save);
           }
        }

        return new CmsPageResult(CommonCode.FAIL,null);
    }

    //根据id删除页面
    public ResponseResult del(String id){
        Optional<CmsPage> optional = cmsPageRepository.findById(id);
        if(optional.isPresent()){
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }

    //根据id查询cmsConfig
    public CmsConfig getConfigById(String id){
        Optional<CmsConfig> optional = configRepository.findById(id);
        if (optional.isPresent()){
            CmsConfig cmsConfig = optional.get();
            return cmsConfig;
        }
        return null;
    }


    //页面静态化流程
    public String getPageHtml(String pageId){

        //获取数据模型
        Map model= getModelByPageId(pageId);
        if(model==null){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }

        //获取页面模板
        String template= getTemplateByPageId(pageId);
        if(StringUtils.isEmpty(template)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //执行静态化
        String html = gengerateHtml(template, model);
        return html;
    }

    private String gengerateHtml(String templateContent,Map model) {

        //定义配置配置类
        Configuration configuration = new Configuration(Configuration.getVersion());
        //模板加载器
        StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
        stringTemplateLoader.putTemplate("template", templateContent);
        //向configuration配置模板加载器
        configuration.setTemplateLoader(stringTemplateLoader);
        //获取模板
        try {
            Template template = configuration.getTemplate("template");
            //调用API静态化
            String context = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
            return context;
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            return null;
        }

    }
    //获取数据模型
    private Map getModelByPageId(String pageId){
       //取出页面的信息
        CmsPage cmspage = this.getById(pageId);
        if(cmspage==null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);

        }
        String dataUrl = cmspage.getDataUrl();
        if(StringUtils.isEmpty(dataUrl)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }
        //通过resTemolate请求dataUrl获取数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map model = forEntity.getBody();
        return model;

    }

    private String getTemplateByPageId(String pageId) {
        //查询页面信息
        CmsPage cmspage = this.getById(pageId);
        if(cmspage==null){
            //页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }
        //页面模板
        String templateId = cmspage.getTemplateId();
        if(StringUtils.isEmpty(templateId)){
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }
        //查询模板信息
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);
        if(optional.isPresent()){

            CmsTemplate cmsTemplate = optional.get();
            //获取模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();
            //从GridFs中取模板内容
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));

            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
            //创建GridFsResource对象，获取流
            GridFsResource gridFsResource = new GridFsResource(gridFSFile,gridFSDownloadStream);
            //从流中取数据
            try {
                String content = IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
                return content;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public ResponseResult post(String pageId){
        //执行页面静态化
        String pageHtml = this.getPageHtml(pageId);
        //将文件储存到GridFs中
        CmsPage cmsPage = this.saveHtml(pageId, pageHtml);
        //向mq发送消息
        sendPostMsg(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //向mq发送消息
    private void sendPostMsg(String pageId){
        //获取Cmspage信息
        CmsPage cmsPage = this.getById(pageId);
        if(cmsPage == null){
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //站点id
        String siteId = cmsPage.getSiteId();
        //创建消息对象
        Map<String,String> msg = new HashMap<>();
        msg.put("pageId",pageId);
        //转成json串
        String jsonString = JSON.toJSONString(msg);
        //发送给mq
        rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE,siteId,jsonString);
    }

    //将文件储存到GridFs中
    private CmsPage saveHtml(String pageId,String htmlContent){
        CmsPage cmsPage = getById(pageId);
        if(cmsPage==null){
            ExceptionCast.cast(CommonCode.INVALID_RARAM);
        }
        //将文件转为输入流
        ObjectId objectId=null;
        try {
            InputStream inputStream = IOUtils.toInputStream(htmlContent, "utf-8");
            objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());
        } catch (IOException e) {
            e.printStackTrace();
        }
       //将站点id更新到Cmspage中
        cmsPage.setHtmlFileId(objectId.toHexString());

        return cmsPage;
    }
     //保存页面
    public CmsPageResult save(CmsPage cmsPage) {
        //检查页面是否存在
        CmsPage cmsPage1= cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath());
        //有则更新，没有则添加
        if(cmsPage1!=null){
            return this.edit(cmsPage1.getPageId(),cmsPage);
        }else {
            return this.add(cmsPage);
        }
    }

    //一键发布页面
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {
        //将页面信息存储到Cms_page集合中
        CmsPageResult save = this.save(cmsPage);
        if(!save.isSuccess()){
            return new CmsPostPageResult(CommonCode.FAIL,null);
        }
        
        //得到pageId
        CmsPage cmsPage1 = save.getCmsPage();
        String pageId = cmsPage1.getPageId();
        //执行页面发布(页面静态化 、保存到GrdiFs,向mq发送消息)
        ResponseResult post = this.post(pageId);
        if(!post.isSuccess()){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        //拼接页面Url
        CmsSite cmsSite = this.findByIdCmsSite(cmsPage1.getSiteId());
        String pageUrl=cmsSite.getSiteDomain()+cmsSite.getSiteWebPath()+cmsPage1.getPageWebPath()+cmsPage1.getPageName();
        return new CmsPostPageResult(CommonCode.SUCCESS,pageUrl);
    }
     //查询站点信息
    public CmsSite findByIdCmsSite(String siteId){
        Optional<CmsSite> optional = cmsSiteRepository.findById(siteId);
        if(optional.isPresent()){
            return optional.get();
        }
        return null;
    }
}
