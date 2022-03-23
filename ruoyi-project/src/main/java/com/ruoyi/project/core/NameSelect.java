package com.ruoyi.project.core;

import com.ruoyi.project.domain.ProjectDeptUser;

import java.io.Serializable;

public class NameSelect implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String label;

    public NameSelect(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public NameSelect(ProjectDeptUser user)
    {
        this.id=user.getUserId();
        this.label=user.getUserName();

    }
}
