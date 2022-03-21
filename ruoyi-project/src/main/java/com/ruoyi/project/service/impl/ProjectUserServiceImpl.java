package com.ruoyi.project.service.impl;

import java.util.List;

import com.ruoyi.project.domain.ProjectUserList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.project.mapper.ProjectUserMapper;
import com.ruoyi.project.domain.ProjectUser;
import com.ruoyi.project.service.IProjectUserService;

/**
 * 【请填写功能名称】Service业务层处理
 * 
 * @author ruoyi
 * @date 2022-03-21
 */
@Service
public class ProjectUserServiceImpl implements IProjectUserService 
{
    @Autowired
    private ProjectUserMapper projectUserMapper;

    @Override
    public ProjectUserList selectProjectUserById(Long id)
    {
        return projectUserMapper.selectProjectUserById(id);
    }

    @Override
    public List<ProjectUserList> selectProjectUserList(ProjectUserList projectUser)
    {
        return projectUserMapper.selectProjectUserList(projectUser);
    }


    @Override
    public int insertProjectUser(ProjectUserList projectUser)
    {
        return projectUserMapper.insertProjectUser(projectUser);
    }




    @Override
    public int updateProjectUser(ProjectUserList projectUser)
    {
        return projectUserMapper.updateProjectUser(projectUser);
    }


    @Override
    public int deleteProjectUserByIds(Long[] ids)
    {
        return projectUserMapper.deleteProjectUserByIds(ids);
    }


    @Override
    public int deleteProjectUserById(Long id)
    {
        return projectUserMapper.deleteProjectUserById(id);
    }
}
