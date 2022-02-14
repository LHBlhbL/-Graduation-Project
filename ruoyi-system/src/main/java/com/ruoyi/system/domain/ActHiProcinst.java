package com.ruoyi.system.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 【请填写功能名称】对象 act_hi_procinst
 * 
 * @author ruoyi
 * @date 2022-02-15
 */
public class ActHiProcinst extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** $column.columnComment */
    private String id;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long rev;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String procInstId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String businessKey;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String procDefId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date startTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Date endTime;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private Long duration;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String startUserId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String startActId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String endActId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String superProcessInstanceId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String deleteReason;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String tenantId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String name;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String callbackId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String callbackType;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String referenceId;

    /** $column.columnComment */
    @Excel(name = "${comment}", readConverterExp = "$column.readConverterExp()")
    private String referenceType;

    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
    {
        return id;
    }
    public void setRev(Long rev) 
    {
        this.rev = rev;
    }

    public Long getRev() 
    {
        return rev;
    }
    public void setProcInstId(String procInstId) 
    {
        this.procInstId = procInstId;
    }

    public String getProcInstId() 
    {
        return procInstId;
    }
    public void setBusinessKey(String businessKey) 
    {
        this.businessKey = businessKey;
    }

    public String getBusinessKey() 
    {
        return businessKey;
    }
    public void setProcDefId(String procDefId) 
    {
        this.procDefId = procDefId;
    }

    public String getProcDefId() 
    {
        return procDefId;
    }
    public void setStartTime(Date startTime) 
    {
        this.startTime = startTime;
    }

    public Date getStartTime() 
    {
        return startTime;
    }
    public void setEndTime(Date endTime) 
    {
        this.endTime = endTime;
    }

    public Date getEndTime() 
    {
        return endTime;
    }
    public void setDuration(Long duration) 
    {
        this.duration = duration;
    }

    public Long getDuration() 
    {
        return duration;
    }
    public void setStartUserId(String startUserId) 
    {
        this.startUserId = startUserId;
    }

    public String getStartUserId() 
    {
        return startUserId;
    }
    public void setStartActId(String startActId) 
    {
        this.startActId = startActId;
    }

    public String getStartActId() 
    {
        return startActId;
    }
    public void setEndActId(String endActId) 
    {
        this.endActId = endActId;
    }

    public String getEndActId() 
    {
        return endActId;
    }
    public void setSuperProcessInstanceId(String superProcessInstanceId) 
    {
        this.superProcessInstanceId = superProcessInstanceId;
    }

    public String getSuperProcessInstanceId() 
    {
        return superProcessInstanceId;
    }
    public void setDeleteReason(String deleteReason) 
    {
        this.deleteReason = deleteReason;
    }

    public String getDeleteReason() 
    {
        return deleteReason;
    }
    public void setTenantId(String tenantId) 
    {
        this.tenantId = tenantId;
    }

    public String getTenantId() 
    {
        return tenantId;
    }
    public void setName(String name) 
    {
        this.name = name;
    }

    public String getName() 
    {
        return name;
    }
    public void setCallbackId(String callbackId) 
    {
        this.callbackId = callbackId;
    }

    public String getCallbackId() 
    {
        return callbackId;
    }
    public void setCallbackType(String callbackType) 
    {
        this.callbackType = callbackType;
    }

    public String getCallbackType() 
    {
        return callbackType;
    }
    public void setReferenceId(String referenceId) 
    {
        this.referenceId = referenceId;
    }

    public String getReferenceId() 
    {
        return referenceId;
    }
    public void setReferenceType(String referenceType) 
    {
        this.referenceType = referenceType;
    }

    public String getReferenceType() 
    {
        return referenceType;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("rev", getRev())
            .append("procInstId", getProcInstId())
            .append("businessKey", getBusinessKey())
            .append("procDefId", getProcDefId())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("duration", getDuration())
            .append("startUserId", getStartUserId())
            .append("startActId", getStartActId())
            .append("endActId", getEndActId())
            .append("superProcessInstanceId", getSuperProcessInstanceId())
            .append("deleteReason", getDeleteReason())
            .append("tenantId", getTenantId())
            .append("name", getName())
            .append("callbackId", getCallbackId())
            .append("callbackType", getCallbackType())
            .append("referenceId", getReferenceId())
            .append("referenceType", getReferenceType())
            .toString();
    }
}
