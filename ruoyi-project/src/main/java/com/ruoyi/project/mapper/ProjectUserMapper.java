package com.ruoyi.project.mapper;

import java.util.List;

import com.ruoyi.project.domain.ProjectUserList;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2022-03-21
 */
public interface ProjectUserMapper 
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

    public ProjectUserList selectProjectUserListByProcId(ProjectUserList userList);

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
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteProjectUserById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteProjectUserByIds(Long[] ids);

    public int deleteProjectUser(Long projectId,Long userId);

    public int deleteProjectUserByProc(String procInsId);
}
