package com.ruoyi.project.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

import java.util.Map;

/**
 * 【请填写功能名称】对象 project_user
 *
 * @author ruoyi
 * @date 2022-03-21
 */
public class ProjectUserList extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    private Long id;

    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long userId;

    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long projectId;

    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String projectName;

    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String procInsId;


    public void setId(Long id)
    {
        this.id = id;
    }

    public Long getId()
    {
        return id;
    }
    public void setUserId(Long userId)
    {
        this.userId = userId;
    }

    public Long getUserId()
    {
        return userId;
    }
    public void setProjectId(Long projectId)
    {
        this.projectId = projectId;
    }

    public Long getProjectId()
    {
        return projectId;
    }
    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public String getProjectName()
    {
        return projectName;
    }
    public void setProcInsId(String procInsId)
    {
        this.procInsId = procInsId;
    }

    public String getProcInsId()
    {
        return procInsId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("userId", getUserId())
                .append("projectId", getProjectId())
                .append("projectName", getProjectName())
                .append("procInsId", getProcInsId())
                .toString();
    }
}
