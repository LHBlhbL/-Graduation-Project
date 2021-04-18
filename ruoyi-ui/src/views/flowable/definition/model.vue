<template>
  <div>
    <bpmn-modeler
      ref="refNode"
      :xml="xml"
      :users="users"
      :groups="groups"
      :categorys="categorys"
      :is-view="false"
      @save="save"
      @showXML="showXML"
    />
    <!--在线查看xml-->
    <el-dialog :title="xmlTitle" :visible.sync="xmlOpen" width="60%" append-to-body>
      <div>
        <pre v-highlight>
           <code class="xml">
                {{xmlContent}}
           </code>
        </pre>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { readXml, saveXml, userList } from "@/api/flowable/definition";
import bpmnModeler from '@/components/Process/index'
import vkbeautify from 'vkbeautify'
import Hljs from 'highlight.js'
import 'highlight.js/styles/atom-one-dark.css'
export default {
  name: "Model",
  components: {
    bpmnModeler,
    vkbeautify
  },
  // 自定义指令
  directives: {
    highlight:(el) => {
      let blocks = el.querySelectorAll('pre code');
      blocks.forEach((block) => {
        Hljs.highlightBlock(block)
      })
    }
  },
  data() {
    return {
      xml: "", // 后端查询到的xml
      xmlOpen: false,
      xmlTitle: '',
      xmlContent: '',
      users: [
        { nickName: "#{initiator}", userId: "#{initiator}" },
        {nickName: "#{approval}", userId: "#{approval}"}
        // { name: "李四", id: "2" },
        // { name: "轩轩", id: "100" },
      ],
      groups: [
        { name: "超级管理员", id: "1" },
        { name: "普通角色", id: "2" },
        { name: "管理员", id: "100" },
      ],
      categorys: [
        { name: "请假", id: "leave" },
        { name: "财务", id: "finance" },
      ],
    };
  },
  created () {
    const deployId = this.$route.query && this.$route.query.deployId;
    //  查询流程xml
    if (deployId) {
      this.getModelDetail(deployId);
    }
    this.getUserList()
  },
  methods: {
    /** xml 文件 */
    getModelDetail(deployId) {
      // 发送请求，获取xml
      readXml(deployId).then(res =>{
        this.xml = res.data
      })
    },
    /** 保存xml */
    save(data) {
      const params = {
        name: data.process.name,
        category: data.process.category,
        xml: data.xml
      }
      debugger
      // saveXml(params).then(res => {
      //   this.$message(res.msg)
      //   // 关闭当前标签页并返回上个页面
      //   this.$store.dispatch("tagsView/delView", this.$route);
      //   this.$router.go(-1)
      // })
    },
    /** 指定流程办理人员列表 */
    getUserList() {
      // todo 待根据部门选择人员
      // const params = {
      //
      // }
      userList().then(res =>{
        res.data.forEach(val =>{
          let obj = {};
          obj.userId = val.userId;
          obj.nickName = val.nickName;
          this.users.push(obj)
        })
      })
    },
    showXML(data){
      this.xmlTitle = 'xml查看';
      this.xmlOpen = true;
      this.xmlContent = vkbeautify.xml(data);
    }
  },
};
</script>
