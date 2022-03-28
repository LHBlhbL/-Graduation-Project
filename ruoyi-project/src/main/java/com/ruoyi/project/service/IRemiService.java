package com.ruoyi.project.service;

import com.ruoyi.project.domain.Project;

import java.util.List;

public interface IRemiService {
    public List<Project> listRemi();
    public List<Project> queryList(Project project);
}
