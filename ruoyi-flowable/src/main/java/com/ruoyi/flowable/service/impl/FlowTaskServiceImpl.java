package com.ruoyi.flowable.service.impl;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.Lists;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysRole;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.domain.dto.FlowCommentDto;
import com.ruoyi.flowable.domain.dto.FlowTaskDto;
import com.ruoyi.flowable.domain.vo.FlowTaskVo;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.flowable.flow.CustomProcessDiagramGenerator;
import com.ruoyi.flowable.flow.FlowableUtils;
import com.ruoyi.flowable.service.IFlowTaskService;
import com.ruoyi.flowable.service.ISysDeployFormService;
import com.ruoyi.system.domain.SysForm;
import com.ruoyi.system.service.ISysRoleService;
import com.ruoyi.system.service.ISysUserService;
import liquibase.pro.packaged.S;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.FlowableObjectNotFoundException;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.task.Comment;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.history.HistoricIdentityLink;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.api.history.HistoricTaskInstanceQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author XuanXuan
 * @date 2021-04-03
 **/
@Service
@Slf4j
public class FlowTaskServiceImpl extends FlowServiceFactory implements IFlowTaskService {

    @Resource
    private ISysUserService sysUserService;


    @Resource
    private ISysRoleService sysRoleService;


    @Resource
    private ISysDeployFormService sysInstanceFormService;

    /**
     * 完成任务
     *
     * @param task 请求实体参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void completeTask(FlowTaskVo task) {
        // 添加意见
        if (StringUtils.isNotBlank(task.getInstanceId()) && StringUtils.isNotBlank(task.getComment())) {
            taskService.addComment(task.getTaskId(), task.getInstanceId(), "1", task.getComment());
        }
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        // 变量信息
        if (task.getValues() != null && task.getValues().size() > 0) {
            Map<String, Object> map = (Map<String, Object>) task.getValues().get("mainTable");
            map.put("FormOrigData" + task.getTaskId(), JSON.toJSONString(task.getValues()));
//            List<TaskDto> list = getTaskFormList(task.getTaskId());
//            // 节点上挂载的表单ID
//            if (!CollectionUtils.isEmpty(list)) {
//                List<Object> formList = new ArrayList<>();
//                for (TaskDto taskDto : list) {
//                    formList.addAll(taskDto.getFormList());
//                }
//
//                List<String> strs = new ArrayList<>();
//                for (int k = 0; k < formList.size(); k++) {
//                    Map<String, Object> map1 = (Map<String, Object>) formList.get(k);
//                    strs.add(map1.get("id").toString());
//                }
//            }
            taskService.setVariables(task.getTaskId(), map);
        }
        // 设置任务处理人员
        taskService.setAssignee(task.getTaskId(), userId.toString());
        // 提交任务
        taskService.complete(task.getTaskId());

    }

    /**
     * 驳回任务
     *
     * @param flowTaskVo
     */
    @Override
    public void taskReject(FlowTaskVo flowTaskVo) {
        if (taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult().isSuspended()) {
            throw new CustomException("任务处于挂起状态");
        }
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // 获取全部节点列表，包含子节点
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // 获取当前任务节点元素
        FlowElement source = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // 类型为用户节点
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    // 获取节点信息
                    source = flowElement;
                }
            }
        }

        // 目的获取所有跳转到的节点 targetIds
        // 获取当前节点的所有父级用户任务节点
        // 深度优先算法思想：延边迭代深入
        List<UserTask> parentUserTaskList = FlowableUtils.iteratorFindParentUserTasks(source, null, null);
        if (parentUserTaskList == null || parentUserTaskList.size() == 0) {
            throw new CustomException("当前节点为初始任务节点，不能驳回");
        }
        // 获取活动 ID 即节点 Key
        List<String> parentUserTaskKeyList = new ArrayList<>();
        parentUserTaskList.forEach(item -> parentUserTaskKeyList.add(item.getId()));
        // 获取全部历史节点活动实例，即已经走过的节点历史，数据采用开始时间升序
        List<HistoricTaskInstance> historicTaskInstanceList = historyService.createHistoricTaskInstanceQuery().processInstanceId(task.getProcessInstanceId()).orderByHistoricTaskInstanceStartTime().asc().list();
        // 数据清洗，将回滚导致的脏数据清洗掉
        List<String> lastHistoricTaskInstanceList = FlowableUtils.historicTaskInstanceClean(allElements, historicTaskInstanceList);
        // 此时历史任务实例为倒序，获取最后走的节点
        List<String> targetIds = new ArrayList<>();
        // 循环结束标识，遇到当前目标节点的次数
        int number = 0;
        StringBuilder parentHistoricTaskKey = new StringBuilder();
        for (String historicTaskInstanceKey : lastHistoricTaskInstanceList) {
            // 当会签时候会出现特殊的，连续都是同一个节点历史数据的情况，这种时候跳过
            if (parentHistoricTaskKey.toString().equals(historicTaskInstanceKey)) {
                continue;
            }
            parentHistoricTaskKey = new StringBuilder(historicTaskInstanceKey);
            if (historicTaskInstanceKey.equals(task.getTaskDefinitionKey())) {
                number++;
            }
            // 在数据清洗后，历史节点就是唯一一条从起始到当前节点的历史记录，理论上每个点只会出现一次
            // 在流程中如果出现循环，那么每次循环中间的点也只会出现一次，再出现就是下次循环
            // number == 1，第一次遇到当前节点
            // number == 2，第二次遇到，代表最后一次的循环范围
            if (number == 2) {
                break;
            }
            // 如果当前历史节点，属于父级的节点，说明最后一次经过了这个点，需要退回这个点
            if (parentUserTaskKeyList.contains(historicTaskInstanceKey)) {
                targetIds.add(historicTaskInstanceKey);
            }
        }


        // 目的获取所有需要被跳转的节点 currentIds
        // 取其中一个父级任务，因为后续要么存在公共网关，要么就是串行公共线路
        UserTask oneUserTask = parentUserTaskList.get(0);
        // 获取所有正常进行的任务节点 Key，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // 需驳回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runTaskList 比对，获取需要撤回的任务
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(oneUserTask, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> currentIds.add(item.getId()));


        // 规定：并行网关之前节点必须需存在唯一用户任务节点，如果出现多个任务节点，则并行网关节点默认为结束节点，原因为不考虑多对多情况
        if (targetIds.size() > 1 && currentIds.size() > 1) {
            throw new CustomException("任务出现多对多情况，无法撤回");
        }

        // 循环获取那些需要被撤回的节点的ID，用来设置驳回原因
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        // 设置驳回意见
        currentTaskIds.forEach(item -> {
            taskService.addComment(item, task.getProcessInstanceId(), "3", flowTaskVo.getComment());
        });

        try {
            // 如果父级任务多于 1 个，说明当前节点不是并行节点，原因为不考虑多对多情况
            if (targetIds.size() > 1) {
                // 1 对 多任务跳转，currentIds 当前节点(1)，targetIds 跳转到的节点(多)
                runtimeService.createChangeActivityStateBuilder()
                        .processInstanceId(task.getProcessInstanceId()).
                        moveSingleActivityIdToActivityIds(currentIds.get(0), targetIds).changeState();
            }
            // 如果父级任务只有一个，因此当前任务可能为网关中的任务
            if (targetIds.size() == 1) {
                // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetIds.get(0) 跳转到的节点(1)
                runtimeService.createChangeActivityStateBuilder()
                        .processInstanceId(task.getProcessInstanceId())
                        .moveActivityIdsToSingleActivityId(currentIds, targetIds.get(0)).changeState();
            }
        } catch (FlowableObjectNotFoundException e) {
            throw new CustomException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new CustomException("无法取消或开始活动");
        }

    }

    /**
     * 退回任务
     *
     * @param flowTaskVo 请求实体参数
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void taskReturn(FlowTaskVo flowTaskVo) {
        if (taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult().isSuspended()) {
            throw new CustomException("任务处于挂起状态");
        }
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        // 获取全部节点列表，包含子节点
        Collection<FlowElement> allElements = FlowableUtils.getAllElements(process.getFlowElements(), null);
        // 获取当前任务节点元素
        FlowElement source = null;
        // 获取跳转的节点元素
        FlowElement target = null;
        if (allElements != null) {
            for (FlowElement flowElement : allElements) {
                // 当前任务节点元素
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    source = flowElement;
                }
                // 跳转的节点元素
                if (flowElement.getId().equals(flowTaskVo.getTargetKey())) {
                    target = flowElement;
                }
            }
        }

        // 从当前节点向前扫描
        // 如果存在路线上不存在目标节点，说明目标节点是在网关上或非同一路线上，不可跳转
        // 否则目标节点相对于当前节点，属于串行
        Boolean isSequential = FlowableUtils.iteratorCheckSequentialReferTarget(source, flowTaskVo.getTargetKey(), null, null);
        if (!isSequential) {
            throw new CustomException("当前节点相对于目标节点，不属于串行关系，无法回退");
        }


        // 获取所有正常进行的任务节点 Key，这些任务不能直接使用，需要找出其中需要撤回的任务
        List<Task> runTaskList = taskService.createTaskQuery().processInstanceId(task.getProcessInstanceId()).list();
        List<String> runTaskKeyList = new ArrayList<>();
        runTaskList.forEach(item -> runTaskKeyList.add(item.getTaskDefinitionKey()));
        // 需退回任务列表
        List<String> currentIds = new ArrayList<>();
        // 通过父级网关的出口连线，结合 runTaskList 比对，获取需要撤回的任务
        List<UserTask> currentUserTaskList = FlowableUtils.iteratorFindChildUserTasks(target, runTaskKeyList, null, null);
        currentUserTaskList.forEach(item -> currentIds.add(item.getId()));

        // 循环获取那些需要被撤回的节点的ID，用来设置驳回原因
        List<String> currentTaskIds = new ArrayList<>();
        currentIds.forEach(currentId -> runTaskList.forEach(runTask -> {
            if (currentId.equals(runTask.getTaskDefinitionKey())) {
                currentTaskIds.add(runTask.getId());
            }
        }));
        // 设置回退意见
        for (String currentTaskId : currentTaskIds) {
            taskService.addComment(currentTaskId, task.getProcessInstanceId(), "2", flowTaskVo.getComment());
        }

        try {
            // 1 对 1 或 多 对 1 情况，currentIds 当前要跳转的节点列表(1或多)，targetKey 跳转到的节点(1)
            runtimeService.createChangeActivityStateBuilder()
                    .processInstanceId(task.getProcessInstanceId())
                    .moveActivityIdsToSingleActivityId(currentIds, flowTaskVo.getTargetKey()).changeState();
        } catch (FlowableObjectNotFoundException e) {
            throw new CustomException("未找到流程实例，流程可能已发生变化");
        } catch (FlowableException e) {
            throw new CustomException("无法取消或开始活动");
        }
    }


    /**
     * 获取所有可回退的节点
     *
     * @param flowTaskVo
     * @return
     */
    @Override
    public AjaxResult findReturnTaskList(FlowTaskVo flowTaskVo) {
        // 当前任务 task
        Task task = taskService.createTaskQuery().taskId(flowTaskVo.getTaskId()).singleResult();
        // 获取流程定义信息
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(task.getProcessDefinitionId()).singleResult();
        // 获取所有节点信息，暂不考虑子流程情况
        Process process = repositoryService.getBpmnModel(processDefinition.getId()).getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();
        // 获取当前任务节点元素
        UserTask source = null;
        if (flowElements != null) {
            for (FlowElement flowElement : flowElements) {
                // 类型为用户节点
                if (flowElement.getId().equals(task.getTaskDefinitionKey())) {
                    source = (UserTask) flowElement;
                }
            }
        }
        // 获取节点的所有路线
        List<List<UserTask>> roads = FlowableUtils.findRoad(source, null, null, null);
        // 可回退的节点列表
        List<UserTask> userTaskList = new ArrayList<>();
        for (List<UserTask> road : roads) {
            if (userTaskList.size() == 0) {
                // 还没有可回退节点直接添加
                userTaskList = road;
            } else {
                // 如果已有回退节点，则比对取交集部分
                userTaskList.retainAll(road);
            }
        }
        return AjaxResult.success(userTaskList);
    }

    /**
     * 删除任务
     *
     * @param flowTaskVo 请求实体参数
     */
    @Override
    public void deleteTask(FlowTaskVo flowTaskVo) {
        // todo 待确认删除任务是物理删除任务 还是逻辑删除，让这个任务直接通过？
    }

    /**
     * 认领/签收任务
     *
     * @param flowTaskVo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void claim(FlowTaskVo flowTaskVo) {
        taskService.claim(flowTaskVo.getTaskId(), flowTaskVo.getUserId());
    }

    /**
     * 取消认领/签收任务
     *
     * @param flowTaskVo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unClaim(FlowTaskVo flowTaskVo) {
        taskService.unclaim(flowTaskVo.getTaskId());
    }

    /**
     * 委派任务
     *
     * @param flowTaskVo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delegateTask(FlowTaskVo flowTaskVo) {

    }


    /**
     * 转办任务
     *
     * @param flowTaskVo 请求实体参数
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignTask(FlowTaskVo flowTaskVo) {
    }

    /**
     * 我发起的流程
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public AjaxResult myProcess(Integer pageNum, Integer pageSize) {
        Page<FlowTaskDto> page = new Page<>();
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        List<HistoricProcessInstance> historicProcessInstances = historyService.createHistoricProcessInstanceQuery()
                .startedBy(userId.toString())
                .orderByProcessInstanceStartTime()
                .desc()
                .listPage(pageNum - 1, pageSize);
        page.setTotal(historicProcessInstances.size());
        List<FlowTaskDto> flowList = new ArrayList<>();
        for (HistoricProcessInstance hisIns : historicProcessInstances) {
            FlowTaskDto flowTask = new FlowTaskDto();
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
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setCategory(pd.getCategory());
            flowTask.setProcDefVersion(pd.getVersion());

            // 当前所处流程
            Task task = taskService.createTaskQuery().processInstanceId(hisIns.getId()).singleResult();
            if (Objects.nonNull(task)) {
                flowTask.setTaskName(task.getName());
                flowTask.setTaskId(task.getId());
                // 指定审批人员信息
                if (StringUtils.isNotBlank(task.getAssignee())) {
                    SysUser sysUser = sysUserService.selectUserById(Long.parseLong(task.getAssignee()));
                    flowTask.setAssigneeId(sysUser.getUserId());
                    flowTask.setAssigneeName(sysUser.getNickName());
                    flowTask.setDeptName(sysUser.getDept().getDeptName());
                } else {
                    // 候选审批人员信息
                    List<IdentityLink> linksForTask = taskService.getIdentityLinksForTask(task.getId());
                    StringBuilder stringBuilder = new StringBuilder();
                    for (IdentityLink identityLink : linksForTask) {
                        if ("candidate".equals(identityLink.getType())) {
                            if (StringUtils.isNotBlank(identityLink.getUserId())) {
                                SysUser sysUser = sysUserService.selectUserById(Long.parseLong(identityLink.getUserId()));
                                stringBuilder.append(sysUser.getNickName()).append(",");
                            }
                            if (StringUtils.isNotBlank(identityLink.getGroupId())) {
                                SysRole sysRole = sysRoleService.selectRoleById(Long.parseLong(identityLink.getGroupId()));
                                stringBuilder.append(sysRole.getRoleName()).append(",");
                            }
                        }
                    }
                    flowTask.setCandidate(stringBuilder.substring(0, stringBuilder.length() - 1));
                }
            } else {
                List<HistoricTaskInstance> historicTaskInstance = historyService.createHistoricTaskInstanceQuery().processInstanceId(hisIns.getId()).orderByHistoricTaskInstanceEndTime().desc().list();
                flowTask.setTaskId(historicTaskInstance.get(0).getId());
            }
            flowList.add(flowTask);
        }
        page.setRecords(flowList);
        return AjaxResult.success(page);
    }

    /**
     * 撤回流程
     *
     * @param flowTaskVo
     * @return
     */
    @Override
    public AjaxResult revokeProcess(FlowTaskVo flowTaskVo) {
        Task task = taskService.createTaskQuery().processInstanceId(flowTaskVo.getInstanceId()).singleResult();
        if (task == null) {
            throw new CustomException("流程未启动或已执行完成，无法撤回");
        }

        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        List<HistoricTaskInstance> htiList = historyService.createHistoricTaskInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .orderByTaskCreateTime()
                .asc()
                .list();
        String myTaskId = null;
        HistoricTaskInstance myTask = null;
        for (HistoricTaskInstance hti : htiList) {
            if (loginUser.getUserId().toString().equals(hti.getAssignee())) {
                myTaskId = hti.getId();
                myTask = hti;
                break;
            }
        }
        if (null == myTaskId) {
            throw new CustomException("该任务非当前用户提交，无法撤回");
        }

        String processDefinitionId = myTask.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        //变量
//      Map<String, VariableInstance> variables = runtimeService.getVariableInstances(currentTask.getExecutionId());
        String myActivityId = null;
        List<HistoricActivityInstance> haiList = historyService.createHistoricActivityInstanceQuery()
                .executionId(myTask.getExecutionId()).finished().list();
        for (HistoricActivityInstance hai : haiList) {
            if (myTaskId.equals(hai.getTaskId())) {
                myActivityId = hai.getActivityId();
                break;
            }
        }
        FlowNode myFlowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(myActivityId);

        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        String activityId = execution.getActivityId();
        log.warn("------->> activityId:{}", activityId);
        FlowNode flowNode = (FlowNode) bpmnModel.getMainProcess().getFlowElement(activityId);

        //记录原活动方向
        List<SequenceFlow> oriSequenceFlows = new ArrayList<SequenceFlow>();
        oriSequenceFlows.addAll(flowNode.getOutgoingFlows());

        //清理活动方向
        flowNode.getOutgoingFlows().clear();
        //建立新方向
        List<SequenceFlow> newSequenceFlowList = new ArrayList<SequenceFlow>();
        SequenceFlow newSequenceFlow = new SequenceFlow();
        newSequenceFlow.setId("newSequenceFlowId");
        newSequenceFlow.setSourceFlowElement(flowNode);
        newSequenceFlow.setTargetFlowElement(myFlowNode);
        newSequenceFlowList.add(newSequenceFlow);
        flowNode.setOutgoingFlows(newSequenceFlowList);

        Authentication.setAuthenticatedUserId(loginUser.getUserId().toString());
        taskService.addComment(task.getId(), task.getProcessInstanceId(), "撤回");

        Map<String, Object> currentVariables = new HashMap<String, Object>();
        currentVariables.put("applier", loginUser.getUserName());
        //完成任务
        taskService.complete(task.getId(), currentVariables);
        //恢复原方向
        flowNode.setOutgoingFlows(oriSequenceFlows);
        return AjaxResult.success();
    }

    /**
     * 代办任务列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return
     */
    @Override
    public AjaxResult todoList(Integer pageNum, Integer pageSize) {
        Page<FlowTaskDto> page = new Page<>();
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        TaskQuery taskQuery = taskService.createTaskQuery()
                .active()
                .includeProcessVariables()
//                .taskAssignee(userId.toString())
                .orderByTaskCreateTime().desc();
        page.setTotal(taskQuery.count());
        List<Task> taskList = taskQuery.listPage(pageNum - 1, pageSize);
        List<FlowTaskDto> flowList = new ArrayList<>();
        for (Task task : taskList) {
            FlowTaskDto flowTask = new FlowTaskDto();
            // 当前流程信息
            flowTask.setTaskId(task.getId());
            flowTask.setTaskDefKey(task.getTaskDefinitionKey());
//            // 审批人员信息
//            SysUser sysUser = sysUserService.selectUserById(Long.parseLong(task.getAssignee()));
//            flowTask.setAssigneeId(sysUser.getUserId());
//            flowTask.setAssigneeName(sysUser.getNickName());
//            flowTask.setDeptName(sysUser.getDept().getDeptName());
            flowTask.setCreateTime(task.getCreateTime());
//            flowTask.setProcVars(task.getProcessVariables());
            flowTask.setProcDefId(task.getProcessDefinitionId());
            flowTask.setTaskName(task.getName());
            // 流程定义信息
            ProcessDefinition pd = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId())
                    .singleResult();
            flowTask.setProcDefName(pd.getName());
            flowTask.setProcDefVersion(pd.getVersion());
            flowTask.setProcInsId(task.getProcessInstanceId());

            // 流程发起人信息
            HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                    .processInstanceId(task.getProcessInstanceId())
                    .singleResult();
            SysUser startUser = sysUserService.selectUserById(Long.parseLong(historicProcessInstance.getStartUserId()));
            flowTask.setStartUserId(startUser.getNickName());
            flowTask.setStartUserName(startUser.getNickName());
            flowTask.setStartDeptName(startUser.getDept().getDeptName());
            flowList.add(flowTask);
        }

        page.setRecords(flowList);
        return AjaxResult.success(page);
    }


    /**
     * 已办任务列表
     *
     * @param pageNum  当前页码
     * @param pageSize 每页条数
     * @return
     */
    @Override
    public AjaxResult finishedList(Integer pageNum, Integer pageSize) {
        Page<FlowTaskDto> page = new Page<>();
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        HistoricTaskInstanceQuery taskInstanceQuery = historyService.createHistoricTaskInstanceQuery()
                .includeProcessVariables()
                .finished()
                .taskAssignee(userId.toString())
                .orderByHistoricTaskInstanceEndTime()
                .desc();
        List<HistoricTaskInstance> historicTaskInstanceList = taskInstanceQuery.listPage(pageNum - 1, pageSize);
        List<FlowTaskDto> hisTaskList = Lists.newArrayList();
        for (HistoricTaskInstance histTask : historicTaskInstanceList) {
            FlowTaskDto flowTask = new FlowTaskDto();
            // 当前流程信息
            flowTask.setTaskId(histTask.getId());
            // 审批人员信息
//            SysUser sysUser = sysUserService.selectUserById(Long.parseLong(histTask.getAssignee()));
//            flowTask.setAssigneeId(sysUser.getUserId());
//            flowTask.setAssigneeName(sysUser.getNickName());
//            flowTask.setDeptName(sysUser.getDept().getDeptName());
            flowTask.setCreateTime(histTask.getCreateTime());
            flowTask.setFinishTime(histTask.getEndTime());
            flowTask.setDuration(getDate(histTask.getDurationInMillis()));
            flowTask.setProcDefId(histTask.getProcessDefinitionId());
            flowTask.setTaskDefKey(histTask.getTaskDefinitionKey());
            flowTask.setTaskName(histTask.getName());

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
        page.setTotal(hisTaskList.size());
        page.setRecords(hisTaskList);
        return AjaxResult.success(page);
    }

    private static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    /**
     * 流程历史流转记录
     *
     * @param procInsId 流程实例Id
     * @return
     */
    @Override
    public AjaxResult flowRecord(String procInsId, String deployId) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(procInsId)) {
            List<HistoricActivityInstance> list = historyService
                    .createHistoricActivityInstanceQuery()
                    .processInstanceId(procInsId)
                    .orderByHistoricActivityInstanceStartTime()
                    .desc().list();
            List<FlowTaskDto> hisFlowList = new ArrayList<>();
            for (HistoricActivityInstance histIns : list) {
                if (StringUtils.isNotBlank(histIns.getTaskId())) {
                    FlowTaskDto flowTask = new FlowTaskDto();
                    flowTask.setTaskId(histIns.getTaskId());
                    flowTask.setTaskName(histIns.getActivityName());
                    flowTask.setCreateTime(histIns.getStartTime());
                    flowTask.setFinishTime(histIns.getEndTime());
                    if (StringUtils.isNotBlank(histIns.getAssignee())) {
                        SysUser sysUser = sysUserService.selectUserById(Long.parseLong(histIns.getAssignee()));
                        flowTask.setAssigneeId(sysUser.getUserId());
                        flowTask.setAssigneeName(sysUser.getNickName());
                        flowTask.setDeptName(sysUser.getDept().getDeptName());
                    }
                    List<HistoricIdentityLink> linksForTask = historyService.getHistoricIdentityLinksForTask(histIns.getTaskId());
                    StringBuilder stringBuilder = new StringBuilder();
                    for (HistoricIdentityLink identityLink : linksForTask) {
                        if ("candidate".equals(identityLink.getType())) {
                            if (StringUtils.isNotBlank(identityLink.getUserId())) {
                                SysUser sysUser = sysUserService.selectUserById(Long.parseLong(identityLink.getUserId()));
                                stringBuilder.append(sysUser.getNickName()).append(",");
                            }
                            if (StringUtils.isNotBlank(identityLink.getGroupId())) {
                                SysRole sysRole = sysRoleService.selectRoleById(Long.parseLong(identityLink.getGroupId()));
                                stringBuilder.append(sysRole.getRoleName()).append(",");
                            }
                        }
                    }
                    if (StringUtils.isNotBlank(stringBuilder)) {
                        flowTask.setCandidate(stringBuilder.substring(0, stringBuilder.length() - 1));
                    }

                    flowTask.setDuration(histIns.getDurationInMillis() == null || histIns.getDurationInMillis() == 0 ? null : getDate(histIns.getDurationInMillis()));
                    // 获取意见评论内容
                    List<Comment> commentList = taskService.getProcessInstanceComments(histIns.getProcessInstanceId());
                    commentList.forEach(comment -> {
                        if (histIns.getTaskId().equals(comment.getTaskId())) {
                            flowTask.setComment(FlowCommentDto.builder().type(comment.getType()).comment(comment.getFullMessage()).build());
                        }
                    });
                    hisFlowList.add(flowTask);
                }
            }
            map.put("flowList", hisFlowList);
            // 查询当前任务是否完成
            Task task = taskService.createTaskQuery().processInstanceId(procInsId).singleResult();
            if (Objects.nonNull(task)) {
                map.put("finished", true);
            } else {
                map.put("finished", false);
            }
        }
        if (StringUtils.isNotBlank(deployId)) {
            SysForm sysForm = sysInstanceFormService.selectSysDeployFormByDeployId(deployId);
            map.put("formData", JSONObject.parseObject(sysForm.getFormContent()));
        }
        return AjaxResult.success(map);
    }

    /**
     * 根据任务ID查询挂载的表单信息
     *
     * @param taskId 任务Id
     * @return
     */
    @Override
    public Task getTaskForm(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        return task;
    }

    /**
     * 获取流程过程图
     *
     * @param processId
     * @return
     */
    @Override
    public InputStream diagram(String processId) {
//        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();
//        //获得已经办理的历史节点
//        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).orderByHistoricActivityInstanceStartTime().asc().list();
//        List<String> activties = new ArrayList<>();
//        List<String> flows = new ArrayList<>();
//        for (HistoricActivityInstance activityInstance : activityInstances) {
//            if ("sequenceFlow".equals(activityInstance.getActivityType())) {
//                //需要高亮显示的连接线
//                flows.add(activityInstance.getActivityId());
//            } else {
//                //需要高亮显示的节点
//                activties.add(activityInstance.getActivityId());
//            }
//        }
//        BpmnModel bpmnModel = repositoryService.getBpmnModel(instance.getProcessDefinitionId());
//        //获得图片流
//        DefaultProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
//        return diagramGenerator.generateDiagram(
//                bpmnModel,
//                "png",
//                activties,
//                flows,
//                "宋体",
//                "宋体",
//                "宋体",
//                null,
//                1.0,
//                false);

        String processDefinitionId;
        //1.获取当前的流程实例
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
        if (Objects.isNull(processInstance)) {// 如果流程已经结束，则得到结束节点
            HistoricProcessInstance pi = historyService.createHistoricProcessInstanceQuery().processInstanceId(processId).singleResult();

            processDefinitionId = pi.getProcessDefinitionId();
        } else {// 如果流程没有结束，则取当前活动节点
            // 根据流程实例ID获得当前处于活动状态的ActivityId合集
            ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(processId).singleResult();
            processDefinitionId = pi.getProcessDefinitionId();
        }


        /**
         * 获得活动的节点
         */
        List<HistoricActivityInstance> highLightedFlowList = historyService.createHistoricActivityInstanceQuery().processInstanceId(processId).orderByHistoricActivityInstanceStartTime().asc().list();

        List<String> highLightedFlows = new ArrayList<>();
        List<String> highLightedNodes = new ArrayList<>();
        //高亮线
        for (HistoricActivityInstance tempActivity : highLightedFlowList) {
            if ("sequenceFlow".equals(tempActivity.getActivityType())) {
                //高亮线
                highLightedFlows.add(tempActivity.getActivityId());
            } else {
                //高亮节点
                highLightedNodes.add(tempActivity.getActivityId());
            }
        }

        //获取流程图
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);

        ProcessEngineConfiguration configuration = processEngine.getProcessEngineConfiguration();

        // ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
        //获取自定义图片生成器
        ProcessDiagramGenerator diagramGenerator = new CustomProcessDiagramGenerator();
        InputStream in = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedNodes, highLightedFlows, configuration.getActivityFontName(),
                configuration.getLabelFontName(), configuration.getAnnotationFontName(), configuration.getClassLoader(), 1.0, true);
        return in;

    }

    /**
     * 获取流程变量
     *
     * @param taskId
     * @return
     */
    @Override
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

    /**
     * 流程完成时间处理
     *
     * @param ms
     * @return
     */
    private String getDate(long ms) {
        int ss = 1000;
        int mi = ss * 60;
        int hh = mi * 60;
        int dd = hh * 24;

        long day = ms / dd;
        long hour = (ms - day * dd) / hh;
        long minute = (ms - day - hour * hh) / mi;
        long second = (ms - day - hour * mi) / ss;

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