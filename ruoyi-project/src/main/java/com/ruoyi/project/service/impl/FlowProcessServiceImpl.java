package com.ruoyi.project.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.CustomException;
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
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.DelegationState;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
    private JavaMailSender javaMailSender;


    @Override
    public AjaxResult processList(Integer pageNum, Integer pageSize) {
        Page<FlowTask> page = new Page<>();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .orderByProcessInstanceStartTime()
                .desc();
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage(pageSize * (pageNum - 1), pageSize);
        page.setTotal(historicProcessInstanceQuery.count());
        List<FlowTask> flowList = new ArrayList<>();
        List<FlowTaskName> names = flowMapper.selectProjectName();
        Map<String, String> getName = new HashMap<>();
        for (FlowTaskName uname : names) {
            getName.put(uname.getDeployId(), uname.getProjectName());
        }
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            FlowTask flowTask = new FlowTask();
            flowTask.setCreateTime(hisIns.getStartTime());
            flowTask.setFinishTime(hisIns.getEndTime());
            flowTask.setProcInsId(hisIns.getId());

            // ????????????
            if (Objects.nonNull(hisIns.getEndTime())) {
                long time = hisIns.getEndTime().getTime() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            } else {
                long time = System.currentTimeMillis() - hisIns.getStartTime().getTime();
                flowTask.setDuration(getDate(time));
            }
            // ??????????????????
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(hisIns.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setCategory(pd.getCategory());

            // ?????????????????? todo: ??????????????????????????????
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).list();
            if (CollectionUtils.isNotEmpty(taskList)) {
                flowTask.setTaskId(taskList.get(0).getId());
                flowTask.setTaskName(taskList.get(0).getName());
            } else {
                List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
                flowTask.setTaskId(historicTaskInstance.get(0).getId());
            }
            if (getName.containsKey(flowTask.getDeployId())) {
                flowTask.setProjectName(getName.get(flowTask.getDeployId()));
            }

            flowList.add(flowTask);
        }
        page.setRecords(flowList);

        return AjaxResult.success(page);
    }

    @Override
    public AjaxResult processListAllIn(Integer pageNum, Integer pageSize) {
        Page<FlowTask> page = new Page<>();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .orderByProcessInstanceStartTime()
                .desc();
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage(pageSize * (pageNum - 1), pageSize);
        page.setTotal(historicProcessInstanceQuery.count());
        List<FlowTask> flowList = new ArrayList<>();
        List<FlowTaskName> names = flowMapper.selectProjectName();
        Map<String, String> getName = new HashMap<>();
        for (FlowTaskName uname : names) {
            getName.put(uname.getDeployId(), uname.getProjectName());
        }
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).list();
            if (CollectionUtils.isEmpty(taskList))
                continue;


            FlowTask flowTask = new FlowTask();
            flowTask.setCreateTime(hisIns.getStartTime());
            flowTask.setFinishTime(hisIns.getEndTime());
            flowTask.setProcInsId(hisIns.getId());
            SysUser user = sysUserService.selectUserById(Long.parseLong( hisIns.getStartUserId()));
            flowTask.setUserName(user.getNickName());
            // ????????????
            long time = System.currentTimeMillis() - hisIns.getStartTime().getTime();
            flowTask.setDuration(getDate(time));
            // ??????????????????
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(hisIns.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setCategory(pd.getCategory());

            // ?????????????????? todo: ??????????????????????????????
            flowTask.setTaskId(taskList.get(0).getId());
            flowTask.setTaskName(taskList.get(0).getName());
            if (getName.containsKey(flowTask.getDeployId())) {
                flowTask.setProjectName(getName.get(flowTask.getDeployId()));
            }

            flowList.add(flowTask);
        }
        page.setRecords(flowList);

        return AjaxResult.success(page);
    }

    @Override
    public AjaxResult processListAllDown(Integer pageNum, Integer pageSize) {
        Page<FlowTask> page = new Page<>();
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery()
                .orderByProcessInstanceStartTime()
                .desc();
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.finished().listPage(pageSize * (pageNum - 1), pageSize);
        page.setTotal(historicProcessInstanceQuery.count());
        List<FlowTask> flowList = new ArrayList<>();
        List<FlowTaskName> names = flowMapper.selectProjectName();
        Map<String, String> getName = new HashMap<>();
        for (FlowTaskName uname : names) {
            getName.put(uname.getDeployId(), uname.getProjectName());
        }
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            FlowTask flowTask = new FlowTask();
            flowTask.setCreateTime(hisIns.getStartTime());
            flowTask.setFinishTime(hisIns.getEndTime());
            flowTask.setProcInsId(hisIns.getId());

            SysUser user = sysUserService.selectUserById(Long.parseLong( hisIns.getStartUserId()));
            flowTask.setUserName(user.getNickName());

            // ????????????
            long time = hisIns.getEndTime().getTime() - hisIns.getStartTime().getTime();
            flowTask.setDuration(getDate(time));

            // ??????????????????
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(hisIns.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setCategory(pd.getCategory());

            // ??????taskId
            List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
            flowTask.setTaskId(historicTaskInstance.get(0).getId());
            Map<String, Object> stringObjectMap = hisVar(flowTask.getTaskId());
            flowTask.setMoney((String) stringObjectMap.get("money"));
            if (getName.containsKey(flowTask.getDeployId())) {
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
        if (roleIds != null) {
            TaskQuery desc = taskService.createTaskQuery()
                    .active()
                    .includeProcessVariables()
                    .taskCandidateGroup(roleIds[0].toString())
                    .orderByTaskCreateTime().desc();
            task1 = desc.listPage(pageNum - 1, pageSize);
        }


        List<Task> task2 = taskService.createTaskQuery()
                .active()
                .includeProcessVariables()
                .orderByTaskCreateTime().desc().listPage(pageNum - 1, pageSize);
        ProjectFlow flow = new ProjectFlow();
        for (Task task : task2) {
            if (task.getAssignee() != null)
                continue;
            flow.setProcDefId(task.getProcessDefinitionId());
            ProjectFlow flow1 = flowMapper.selectProjectFlow(flow);
            Project project = projectMapper.selectProjectById(flow1.getProjectId());
            if (project.getPrincipalId() == user.getUserId())
                task1.add(task);
        }
        List<Task> taskList = new ArrayList<>();
        if (page1 != null) {
            taskList.addAll(page1);
        }
        if (task1.size() > 0)
            taskList.addAll(task1);
        page.setTotal(taskList.size());
        List<FlowTask> flowList = new ArrayList<>();
        for (Task task : taskList) {

            FlowTask flowTask = new FlowTask();
            // ??????????????????
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
            if (lists.size() != 0) {
                flowTask.setProjectName(lists.get(0).getProjectName());
                flowTask.setProjectId(lists.get(0).getProjectId());
            }


            // ?????????????????????
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
    public AjaxResult complete(FlowTaskVo taskVo, Long projectId, String procInsId) {
        Task task = taskService.createTaskQuery().taskId(taskVo.getTaskId()).singleResult();
        if (Objects.isNull(task)) {
            return AjaxResult.error("???????????????");
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
                String money = (String) historicTaskInstance.getProcessVariables().get("money");
                Double mon = Double.parseDouble(money);
                project.setExpensesLeft(project.getExpensesLeft() - mon);
                projectMapper.updateProject(project);
                projectUserMapper.deleteProjectUser(projectId, Long.parseLong(historicProcessInstance.getStartUserId()));
                String context = "????????????"+project.getProjectName()+"????????????????????????";
                sendMail("????????????",historicProcessInstance.getStartUserId(),context);
            }


        }
        return AjaxResult.success();
    }


    @Override
    public AjaxResult rejectProcess(FlowTaskVo taskVo, Long projectId, String procInsId) {
        List<Task> task = taskService.createTaskQuery().processInstanceId(procInsId).list();
        if (CollectionUtils.isEmpty(task)) {
            throw new CustomException("??????????????????????????????????????????????????????");
        }

        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        ProcessInstance processInstance =
                runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        String startUserId = processInstance.getStartUserId();
        String commit = taskVo.getComment();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        if (Objects.nonNull(bpmnModel)) {
            Process process = bpmnModel.getMainProcess();
            List<EndEvent> endNodes = process.findFlowElementsOfType(EndEvent.class, false);
            if (CollectionUtils.isNotEmpty(endNodes)) {
                Authentication.setAuthenticatedUserId(loginUser.getUserId().toString());
                String endId = endNodes.get(0).getId();
                List<Execution> executions =
                        runtimeService.createExecutionQuery().parentId(processInstance.getProcessInstanceId()).list();
                List<String> executionIds = new ArrayList<>();
                executions.forEach(execution -> executionIds.add(execution.getId()));
                runtimeService.createChangeActivityStateBuilder().moveExecutionsToSingleActivityId(executionIds,
                        endId).changeState();
            }
        }
        projectUserMapper.deleteProjectUserByProc(procInsId);
        Project project = projectMapper.selectProjectById(projectId);
        String context = "????????????"+project.getProjectName()+"??????????????????????????????"+commit+"???";
        sendMail("??????",startUserId,context);
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
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.finished().listPage(pageSize * (pageNum - 1), pageSize);
        page.setTotal(historicProcessInstanceQuery.count());
        List<FlowTask> flowList = new ArrayList<>();
        List<FlowTaskName> names = flowMapper.selectProjectName();
        Map<String, String> getName = new HashMap<>();
        for (FlowTaskName uname : names) {
            getName.put(uname.getDeployId(), uname.getProjectName());
        }
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            FlowTask flowTask = new FlowTask();
            flowTask.setCreateTime(hisIns.getStartTime());
            flowTask.setFinishTime(hisIns.getEndTime());
            flowTask.setProcInsId(hisIns.getId());

            // ????????????
            long time = hisIns.getEndTime().getTime() - hisIns.getStartTime().getTime();
            flowTask.setDuration(getDate(time));

            // ??????????????????
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(hisIns.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setCategory(pd.getCategory());

            // ??????taskId
            List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
            flowTask.setTaskId(historicTaskInstance.get(0).getId());
            if (getName.containsKey(flowTask.getDeployId())) {
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
        List<HistoricProcessInstance> historicProcessInstances = historicProcessInstanceQuery.listPage(pageSize * (pageNum - 1), pageSize);
        page.setTotal(historicProcessInstanceQuery.count());
        List<FlowTask> flowList = new ArrayList<>();
        List<FlowTaskName> names = flowMapper.selectProjectName();
        Map<String, String> getName = new HashMap<>();
        for (FlowTaskName uname : names) {
            getName.put(uname.getDeployId(), uname.getProjectName());
        }
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            List<Task> taskList = taskService.createTaskQuery().processInstanceId(hisIns.getId()).list();
            if (CollectionUtils.isEmpty(taskList))
                continue;


            FlowTask flowTask = new FlowTask();
            flowTask.setCreateTime(hisIns.getStartTime());
            flowTask.setFinishTime(hisIns.getEndTime());
            flowTask.setProcInsId(hisIns.getId());

            // ????????????
            long time = System.currentTimeMillis() - hisIns.getStartTime().getTime();
            flowTask.setDuration(getDate(time));
            // ??????????????????
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(hisIns.getProcessDefinitionId())
                    .singleResult();
            flowTask.setDeployId(pd.getDeploymentId());
            flowTask.setCategory(pd.getCategory());

            // ?????????????????? todo: ??????????????????????????????
            flowTask.setTaskId(taskList.get(0).getId());
            flowTask.setTaskName(taskList.get(0).getName());
            if (getName.containsKey(flowTask.getDeployId())) {
                flowTask.setProjectName(getName.get(flowTask.getDeployId()));
            }

            flowList.add(flowTask);
        }
        page.setRecords(flowList);

        return AjaxResult.success(page);
    }

    @Override
    public void sendMail(String subject,String to, String context) {
        SimpleMailMessage message = new SimpleMailMessage();
        String username = "2712544015@qq.com";
        //????????????
        message.setSubject("??????");
        //?????????
        message.setFrom(username);
        SysUser user = sysUserService.selectUserById(Long.parseLong(to));
        //?????????
        message.setTo(user.getEmail());
        //??????
        message.setText(context);
        message.setSentDate(new Date());
        //????????????
        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public Map<String,Object> hisVar(String taskId){
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskId(taskId).singleResult();
        return historicTaskInstance.getProcessVariables();
    }

    public AjaxResult processVariables(String taskId) {
        // ????????????
        HistoricTaskInstance historicTaskInstance = historyService.createHistoricTaskInstanceQuery().includeProcessVariables().finished().taskId(taskId).singleResult();
        if (Objects.nonNull(historicTaskInstance)) {
            return AjaxResult.success(historicTaskInstance.getProcessVariables());
        } else {
            Map<String, Object> variables = taskService.getVariables(taskId);
            return AjaxResult.success(variables);
        }
    }

    @Override
    public AjaxResult startProcessInstanceById(Map<String, Object> variables) {
        String procDefId = (String) variables.get("procDefId");
        Long projectId = Long.parseLong(variables.get("projectId").toString());
        try {
            SysUser sysUser = SecurityUtils.getLoginUser().getUser();
            Project project = projectMapper.selectProjectById(projectId);


            ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(procDefId)
                    .latestVersion().singleResult();
            if (Objects.nonNull(processDefinition) && processDefinition.isSuspended()) {
                return AjaxResult.error("??????????????????,??????????????????");
            }
//           variables.put("skip", true);
//           variables.put(ProcessConstants.FLOWABLE_SKIP_EXPRESSION_ENABLED, true);
            // ?????????????????????Id????????????


            identityService.setAuthenticatedUserId(sysUser.getUserId().toString());
            variables.put(ProcessConstants.PROCESS_INITIATOR, "");
            ProcessInstance processInstance = runtimeService.startProcessInstanceById(procDefId, variables);
            // ????????????????????????????????????????????????????????? todo:?????????????????????????????????????????????????????????
            Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId()).singleResult();
            if (Objects.nonNull(task)) {
                taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.NORMAL.getType(), sysUser.getNickName() + "??????????????????");
                taskService.complete(task.getId(), variables);
            }

            ProjectUserList userList = new ProjectUserList();
            userList.setUserId(sysUser.getUserId());
            userList.setProjectId(projectId);
            userList.setProcInsId(task.getProcessInstanceId());
            userList.setProjectName(project.getProjectName());
            projectUserMapper.insertProjectUser(userList);
            return AjaxResult.success("??????????????????");
        }  catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("??????????????????");
        }
    }


    private String getDate(long ms) {

        long day = ms / (24 * 60 * 60 * 1000);
        long hour = (ms / (60 * 60 * 1000) - day * 24);
        long minute = ((ms / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long second = (ms / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - minute * 60);

        if (day > 0) {
            return day + "???" + hour + "??????" + minute + "??????";
        }
        if (hour > 0) {
            return hour + "??????" + minute + "??????";
        }
        if (minute > 0) {
            return minute + "??????";
        }
        if (second > 0) {
            return second + "???";
        } else {
            return 0 + "???";
        }
    }
}
