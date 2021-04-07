<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="开始时间" prop="deployTime">
        <el-date-picker clearable size="small"
                        v-model="queryParams.deployTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="选择时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:deployment:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:deployment:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:deployment:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:deployment:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="todoList" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="任务编号" width="300" align="center" prop="taskId"/>
      <el-table-column label="流程名称" align="center" prop="procDefName"/>
      <el-table-column label="任务节点" align="center" prop="taskName"/>
      <el-table-column label="流程版本" align="center">
        <template slot-scope="scope">
          <el-tag size="medium" >v{{scope.row.procDefVersion}}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="流程发起人" align="center">
        <template slot-scope="scope">
          <label>{{scope.row.startUserName}} <el-tag type="info" size="mini">{{scope.row.startDeptName}}</el-tag></label>
        </template>
      </el-table-column>
      <el-table-column label="接收时间" align="center" prop="createTime" width="180"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit-outline"
            @click="handleProcess(scope.row)"
          >处理
          </el-button>
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-edit-outline"-->
<!--            @click="handleComplete(scope.row)"-->
<!--          >审批-->
<!--          </el-button>-->
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-refresh-left"-->
<!--            @click="handleReturn(scope.row)"-->
<!--          >退回-->
<!--          </el-button>-->
<!--          <el-button-->
<!--            size="mini"-->
<!--            type="text"-->
<!--            icon="el-icon-circle-close"-->
<!--            @click="handleReject(scope.row)"-->
<!--          >驳回-->
<!--          </el-button>-->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:deployment:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 审批任务 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="任务编号" prop="taskId">
          <el-input disabled v-model="form.taskId"/>
        </el-form-item>
        <el-form-item label="意见内容" prop="comment">
          <el-input type="textarea" v-model="form.comment" placeholder="请输入意见"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 驳回任务 -->
    <el-dialog :title="rejectTask.title" :visible.sync="rejectTask.open" width="500px" append-to-body>
      <el-form ref="rejectTask" :model="rejectTask" label-width="80px">
        <el-form-item label="任务编号" prop="taskId">
          <el-input disabled v-model="rejectTask.taskId"/>
        </el-form-item>
        <el-form-item label="意见内容" prop="comment">
          <el-input type="textarea" v-model="rejectTask.comment" placeholder="请输入意见"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitRejectForm">确 定</el-button>
        <el-button @click="cancelReject">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 退回任务 -->
    <el-dialog :title="returnTaskData.title" :visible.sync="returnTaskData.open" width="500px" append-to-body>
      <el-table
        v-loading="loading"
        :data="returnList"
        @current-change="handleCurrentChange"
        highlight-current-row
        border
      >
        <el-table-column width="55" align="center" label="选择">
          <template scope="scope">
            <el-radio :label="scope.$index" v-model="radio" @change.native="handleRadioChoice(scope.row)">{{""}}</el-radio>
          </template>
        </el-table-column>
        <el-table-column label="退回key" align="center" prop="id"/>
        <el-table-column label="退回节点" align="center" prop="name"/>
      </el-table>
      <el-divider></el-divider>
      <label>意见</label><el-input type="textarea" v-model="taskComment" placeholder="请输入意见"/>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitReturnTask">确 定</el-button>
        <el-button @click="cancelTask">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {
  todoList,
  complete,
  returnList,
  returnTask,
  rejectTask,
  getDeployment,
  delDeployment,
  exportDeployment
} from "@/api/flowable/todo";

export default {
  name: "Deploy",
  components: {},
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 流程待办任务表格数据
      todoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      radio: "",
      // 回退列表数据
      returnList: [],
      // 回退请求参数
      returnTaskData: {},
      // 退回意见
      taskComment: "",
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        category: null
      },
      // 表单参数
      form: {},
      rejectTask:{},
      // 表单校验
      rules: {}
    };
  },
  created() {
    this.getList();

  },
  methods: {
    /** 查询流程定义列表 */
    getList() {
      this.loading = true;
      todoList(this.queryParams).then(response => {
        this.todoList = response.data.records;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    // 跳转到处理页面
    handleProcess(row){
      this.$router.push({ path: '/flowable/task/record/index',
        query: {
          procInsId: row.procInsId,
          deployId: row.deployId,
          taskId: row.taskId,
          isFinished: 0
        }})
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 取消驳回任务按钮
    cancelReject() {
      this.rejectTask.open = false;
      this.rejectTask = {};
    },
    // 取消任务按钮
    cancelTask() {
      this.returnTaskData.open = false;
      this.radio = "";
      this.getList();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        name: null,
        category: null,
        key: null,
        tenantId: null,
        deployTime: null,
        derivedFrom: null,
        derivedFromRoot: null,
        parentDeploymentId: null,
        engineVersion: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    // 单选框选中
    handleRadioChoice(row){
      this.returnTaskData.targetKey =  row.id
    },
    handleCurrentChange(val) {
      if (val) {
        let index = this.returnList.findIndex(
          item => item.id === val.id
        )
        if (index > -1) {
          this.radio = index;
        }
        this.$emit('data', val.pkg)
      }
      this.returnTaskData.targetKey =  val.id
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加流程定义";
    },
    /** 审批任务*/
    handleComplete(row) {
      this.form = {
        instanceId: row.procInsId,
        taskId: row.taskId
      }
      this.open = true;
      this.title = "审批任务";
    },
    /** 可退回任务列表 */
    handleReturn(row) {
      const params = {
        taskId: row.taskId
      }
      returnList(params).then(res => {
        this.returnList = res.data;
        this.returnTaskData.taskId = params.taskId
        this.returnTaskData.open = true;
        this.returnTaskData.title = "可退回任务列表";
      })
    },
    /** 驳回任务 */
    handleReject(row) {
      this.rejectTask = {
        instanceId: row.procInsId,
        taskId: row.taskId
      }
      this.rejectTask.open = true;
      this.rejectTask.title = "驳回任务";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getDeployment(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改流程定义";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          complete(this.form).then(response => {
            this.msgSuccess(response.msg);
            this.open = false;
            this.getList();
          })
        }
      });
    },
    /** 提交退回任务*/
    submitReturnTask(){
      const params = {
        taskId: this.returnTaskData.taskId,
        targetKey: this.returnTaskData.targetKey
      }
      returnTask(params).then(res => {
        this.returnTaskData.open = false;
        this.msgSuccess(res.msg);
        this.radio = "";
        this.getList();
      })
    },
    /** 提交驳回任务*/
    submitRejectForm(){
      const params = {
        taskId: this.rejectTask.taskId,
        comment: this.rejectTask.comment
      }
      rejectTask(params).then(res => {
        this.msgSuccess(res.msg);
        this.rejectTask.open = false;
        this.getList();
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$confirm('是否确认删除流程定义编号为"' + ids + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function () {
        return delDeployment(ids);
      }).then(() => {
        this.getList();
        this.msgSuccess("删除成功");
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有流程定义数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function () {
        return exportDeployment(queryParams);
      }).then(response => {
        this.download(response.msg);
      })
    }
  }
};
</script>

