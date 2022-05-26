package com.ruoyi.project.service.impl;

import java.util.*;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.domain.dto.FlowTaskDto;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.project.domain.*;
import com.ruoyi.project.mapper.ProjectFlowMapper;
import com.ruoyi.project.mapper.ProjectMapper;
import com.ruoyi.project.mapper.ProjectUserMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.ISysUserService;
import lombok.NonNull;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.mapper.ProjectHistoryMapper;
import com.ruoyi.project.service.IProjectHistoryService;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-03-24
 */
@Service
public class ProjectHistoryServiceImpl extends FlowServiceFactory implements IProjectHistoryService
{
    @Autowired
    private ProjectHistoryMapper projectHistoryMapper;

    @Autowired
    private ProjectMapper projectMapper;


    @Autowired
    private SysUserMapper sysUserMapper;

    @Resource
    private ISysUserService sysUserService;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ProjectHistory selectProjectHistoryById(Long id)
    {
        return projectHistoryMapper.selectProjectHistoryById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param projectHistory 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ProjectHistory>  selectProjectHistoryList(Integer pageNum, Integer pageSize)
    {
        List<ProjectHistory> histories = new ArrayList<>();

        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .orderByProcessInstanceStartTime()
                .desc();
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.finished().listPage(pageSize * (pageNum - 1), pageSize);
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        for (HistoricProcessInstance hisIns : historicProcessInstances)
        {
            ProjectHistory history = new ProjectHistory();
            List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
            String taskId = historicTaskInstance.get(0).getId();
            Map<String, Object> stringObjectMap = hisVar(taskId);
            Long projectId= Long.parseLong((String) stringObjectMap.get("projectId"));
            Long principalId = projectMapper.selectProjectById(projectId).getPrincipalId();
            if(userId==principalId)
            {
                history.setProjectId(projectId);
                Long startUserId =Long.parseLong( hisIns.getStartUserId());
                history.setUserId(startUserId);
                history.setMoney(Double.parseDouble((String) stringObjectMap.get("money")));
                histories.add(history);
            }
        }
        return histories;
    }

    public Map<String,Object> hisVar(String taskId){
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskId(taskId).singleResult();
        return historicTaskInstance.getProcessVariables();
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param projectHistory 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertProjectHistory(ProjectHistory projectHistory)
    {
        return projectHistoryMapper.insertProjectHistory(projectHistory);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param projectHistory 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateProjectHistory(ProjectHistory projectHistory)
    {
        return projectHistoryMapper.updateProjectHistory(projectHistory);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteProjectHistoryByIds(Long[] ids)
    {
        return projectHistoryMapper.deleteProjectHistoryByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteProjectHistoryById(Long id)
    {
        return projectHistoryMapper.deleteProjectHistoryById(id);
    }

    @Override
    public int deleteProjectHistoryByProc(String id)
    {
        historyService.deleteHistoricProcessInstance(id);
        ProjectHistory projectHistory = new ProjectHistory();
        projectHistory.setHisTaskId(id);
        return projectHistoryMapper.deleteProjectHistoryByHis(projectHistory);
    }

    @Override
    public AjaxResult finishedList(Integer pageNum, Integer pageSize) {
        Page<FlowTask> page = new Page<>();
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .includeProcessVariables()
                .finished()
                .taskAssignee(userId.toString())
                .orderByHistoricTaskInstanceEndTime()
                .desc();
        List<HistoricTaskInstance> historicTaskInstanceList = taskInstanceQuery.listPage(pageSize * (pageNum - 1), pageSize);
        List<FlowTask> hisTaskList = Lists.newArrayList();

        ProjectUserList userList = new ProjectUserList();

        for (HistoricTaskInstance histTask : historicTaskInstanceList) {
            FlowTask flowTask = new FlowTask();
            // 当前流程信息
            flowTask.setTaskId(histTask.getId());
            // 审批人员信息
            flowTask.setCreateTime(histTask.getCreateTime());
            flowTask.setFinishTime(histTask.getEndTime());
            flowTask.setDuration(getDate(histTask.getDurationInMillis()));
            flowTask.setProcDefId(histTask.getProcessDefinitionId());
            flowTask.setTaskDefKey(histTask.getTaskDefinitionKey());
            flowTask.setTaskName(histTask.getName());
            userList.setProcInsId(histTask.getProcessInstanceId());
            Map<String, Object> variables = processVariables(histTask.getId());
            String name = projectMapper.selectProjectById(Long.valueOf(String.valueOf(variables.get("projectId")))).getProjectName();
            flowTask.setProjectName(name);
//            if(userList1!=null)
//            {
//                flowTask.setProjectId(userList1.getProjectId());
//                flowTask.setProjectName(userList1.getProjectName());
//            }
//            else
//            {
//                Project project = projectMapper.selectProjectByProcInsId(histTask.getProcessInstanceId());
//                if(project!=null) {
//                    flowTask.setProjectId(project.getProjectId());
//                    flowTask.setProjectName(project.getProjectName());
//                }
//            }


            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(histTask.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(histTask.getProcessInstanceId());
            flowTask.setHisProcInsId(histTask.getProcessInstanceId());

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(histTask.getProcessInstanceId())
                    .singleResult();
            SysUser startUser = sysUserService.selectUserById(Long.parseLong(historicProcessInstance.getStartUserId()));
            flowTask.setStartUserId(startUser.getNickName());
            flowTask.setStartUserName(startUser.getNickName());
            flowTask.setStartDeptName(startUser.getDept().getDeptName());
            hisTaskList.add(flowTask);
        }
        page.setTotal(taskInstanceQuery.count());
        page.setRecords(hisTaskList);
        return AjaxResult.success(page);
    }

    public AjaxResult processVariablesOn(String taskId) {
        // 流程变量
            Map<String, Object> variables = taskService.getVariables(taskId);
            return AjaxResult.success(variables);
    }

    public Map<String, Object> processVariables(String taskId) {
        // 流程变量
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskId(taskId).singleResult();
        if (Objects.nonNull(historicTaskInstance)) {
            return historicTaskInstance.getProcessVariables();
        } else {
            Map<String, Object> variables = taskService.getVariables(taskId);
            return variables;
        }
    }

    private String getDate(long ms) {

        long day = ms / (24 * 60 * 60 * 1000);
        long hour = (ms / (60 * 60 * 1000) - day * 24);
        long minute = ((ms / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long second = (ms / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);

        if (day > 0) {
            return day + "天" + hour + "小时" + minute + "分钟";
        }
        if (hour > 0) {
            return hour + "小时" + minute + "分钟";
        }
        if (minute > 0) {
            return minute + "分钟";
        }
        if (second > 0) {
            return second + "秒";
        } else {
            return 0 + "秒";
        }
    }
}
