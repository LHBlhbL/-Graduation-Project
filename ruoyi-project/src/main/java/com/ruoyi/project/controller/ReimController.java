package com.ruoyi.project.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.service.IRemiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/reim")
public class ReimController extends BaseController {

    @Autowired
    private IRemiService remiService;

    @GetMapping("/list")
    public TableDataInfo reimlist()
    {
        startPage();
        List<Project> list = remiService.listRemi();
        return getDataTable(list);
    }
}
