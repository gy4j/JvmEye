<template>
  <div>
    <div v-for="(stackInfo,index) in stackInfos" style="clear: both;padding-top: 20px">
      <div style="white-space: pre-wrap;">
        <span>监测时间：{{ stackInfo.ts }} </span>
        <span style="margin-left: 10px">客户端：{{ stackInfo.clientId }}</span>
        <span style="margin-left: 10px">线程名称：{{ stackInfo.threadName }}</span>
        <span style="margin-left: 10px">线程ID：{{ stackInfo.threadId }}</span>
        <span style="margin-left: 10px">耗时：{{ stackInfo.cost }}(ms)</span>
        <span style="margin-left: 10px">是否守护线程：{{ stackInfo.daemon }}</span>
        <span style="margin-left: 10px">优先级：{{ stackInfo.priority }}</span>
      </div>
      <div>
        <span>@{{ sessionCommand.className }}.{{ sessionCommand.methodName }}()</span>
      </div>
      <div v-for="(stackElement,index) in stackInfo.stackTrace" style="margin-left: 20px">
        <div v-if="index>0">
                        <span>{{ stackElement.declaringClass }}.{{ stackElement.methodName }}
                          ({{ stackElement.fileName }}:{{ stackElement.lineNumber }})
                        </span>
        </div>
      </div>
    </div>
  </div>
</template>
<script>
import {getContentWithDeep} from '../../util/CommonUtil';


export default {
  name: 'StackList',
  props: {
    stackInfos: {
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