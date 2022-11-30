<template>
  <el-container>
    <el-aside width="600px" style="text-align: left;overflow-x: hidden">
      <el-form ref="form" :model="methodInfo" label-width="150px">
        <el-form-item label="类型(不兼容jrebel)">
          <el-radio-group v-model="methodInfo.enhanceType">
            <el-radio-button label="stack"></el-radio-button>
            <el-radio-button label="trace"></el-radio-button>
            <el-radio-button label="watch"></el-radio-button>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="客户端">
          <el-select v-model="clientIds" multiple placeholder="请选择" style="width: 100%;">
            <el-option
                v-for="item in clientInfos"
                :key="item.clientId"
                :label="item.clientName+'_'+item.clientId+'_'+item.host"
                :value="item.clientId">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="类名">
          <el-autocomplete class="inline-input" v-model="methodInfo.className" style="width: 100%;"
                           :fetch-suggestions="querySearchKeyForClassName" clearable
                           placeholder="请输入类名">
          </el-autocomplete>
        </el-form-item>
        <el-form-item label="方法名">
          <el-autocomplete class="inline-input" v-model="methodInfo.methodName" style="width: 100%;"
                           :fetch-suggestions="querySearchKeyForMethodName" clearable
                           placeholder="请输入方法名">
          </el-autocomplete>
        </el-form-item>
        <el-form-item label="类加载器Hash">
          <el-input v-model="methodInfo.classLoaderHash" placeholder="请输入类加载器Hash"></el-input>
        </el-form-item>
      </el-form>
      <el-form v-if="methodInfo.enhanceType=='stack'" ref="form" :model="stackCommand" label-width="150px">
        <el-form-item label="条件OGNL">
          <el-input v-model="stackCommand.conditionExpress" placeholder="请输入OGNL条件表达式"></el-input>
        </el-form-item>
        <el-form-item label="执行次数">
          <el-input v-model="stackCommand.numberOfLimit" placeholder="请输入方法名"></el-input>
        </el-form-item>
        <el-form-item label="是否忽略JDK方法">
          <el-switch v-model="stackCommand.skipJDKTrace" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
        </el-form-item>
      </el-form>
      <el-form v-if="methodInfo.enhanceType=='trace'" ref="traceForm" :model="traceCommand" label-width="150px">
        <el-form-item label="条件OGNL">
          <el-input v-model="traceCommand.conditionExpress" placeholder="请输入OGNL条件表达式"></el-input>
        </el-form-item>
        <el-form-item label="执行次数">
          <el-input v-model="traceCommand.numberOfLimit" placeholder="请输入方法名"></el-input>
        </el-form-item>
        <el-form-item label="是否忽略JDK方法">
          <el-switch v-model="traceCommand.skipJDKTrace" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
        </el-form-item>
      </el-form>
      <el-form v-if="methodInfo.enhanceType=='watch'" ref="watchForm" :model="watchCommand" label-width="150px">
        <el-form-item label="条件OGNL">
          <el-input v-model="watchCommand.conditionExpress" placeholder="请输入OGNL条件表达式"></el-input>
        </el-form-item>
        <el-form-item label="执行次数">
          <el-input v-model="watchCommand.numberOfLimit" placeholder="请输入方法名"></el-input>
        </el-form-item>
        <el-form-item label="是否JSON格式">
          <el-switch v-model="watchCommand.showWithJson" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
        </el-form-item>
        <el-form-item label="Object展开层次" v-if="!watchCommand.showWithJson">
          <el-input v-model="watchCommand.expand" placeholder="请输入展开层次（1~4）"></el-input>
        </el-form-item>
        <el-form-item label="执行前是否通知">
          <el-switch v-model="watchCommand.atBefore" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
        </el-form-item>
        <el-form-item label="是否展示执行实例">
          <el-switch v-model="watchCommand.showTarget" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
        </el-form-item>
        <el-form-item label="是否展示参数">
          <el-switch v-model="watchCommand.showParams" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
        </el-form-item>
        <el-form-item label="是否展示返回结果">
          <el-switch v-model="watchCommand.showReturnObj" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
        </el-form-item>
        <el-form-item label="是否展示异常">
          <el-switch v-model="watchCommand.showException" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
        </el-form-item>
      </el-form>
    </el-aside>

    <el-container>
      <el-header style="height: 30px;text-align: left">
        <el-button @click="createEnhance" type="warning">创建监测</el-button>
        <el-button @click="closeAllEnhance" type="warning">关闭所有监测</el-button>
        <el-button @click="showMethodSourceCode" type="warning">查看源码</el-button>
      </el-header>
      <el-main>
        <el-tabs v-model="activeSession" closable @tab-remove="removeTab">
          <el-tab-pane v-for="(value,key)  in sessionTabs" :label="value" :name="key">
            <div style="margin-right: 10px;text-align: left;">
              <div style="width: 100%;background-color: #ebb563;padding: 5px;line-height: 20px">
                <span>
                  监控方法：{{ sessionCommands[key].className }}.{{
                    sessionCommands[key].methodName
                  }}#{{ sessionCommands[key].classLoaderHash }}
                </span>
                <span style="margin-left: 10px"
                      v-if="sessionCommands[key].conditionExpress">{{ sessionCommands[key].conditionExpress }}</span>
                <span style="margin-left: 10px">执行次数：{{ sessionCommands[key].numberOfLimit }}</span>
                <span style="margin-left: 10px" v-if="sessionCommands[key].type=='watch'">
                    是否JSON格式：{{ sessionCommands[key].showWithJson }}
                  </span>
                <span style="margin-left: 10px" v-if="sessionCommands[key].expand">
                    Object展开层次：{{ sessionCommands[key].expand }}
                  </span>
                <span style="margin-left: 10px" v-if="sessionCommands[key].type=='watch'">
                    执行前是否通知：{{ sessionCommands[key].atBefore }}
                  </span>
                <span style="margin-left: 10px" v-if="sessionCommands[key].type=='watch'">
                    是否展示执行实例：{{ sessionCommands[key].showTarget }}
                  </span>
                <span style="margin-left: 10px" v-if="sessionCommands[key].type=='watch'">
                    是否展示参数：{{ sessionCommands[key].showParams }}
                  </span>
                <span style="margin-left: 10px" v-if="sessionCommands[key].type=='watch'">
                    是否展示返回结果：{{ sessionCommands[key].showReturnObj }}
                  </span>
                <span style="margin-left: 10px" v-if="sessionCommands[key].type=='watch'">
                    是否展示异常：{{ sessionCommands[key].showException }}
                  </span>
                <span style="margin-left: 10px" v-if="sessionCommands[key].type!='watch'">
                    是否忽略JDK方法：{{ sessionCommands[key].skipJDKTrace }}
                  </span>
              </div>
              <div style="width: 100%;overflow-y: scroll;" :style="{height:dynamicHeight+'px'}">
                <div v-if="sessionCommands[key].type=='watch' && sessionResponses[key]">
                  <WatchList :watch-infos="sessionResponses[key]" :session-command="sessionCommands[key]">
                  </WatchList>
                </div>
                <div v-if="sessionCommands[key].type=='stack' && sessionResponses[key]">
                  <StackList :stack-infos="sessionResponses[key]" :session-command="sessionCommands[key]">
                  </StackList>
                </div>
                <div v-if="sessionCommands[key].type=='trace' && sessionResponses[key]">
                  <TraceList :trace-infos="sessionResponses[key]" :session-command="sessionCommands[key]"></TraceList>
                </div>
              </div>
            </div>
          </el-tab-pane>
        </el-tabs>
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
import Vue from "vue";
import MonacoEditor from "vue-monaco-editor";
import TraceList from "@/components/enhance/TraceList";
import WatchList from "@/components/enhance/WatchList";
import StackList from "@/components/enhance/StackList";
import {MessageBox} from "element-ui";

export default {
  name: 'Enhance',
  props: {
    methodInfo: {
      type: Object,
      default: null,
    },
    clientIds: {
      type: Array,
      default: null,
    }
  },
  components: {MonacoEditor, TraceList, WatchList, StackList},

  data() {
    return {
      stackCommand: {
        numberOfLimit: 1,
        conditionExpress: "",
        skipJDKTrace: false,
      },
      traceCommand: {
        numberOfLimit: 5,
        conditionExpress: "",
        skipJDKTrace: false,
      },
      watchCommand: {
        numberOfLimit: 5,
        conditionExpress: "",
        skipJDKTrace: false,
        expand: 2,
        sizeLimit: 1024000,
        atBefore: false,
        atFinish: true,
        showTarget: false,
        showParams: true,
        showReturnObj: true,
        showException: true,
        showWithJson: true,
      },
      clientInfos: [],
      suggestClassNames: [],
      suggestMethodNames: [],
      sessionTabs: {},
      sessionCommands: {},
      sessionResponses: {},
      sessionWebSockets: {},
      activeSession: "",
      randomKey: 123456,
      dialogSourceCodeVisible: false,
      code: "",
      options: {
        selectOnLineNumbers: true,
        roundedSelection: false,
        readOnly: false,
        cursorStyle: 'line',
        automaticLayout: false,
        glyphMargin: true
      }
    }
  },
  mounted() {
  },
  created() {
    Vue.axios.get('/api/client/list', {}).then((response) => {
      let clientInfos = response;
      let clientId = this.clientIds[0];
      let clientName = '';
      for (let index in clientInfos) {
        if (clientInfos[index].clientId == clientId) {
          clientName = clientInfos[index].clientName;
          break;
        }
      }
      let tempClientInfos = [];
      for (let index in clientInfos) {
        if (clientInfos[index].clientName == clientName) {
          tempClientInfos.push(clientInfos[index]);
        }
      }
      this.clientInfos = tempClientInfos;
    });
  },
  methods: {
    showMethodSourceCode() {
      Vue.axios.post('/api/class/jad?clientId=' + this.clientIds[0]
          , {
            "className": this.methodInfo.className
            , "classLoaderHash": this.methodInfo.classLoaderHash
            , "methodName": this.methodInfo.methodName
          }).then((response) => {
        this.randomKey = Math.floor(Math.random() * 100000);
        this.code = response.sourceInfo.source;
        this.dialogSourceCodeVisible = true;
      });
    },
    removeTab: function (sessionId) {
      let websocket = this.sessionWebSockets[sessionId];
      try {
        if (websocket) {
          websocket.close();
        }
      } catch (e) {
      }
      delete this.sessionTabs[sessionId];
      delete this.sessionCommands[sessionId];
      delete this.sessionResponses[sessionId];
      delete this.sessionWebSockets[sessionId];
      this.sessionTabs = JSON.parse(JSON.stringify(this.sessionTabs));
      for (let sessionId in this.sessionTabs) {
        this.activeSession = sessionId;
        break;
      }
    },
    closeAllEnhance: function () {
      try {
        for (let sessionId in this.sessionWebSockets) {
          let websocket = this.sessionWebSockets[sessionId];
          if (websocket) {
            websocket.close();
          }
        }
      } catch (e) {
      }
      this.sessionTabs = {};
      this.sessionCommands = {};
      this.sessionResponses = {};
      this.sessionWebSockets = {};
    },
    createEnhance: function () {
      if (!this.methodInfo || !this.methodInfo.className || !this.methodInfo.methodName
          || !this.methodInfo.classLoaderHash) {
        MessageBox.alert("类名、方法名和类加载器不能为空!")
        return;
      }

      let sessionId = this.getRandomString(8);

      let url = null;
      let command = null;
      if (this.methodInfo.enhanceType == 'stack') {
        command = JSON.parse(JSON.stringify(this.stackCommand));
      } else if (this.methodInfo.enhanceType == 'trace') {
        command = JSON.parse(JSON.stringify(this.traceCommand));
      } else if (this.methodInfo.enhanceType == 'watch') {
        command = JSON.parse(JSON.stringify(this.watchCommand));
      }
      command.type = this.methodInfo.enhanceType;
      command.sessionId = sessionId;
      command.className = this.methodInfo.className;
      command.methodName = this.methodInfo.methodName;
      command.classLoaderHash = this.methodInfo.classLoaderHash;

      this.checkSuggests(this.suggestClassNames, command.className);
      this.checkSuggests(this.suggestMethodNames, command.methodName);

      this.createSession(command, this.methodInfo.enhanceType);

      for (let idx in this.clientIds) {
        let commandId = this.getRandomString(8);
        command.commandId = commandId;

        if (this.methodInfo.enhanceType == 'stack') {
          url = '/api/enhance/stack?clientId=' + this.clientIds[idx];
        } else if (this.methodInfo.enhanceType == 'trace') {
          url = '/api/enhance/trace?clientId=' + this.clientIds[idx];
        } else if (this.methodInfo.enhanceType == 'watch') {
          url = '/api/enhance/watch?clientId=' + this.clientIds[idx];
        }
        Vue.axios.post(url, command).then((response) => {
          this.$message("cost:" + response.enhanceInfo.cost + "ms ,enhance classes:"
              + response.enhanceInfo.classCnt + " ,enhance methods:" + response.enhanceInfo.methodCnt + ".")
        });
      }
    },
    createSession: function (command, type) {
      let sessionTabs = JSON.parse(JSON.stringify(this.sessionTabs));
      sessionTabs[command.sessionId] = type + "-" + command.sessionId;
      this.sessionTabs = sessionTabs;
      this.activeSession = command.sessionId;

      let sessionCommands = JSON.parse(JSON.stringify(this.sessionCommands));
      sessionCommands[command.sessionId] = command;
      this.sessionCommands = sessionCommands;

      let websocket = this.connectWebsocket(command.sessionId);
      this.sessionWebSockets[command.sessionId] = websocket;
    },
    getRandomString: function (len) {
      let arr = ["0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E",
        "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U",
        "V", "W", "X", "Y", "Z"];
      let randomString = "";
      for (let i = 0; i < len; i++) {
        randomString += arr[parseInt(Math.random() * 36)];
      }
      return randomString;
    },
    querySearchKeyForClassName(queryString, cb) {
      let results = queryString ? this.suggestClassNames
          .filter(item => item.value.toLowerCase().indexOf(queryString.toLowerCase()) != -1) : this.suggestClassNames;
      cb(results);
    },
    querySearchKeyForMethodName(queryString, cb) {
      let results = queryString ? this.suggestMethodNames
          .filter(item => item.value.toLowerCase().indexOf(queryString.toLowerCase()) != -1) : this.suggestMethodNames;
      cb(results);
    },
    checkSuggests(arr, val) {
      let exist = false;
      for (let index in arr) {
        if (arr[index].value == val) {
          exist = true;
        }
      }
      if (!exist) {
        arr.push({value: val});
      }
    },
    connectWebsocket: function (sessionId) {
      let websocket;
      if (typeof WebSocket === "undefined") {
        console.log("您的浏览器不支持WebSocket");
        return;
      } else {
        let protocol = "ws";
        if (window.location.protocol == "https:") {
          protocol = "wss";
        }
        let url = protocol + '://localhost:9099/ws';

        // 打开一个websocket
        websocket = new WebSocket(url + "?sessionId=" + sessionId);
        // 建立连接
        websocket.onopen = () => {
          // 发送数据
          // websocket.send("发送数据");
          console.log("websocket发送数据中");
        };
        // 客户端接收服务端返回的数据
        websocket.onmessage = evt => {
          console.log("websocket返回的数据：", evt);
          let obj = JSON.parse(evt.data);
          if (obj.stackInfo) {
            let stackInfo = obj.stackInfo;
            stackInfo.clientId = obj.clientId;
            if (!this.sessionResponses[sessionId]) {
              this.sessionResponses[sessionId] = [];
            }
            this.sessionResponses[sessionId].push(stackInfo);
            this.sessionResponses = JSON.parse(JSON.stringify(this.sessionResponses));

          } else if (obj.traceInfo) {
            let traceInfo = obj.traceInfo;
            traceInfo.clientId = obj.clientId;
            console.log(traceInfo);
            if (!this.sessionResponses[sessionId]) {
              this.sessionResponses[sessionId] = [];
            }
            this.sessionResponses[sessionId].push(traceInfo);
            this.sessionResponses = JSON.parse(JSON.stringify(this.sessionResponses));
          } else if (obj.watchInfo) {
            let watchInfo = obj.watchInfo;
            watchInfo.clientId = obj.clientId;
            if (!this.sessionResponses[sessionId]) {
              this.sessionResponses[sessionId] = [];
            }
            this.sessionResponses[sessionId].push(watchInfo);
            this.sessionResponses = JSON.parse(JSON.stringify(this.sessionResponses));
          }
        };
        // 发生错误时
        websocket.onerror = evt => {
          console.log("websocket错误：", evt);
        };
        // 关闭连接
        websocket.onclose = evt => {
          console.log("websocket关闭：", evt);
        };
      }
      return websocket;
    },
    getMethodsContent(list, methodInfo, deep) {
      methodInfo.deep = deep;
      list.push(methodInfo);
      deep = deep + 1;
      if (methodInfo.throwFlag && methodInfo.traceThrowInfos.length > 0) {
        let throwInfo = methodInfo.traceThrowInfos[0];
        methodInfo.throwInfo = "throw:" + throwInfo.exception + "#" + throwInfo.lineNumber + ":" + throwInfo.message;
      } else {
        for (let index in methodInfo.children) {
          this.getMethodsContent(list, methodInfo.children[index], deep);
        }
      }
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