package com.ruoyi.project.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDeptUser;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.project.mapper.ProjectFlowMapper;
import com.ruoyi.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.mapper.ProjectMapper;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.service.IProjectService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-03-02
 */
@Service
public class ProjectServiceImpl implements IProjectService 
{
    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param projectId 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public Project selectProjectById(Long projectId)
    {
        return projectMapper.selectProjectById(projectId);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param project 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<Project> selectProjectList(Project project)
    {
        return projectMapper.selectProjectList(project);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param project 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertProject(Project project)
    {
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
    public int updateProject(Project project)
    {
        return projectMapper.updateProject(project);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param projectIds 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteProjectByIds(Long[] projectIds)
    {
        return projectMapper.deleteProjectByIds(projectIds);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param projectId 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteProjectById(Long projectId)
    {
        return projectMapper.deleteProjectById(projectId);
    }

    @Override
    public List<TreeSelect> buildUserTree(Long deptId) {
        List<SysDeptUser> returnList = new ArrayList<>();
        returnList = sysUserMapper.getNameByDept(deptId);
        return returnList.stream().map(TreeSelect::new).collect(Collectors.toList());
    }

    @Override
    public int updateProjectStatus(Project project) {
        return projectMapper.updateProject(project);
    }
}
