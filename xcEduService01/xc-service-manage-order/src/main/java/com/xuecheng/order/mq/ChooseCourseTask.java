package com.xuecheng.order.mq;

import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.order.config.RabbitMQConfig;
import com.xuecheng.order.service.TaskService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Component
public class ChooseCourseTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChooseCourseTask.class);

      @Autowired
      TaskService taskService;

      @RabbitListener(queues = RabbitMQConfig.XC_LEARNING_FINISHADDCHOOSECOURSE)


      public void receiveFinshChoosecourseTask(XcTask xctask){
       /* LOGGER.info("receiveChoosecourseTask");
        //接收到 的消息id
        String id=task.getId();
        //删除任务，添加历史任务*/
       if(xctask!=null || StringUtils.isNotEmpty(xctask.getId())) {
         taskService.finshTask(xctask.getId());
       }
      }

      //每隔1分钟扫描消息表，向mq发送消息
      @Scheduled(fixedDelay = 60000)
      public void sendChoosecourseTask(){
        //取出当前时间之前1分钟
        Calendar calendar=new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(GregorianCalendar.MINUTE,-1);
        Date time = calendar.getTime();
        List<XcTask> tasklist = taskService.findList(time, 100);
        //调用service发布消息 将添加课程信息发送给mq
        for (XcTask xcTask : tasklist) {
          //取任务
          if(taskService.getTask(xcTask.getId(),xcTask.getVersion())>0) {
            String exchange = xcTask.getMqExchange();
            String mqRoutingkey = xcTask.getMqRoutingkey();
            taskService.publish(xcTask, exchange, mqRoutingkey);
          }
        }



    }
}
