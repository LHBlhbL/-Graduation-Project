<template>
  <div class="app-container">
<!--    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">-->
<!--      <el-form-item label="名称" prop="name">-->
<!--        <el-input-->
<!--          v-model="queryParams.name"-->
<!--          placeholder="请输入名称"-->
<!--          clearable-->
<!--          size="small"-->
<!--          @keyup.enter.native="handleQuery"-->
<!--        />-->
<!--      </el-form-item>-->

<!--      <el-form-item>-->
<!--        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>-->
<!--        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>-->
<!--      </el-form-item>-->
<!--    </el-form>-->
    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>


    <el-table v-loading="loading" :data="myProcessList"  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="报销项目" align="center" prop="projectName" :show-overflow-tooltip="true"/>
      <el-table-column label="申请人" align="center" prop="userName"/>
      <el-table-column label="提交时间" align="center" prop="createTime" width="180"/>
      <el-table-column label="当前节点" align="center" prop="taskName"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
<!--          <el-dropdown>-->
<!--         -->
<!--            <el-dropdown-menu slot="dropdown">-->
<!--              <el-dropdown-item icon="el-icon-tickets" @click.native="handleFlowRecord(scope.row)">-->
<!--                详情-->
<!--              </el-dropdown-item>-->
<!--              <el-dropdown-item v-if="scope.row.finishTime == null" icon="el-icon-circle-close" @click.native="handleStop(scope.row)">-->
<!--                取消申请-->
<!--              </el-dropdown-item>-->
<!--            </el-dropdown-menu>-->
<!--          </el-dropdown>-->
          <el-button
            size="mini"
            type="text"
            icon="el-icon-tickets"
            @click="handleFlowRecord(scope.row)"
            v-hasPermi="['project:processAdmin:detail']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-circle-close"
            @click="handleStop(scope.row)"
            v-hasPermi="['project:processAdmin:stop']"
          >取消申请</el-button>

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



  </div>
</template>
<style lang="scss" scoped>
//去掉表头多选框
::v-deep .el-table .el-checkbox {
  display: none;
}
</style>
<script>
import {
  getDeployment,
  addDeployment,
  updateDeployment,

} from "@/api/flowable/finished";
import {listDefinition} from "@/api/flowable/definition";
import {delProcinst,exportProcinst} from "@/api/system/procinst";
import {processList,stopProcess,processDownList} from "@/api/project/process"
export default {
  name: "Deploy",
  components: {
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      processLoading: true,
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
      processTotal:0,
      // 我发起的流程列表数据
      myProcessList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      src: "",
      definitionList:[],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        category: null,
        key: null,
        tenantId: null,
        deployTime: null,
        derivedFrom: null,
        derivedFromRoot: null,
        parentDeploymentId: null,
        engineVersion: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
      },
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询流程定义列表 */
    getList() {
      this.loading = true;
      processList(this.queryParams).then(response => {
        this.myProcessList = response.data.records;
        this.total = response.data.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
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
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.open = true;
      this.title = "发起流程";
      this.listDefinition();
    },
    listDefinition(){
      listDefinition(this.queryParams).then(response => {
        this.definitionList = response.data.records;
        this.processTotal = response.data.total;
        this.processLoading = false;
      });
    },
    /**  发起流程申请 */
    handleStartProcess(row){
      this.$router.push({ path: '/project/record/index',
        query: {
          deployId: row.deploymentId,
          procDefId:row.id,
          finished: true
          }
      })
    },
    /**  取消流程申请 */
    handleStop(row){
      const id = row.procInsId ;
      this.$confirm('是否取消编号为"' + id + '"的流程申请?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return  stopProcess(row.procInsId);
      }).then(() => {
        this.msgSuccess("取消成功");
      })
    },
    /** 流程流转记录 */
    handleFlowRecord(row){
      this.$router.push({ path: '/project/record/index',
        query: {
          procInsId: row.procInsId,
          deployId: row.deployId,
          taskId: row.taskId,
          finished: false
      }})
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
          if (this.form.id != null) {
            updateDeployment(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addDeployment(this.form).then(response => {
              this.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.procInsId ;
      this.$confirm('是否确认删除编号为"' + ids + '"的数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return delProcinst(ids);
      }).then(() => {
        this.getList();
        this.msgSuccess("删除成功");
      })
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出数据项?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return exportProcinst(queryParams);
      }).then(response => {
        this.download(response.msg);
      })
    }
  }
};
</script>

