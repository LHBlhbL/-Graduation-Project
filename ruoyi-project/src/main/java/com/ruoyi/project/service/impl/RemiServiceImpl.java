package com.ruoyi.project.service.impl;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.exception.CustomException;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.domain.vo.FlowTaskVo;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.domain.ProjectFlow;
import com.ruoyi.project.mapper.ProjectFlowMapper;
import com.ruoyi.project.mapper.ProjectMapper;
import com.ruoyi.project.mapper.ProjectUserMapper;
import com.ruoyi.project.service.IRemiService;
import com.ruoyi.system.service.ISysUserService;
import org.apache.commons.collections4.CollectionUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.Process;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class RemiServiceImpl extends FlowServiceFactory implements IRemiService  {

    @Autowired
    ProjectMapper mapper;

    @Resource
    private ISysUserService sysUserService;

    @Autowired
    private ProjectFlowMapper projectFlowMapper;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Override
    public List<Project> listRemi() {
        List<Project> returnList = mapper.selectRemiProjectList();
        for(Project project:returnList)
        {
            String nickName = sysUserService.selectUserById(project.getPrincipalId()).getNickName();
            project.setStartUserName(nickName);
        }
        return returnList;
    }

    @Override
    public List<Project> queryList(Project project) {
        List<Project> projects = mapper.selectRemiQueryList(project);

        List<Project> returnList = new ArrayList<>();
        for(Project project1:projects)
        {
            ProjectFlow projectFlow = projectFlowMapper.selectProjectFlowByPId(project1.getProjectId());
            if(projectFlow!=null)
            {
                returnList.add(project1);
            }
        }
        return returnList;
    }

    @Override
    public AjaxResult stopProcess(String procInsId) {
        List<Task> task = taskService.createTaskQuery().processInstanceId(procInsId).list();
        if (CollectionUtils.isEmpty(task)) {
            throw new CustomException("流程未启动或已执行完成，取消申请失败");
        }

        SysUser loginUser = SecurityUtils.getLoginUser().getUser();
        ProcessInstance processInstance =
                runtimeService.createProcessInstanceQuery().processInstanceId(procInsId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        if (Objects.nonNull(bpmnModel)) {
            Process process = bpmnModel.getMainProcess();
            List<EndEvent> endNodes = process.findFlowElementsOfType(EndEvent.class, false);
            if (CollectionUtils.isNotEmpty(endNodes)) {
                Authentication.setAuthenticatedUserId(loginUser.getUserId().toString());
//                taskService.addComment(task.getId(), processInstance.getProcessInstanceId(), FlowComment.STOP.getType(),
//                        StringUtils.isBlank(flowTaskVo.getComment()) ? "取消申请" : flowTaskVo.getComment());
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
        return AjaxResult.success();
    }



}
