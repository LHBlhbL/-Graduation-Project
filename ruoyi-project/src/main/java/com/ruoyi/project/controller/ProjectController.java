package com.ruoyi.project.controller;

import java.util.List;

import com.ruoyi.flowable.service.IFlowDefinitionService;
import com.ruoyi.project.domain.ProjectFlow;
import com.ruoyi.project.service.IFlowableService;
import com.ruoyi.project.service.IProjectFlowService;
import com.ruoyi.system.domain.FlowProcDefDto;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.service.IProjectService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 *
 * @author ruoyi
 * @date 2022-03-02
 */
@RestController
@RequestMapping("/project")
public class ProjectController extends BaseController {
    @Autowired
    private IProjectService projectService;

    @Autowired
    private IProjectFlowService flowService;

    @Autowired
    private IFlowableService flowableService;


    /**
     * 查询项目列表
     */
    @GetMapping("/list")
    public TableDataInfo list(Project project) {
        startPage();
        List<Project> list = projectService.selectProjectList(project);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:project:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(Project project) {
        List<Project> list = projectService.selectProjectList(project);
        ExcelUtil<Project> util = new ExcelUtil<Project>(Project.class);
        return util.exportExcel(list, "project");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:project:query')")
    @GetMapping(value = "/{projectId}")
    public AjaxResult getInfo(@PathVariable("projectId") Long projectId) {
        return AjaxResult.success(projectService.selectProjectById(projectId));
    }

    /**
     * 新增【请填写功能名称】
     */
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Project project) {
        return toAjax(projectService.insertProject(project));
    }

    /**
     * 修改【请填写功能名称】
     */
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Project project) {
        return toAjax(projectService.updateProject(project));
    }

    /**
     * 删除【请填写功能名称】
     */
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
    @DeleteMapping("/{projectIds}")
    public AjaxResult remove(@PathVariable Long[] projectIds) {
        return toAjax(projectService.deleteProjectByIds(projectIds));
    }

    @GetMapping(value = "/userTreeselect/{deptId}")
    public AjaxResult userTreeselect(@PathVariable("deptId") Long deptId) {
        return AjaxResult.success(projectService.buildUserTree(deptId));
    }

    @Log(title = "状态转换", businessType = BusinessType.UPDATE)
    @PutMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody Project project) {
        return AjaxResult.success(projectService.updateProjectStatus(project));
    }

    @Log(title = "流程配置", businessType = BusinessType.INSERT)
    @PutMapping("/flow/add")
    public AjaxResult addFlow(@RequestBody ProjectFlow projectFlow) {
        return AjaxResult.success(flowService.insertProjectFlow(projectFlow));
    }

    @PutMapping("/flow/form")
    public AjaxResult getForm(@RequestBody Long projectId) {
        return AjaxResult.success(flowService.selectProjectFlowByPId(projectId));
    }

    @GetMapping(value = "/definition/list")
    public AjaxResult list(@ApiParam(value = "当前页码", required = true) @RequestParam Integer pageNum,
                           @ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize,
                           @ApiParam(value = "流程名称", required = false) @RequestParam(required = false) String name) {
        return AjaxResult.success(flowableService.listDefinition(name,pageNum, pageSize));
    }


}
