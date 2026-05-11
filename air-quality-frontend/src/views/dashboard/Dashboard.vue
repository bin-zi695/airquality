<template>
  <div class="dashboard">
    <div class="alert-wrap" v-if="alertVisible">
      <el-alert
        :title="`AQI 预警: ${alertCityName} 当前 AQI 为 ${alertData.aqi}，请注意防护！`"
        type="warning"
        :closable="true"
        show-icon
        class="alert-animate"
        @close="alertVisible = false"
      />
    </div>

    <el-row :gutter="20" class="stats-row">
      <el-col :span="6" v-for="(item, idx) in statsCards" :key="item.label">
        <div class="stat-card-dash" :class="`stat-grad-${idx}`">
          <div class="stat-inner">
            <div class="stat-left">
              <div class="stat-num">{{ item.value }}</div>
              <div class="stat-desc">{{ item.label }}</div>
            </div>
            <div class="stat-icon-wrap">
              <el-icon :size="28"><component :is="item.icon" /></el-icon>
            </div>
          </div>
          <div class="stat-pattern"></div>
        </div>
      </el-col>
    </el-row>

    <el-card class="section-card fav-section" shadow="never">
      <template #header>
        <div class="section-header">
          <span class="section-title">
            <span class="title-dot"></span>我的收藏城市
          </span>
          <div class="header-right-controls">
            <el-select v-model="selectedDate" placeholder="选择日期" size="small" style="width:150px" @change="loadByDate" v-if="hasFavorites && dates.length">
              <el-option v-for="d in dates" :key="d" :label="d" :value="d" />
            </el-select>
            <el-button type="primary" size="small" round @click="$router.push('/favorites')">
              管理收藏 <el-icon style="margin-left:2px"><ArrowRight /></el-icon>
            </el-button>
          </div>
        </div>
      </template>

      <div v-if="loading" class="loading-wrap">
        <el-skeleton :rows="3" animated />
      </div>

      <div class="empty-wrap" v-else-if="!hasFavorites">
        <el-empty description="还没有收藏城市，快去添加吧">
          <el-button type="primary" round @click="$router.push('/air-quality')"><el-icon style="margin-right:4px"><Search /></el-icon>去查询</el-button>
        </el-empty>
      </div>

      <div class="empty-wrap" v-else-if="!favoriteData.length">
        <el-empty description="收藏的城市暂无空气质量数据" :image-size="100">
          <el-button type="primary" round @click="$router.push('/air-quality')"><el-icon style="margin-right:4px"><Search /></el-icon>去查询</el-button>
        </el-empty>
      </div>

      <el-row v-else :gutter="16">
        <el-col :span="8" v-for="item in favoriteData" :key="item.id">
          <div class="aqi-card-new" :style="{ borderTopColor: getAqiColor(item.aqi) }" @click="$router.push(`/air-quality/${item.id}`)">
            <div class="aqi-top">
              <div class="aqi-city">
                <el-icon style="margin-right:2px"><OfficeBuilding /></el-icon>
                {{ getCityName(item.cityId) }}
              </div>
              <span class="aqi-badge-sm" :style="{ background: getAqiColor(item.aqi) }">
                {{ getAqiLevelName(item.aqi) }}
              </span>
            </div>
            <div class="aqi-big" :style="{ color: getAqiColor(item.aqi) }">
              {{ item.aqi ?? '-' }}
            </div>
            <div class="aqi-unit">AQI · 空气质量指数</div>
            <div class="pollutants">
              <div class="p-item">
                <div class="p-label">PM2.5</div>
                <div class="p-val">{{ formatNum(item.pm25) }}</div>
              </div>
              <div class="p-item">
                <div class="p-label">PM10</div>
                <div class="p-val">{{ formatNum(item.pm10) }}</div>
              </div>
              <div class="p-item">
                <div class="p-label">O3</div>
                <div class="p-val">{{ formatNum(item.o3) }}</div>
              </div>
              <div class="p-item">
                <div class="p-label">气温</div>
                <div class="p-val">{{ formatTemp(item.temperature) }}</div>
              </div>
              <div class="p-item">
                <div class="p-label">天气</div>
                <div class="p-val nowrap-text">{{ item.weather || '-' }}</div>
              </div>
            </div>
            <div class="aqi-date"><el-icon style="margin-right:2px;vertical-align:middle"><Calendar /></el-icon>{{ item.date }}</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-row :gutter="20">
      <el-col :span="12">
        <div class="feature-card chart-card" @click="$router.push('/charts')">
          <div class="fc-icon"><el-icon :size="36"><TrendCharts /></el-icon></div>
          <div class="fc-info">
            <div class="fc-title">数据可视化</div>
            <div class="fc-desc">查看空气质量趋势、分布与城市对比图表</div>
          </div>
          <div class="fc-arrow"><el-icon><ArrowRight /></el-icon></div>
        </div>
      </el-col>
      <el-col :span="12">
        <div class="feature-card article-card" @click="$router.push('/articles')">
          <div class="fc-icon"><el-icon :size="36"><Reading /></el-icon></div>
          <div class="fc-info">
            <div class="fc-title">科普资讯</div>
            <div class="fc-desc">了解空气质量知识，学会科学防护</div>
          </div>
          <div class="fc-arrow"><el-icon><ArrowRight /></el-icon></div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { airDataApi, favoriteApi, cityApi, alertApi } from '@/api'
import { useUserStore } from '@/store/user'

const userStore = useUserStore()
const loading = ref(true)
const favoriteData = ref([])
const hasFavorites = ref(false)
const cityMap = ref({})
const alertVisible = ref(false)
const alertData = ref({})
const alertCityName = ref('')

const statsCards = computed(() => [
  { label: '收藏城市', value: favoriteCitiesCount.value, color: '#667eea', icon: 'Star' },
  { label: '优/良天数', value: favoriteData.value.filter(d => d.aqi != null && d.aqi <= 100).length, color: '#11998e', icon: 'Sunny' },
  { label: '预警城市', value: favoriteData.value.filter(d => d.aqi != null && d.aqi >= 150).length, color: '#f5576c', icon: 'WarningFilled' },
  { label: '监测指标', value: '6项', color: '#4facfe', icon: 'DataAnalysis' },
])

const favoriteCitiesCount = computed(() => favoriteIds.value.length)

const favoriteIds = ref([])
const dates = ref([])
const selectedDate = ref('')

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
function getAqiLevelName(aqi) {
  if (aqi == null) return '未知'
  for (const [min, max, name] of aqiLevelMap) { if (aqi >= min && aqi <= max) return name }
  return '未知'
}
function getCityName(cityId) { return cityMap.value[cityId] || `城市${cityId}` }
function formatNum(v) { return v != null ? v : '-' }
function formatTemp(v) { return v != null ? v + '℃' : '-' }

onMounted(async () => {
  try {
    const cityRes = await cityApi.listAll()
    const cities = cityRes.data || []
    cities.forEach(c => { cityMap.value[c.id] = c.name })
    const userId = userStore.userId

    const countRes = await favoriteApi.count(userId)
    const count = countRes.data
    hasFavorites.value = count > 0

    const idsRes = await favoriteApi.getCityIds(userId)
    favoriteIds.value = idsRes.data || []

    if (count > 0) {
      const datesRes = await airDataApi.getFavoriteDates(userId)
      dates.value = (datesRes.data || []).map(d => typeof d === 'string' ? d : d)
      if (dates.value.length) {
        selectedDate.value = dates.value[0]
      }
    }

    let airData = []
    if (count > 0) {
      if (selectedDate.value) {
        const favRes = await airDataApi.getFavoritesByDate(userId, selectedDate.value)
        airData = favRes.data || []
      } else {
        const favRes = await airDataApi.getLatestByFavorites(userId)
        airData = favRes.data || []
      }
    }
    favoriteData.value = airData

    const alertRes = await alertApi.getEnabled()
    const thresholds = alertRes.data || []
    for (const item of airData) {
      for (const t of thresholds) {
        if (item.aqi >= t.aqiThreshold) {
          alertData.value = item
          alertCityName.value = getCityName(item.cityId)
          alertVisible.value = true
          break
        }
      }
      if (alertVisible.value) break
    }
  } finally {
    loading.value = false
  }
})

async function loadByDate() {
  if (!selectedDate.value) return
  loading.value = true
  try {
    const favRes = await airDataApi.getFavoritesByDate(userStore.userId, selectedDate.value)
    favoriteData.value = favRes.data || []
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.dashboard { }

.alert-wrap { margin-bottom: 20px; }
.alert-animate { animation: alertPulse 2s ease-in-out infinite; }
@keyframes alertPulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.85; }
}

.stats-row { margin-bottom: 24px; }

.stat-card-dash {
  border-radius: 14px;
  padding: 20px 18px;
  position: relative;
  overflow: hidden;
  cursor: default;
  transition: transform 0.25s, box-shadow 0.25s;
}
.stat-card-dash:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 28px rgba(0,0,0,0.1);
}
.stat-grad-0 { background: linear-gradient(135deg, #667eea, #764ba2); }
.stat-grad-1 { background: linear-gradient(135deg, #11998e, #38ef7d); }
.stat-grad-2 { background: linear-gradient(135deg, #f093fb, #f5576c); }
.stat-grad-3 { background: linear-gradient(135deg, #4facfe, #00f2fe); }

.stat-pattern {
  position: absolute;
  width: 90px; height: 90px;
  border-radius: 50%;
  background: rgba(255,255,255,0.08);
  top: -20px; right: -25px;
}
.stat-pattern::after {
  content: '';
  position: absolute;
  width: 55px; height: 55px;
  border-radius: 50%;
  background: rgba(255,255,255,0.06);
  top: 50px; left: 20px;
}

.stat-inner {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}
.stat-num { font-size: 30px; font-weight: 700; color: #fff; line-height: 1; }
.stat-desc { font-size: 12px; color: rgba(255,255,255,0.75); margin-top: 6px; }
.stat-icon-wrap { color: rgba(255,255,255,0.4); }

.section-card {
  border-radius: 14px;
  overflow: hidden;
  margin-bottom: 20px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.section-header { display: flex; justify-content: space-between; align-items: center; }
.header-right-controls { display: flex; align-items: center; gap: 10px; }
.section-title { font-size: 16px; font-weight: 600; color: #1a1a2e; display: flex; align-items: center; gap: 8px; }
.title-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: inline-block;
}

.loading-wrap { padding: 24px; }
.empty-wrap { padding: 20px 0; }

.aqi-card-new {
  background: #fff;
  border-radius: 14px;
  border-top: 4px solid #ccc;
  padding: 20px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  margin-bottom: 16px;
}
.aqi-card-new:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 32px rgba(0,0,0,0.08);
}

.aqi-top { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.aqi-city { font-weight: 600; font-size: 15px; display: flex; align-items: center; gap: 6px; }
.city-icon { font-size: 16px; }
.aqi-badge-sm {
  display: inline-flex; align-items: center; justify-content: center;
  height: 24px; padding: 0 10px; border-radius: 12px;
  font-size: 11px; font-weight: 600; color: #fff;
}

.aqi-big { font-size: 48px; font-weight: 800; text-align: center; line-height: 1.1; letter-spacing: -1px; }
.aqi-unit { text-align: center; font-size: 11px; color: #aaa; margin-bottom: 14px; }

.pollutants {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 4px;
  padding: 10px 0;
  border-top: 1px solid #f0f0f0;
}
.p-item { text-align: center; min-width: 0; }
.p-label { font-size: 11px; color: #bbb; margin-bottom: 4px; white-space: nowrap; }
.p-val { font-size: 14px; font-weight: 700; color: #333; }
.nowrap-text { white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }

.aqi-date { text-align: center; font-size: 11px; color: #bbb; margin-top: 4px; }

.feature-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 24px;
  border-radius: 14px;
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  position: relative;
  overflow: hidden;
}
.feature-card:hover { transform: translateY(-3px); box-shadow: 0 8px 28px rgba(0,0,0,0.08); }
.chart-card { background: linear-gradient(135deg, #f0f5ff, #e8eeff); }
.article-card { background: linear-gradient(135deg, #f0fdf4, #e6f9ee); }
.fc-icon { font-size: 36px; }
.fc-info { flex: 1; }
.fc-title { font-size: 16px; font-weight: 700; color: #1a1a2e; margin-bottom: 4px; }
.fc-desc { font-size: 12px; color: #888; }
.fc-arrow { font-size: 24px; color: #ccc; transition: color 0.2s, transform 0.2s; }
.feature-card:hover .fc-arrow { color: #666; transform: translateX(3px); }
</style>
