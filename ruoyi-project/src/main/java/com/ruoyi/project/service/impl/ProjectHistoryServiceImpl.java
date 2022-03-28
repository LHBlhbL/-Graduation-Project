package com.ruoyi.project.service.impl;

import java.util.*;

import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.project.domain.FlowTask;
import com.ruoyi.project.domain.FlowTaskName;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.mapper.ProjectFlowMapper;
import com.ruoyi.project.mapper.ProjectMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import lombok.NonNull;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.mapper.ProjectHistoryMapper;
import com.ruoyi.project.domain.ProjectHistory;
import com.ruoyi.project.service.IProjectHistoryService;

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
    private ProjectFlowMapper flowMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

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
    public List<ProjectHistory> selectProjectHistoryList(ProjectHistory projectHistory)
    {

        List<ProjectHistory> projectHistories = projectHistoryMapper.selectProjectHistoryList(projectHistory);
        Project project = new Project();
        SysUser user = new SysUser();
        for(ProjectHistory projectHistory1:projectHistories)
        {
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .orderByProcessInstanceStartTime()
                    .processInstanceId(projectHistory1.getHisTaskId())
                    .finished()
                    .desc()
                    .singleResult();
            FlowTask flowTask = new FlowTask();
            project = projectMapper.selectProjectById(projectHistory1.getProjectId());
            user = sysUserMapper.selectUserById(projectHistory1.getUserId());
            flowTask.setUserName(user.getUserName());
            flowTask.setProjectName(project.getProjectName());
            if(historicProcessInstance!=null)
            {
                flowTask.setDeployId(historicProcessInstance.getDeploymentId());
            }
            List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(projectHistory1.getHisTaskId()).orderByHistoricTaskInstanceEndTime().desc().list();
            if(historicTaskInstance.size()!=0)
            {
                flowTask.setTaskId(historicTaskInstance.get(0).getId());
            }

            projectHistory1.setFlowTask(flowTask);
        }

            return projectHistories;
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

    public int deleteProjectHistoryByProc(String id)
    {
        historyService.deleteHistoricProcessInstance(id);
        ProjectHistory projectHistory = new ProjectHistory();
        projectHistory.setHisTaskId(id);
        return projectHistoryMapper.deleteProjectHistoryByHis(projectHistory);
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
