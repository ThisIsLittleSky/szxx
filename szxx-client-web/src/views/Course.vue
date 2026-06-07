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
        @click="activeLevel = l.value"
      >
        <span class="level-icon">{{ l.icon }}</span>
        <span class="level-name">{{ l.label }}</span>
      </button>
    </div>

    <div class="card result-area">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Reading /></el-icon>
        <p>选择学段查看配套思政课程素材</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Reading } from '@element-plus/icons-vue'

const activeLevel = ref('primary-low')

const levels = [
  { value: 'primary-low', label: '小学 1-3年级', icon: '📖' },
  { value: 'primary-high', label: '小学 4-6年级', icon: '📚' },
  { value: 'junior', label: '初中', icon: '📝' },
  { value: 'senior', label: '高中', icon: '📜' },
  { value: 'college', label: '大学', icon: '🎓' }
]
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

.result-area { padding: 20px; min-height: 300px; }
.empty-hint { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 60px 0; color: var(--ink-light); font-size: 14px; }
</style>
