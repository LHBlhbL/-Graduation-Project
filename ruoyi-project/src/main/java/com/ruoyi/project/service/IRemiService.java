package com.ruoyi.project.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.project.domain.Project;

import java.util.List;

public interface IRemiService {
    public List<Project> listRemi();
    public List<Project> queryList(Project project);

    public void deleteDeployment(String id);

    public AjaxResult stopProcess(String procInsId);
}
