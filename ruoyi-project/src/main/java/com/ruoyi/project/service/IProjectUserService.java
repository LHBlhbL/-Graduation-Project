package com.ruoyi.project.service;

import java.util.List;
import com.ruoyi.project.domain.ProjectUser;
import com.ruoyi.project.domain.ProjectUserList;

/**
 * 【请填写功能名称】Service接口
 * 
 * @author ruoyi
 * @date 2022-03-21
 */
public interface IProjectUserService 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public ProjectUserList selectProjectUserById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param projectUser 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ProjectUserList> selectProjectUserList(ProjectUserList projectUser);

    /**
     * 新增【请填写功能名称】
     * 
     * @param projectUser 【请填写功能名称】
     * @return 结果
     */
    public int insertProjectUser(ProjectUserList projectUser);

    /**
     * 修改【请填写功能名称】
     * 
     * @param projectUser 【请填写功能名称】
     * @return 结果
     */
    public int updateProjectUser(ProjectUserList projectUser);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的【请填写功能名称】ID
     * @return 结果
     */
    public int deleteProjectUserByIds(Long[] ids);

    /**
     * 删除【请填写功能名称】信息
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteProjectUserById(Long id);
}
