<template>
  <div class="header-container">
    <div class="sub-header">
      <span class="home-icon">
        <img src="/img/logo.png">
        <small class="version-box">
          <label>JVM-EYE</label><label>1.0.0</label>
        </small>
      </span>
      <el-menu :default-active="activeMenu" class="menu-container" mode="horizontal"
               active-text-color="#ea6947" @select="selectMenu">
        <el-menu-item index="1" style="border-bottom: none">客户端列表</el-menu-item>
        <el-menu-item index="2" style="border-bottom: none" v-if="activeMenu=='2'">客户端详情</el-menu-item>
        <el-menu-item style="margin-left: 15px;border-bottom: none">
          <template slot="title">
            <div class="avatar-circle avatar-color-0">管</div>
          </template>
        </el-menu-item>
      </el-menu>
    </div>
  </div>
</template>

<script>

export default {
  name: 'Layout',
  props: {
    activeMenu: {
      type: String,
      default: "1"
    }
  },

  data() {
    return {};
  },

  created() {

  },

  methods: {
    selectMenu(newVal) {
      if (newVal == '1') {
        this.$router.push({name: 'ClientList'});
      } else if (newVal == '2') {
        this.$router.push({name: 'ClientDetail'});
      }
    }
  }
};
</script>
<style>
.header-container {
  width: 100%;
  height: 64px;
  top: 0;
  background: #fff;
  z-index: 999;
}

.header-container .sub-header {
  color: #493e54;
  border-bottom: 0;
  position: relative;
  margin: 0 0 10px;
  line-height: 1;
  font-size: 16px;
  min-height: 62px;
  box-shadow: 0 2px 10px 0 rgb(0 0 0 / 5%);
}

.header-container .sub-header > span:first-child {
  height: 60px;
  line-height: 60px;
  padding: 15px;
  font-size: 22px;
  font-weight: 700;
  font-family: monospace;
  letter-spacing: -2px;
  cursor: pointer;
  float: left;
  margin-right: 55px;
}

.header-container .sub-header > span:first-child img {
  float: left;
  height: 100%;
}

.header-container .version-box {
  font-size: 12px;
  color: #aaa;
  font-weight: 200;
  position: absolute;
  line-height: 12px;
  margin-top: 14px;
  margin-left: 2px;
}

.header-container .sub-header .menu-container {
  float: right;
  height: 60px;
  line-height: 60px;
  font-size: 14px;
  border: none;
}

.avatar-color-0 {
  background: #ffaa74;
}

.header-container .avatar-circle {
  display: -ms-flexbox;
  display: flex;
  -ms-flex-pack: center;
  justify-content: center;
  -ms-flex-align: center;
  align-items: center;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  font-size: 14px;
  color: #fff;
  cursor: pointer;
  margin-top: 14px;
}

.el-menu--horizontal > .el-menu-item.is-active {
  font-weight: 700;
}

.el-header {
  padding: 0px;
}
</style>