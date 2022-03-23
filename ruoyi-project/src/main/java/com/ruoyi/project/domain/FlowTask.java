package com.ruoyi.project.domain;

import com.ruoyi.flowable.domain.dto.FlowTaskDto;

public class FlowTask extends FlowTaskDto {
    private String projectName;

    private Long projectId;

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
