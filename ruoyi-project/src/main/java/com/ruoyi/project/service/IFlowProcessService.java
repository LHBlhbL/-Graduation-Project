package com.ruoyi.project.service;

import com.ruoyi.common.core.domain.AjaxResult;

import java.util.Map;

public interface IFlowProcessService {

     AjaxResult startProcessInstanceById(String procDefId, Map<String, Object> variables);

     AjaxResult processList(Integer pageNum, Integer pageSize);

     AjaxResult todoList(Integer pageNum,Integer pageSize);

}
