<template>
  <div>
    <div v-for="(watchInfo,index) in watchInfos" style="clear: both;padding-top: 20px">
      <div style="white-space: pre-wrap;">
        <span>监测时间：{{ watchInfo.ts }} </span>
        <span style="margin-left: 10px">客户端：{{ watchInfo.clientId }}</span>
        <span style="margin-left: 10px">监测位置：{{ watchInfo.accessPoint }}</span>
        <span style="margin-left: 10px">耗时：{{ watchInfo.cost }}(ms)</span>
      </div>
      <div style="white-space: pre-wrap;clear: both"
           v-if="sessionCommand.showTarget && watchInfo.targetInfo!='null'">
        <div style="float: left">执行实例：</div>
        <div style="float: left">{{ watchInfo.targetInfo }}</div>
      </div>
      <div style="white-space: pre-wrap;clear: both"
           v-if="sessionCommand.showParams && watchInfo.paramsInfo!='null'">
        <div style="float: left">输入参数：</div>
        <div style="float: left">{{ watchInfo.paramsInfo }}</div>
      </div>
      <div style="white-space: pre-wrap;clear: both"
           v-if="sessionCommand.showReturnObj && watchInfo.returnObjInfo!='null'">
        <div style="float: left">返回结果：</div>
        <div style="float: left">{{ watchInfo.returnObjInfo }}</div>
      </div>
      <div style="white-space: pre-wrap;clear: both"
           v-if="sessionCommand.showException && watchInfo.exceptionInfo!='null'">
        <div style="float: left">异常信息：</div>
        <div style="float: left">{{ watchInfo.exceptionInfo }}</div>
      </div>
    </div>
  </div>
</template>
<script>
import {getContentWithDeep} from '../../util/CommonUtil';


export default {
  name: 'WatchList',
  props: {
    watchInfos: {
      type: Array,
      default: [],
    },
    sessionCommand: {
      type: Object,
      default: {},
    }
  },
  data() {
    return {}
  },
  created() {
  },
  methods: {
    getLineWithDeep(deep) {
      return getContentWithDeep(deep);
    }
  }
}
</script>
<style>
</style>