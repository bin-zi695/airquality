<template>
  <div class="admin-logs">
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="请求路径"><el-input v-model="query.requestUrl" placeholder="如 /api/air-data" /></el-form-item>
        <el-form-item label="时间范围">
          <el-date-picker v-model="query.dateRange" type="datetimerange" range-separator="至"
            start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item><el-button type="primary" @click="fetchData">查询</el-button></el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:16px">
      <el-table :data="tableData" border stripe v-loading="loading" max-height="600">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="ipAddress" label="IP" width="130" />
        <el-table-column prop="requestMethod" label="方法" width="70" />
        <el-table-column prop="requestUrl" label="请求路径" min-width="180" show-overflow-tooltip />
        <el-table-column prop="responseStatus" label="状态码" width="80" />
        <el-table-column prop="executionTime" label="耗时(ms)" width="90" />
        <el-table-column prop="userAgent" label="User-Agent" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createdAt" label="时间" width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { accessLogApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const query = reactive({ requestUrl: '', dateRange: null })

async function fetchData() {
  loading.value = true
  try {
    const params = { requestUrl: query.requestUrl || null }
    if (query.dateRange) { params.startTime = query.dateRange[0]; params.endTime = query.dateRange[1] }
    tableData.value = (await accessLogApi.list(params)).data || []
  } finally { loading.value = false }
}
</script>
