<template>
  <div class="component-upload-image">
    <el-upload
      :action="uploadImgUrl"
      list-type="picture-card"
      :on-success="handleUploadSuccess"
      :before-upload="handleBeforeUpload"
      :on-error="handleUploadError"
      name="file"
      :show-file-list="false"
      :headers="headers"
      style="display: inline-block; vertical-align: top"
    >
      <el-image v-if="!value" :src="value">
        <div slot="error" class="image-slot">
          <i class="el-icon-plus" />
        </div>
      </el-image>
      <div v-else class="image">
        <el-image :src="value" :style="`width:150px;height:150px;`" fit="fill"/>
        <div class="mask">
          <div class="actions">
            <span title="预览" @click.stop="dialogVisible = true">
              <i class="el-icon-zoom-in" />
            </span>
            <span title="移除" @click.stop="removeImage">
              <i class="el-icon-delete" />
            </span>
          </div>
        </div>
      </div>
    </el-upload>
    <el-dialog :visible.sync="dialogVisible" title="预览" width="800" append-to-body>
      <img :src="value" style="display: block; max-width: 100%; margin: 0 auto;">
    </el-dialog>
  </div>
</template>

<script>
import { getToken } from "@/utils/auth";

export default {
  data() {
    return {
      dialogVisible: false,
      uploadImgUrl: process.env.VUE_APP_BASE_API + "/common/upload", // 上传的图片服务器地址
      headers: {
        Authorization: "Bearer " + getToken(),
      },
    };
  },
  props: {
    value: {
      type: String,
      default: "",
    },
  },
  methods: {
    removeImage() {
      this.$emit("input", "");
    },
    handleUploadSuccess(res) {
      this.$emit("input", res.url);
      this.loading.close();
    },
    handleBeforeUpload() {
      this.loading = this.$loading({
        lock: true,
        text: "上传中",
        background: "rgba(0, 0, 0, 0.7)",
      });
    },
    handleUploadError() {
      this.$message({
        type: "error",
        message: "上传失败",
      });
      this.loading.close();
    },
  },
  watch: {},
};
</script>

<!--<script>-->
<!--export default {-->
<!--  name: "Forum",-->
<!--  data() {-->
<!--    return {-->
<!--      //图片上传判断是否有相同图片-->
<!--      isCommonName: true,-->
<!--      //修改时此属性用于接收数据库中图片存储list，图片才能正常显示-->
<!--      fileListShow: [],-->
<!--      //页面上存的暂时图片地址List-->
<!--      fileListPut: [],-->
<!--      dialogImageUrl: '',-->
<!--      dialogVisible: false,-->
<!--      imgUpload: {-->
<!--        // 设置上传的请求头部-->
<!--        headers: {-->
<!--          Authorization: "Bearer " + getToken()-->
<!--        },-->
<!--        // 图片上传的方法地址:-->
<!--        url: process.env.VUE_APP_BASE_API + "/forum/forum/multiPicturesUpload",-->
<!--        url2: process.env.VUE_APP_BASE_API,-->
<!--      }-->
<!--    };-->
<!--  },-->
<!--  methods: {-->
<!--    //图片上传前的相关判断-->
<!--    beforePictureUpload(file){-->
<!--      //每次进来初始化 isCommonName 为true-->
<!--      this.isCommonName = true;-->
<!--      const isJPG = file.type === 'image/jpeg';-->
<!--      const isLt2M = file.size / 1024 / 1024 < 2;-->
<!--      //判断是否有相同的图片，如何有即提示并添加失败-->
<!--      if(this.fileListPut.length > 0){-->
<!--        this.fileListPut.forEach((item,index)=>{-->
<!--          if(item.name == file.name){-->
<!--            this.$message.error('已存在相同的图片！');-->
<!--            this.isCommonName = false;-->
<!--          }-->
<!--        })-->
<!--      }-->
<!--      if (!isJPG) {-->
<!--        this.$message.error('请上传图片格式的文件！');-->
<!--      }-->
<!--      if (!isLt2M) {-->
<!--        this.$message.error('上传的图片不能超过2MB!');-->
<!--      }-->
<!--      return isJPG && isLt2M && this.isCommonName;-->
<!--    },-->
<!--    //图片上传删除-->
<!--    handleRemove(file, fileList) {-->
<!--      //根据传进来删除的file里图片，同时删除保存在fileListPut的相同图片-->
<!--      if(this.fileListPut.length > 0){-->
<!--        this.fileListPut = this.fileListPut.filter((item, index)=>{-->
<!--          return item.name != file.name;-->
<!--        })-->
<!--      }-->
<!--    },-->
<!--    //图片预览-->
<!--    handlePictureCardPreview(file) {-->
<!--      this.dialogImageUrl = file.url;-->
<!--      this.dialogVisible = true;-->
<!--    },-->
<!--    //图片上传成功后的回调-->
<!--    handlePictureSuccess(res, file){-->
<!--      //设置图片访问路径-->
<!--      const imgObjectUrl = this.videoUpload.url2 + file.response.imgUrl;-->
<!--      //这是每个成功上传图片，以对象的形式保存在一个数组中，进而以JSON格式保存在数据库中某个字段里-->
<!--      let currentFile = {name: '',url: ''};-->
<!--      currentFile.name = file.name;-->
<!--      currentFile.url = imgObjectUrl;-->
<!--      //往此数组中保存当前图片对象-->
<!--      this.fileListPut.push(currentFile);-->
<!--    }-->
<!--  }}-->
<!--</script>-->
