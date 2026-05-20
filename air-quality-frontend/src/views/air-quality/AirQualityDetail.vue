<template>
  <div class="detail-page">
    <el-page-header @back="$router.back()">
      <template #content>
        <span class="page-header-title"><el-icon style="margin-right:4px"><Document /></el-icon>数据详情</span>
      </template>
    </el-page-header>

    <div v-loading="loading">
      <template v-if="data">
        <!-- AQI Hero Section -->
        <div class="aqi-hero animate__animated animate__fadeInDown" :style="{ background: getColor(data.aqi) }">
          <div class="hero-bg-pattern"></div>
          <div class="hero-content">
            <div class="hero-left">
              <div class="hero-label">空气质量指数</div>
              <div class="hero-aqi">{{ data.aqi ?? '-' }}</div>
              <div class="hero-level">{{ level?.levelName || '-' }}</div>
            </div>
            <div class="hero-right">
              <div class="hero-city"><el-icon><OfficeBuilding /></el-icon> {{ cityName }}</div>
              <div class="hero-date"><el-icon><Calendar /></el-icon> {{ data.date }}</div>
              <div class="hero-weather" v-if="data.weather">
                <el-icon><Sunny /></el-icon> {{ data.weather }} {{ data.temperature != null ? data.temperature + '℃' : '' }}
              </div>
            </div>
          </div>
        </div>

        <!-- Pollutant Grid -->
        <div class="section-title-bar">
          <span class="title-dot"></span> 污染物浓度
        </div>
        <el-row :gutter="14" class="pollutant-grid">
          <el-col :span="8" v-for="(p, idx) in pollutants" :key="p.key">
            <div class="pollutant-card animate__animated animate__fadeInUp" :style="{ borderTopColor: p.color, animationDelay: `${idx * 0.08}s` }">
              <div class="pollutant-label">{{ p.label }}</div>
              <div class="pollutant-value">{{ formatVal(data[p.key]) }}</div>
              <div class="pollutant-unit">{{ p.unit }}</div>
            </div>
          </el-col>
        </el-row>

        <!-- Weather & Other Info -->
        <div class="section-title-bar" style="margin-top: 4px;">
          <span class="title-dot"></span> 气象信息
        </div>
        <el-row :gutter="14">
          <el-col :span="8" v-for="(w, idx) in weatherInfo" :key="w.key">
            <div class="weather-card animate__animated animate__fadeInUp" :style="{ animationDelay: `${0.5 + idx * 0.1}s` }">
              <div class="weather-icon"><el-icon><component :is="w.icon" /></el-icon></div>
              <div class="weather-body">
                <div class="weather-label">{{ w.label }}</div>
                <div class="weather-value">{{ formatVal(data[w.key]) }}{{ w.unit }}</div>
              </div>
            </div>
          </el-col>
        </el-row>

        <!-- Health Tip -->
        <el-alert
          v-if="level"
          :title="level.healthImplication"
          type="info"
          show-icon
          :closable="false"
          class="health-tip"
        />
      </template>

      <el-empty v-else-if="!loading" description="暂无数据" :image-size="100" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { airDataApi, cityApi } from '@/api'

const route = useRoute()
const data = ref(null)
const level = ref(null)
const loading = ref(true)
const cityName = ref('')

const aqiLevelMap = [[0,50,'优','#00E400'],[51,100,'良','#F5C842'],[101,150,'轻度污染','#FF7E00'],[151,200,'中度污染','#FF0000'],[201,300,'重度污染','#99004C'],[301,9999,'严重污染','#7E0023']]

function getColor(aqi) {
  if (aqi == null) return '#94a3b8'
  for (const [min,max,,color] of aqiLevelMap) { if (aqi>=min&&aqi<=max) return color }
  return '#94a3b8'
}

function formatVal(v) { return v != null ? v : '-' }

const pollutants = [
  { key: 'pm25', label: 'PM2.5', unit: 'μg/m³', color: '#3b82f6' },
  { key: 'pm10', label: 'PM10', unit: 'μg/m³', color: '#10b981' },
  { key: 'so2', label: 'SO₂', unit: 'μg/m³', color: '#f59e0b' },
  { key: 'no2', label: 'NO₂', unit: 'μg/m³', color: '#f97316' },
  { key: 'co', label: 'CO', unit: 'mg/m³', color: '#6366f1' },
  { key: 'o3', label: 'O₃', unit: 'μg/m³', color: '#06b6d4' },
]

const weatherInfo = [
  { key: 'temperature', label: '最高气温', icon: 'Sunny', unit: '℃' },
  { key: 'windSpeed', label: '风力', icon: 'WindPower', unit: '' },
  { key: 'humidity', label: '湿度', icon: 'Pouring', unit: '%' },
]

onMounted(async () => {
  try {
    const res = await airDataApi.getById(route.params.id)
    data.value = res.data
    if (data.value?.aqi != null) {
      const lv = await airDataApi.getAqiLevel(data.value.aqi)
      level.value = lv.data
    }
    if (data.value?.cityId) {
      try {
        const cityRes = await cityApi.listAll()
        const cities = cityRes.data || []
        const found = cities.find(c => c.id === data.value.cityId)
        if (found) cityName.value = found.name
      } catch {}
    }
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.page-header-title { font-size: 16px; font-weight: 600; }

/* AQI Hero */
.aqi-hero {
  margin-top: 20px;
  border-radius: 16px;
  padding: 32px 36px;
  position: relative;
  overflow: hidden;
  color: #fff;
  transition: background 0.3s;
}
.hero-bg-pattern {
  position: absolute;
  width: 280px;
  height: 280px;
  border-radius: 50%;
  background: rgba(255,255,255,0.06);
  top: -80px;
  right: -60px;
}
.hero-bg-pattern::after {
  content: '';
  position: absolute;
  width: 160px;
  height: 160px;
  border-radius: 50%;
  background: rgba(255,255,255,0.04);
  bottom: -40px;
  left: -40px;
}
.hero-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  position: relative;
  z-index: 1;
}
.hero-label { font-size: 13px; opacity: 0.8; margin-bottom: 4px; letter-spacing: 1px; }
.hero-aqi { font-size: 72px; font-weight: 800; line-height: 1; letter-spacing: -2px; margin-bottom: 4px; }
.hero-level { font-size: 18px; font-weight: 600; opacity: 0.9; }
.hero-right { text-align: right; }
.hero-right div {
  display: flex;
  align-items: center;
  gap: 4px;
  justify-content: flex-end;
  font-size: 14px;
  opacity: 0.85;
  margin-bottom: 6px;
}
.hero-city { font-size: 18px !important; font-weight: 600; opacity: 1 !important; }

/* Section Title */
.section-title-bar {
  font-size: 15px;
  font-weight: 600;
  color: #0f172a;
  display: flex;
  align-items: center;
  gap: 6px;
  margin: 20px 0 14px;
}
.title-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6, #06b6d4);
  display: inline-block;
  flex-shrink: 0;
}

/* Pollutant Grid */
.pollutant-grid { margin-bottom: 4px; }
.pollutant-card {
  background: #fff;
  border-radius: 12px;
  border-top: 3px solid #ccc;
  padding: 16px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  transition: transform 0.25s, box-shadow 0.25s;
  margin-bottom: 14px;
}
.pollutant-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0,0,0,0.06);
}
.pollutant-label { font-size: 11px; color: #94a3b8; margin-bottom: 4px; font-weight: 500; }
.pollutant-value { font-size: 22px; font-weight: 700; color: #0f172a; line-height: 1.2; }
.pollutant-unit { font-size: 10px; color: #94a3b8; margin-top: 2px; }

/* Weather Cards */
.weather-card {
  background: #fff;
  border-radius: 12px;
  padding: 16px 18px;
  display: flex;
  align-items: center;
  gap: 14px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  margin-bottom: 14px;
}
.weather-icon { font-size: 28px; color: #3b82f6; flex-shrink: 0; }
.weather-body { flex: 1; }
.weather-label { font-size: 11px; color: #94a3b8; margin-bottom: 2px; }
.weather-value { font-size: 18px; font-weight: 700; color: #0f172a; }

/* Health Tip */
.health-tip {
  margin-top: 20px;
  border-radius: 12px;
  border: none;
}
.health-tip :deep(.el-alert__content) { font-size: 13px; }
</style>
