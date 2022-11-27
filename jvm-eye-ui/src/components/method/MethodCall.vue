<template>
  <el-container>
    <el-aside width="600px" style="text-align: left;overflow-x: hidden">
      <el-form ref="form" :model="methodInfo" label-width="150px">
        <el-form-item label="类名">
          <el-input v-model="methodInfo.className" style="width: 100%;" clearable
                    placeholder="请输入类名">
          </el-input>
        </el-form-item>
        <el-form-item label="方法名">
          <el-input v-model="methodInfo.methodName" style="width: 100%;" clearable
                    placeholder="请输入方法名">
          </el-input>
        </el-form-item>
        <el-form-item label="类加载器Hash">
          <el-input v-model="methodInfo.classLoaderHash" placeholder="请输入类加载器Hash"></el-input>
        </el-form-item>
        <el-form-item label="参数">
          <el-input v-model="methodInfo.parameters" placeholder="请输入参数"></el-input>
        </el-form-item>
      </el-form>
    </el-aside>

    <el-container>
      <el-header style="height: 30px;text-align: left">
        <el-button @click="callMethod" type="warning">调用方法</el-button>
        <el-button @click="showMethodSourceCode" type="warning">查看源码</el-button>
      </el-header>
      <el-main>
        <div style="text-align: left;line-height: 14px">执行结果：{{ callResult }}</div>
      </el-main>
    </el-container>
    <el-dialog title="源码" :visible.sync="dialogSourceCodeVisible" :fullscreen="true" :modal="false">
      <MonacoEditor
          language="java"
          :height="dynamicHeight+120"
          :key="randomKey"
          :code="code"
          :editorOptions="options" style="text-align: left">
      </MonacoEditor>
    </el-dialog>
  </el-container>
</template>
<script>
import MonacoEditor from "vue-monaco-editor";
import Vue from "vue";

export default {
  name: 'MethodCall',
  props: {
    methodInfo: {
      type: Object,
      default: null,
    },
    clientId: {
      type: String,
      default: null,
    }
  },
  components: {MonacoEditor},

  data() {
    return {
      dialogSourceCodeVisible: false,
      randomKey: 123456,
      code: "",
      options: {
        selectOnLineNumbers: true,
        roundedSelection: false,
        readOnly: false,
        cursorStyle: 'line',
        automaticLayout: false,
        glyphMargin: true
      },
      callResult: ""
    }
  },
  mounted() {
  },
  created() {
  },
  methods: {
    callMethod: function () {
      let command = {};
      command.className = this.methodInfo.className;
      command.classLoaderHash = this.methodInfo.classLoaderHash;
      command.express = "#instance=instances[0],#result=#instance." + this.methodInfo.methodName + "(" + this.methodInfo.parameters + "),#result";
      Vue.axios.post('/api/vmtool/execute?clientId=' + this.clientId
          , command).then((response) => {
        this.callResult = response.returnObj;
      });
    },
    showMethodSourceCode: function () {
      Vue.axios.post('/api/class/jad?clientId=' + this.clientId
          , {
            "className": this.methodInfo.className
            , "classLoaderHash": this.methodInfo.classLoaderHash
            , "methodName": this.methodInfo.methodName
          }).then((response) => {
        this.randomKey = Math.floor(Math.random() * 100000);
        this.code = response.sourceInfo.source;
        this.dialogSourceCodeVisible = true;
      });
    }
  },
  computed: {
    dynamicHeight: function () {
      return (window.innerHeight - 270);
    }
  }
}
</script>
<style scoped>
</style>