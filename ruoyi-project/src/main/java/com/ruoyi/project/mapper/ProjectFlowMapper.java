package com.ruoyi.project.mapper;

import java.util.List;
import java.util.Map;

import com.ruoyi.project.domain.FlowTaskName;
import com.ruoyi.project.domain.ProjectFlow;
import org.apache.ibatis.annotations.MapKey;

/**
 * 【请填写功能名称】Mapper接口
 * 
 * @author ruoyi
 * @date 2022-03-06
 */
public interface ProjectFlowMapper 
{
    /**
     * 查询【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 【请填写功能名称】
     */
    public ProjectFlow selectProjectFlowById(Long id);

    /**
     * 查询【请填写功能名称】列表
     * 
     * @param projectFlow 【请填写功能名称】
     * @return 【请填写功能名称】集合
     */
    public List<ProjectFlow> selectProjectFlowList(ProjectFlow projectFlow);
    public ProjectFlow selectProjectFlow(ProjectFlow projectFlow);
    /**
     * 新增【请填写功能名称】
     * 
     * @param projectFlow 【请填写功能名称】
     * @return 结果
     */
    public int insertProjectFlow(ProjectFlow projectFlow);

    /**
     * 修改【请填写功能名称】
     * 
     * @param projectFlow 【请填写功能名称】
     * @return 结果
     */
    public int updateProjectFlow(ProjectFlow projectFlow);

    /**
     * 删除【请填写功能名称】
     * 
     * @param id 【请填写功能名称】ID
     * @return 结果
     */
    public int deleteProjectFlowById(Long id);

    /**
     * 批量删除【请填写功能名称】
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteProjectFlowByIds(Long[] ids);

    public int deleteProjectFlowByProjectId(Long id);

    public ProjectFlow selectProjectFlowByPId(Long ProjectId);


    public List<FlowTaskName> selectProjectName();
}
