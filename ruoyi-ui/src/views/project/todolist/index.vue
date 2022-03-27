<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="项目编号" prop="projectId">
        <el-input
          v-model="queryParams.projectId"
          placeholder="请输入项目编号"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="项目名称" prop="projectName">
        <el-input
          v-model="queryParams.projectName"
          placeholder="请输入项目名称"
          clearable
          size="small"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="todoList">
      <div v-if="!status">
        <el-table-column type="selection" width="55" align="center"/>
        <el-table-column label="项目编号" align="center" prop="projectId"/>
        <el-table-column label="项目名称" align="center" prop="projectName"/>
        <el-table-column label="报销发起人" align="center" prop="startUserName"/>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="startProcess(scope.row)"
            >处理
            </el-button>
          </template>
        </el-table-column>
      </div>
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
import {listProject, getProject, addProject, updateProject, userTreeselect, getProcess} from "@/api/project/list";
import {todoList} from "@/api/project/reimbursement"
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";

export default {
  name: "Project",
  components: {
    Treeselect
  },
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
      todoList: [],
      // 总条数
      total: 0,
      // 【请填写功能名称】表格数据
      projectList: [],
      deptOptions: undefined,
      principalOptions: undefined,
      // 弹出层标题
      title: "",
      queryParams: {
        pageNum: 1,
        pageSize: 10,
      },
      // 是否显示弹出层
      open: false,
      // 表单参数
      form: {},
      // 表单校验
      rules: {}
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询【请填写功能名称】列表 */
    getList() {
      this.loading = true;
      todoList(this.queryParams).then(response => {
        this.todoList = response.data.records;
        this.total = response.total;
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
        projectId: null,
        deptId: null,
        projectName: null,
        principalId: null,
        expensesTotal: null,
        expensesLeft: null,
        status: "0",
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    startProcess(row) {
      this.$router.push({
        path: '/project/record/index',
        query: {
          procInsId: row.procInsId,
          deployId: row.deployId,
          taskId: row.taskId,
          projectId:row.projectId,
          finished: true
        }
      })

    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },


  }
};
</script>
