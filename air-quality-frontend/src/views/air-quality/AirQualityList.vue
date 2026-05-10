<template>
  <div class="air-quality-list">
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true" :model="query" class="filter-form">
        <el-form-item label="城市">
          <el-select v-model="query.cityId" placeholder="选择城市" clearable size="large" style="min-width:180px">
            <el-option v-for="c in cities" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker v-model="query.dateRange" type="daterange" range-separator="至"
            start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" round @click="fetchData">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button size="large" round @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card" shadow="never" v-loading="loading">
      <template #header>
        <div class="card-title">
          <span class="title-dot"></span>查询结果
          <el-tag v-if="tableData.length" size="small" round type="info" style="margin-left:8px">{{ tableData.length }} 条</el-tag>
        </div>
      </template>
      <el-table :data="tableData" border stripe class="data-table">
        <el-table-column prop="date" label="日期" width="120" />
        <el-table-column label="城市" width="100">
          <template #default="{ row }">
            <span class="city-label">🏙 {{ getCityName(row.cityId) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="aqi" label="AQI" width="80">
          <template #default="{ row }">
            <span class="aqi-badge-sm" :style="{ background: getAqiColor(row.aqi) }">{{ row.aqi }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="pm25" label="PM2.5" width="80" />
        <el-table-column prop="pm10" label="PM10" width="80" />
        <el-table-column prop="so2" label="SO₂" width="70" />
        <el-table-column prop="no2" label="NO₂" width="70" />
        <el-table-column prop="co" label="CO" width="70" />
        <el-table-column prop="o3" label="O₃" width="70" />
        <el-table-column prop="temperature" label="最高气温" width="90">
          <template #default="{ row }">{{ row.temperature != null ? row.temperature + '℃' : '-' }}</template>
        </el-table-column>
        <el-table-column prop="wind_speed" label="风力" width="100" />
        <el-table-column prop="weather" label="天气状况" width="100" />
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ row }">
            <el-button size="small" type="primary" link @click="$router.push(`/air-quality/${row.id}`)">详情 →</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div v-if="!tableData.length && !loading" class="empty-hint">
        <el-empty description="暂无数据" :image-size="100">
          <span style="color:#999">请选择城市和日期范围查询</span>
        </el-empty>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { airDataApi, cityApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const cities = ref([])
const cityMap = ref({})
const query = reactive({ cityId: null, dateRange: null })

const aqiLevelMap = [[0,50,'#00E400'],[51,100,'#FFFF00'],[101,150,'#FF7E00'],[151,200,'#FF0000'],[201,300,'#99004C'],[301,9999,'#7E0023']]

function getAqiColor(aqi) {
  if (!aqi) return '#999'
  for (const [min,max,,color] of aqiLevelMap) { if (aqi>=min&&aqi<=max) return color }
  return '#999'
}

function getCityName(id) { return cityMap.value[id] || '' }

async function fetchData() {
  loading.value = true
  try {
    const params = {}
    if (query.cityId) params.cityId = query.cityId
    if (query.dateRange) {
      params.startDate = query.dateRange[0]
      params.endDate = query.dateRange[1]
    }
    const res = await airDataApi.list(params)
    tableData.value = res.data || []
  } finally { loading.value = false }
}

function resetQuery() {
  query.cityId = null
  query.dateRange = null
  tableData.value = []
}

onMounted(async () => {
  const res = await cityApi.listAll()
  cities.value = res.data || []
  cities.value.forEach(c => { cityMap.value[c.id] = c.name })
})
</script>

<style scoped>
.air-quality-list { }

.filter-card {
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  margin-bottom: 16px;
}

.filter-form { margin-bottom: -8px; }

.table-card {
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
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
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: inline-block;
}

.city-label { font-weight: 500; }

.aqi-badge-sm {
  display: inline-flex; align-items: center; justify-content: center;
  min-width: 32px; height: 22px; border-radius: 11px;
  font-size: 11px; font-weight: 600; color: #fff; padding: 0 8px;
}

.empty-hint { padding: 40px 0; }

.data-table :deep(th) {
  background: #fafbfc;
  font-weight: 600;
}
</style>
