<template>
  <div>
    <el-row :gutter="15">
      <el-form ref="elForm" :model="formData" :rules="rules" size="medium" label-width="100px">
        <el-col :span="12">
          <el-form-item label="申请人姓名" prop="name">
            <el-input v-model="formData.name" placeholder="请输入申请人姓名申请人姓名申请人姓名申请人姓名申请人姓名申请人姓名" clearable
              :style="{width: '100%'}"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="职工编号" prop="applicant_number">
            <el-input v-model="formData.applicant_number" placeholder="请输入单行文本职工编号职工编号职工编号职工编号职工编号职工编号职工编号"
              clearable :style="{width: '100%'}"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="支付时间" prop="pay_time">
            <el-date-picker v-model="formData.pay_time" format="yyyy-MM-dd" value-format="yyyy-MM-dd"
              :style="{width: '100%'}" placeholder="请选择支付时间" clearable></el-date-picker>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话" prop="applicant_phone">
            <el-input v-model="formData.applicant_phone" placeholder="请输入联系电话" clearable
              :style="{width: '100%'}"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="项目部门" prop="project_deptid">
            <el-select v-model="formData.project_deptid" placeholder="请选择项目部门" clearable
              :style="{width: '100%'}">
              <el-option v-for="(item, index) in project_deptidOptions" :key="index" :label="item.label"
                :value="item.value" :disabled="item.disabled"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="项目名称" prop="project_name">
            <el-select v-model="formData.project_name" placeholder="请选择项目名称" clearable
              :style="{width: '100%'}">
              <el-option v-for="(item, index) in project_nameOptions" :key="index" :label="item.label"
                :value="item.value" :disabled="item.disabled"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="报销金额" prop="pay_money">
            <el-input v-model="formData.pay_money" placeholder="请输入报销金额报销金额" clearable
              :style="{width: '100%'}"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="单据附件" prop="field107">
            <el-upload ref="field107" :file-list="field107fileList" :action="field107Action"
              :before-upload="field107BeforeUpload">
              <el-button size="small" type="primary" icon="el-icon-upload">点击上传</el-button>
            </el-upload>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="说明" prop="field101">
            <el-input v-model="formData.field101" type="textarea" placeholder="请输入说明说明"
              :autosize="{minRows: 4, maxRows: 4}" :style="{width: '100%'}"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item size="large">
            <el-button type="primary" @click="submitForm">提交</el-button>
            <el-button @click="resetForm">重置</el-button>
          </el-form-item>
        </el-col>
      </el-form>
    </el-row>
  </div>
</template>
<script>
export default {
  components: {},
  props: [],
  data() {
    return {
      formData: {
        name: undefined,
        applicant_number: undefined,
        pay_time: null,
        applicant_phone: undefined,
        project_deptid: undefined,
        project_name: undefined,
        pay_money: undefined,
        field107: null,
        field101: undefined,
      },
      rules: {
        name: [{
          required: true,
          message: '请输入申请人姓名申请人姓名申请人姓名申请人姓名申请人姓名申请人姓名',
          trigger: 'blur'
        }],
        applicant_number: [{
          required: true,
          message: '请输入单行文本职工编号职工编号职工编号职工编号职工编号职工编号职工编号',
          trigger: 'blur'
        }],
        pay_time: [{
          required: true,
          message: '请选择支付时间',
          trigger: 'change'
        }],
        applicant_phone: [{
          required: true,
          message: '请输入联系电话',
          trigger: 'blur'
        }],
        project_deptid: [{
          required: true,
          message: '请选择项目部门',
          trigger: 'change'
        }],
        project_name: [{
          required: true,
          message: '请选择项目名称',
          trigger: 'change'
        }],
        pay_money: [{
          required: true,
          message: '请输入报销金额报销金额',
          trigger: 'blur'
        }],
        field101: [{
          required: true,
          message: '请输入说明说明',
          trigger: 'blur'
        }],
      },
      field107Action: 'https://jsonplaceholder.typicode.com/posts/',
      field107fileList: [],
      project_deptidOptions: [{
        "label": "选项一",
        "value": 1
      }, {
        "label": "选项二",
        "value": 2
      }, {
        "label": "",
        "value": ""
      }],
      project_nameOptions: [{
        "label": "选项一",
        "value": 1
      }, {
        "label": "选项二",
        "value": 2
      }],
    }
  },
  computed: {},
  watch: {},
  created() {},
  mounted() {},
  methods: {
    submitForm() {
      this.$refs['elForm'].validate(valid => {
        if (!valid) return
        // TODO 提交表单
      })
    },
    resetForm() {
      this.$refs['elForm'].resetFields()
    },
    field107BeforeUpload(file) {
      let isRightSize = file.size / 1024 / 1024 < 2
      if (!isRightSize) {
        this.$message.error('文件大小超过 2MB')
      }
      return isRightSize
    },
  }
}

</script>
<style>
</style>
