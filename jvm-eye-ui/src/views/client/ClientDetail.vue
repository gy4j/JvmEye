<template>
  <div>
    <el-container>
      <el-header style="padding: 0px">
        <Header :active-menu="activeMenu"/>
      </el-header>
      <el-container>
        <el-main style="background: #f8f8f9;padding: 10px;margin-top: 3px">
          <el-row
              style="background-color: white
              ;box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);text-align: left; line-height: 60px
              ;height: 60px;padding-left: 10px;padding-right: 10px;overflow:hidden;text-overflow: ellipsis;">
            <el-col :span="9">
              <div>客户端：{{ clientInfo.clientName }}({{ clientInfo.clientId }})</div>
            </el-col>
            <el-col :span="11">
              <div>
                地址：{{ clientInfo.host }}({{ clientInfo.ip }})
              </div>
            </el-col>
            <el-col :span="4" style="text-align: right">
              <el-button type="warning" icon="el-icon-search" @click="changeClient">切换客户端</el-button>
            </el-col>
          </el-row>
          <el-tabs class="tabs" v-model="tabName" :style="{height:(dynamicHeight+110)+'px'}">
            <el-tab-pane label="JVM信息" name="jvm">
              <JvmPanel :client-id="clientInfo.clientId"></JvmPanel>
            </el-tab-pane>
            <el-tab-pane label="JVM参数" name="vmoption">
              <VmOptionPanel :client-id="clientInfo.clientId"></VmOptionPanel>
            </el-tab-pane>
            <el-tab-pane label="环境变量" name="sysenv">
              <SysEnvPanel :client-id="clientInfo.clientId"></SysEnvPanel>
            </el-tab-pane>
            <el-tab-pane label="系统属性" name="sysprop">
              <SysPropPanel :client-id="clientInfo.clientId"></SysPropPanel>
            </el-tab-pane>
            <el-tab-pane label="内存分析" name="memory">
              <MemoryPanel :client-id="clientInfo.clientId"/>
            </el-tab-pane>
            <el-tab-pane label="线程分析" name="thread">
              <ThreadPanel :client-id="clientInfo.clientId"/>
            </el-tab-pane>
            <el-tab-pane label="类加载器分析" name="classloader">
              <ClassLoaderPanel :client-id="clientInfo.clientId"></ClassLoaderPanel>
            </el-tab-pane>
            <el-tab-pane label="类分析(源码、日志级别)" name="class">
              <el-row>
                <el-col style="text-align: left">
                  <el-input v-model="searchClassName" placeholder="请输入类名" clearable
                            style="width: 500px;margin-right: 10px"></el-input>
                  <el-button @click="searchClass" type="warning">搜索</el-button>
                </el-col>
              </el-row>
              <el-table :data="classList" :height="dynamicHeight" style="margin-top: 10px">
                <el-table-column prop="className" label="类名" width="350"></el-table-column>
                <el-table-column prop="classloader" label="类加载器" width="300"></el-table-column>
                <el-table-column prop="classLoaderHash" label="类加载器Hash" width="150"></el-table-column>
                <el-table-column prop="codeSource" label="类加载来源"></el-table-column>
                <el-table-column fixed="right" label="操作" width="200">
                  <template slot-scope="scope">
                    <el-button @click="showClassSource(scope.row)" type="text" size="small">源码</el-button>
                    <el-button @click="showClassMethods(scope.row)" type="text" size="small">方法列表</el-button>
                    <el-button @click="showLoggerLevel(scope.row)" type="text" size="small">日志级别</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="方法分析(源码、栈、耗时、参数)" name="method">
              <el-row>
                <el-col style="text-align: left">
                  <el-input v-model="searchClassName" placeholder="请输入类名" clearable
                            style="width: 500px;margin-right: 10px"></el-input>
                  <el-input v-model="searchMethodName" placeholder="请输入方法名" clearable
                            style="width: 250px;margin-right: 10px"></el-input>
                  <el-input v-model="searchClassLoaderHash" placeholder="请输入类加载器Hash" clearable
                            style="width: 200px;margin-right: 10px"></el-input>
                  <el-button @click="searchClassMethod" type="warning"
                             style="margin-right: 10px">搜索
                  </el-button>
                  <el-button @click="showEnhanceDialog" type="warning">
                    打开方法监测页面
                  </el-button>
                </el-col>
              </el-row>
              <el-table :data="methodList" :height="dynamicHeight" style="margin-top: 10px">
                <el-table-column prop="methodName" label="方法名" width="200"></el-table-column>
                <el-table-column prop="parameters" label="参数类型" width="250">
                  <template slot-scope="scope">
                    <span>{{ scope.row.parameters.join() }}</span>
                  </template>
                </el-table-column>
                <el-table-column prop="className" label="类名" width="350"></el-table-column>
                <el-table-column prop="classloader" label="类加载器"></el-table-column>
                <el-table-column prop="classLoaderHash" label="类加载器Hash" width="150"></el-table-column>
                <el-table-column fixed="right" label="操作" width="300">
                  <template slot-scope="scope">
                    <el-button @click="showMethodSource(scope.row)" type="text" size="small">源码</el-button>
                    <el-button @click="watch(scope.row)" type="text" size="small">watch</el-button>
                    <el-button @click="trace(scope.row)" type="text" size="small">trace</el-button>
                    <el-button @click="stack(scope.row)" type="text" size="small">stack</el-button>
                    <el-button @click="call(scope.row)" type="text" size="small">调用</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
            <el-tab-pane label="Spring相关" name="spring">
              <SpringPanel :client-id="clientInfo.clientId"></SpringPanel>
            </el-tab-pane>
          </el-tabs>
        </el-main>
      </el-container>
    </el-container>
    <el-dialog title="源码" :visible.sync="dialogSourceCodeVisible" :fullscreen="true">
      <MonacoEditor
          language="java"
          :height="dynamicHeight+120"
          :key="randomKey"
          :code="code"
          :editorOptions="options" style="text-align: left">
      </MonacoEditor>
    </el-dialog>
    <el-dialog :visible.sync="dialogLoggerLevelVisible">
      <LoggerDetail :client-id="clientInfo.clientId" :logger-info="loggerInfo" @closeLoggerDialog="closeLoggerDialog"/>
    </el-dialog>
    <el-dialog :visible.sync="dialogEnhanceVisible" :fullscreen="true">
      <Enhance :client-ids="clientIds" :method-info="methodInfo"/>
    </el-dialog>
    <el-dialog :visible.sync="dialogCallVisible" :fullscreen="true">
      <MethodCall :client-id="clientInfo.clientId" :method-info="methodInfo"/>
    </el-dialog>
  </div>
</template>
<script>
import Header from "@/components/common/Header";
import Vue from "vue";
import JvmPanel from "@/components/jvm/JvmPanel";
import SpringPanel from "@/components/spring/SpringPanel";
import ThreadPanel from "@/components/jvm/ThreadPanel";
import MemoryPanel from "@/components/jvm/MemoryPanel";
import VmOptionPanel from "@/components/jvm/VmOptionPanel";
import ClassLoaderPanel from "@/components/jvm/ClassLoaderPanel";
import SysEnvPanel from "@/components/jvm/SysEnvPanel";
import SysPropPanel from "@/components/jvm/SysPropPanel";
import MonacoEditor from 'vue-monaco-editor';
import GroupLabelValue from "@/components/common/GroupLabelValue";
import LoggerDetail from "@/components/logger/LoggerDetail";
import Enhance from "@/components/enhance/Enhance";
import MethodCall from "@/components/method/MethodCall";

export default {
  name: 'ClientDetail',

  components: {
    Header,
    MonacoEditor,
    JvmPanel,
    GroupLabelValue,
    LoggerDetail,
    Enhance,
    ThreadPanel,
    MemoryPanel,
    VmOptionPanel,
    ClassLoaderPanel,
    SysEnvPanel,
    SysPropPanel,
    SpringPanel,
    MethodCall
  },

  data() {
    return {
      activeMenu: "2",
      clientInfo: {},
      tabName: "jvm",
      searchClassName: "",
      searchMethodName: "",
      searchClassLoaderHash: "",
      classList: [],
      methodList: [],
      randomKey: 123456,
      dialogSourceCodeVisible: false,
      dialogLoggerLevelVisible: false,
      dialogEnhanceVisible: false,
      dialogCallVisible: false,
      code: "",
      options: {
        selectOnLineNumbers: true,
        roundedSelection: false,
        readOnly: false,
        cursorStyle: 'line',
        automaticLayout: false,
        glyphMargin: true
      },
      loggerInfo: {},
      methodInfo: {}
    };
  },

  created() {
    let query = this.$route.query;
    if (query && query.clientId) {
      this.clientInfo = query;
    } else {
      // 先去选择客户端
      this.$router.push({name: 'ClientList'});
    }
  },

  methods: {
    changeClient() {
      this.$router.push({name: 'ClientList'});
    },
    searchClass() {
      Vue.axios.post('/api/class/list?clientId=' + this.clientInfo.clientId
          , {"className": this.searchClassName}).then((response) => {
        this.classList = response['classInfos'];
      });
    },
    searchClassMethod() {
      Vue.axios.post('/api/class/method?clientId=' + this.clientInfo.clientId
          , {
            "className": this.searchClassName,
            "methodName": this.searchMethodName,
            "classLoaderHash": this.classLoaderHash
          }).then((response) => {
        this.methodList = response['methodInfos'];
      });
    },
    showClassSource(classInfo) {
      Vue.axios.post('/api/class/jad?clientId=' + this.clientInfo.clientId
          , {
            "className": classInfo.className
            , "classLoaderHash": classInfo.classLoaderHash
          }).then((response) => {
        this.randomKey = Math.floor(Math.random() * 100000);
        this.code = response.sourceInfo.source;
        this.dialogSourceCodeVisible = true;
      });
    },
    showMethodSource(methodInfo) {
      Vue.axios.post('/api/class/jad?clientId=' + this.clientInfo.clientId
          , {
            "className": methodInfo.className,
            "methodName": methodInfo.methodName
            , "classLoaderHash": methodInfo.classLoaderHash
          }).then((response) => {
        this.randomKey = Math.floor(Math.random() * 100000);
        this.code = response.sourceInfo.source;
        this.dialogSourceCodeVisible = true;
      });
    },
    showClassMethods(classInfo) {
      this.searchClassName = classInfo.className;
      this.searchClassLoaderHash = classInfo.classLoaderHash;
      this.searchClassMethod();
      this.tabName = "method";
    },
    showLoggerLevel(classInfo) {
      Vue.axios.post('/api/logger/info?clientId=' + this.clientInfo.clientId
          , {"name": classInfo.className, "classLoaderHash": classInfo.classLoaderHash}).then((response) => {
        if (response.loggerInfos && response.loggerInfos.length > 0) {
          this.loggerInfo = response.loggerInfos[0];
          this.loggerInfo.loggerName = classInfo.className;
          this.dialogLoggerLevelVisible = true;
        }
      });
    },
    closeLoggerDialog() {
      this.dialogLoggerLevelVisible = false;
    },
    stack(methodInfo) {
      this.methodInfo = this.simpleMethodInfo("stack", methodInfo);
      this.dialogEnhanceVisible = true;
    },
    watch(methodInfo) {
      this.methodInfo = this.simpleMethodInfo("watch", methodInfo);
      this.dialogEnhanceVisible = true;
    },
    trace(methodInfo) {
      this.methodInfo = this.simpleMethodInfo("trace", methodInfo);
      this.dialogEnhanceVisible = true;
    },
    simpleMethodInfo(enhanceType, methodInfo) {
      return {
        "enhanceType": enhanceType
        , "className": methodInfo.className
        , "methodName": methodInfo.methodName
        , "classLoaderHash": methodInfo.classLoaderHash
        , "parameters": this.generateParameters(methodInfo.parameters)
      }
    },
    showEnhanceDialog() {
      if (!this.methodInfo.enhanceType) {
        this.methodInfo = this.simpleMethodInfo("stack", {});
      }
      this.dialogEnhanceVisible = true;
    },
    generateParameters(parameters) {
      let arr = [];
      for (let idx in parameters) {
        if (parameters[idx].trim() == 'int' || parameters[idx].trim() == 'java.lang.Integer'
            || parameters[idx].trim() == 'long' || parameters[idx].trim() == 'java.lang.Long'
            || parameters[idx].trim() == 'byte' || parameters[idx].trim() == 'java.lang.Byte'
            || parameters[idx].trim() == 'short' || parameters[idx].trim() == 'java.lang.Short'
            || parameters[idx].trim() == 'char' || parameters[idx].trim() == 'java.lang.Char'
            || parameters[idx].trim() == 'float' || parameters[idx].trim() == 'java.lang.Float'
            || parameters[idx].trim() == 'double' || parameters[idx].trim() == 'java.lang.Double') {
          arr.push(0);
        } else if (parameters[idx].trim() == 'java.util.List') {
          arr.push("new java.util.ArrayList()");
        } else if (parameters[idx].trim() == 'java.util.Map') {
          arr.push("new java.util.HashMap()");
        } else {
          arr.push("new " + parameters[idx] + "()");
        }
      }
      return arr.join(",");
    },
    call(methodInfo) {
      this.methodInfo = this.simpleMethodInfo("call", methodInfo);
      this.dialogCallVisible = true;
    }
  },
  computed: {
    dynamicHeight: function () {
      return (window.innerHeight - 270);
    },
    clientIds: function () {
      return [this.clientInfo.clientId];
    }
  }
};
</script>
<style>
.tabs {
  margin-top: 10px;
  padding: 10px;
  background-color: white;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1)
}
</style>