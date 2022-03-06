package com.ruoyi.project.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.mapper.ProjectFlowMapper;
import com.ruoyi.project.domain.ProjectFlow;
import com.ruoyi.project.service.IProjectFlowService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-03-06
 */
@Service
public class ProjectFlowServiceImpl implements IProjectFlowService 
{
    @Autowired
    private ProjectFlowMapper projectFlowMapper;

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
