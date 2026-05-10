<template>
  <div class="admin-cities">
    <el-card>
      <el-button type="primary" @click="openDialog(null)">新增城市</el-button>
    </el-card>

    <el-card style="margin-top:16px">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="name" label="城市名" width="100" />
        <el-table-column prop="province" label="省份" width="100" />
        <el-table-column prop="latitude" label="纬度" width="100" />
        <el-table-column prop="longitude" label="经度" width="100" />
        <el-table-column prop="category" label="分类" width="100" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteCity(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingCity.id ? '编辑城市' : '新增城市'" width="480px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="城市名"><el-input v-model="form.name" /></el-form-item>
        <el-form-item label="省份"><el-input v-model="form.province" /></el-form-item>
        <el-form-item label="纬度"><el-input v-model.number="form.latitude" /></el-form-item>
        <el-form-item label="经度"><el-input v-model.number="form.longitude" /></el-form-item>
        <el-form-item label="分类"><el-input v-model="form.category" /></el-form-item>
        <el-form-item label="状态">
          <el-switch v-model="form.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveCity" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { cityApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const editingCity = ref({})
const form = reactive({ name: '', province: '', latitude: null, longitude: null, category: '', status: 1 })

async function fetchData() {
  loading.value = true
  try {
    const res = await cityApi.listAll()
    tableData.value = res.data || []
  } finally { loading.value = false }
}

function openDialog(row) {
  editingCity.value = row || {}
  Object.assign(form, { name: '', province: '', latitude: null, longitude: null, category: '', status: 1 }, row || {})
  dialogVisible.value = true
}

async function saveCity() {
  saving.value = true
  try {
    if (editingCity.value.id) {
      await cityApi.update(editingCity.value.id, { ...form })
    } else {
      await cityApi.save({ ...form })
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } finally { saving.value = false }
}

async function deleteCity(row) {
  await ElMessageBox.confirm(`确认删除城市 ${row.name}？`, '确认删除', { type: 'warning' })
  await cityApi.delete(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(fetchData)
</script>
