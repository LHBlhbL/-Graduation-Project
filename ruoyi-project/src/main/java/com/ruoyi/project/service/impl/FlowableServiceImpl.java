package com.ruoyi.project.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.flowable.factory.FlowServiceFactory;
import com.ruoyi.flowable.service.ISysDeployFormService;
import com.ruoyi.project.service.IFlowableService;
import com.ruoyi.system.domain.FlowProcDefDto;
import com.ruoyi.system.domain.SysForm;
import com.ruoyi.system.mapper.FlowDeployMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class FlowableServiceImpl extends FlowServiceFactory implements IFlowableService {

    @Resource
    private ISysDeployFormService sysDeployFormService;

    @Resource
    private FlowDeployMapper flowDeployMapper;

    @Override
    public Page<FlowProcDefDto> listDefinition(String name, Integer pageNum, Integer pageSize) {
        Page<FlowProcDefDto> page = new Page<>();
        PageHelper.startPage(pageNum, pageSize);
        final List<FlowProcDefDto> dataList = flowDeployMapper.selectDeployList(name);
        List<FlowProcDefDto> returnList = new ArrayList<>();
        // 加载挂表单
        for (FlowProcDefDto procDef : dataList) {
            SysForm sysForm = sysDeployFormService.selectSysDeployFormByDeployId(procDef.getDeploymentId());
            if (Objects.nonNull(sysForm)) {
                procDef.setFormName(sysForm.getFormName());
                procDef.setFormId(sysForm.getFormId());
                returnList.add(procDef);
            }
        }
        page.setTotal(new PageInfo(returnList).getTotal());
        page.setRecords(returnList);
        return page;
    }
}
