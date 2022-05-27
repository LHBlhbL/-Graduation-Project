package com.ruoyi.project.mapper;

import java.util.List;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.domain.ProjectDeptUser;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2022-03-02
 */
public interface ProjectMapper 
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

    public List<Project> selectProjectListByPrincipal(Long userId);

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
     * 删除【请填写功能名称】
     * 
     * @param projectId 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteProjectById(Long projectId);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param projectIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteProjectByIds(Long[] projectIds);

    public List<Project> selectRemiList();

    public List<Project> selectRemiProjectList();

    public Project selectProjectInfoById(Long id);

    public List<ProjectDeptUser> selectUserName(Long deptId);

    public List<Project> selectRemiQueryList(Project project);

    public Project selectProjectByProcInsId(String procInsId);
}
