package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 sys_project
 * 
 * @author ruoyi
 * @date 2022-02-22
 */
public class SysProject extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private Long projectId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long deptId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String projectName;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String 
projectPrincipal;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long 
expensesTotal;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long 
expensesLeft;

    public void setProjectId(Long projectId) 
    {
        this.projectId = projectId;
    }

    public Long getProjectId() 
    {
        return projectId;
    }
    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }
    public void setProjectName(String projectName) 
    {
        this.projectName = projectName;
    }

    public String getProjectName() 
    {
        return projectName;
    }
    public void setprojectPrincipal(String projectPrincipal)
    {
        this.projectPrincipal = projectPrincipal;
    }

    public String getprojectPrincipal()
    {
        return projectPrincipal;
    }
    public void setexpensesTotal(Long expensesTotal)
    {
        this.expensesTotal = expensesTotal;
    }

    public Long getexpensesTotal()
    {
        return expensesTotal;
    }
    public void setexpensesLeft(Long expensesLeft)
    {
        this.expensesLeft = expensesLeft;
    }

    public Long getexpensesLeft()
    {
        return expensesLeft;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("projectId", getProjectId())
            .append("deptId", getDeptId())
            .append("projectName", getProjectName())
            .append(" projectPrincipal", getprojectPrincipal())
            .append(" expensesTotal", getexpensesTotal())
            .append("expensesLeft", getexpensesLeft())
            .toString();
    }
}
