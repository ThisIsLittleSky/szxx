<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">我的收藏</h1>
      <p class="page-desc">管理已收藏的传统文化素材</p>
    </header>

    <div class="card list-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Star /></el-icon>
        <p>收藏夹是空的</p>
        <p class="sub">浏览素材时点击收藏即可加入收藏夹</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Star } from '@element-plus/icons-vue'
</script>

<style scoped>
.page { max-width: 960px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.page-desc { margin-top: 6px; color: var(--ink-gray); font-size: 13px; }

.list-card { padding: 20px; min-height: 300px; }
.empty-hint { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 60px 0; color: var(--ink-light); font-size: 14px; }
.sub { font-size: 12px; }
</style>
