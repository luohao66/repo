package com.xuecheng.framework.domain.course.response;

import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.framework.model.response.ResultCode;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class CoursePublicResult extends ResponseResult {

       String previewUrl;
       public CoursePublicResult(ResultCode code,String previewUrl){
           super(code);
           this.previewUrl=previewUrl;
       }
}
