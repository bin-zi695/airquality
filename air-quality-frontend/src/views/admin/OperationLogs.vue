<template>
  <div class="admin-logs">
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="操作类型">
          <el-select v-model="query.operationType" clearable style="min-width:130px">
            <el-option label="新增" value="INSERT" /><el-option label="更新" value="UPDATE" />
            <el-option label="删除" value="DELETE" /><el-option label="登录" value="LOGIN" />
          </el-select>
        </el-form-item>
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
        <el-table-column prop="username" label="用户" width="120" />
        <el-table-column prop="operationType" label="操作类型" width="80" />
        <el-table-column prop="module" label="模块" width="80" />
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="requestUrl" label="请求URL" width="180" show-overflow-tooltip />
        <el-table-column prop="ipAddress" label="IP" width="120" />
        <el-table-column label="状态" width="60">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">{{ row.status === 1 ? '成功' : '失败' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="executionTime" label="耗时(ms)" width="80" />
        <el-table-column prop="operationTime" label="时间" width="160" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { operationLogApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const query = reactive({ operationType: '', dateRange: null })

async function fetchData() {
  loading.value = true
  try {
    const params = { operationType: query.operationType || '' }
    if (query.dateRange) { params.startTime = query.dateRange[0]; params.endTime = query.dateRange[1] }
    tableData.value = (await operationLogApi.list(params)).data || []
  } finally { loading.value = false }
}
</script>
