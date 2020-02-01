package com.xuecheng.order.service;

import com.github.pagehelper.Page;
import com.xuecheng.framework.domain.task.XcTask;
import com.xuecheng.framework.domain.task.XcTaskHis;
import com.xuecheng.order.dao.XcTaskHisRepository;
import com.xuecheng.order.dao.XcTaskRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    XcTaskRepository xcTaskRepository;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    XcTaskHisRepository xcTaskHisRepository;

    //取出前n条任务,取出指定时间之前处理的任务
    public List<XcTask> findList(Date updatetTime,int n){
        //设置分页参数
        Pageable pageable=new PageRequest(0,n);
        //查询前n条任务
        Page<XcTask> byUpdateTimeBefore = xcTaskRepository.findByUpdateTimeBefore(pageable, updatetTime);
        return byUpdateTimeBefore.getResult();
    }

    //发布消息
    public void publish(XcTask xcTask,String ex,String routingKey){
        Optional<XcTask> optional= xcTaskRepository.findById(xcTask.getId());
        if(optional.isPresent()){
            rabbitTemplate.convertAndSend(ex,routingKey,xcTask);
            //更新任务时间
            XcTask xcTask1 = optional.get();
            xcTask1.setUpdateTime(new Date());
            xcTaskRepository.save(xcTask1);
        }
    }

    //获取任务
    @Transactional
    public int getTask(String id,int version){
        //通过乐观锁的方式类更新数据表,如果结果大于0说明取到任务
        int count = xcTaskRepository.updateTaskVersion(id, version);
        return count;
    }

    //完成任务
    @Transactional
    public void finshTask(String taskid){
        Optional<XcTask> optional = xcTaskRepository.findById(taskid);
        if(optional.isPresent()){
            //如果存在就它添加到历史任务表,然后删除当前任务

            //当前任务
            XcTask xcTask = optional.get();
            //历史任务
            XcTaskHis xcTaskHis=new XcTaskHis();
            BeanUtils.copyProperties(xcTask,xcTaskHis);
            //保存历史任务
            xcTaskHisRepository.save(xcTaskHis);
            //删除当前任务
            xcTaskRepository.delete(xcTask);

        }
    }
}
