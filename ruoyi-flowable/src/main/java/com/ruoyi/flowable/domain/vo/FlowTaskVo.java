package com.ruoyi.flowable.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * <p>流程任务<p>
 *
 * @author XuanXuan
 * @date 2021-04-03
 */
@Data
@ApiModel("工作流任务相关--请求参数")
public class FlowTaskVo {

    @ApiModelProperty("任务Id")
    private String taskId;

    @ApiModelProperty("用户Id")
    private String userId;

    @ApiModelProperty("任务意见")
    private String comment;

    @ApiModelProperty("流程实例Id")
    private String instanceId;

    @ApiModelProperty("退回节点")
    private String targetKey;

    @ApiModelProperty("流程变量信息")
    private Map<String, Object> values;
}
