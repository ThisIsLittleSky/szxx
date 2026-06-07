<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <button class="back-btn" @click="$router.back()">
        <el-icon><ArrowLeft /></el-icon>
        返回
      </button>
    </header>

    <div class="card detail-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Document /></el-icon>
        <p>素材详情加载中...</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ArrowLeft, Document } from '@element-plus/icons-vue'
</script>

<style scoped>
.page { max-width: 860px; margin: 0 auto; }
.page-header { margin-bottom: 20px; }
.back-btn {
  display: inline-flex; align-items: center; gap: 4px;
  background: none; border: none; color: var(--ink-gray); font-size: 13px;
  cursor: pointer; padding: 6px 0; font-family: inherit; letter-spacing: 1px;
  transition: color 0.3s;
}
.back-btn:hover { color: var(--accent-seal); }

.detail-card { padding: 20px; min-height: 400px; }
.empty-hint { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 80px 0; color: var(--ink-light); font-size: 14px; }
</style>
