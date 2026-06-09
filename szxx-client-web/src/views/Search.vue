<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">关键词检索</h1>
      <p class="page-desc">使用中文分词智能检索传统文化素材，支持标题、标签、正文全文搜索</p>
    </header>

    <div class="card search-bar-card">
      <div class="search-row">
        <el-input
          v-model="keyword"
          placeholder="输入关键词，如：儒家思想、非遗文化..."
          size="large"
          clearable
          @keyup.enter="doSearch"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" size="large" @click="doSearch">检索</el-button>
      </div>

    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="card result-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Loading /></el-icon>
        <p>检索中...</p>
      </div>
    </div>

    <!-- 空结果 -->
    <div v-else-if="searched && results.length === 0" class="card result-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Search /></el-icon>
        <p>未找到与「{{ lastKeyword }}」相关的内容</p>
      </div>
    </div>

    <!-- 初始状态 -->
    <div v-else-if="!searched" class="card result-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Search /></el-icon>
        <p>输入关键词开始探索</p>
      </div>
    </div>

    <!-- 搜索结果 -->
    <template v-else>
      <div class="result-info">
        找到 <strong>{{ total }}</strong> 条与「<strong>{{ lastKeyword }}</strong>」相关的结果
      </div>
      <div class="material-list">
        <div
          v-for="item in results"
          :key="item.id"
          class="card material-card"
          @click="$router.push(`/material/${item.id}`)"
        >
          <div class="card-cover">
            <img v-if="item.coverImage" :src="item.coverImage" :alt="item.title" referrerpolicy="no-referrer" />
            <el-icon v-else :size="48" color="var(--ink-wash)"><Document /></el-icon>
          </div>
          <div class="card-body">
            <h3 class="card-title" v-html="item.highlightTitle || item.title"></h3>
            <p class="card-author" v-if="item.author">{{ item.author }}</p>
            <p class="card-summary" v-if="item.highlightContent" v-html="item.highlightContent"></p>
            <div class="card-meta">
              <span v-if="item.dynasty">{{ item.dynasty }}</span>
              <span v-if="item.category">{{ item.category }}</span>
              <span v-if="item.educationLevel">{{ item.educationLevel }}</span>
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
          @current-change="doSearch"
        />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search, Document, Loading, View, Star } from '@element-plus/icons-vue'
import { searchMaterials } from '../api/materials'

interface SearchResult {
  id: number
  title: string
  highlightTitle: string
  author: string
  dynasty: string
  category: string
  educationLevel: string
  highlightContent: string
  coverImage: string
  viewCount: number
  favoriteCount: number
  createdAt: string
}

const keyword = ref('')
const lastKeyword = ref('')
const searched = ref(false)
const loading = ref(false)
const results = ref<SearchResult[]>([])
const currentPage = ref(1)
const total = ref(0)
const pageSize = 12
async function doSearch() {
  const kw = keyword.value.trim()
  if (!kw) return

  searched.value = true
  lastKeyword.value = kw
  loading.value = true
  try {
    const res = await searchMaterials({ keyword: kw, page: currentPage.value, size: pageSize })
    const data = res.data || res
    results.value = data.records || []
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

.search-bar-card { padding: 20px; margin-bottom: 20px; }
.search-row { display: flex; gap: 12px; }
.search-row .el-input { flex: 1; }

.result-info { font-size: 13px; color: var(--ink-gray); margin-bottom: 16px; }

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
.card-title :deep(em) {
  font-style: normal;
  color: var(--accent-seal);
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
.card-summary :deep(em) {
  font-style: normal;
  color: var(--accent-seal);
}

.card-meta {
  display: flex; gap: 16px;
  font-size: 12px; color: var(--ink-gray);
}
.card-meta span::before { content: '#'; margin-right: 2px; }

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
