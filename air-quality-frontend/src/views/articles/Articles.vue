<template>
  <div class="articles-page">
    <div class="page-header animate__animated animate__fadeInDown">
      <h2 class="page-title"><el-icon style="margin-right:6px"><Reading /></el-icon>科普资讯</h2>
      <p class="page-desc">了解空气质量知识，学会科学防护</p>
    </div>
    <div class="articles-grid">
      <div v-for="(a, idx) in articles" :key="a.id" class="article-card animate__animated animate__fadeInUp" :style="{ animationDelay: `${idx * 0.08}s` }" @click="$router.push(`/articles/${a.id}`)">
        <div class="article-inner">
          <div class="article-icon"><el-icon :size="28"><Document /></el-icon></div>
          <div class="article-body">
            <h3 class="article-title">{{ a.title }}</h3>
            <p class="summary">{{ a.summary }}</p>
            <div class="meta">
              <span><el-icon style="margin-right:2px"><Edit /></el-icon>{{ a.author }}</span>
              <span><el-icon style="margin-right:2px"><Calendar /></el-icon>{{ a.publishTime?.slice(0,10) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    <el-empty v-if="!articles.length" description="暂无科普资讯" :image-size="120" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { articleApi } from '@/api'

const articles = ref([])

onMounted(async () => {
  const res = await articleApi.listPublished()
  articles.value = res.data || []
})
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-title { font-size: 20px; font-weight: 700; color: #1a1a2e; margin-bottom: 4px; display: flex; align-items: center; }
.page-desc { font-size: 13px; color: #999; }

.articles-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(360px, 1fr)); gap: 14px; }

.article-card {
  background: #fff;
  border-radius: 16px;
  padding: 22px 24px;
  cursor: pointer;
  transition: transform 0.3s cubic-bezier(0.34, 1.56, 0.64, 1), box-shadow 0.3s;
  box-shadow: 0 2px 10px rgba(0,0,0,0.04);
  border-left: 4px solid #3b82f6;
  position: relative;
  overflow: hidden;
}
.article-card::after {
  content: '';
  position: absolute;
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(59,130,246,0.04), transparent 70%);
  top: -30px;
  right: -30px;
}
.article-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 12px 36px rgba(0,0,0,0.1);
}
.article-inner { display: flex; gap: 16px; align-items: flex-start; }
.article-icon { font-size: 28px; flex-shrink: 0; color: #3b82f6; }
.article-body { flex: 1; min-width: 0; }
.article-title { font-size: 15px; font-weight: 600; margin-bottom: 6px; color: #1a1a2e; line-height: 1.4; }
.summary { color: #777; font-size: 13px; margin-bottom: 10px; line-height: 1.6; display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.meta { display: flex; gap: 20px; font-size: 11px; color: #bbb; }
.meta span { display: flex; align-items: center; }
</style>
