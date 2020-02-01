package com.xuecheng.api.course;

import com.xuecheng.framework.domain.course.*;
import com.xuecheng.framework.domain.course.ext.CategoryNode;
import com.xuecheng.framework.domain.course.ext.CourseInfo;
import com.xuecheng.framework.domain.course.ext.CourseView;
import com.xuecheng.framework.domain.course.ext.TeachplanNode;
import com.xuecheng.framework.domain.course.request.CourseListRequest;
import com.xuecheng.framework.domain.course.response.AddCourseResult;
import com.xuecheng.framework.domain.course.response.CourseBaseResult;
import com.xuecheng.framework.domain.course.response.CoursePublicResult;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

/**
 * Created by Administrator.
 */

@Api(value="课程管理接口",description = "课程管理接口，提供课程的增、删、改、查")
public interface CourseControllerApi {
    @ApiOperation("查询课程列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name="page",value ="页码",required =true,paramType ="path",dataType ="int" ),
            @ApiImplicitParam(name="size",value ="每页记录数 ",required =true,paramType ="path",dataType ="int" ),
    })
    public QueryResponseResult<CourseInfo> findCourseList(int page,int size,CourseListRequest courseListRequest);

    @ApiOperation("查询课程分类")
    public CategoryNode findList();

    @ApiOperation("新增课程")
    public AddCourseResult add(CourseBase courseBase);

    @ApiOperation("获取课程信息")
    public CourseBase getCourseById(String courseId);

    @ApiOperation("修改课程")
    public CourseBaseResult update(String courseId,CourseBase courseBase);

    @ApiOperation("获取课程营销信息")
    public CourseMarket getCourseMarket(String courseid);

    @ApiOperation("修改课程营销信息")
    public ResponseResult updateMarket(String id,CourseMarket courseMarket);

    @ApiOperation("课程计划查询")
    public TeachplanNode findTeachplanList(String courseId);

    @ApiOperation("添加课程计划")
    public ResponseResult addTeachplan(Teachplan teachplan);

    @ApiOperation("添加课程图片")
    public ResponseResult addCoursePic(String courseId,String pic);

    @ApiOperation("查询课程图片")
    public CoursePic findCoursePic(String courseId);

    @ApiOperation("删除课程图片")
    public ResponseResult deleCoursePic(String courseId);

    @ApiOperation("查询课程视图")
    public CourseView getCourseView(String courseId);

    @ApiOperation("课程预览")
    public CoursePublicResult preview(String id);

    @ApiOperation("课程发布")
    public CoursePublicResult publicsh(String id);

    @ApiOperation("保存课程计划和媒资关联的信息")
    public ResponseResult savemdeia(TeachplanMedia teachplanMedia);

 @ApiOperation("课程查询")
 public QueryResponseResult<CourseInfo> findCoursePageList(int page,
                                                       int size,
                                                       CourseListRequest courseListRequest);

}
