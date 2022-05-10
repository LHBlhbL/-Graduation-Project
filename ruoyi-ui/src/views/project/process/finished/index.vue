<template>
  <div class="app-container">

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>


    <el-table v-loading="loading" :data="myProcessList"  @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="报销项目" align="center" prop="projectName" :show-overflow-tooltip="true"/>
      <el-table-column label="提交时间" align="center" prop="createTime" width="180"/>
      <el-table-column label="完成时间" align="center" prop="finishTime" width="180"/>
      <el-table-column label="耗时" align="center" prop="duration" width="180"/>
      <el-table-column label="报销状态" align="center" width="100">
        <template slot-scope="scope">
          <el-tag type="success"  size="mini">已完成</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-tickets"
            @click="handleFlowRecord(scope.row)"
            v-hasPermi="['project:finished:detail']"
          >详情</el-button>
        </template>
      </el-table-column>
    </el-table>
<!--
    <el-row>
      <el-col>
        <p>总计</p>
      </el-col>
    </el-row>-->

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
import {processFinishedList} from "@/api/project/process"
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
      processFinishedList(this.queryParams).then(response => {
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
    /** 流程流转记录 */
    handleFlowRecord(row){
      this.$router.push({ path: '/flowable/task/record/index',
        query: {
          procInsId: row.procInsId,
          deployId: row.deployId,
          taskId: row.taskId,
          finished: false
      }})
    },
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
  }
};
</script>

