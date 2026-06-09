<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">思政课程</h1>
      <p class="page-desc">按思政学段浏览配套课程素材，支持图文阅览与微课视频学习</p>
    </header>

    <div class="level-tabs">
      <button
        v-for="l in levels"
        :key="l.value"
        class="level-tab"
        :class="{ active: activeLevel === l.value }"
        @click="switchLevel(l.value)"
      >
        <span class="level-icon">{{ l.icon }}</span>
        <span class="level-name">{{ l.label }}</span>
      </button>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="card result-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Loading /></el-icon>
        <p>加载中...</p>
      </div>
    </div>

    <!-- 空结果 -->
    <div v-else-if="!loading && materials.length === 0" class="card result-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Reading /></el-icon>
        <p>该学段暂无配套思政课程素材</p>
      </div>
    </div>

    <!-- 结果列表 -->
    <template v-else>
      <div class="material-list">
        <div
          v-for="item in materials"
          :key="item.id"
          class="card material-card"
          @click="$router.push(`/material/${item.id}`)"
        >
          <div class="card-cover">
            <img v-if="item.coverImage" :src="item.coverImage" :alt="item.title" referrerpolicy="no-referrer" />
            <el-icon v-else :size="48" color="var(--ink-wash)"><Document /></el-icon>
          </div>
          <div class="card-body">
            <h3 class="card-title">{{ item.title }}</h3>
            <p class="card-author" v-if="item.author">{{ item.author }}</p>
            <p class="card-summary" v-if="item.summary">{{ item.summary }}</p>
            <div class="card-meta">
              <span v-if="item.dynasty">{{ item.dynasty }}</span>
              <span v-if="item.category">{{ item.category }}</span>
              <span v-if="item.educationLevel">{{ item.educationLevel }}</span>
            </div>
            <div class="card-tags" v-if="item.tags && item.tags.length > 0">
              <el-tag v-for="tag in item.tags" :key="tag" size="small" type="info">{{ tag }}</el-tag>
            </div>
            <div class="card-footer">
              <span class="card-stats">
                <el-icon><View /></el-icon> {{ item.viewCount }}
                <el-icon style="margin-left:12px;"><Star /></el-icon> {{ item.favoriteCount }}
              </span>
              <span class="card-date">{{ formatDate(item.createdAt) }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="pagination-wrap" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchData"
        />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Reading, Document, Loading, View, Star } from '@element-plus/icons-vue'
import { getMaterials } from '../api/materials'

interface MaterialItem {
  id: number
  title: string
  author: string
  dynasty: string
  category: string
  educationLevel: string
  tags: string[]
  coverImage: string
  summary: string
  viewCount: number
  favoriteCount: number
  createdAt: string
}

const levels = [
  { value: 'primary_low', label: '小学 1-3年级', icon: '📖' },
  { value: 'primary_high', label: '小学 4-6年级', icon: '📚' },
  { value: 'junior', label: '初中', icon: '📝' },
  { value: 'high', label: '高中', icon: '📜' },
  { value: 'college', label: '大学', icon: '🎓' }
]

const activeLevel = ref('primary_low')
const materials = ref<MaterialItem[]>([])
const loading = ref(false)
const currentPage = ref(1)
const total = ref(0)
const pageSize = 12

onMounted(() => {
  fetchData()
})

function switchLevel(level: string) {
  if (activeLevel.value === level) return
  activeLevel.value = level
  currentPage.value = 1
  fetchData()
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getMaterials({
      page: currentPage.value,
      size: pageSize,
      educationLevel: activeLevel.value
    })
    const data = res.data || res
    materials.value = data.records || []
    total.value = data.total || 0
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

function formatDate(dateStr: string): string {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}
</script>

<style scoped>
.page { max-width: 960px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.page-desc { margin-top: 6px; color: var(--ink-gray); font-size: 13px; }

.level-tabs { display: flex; gap: 12px; margin-bottom: 24px; flex-wrap: wrap; }
.level-tab {
  display: flex; align-items: center; gap: 8px;
  padding: 12px 20px; background: var(--paper-white); border: 1px solid var(--ink-wash);
  border-radius: 6px; cursor: pointer; transition: all 0.3s; font-family: inherit;
}
.level-tab:hover { border-color: var(--accent-seal); }
.level-tab.active { border-color: var(--accent-seal); background: rgba(194, 59, 34, 0.04); }
.level-name { font-size: 13px; color: var(--ink-dark); }

.result-card { padding: 20px; min-height: 300px; }
.empty-hint {
  display: flex; flex-direction: column; align-items: center; gap: 12px;
  padding: 60px 0; color: var(--ink-light); font-size: 14px;
}

.material-list { display: flex; flex-direction: column; gap: 16px; }

.material-card {
  display: flex;
  padding: 0;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.material-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,.08); }

.card-cover {
  width: 160px;
  min-height: 160px;
  flex-shrink: 0;
  background: var(--el-fill-color-light);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-body {
  flex: 1;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--ink-black);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-author { font-size: 13px; color: var(--ink-gray); margin: 0; }
.card-summary {
  font-size: 13px;
  color: var(--ink-light);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex; gap: 16px;
  font-size: 12px; color: var(--ink-gray);
}
.card-meta span::before { content: '#'; margin-right: 2px; }

.card-tags { display: flex; flex-wrap: wrap; gap: 6px; }

.card-footer {
  display: flex; align-items: center; justify-content: space-between;
  margin-top: auto;
  font-size: 12px; color: var(--ink-light);
}
.card-stats { display: flex; align-items: center; gap: 2px; }
.card-date { color: var(--ink-light); }

.pagination-wrap {
  display: flex; justify-content: center;
  margin-top: 24px;
}
</style>
