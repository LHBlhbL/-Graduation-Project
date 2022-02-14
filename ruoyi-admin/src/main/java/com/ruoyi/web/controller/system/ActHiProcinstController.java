package com.ruoyi.web.controller.system;

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
import com.ruoyi.system.domain.ActHiProcinst;
import com.ruoyi.system.service.IActHiProcinstService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 【请填写功能名称】Controller
 * 
 * @author ruoyi
 * @date 2022-02-15
 */
@RestController
@RequestMapping("/system/procinst")
public class ActHiProcinstController extends BaseController
{
    @Autowired
    private IActHiProcinstService actHiProcinstService;

    /**
     * 查询【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:procinst:list')")
    @GetMapping("/list")
    public TableDataInfo list(ActHiProcinst actHiProcinst)
    {
        startPage();
        List<ActHiProcinst> list = actHiProcinstService.selectActHiProcinstList(actHiProcinst);
        return getDataTable(list);
    }

    /**
     * 导出【请填写功能名称】列表
     */
    @PreAuthorize("@ss.hasPermi('system:procinst:export')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(ActHiProcinst actHiProcinst)
    {
        List<ActHiProcinst> list = actHiProcinstService.selectActHiProcinstList(actHiProcinst);
        ExcelUtil<ActHiProcinst> util = new ExcelUtil<ActHiProcinst>(ActHiProcinst.class);
        return util.exportExcel(list, "procinst");
    }

    /**
     * 获取【请填写功能名称】详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:procinst:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return AjaxResult.success(actHiProcinstService.selectActHiProcinstById(id));
    }

    /**
     * 新增【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:procinst:add')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ActHiProcinst actHiProcinst)
    {
        return toAjax(actHiProcinstService.insertActHiProcinst(actHiProcinst));
    }

    /**
     * 修改【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:procinst:edit')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ActHiProcinst actHiProcinst)
    {
        return toAjax(actHiProcinstService.updateActHiProcinst(actHiProcinst));
    }

    /**
     * 删除【请填写功能名称】
     */
    @PreAuthorize("@ss.hasPermi('system:procinst:remove')")
    @Log(title = "【请填写功能名称】", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable String[] ids)
    {
        System.out.println(ids+"}}}}}}}}}}}");
        return toAjax(actHiProcinstService.deleteActHiProcinstByIds(ids));
    }
}
