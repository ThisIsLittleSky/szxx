<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">我的素材</h1>
      <p class="page-desc">管理已上传的素材，查看审核状态</p>
    </header>

    <div class="card list-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Document /></el-icon>
        <p>还没有上传过素材</p>
        <el-button type="primary" style="margin-top:12px;" @click="$router.push('/upload')">去上传</el-button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Document } from '@element-plus/icons-vue'
</script>

<style scoped>
.page { max-width: 960px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.page-desc { margin-top: 6px; color: var(--ink-gray); font-size: 13px; }

.list-card { padding: 20px; min-height: 300px; }
.empty-hint { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 60px 0; color: var(--ink-light); font-size: 14px; }
</style>
