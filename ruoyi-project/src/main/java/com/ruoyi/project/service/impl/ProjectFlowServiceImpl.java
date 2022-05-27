package com.ruoyi.project.service.impl;

import java.util.List;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.project.domain.ProjectUserList;
import com.ruoyi.project.mapper.ProjectUserMapper;
import com.ruoyi.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.mapper.ProjectFlowMapper;
import com.ruoyi.project.domain.ProjectFlow;
import com.ruoyi.project.service.IProjectFlowService;

import javax.annotation.Resource;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-03-06
 */
@Service
public class ProjectFlowServiceImpl extends FlowServiceFactory implements IProjectFlowService
{
    @Autowired
    private ProjectFlowMapper projectFlowMapper;

    @Resource
    private ISysUserService sysUserService;

    @Autowired
    private ProjectUserMapper userMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ProjectFlow selectProjectFlowById(Long id)
    {
        return projectFlowMapper.selectProjectFlowById(id);
    }

    @Override
    public ProjectFlow selectProjectFlowByPId(Long projectId) {
        ProjectFlow flow = new ProjectFlow();

        Long userId = SecurityUtils.getLoginUser().getUser().getUserId();
        ProjectUserList userList = new ProjectUserList();
        userList.setProjectId(projectId);
        userList.setUserId(userId);
        List<ProjectUserList> projectUserLists = userMapper.selectProjectUserList(userList);
        if(projectUserLists.size()>0)
            return flow;
        return projectFlowMapper.selectProjectFlowByPId(projectId);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param projectFlow 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ProjectFlow> selectProjectFlowList(ProjectFlow projectFlow)
    {
        return projectFlowMapper.selectProjectFlowList(projectFlow);
    }

    @Override
    public int selectProjectFlowListByDeployId(String id) {
        List<ProjectFlow> flows = projectFlowMapper.selectProjectFlowByDeployId(id);
        if(flows.size()>0)
        {
            return 0;
        }
        else
        {
            repositoryService.deleteDeployment(id, true);
            return 1;
        }
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param projectFlow 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertProjectFlow(ProjectFlow projectFlow)
    {
        List<ProjectFlow> flows = selectProjectFlowList(projectFlow);
        for (ProjectFlow flow:flows)
        {
            projectFlow.setId(flow.getId());
        }
        if(projectFlow.getId()!=null)
            return projectFlowMapper.updateProjectFlow(projectFlow);
        return projectFlowMapper.insertProjectFlow(projectFlow);

    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param projectFlow 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateProjectFlow(ProjectFlow projectFlow)
    {
        return projectFlowMapper.updateProjectFlow(projectFlow);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteProjectFlowByIds(Long[] ids)
    {
        return projectFlowMapper.deleteProjectFlowByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteProjectFlowById(Long id)
    {
        return projectFlowMapper.deleteProjectFlowById(id);
    }

}
