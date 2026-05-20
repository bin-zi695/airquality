<template>
  <div class="admin-alert">
    <el-card class="animate__animated animate__fadeInUp">
      <el-button type="primary" @click="openDialog(null)">新增阈值</el-button>
    </el-card>

    <el-card class="animate__animated animate__fadeInUp" style="margin-top:16px;animation-delay:0.1s">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="thresholdName" label="名称" width="150" />
        <el-table-column prop="aqiThreshold" label="AQI阈值" width="100" />
        <el-table-column prop="pm25Threshold" label="PM2.5阈值" width="100" />
        <el-table-column prop="pm10Threshold" label="PM10阈值" width="100" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingItem.id ? '编辑阈值' : '新增阈值'" width="450px">
      <el-form :model="form" label-width="110px">
        <el-form-item label="阈值名称"><el-input v-model="form.thresholdName" /></el-form-item>
        <el-form-item label="AQI阈值"><el-input-number v-model="form.aqiThreshold" /></el-form-item>
        <el-form-item label="PM2.5阈值"><el-input-number v-model="form.pm25Threshold" :precision="1" /></el-form-item>
        <el-form-item label="PM10阈值"><el-input-number v-model="form.pm10Threshold" :precision="1" /></el-form-item>
        <el-form-item label="启用"><el-switch v-model="form.enabled" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveItem" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { alertApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const editingItem = ref({})
const form = reactive({ thresholdName: '', aqiThreshold: 150, pm25Threshold: 75, pm10Threshold: 150, enabled: 1 })

async function fetchData() { loading.value = true; try { tableData.value = (await alertApi.listAll()).data || [] } finally { loading.value = false } }

function openDialog(row) {
  editingItem.value = row || {}
  Object.assign(form, { thresholdName: '', aqiThreshold: 150, pm25Threshold: 75, pm10Threshold: 150, enabled: 1 }, row || {})
  dialogVisible.value = true
}

async function saveItem() {
  saving.value = true
  try {
    if (editingItem.value.id) { await alertApi.update(editingItem.value.id, { ...form }) } else { await alertApi.save({ ...form }) }
    ElMessage.success('保存成功'); dialogVisible.value = false; fetchData()
  } finally { saving.value = false }
}

async function deleteItem(row) {
  await ElMessageBox.confirm('确认删除？', '确认', { type: 'warning' })
  await alertApi.delete(row.id); ElMessage.success('删除成功'); fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.admin-alert :deep(.el-card) {
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.admin-alert :deep(.el-table th.el-table__cell) {
  background: #f8f9fc !important;
  font-weight: 600 !important;
  color: #1a1a2e !important;
}
.admin-alert :deep(.el-table__row:hover) {
  background: #f0f5ff !important;
}
</style>
