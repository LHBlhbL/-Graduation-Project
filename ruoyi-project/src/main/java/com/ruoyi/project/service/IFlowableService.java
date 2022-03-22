package com.ruoyi.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.FlowProcDefDto;

public interface IFlowableService {

    Page<FlowProcDefDto> listDefinition(String name, Integer pageNum, Integer pageSize);
}
