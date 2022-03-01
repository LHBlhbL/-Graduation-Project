package com.ruoyi.system.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDeptUser;
import com.ruoyi.system.domain.SysProject;
import com.ruoyi.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysProjectMapper;
import com.ruoyi.system.service.ISysProjectService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-02-22
 */
@Service
public class SysProjectServiceImpl implements ISysProjectService 
{
    @Autowired
    private SysProjectMapper sysProjectMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param projectId 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public SysProject selectSysProjectById(Long projectId)
    {
        return sysProjectMapper.selectSysProjectById(projectId);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param sysProject 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<SysProject> selectSysProjectList(SysProject sysProject)
    {
        return sysProjectMapper.selectSysProjectList(sysProject);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param sysProject 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertSysProject(SysProject sysProject)
    {
        return sysProjectMapper.insertSysProject(sysProject);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param sysProject 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateSysProject(SysProject sysProject)
    {
        return sysProjectMapper.updateSysProject(sysProject);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param projectIds 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteSysProjectByIds(Long[] projectIds)
    {
        return sysProjectMapper.deleteSysProjectByIds(projectIds);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param projectId 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteSysProjectById(Long projectId)
    {
        return sysProjectMapper.deleteSysProjectById(projectId);
    }

    @Override
    public List<TreeSelect> buildUserTree(Long deptId) {
        List<SysDeptUser> returnList = new ArrayList<>();
        returnList = sysUserMapper.getNameByDept(deptId);
        return returnList.stream().map(TreeSelect::new).collect(Collectors.toList());
    }
}
