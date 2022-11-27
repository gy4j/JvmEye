<template>
  <div>
    <div v-for="(traceInfo,index) in traceInfos" style="clear: both;padding-top: 20px">
      <div v-for="(nodeInfo,index) in traceInfo.nodeInfos">
        <div v-if="nodeInfo.nodeType=='thread'">
          <span>监测时间：{{ nodeInfo.ts }} </span>
          <span style="margin-left: 10px">客户端：{{ traceInfo.clientId }}</span>
          <span style="margin-left: 10px">线程名称：{{ nodeInfo.threadName }}</span>
          <span style="margin-left: 10px">线程ID：{{ nodeInfo.threadId }}</span>
          <span style="margin-left: 10px">是否守护线程：{{ nodeInfo.daemon }}</span>
          <span style="margin-left: 10px">优先级：{{ nodeInfo.priority }}</span>
        </div>
        <div v-if="nodeInfo.nodeType=='method'">
          <div v-if="nodeInfo.times>1" :class="nodeInfo.maxCostNode?'max_cost':''">
            {{ getLineWithDeep(nodeInfo.deep) }}[{{ nodeInfo.percentage }}% min={{ nodeInfo.minCost / 1000000 }}ms
            max={{ nodeInfo.maxCost / 1000000 }}ms total={{ nodeInfo.totalCost / 1000000 }}ms times={{
              nodeInfo.times
            }}]
            {{ nodeInfo.className }}.{{ nodeInfo.methodName }}()
            #{{ nodeInfo.lineNumber }} <span v-if="nodeInfo.mark">[{{ nodeInfo.mark }}]</span>
          </div>
          <div v-if="nodeInfo.times<=1">
            {{ getLineWithDeep(nodeInfo.deep) }}[{{ nodeInfo.percentage }}% {{ nodeInfo.cost / 1000000 }}ms] {{
              nodeInfo.className
            }}.{{ nodeInfo.methodName }}()
            #{{ nodeInfo.lineNumber }} <span v-if="nodeInfo.mark">[{{ nodeInfo.mark }}]</span>
          </div>
        </div>
        <div v-if="nodeInfo.nodeType=='exception'">
          {{ getLineWithDeep(nodeInfo.deep + 1) }}
          throw: {{ nodeInfo.exception }} #{{ nodeInfo.lineNumber }} [{{ nodeInfo.message }}]
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {getContentWithDeep} from '../../util/CommonUtil';


export default {
  name: 'TraceList',
  props: {
    traceInfos: {
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
.max_cost {
  font-weight: bold;
  color: #ebb563
}
</style>