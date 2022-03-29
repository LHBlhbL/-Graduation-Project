package com.ruoyi.project.controller;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.flowable.domain.vo.FlowTaskVo;
import com.ruoyi.project.domain.Project;
import com.ruoyi.project.domain.ProjectUserList;
import com.ruoyi.project.service.IFlowProcessService;
import com.ruoyi.project.service.IProjectUserService;
import com.ruoyi.project.service.IRemiService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/reim")
public class ReimController extends BaseController {

    @Autowired
    private IRemiService remiService;

    @Autowired
    private IFlowProcessService processService;

    @Autowired
    private IProjectUserService service;


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

    @ApiOperation(value = "根据流程定义id启动流程实例")
    @PostMapping("/definition/start/{procDefId}/{projectId}")
    public AjaxResult start(@ApiParam(value = "流程定义id") @PathVariable(value = "procDefId") String procDefId,
                            @PathVariable(value = "projectId") Long projectId,
                            @ApiParam(value = "变量集合,json对象") @RequestBody Map<String,Object> variables) {
        return processService.startProcessInstanceById(procDefId, variables,projectId);

    }

    @PutMapping("/process/start")
    public AjaxResult startProcess(@RequestBody ProjectUserList list)
    {
        return AjaxResult.success(service.insertProjectUser(list));
    }

    @ApiOperation(value = "审批任务")
    @PostMapping(value = "/task/complete/{projectId}/{procInsId}")
    public AjaxResult complete(@RequestBody FlowTaskVo flowTaskVo,@PathVariable(value = "projectId")Long projectId,@PathVariable(value = "procInsId")String procInsId) {
        return processService.complete(flowTaskVo,projectId,procInsId);
    }

    @PostMapping("/queryList")
    public TableDataInfo queryList(@RequestBody Project project)
    {
        startPage();
        List<Project> list = remiService.queryList(project);
        return getDataTable(list);
    }

    @GetMapping("/process/stop/{procInsId}")
    public AjaxResult stopProcess(@PathVariable("procInsId") String procInsId){
        return remiService.stopProcess(procInsId);
    }

}
