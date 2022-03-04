package com.ruoyi.project.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 project
 *
 * @author ruoyi
 * @date 2022-03-02
 */
public class Project extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * $column.columnComment
     */
    private Long projectId;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long deptId;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String projectName;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String projectPrincipal;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long expensesTotal;

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long expensesLeft;

    private ProjectDept dept;

    public ProjectDept getDept() {
        return dept;
    }

    public void setDept(ProjectDept dept) {
        this.dept = dept;
    }

    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setDeptId(Long deptId) {
        this.deptId = deptId;
    }

    public Long getDeptId() {
        return deptId;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectPrincipal(String projectPrincipal) {
        this.projectPrincipal = projectPrincipal;
    }

    public String getProjectPrincipal() {
        return projectPrincipal;
    }

    public void setExpensesTotal(Long expensesTotal) {
        this.expensesTotal = expensesTotal;
    }

    public Long getExpensesTotal() {
        return expensesTotal;
    }

    public void setExpensesLeft(Long expensesLeft) {
        this.expensesLeft = expensesLeft;
    }

    public Long getExpensesLeft() {
        return expensesLeft;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("projectId", getProjectId())
                .append("deptId", getDeptId())
                .append("projectName", getProjectName())
                .append("projectPrincipal", getProjectPrincipal())
                .append("expensesTotal", getExpensesTotal())
                .append("expensesLeft", getExpensesLeft())
                .append("status", getStatus())
                .append("dept",getDept())
                .toString();
    }
}
