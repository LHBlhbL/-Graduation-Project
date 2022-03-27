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
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:project:add']"
        >新增</el-button>
      </el-col>



      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="projectList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="项目编号" align="center" prop="projectId" />
      <el-table-column label="项目部门" align="center" prop="dept.deptName" />
      <el-table-column label="项目名称" align="center" prop="projectName" />
      <el-table-column label="项目负责人" align="center" prop="principal.principalName" />
      <el-table-column label="项目经费" align="center" prop="expensesTotal" />
      <el-table-column label="剩余经费" align="center" prop="expensesLeft" />
      <el-table-column label="流程名称" align="center"  >
        <template slot-scope="scope">
          <el-button v-if="scope.row.version" type="text" >
            <span>{{ scope.row.procName }}</span>
          </el-button>
          <label v-else>未配置流程</label>
        </template>
      </el-table-column>
      <el-table-column label="流程版本" align="center" width="80px">
        <template slot-scope="scope">
          <el-tag size="medium" >v{{ scope.row.version }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" key="status" >
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          ></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-share"
            @click="handleConfigure(scope.row)"
          >配置</el-button>
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

    <!-- 添加或修改项目对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="项目所属部门" prop="deptId">
          <treeselect v-model="form.deptId" :options="deptOptions" :show-count="true" @input="updatePrincipals" placeholder="请选择所属部门" />
        </el-form-item>
        <el-form-item label="项目名称" prop="projectName">
          <el-input v-model="form.projectName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="负责人" prop="principalID">
          <treeselect v-model="form.principalId" :options="principalOptions"  :show-count="true" placeholder="请输入项目负责人"/>
        </el-form-item>
        <el-form-item label="项目经费" prop="expensesTotal">
          <el-input v-model="form.expensesTotal" placeholder="请输入项目经费" />
        </el-form-item>
        <el-col :span="12">
          <el-form-item label="状态">
            <el-radio-group v-model="form.status">
              <el-radio
                v-for="dict in statusOptions"
                :key="dict.dictValue"
                :label="dict.dictValue"
              >{{dict.dictLabel}}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
<!--        <el-form-item label="${comment}" prop="expensesLeft">-->
<!--          <el-input v-model="form.expensesLeft" placeholder="请输入${comment}" />-->
<!--        </el-form-item>-->
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog :title="title" :visible.sync="openConfigure" width="60%" append-to-body>
      <el-table v-loading="processLoading" fit :data="definitionList" border >
        <el-table-column label="流程名称" align="center" prop="name" />
        <el-table-column label="流程版本" align="center">
          <template slot-scope="scope">
            <el-tag size="medium" >v{{ scope.row.version }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="流程分类" align="center" prop="category" />
        <el-table-column label="操作" align="center" width="300" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-edit-outline"
              @click="handleConfProcess(scope.row)"
            >配置流程</el-button>
          </template>
        </el-table-column>
      </el-table>
      <pagination
        v-show="processTotal>0"
        :total="processTotal"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="listDefinition"
      />
    </el-dialog>
  </div>
</template>
<style lang="scss" scoped>
//去掉表头多选框
::v-deep .el-table .el-checkbox {
  display: none;
}
</style>
<script>
import { listProject,listDefinitionFlow, changeProjectStatus,getProject, delProject, addProject, updateProject, exportProject,userTreeselect,addProjectPro } from "@/api/project/list";
import { treeselect } from "@/api/system/dept";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
export default {
  name: "Project",
  components: {Treeselect
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
      // 总条数
      total: 0,
      // 【请填写功能名称】表格数据
      projectList: [],
      deptOptions: undefined,
      principalOptions: undefined,
      definitionList:undefined,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      openConfigure:false,
      projectIdPro: undefined,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deptId: null,
        projectName: null,
        principalId: null,
        expensesTotal: null,
        expensesLeft: null,
        status:null
      },
      queryParamsCon: {
        pageNum: 1,
        pageSize: 10
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        expensesTotal:[{
          pattern: /^0{1}(\.\d*)|(^[1-9][0-9]*)+(\.\d*)?$/,
          message: "请输入正确金额",
          trigger: "blur"
        }]
      }
    };
  },
  created() {
    this.getList();
    this.getDicts("sys_normal_disable").then(response => {
      this.statusOptions = response.data;
    });
  },
  methods: {
    /** 查询【请填写功能名称】列表 */
    getList() {
      this.loading = true;
      listProject(this.queryParams).then(response => {
        this.projectList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    handleStatusChange(row) {
      let text = row.status === "0" ? "启用" : "停用";
      this.$confirm('确认要"' + text + '""' + row.projectName + '"项目吗?', "警告", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(function() {
        return changeProjectStatus(row.projectId, row.status);
      }).then(() => {
        this.msgSuccess(text + "成功");
      }).catch(function() {
        row.status = row.status === "0" ? "1" : "0";
      });
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
        status:"0",
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
    /** 查询部门下拉树结构 */
    getTreeselect() {
      treeselect().then(response => {
        this.deptOptions = response.data;
      });
    },
    updatePrincipals(){
      this.getPrincipals();
    },
    getPrincipals(){
      if(this.form.deptId!=null){
        userTreeselect(this.form.deptId).then(response => {
            this.principalOptions=response.data;
          }
        );
      }
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.projectId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加项目";
      this.getTreeselect();
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const projectId = row.projectId || this.ids
      getProject(projectId).then(response => {
        this.form = response.data;
        this.open = true;
        this.getTreeselect();
        this.getPrincipals();
        this.title = "修改【请填写功能名称】";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.projectId != null) {
            updateProject(this.form).then(response => {
              this.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addProject(this.form).then(response => {
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
      const projectIds = row.projectId;
      this.$confirm('是否确认删除【请填写功能名称】编号为"' + projectIds + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return delProject(projectIds);
        }).then((res) => {
          this.getList();
          this.msg(res.msg);
        })
    },
    handleConfigure(row){
      this.openConfigure = true;
      this.title = "配置流程";
      this.projectIdPro = row.projectId;
      this.listDefinition();
    },
    listDefinition(){
      listDefinitionFlow(this.queryParamsCon).then(response => {
        this.definitionList = response.data.records;
        this.processTotal = response.data.total;
        this.processLoading = false;
      });
    },
    handleConfProcess(row) {
      addProjectPro(this.projectIdPro,row.deploymentId,row.id).then(res => {
        this.msgSuccess(res.msg);
        this.openConfigure=false;
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      const queryParams = this.queryParams;
      this.$confirm('是否确认导出所有【请填写功能名称】数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function() {
          return exportProject(queryParams);
        }).then(response => {
          this.download(response.msg);
        })
    }
  }
};
</script>
