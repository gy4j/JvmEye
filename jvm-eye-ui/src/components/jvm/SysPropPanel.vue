<template>
  <div>
    <el-row>
      <el-col style="text-align: left;">
        <el-button @click="showSysProps()" type="warning">刷新数据</el-button>
      </el-col>
    </el-row>
    <GroupLabelValue :group-obj="sysProps" group-name="系统属性" :style="{height:dynamicHeight}"
                     style="height: 480px;overflow-y: scroll;margin-top: 10px;"></GroupLabelValue>
  </div>
</template>
<script>
import Vue from "vue";
import GroupLabelValue from "@/components/common/GroupLabelValue";

export default {
  name: 'SysPropPanel',

  components: {
    GroupLabelValue,
  },

  props: {
    clientId: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      sysProps: {},
    }
  },
  created() {
    // this.showSysProps();
  },
  methods: {
    showSysProps() {
      Vue.axios.post('/api/jvm/sysprop?clientId=' + this.clientId
          , {}).then((response) => {
        this.sysProps = response.info;
      });
    },
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