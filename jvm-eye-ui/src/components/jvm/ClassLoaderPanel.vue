<template>
  <div>
    <el-row>
      <el-col :span="4" style="text-align: left">
        <el-button @click="showClassLoaders()" type="warning">刷新数据</el-button>
      </el-col>
      <el-col :span="20" style="text-align: right">
        <el-checkbox v-model="filterDelegatingClassLoader" @change="refreshClassLoaders">
          排除DelegatingClassLoader
        </el-checkbox>
        <el-autocomplete class="inline-input" v-model="classLoaderSearchKey" clearable
                         :fetch-suggestions="querySearchKey" style="width: 400px;margin-left: 20px"
                         placeholder="请输入搜索关键词">
          <el-button slot="append" icon="el-icon-search" @click="refreshClassLoaders"></el-button>
        </el-autocomplete>
      </el-col>
    </el-row>
    <el-table :data="filteredClassLoaders" :height="dynamicHeight" style="margin-top:10px;">
      <el-table-column prop="name" label="类加载器名" width="350"></el-table-column>
      <el-table-column prop="classLoaderHash" label="类加载器Hash" width="150"></el-table-column>
      <el-table-column prop="parentName" label="父类加载器"></el-table-column>
      <el-table-column prop="loadedClassCount" label="类加载数量" width="150"></el-table-column>
    </el-table>
  </div>
</template>
<script>
import Vue from "vue";

export default {
  name: 'ClassLoaderPanel',
  props: {
    clientId: {
      type: String,
      default: null,
    },
  },
  data() {
    return {
      classLoaders: [],
      filteredClassLoaders: [],
      suggestClassLoaderNames: [],
      classLoaderSearchKey: "",
      filterDelegatingClassLoader: true
    }
  },
  created() {
    // this.showClassLoaders();
  },
  methods: {
    showClassLoaders() {
      Vue.axios.post('/api/jvm/classloader?clientId=' + this.clientId
          , {}).then((response) => {
        this.classLoaders = response.classLoaderInfos;
        this.refreshClassLoaders();
      });
    },
    querySearchKey(queryString, cb) {
      let results = queryString ? this.suggestClassLoaderNames
          .filter(item => item.value.toLowerCase().indexOf(queryString.toLowerCase()) != -1) : this.suggestClassLoaderNames;
      cb(results);
    },
    refreshClassLoaders() {
      let arr = [];
      for (let index in this.classLoaders) {
        let classLoader = this.classLoaders[index];
        if (this.filterDelegatingClassLoader && classLoader.className == 'sun.reflect.DelegatingClassLoader') {
          continue;
        }
        if (!this.classLoaderSearchKey) {
          arr.push(classLoader)
        } else if (classLoader.name.toLowerCase().indexOf(this.classLoaderSearchKey.toLowerCase()) != -1) {
          arr.push(classLoader)
        }
      }
      this.filteredClassLoaders = arr;
    }
  },
  watch: {
    classLoaders: function (newVal) {
      this.suggestClassLoaderNames = [];
      let arr = [];
      let exists = {};
      for (let index in newVal) {
        let classLoader = newVal[index];
        if (classLoader.className && !exists[classLoader.className]) {
          arr.push({value: classLoader.className});
          exists[classLoader.className] = true;
        }
      }
      this.suggestClassLoaderNames = arr;
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