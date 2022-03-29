package com.ruoyi.project.controller;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public TableDataInfo list(ProjectHistory projectHistory)
    {
        startPage();
        List<ProjectHistory> list = projectHistoryService.selectProjectHistoryList(projectHistory);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('project:history:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ProjectHistory projectHistory)
    {
        List<ProjectHistory> list = projectHistoryService.selectProjectHistoryList(projectHistory);
        ExcelUtil<ProjectHistory> util = new ExcelUtil<ProjectHistory>(ProjectHistory.class);
        return util.exportExcel(list, "history");
    }

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
}
