<template>
  <div class="app-container">
        <el-card class="box-card" >
              <div slot="header" class="clearfix">
                <span class="el-icon-document">基础信息</span>
                <el-button style="float: right;" type="primary" @click="goBack">返回</el-button>
              </div>

            <!--流程表单填写数据-->
            <el-col :span="16" :offset="8" v-if="variableOpen">
                <el-form style="margin-bottom: 20px;font-size: 14px"  ref="variablesForm"  label-width="80px" size="mini">
                  <div v-for="item in variables">
                      <el-form-item :label="item.label">
                        <label v-if="item.val instanceof Array" style="color:#8a909c;font-weight: normal">{{item.val[0]}}  至  {{item.val[1]}}</label>
                        <label v-else style="color:#8a909c;font-weight: normal">{{item.val}}</label>
                      </el-form-item>
                  </div>
                </el-form>

              <!--审批意见填写-->
              <div style="margin-bottom: 20px;font-size: 14px;" v-if="finished">
                <el-form ref="taskForm" :model="taskForm" label-width="80px" size="mini">
                  <el-form-item label="退回节点" prop="targetKey" v-show="taskForm.returnTaskShow">
                    <el-radio-group v-model="taskForm.targetKey">
                      <<el-radio-button
                        v-for="item in returnTaskList"
                        :key="item.id"
                        :label="item.id"
                      >{{item.name}}</el-radio-button>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="审批意见" prop="comment" :rules="[{ required: true, message: '请输入意见', trigger: 'blur' }]">
                    <el-input style="width: 30%;" type="textarea" v-model="taskForm.comment" placeholder="请输入意见"/>
                  </el-form-item>
                  <el-form-item>
                    <div  v-show="taskForm.defaultTaskShow">
                      <el-button  icon="el-icon-edit-outline" type="primary" size="mini" @click="handleComplete">审批</el-button>
                      <el-button  icon="el-icon-refresh-left" type="warning" size="mini" @click="handleReturn">退回</el-button>
                      <el-button  icon="el-icon-circle-close" type="danger" size="mini" @click="handleReject">驳回</el-button>
                    </div>
                    <div v-show="taskForm.returnTaskShow">
                      <el-button type="primary" @click="submitReturnTask">确 定</el-button>
                      <el-button @click="cancelTask">取 消</el-button>
                    </div>
                  </el-form-item>
                </el-form>
              </div>
            </el-col>

            <!--初始化流程加载表单信息-->
            <el-col :span="16" :offset="4" v-if="formConfOpen">
              <div class="test-form">
                <parser :key="new Date().getTime()"  :form-conf="formConf" @submit="submitForm" ref="parser" @getData="getData" />
              </div>
            </el-col>
        </el-card>

      <!--流程流转记录-->
       <el-card class="box-card" v-if="flowRecordList">
          <div slot="header" class="clearfix">
            <span class="el-icon-notebook-1">审批记录</span>
          </div>
          <el-col :span="16" :offset="4" >
            <div class="block">
              <el-timeline>
                <el-timeline-item
                  v-for="(item,index ) in flowRecordList"
                  :key="index"
                  :icon="setIcon(item.finishTime)"
                  :color="setColor(item.finishTime)"
                >
                  <p style="font-weight: 700">{{item.taskName}}</p>
                  <el-card :body-style="{ padding: '10px' }">
                    <label v-if="item.assigneeName" style="font-weight: normal;margin-right: 30px;">实际办理： {{item.assigneeName}} <el-tag type="info" size="mini">{{item.deptName}}</el-tag></label>
                    <label v-if="item.candidate" style="font-weight: normal;margin-right: 30px;">候选办理： {{item.candidate}}</label>
                    <label style="font-weight: normal">接收时间： </label><label style="color:#8a909c;font-weight: normal">{{item.createTime}}</label>
                    <label style="margin-left: 30px;font-weight: normal">办结时间： </label><label style="color:#8a909c;font-weight: normal">{{item.finishTime}}</label>
                    <label style="margin-left: 30px;font-weight: normal">耗时： </label><label style="color:#8a909c;font-weight: normal">{{item.duration}}</label>

                    <p  v-if="item.comment">
                      <el-tag type="success" v-if="item.comment.type === '1'">  {{item.comment.comment}}</el-tag>
                      <el-tag type="warning" v-if="item.comment.type === '2'">  {{item.comment.comment}}</el-tag>
                      <el-tag type="danger" v-if="item.comment.type === '3'">  {{item.comment.comment}}</el-tag>
                    </p>
                  </el-card>
                </el-timeline-item>
              </el-timeline>
            </div>
          </el-col>
      </el-card>

      <!--流程执行图-->
      <el-card class="box-card" v-if="src" >
        <div slot="header" class="clearfix">
          <span class="el-icon-picture-outline">流程图</span>
        </div>
        <el-col :span="16" :offset="4">
          <el-image :src="src"></el-image>
        </el-col>
      </el-card>
  </div>
</template>

<script>
import { flowRecord } from "@/api/flowable/finished";
import Parser from '@/components/parser/Parser'
import {definitionStart, getProcessVariables } from "@/api/flowable/definition";
import {complete, rejectTask, returnList, returnTask} from "@/api/flowable/todo";
export default {
  name: "Record",
  components: {
    Parser
  },
  props: {},
  data() {
    return {
      // 遮罩层
      loading: true,
      flowRecordList: [], // 流程流转数据
      formConfCopy: {},
      src: null,
      rules: {}, // 表单校验
      variablesForm: {}, // 流程变量数据
      taskForm:{
        returnTaskShow: false, // 是否展示回退表单
        defaultTaskShow: true, // 默认处理按钮
        comment:"", // 意见内容
        procInsId: "", // 流程实例编号
        instanceId: "", // 流程实例编号
        deployId: "",  // 流程定义编号
        taskId: "" ,// 流程任务编号
        procDefId: "",  // 流程编号
      },
      formConf: {}, // 默认表单数据
      formConfOpen: false, // 是否加载默认表单数据
      variables: [], // 流程变量数据
      variableOpen: false, // 是否加载流程变量数据
      returnTaskList: [],  // 回退列表数据
      finished: false,
      isFinished: 0
    };
  },
  created() {
    this.taskForm.procInsId = this.$route.query && this.$route.query.procInsId;
    this.taskForm.instanceId = this.$route.query && this.$route.query.procInsId;
    // 初始化表单
    this.taskForm.deployId = this.$route.query && this.$route.query.deployId;
    this.taskForm.procDefId  = this.$route.query && this.$route.query.procDefId;
    this.isFinished =  this.$route.query && this.$route.query.isFinished

    // 流程任务重获取变量表单
    this.taskForm.taskId  = this.$route.query && this.$route.query.taskId;
    if (this.taskForm.taskId){
      this.processVariables( this.taskForm.taskId)
      this.taskForm.deployId = null
    }
    this.getFlowRecordList( this.taskForm.procInsId, this.taskForm.deployId);
  },
  mounted() {
    // // 表单数据回填，模拟异步请求场景
    // setTimeout(() => {
    //   // 请求回来的表单数据
    //   const data = {
    //     field102: '18836662555'
    //   }
    //   // 回填数据
    //   this.fillFormData(this.formConf, data)
    //   // 更新表单
    //   this.key = +new Date().getTime()
    // }, 1000)
  },
  methods: {
    setIcon(val) {
      if (val) {
        return "el-icon-check";
      } else {
        return "el-icon-time";
      }
    },
    setColor(val) {
      if (val) {
        return "#2bc418";
      } else {
        return "#b3bdbb";
      }
    },
    /** 流程流转记录 */
    getFlowRecordList(procInsId, deployId) {
      const params = {procInsId: procInsId, deployId: deployId}
      flowRecord(params).then(res => {
        this.flowRecordList = res.data.flowList;
        // 流程过程中不存在初始化表单 直接读取的流程变量众存储的表单值
        if (res.data.formData) {
          this.formConf = res.data.formData;
          this.formConfOpen = true
        }
        // 处理已办任务页面跳转后会显示审批等操作按钮 todo: 待优化
        if (this.isFinished == 0) {
          this.finished = res.data.finished;
        }
        if (procInsId) {
          this.src = process.env.VUE_APP_BASE_API + "/flowable/task/diagram/" + procInsId;
        }
      })
    },
    fillFormData(form, data) {
      form.fields.forEach(item => {
        const val = data[item.__vModel__]
        if (val) {
          item.__config__.defaultValue = val
        }
      })
    },
    /** 获取流程变量内容 */
    processVariables(taskId) {
      if (taskId) {
        getProcessVariables(taskId).then(res => {
          this.variables = res.data.variables;
          this.variableOpen = true
        });
      }
    },
    /** 审批任务 */
    handleComplete() {
      this.$refs["taskForm"].validate(valid => {
        if (valid) {
          complete(this.taskForm).then(response => {
            this.msgSuccess(response.msg);
            this.goBack();
          });
        }
      });
    },
    /** 返回页面 */
    goBack() {
      // 关闭当前标签页并返回上个页面
      this.$store.dispatch("tagsView/delView", this.$route);
      this.$router.go(-1)
    },
    /** 接收子组件传的值 */
    getData(data) {
      if (data) {
        const variables = [];
        data.fields.forEach(item => {
          let variableData = {};
          variableData.label = item.__config__.label
          // 表单值为多个选项时
          if (item.__config__.defaultValue instanceof Array) {
            const array = [];
            item.__config__.defaultValue.forEach(val => {
              array.push(val)
            })
            variableData.val = array;
          } else {
            variableData.val = item.__config__.defaultValue
          }
          variables.push(variableData)
        })
        this.variables = variables;
      }
    },
    /** 申请流程表单数据提交 */
    submitForm(data) {
      if (data) {
        const variableList = [];
        data.fields.forEach(item => {
          let variableData = {};
          variableData.label = item.__config__.label
          // 表单值为多个选项时
          if (item.__config__.defaultValue instanceof Array) {
            const array = [];
            item.__config__.defaultValue.forEach(val => {
              array.push(val)
            })
            variableData.val = array;
          } else {
            variableData.val = item.__config__.defaultValue
          }
          variableList.push(variableData)
        })
        if (this.taskForm.procDefId) {
          let variables = {
            "variables": variableList
          }
          // 启动流程并将表单数据加入流程变量
          definitionStart(this.taskForm.procDefId, JSON.stringify(variables)).then(res => {
            this.msgSuccess(res.msg);
            this.goBack();
          })
        }
      }
    },
    /** 驳回任务 */
    handleReject() {
      this.$refs["taskForm"].validate(valid => {
        if (valid) {
          rejectTask(this.taskForm).then(res => {
            this.msgSuccess(res.msg);
            this.goBack();
          });
        }
      });
    },
    /** 可退回任务列表 */
    handleReturn() {
      returnList(this.taskForm).then(res => {
        this.returnTaskList = res.data;
        this.taskForm.returnTaskShow = true;
        this.taskForm.defaultTaskShow = false;
      })
    },
    /** 取消回退任务按钮 */
    cancelTask() {
      this.taskForm.returnTaskShow = false;
      this.taskForm.defaultTaskShow = true;
      this.returnTaskList = [];
    },
    /** 提交退回任务 */
    submitReturnTask() {
      this.$refs["taskForm"].validate(valid => {
        if (valid) {
          returnTask(this.taskForm).then(res => {
            this.msgSuccess(res.msg);
            this.goBack()
          });
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
.test-form {
  margin: 15px auto;
  width: 800px;
  padding: 15px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}

.box-card {
  width: 100%;
  margin-bottom: 20px;
}
</style>
