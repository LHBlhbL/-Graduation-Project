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
    />
  </div>
</template>

<script>
import { readXml, saveXml } from "@/api/flowable/definition";
// import bpmnModeler from "workflow-bpmn-modeler";
import bpmnModeler from '@/components/Process/index'

export default {
  name: "Model",
  components: {
    bpmnModeler
  },
  data() {
    return {
      xml: "", // 后端查询到的xml
      users: [
        { name: "张三", id: "1" },
        { name: "李四", id: "2" },
        { name: "轩轩", id: "100" },
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
    if (deployId) {
      this.getModelDetail(deployId);
    }
  },
  methods: {
    getModelDetail(deployId) {
      // 发送请求，获取xml
      readXml(deployId).then(res =>{
        this.xml = res.data
      })
    },
    save(data) {
      const params = {
        name: data.process.name,
        category: data.process.category,
        xml: data.xml
      }
      saveXml(params).then(res =>{
        this.$message(res.msg)
        // 关闭当前标签页并返回上个页面
        this.$store.dispatch("tagsView/delView", this.$route);
        this.$router.go(-1)
      })
    },
  },
};
</script>
