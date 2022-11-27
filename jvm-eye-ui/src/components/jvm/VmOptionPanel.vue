<template>
  <div>
    <el-row>
      <el-col style="text-align: left;">
        <el-button @click="showVmOptions()" type="warning">刷新数据</el-button>
      </el-col>
    </el-row>
    <el-table :data="vmOptions" :height="dynamicHeight" style="margin-top:10px;">
      <el-table-column prop="name" label="类型" width="350"></el-table-column>
      <el-table-column prop="value" label="值" width="350"></el-table-column>
      <el-table-column prop="writeable" label="是否可改">
        <template slot-scope="scope">
          {{ scope.row.writeable ? '是' : '否' }}
        </template>
      </el-table-column>
      <el-table-column prop="origin" label="源类型"></el-table-column>
    </el-table>
  </div>
</template>
<script>
import Vue from "vue";

export default {
  name: 'VmOptionPanel',
  props: {
    clientId: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      vmOptions: [],
    }
  },
  created() {
    // this.showVmOptions();
  },
  methods: {
    showVmOptions() {
      Vue.axios.post('/api/jvm/vmoption?clientId=' + this.clientId
          , {}).then((response) => {
        this.vmOptions = response.vmOptions;
      });
    },
  },
  computed: {
    dynamicHeight: function () {
      return (window.innerHeight - 270);
    }
  }
}
</script>
<style>

</style>