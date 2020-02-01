package com.xuecheng.manage_course.controller;


import com.xuecheng.api.course.CourseControllerApi;
import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseBaseResult;
import com.xuecheng.framework.domain.course.response.CoursePublicResult;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.utils.XcOauth2Util;
import com.xuecheng.framework.web.BaseController;
import com.xuecheng.manage_course.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * @author Administrator
 * @version 1.0
 **/
@RestController
@RequestMapping("/course")
public class CourseController extends BaseController implements CourseControllerApi {

    @Autowired
    CourseService courseService;

    //分页查询课程列表
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult findCourseList(@PathVariable("page") int page, @PathVariable("size") int size, CourseListRequest courseListRequest) {
        return courseService.findList(page,size,courseListRequest);
    }

    //课程分类
    @GetMapping("/category/list")
    public CategoryNode findList() {
        return courseService.findList();
    }

    //新增课程
   @PutMapping("/add/base")
    public AddCourseResult add(@RequestBody CourseBase courseBase) {
        return null;
    }

    //获取课程信息
     @GetMapping("/coursebase/get/{courseId}")
    public CourseBase getCourseById(@PathVariable("courseId") String courseId) {
        return courseService.getCourseById(courseId);
    }

    //修改课程
    @PutMapping("/coursebase/update/{id}")
    public CourseBaseResult update(@PathVariable("id") String id, @RequestBody CourseBase courseBase) {

        return courseService.update(id,courseBase);
    }

    //获取课程营销信息
    @GetMapping("/coursemarket/get/{courseId}")
    public CourseMarket getCourseMarket(@PathVariable("courseId") String courseId) {
       return courseService.getcourseMarket(courseId);
    }

    //更新课程营销信息
    @PutMapping("/coursemarket/update/{id}")
    public ResponseResult updateMarket(@PathVariable("id")String id,@RequestBody CourseMarket courseMarket) {
        CourseMarket courseMarket1 = courseService.updateMarket(id, courseMarket);
        if(courseMarket1!=null){
            return new ResponseResult(CommonCode.SUCCESS);
        }
        else {
            return new ResponseResult(CommonCode.FAIL);
        }
    }

    //添加课程图片关联的信息
    @PostMapping("/coursepic/add")
    public ResponseResult addCoursePic(@RequestParam("courseId") String courseId,@RequestParam("pic") String pic) {
        return courseService.addCoursePic(courseId,pic);
    }

    //查询课程图片
    //当用户拥有course_pic_list权限时候方可访问此方法
    @PreAuthorize("hasAuthority('course_pic_list')")
    @GetMapping("/coursepic/list/{courseId}")
    public CoursePic findCoursePic(@PathVariable("courseId") String courseId) {
        return courseService.findCoursePic(courseId);
    }

    //删除课程图片
    @DeleteMapping("/coursepic/delete")
    public ResponseResult deleCoursePic(@RequestParam("courseId") String courseId) {
        return courseService.deleCoursePic(courseId);
    }

    //当用户拥有course_teachplan_list权限时候方可访问此方法
    @PreAuthorize("hasAuthority('course_teachplan_list')")
    @GetMapping("/teachplan/list/{courseId}")
    public TeachplanNode findTeachplanList(@PathVariable("courseId") String courseId) {
        return courseService.findTeachplanList(courseId);

    }

    @PostMapping("/teachplan/add")
    public ResponseResult addTeachplan(@RequestBody  Teachplan teachplan) {
        return courseService.addTeachplan(teachplan);
    }

    @GetMapping("/courseview/{courseId}")
    public CourseView getCourseView(@PathVariable("courseId") String courseId) {
        return courseService.getCourseView(courseId);
    }

    //课程预览
    @PostMapping("/preview/{id}")
    public CoursePublicResult preview(@PathVariable("id") String id) {
        return courseService.preview(id);
    }

    //课程发布
    @PostMapping("/publish/{id}")
    public CoursePublicResult publicsh(@PathVariable("id") String id) {
        return courseService.publicsh(id);
    }

    //保存课程计划和媒资关联的信息
    @PostMapping("/savemedia")
    public ResponseResult savemdeia(@RequestBody TeachplanMedia teachplanMedia) {
        return courseService.savemdia(teachplanMedia);
    }

    //查询我的课程
    @GetMapping("/coursebase/list/{page}/{size}")
    public QueryResponseResult<CourseInfo> findCoursePageList(@PathVariable("page")int page, @PathVariable("size")int size, CourseListRequest courseListRequest) {
        //从jwt令牌获取当前用户信息
        XcOauth2Util xcOauth2Util=new XcOauth2Util();
        XcOauth2Util.UserJwt userJwtFromHeader = xcOauth2Util.getUserJwtFromHeader(request);
        //当前用户所属的单位
        String companyId = userJwtFromHeader.getCompanyId();
        QueryResponseResult<CourseInfo> coursePageList = courseService.findCoursePageList(companyId, page, size, courseListRequest);
        return coursePageList;
    }
}
