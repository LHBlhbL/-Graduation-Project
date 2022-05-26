package com.ruoyi.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.FlowProcDefDto;

import java.util.List;

public interface IFlowableService {

    List<FlowProcDefDto> listDefinition(String name, Integer pageNum, Integer pageSize);
}
