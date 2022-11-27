<template>
  <div v-if="groupObj!=null && Object.keys(groupObj).length!=0">
    <el-row
        v-for="(value,key) in groupObj"
        class="group-content">
      <el-col :span=4 class="group-label">{{ key }}:</el-col>
      <el-col :span=20 class="group-value">{{ value }}</el-col>
    </el-row>
  </div>
</template>
<script>
export default {
  name: 'GroupLabelValue',
  props: {
    groupObj: {
      type: Object,
      default: null,
    },
    groupName: {
      type: String,
      default: null,
    },
  },
  components: {},
  data() {
    return {}
  },
  created() {
  },
  methods: {}
}
</script>
<style>

.group-content {
  font-size: 12px;
  word-break: break-all;
  line-height: 14px;
}

.group-label {
  font-weight: bold;
  text-align: right;
  padding-right: 10px
}

.group-value {
  text-align: left;
  white-space: pre-wrap;
}
</style>