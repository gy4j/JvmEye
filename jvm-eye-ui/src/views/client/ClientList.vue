<template>
  <div>
    <el-container>
      <el-header style="padding: 0px">
        <Header :active-menu="activeMenu"/>
      </el-header>
      <el-container>
        <el-main style="background: #f8f8f9;margin-top: 3px">
          <el-row>
            <el-col :span="4" style="text-align: left">
              <el-button type="warning" icon="el-icon-search" @click="initClientInfos">刷新数据</el-button>
            </el-col>
            <el-col :span="20" style="text-align: right">
              <el-autocomplete class="inline-input" v-model="clientSearchKey"
                               :fetch-suggestions="querySearchKey" style="width: 400px"
                               placeholder="请输入搜索关键词(客户端名称、ID、服务器名)">
                <el-button slot="append" icon="el-icon-search" @click="refreshPageClientInfos"></el-button>
              </el-autocomplete>
            </el-col>
          </el-row>
          <el-table :data="pageClientInfos" :height="dynamicHeight"
                    style="width: 100%;margin-top: 20px; margin-bottom: 20px">
            <el-table-column prop="clientName" label="客户端名称" width="400"></el-table-column>
            <el-table-column prop="clientId" label="客户端ID" width="250"></el-table-column>
            <el-table-column prop="host" label="服务器名"></el-table-column>
            <el-table-column prop="ip" label="IP" width="150"></el-table-column>
            <el-table-column prop="version" label="版本" width="150"></el-table-column>
            <el-table-column fixed="right" label="操作" width="200">
              <template slot-scope="scope">
                <el-button @click="showClientDetail(scope.row)" type="text" size="small">查看详情</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-pagination @size-change="handleSizeChange"
                         @current-change="handleCurrentChange"
                         :current-page.sync="currentPage"
                         :page-sizes="[10, 20, 30, 40]"
                         :page-size.sync="pageSize"
                         layout="total, sizes, prev, pager, next, jumper"
                         :total="total">
          </el-pagination>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>
<script>
import Vue from 'vue';
import Header from "@/components/common/Header";

export default {
  name: 'ClientList',

  components: {Header},

  data() {
    return {
      activeMenu: "1",
      clientSearchKey: "",
      suggestClientNames: [],
      clientInfos: [],
      filteredClientInfos: [],
      pageClientInfos: [],
      currentPage: 1,
      pageSize: 10,
      total: 0
    };
  },

  created() {
    this.initClientInfos();
  },

  methods: {
    showClientDetail(clientInfo) {
      this.$router.push({name: 'ClientDetail', query: clientInfo});
    },
    handleSizeChange() {
      this.refreshPageClientInfos();
    },
    handleCurrentChange() {
      this.refreshPageClientInfos();
    },
    querySearchKey(queryString, cb) {
      let results = queryString ? this.suggestClientNames
          .filter(item => item.value.toLowerCase().indexOf(queryString.toLowerCase()) != -1) : this.suggestClientNames;
      cb(results);
    },
    refreshPageClientInfos() {
      let filteredClientInfos = this.filterClientInfos();
      this.filteredClientInfos = filteredClientInfos;
      this.total = filteredClientInfos.length;

      let start = (this.currentPage - 1) * this.pageSize;
      let end = this.currentPage * this.pageSize;
      let arr = [];
      for (let i = start; i < end && i < this.total; i++) {
        arr.push(filteredClientInfos[i])
      }

      this.pageClientInfos = arr;
    },
    filterClientInfos() {
      let filteredClientInfos = [];
      if (this.clientSearchKey) {
        let sk = this.clientSearchKey.toLowerCase();
        for (let index in this.clientInfos) {
          if (this.clientInfos[index].clientName.toLowerCase().indexOf(sk) != -1
              || this.clientInfos[index].clientId.toLowerCase().indexOf(sk) != -1
              || this.clientInfos[index].host.toLowerCase().indexOf(sk) != -1) {
            filteredClientInfos.push(this.clientInfos[index])
          }
        }
      } else {
        filteredClientInfos = this.clientInfos;
      }
      return filteredClientInfos;
    },
    initClientInfos() {
      this.clientInfos = [];
      this.filteredClientInfos = [];
      Vue.axios.get('/api/client/list', {}).then((response) => {
        this.clientInfos = response;
        let arr = [];
        let exists = {};
        for (let index in this.clientInfos) {
          let clientInfo = this.clientInfos[index];
          if (!exists[clientInfo.clientName]) {
            arr.push({value: clientInfo.clientName});
            exists[clientInfo.clientName] = true;
          }
        }
        this.suggestClientNames = arr;
        this.refreshPageClientInfos();
      });
    }
  },
  computed: {
    dynamicHeight: function () {
      return (window.innerHeight - 210);
    }
  }
};
</script>
<style>
</style>