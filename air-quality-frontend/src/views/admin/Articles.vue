<template>
  <div class="admin-articles">
    <el-card class="animate__animated animate__fadeInUp">
      <el-button type="primary" @click="openDialog(null)">发布资讯</el-button>
    </el-card>

    <el-card class="animate__animated animate__fadeInUp" style="margin-top:16px;animation-delay:0.1s">
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="title" label="标题" min-width="180" />
        <el-table-column prop="author" label="作者" width="100" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }"><el-tag :type="row.status === 1 ? 'success' : 'info'">{{ row.status === 1 ? '已发布' : '草稿' }}</el-tag></template>
        </el-table-column>
        <el-table-column prop="publishTime" label="发布时间" width="160" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingItem.id ? '编辑资讯' : '发布资讯'" width="700px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="标题"><el-input v-model="form.title" /></el-form-item>
        <el-form-item label="摘要"><el-input v-model="form.summary" type="textarea" :rows="2" /></el-form-item>
        <el-form-item label="内容"><el-input v-model="form.content" type="textarea" :rows="8" /></el-form-item>
        <el-form-item label="作者"><el-input v-model="form.author" /></el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">发布</el-radio>
            <el-radio :value="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="saveItem" :loading="saving">保存</el-button></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { articleApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const saving = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const editingItem = ref({})
const form = reactive({ title: '', summary: '', content: '', author: '', status: 1, publishTime: null })

async function fetchData() { loading.value = true; try { tableData.value = (await articleApi.list()).data || [] } finally { loading.value = false } }

function openDialog(row) {
  editingItem.value = row || {}
  Object.assign(form, { title: '', summary: '', content: '', author: '', status: 1, publishTime: null }, row || {})
  dialogVisible.value = true
}

async function saveItem() {
  saving.value = true
  try {
    const data = { ...form, publishTime: form.status === 1 ? new Date().toISOString() : form.publishTime }
    if (editingItem.value.id) { await articleApi.update(editingItem.value.id, data) } else { await articleApi.save(data) }
    ElMessage.success('保存成功'); dialogVisible.value = false; fetchData()
  } finally { saving.value = false }
}

async function deleteItem(row) {
  await ElMessageBox.confirm('确认删除？', '确认', { type: 'warning' })
  await articleApi.delete(row.id); ElMessage.success('删除成功'); fetchData()
}

onMounted(fetchData)
</script>

<style scoped>
.admin-articles :deep(.el-card) {
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.admin-articles :deep(.el-table th.el-table__cell) {
  background: #f8f9fc !important;
  font-weight: 600 !important;
  color: #1a1a2e !important;
}
.admin-articles :deep(.el-table__row:hover) {
  background: #f0f5ff !important;
}
</style>
