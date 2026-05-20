<template>
  <div class="admin-logs">
    <el-card class="admin-card animate__animated animate__fadeInUp" shadow="never">
      <template #header>
        <div class="card-title-row">
          <span class="card-title"><span class="title-dot"></span>操作日志</span>
          <el-tag v-if="totalCount" size="small" round type="info">共 {{ totalCount }} 条</el-tag>
        </div>
      </template>

      <el-form :inline="true" class="filter-form">
        <el-form-item label="操作类型">
          <el-select v-model="query.action" clearable placeholder="全部类型" style="min-width:130px" @change="fetchData">
            <el-option label="用户注册" value="register" />
            <el-option label="新增" value="create" />
            <el-option label="更新" value="update" />
            <el-option label="删除" value="delete" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="query.username" placeholder="搜索用户名" clearable style="min-width:160px" @keyup.enter="fetchData" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="fetchData"><el-icon><Search /></el-icon> 查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border stripe v-loading="loading" class="data-table">
        <el-table-column prop="id" label="ID" width="60" align="center" />
        <el-table-column label="操作用户" width="120">
          <template #default="{ row }">
            <span>{{ row.username || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作类型" width="110">
          <template #default="{ row }">
            <el-tag :type="actionTagType(row.action)" size="small" round>
              {{ actionLabel(row.action) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="target" label="操作模块" width="110" />
        <el-table-column prop="detail" label="操作详情" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="IP地址" width="140" />
        <el-table-column prop="createdAt" label="操作时间" width="180" />
      </el-table>

      <el-empty v-if="!tableData.length && !loading" description="暂无操作日志" :image-size="100" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { logApi } from '@/api'
import { ElMessage } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const totalCount = ref(0)
const query = ref({ action: '', username: '' })

const actionMap = {
  register: { label: '用户注册', type: 'success' },
  create: { label: '新增', type: 'primary' },
  update: { label: '更新', type: 'warning' },
  delete: { label: '删除', type: 'danger' },
}

function actionLabel(action) { return actionMap[action]?.label || action }
function actionTagType(action) { return actionMap[action]?.type || 'info' }

async function fetchData() {
  loading.value = true
  try {
    const params = {}
    if (query.value.action) params.action = query.value.action
    if (query.value.username) params.username = query.value.username

    const [listRes, countRes] = await Promise.all([
      logApi.list(params),
      logApi.count(),
    ])
    tableData.value = listRes.data || []
    totalCount.value = countRes.data || 0
  } finally {
    loading.value = false
  }
}

function resetQuery() {
  query.value = { action: '', username: '' }
  fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.admin-card {
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.card-title-row {
  display: flex;
  align-items: center;
  gap: 10px;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  display: flex;
  align-items: center;
  gap: 6px;
}
.title-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6, #06b6d4);
  display: inline-block;
}
.filter-form {
  margin-bottom: 4px;
}
.data-table {
  width: 100%;
}
</style>
