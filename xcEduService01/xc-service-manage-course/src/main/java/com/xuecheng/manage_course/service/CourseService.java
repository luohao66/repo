package com.xuecheng.manage_course.service;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseBaseResult;
import com.xuecheng.framework.domain.course.response.CoursePublicResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.exception.ExceptionCatch;
import com.xuecheng.framework.model.response.*;
import com.xuecheng.manage_course.client.CmsPageClient;
import com.xuecheng.manage_course.dao.*;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author Administrator
 * @version 1.0
 **/
@Service
public class CourseService {
    @Autowired
    TeachplanMapper teachplanMapper;

    @Autowired
    TeachplanRepository teachplanRepository;

    @Autowired
    CourseBaseRepository courseBaseRepository;

    @Autowired
    CourseMarkertRepository courseMarkertRepository;

    @Autowired
    CoursePicRepository coursePicRepository;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    CmsPageClient cmsPageClient;

    @Autowired
    CoursePubRepository coursePubRepository;

    @Autowired
    TeachplanMediaRepository teachplanMediaRepository;


    @Value("${course-publish.dataUrlPre}")
    private String publish_dataUrlPre;
    @Value("${course-publish.pagePhysicalPath}")
    private String publish_page_physicalpath;
    @Value("${course-publish.pageWebPath}")
    private String publish_page_webpath;
    @Value("${course-publish.siteId}")
    private String publish_siteId;
    @Value("${course-publish.templateId}")
    private String publish_templateId;
    @Value("${course-publish.previewUrl}")
    private String previewUrl;

    //分页查询
    public QueryResponseResult findList(int page, int size, CourseListRequest courseListRequest) {
        //非空判断
        if (page < 0) {
            page = 1;
        }
        if (size < 5) {
            size = 5;
        }
        if (courseListRequest == null) {
            courseListRequest = new CourseListRequest();
        }
        //插件分页
        PageHelper.startPage(page, size);
        Page<CourseInfo> courseBasePage = courseMapper.findCourseList(courseListRequest);
        //获取总页数
        long total = courseBasePage.getTotal();
        //获取
        List<CourseInfo> resultlist = courseBasePage.getResult();

        QueryResult queryResult = new QueryResult();
        queryResult.setTotal(total);
        queryResult.setList(resultlist);

        return new QueryResponseResult (CommonCode.SUCCESS, queryResult);
    }

    //课程分类
    public CategoryNode findList() {
        CategoryNode list = courseMapper.findList();
        return list;
    }

    //获取课程信息
    public CourseBase getCourseById(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            CourseBase courseBase = optional.get();
            return courseBase;
        }
        return null;
    }

    //修改课程信息
    public CourseBaseResult update(String courseId, CourseBase courseBase) {

        CourseBase courseBase1 = this.getCourseById(courseId);
        if (courseBase1 != null) {

            courseBase1.setName(courseBase.getName());
            courseBase1.setDescription(courseBase.getGrade());
            courseBase1.setUsers(courseBase.getUsers());
            courseBase1.setSt(courseBase.getSt());
            courseBase1.setMt(courseBase.getMt());
            courseBase1.setStudymodel(courseBase.getStudymodel());
            courseBase1.setDescription(courseBase.getDescription());
            courseBaseRepository.save(courseBase1);
            return new CourseBaseResult(CommonCode.SUCCESS, courseBase1);

        }

        return new CourseBaseResult(CommonCode.FAIL, null);
    }

    //新增课程
    @Transactional
    public AddCourseResult add(CourseBase courseBase) {
        //课程状态认为未发布
        courseBase.setStatus("202001");

        CourseBase save = courseBaseRepository.save(courseBase);
        return new AddCourseResult(CommonCode.SUCCESS, courseBase.getId());

    }

    //获取课程营销信息
    public CourseMarket getcourseMarket(String courseid) {
        Optional<CourseMarket> optional = courseMarkertRepository.findById(courseid);
        if (optional.isPresent()) {
            CourseMarket courseMarket = optional.get();
            return courseMarket;

        }
        return null;
    }

    //更新课程营销信息
    @Transactional
    public CourseMarket updateMarket(String id, CourseMarket courseMarket) {
        CourseMarket courseMarket1 = this.getcourseMarket(id);
        if (courseMarket1 == null) {
            //添加课程营销信息
            courseMarket1 = new CourseMarket();
            BeanUtils.copyProperties(courseMarket, courseMarket1);
            //设置课程id
            courseMarket1.setId(id);
            courseMarkertRepository.save(courseMarket1);

        } else {
            //不为空，修改课程营销信息
            courseMarket1.setCharge(courseMarket.getCharge());
            courseMarket1.setPrice(courseMarket.getPrice());
            courseMarket1.setStartTime(courseMarket.getStartTime());
            courseMarket1.setEndTime(courseMarket.getEndTime());
            courseMarket1.setQq(courseMarket.getQq());
            courseMarket1.setValid(courseMarket.getValid());
            courseMarket1.setPrice_old(courseMarket.getPrice_old());
            courseMarkertRepository.save(courseMarket1);
        }
        return courseMarket1;

    }


    //添加课程图片
    @Transactional
    public ResponseResult addCoursePic(String courseId, String pic) {

        //查询课程图片
        Optional<CoursePic> optional = coursePicRepository.findById(courseId);
        CoursePic coursePic = null;
        if (optional.isPresent()) {
            coursePic = optional.get();
        }
        if (coursePic == null) {
            coursePic = new CoursePic();
        }
        coursePic.setCourseid(courseId);
        coursePic.setPic(pic);
        coursePicRepository.save(coursePic);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //查询课程图片
    public CoursePic findCoursePic(String courseId) {
        //查询课程图片
        Optional<CoursePic> picOptional = coursePicRepository.findById(courseId);
        if (picOptional.isPresent()) {
            CoursePic coursePic = picOptional.get();
            return coursePic;
        }
        return null;
    }

    //删除课程图片信息
    @Transactional
    public ResponseResult deleCoursePic(String courseId) {
        //执行删除
        long result = coursePicRepository.deleteByCourseid(courseId);
        if (result > 0) {
            return new ResponseResult(CommonCode.SUCCESS);
        }
        return new ResponseResult(CommonCode.FAIL);
    }


    //查询课程计划
    public TeachplanNode findTeachplanList(String courseId) {
        return teachplanMapper.selectList(courseId);
    }

    @Transactional
    public ResponseResult addTeachplan(Teachplan teachplan) {

        if (teachplan == null ||
                StringUtils.isEmpty(teachplan.getPname()) ||
                StringUtils.isEmpty(teachplan.getCourseid())) {
            ExceptionCast.cast(CommonCode.INVALID_PARAM);
        }
        //课程id
        String courseid = teachplan.getCourseid();
        //父结点的id
        String parentid = teachplan.getParentid();
        if (StringUtils.isEmpty(parentid)) {
            //获取课程的根结点
            parentid = getTeachplanRoot(courseid);
        }
        //查询根结点信息
        Optional<Teachplan> optional = teachplanRepository.findById(parentid);
        Teachplan teachplan1 = optional.get();
        //父结点的级别
        String parent_grade = teachplan1.getGrade();
        //创建一个新结点准备添加
        Teachplan teachplanNew = new Teachplan();
        //将teachplan的属性拷贝到teachplanNew中
        BeanUtils.copyProperties(teachplan, teachplanNew);
        //要设置必要的属性
        teachplanNew.setParentid(parentid);
        if (parent_grade.equals("1")) {
            teachplanNew.setGrade("2");
        } else {
            teachplanNew.setGrade("3");
        }
        teachplanNew.setStatus("0");//未发布
        teachplanRepository.save(teachplanNew);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    //获取课程的根结点
    public String getTeachplanRoot(String courseId) {
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (!optional.isPresent()) {
            return null;
        }
        CourseBase courseBase = optional.get();
        //调用dao查询teachplan表得到该课程的根结点（一级结点）
        List<Teachplan> teachplanList = teachplanRepository.findByCourseidAndParentid(courseId, "0");
        if (teachplanList == null || teachplanList.size() <= 0) {
            //新添加一个课程的根结点
            Teachplan teachplan = new Teachplan();
            teachplan.setCourseid(courseId);
            teachplan.setParentid("0");
            teachplan.setGrade("1");//一级结点
            teachplan.setStatus("0");
            teachplan.setPname(courseBase.getName());
            teachplanRepository.save(teachplan);
            return teachplan.getId();

        }
        //返回根结点的id
        return teachplanList.get(0).getId();

    }

    //查询课程视图
    public CourseView getCourseView(String courseId) {

        CourseView courseView = new CourseView();

        //查询课程信息
        Optional<CourseBase> optional = courseBaseRepository.findById(courseId);
        if (optional.isPresent()) {
            courseView.setCourseBase(optional.get());
        }

        //查询课程图片
        Optional<CoursePic> optional1 = coursePicRepository.findById(courseId);
        if (optional1.isPresent()) {
            courseView.setCoursePic(optional1.get());
        }

        //查询课程营销信息
        Optional<CourseMarket> optional2 = courseMarkertRepository.findById(courseId);
        if (optional2.isPresent()) {
            courseView.setCourseMarket(optional2.get());
        }

        //查询课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        courseView.setTeachplanNode(teachplanNode);

        return courseView;
    }

    //课程预览
    public CoursePublicResult preview(String id) {
        //查询课程信息
        CourseBase courseBase = this.getCourseById(id);
        //请求cms添加页面
        //远程调用cms接口
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre + id);//数据模型url
        cmsPage.setPageName(id + ".html");//页面名称
        cmsPage.setPageAliase(courseBase.getName());//页面别名，就是课程名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id

        CmsPageResult cmsPageResult = cmsPageClient.saveCmsPage(cmsPage);

        if (!cmsPageResult.isSuccess()) {
            //抛出异常
            return new CoursePublicResult(CommonCode.FAIL, null);
        }


        CmsPage cmsPage1 = cmsPageResult.getCmsPage();
        String pageId = cmsPage1.getPageId();
        //拼接页面预览url
        String previewUrl = "http://www.xuecheng.com/cms/preview/" + pageId;
        return new CoursePublicResult(CommonCode.SUCCESS, previewUrl);
    }

    //课程发布
    @Transactional
    public CoursePublicResult publicsh(String id) {
        //查询课程信息
        CourseBase courseBase = this.getCourseById(id);
        //请求cms添加页面
        //远程调用cms接口
        CmsPage cmsPage = new CmsPage();
        cmsPage.setSiteId(publish_siteId);//站点id
        cmsPage.setDataUrl(publish_dataUrlPre + id);//数据模型url
        cmsPage.setPageName(id + ".html");//页面名称
        cmsPage.setPageAliase(courseBase.getName());//页面别名，就是课程名称
        cmsPage.setPagePhysicalPath(publish_page_physicalpath);//页面物理路径
        cmsPage.setPageWebPath(publish_page_webpath);//页面webpath
        cmsPage.setTemplateId(publish_templateId);//页面模板id
        //远程调用Cms
        CmsPostPageResult cmsPostPageResult = cmsPageClient.postPageQuick(cmsPage);
        if (!cmsPostPageResult.isSuccess()) {
            return new CoursePublicResult(CommonCode.FAIL, null);
        }

        //保存课程索引信息
        //创建CoursePub对象
        CoursePub coursePub = creteCoursePub(id);
        //将CoursePub对象保存到书数据库
        saveCoursePub(id,coursePub);

        //缓存课程的信息

        //保存课程的发布状态为"已发布"
        CourseBase courseBase1= this.savestate(id);
        if(courseBase1==null){
            return new CoursePublicResult(CommonCode.FAIL, null);
        }
        //获取页面url
        String pageUrl = cmsPostPageResult.getPageUrl();
        return new CoursePublicResult(CommonCode.SUCCESS,pageUrl) ;
    }

    //更新课程发布的状态信息
    public CourseBase savestate(String id) {
        CourseBase courseById = this.getCourseById(id);
        courseById.setStatus("202002");
        courseBaseRepository.save(courseById);
        return courseById;

    }

    //将CoursePub对象保存到书数据库
    private CoursePub saveCoursePub(String id,CoursePub coursePub){

        //查询CoursePub信息
        Optional<CoursePub> optional= coursePubRepository.findById(id);
        CoursePub coursePubNew=null;
        if(optional.isPresent()){
            coursePubNew=optional.get();
        }else {
            coursePubNew=new CoursePub();

        }
        
         //将coursePub对象的信息保存到coursePub
         BeanUtils.copyProperties(coursePub,coursePubNew);
         coursePubNew.setId(id);
         //时间戳
         coursePubNew.setTimestamp(new Date());
         //发布时间
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        coursePubNew.setPubTime(date);
        return coursePubRepository.save(coursePubNew);

    }

    //创建course对象
    private CoursePub creteCoursePub(String courseId){
        CoursePub coursePub=new CoursePub();

        //查询课程信息
        CourseBase courseBase = getCourseById(courseId);
        //将courseBase的属性拷贝到CoursePub中
        BeanUtils.copyProperties(courseBase,coursePub);

        //查询课程图片信息
        CoursePic coursePic = findCoursePic(courseId);
        //将coursePic的属性拷贝到CoursePub中
        BeanUtils.copyProperties(coursePic,coursePub);

        //查询课程营销信息
        CourseMarket courseMarket = getcourseMarket(courseId);
        //将CourseMarket的属性拷贝到CoursePub中
        BeanUtils.copyProperties(courseMarket,coursePub);


        //查询课程计划信息
        TeachplanNode teachplanNode = teachplanMapper.selectList(courseId);
        String jsonString = JSON.toJSONString(teachplanNode);
        BeanUtils.copyProperties(jsonString,coursePub);

        return coursePub;
    }

    //保存课程计划和媒资关联的信息
    public ResponseResult savemdia(TeachplanMedia teachplanMedia) {
        if(teachplanMedia==null || StringUtils.isEmpty(teachplanMedia.getTeachplanId()) ){
            ExceptionCast.cast(CommonCode.INVALID_RARAM);
        }

        //校验课程计划是否是3级
        //课程计划
        String teachplanId=teachplanMedia.getTeachplanId();
        //查询课程计划
        Optional<Teachplan> optional = teachplanRepository.findById(teachplanId);
        if(!optional.isPresent()){
            ExceptionCast.cast(CommonCode.INVALID_RARAM);
        }

        //查询教学计划
        Teachplan teachplan = optional.get();
        //取出等级
        String grade = teachplan.getGrade();
        if(StringUtils.isEmpty(grade) || !grade.equals("3")){
            ExceptionCast.cast(CommonCode.FAIL);
        }

        Optional<TeachplanMedia> mediaOptional = teachplanMediaRepository.findById(teachplanId);
         //为空就添加信息,否则就更新信息
        TeachplanMedia teachplanMedia1=null;
         if(mediaOptional.isPresent()){
             teachplanMedia1= mediaOptional.get();
         }else {
             teachplanMedia1=new TeachplanMedia();
         }
         teachplanMedia1.setCourseId(teachplan.getCourseid());
         teachplanMedia1.setMediaId(teachplanMedia.getMediaId());
         teachplanMedia1.setMediaFileOriginalName(teachplanMedia.getMediaFileOriginalName());
         teachplanMedia1.setTeachplanId(teachplanId);
         teachplanMediaRepository.save(teachplanMedia1);
         return new ResponseResult(CommonCode.SUCCESS);
    }

    public QueryResponseResult<CourseInfo> findCoursePageList(String company_id, int page, int size, CourseListRequest courseListRequest) {
        if(courseListRequest==null){
            courseListRequest=new CourseListRequest();
        }
         //将公司id传入dao
        courseListRequest.setCompanyId(company_id);
        //分页
        PageHelper.startPage(page,size);
        //调用dao
        Page<CourseInfo> courseListPage = courseMapper.findCourseListPage(courseListRequest);
        List<CourseInfo> list = courseListPage.getResult();
        long total = courseListPage.getTotal();
        QueryResult<CourseInfo> courseIncfoQueryResult = new QueryResult<CourseInfo>();
        courseIncfoQueryResult.setList(list);
        courseIncfoQueryResult.setTotal(total);
        return new QueryResponseResult<CourseInfo>(CommonCode.SUCCESS,courseIncfoQueryResult);
    }
}