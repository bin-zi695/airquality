<template>
  <div class="air-quality-list">
    <el-card class="filter-card animate__animated animate__fadeInUp" shadow="never">
      <el-form :inline="true" :model="query" class="filter-form">
        <el-form-item label="省份">
          <el-select v-model="filterProvince" placeholder="全部省份" clearable size="large" style="min-width:130px" @change="query.cityId = null">
            <el-option v-for="p in provinces" :key="p" :label="p" :value="p" />
          </el-select>
        </el-form-item>
        <el-form-item label="城市">
          <el-select v-model="query.cityId" placeholder="选择城市" clearable filterable size="large" style="min-width:180px">
            <el-option v-for="c in filteredCities" :key="c.id" :label="`${c.name} · ${c.province || ''}`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="日期范围">
          <el-date-picker v-model="query.dateRange" type="daterange" range-separator="至"
            start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" size="large"
            :disabled-date="disabledDate" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" round @click="fetchData">
            <el-icon><Search /></el-icon> 查询
          </el-button>
          <el-button size="large" round @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card animate__animated animate__fadeInUp" shadow="never" v-loading="loading" style="animation-delay:0.1s">
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
            <span class="city-label"><el-icon style="margin-right:2px"><OfficeBuilding /></el-icon>{{ getCityName(row.cityId) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="AQI" width="80">
          <template #default="{ row }">
            <span class="aqi-badge-sm" :style="{ background: getAqiColor(row.aqi) }">{{ row.aqi ?? '-' }}</span>
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
        <el-table-column prop="windSpeed" label="风力" width="100" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { airDataApi, cityApi } from '@/api'

const loading = ref(false)
const tableData = ref([])
const cities = ref([])
const cityMap = ref({})
const query = reactive({ cityId: null, dateRange: null })
const filterProvince = ref('')

const provinces = computed(() => {
  const set = new Set(cities.value.map(c => c.province).filter(Boolean))
  return [...set].sort()
})
const filteredCities = computed(() => {
  if (!filterProvince.value) return cities.value
  return cities.value.filter(c => c.province === filterProvince.value)
})

const aqiLevelMap = [
  [0, 50, '优', '#00E400'],
  [51, 100, '良', '#F5C842'],
  [101, 150, '轻度污染', '#FF7E00'],
  [151, 200, '中度污染', '#FF0000'],
  [201, 300, '重度污染', '#99004C'],
  [301, 9999, '严重污染', '#7E0023'],
]

function getAqiColor(aqi) {
  if (aqi == null) return '#999'
  for (const [min, max, , color] of aqiLevelMap) { if (aqi >= min && aqi <= max) return color }
  return '#999'
}

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
  fetchData()
})
</script>

<style scoped>
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
  background: linear-gradient(135deg, #3b82f6, #06b6d4);
  display: inline-block;
}

.city-label { font-weight: 500; display: flex; align-items: center; }

.aqi-badge-sm {
  display: inline-flex; align-items: center; justify-content: center;
  min-width: 32px; height: 22px; border-radius: 11px;
  font-size: 11px; font-weight: 600; color: #fff; padding: 0 8px;
}

.empty-hint { padding: 40px 0; }

.data-table {
  border-radius: 12px;
}
.data-table :deep(th.el-table__cell) {
  background: #f8f9fc !important;
  font-weight: 600 !important;
  color: #1a1a2e !important;
}
.data-table :deep(.el-table__row:hover) {
  background: #f0f5ff !important;
}
</style>
