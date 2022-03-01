package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDeptUser;
import com.ruoyi.system.domain.SysProject;

import java.util.List;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2022-02-22
 */
public interface ISysProjectService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param projectId 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public SysProject selectSysProjectById(Long projectId);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param sysProject 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<SysProject> selectSysProjectList(SysProject sysProject);

    /**
     * 新增【请填写功能名称】
     * 
     * @param sysProject 【请填写功能名称】
     * @return 结果
     */
    public int insertSysProject(SysProject sysProject);

    /**
     * 修改【请填写功能名称】
     * 
     * @param sysProject 【请填写功能名称】
     * @return 结果
     */
    public int updateSysProject(SysProject sysProject);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param projectIds 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteSysProjectByIds(Long[] projectIds);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param projectId 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteSysProjectById(Long projectId);

    public List<TreeSelect> buildUserTree(Long deptId);
}
