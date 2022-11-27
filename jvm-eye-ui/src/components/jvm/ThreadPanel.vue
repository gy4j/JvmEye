<template>
  <div>
    <el-row>
      <el-col :span=8 style="text-align: left;">
        <el-input v-model="sampleInterval" placeholder="请输入采样间隔(100~5000)"
                  style="width: 200px;margin-right: 10px"></el-input>
        <el-button @click="showAllThreads()" type="warning">刷新数据</el-button>
      </el-col>
      <el-col :span="16" style="text-align: right">
        <el-select v-model="filterState">
          <el-option key="all" label="所有" value="all">
          </el-option>
          <el-option
              v-for="item in stateOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value">
          </el-option>
        </el-select>
        <el-autocomplete class="inline-input" v-model="threadSearchKey" clearable
                         :fetch-suggestions="querySearchKey" style="width: 400px;margin-left: 20px"
                         placeholder="请输入搜索关键词">
          <el-button slot="append" icon="el-icon-search" @click="refreshThreads"></el-button>
        </el-autocomplete>
      </el-col>
    </el-row>
    <el-table :data="filteredThreads" :height="dynamicHeight" style="margin-top:10px;">
      <el-table-column prop="id" label="ID" width="100"></el-table-column>
      <el-table-column prop="name" label="名称" sortable></el-table-column>
      <el-table-column prop="group" label="线程组" sortable>
        <template slot-scope="scope">
          {{ scope.row.group ? scope.row.group : "-" }}
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="100" sortable>
        <template slot-scope="scope">
          {{ parseInt(scope.row.priority) }}
        </template>
      </el-table-column>
      <el-table-column prop="state" label="状态" width="200" sortable>
        <template slot-scope="scope">
          {{ scope.row.state ? scope.row.state : "-" }}
        </template>
      </el-table-column>
      <el-table-column prop="cpu" label="CPU占用" width="100" sortable>
        <template slot-scope="scope">
          {{ scope.row.cpu }}%
        </template>
      </el-table-column>
      <el-table-column prop="deltaTime" label="增量CPU时间" width="150">
        <template slot-scope="scope">
          {{ scope.row.deltaTime }}ms
        </template>
      </el-table-column>
      <el-table-column prop="daemon" label="守护线程" width="100">
        <template slot-scope="scope">
          {{ scope.row.daemon ? '是' : '否' }}
        </template>
      </el-table-column>
      <el-table-column fixed="right" label="操作" width="150">
        <template slot-scope="scope">
          <el-button @click="showThreadDetail(scope.row)" type="text" size="small">详情</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-dialog :visible.sync="dialogThreadDetailVisible" :fullscreen="true">
      <GroupLabelValue group-name="线程详情" :groupObj="threadInfo"/>
    </el-dialog>
  </div>
</template>
<script>
import Vue from "vue";
import GroupLabelValue from "@/components/common/GroupLabelValue";

export default {
  name: 'ThreadPanel',
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
      sampleInterval: 500,
      threadInfos: [],
      filteredThreads: [],
      threadInfo: {},
      dialogThreadDetailVisible: false,
      threadSearchKey: "",
      suggestThreadNames: "",
      filterState: "all",
      stateOptions: [],
    }
  },
  created() {
  },
  methods: {
    showAllThreads() {
      this.threadInfos = [];
      Vue.axios.post('/api/thread/all?clientId=' + this.clientId
          , {"sampleInterval": this.sampleInterval}).then((response) => {
        this.threadInfos = response.threadSampleInfos;
        this.refreshThreads();
      });
    },
    showThreadDetail(threadInfo) {
      this.threadInfo = threadInfo;
      this.dialogThreadDetailVisible = true;
    },
    querySearchKey(queryString, cb) {
      let results = queryString ? this.suggestThreadNames
          .filter(item => item.value.toLowerCase().indexOf(queryString.toLowerCase()) != -1) : this.suggestThreadNames;
      cb(results);
    },
    refreshThreads() {
      let arr = [];
      for (let index in this.threadInfos) {
        let thread = this.threadInfos[index];
        if (this.filterState != 'all' && thread.state != this.filterState) {
          continue;
        }
        if (!this.threadSearchKey) {
          arr.push(thread)
        } else if (thread.name.toLowerCase().indexOf(this.threadSearchKey.toLowerCase()) != -1) {
          arr.push(thread)
        }
      }
      this.filteredThreads = arr;
    },
  },
  watch: {
    threadInfos: function (newVal) {
      this.suggestThreadNames = [];
      let arr = [];
      let exists = {};
      let stateOptions = [{label: "-", value: "-"}];
      for (let index in newVal) {
        let thread = newVal[index];
        if (thread.name && !exists[thread.name]) {
          arr.push({value: thread.name});
          exists[thread.name] = true;
        }
        if (thread.state && !exists[thread.state]) {
          stateOptions.push({value: thread.state, label: thread.state});
          exists[thread.state] = true;
        }
        newVal[index].stackTraces = "";
      }
      this.stateOptions = stateOptions;
      this.suggestThreadNames = arr;
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