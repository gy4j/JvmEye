<template>
  <div>
    <el-row>
      <el-col style="text-align: left;">
        <el-select v-model="classLoaderName" style="width: 400px;margin-right: 10px" clearable
                   placeholder="请输入类加载器名">
          <el-option key="org.springframework.boot.loader.LaunchedURLClassLoader"
                     label="org.springframework.boot.loader.LaunchedURLClassLoader"
                     value="org.springframework.boot.loader.LaunchedURLClassLoader"></el-option>
          <el-option key="sun.misc.Launcher$AppClassLoader" label="sun.misc.Launcher$AppClassLoader"
                     value="sun.misc.Launcher$AppClassLoader"></el-option>
        </el-select>
        <el-button @click="showConfigInfo()" type="warning">查询配置项</el-button>
        <el-button @click="showResourceInfo()" type="warning">查询资源文件</el-button>
      </el-col>
    </el-row>
    <el-row style="margin-top:10px">
      <el-col>
        <el-tabs v-model="activeName" :style="{height:dynamicHeight}" style="overflow-y: scroll;">
          <el-tab-pane label="配置项" name="configInfo">
            <div v-for="(item,key) in springConfigInfo">
              <div v-if="item"
                   style="background-color: #ebb563; font-size: 16px;line-height: 20px;margin-top: 10px;margin-bottom: 10px">
                {{ key }}
              </div>
              <GroupLabelValue :group-obj="item" group-name="配置项信息"></GroupLabelValue>
            </div>
          </el-tab-pane>
          <el-tab-pane label="资源文件" name="resourceInfo">
            <div v-for="(item,key) in springResourceInfos">
              <div
                  style="font-size: 16px;line-height: 20px;margin-top: 10px;margin-bottom: 10px;text-align: left">
                {{ item }}
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>
<script>
import Vue from "vue";
import GroupLabelValue from "@/components/common/GroupLabelValue";

export default {
  name: 'SpringPanel',
  components: {GroupLabelValue},
  props: {
    clientId: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      springConfigInfo: {},
      springResourceInfos: {},
      activeName: "configInfo",
      classLoaderName: "org.springframework.boot.loader.LaunchedURLClassLoader"
    }
  },
  created() {
    // this.showJvmInfo();
  },
  methods: {
    showResourceInfo() {
      this.activeName = "resourceInfo";
      let command = {};
      command.className = "org.springframework.context.ApplicationContext";
      command.classLoaderName = this.classLoaderName;
      command.express = "#springContext=instances[0],+#allResources={},#springContext.getResources(\"classpath*:*.properties\").{#url=#this.getURL(),#allResources.add(#url)},#springContext.getResources(\"classpath*:*.yml\").{#url=#this.getURL(),#allResources.add(#url)},#allResources";
      Vue.axios.post('/api/vmtool/execute?clientId=' + this.clientId
          , command).then((response) => {
        let arr = JSON.parse(response.returnObj);
        let resourceInfos = {};
        for (let key in arr) {
          resourceInfos[key] = arr[key];
        }
        this.springResourceInfos = resourceInfos;
      });
    },
    showConfigInfo() {
      this.activeName = "configInfo";
      let command = {};
      command.className = "org.springframework.context.ApplicationContext";
      command.classLoaderName = this.classLoaderName;
      command.express = "#springContext=instances[0],#standardServletEnvironment=#springContext.getEnvironment(),+#allProperties={},#propertySourceIterator=#standardServletEnvironment.getPropertySources().iterator(),#propertySourceIterator.{#key=#this.getName(),#allProperties.add(\"propertySourceName:\"+#key)" +
          ",#pSource=#this,#this instanceof org.springframework.core.env.EnumerablePropertySource ?#this.getPropertyNames().{#key=#this,#allProperties.add(#key+\":\"+#pSource.getProperty(#key))}:#{}},#allProperties";
      Vue.axios.post('/api/vmtool/execute?clientId=' + this.clientId
          , command).then((response) => {
        let obj = JSON.parse(response.returnObj);
        let configInfo = {};
        let currentSource = "";
        for (let index in obj) {
          let lineInfo = this.parseLineInfo(obj[index]);
          if (obj[index].startsWith("propertySourceName")) {
            if (JSON.stringify(configInfo[currentSource]) == '{}') {
              configInfo[currentSource] = null;
            }
            currentSource = lineInfo.value;
            configInfo[currentSource] = {};
          } else {
            if (configInfo[currentSource]) {
              configInfo[currentSource][lineInfo.key] = lineInfo.value;
            }
          }
        }
        this.springConfigInfo = configInfo;
      });
    },
    parseLineInfo(line) {
      let idx = line.indexOf(":");
      let key = line.substr(0, idx);
      let value = line.substr(idx + 1);
      return {"key": key, "value": value};
    }
  },
  computed: {
    dynamicHeight: function () {
      return (window.innerHeight - 270) + "px";
    }
  }
}
</script>
<style>
</style>