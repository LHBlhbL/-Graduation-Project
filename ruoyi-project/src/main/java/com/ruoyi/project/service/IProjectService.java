package com.ruoyi.project.service;

import java.util.List;

import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.project.core.NameSelect;
import com.ruoyi.project.domain.Project;

import javax.naming.Name;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2022-03-02
 */
public interface IProjectService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param projectId 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public Project selectProjectById(Long projectId);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param project 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<Project> selectProjectList(Project project);

    /**
     * 新增【请填写功能名称】
     * 
     * @param project 【请填写功能名称】
     * @return 结果
     */
    public int insertProject(Project project);

    /**
     * 修改【请填写功能名称】
     * 
     * @param project 【请填写功能名称】
     * @return 结果
     */
    public int updateProject(Project project);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param projectIds 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteProjectByIds(Long[] projectIds);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param projectId 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteProjectById(Long projectId);

    public List<NameSelect> buildUserTree(Long deptId);

    public int updateProjectStatus(Project project);

    public int check(Project project);
}
