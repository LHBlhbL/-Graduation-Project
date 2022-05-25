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


    private Long projectId;


    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long deptId;


    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String projectName;


    private Long principalId;


    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Double expensesTotal;

    private int version;

    private String procName;

    private String note;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getProcName() {
        return procName;
    }

    public void setProcName(String procName) {
        this.procName = procName;
    }

    /**
     * $column.columnComment
     */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Double expensesLeft;

    private ProjectDept dept;

    private ProjectPrincipal principal;

    private ProjectFlow flow;

    public ProjectFlow getFlow() {
        return flow;
    }

    public void setFlow(ProjectFlow flow) {
        this.flow = flow;
    }

    public ProjectPrincipal getPrincipal() {
        return principal;
    }

    public void setPrincipal(ProjectPrincipal principal) {
        this.principal = principal;
    }

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


    public void setExpensesTotal(Double expensesTotal) {
        this.expensesTotal = expensesTotal;
    }

    public Double getExpensesTotal() {
        return expensesTotal;
    }

    public void setExpensesLeft(Double expensesLeft) {
        this.expensesLeft = expensesLeft;
    }

    public Double getExpensesLeft() {
        return expensesLeft;
    }

    public Long getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(Long principalId) {
        this.principalId = principalId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("projectId", getProjectId())
                .append("deptId", getDeptId())
                .append("projectName", getProjectName())
                .append("principalId", getPrincipalId())
                .append("expensesTotal", getExpensesTotal())
                .append("expensesLeft", getExpensesLeft())
                .append("status", getStatus())
                .append("dept",getDept())
                .append("principal",getPrincipal())
                .append("flow",getFlow())
                .append("procName",getProcName())
                .append("version",getVersion())
                .toString();
    }
}
