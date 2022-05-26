package com.ruoyi.project.controller;

import java.util.List;

import io.swagger.annotations.ApiParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.project.domain.ProjectHistory;
import com.ruoyi.project.service.IProjectHistoryService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2022-03-24
 */
@RestController
@RequestMapping("/project/history")
public class ProjectHistoryController extends BaseController
{
    @Autowired
    private IProjectHistoryService projectHistoryService;

    /**
     * 查询【请填写功能名称】列表
     */
    @GetMapping("/list")
    public TableDataInfo list(@RequestParam Integer pageNum,@RequestParam Integer pageSize)
    {
        startPage();
        List<ProjectHistory> histories = projectHistoryService.selectProjectHistoryList(pageNum, pageSize);
        return getDataTable(histories);
    }




    /**
     * 导出【请填写功能名称】列表
     */


    /**
     * 获取【请填写功能名称】详细信息
     */
    @PreAuthorize("@ss.hasPermi('project:history:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return AjaxResult.success(projectHistoryService.selectProjectHistoryById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('project:history:add')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ProjectHistory projectHistory)
    {
        return toAjax(projectHistoryService.insertProjectHistory(projectHistory));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('project:history:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ProjectHistory projectHistory)
    {
        return toAjax(projectHistoryService.updateProjectHistory(projectHistory));
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('project:history:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable String id)
    {
        return toAjax(projectHistoryService.deleteProjectHistoryByProc(id));
    }

    @GetMapping(value = "/finishedList")
    public AjaxResult finishedList(@ApiParam(value = "当前页码", required = true) @RequestParam Integer pageNum,
                                   @ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize) {
        return projectHistoryService.finishedList(pageNum, pageSize);
    }
}
