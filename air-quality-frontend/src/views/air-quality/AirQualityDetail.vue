<template>
  <div class="detail-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-header-title"><el-icon style="margin-right:4px"><Document /></el-icon>数据详情</span>
      </template>
    </el-page-header>
    <el-card class="detail-card" shadow="never" v-loading="loading">
      <template v-if="data">
        <el-descriptions :column="3" border class="detail-desc">
          <el-descriptions-item label="日期">{{ data.date }}</el-descriptions-item>
          <el-descriptions-item label="AQI">
            <span class="aqi-badge-sm" :style="{ background: getColor(data.aqi) }">{{ data.aqi ?? '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="等级">
            <span class="level-text">{{ level?.levelName || '-' }}</span>
          </el-descriptions-item>
          <el-descriptions-item label="PM2.5">{{ data.pm25 ?? '-' }} μg/m³</el-descriptions-item>
          <el-descriptions-item label="PM10">{{ data.pm10 ?? '-' }} μg/m³</el-descriptions-item>
          <el-descriptions-item label="SO₂">{{ data.so2 ?? '-' }} μg/m³</el-descriptions-item>
          <el-descriptions-item label="NO₂">{{ data.no2 ?? '-' }} μg/m³</el-descriptions-item>
          <el-descriptions-item label="CO">{{ data.co ?? '-' }} mg/m³</el-descriptions-item>
          <el-descriptions-item label="O₃">{{ data.o3 ?? '-' }} μg/m³</el-descriptions-item>
          <el-descriptions-item label="最高气温">{{ data.temperature != null ? data.temperature + ' ℃' : '-' }}</el-descriptions-item>
          <el-descriptions-item label="风力">{{ data.windSpeed || '-' }}</el-descriptions-item>
          <el-descriptions-item label="天气状况">{{ data.weather || '-' }}</el-descriptions-item>
        </el-descriptions>
        <el-alert v-if="level" :title="level.healthImplication" type="info" show-icon :closable="false" class="health-tip" />
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { airDataApi } from '@/api'

const route = useRoute()
const data = ref(null)
const level = ref(null)
const loading = ref(true)

const aqiLevelMap = [[0,50,'优','#00E400'],[51,100,'良','#F5C842'],[101,150,'轻度污染','#FF7E00'],[151,200,'中度污染','#FF0000'],[201,300,'重度污染','#99004C'],[301,9999,'严重污染','#7E0023']]
function getColor(aqi) {
  if (aqi == null) return '#999'
  for (const [min,max,,color] of aqiLevelMap) { if (aqi>=min&&aqi<=max) return color }
  return '#999'
}

onMounted(async () => {
  try {
    const res = await airDataApi.getById(route.params.id)
    data.value = res.data
    if (data.value?.aqi != null) {
      const lv = await airDataApi.getAqiLevel(data.value.aqi)
      level.value = lv.data
    }
  } finally { loading.value = false }
})
</script>

<style scoped>
.detail-page { }
.page-header-title { font-size: 16px; font-weight: 600; }
.detail-card {
  margin-top: 20px;
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.detail-desc { }
.aqi-badge-sm {
  display: inline-flex; align-items: center; justify-content: center;
  min-width: 32px; height: 22px; border-radius: 11px;
  font-size: 11px; font-weight: 600; color: #fff; padding: 0 10px;
}
.level-text { font-weight: 700; font-size: 15px; }
.health-tip { margin-top: 20px; border-radius: 10px; }
</style>
