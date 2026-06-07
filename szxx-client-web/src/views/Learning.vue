<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">学习中心</h1>
      <p class="page-desc">查看学习记录、阅读进度与学习统计</p>
    </header>

    <div class="stats-row">
      <div class="stat-card" v-for="s in stats" :key="s.label">
        <div class="stat-num">{{ s.value }}</div>
        <div class="stat-label">{{ s.label }}</div>
      </div>
    </div>

    <div class="card list-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Reading /></el-icon>
        <p>暂无学习记录</p>
        <p class="sub">浏览素材后将自动记录学习进度</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Reading } from '@element-plus/icons-vue'

const stats = [
  { label: '已学素材', value: '0' },
  { label: '学习时长', value: '0分钟' },
  { label: '完成阅读', value: '0' },
  { label: '知识掌握', value: '0点' }
]
</script>

<style scoped>
.page { max-width: 960px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.page-desc { margin-top: 6px; color: var(--ink-gray); font-size: 13px; }

.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card {
  text-align: center; padding: 20px 12px; background: var(--paper-white);
  border: 1px solid var(--ink-wash); border-radius: 6px;
}
.stat-num { font-size: 28px; font-weight: 700; color: var(--ink-black); font-family: var(--font-calligraphy); }
.stat-label { margin-top: 4px; font-size: 12px; color: var(--ink-light); letter-spacing: 1px; }

.list-card { padding: 20px; min-height: 300px; }
.empty-hint { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 60px 0; color: var(--ink-light); font-size: 14px; }
.sub { font-size: 12px; }
</style>
