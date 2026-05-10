<template>
  <div class="charts-page">
    <el-card class="filter-card" shadow="never">
      <el-form :inline="true">
        <el-form-item label="城市">
          <el-select v-model="cityId" placeholder="选择城市" size="large" style="min-width:160px" @change="loadCharts">
            <el-option v-for="c in cities" :key="c.id" :label="c.name" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-select v-model="range" size="large" style="min-width:120px" @change="loadCharts">
            <el-option label="近7天" value="7" />
            <el-option label="近30天" value="30" />
            <el-option label="近90天" value="90" />
          </el-select>
        </el-form-item>
      </el-form>
    </el-card>

    <el-row :gutter="16">
      <el-col :span="24">
        <el-card class="chart-card" shadow="never">
          <template #header><span class="chart-title">📈 空气质量趋势</span></template>
          <div ref="trendChart" style="height:350px"></div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top:16px">
      <el-col :span="12">
        <el-card class="chart-card" shadow="never">
          <template #header><span class="chart-title">🎯 AQI 等级分布</span></template>
          <div ref="pieChart" style="height:350px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" shadow="never">
          <template #header><span class="chart-title">🏙️ 多城市对比</span></template>
          <div ref="compareChart" style="height:350px"></div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import * as echarts from 'echarts'
import { airDataApi, cityApi } from '@/api'

const cities = ref([])
const cityId = ref(1)
const range = ref('30')
const trendChart = ref()
const pieChart = ref()
const compareChart = ref()
let trendIns, pieIns, compareIns

function getDateRange() {
  const end = new Date()
  const start = new Date()
  start.setDate(start.getDate() - parseInt(range.value))
  return { startDate: start.toISOString().slice(0,10), endDate: end.toISOString().slice(0,10) }
}

async function loadCharts() {
  const { startDate, endDate } = getDateRange()
  const res = await airDataApi.getTrend({ cityId: cityId.value, startDate, endDate })
  const list = res.data || []

  if (!trendIns) trendIns = echarts.init(trendChart.value)
  trendIns.setOption({
    tooltip: { trigger: 'axis' },
    legend: { data: ['AQI', 'PM2.5', 'PM10', 'O₃'] },
    xAxis: { type: 'category', data: list.map(d => d.date) },
    yAxis: { type: 'value' },
    series: [
      { name: 'AQI', type: 'line', data: list.map(d => d.aqi), smooth: true, lineStyle: { width: 3, color: '#E6A23C' }, itemStyle: { color: '#E6A23C' } },
      { name: 'PM2.5', type: 'line', data: list.map(d => d.pm25), smooth: true },
      { name: 'PM10', type: 'line', data: list.map(d => d.pm10), smooth: true },
      { name: 'O₃', type: 'line', data: list.map(d => d.o3), smooth: true },
    ],
    grid: { left: 50, right: 20, top: 40, bottom: 30 },
  })

  if (!pieIns) pieIns = echarts.init(pieChart.value)
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
    series: [{
      type: 'pie', radius: ['40%', '70%'],
      data: Object.entries(aqiDistribution).map(([name, value]) => ({ name, value })),
      label: { formatter: '{b}: {c}天' },
      itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
    }],
  })

  if (!compareIns) compareIns = echarts.init(compareChart.value)
  const cmpCities = cities.value.slice(0, 5)
  const cmpData = await Promise.all(cmpCities.map(async c => {
    const r = await airDataApi.getLatestByCity(c.id)
    return r.data ? { ...r.data, cityName: c.name } : null
  }))
  const validCmp = cmpData.filter(Boolean)
  compareIns.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: validCmp.map(d => d.cityName) },
    yAxis: { type: 'value' },
    series: [
      { name: 'AQI', type: 'bar', data: validCmp.map(d => d.aqi), itemStyle: { borderRadius: [6,6,0,0], color: '#667eea' } },
      { name: 'PM2.5', type: 'bar', data: validCmp.map(d => d.pm25), itemStyle: { borderRadius: [6,6,0,0], color: '#f093fb' } },
    ],
    grid: { left: 50, right: 20, top: 40, bottom: 30 },
  })
}

onMounted(async () => {
  const res = await cityApi.listAll()
  cities.value = res.data || []
  await nextTick()
  loadCharts()
})
</script>

<style scoped>
.charts-page { }
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
  margin-bottom: 16px;
}
.chart-title { font-weight: 600; font-size: 14px; }
</style>
