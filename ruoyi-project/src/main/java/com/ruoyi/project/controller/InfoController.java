package com.ruoyi.project.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.service.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/Info")
public class InfoController extends BaseController {

    @Autowired
    private IProjectService projectService;

    /**
     * 查询项目列表
     */
    @GetMapping("/list")
    public TableDataInfo list() {
        startPage();
        List<Project> list = projectService.selectProjectListPrincipal();
        return getDataTable(list);
    }
}
