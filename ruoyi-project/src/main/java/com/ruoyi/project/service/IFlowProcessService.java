package com.ruoyi.project.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.flowable.domain.vo.FlowTaskVo;

import java.util.Map;

public interface IFlowProcessService {

     AjaxResult startProcessInstanceById( Map<String, Object> variables);

     AjaxResult processList(Integer pageNum, Integer pageSize);

     AjaxResult processListAllIn(Integer pageNum, Integer pageSize);

     AjaxResult processListAllDown(Integer pageNum, Integer pageSize);

     AjaxResult todoList(Integer pageNum,Integer pageSize);

     AjaxResult complete(FlowTaskVo taskVo,Long projectId,String procInsId);

     AjaxResult rejectProcess(FlowTaskVo taskVo,Long projectId,String procInsId);

     AjaxResult finishedList(Integer pageNum, Integer pageSize);

     AjaxResult onDoingList(Integer pageNum,Integer pageSize);

     void sendMail(String subject,String to,String context);


}
