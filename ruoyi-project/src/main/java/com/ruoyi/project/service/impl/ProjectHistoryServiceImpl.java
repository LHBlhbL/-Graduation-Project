package com.ruoyi.project.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.mapper.ProjectHistoryMapper;
import com.ruoyi.project.domain.ProjectHistory;
import com.ruoyi.project.service.IProjectHistoryService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-03-24
 */
@Service
public class ProjectHistoryServiceImpl implements IProjectHistoryService 
{
    @Autowired
    private ProjectHistoryMapper projectHistoryMapper;

    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    @Override
    public ProjectHistory selectProjectHistoryById(Long id)
    {
        return projectHistoryMapper.selectProjectHistoryById(id);
    }

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param projectHistory 【请填写功能名称】
     * @return 【请填写功能名称】
     */
    @Override
    public List<ProjectHistory> selectProjectHistoryList(ProjectHistory projectHistory)
    {
        return projectHistoryMapper.selectProjectHistoryList(projectHistory);
    }

    /**
     * 新增【请填写功能名称】
     * 
     * @param projectHistory 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int insertProjectHistory(ProjectHistory projectHistory)
    {
        return projectHistoryMapper.insertProjectHistory(projectHistory);
    }

    /**
     * 修改【请填写功能名称】
     * 
     * @param projectHistory 【请填写功能名称】
     * @return 结果
     */
    @Override
    public int updateProjectHistory(ProjectHistory projectHistory)
    {
        return projectHistoryMapper.updateProjectHistory(projectHistory);
    }

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteProjectHistoryByIds(Long[] ids)
    {
        return projectHistoryMapper.deleteProjectHistoryByIds(ids);
    }

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    @Override
    public int deleteProjectHistoryById(Long id)
    {
        return projectHistoryMapper.deleteProjectHistoryById(id);
    }
}
