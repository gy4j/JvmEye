<template>
  <div style="text-align: left">
    <el-form ref="form" :model="loggerInfo" label-width="150px">
      <el-form-item label="类名">
        <label>{{ loggerInfo.loggerName }}</label>
      </el-form-item>
      <el-form-item label="日志级别">
        <el-radio-group v-model="loggerInfo.effectiveLevel" @change="changeLevel()">
          <el-radio-button label="DEBUG"></el-radio-button>
          <el-radio-button label="INFO"></el-radio-button>
          <el-radio-button label="WARN"></el-radio-button>
          <el-radio-button label="ERROR"></el-radio-button>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="日志类型">
        <label>{{ loggerInfo.loggerType }}</label>
      </el-form-item>
      <el-form-item label="日志类名">
        <label>{{ loggerInfo.className }}</label>
      </el-form-item>
      <el-form-item label="日志类加载器">
        <label>{{ loggerInfo.classLoader }}</label>
      </el-form-item>
      <el-form-item label="日志类加载器Hash">
        <label>{{ loggerInfo.classLoaderHash }}</label>
      </el-form-item>
      <el-form-item label="日志类来源">
        <label>{{ loggerInfo.codeSource }}</label>
      </el-form-item>
      <el-form-item label="additivity">
        <label>{{ loggerInfo.additivity }}</label>
      </el-form-item>
      <el-form-item label="appenders">
        <label>{{ loggerInfo.appenders }}</label>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
import Vue from "vue";

export default {
  name: 'LoggerDetail',
  props: {
    loggerInfo: {
      type: Object,
      default: null,
    },
    clientId: {
      type: String,
      default: null
    }
  },
  components: {},
  data() {
    return {}
  },
  created() {
  },
  methods: {
    changeLevel(newLevel) {
      Vue.axios.post('/api/logger/level?clientId=' + this.clientId
          , {
            "name": this.loggerInfo.loggerName
            , "level": this.loggerInfo.effectiveLevel
          }).then((response) => {
        this.$message(response.msg);
        this.$emit("closeLoggerDialog");
      });
    }
  }
}
</script>
<style>
</style>