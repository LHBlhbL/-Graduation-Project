package com.ruoyi.project.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.BaseEntity;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.flowable.domain.dto.FlowTaskDto;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.service.IFlowProcessService;
import com.ruoyi.project.service.IRemiService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/reim")
public class ReimController extends BaseController {

    @Autowired
    private IRemiService remiService;

    @Autowired
    private IFlowProcessService processService;


    @GetMapping("/list")
    public TableDataInfo reimlist()
    {
        startPage();
        List<Project> list = remiService.listRemi();
        return getDataTable(list);
    }

    @GetMapping("process/list")
    public AjaxResult onGoingList(@RequestParam Integer pageNum,@RequestParam Integer pageSize){
        return processService.processList(pageNum, pageSize);
    }

    @GetMapping(value = "/todoList")
    public AjaxResult todoList(@ApiParam(value = "当前页码", required = true) @RequestParam Integer pageNum,
                               @ApiParam(value = "每页条数", required = true) @RequestParam Integer pageSize) {
        return processService.todoList(pageNum, pageSize);
    }

}
