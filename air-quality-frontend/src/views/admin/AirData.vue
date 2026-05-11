<template>
  <div class="admin-air-data">
    <el-card>
      <el-button type="primary" @click="openDialog(null)">新增数据记录</el-button>
      <el-button type="success" @click="triggerSync" :loading="syncing" style="margin-left:12px">
        <el-icon><Refresh /></el-icon> 采集数据
      </el-button>
    </el-card>

    <el-card style="margin-top:16px">
      <el-form :inline="true" :model="query">
        <el-form-item label="城市">
          <el-select v-model="query.cityId" clearable placeholder="选择城市" style="min-width:150px">
            <el-option v-for="c in cities" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker v-model="query.dateRange" type="daterange" range-separator="至"
            start-placeholder="开始" end-placeholder="结束" value-format="YYYY-MM-DD"
            :disabled-date="disabledDate" />
        </el-form-item>
        <el-form-item><el-button type="primary" @click="fetchData">查询</el-button></el-form-item>
      </el-form>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column label="城市" width="80"><template #default="{ row }">{{ getCityName(row.cityId) }}</template></el-table-column>
        <el-table-column prop="date" label="日期" width="110" />
        <el-table-column prop="aqi" label="AQI" width="70" />
        <el-table-column prop="pm25" label="PM2.5" width="70" />
        <el-table-column prop="pm10" label="PM10" width="70" />
        <el-table-column prop="temperature" label="最高气温" width="80">
          <template #default="{ row }">{{ row.temperature != null ? row.temperature + '℃' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="wind_speed" label="风力" width="80" />
        <el-table-column prop="weather" label="天气" width="90" />
        <el-table-column label="操作" width="160">
          <template #default="{ row }">
            <el-button size="small" @click="openDialog(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="deleteData(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="editingData.id ? '编辑数据' : '新增数据'" width="600px">
      <el-form :model="form" label-width="80px">
        <el-row :gutter="16">
          <el-col :span="12"><el-form-item label="城市"><el-select v-model="form.cityId" style="width:100%"><el-option v-for="c in cities" :key="c.id" :label="c.name" :value="c.id" /></el-select></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="日期"><el-date-picker v-model="form.date" value-format="YYYY-MM-DD" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="AQI"><el-input-number v-model="form.aqi" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="PM2.5"><el-input-number v-model="form.pm25" :precision="1" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="PM10"><el-input-number v-model="form.pm10" :precision="1" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="最高气温"><el-input-number v-model="form.temperature" :precision="1" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="风力"><el-input v-model="form.wind_speed" /></el-form-item></el-col>
          <el-col :span="12"><el-form-item label="天气"><el-input v-model="form.weather" /></el-form-item></el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveData" :loading="saving">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { airDataApi, cityApi } from '@/api'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh } from '@element-plus/icons-vue'
import axios from 'axios'

const loading = ref(false)
const syncing = ref(false)
const saving = ref(false)
const tableData = ref([])
const cities = ref([])
const cityMap = ref({})
const dialogVisible = ref(false)
const editingData = ref({})
const query = reactive({ cityId: null, dateRange: null })
const form = reactive({ cityId: null, date: null, aqi: null, pm25: null, pm10: null, so2: null, no2: null, co: null, o3: null, temperature: null, wind_speed: '', weather: '' })

function getCityName(id) { return cityMap.value[id] || '' }

function disabledDate(time) {
  const d = new Date()
  d.setDate(d.getDate() - 15)
  return time.getTime() < d.getTime() || time.getTime() > Date.now() + 86400000
}

async function fetchData() {
  loading.value = true
  try {
    const params = {}
    if (query.cityId) params.cityId = query.cityId
    if (query.dateRange) { params.startDate = query.dateRange[0]; params.endDate = query.dateRange[1] }
    const res = await airDataApi.list(params)
    tableData.value = res.data || []
  } finally { loading.value = false }
}

function openDialog(row) {
  editingData.value = row || {}
  Object.assign(form, { cityId: null, date: null, aqi: null, pm25: null, pm10: null, so2: null, no2: null, co: null, o3: null, temperature: null, wind_speed: '', weather: '' }, row || {})
  dialogVisible.value = true
}

async function saveData() {
  saving.value = true
  try {
    if (editingData.value.id) {
      await airDataApi.update(editingData.value.id, { ...form })
    } else {
      await airDataApi.save({ ...form })
    }
    ElMessage.success('保存成功')
    dialogVisible.value = false
    fetchData()
  } finally { saving.value = false }
}

async function deleteData(row) {
  await ElMessageBox.confirm(`确认删除该条数据？`, '确认删除', { type: 'warning' })
  await airDataApi.delete(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

async function triggerSync() {
  syncing.value = true
  try {
    const res = await axios.post('/api/admin/sync-now')
    const d = res.data?.data
    ElMessage.success(`采集完成: 成功${d?.success || 0} 失败${d?.fail || 0}`)
    fetchData()
  } catch (e) {
    ElMessage.error('采集请求失败')
  } finally { syncing.value = false }
}

onMounted(async () => {
  const res = await cityApi.listAll()
  cities.value = res.data || []
  cities.value.forEach(c => { cityMap.value[c.id] = c.name })
})
</script>
