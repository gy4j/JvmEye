<template>
  <div>
    <el-row>
      <el-col style="text-align: left;">
        <el-button @click="showMemoryInfos()" type="warning">刷新数据</el-button>
      </el-col>
    </el-row>
    <el-table :data="memoryInfos" :height="dynamicHeight" style="margin-top:10px;">
      <el-table-column prop="type" label="类型" width="150"></el-table-column>
      <el-table-column prop="name" label="名称" width="200"></el-table-column>
      <el-table-column prop="used" label="已使用">
        <template slot-scope="scope">
          <span>{{ computeValue(scope.row.used) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="total" label="总大小">
        <template slot-scope="scope">
          <span>{{ computeValue(scope.row.total) }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="max" label="最大">
        <template slot-scope="scope">
          <span>{{ computeValue(scope.row.max) }}</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script>
import Vue from "vue";

export default {
  name: 'MemoryPanel',
  props: {
    clientId: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      memoryInfos: [],
    }
  },
  created() {
    // this.showMemoryInfos();
  },
  methods: {
    showMemoryInfos() {
      Vue.axios.post('/api/jvm/memory?clientId=' + this.clientId
          , {}).then((response) => {
        this.memoryInfos = response.memoryInfos;
      });
    },
    computeValue(value) {
      if (value <= 0) {
        return "0M";
      }
      return (value / 1024 / 1024).toFixed(2) + "M";
    }
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