<template>
  <div class="article-detail">
    <el-page-header @back="$router.push('/articles')" class="animate__animated animate__fadeInDown">
      <template #content>
        <span class="page-header-title"><el-icon style="margin-right:4px"><Reading /></el-icon>科普资讯</span>
      </template>
    </el-page-header>

    <el-card class="detail-card animate__animated animate__fadeInUp" shadow="never" v-loading="loading">
      <template v-if="article">
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span><el-icon style="margin-right:2px"><Edit /></el-icon>{{ article.author }}</span>
          <span><el-icon style="margin-right:2px"><Calendar /></el-icon>{{ article.publishTime?.slice(0, 10) }}</span>
        </div>
        <el-divider />
        <div class="article-content" v-html="renderedContent"></div>
      </template>
      <el-empty v-else-if="!loading" description="资讯不存在" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { articleApi } from '@/api'

const route = useRoute()
const article = ref(null)
const loading = ref(true)

function renderMD(text) {
  let html = text
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')

  const tables = []
  html = html.replace(/(^\|.+\|\n)+/gm, (match) => {
    tables.push(match)
    return `%%TABLE_${tables.length - 1}%%`
  })

  html = html
    .replace(/^#### (.+)$/gm, '<h5>$1</h5>')
    .replace(/^### (.+)$/gm, '<h4>$1</h4>')
    .replace(/^## (.+)$/gm, '<h3>$1</h3>')
    .replace(/^# (.+)$/gm, '<h2>$1</h2>')

  html = html.replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')

  html = html.replace(/^- (.+)$/gm, '<li>$1</li>')
  html = html.replace(/(<li>[\s\S]*?<\/li>)/g, '<ul>$1</ul>')

  const blocks = html.split('\n\n')
  html = blocks.map(b => {
    const trimmed = b.trim()
    if (!trimmed) return ''
    if (trimmed.startsWith('<h') || trimmed.startsWith('<ul') || trimmed.startsWith('<table') || /^%%TABLE/.test(trimmed)) {
      return trimmed
    }
    return '<p>' + trimmed.replace(/\n/g, '<br>') + '</p>'
  }).join('\n')

  tables.forEach((t, i) => {
    let tableHtml = '<table>'
    const lines = t.trim().split('\n')
    let headerDone = false
    lines.forEach((line, idx) => {
      if (line.includes('---')) { headerDone = true; return }
      const cells = line.split('|').filter(c => c.trim())
      const tag = headerDone ? 'td' : 'th'
      const row = '<tr>' + cells.map(c => `<${tag}>${c.trim()}</${tag}>`).join('') + '</tr>'
      tableHtml += row
      if (idx === 0 && lines.length > 2 && lines[1] && lines[1].includes('---')) {
        headerDone = true
      } else if (idx > 0) {
        headerDone = true
      }
    })
    tableHtml += '</table>'
    html = html.replace(`%%TABLE_${i}%%`, tableHtml)
  })

  return html
}

const renderedContent = computed(() => {
  if (!article.value?.content) return ''
  return renderMD(article.value.content)
})

onMounted(async () => {
  try {
    const res = await articleApi.getById(route.params.id)
    article.value = res.data
  } finally {
    loading.value = false
  }
})
</script>

<style scoped>
.page-header-title { font-size: 16px; font-weight: 600; }
.detail-card {
  margin-top: 20px;
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}
.article-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12px;
  line-height: 1.3;
}
.article-meta {
  display: flex;
  gap: 24px;
  font-size: 12px;
  color: #bbb;
}
.article-meta span { display: flex; align-items: center; }
.article-content {
  font-size: 14px;
  color: #444;
  max-width: 780px;
}
.article-content :deep(h2) {
  font-size: 19px;
  margin: 22px 0 10px;
  padding-bottom: 6px;
  border-bottom: 1px solid #eee;
  color: #1a1a2e;
}
.article-content :deep(h3) {
  font-size: 16px;
  margin: 18px 0 8px;
  color: #333;
}
.article-content :deep(h4) {
  font-size: 14px;
  margin: 14px 0 6px;
  color: #555;
}
.article-content :deep(h5) {
  font-size: 13px;
  margin: 10px 0 4px;
  color: #666;
}
.article-content :deep(strong) {
  color: #1a1a2e;
  font-weight: 600;
}
.article-content :deep(ul) {
  margin: 8px 0;
  padding-left: 22px;
}
.article-content :deep(li) {
  margin: 2px 0;
  line-height: 1.6;
}
.article-content :deep(table) {
  width: 100%;
  border-collapse: collapse;
  margin: 12px 0;
  font-size: 13px;
}
.article-content :deep(th),
.article-content :deep(td) {
  border: 1px solid #e0e0e0;
  padding: 7px 10px;
  text-align: left;
}
.article-content :deep(th) {
  background: #f5f7fa;
  font-weight: 600;
  color: #333;
}
.article-content :deep(td) {
  color: #555;
}
.article-content :deep(p) {
  margin: 8px 0;
  line-height: 1.7;
}
</style>
