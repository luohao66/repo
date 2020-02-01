package com.xuecheng.manage_cms.service;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class TemplateService {

    @Autowired
    CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    GridFsTemplate gridFsTemplate;

    //查询所有模板信息
    public QueryResponseResult findList(int page, int size) {

        if(page<=0){
            page=1;
        }
        page=page-1;

        if(size<5){
            size=5;
        }
        //分页
        Pageable pageable = PageRequest.of(page, size);
        Page<CmsTemplate> all =cmsTemplateRepository.findAll(pageable);

        QueryResult<CmsTemplate> queryResult=new QueryResult<>();
        queryResult.setTotal(all.getTotalElements());
        queryResult.setList(all.getContent());

        return new QueryResponseResult(CommonCode.SUCCESS,queryResult);

    }

    //新增模板信息
    public ResponseResult add(CmsTemplate cmsTemplate) {

        CmsTemplate templateName = cmsTemplateRepository.findByTemplateName(cmsTemplate.getTemplateName());
        String templateFileId = cmsTemplate.getTemplateFileId();
        //取出模板文件内容
        GridFSFile gridFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));
        if((gridFile!=null) || (templateName!=null)){
            ExceptionCast.cast(CommonCode.FAIL);
        }
        cmsTemplateRepository.save(cmsTemplate);
        return new ResponseResult(CommonCode.SUCCESS);
    }


    //根据id删除模板
    public ResponseResult deleteTemplate(String id) {
        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(id);
        if (optional.isPresent()){
            cmsTemplateRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }





}
