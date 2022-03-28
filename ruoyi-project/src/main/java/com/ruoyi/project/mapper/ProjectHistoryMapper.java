package com.ruoyi.project.mapper;

import java.util.List;
import com.ruoyi.project.domain.ProjectHistory;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2022-03-24
 */
public interface ProjectHistoryMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public ProjectHistory selectProjectHistoryById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param projectHistory 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ProjectHistory> selectProjectHistoryList(ProjectHistory projectHistory);

    /**
     * 新增【请填写功能名称】
     * 
     * @param projectHistory 【请填写功能名称】
     * @return 结果
     */
    public int insertProjectHistory(ProjectHistory projectHistory);

    /**
     * 修改【请填写功能名称】
     * 
     * @param projectHistory 【请填写功能名称】
     * @return 结果
     */
    public int updateProjectHistory(ProjectHistory projectHistory);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteProjectHistoryById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteProjectHistoryByIds(Long[] ids);

    public int deleteProjectHistoryByHis(ProjectHistory projectHistory);
}
