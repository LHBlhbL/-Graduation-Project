package com.ruoyi.project.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.common.constant.ProcessConstants;
import com.ruoyi.flowable.common.enums.FlowComment;
import com.ruoyi.flowable.domain.dto.FlowTaskDto;
import com.ruoyi.flowable.domain.vo.FlowTaskVo;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.project.domain.*;
import com.ruoyi.project.mapper.ProjectFlowMapper;
import com.ruoyi.project.mapper.ProjectHistoryMapper;
import com.ruoyi.project.mapper.ProjectMapper;
import com.ruoyi.project.mapper.ProjectUserMapper;
import com.ruoyi.project.service.IFlowProcessService;
import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class FlowProcessServiceImpl extends FlowServiceFactory implements IFlowProcessService {


    @Resource
    private ISysUserService sysUserService;

    @Autowired
    private ProjectFlowMapper flowMapper;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Autowired
    private ProjectHistoryMapper projectHistoryMapper;


    @Override
    public AjaxResult processList(Integer pageNum, Integer pageSize) {
        Page<FlowTask> page = new Page<>();
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .startedBy(userId.toString())
                .orderByProcessInstanceStartTime()
                .desc();
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage(pageNum - 1, pageSize);
        page.setTotal(historicProcessInstanceQuery.count());
        List<FlowTask> flowList = new ArrayList<>();
      List<FlowTaskName> names = flowMapper.selectProjectName();
      Map<String,String> getName = new HashMap<>();
      for(FlowTaskName uname:names)
      {
          getName.put(uname.getDeployId(),uname.getProjectName());
      }
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            FlowTask flowTask = new FlowTask();
            flowTask.setCreateTime(hisIns.getStartTime());
            flowTask.setFinishTime(hisIns.getEndTime());
            flowTask.setProcInsId(hisIns.getId());

            // 计算耗时
            if (Objects.nonNull(hisIns.getEndTime())) {
                long time = hisIns.getEndTime().getTime() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            } else {
                long time = System.currentTimeMillis() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            }
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(hisIns.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setCategory(pd.getCategory());

            // 当前所处流程 todo: 本地启动放开以下注释
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).list();
            if (CollectionUtils.isNotEmpty(taskList)) {
                flowTask.setTaskId(taskList.get(0).getId());
                flowTask.setTaskName(taskList.get(0).getName());
            } else {
                List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
                flowTask.setTaskId(historicTaskInstance.get(0).getId());
            }
            if(getName.containsKey(flowTask.getDeployId()))
            {
                flowTask.setProjectName(getName.get(flowTask.getDeployId()));
            }

            flowList.add(flowTask);
        }
        page.setRecords(flowList);

        return AjaxResult.success(page);
    }

    @Override
    public AjaxResult todoList(Integer pageNum, Integer pageSize) {
        Page<FlowTask> page = new Page<>();
        SysUser user = SecurityUtils.getLoginUser().getUser();
        List<Task> page1 = taskService.createTaskQuery()
                .active()
                .includeProcessVariables()
                .taskCandidateOrAssigned(user.getUserId().toString())
                .orderByTaskCreateTime().desc().listPage(pageNum - 1, pageSize);
        Long[] roleIds = user.getRoleIds();
        List<Task> task1 = new ArrayList<>();
        if(roleIds!=null)
        {
            TaskQuery desc = taskService.createTaskQuery()
                    .active()
                    .includeProcessVariables()
                    .taskCandidateGroup(roleIds[0].toString())
                    .orderByTaskCreateTime().desc();
             task1 = desc.listPage(pageNum - 1, pageSize);
        }


        List<Task>task2 = taskService.createTaskQuery()
                .active()
                .includeProcessVariables()
                .orderByTaskCreateTime().desc().listPage(pageNum-1,pageSize);
        ProjectFlow flow = new ProjectFlow();
        for(Task task:task2)
        {
            if(task.getAssignee()!=null)
                continue;
            flow.setProcDefId(task.getProcessDefinitionId());
            ProjectFlow flow1 = flowMapper.selectProjectFlow(flow);
            Project project = projectMapper.selectProjectById(flow1.getProjectId());
            if(project.getPrincipalId()== user.getUserId())
                task1.add(task);
        }
        List<Task> taskList = new ArrayList<>();
        if(page1!=null)
        {
            taskList.addAll(page1);
        }
        if(task1.size()>0)
            taskList.addAll(task1);
        page.setTotal(taskList.size());
        List<FlowTask> flowList = new ArrayList<>();
        for (Task task : taskList) {

            FlowTask flowTask = new FlowTask();
            // 流程定义信息
            flowTask.setTaskId(task.getId());
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());

            ProjectUserList userList = new ProjectUserList();
            userList.setProcInsId(task.getProcessInstanceId());


            List<ProjectUserList> lists = projectUserMapper.selectProjectUserList(userList);
            if(lists.size()!=0)
            {
                flowTask.setProjectName(lists.get(0).getProjectName());
                flowTask.setProjectId(lists.get(0).getProjectId());
            }


            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();

            SysUser startUser = sysUserService.selectUserById(Long.parseLong(historicProcessInstance.getStartUserId()));
            flowTask.setStartUserName(startUser.getNickName());
            flowList.add(flowTask);
        }
        page.setRecords(flowList);
        return AjaxResult.success(page);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public AjaxResult complete(FlowTaskVo taskVo,Long projectId,String procInsId) {
        Task task = taskService.createTaskQuery().taskId(taskVo.getTaskId()).singleResult();
        if (Objects.isNull(task)) {
            return AjaxResult.error("任务不存在");
        }
        if (DelegationState.PENDING.equals(task.getDelegationState())) {
            taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.DELEGATE.getType(), taskVo.getComment());
            taskService.resolveTask(taskVo.getTaskId(), taskVo.getValues());
        } else {
            taskService.addComment(taskVo.getTaskId(), taskVo.getInstanceId(), FlowComment.NORMAL.getType(), taskVo.getComment());
            Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
            taskService.setAssignee(taskVo.getTaskId(), userId.toString());
            taskService.complete(taskVo.getTaskId(), taskVo.getValues());
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(procInsId).list();
            if (CollectionUtils.isEmpty(taskList)) {
                HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskId(taskVo.getTaskId()).singleResult();
                Project project = projectMapper.selectProjectById(projectId);
                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery().processInstanceId(procInsId).singleResult();
                ProjectHistory projectHistory = new ProjectHistory();
                projectHistory.setProjectId(projectId);
                projectHistory.setHisTaskId(procInsId);
                projectHistory.setUserId(Long.parseLong(historicProcessInstance.getStartUserId()));
                String money = (String) historicTaskInstance.getProcessVariables().get("money");
                Double mon =Double.parseDouble(money);
                projectHistory.setMoney(mon);
                project.setExpensesLeft(project.getExpensesLeft()-mon);
                projectMapper.updateProject(project);
                projectUserMapper.deleteProjectUser(projectId,Long.parseLong( historicProcessInstance.getStartUserId()));
                projectHistoryMapper.insertProjectHistory(projectHistory);
            }

//            HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskId(taskVo.getTaskId()).singleResult();
//            if (Objects.nonNull(historicTaskInstance)) {
//                HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
//                        .processInstanceId(task.getProcessInstanceId())
//                        .singleResult();
//                Map<String, Object> processVariables = historicTaskInstance.getProcessVariables();
//                ProjectHistory projectHistory = new ProjectHistory();
//                String time = (String) processVariables.get("money");
//                Project project = projectMapper.selectProjectById(projectId);
//                project.setExpensesLeft(project.getExpensesLeft()- Long.parseLong(time));
//                projectMapper.updateProject(project);
//                projectUserMapper.deleteProjectUser(projectId,Long.parseLong( historicProcessInstance.getStartUserId()));
//                projectHistory.setProjectId(projectId);
//                projectHistory.setHisTaskId(historicTaskInstance.getProcessInstanceId());
//                projectHistory.setMoney(Long.parseLong(time));
//                projectHistory.setUserId(Long.parseLong( historicProcessInstance.getStartUserId()));
//                projectHistoryMapper.insertProjectHistory(projectHistory);
//            }
        }
        return AjaxResult.success();
    }

    @Override
    public AjaxResult finishedList(Integer pageNum, Integer pageSize) {
        Page<FlowTask> page = new Page<>();
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .startedBy(userId.toString())
                .orderByProcessInstanceStartTime()
                .desc();
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.finished().listPage(pageNum - 1, pageSize);
        page.setTotal(historicProcessInstanceQuery.count());
        List<FlowTask> flowList = new ArrayList<>();
        List<FlowTaskName> names = flowMapper.selectProjectName();
        Map<String,String> getName = new HashMap<>();
        for(FlowTaskName uname:names)
        {
            getName.put(uname.getDeployId(),uname.getProjectName());
        }
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            FlowTask flowTask = new FlowTask();
            flowTask.setCreateTime(hisIns.getStartTime());
            flowTask.setFinishTime(hisIns.getEndTime());
            flowTask.setProcInsId(hisIns.getId());

            // 计算耗时
            long time = hisIns.getEndTime().getTime() - hisIns.getStartTime().getTime();
            flowTask.setDuration(getDate(time));

            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(hisIns.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setCategory(pd.getCategory());

            // 设置taskId
            List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
            flowTask.setTaskId(historicTaskInstance.get(0).getId());
            if(getName.containsKey(flowTask.getDeployId()))
            {
                flowTask.setProjectName(getName.get(flowTask.getDeployId()));
            }

            flowList.add(flowTask);
        }
        page.setRecords(flowList);

        return AjaxResult.success(page);
    }

    @Override
    public AjaxResult onDoingList(Integer pageNum, Integer pageSize) {
        Page<FlowTask> page = new Page<>();
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .startedBy(userId.toString())
                .orderByProcessInstanceStartTime()
                .desc();
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage(pageNum - 1, pageSize);
        page.setTotal(historicProcessInstanceQuery.count());
        List<FlowTask> flowList = new ArrayList<>();
        List<FlowTaskName> names = flowMapper.selectProjectName();
        Map<String,String> getName = new HashMap<>();
        for(FlowTaskName uname:names)
        {
            getName.put(uname.getDeployId(),uname.getProjectName());
        }
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).list();
            if (CollectionUtils.isEmpty(taskList))
                continue;


            FlowTask flowTask = new FlowTask();
            flowTask.setCreateTime(hisIns.getStartTime());
            flowTask.setFinishTime(hisIns.getEndTime());
            flowTask.setProcInsId(hisIns.getId());

            // 计算耗时
                long time = System.currentTimeMillis() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(hisIns.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setCategory(pd.getCategory());

            // 当前所处流程 todo: 本地启动放开以下注释
            flowTask.setTaskId(taskList.get(0).getId());
            flowTask.setTaskName(taskList.get(0).getName());
            if(getName.containsKey(flowTask.getDeployId()))
            {
                flowTask.setProjectName(getName.get(flowTask.getDeployId()));
            }

            flowList.add(flowTask);
        }
        page.setRecords(flowList);

        return AjaxResult.success(page);
    }

    public AjaxResult processVariables(String taskId) {
        // 流程变量
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskId(taskId).singleResult();
        if (Objects.nonNull(historicTaskInstance)) {
            return AjaxResult.success(historicTaskInstance.getProcessVariables());
        } else {
            Map<String, Object> variables = taskService.getVariables(taskId);
            return AjaxResult.success(variables);
        }
    }

    @Override
    public AjaxResult startProcessInstanceById(String procDefId, Map<String, Object> variables,Long projectId) {
        try {
            SysUser sysUser = SecurityUtils.getLoginUser().getUser();
            Project project = projectMapper.selectProjectById(projectId);
            Object money = variables.get("money");
            if(money!=null)
            {
                Double expensesLeft = project.getExpensesLeft();
                Double now = Double.parseDouble((String) money);
                if(expensesLeft<now)
                {
                    return AjaxResult.error("项目经费不足");
                }

            }
            ProjectUserList projectUserList = new ProjectUserList();
            projectUserList.setProjectId(projectId);
            projectUserList.setUserId(sysUser.getUserId());
            List<ProjectUserList> projectUserLists = projectUserMapper.selectProjectUserList(projectUserList);
            if(projectUserLists.size()!=0)
                throw new NullPointerException();



            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId)
                    .latestVersion().singleResult();
            if (Objects.nonNull(processDefinition) && processDefinition.isSuspended()) {
                return AjaxResult.error("流程已被挂起,请先激活流程");
            }
//           variables.put("skip", true);
//           variables.put(ProcessConstants.FLOWABLE_SKIP_EXPRESSION_ENABLED, true);
            // 设置流程发起人Id到流程中


            identityService.setAuthenticatedUserId(sysUser.getUserId().toString());
            variables.put(ProcessConstants.PROCESS_INITIATOR, "");
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, variables);
            // 给第一步申请人节点设置任务执行人和意见 todo:第一个节点不设置为申请人节点有点问题？
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
            if (Objects.nonNull(task)) {
                taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.NORMAL.getType(), sysUser.getNickName() + "发起流程申请");
                taskService.complete(task.getId(), variables);
            }

            ProjectUserList userList = new ProjectUserList();
            userList.setUserId(sysUser.getUserId());
            userList.setProjectId(projectId);
            userList.setProcInsId(task.getProcessInstanceId());
            userList.setProjectName(project.getProjectName());
            projectUserMapper.insertProjectUser(userList);
            return AjaxResult.success("报销启动成功");
        }
        catch (NullPointerException exception)
        {
            exception.printStackTrace();
            return AjaxResult.error("报销流程进行中");
        }
        catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("流程启动错误");
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
