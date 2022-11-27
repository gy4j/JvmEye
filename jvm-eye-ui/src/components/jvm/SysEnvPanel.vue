<template>
  <div>
    <el-row>
      <el-col style="text-align: left;">
        <el-button @click="showSysEnvs()" type="warning">刷新数据</el-button>
      </el-col>
    </el-row>
    <GroupLabelValue :group-obj="sysEnvs" group-name="环境变量" :style="{height:dynamicHeight}"
                     style="height: 480px;overflow-y: scroll;margin-top: 10px;"></GroupLabelValue>
  </div>
</template>
<script>
import Vue from "vue";
import GroupLabelValue from "@/components/common/GroupLabelValue";

export default {
  name: 'SysEnvPanel',

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
      sysEnvs: {},
    }
  },
  created() {
    // this.showSysEnvs();
  },
  methods: {
    showSysEnvs() {
      Vue.axios.post('/api/jvm/sysenv?clientId=' + this.clientId
          , {}).then((response) => {
        this.sysEnvs = response.info;
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