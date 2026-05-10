<template>
  <div class="favorites-page">
    <el-card class="add-card" shadow="never">
      <template #header>
        <span class="card-title"><span class="title-dot"></span>添加收藏城市</span>
      </template>
      <div class="add-row">
        <el-select v-model="selectedCityId" placeholder="搜索并选择城市" clearable size="large" class="city-picker">
          <el-option v-for="c in allCities" :key="c.id" :label="`🏙 ${c.name} · ${c.province || ''}`" :value="c.id"
            :disabled="favCityIds.includes(c.id)" />
        </el-select>
        <el-button type="primary" size="large" round @click="addFavorite" :disabled="!selectedCityId">
          <el-icon><Plus /></el-icon> 添加收藏
        </el-button>
      </div>
    </el-card>

    <el-card class="table-card" shadow="never" v-loading="loading">
      <template #header>
        <span class="card-title"><span class="title-dot"></span>我的收藏列表</span>
        <el-tag v-if="favCities.length" size="small" round type="success" style="margin-left:8px">{{ favCities.length }} 个城市</el-tag>
      </template>
      <el-table :data="favCities" border stripe>
        <el-table-column label="城市" min-width="150">
          <template #default="{ row }">
            <span class="city-name-cell">🏙 {{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="province" label="省份" width="120" />
        <el-table-column prop="category" label="分类" width="140" />
        <el-table-column label="操作" width="120">
          <template #default="{ row }">
            <el-button type="danger" round size="small" @click="removeFavorite(row.id)">取消收藏</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="!favCities.length && !loading" description="还没有收藏城市" :image-size="100" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { cityApi, favoriteApi } from '@/api'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const userStore = useUserStore()
const allCities = ref([])
const favCities = ref([])
const favCityIds = ref([])
const selectedCityId = ref(null)
const loading = ref(false)

async function loadData() {
  loading.value = true
  try {
    const allRes = await cityApi.listAll()
    allCities.value = allRes.data || []
    const idsRes = await favoriteApi.getCityIds(userStore.userId)
    favCityIds.value = idsRes.data || []
    favCities.value = allCities.value.filter(c => favCityIds.value.includes(c.id))
  } finally { loading.value = false }
}

async function addFavorite() {
  await favoriteApi.add(userStore.userId, selectedCityId.value)
  ElMessage.success('收藏成功')
  selectedCityId.value = null
  loadData()
}

async function removeFavorite(cityId) {
  await favoriteApi.remove(userStore.userId, cityId)
  ElMessage.success('已取消收藏')
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
.favorites-page { }
.add-card {
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  margin-bottom: 16px;
}
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
.add-row { display: flex; align-items: center; gap: 12px; }
.city-picker { width: 360px; }
.city-name-cell { font-weight: 500; }
</style>
