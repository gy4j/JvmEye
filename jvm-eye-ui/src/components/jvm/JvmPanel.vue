<template>
  <div>
    <el-row>
      <el-col style="text-align: left;">
        <el-button @click="showJvmInfo()" type="warning">刷新数据</el-button>
        <el-button @click="dumpHeap()" type="warning">导出堆内存</el-button>
      </el-col>
    </el-row>
    <el-row style="margin-top:10px">
      <el-col>
        <el-tabs v-model="activeName">
          <el-tab-pane label="运行信息" name="runtimeInfo">
            <GroupLabelValue :group-obj="jvmInfo.runtimeInfo" group-name="运行信息"
                             style="overflow-y: scroll;"
                             :style="{height:dynamicHeight}"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label=" 编译器信息
            " name="compilationInfo">
            <GroupLabelValue :group-obj="jvmInfo.compilationInfo" group-name="编译器信息"
                             style="overflow-y: scroll;"
                             :style="{height:dynamicHeight}"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="内存管理信息" name="memoryManagerInfo">
            <GroupLabelValue :group-obj="jvmInfo.memoryManagerInfo" group-name="内存管理信息"
                             style="overflow-y: scroll;"
                             :style="{height:dynamicHeight}"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="内存信息" name="memoryInfo">
            <GroupLabelValue :group-obj="jvmInfo.memoryInfo" group-name="内存信息"
                             style="overflow-y: scroll;"
                             :style="{height:dynamicHeight}"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="垃圾回收信息" name="garbageCollectorInfo">
            <GroupLabelValue :group-obj="jvmInfo.garbageCollectorInfo" group-name="垃圾回收信息"
                             style="overflow-y: scroll;"
                             :style="{height:dynamicHeight}"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="线程信息" name="threadInfo">
            <GroupLabelValue :group-obj="jvmInfo.threadInfo" group-name="线程信息"
                             style="overflow-y: scroll;"
                             :style="{height:dynamicHeight}"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="类加载信息" name="classLoadingInfo">
            <GroupLabelValue :group-obj="jvmInfo.classLoadingInfo" group-name="类加载信息"
                             style="overflow-y: scroll;"
                             :style="{height:dynamicHeight}"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="操作系统信息" name="operatingSystemInfo">
            <GroupLabelValue :group-obj="jvmInfo.operatingSystemInfo" group-name="操作系统信息"
                             style="overflow-y: scroll;"
                             :style="{height:dynamicHeight}"></GroupLabelValue>
          </el-tab-pane>
          <el-tab-pane label="文件描述信息" name="fileDescriptorInfo">
            <GroupLabelValue :group-obj="jvmInfo.fileDescriptorInfo" group-name="文件描述信息"
                             style="overflow-y: scroll;"
                             :style="{height:dynamicHeight}"></GroupLabelValue>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import Vue from "vue";
import {MessageBox} from "element-ui";
import GroupLabelValue from "@/components/common/GroupLabelValue";

export default {
  name: 'JvmPanel',
  components: {GroupLabelValue},
  props: {
    clientId: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      jvmInfo: {},
      activeName: "runtimeInfo"
    }
  },
  created() {
    // this.showJvmInfo();
  },
  methods: {
    showJvmInfo() {
      Vue.axios.post('/api/jvm/info?clientId=' + this.clientId
          , {}).then((response) => {
        this.jvmInfo = response.jvmInfo;
        console.log(this.jvmInfo)
      });
    },
    dumpHeap() {
      Vue.axios.post('/api/jvm/heapdump?clientId=' + this.clientId
          , {}).then((response) => {
        MessageBox.confirm(response.msg + " filePath:" + response.dumpFile);
      });
    },
  },
  computed: {
    dynamicHeight: function () {
      return (window.innerHeight - 330) + "px";
    }
  }
}
</script>
<style>
</style>