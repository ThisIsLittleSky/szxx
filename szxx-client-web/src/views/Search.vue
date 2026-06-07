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
          @keyup.enter="search"
        >
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" size="large" @click="search">检索</el-button>
      </div>

      <div class="hot-tags" v-if="hotTags.length">
        <span class="hot-label">热门搜索：</span>
        <el-tag v-for="t in hotTags" :key="t" class="hot-tag" @click="keyword = t; search()">{{ t }}</el-tag>
      </div>
    </div>

    <div class="card result-area">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Search /></el-icon>
        <p>输入关键词开始探索</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Search } from '@element-plus/icons-vue'

const keyword = ref('')
const hotTags = ['论语', '唐诗', '宋词', '书法', '国画', '戏曲', '儒家', '非遗']

function search() {
  // TODO: 调用搜索API
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

.hot-tags { margin-top: 14px; display: flex; align-items: center; gap: 8px; flex-wrap: wrap; }
.hot-label { font-size: 12px; color: var(--ink-light); white-space: nowrap; }
.hot-tag { cursor: pointer; border-radius: 2px; }

.result-area { padding: 20px; min-height: 300px; }
.empty-hint { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 60px 0; color: var(--ink-light); font-size: 14px; }
</style>
