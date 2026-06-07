<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">下载中心</h1>
      <p class="page-desc">下载素材附件与导出课件</p>
    </header>

    <div class="card list-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Download /></el-icon>
        <p>暂无下载记录</p>
        <p class="sub">下载素材附件后将在此显示</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Download } from '@element-plus/icons-vue'
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
