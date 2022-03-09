package com.ruoyi.project.domain;

import com.ruoyi.flowable.domain.dto.FlowTaskDto;

public class FlowTask extends FlowTaskDto {
    private String projectName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
