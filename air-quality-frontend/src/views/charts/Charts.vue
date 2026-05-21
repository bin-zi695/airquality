<template>
  <div class="charts-page">
    <el-card class="filter-card animate__animated animate__fadeInUp" shadow="never">
      <el-form :inline="true">
        <el-form-item label="省份">
          <el-select v-model="filterProvince" placeholder="全部省份" clearable size="large" style="min-width:130px">
            <el-option v-for="p in provinces" :key="p" :label="p" :value="p" />
          </el-select>
        </el-form-item>
        <el-form-item label="城市">
          <el-select v-model="cityId" placeholder="选择城市" filterable size="large" style="min-width:160px" @change="loadTrendAndPie">
            <el-option v-for="c in filteredCities" :key="c.id" :label="`${c.name} · ${c.province || ''}`" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-select v-model="range" size="large" style="min-width:120px" @change="loadTrendAndPie">
            <el-option label="近7天" value="7" />
            <el-option label="近15天" value="15" />
          </el-select>
        </el-form-item>
        <el-form-item label="对比日期">
          <el-select v-model="compareDate" placeholder="选择日期" size="large" style="min-width:160px" @change="loadCompare">
            <el-option v-for="d in allDates" :key="d" :label="d" :value="d" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="24">
        <el-card class="chart-card animate__animated animate__fadeInUp" shadow="never" style="animation-delay:0.1s">
          <template #header><span class="chart-title"><el-icon style="margin-right:4px"><TrendCharts /></el-icon>空气质量趋势</span></template>
          <div ref="trendChart" style="height:350px"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12">
        <el-card class="chart-card animate__animated animate__fadeInUp" shadow="never" style="animation-delay:0.2s">
          <template #header><span class="chart-title"><el-icon style="margin-right:4px"><Aim /></el-icon>AQI 等级分布</span></template>
          <div ref="pieChart" style="height:300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card animate__animated animate__fadeInUp" shadow="never" style="animation-delay:0.3s">
          <template #header><span class="chart-title"><el-icon style="margin-right:4px"><Aim /></el-icon>污染等级占比</span></template>
          <div ref="pieChart2" style="height:300px"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="24">
        <el-card class="chart-card animate__animated animate__fadeInUp" shadow="never" style="animation-delay:0.4s">
          <template #header>
            <div class="chart-header-row">
              <span class="chart-title"><el-icon style="margin-right:4px"><OfficeBuilding /></el-icon>多城市对比</span>
              <span class="chart-date-hint" v-if="compareDate">{{ compareDate }}</span>
            </div>
          </template>
          <div ref="compareChart" style="height:500px"></div>
          <div v-if="!compareDate" class="chart-empty-hint">请选择对比日期</div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, nextTick, onBeforeUnmount } from 'vue'
import * as echarts from 'echarts'
import { airDataApi, cityApi } from '@/api'

const cities = ref([])
const cityId = ref(1)
const filterProvince = ref('')

const provinces = computed(() => {
  const set = new Set(cities.value.map(c => c.province).filter(Boolean))
  return [...set].sort()
})
const filteredCities = computed(() => {
  if (!filterProvince.value) return cities.value
  return cities.value.filter(c => c.province === filterProvince.value)
})
const range = ref('7')
const compareDate = ref('')
const allDates = ref([])
const cityMap = ref({})
const trendChart = ref()
const pieChart = ref()
const pieChart2 = ref()
const compareChart = ref()
let trendIns = null
let pieIns = null
let pieIns2 = null
let compareIns = null

function getDateRange() {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - parseInt(range.value))
  return { startDate: start.toISOString().slice(0,10), endDate: end.toISOString().slice(0,10) }
}

function initChart(refEl) {
  if (!refEl) return null
  try {
    const instance = echarts.init(refEl)
    return instance
  } catch (e) {
    console.warn('Chart init failed:', e)
    return null
  }
}

function ensureInstance(refEl, instanceRef) {
  if (instanceRef) return instanceRef
  return initChart(refEl)
}

function getCityName(cityId) { return cityMap.value[cityId] || `城市${cityId}` }

async function loadTrendAndPie() {
  const { startDate, endDate } = getDateRange()
  const res = await airDataApi.getTrend({ cityId: cityId.value, startDate, endDate })
  const list = res.data || []

  // Ensure chart instances exist
  trendIns = ensureInstance(trendChart.value, trendIns)
  pieIns = ensureInstance(pieChart.value, pieIns)

  if (trendIns) {
    trendIns.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['AQI', 'PM2.5', 'PM10', 'O₃'] },
      xAxis: { type: 'category', data: list.map(d => d.date) },
      yAxis: { type: 'value', name: '浓度' },
      series: [
        { name: 'AQI', type: 'line', data: list.map(d => d.aqi), smooth: true, lineStyle: { width: 3, color: '#E6A23C' }, itemStyle: { color: '#E6A23C' } },
        { name: 'PM2.5', type: 'line', data: list.map(d => d.pm25), smooth: true },
        { name: 'PM10', type: 'line', data: list.map(d => d.pm10), smooth: true },
        { name: 'O₃', type: 'line', data: list.map(d => d.o3), smooth: true },
      ],
      grid: { left: 50, right: 20, top: 40, bottom: 30 },
    })
    trendIns.resize()
  }

  if (pieIns) {
    const aqiDistribution = { '优(0-50)': 0, '良(51-100)': 0, '轻度(101-150)': 0, '中度(151-200)': 0, '重度(201-300)': 0, '严重(>300)': 0 }
    list.forEach(d => {
      const a = d.aqi || 0
      if (a <= 50) aqiDistribution['优(0-50)']++
      else if (a <= 100) aqiDistribution['良(51-100)']++
      else if (a <= 150) aqiDistribution['轻度(101-150)']++
      else if (a <= 200) aqiDistribution['中度(151-200)']++
      else if (a <= 300) aqiDistribution['重度(201-300)']++
      else aqiDistribution['严重(>300)']++
    })
    pieIns.setOption({
      tooltip: { trigger: 'item' },
      graphic: [{
        type: 'text', left: 'center', top: 'center',
        style: { text: `${list.length}天`, fontSize: 20, fontWeight: 'bold', fill: '#1a1a2e' },
      }],
      series: [{
        type: 'pie', radius: ['40%', '70%'],
        data: Object.entries(aqiDistribution).map(([name, value]) => ({ name, value })),
        label: { formatter: '{b}: {c}天' },
        itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
      }],
    })
    pieIns.resize()
  }
}

async function loadCompare() {
  if (!compareDate.value) return
  if (!compareChart.value) return

  compareIns = ensureInstance(compareChart.value, compareIns)
  if (!compareIns) return

  const res = await airDataApi.getAllByDate(compareDate.value)
  const list = res.data || []
  const valid = list.filter(d => d.aqi != null)

  if (!valid.length) {
    compareIns.setOption({
      title: { text: '该日期暂无数据', left: 'center', top: 'center', textStyle: { color: '#999', fontSize: 14 } },
    })
    compareIns.resize()
    return
  }

  compareIns.setOption({
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { data: ['AQI', 'PM2.5'] },
    yAxis: { type: 'category', data: valid.map(d => getCityName(d.cityId)).reverse(), axisLabel: { fontSize: 11 } },
    xAxis: { type: 'value' },
    series: [
      { name: 'AQI', type: 'bar', data: valid.map(d => d.aqi).reverse(), itemStyle: { borderRadius: [0,6,6,0], color: '#3b82f6' } },
      { name: 'PM2.5', type: 'bar', data: valid.map(d => d.pm25).reverse(), itemStyle: { borderRadius: [0,6,6,0], color: '#f093fb' } },
    ],
    grid: { left: 100, right: 30, top: 40, bottom: 30 },
  })
  compareIns.resize()

  // 污染等级占比 (基于对比数据)
  pieIns2 = ensureInstance(pieChart2.value, pieIns2)
  if (pieIns2) {
    const dist = { '优(0-50)': 0, '良(51-100)': 0, '轻度(101-150)': 0, '中度(151-200)': 0, '重度(201-300)': 0, '严重(>300)': 0 }
    valid.forEach(d => {
      const a = d.aqi || 0
      if (a <= 50) dist['优(0-50)']++
      else if (a <= 100) dist['良(51-100)']++
      else if (a <= 150) dist['轻度(101-150)']++
      else if (a <= 200) dist['中度(151-200)']++
      else if (a <= 300) dist['重度(201-300)']++
      else dist['严重(>300)']++
    })
    pieIns2.setOption({
      tooltip: { trigger: 'item' },
      graphic: [{
        type: 'text', left: 'center', top: 'center',
        style: { text: `${valid.length}城`, fontSize: 18, fontWeight: 'bold', fill: '#1a1a2e' },
      }],
      series: [{
        type: 'pie', radius: ['40%', '70%'],
        data: Object.entries(dist).map(([name, value]) => ({ name, value })),
        label: { formatter: '{b}: {c}城' },
        itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
      }],
    })
    pieIns2.resize()
  }
}

function handleResize() {
  if (trendIns) trendIns.resize()
  if (pieIns) pieIns.resize()
  if (pieIns2) pieIns2.resize()
  if (compareIns) compareIns.resize()
}

onMounted(async () => {
  const cityRes = await cityApi.listAll()
  cities.value = cityRes.data || []
  cities.value.forEach(c => { cityMap.value[c.id] = c.name })

  const datesRes = await airDataApi.getAllDates()
  allDates.value = (datesRes.data || []).map(d => typeof d === 'string' ? d : d)
  if (allDates.value.length) {
    compareDate.value = allDates.value[0]
  }

  await nextTick()
  // Initialize chart instances
  if (trendChart.value) trendIns = initChart(trendChart.value)
  if (pieChart.value) pieIns = initChart(pieChart.value)
  if (pieChart2.value) pieIns2 = initChart(pieChart2.value)
  if (compareChart.value) compareIns = initChart(compareChart.value)

  await loadTrendAndPie()
  await loadCompare()

  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (trendIns) { trendIns.dispose(); trendIns = null }
  if (pieIns) { pieIns.dispose(); pieIns = null }
  if (pieIns2) { pieIns2.dispose(); pieIns2 = null }
  if (compareIns) { compareIns.dispose(); compareIns = null }
})
</script>

<style scoped>
.filter-card {
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  margin-bottom: 16px;
}
.chart-card {
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  transition: box-shadow 0.3s;
}
.chart-card:hover {
  box-shadow: 0 4px 20px rgba(0,0,0,0.06);
}
.chart-title { font-weight: 600; font-size: 14px; display: flex; align-items: center; }
.chart-header-row { display: flex; align-items: center; gap: 10px; }
.chart-date-hint { font-size: 12px; color: #999; }
.chart-empty-hint { text-align: center; padding: 80px 0; color: #bbb; font-size: 13px; }
</style>
