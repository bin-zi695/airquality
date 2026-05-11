<template>
  <div class="admin-users">
    <el-card>
      <el-form :inline="true" :model="query">
        <el-form-item label="搜索"><el-input v-model="query.username" placeholder="搜索用户名" clearable /></el-form-item>
        <el-form-item label="角色">
          <el-select v-model="query.role" clearable style="width:100px"><el-option label="用户" value="user" /><el-option label="管理员" value="admin" /></el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" clearable style="width:100px"><el-option label="启用" :value="1" /><el-option label="禁用" :value="0" /></el-select>
        </el-form-item>
        <el-form-item><el-button type="primary" @click="fetchData">查询</el-button></el-form-item>
      </el-form>
    </el-card>

    <el-card style="margin-top:16px">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="email" label="邮箱" width="180" />
        <el-table-column prop="nickname" label="昵称" width="120" />
        <el-table-column prop="phone" label="手机" width="120" />
        <el-table-column prop="role" label="角色" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="注册时间" width="160" prop="createdAt" />
        <el-table-column label="操作">
          <template #default="{ row }">
            <el-button size="small" @click="toggleStatus(row)" :type="row.status === 1 ? 'warning' : 'success'">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
            <el-button size="small" type="danger" @click="deleteUser(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { userApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const query = reactive({ username: '', role: '', status: null })

async function fetchData() {
  loading.value = true
  try {
    const res = await userApi.list({ ...query })
    tableData.value = res.data || []
  } finally { loading.value = false }
}

async function toggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const text = newStatus === 0 ? '禁用' : '启用'
  await ElMessageBox.confirm(`确认${text}用户 ${row.username}？`)
  await userApi.updateStatus(row.id, newStatus)
  ElMessage.success(`${text}成功`)
  fetchData()
}

async function deleteUser(row) {
  await ElMessageBox.confirm(`确认删除用户 ${row.username}？`, '确认删除', { type: 'warning' })
  await userApi.delete(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(fetchData)
</script>
