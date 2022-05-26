package com.ruoyi.project.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.project.core.NameSelect;
import com.ruoyi.project.domain.ProjectDeptUser;
import com.ruoyi.project.domain.ProjectFlow;
import com.ruoyi.project.domain.ProjectUserList;
import com.ruoyi.project.mapper.ProjectUserMapper;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.history.HistoricProcessInstanceQuery;
import org.flowable.engine.repository.ProcessDefinition;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDeptUser;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.project.mapper.ProjectFlowMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.mapper.ProjectMapper;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.service.IProjectService;

import javax.naming.Name;

/**
 * 【请填写功能名称】Service业务层处理
 *
 * @author ruoyi
 * @date 2022-03-02
 */
@Service
public class ProjectServiceImpl extends FlowServiceFactory implements IProjectService {
    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Autowired
    private ProjectFlowMapper projectFlowMapper;

    /**
     * 查询【请填写功能名称】
     *
     * @param projectId 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public Project selectProjectById(Long projectId) {
        return projectMapper.selectProjectById(projectId);
    }

    /**
     * 查询【请填写功能名称】列表
     *
     * @param project 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<Project> selectProjectList(Project project) {
        List<Project> returnList = projectMapper.selectProjectList(project);
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .orderByProcessDefinitionKey().asc();
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.listPage(1 - 1, 10);
        Map<String, String> name = new HashMap<>();
        Map<String, Integer> version = new HashMap<>();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            String p = processDefinition.getDeploymentId();
            name.put(p, processDefinition.getName());
            version.put(p, processDefinition.getVersion());

        }

        for (Project list : returnList) {
            String deploy = list.getFlow().getDeployId();
            list.setProcName(name.get(deploy));
            if (version.containsKey(deploy)) {
                list.setVersion(version.get(deploy));
            }
        }
        return returnList;
    }

    @Override
    public List<Project> selectProjectListPrincipal() {
        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        List<Project> returnList = projectMapper.selectProjectListByPrincipal(userId);
        return returnList;
    }

    /**
     * 新增【请填写功能名称】
     *
     * @param project 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertProject(Project project) {
        project.setExpensesLeft(project.getExpensesTotal());
        return projectMapper.insertProject(project);
    }

    /**
     * 修改【请填写功能名称】
     *
     * @param project 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateProject(Project project) {
        return projectMapper.updateProject(project);
    }

    /**
     * 批量删除【请填写功能名称】
     *
     * @param projectIds 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteProjectByIds(Long[] projectIds) {
        return projectMapper.deleteProjectByIds(projectIds);
    }

    /**
     * 删除【请填写功能名称】信息
     *
     * @param projectId 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteProjectById(Long projectId) {
        ProjectUserList projectUserList = new ProjectUserList();
        projectUserList.setProjectId(projectId);
        List<ProjectUserList> projectUserLists = projectUserMapper.selectProjectUserList(projectUserList);
        if(projectUserLists.size()>0)
        {
            return 0;
        }
        projectFlowMapper.deleteProjectFlowByProjectId(projectId);
        projectMapper.deleteProjectById(projectId);
        return 1;
    }

    @Override
    public List<NameSelect> buildUserTree(Long deptId) {
        List<ProjectDeptUser> returnList = new ArrayList<>();
        returnList = projectMapper.selectUserName(deptId);
        return returnList.stream().map(NameSelect::new).collect(Collectors.toList());
    }

    @Override
    public int updateProjectStatus(Project project) {
        return projectMapper.updateProject(project);
    }

    @Override
    public int check(Project project)
    {
        ProjectUserList list = new ProjectUserList();
        list.setProjectId(project.getProjectId());
        List<ProjectUserList> projectUserLists = projectUserMapper.selectProjectUserList(list);
        if(projectUserLists.size()>0)
            return 0;
        return 1;
    }
}
