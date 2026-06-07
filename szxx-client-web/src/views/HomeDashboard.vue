<template>
  <div class="dashboard fade-up visible">
    <!-- 欢迎区 -->
    <section class="welcome">
      <h1 class="welcome-title">
        早安，{{ user?.nickname || '同学' }}
        <span class="welcome-wave">👋</span>
      </h1>
      <p class="welcome-desc">今天想探索哪段传统文化？</p>
    </section>

    <!-- 快捷入口 -->
    <section class="quick-actions">
      <div class="action-card" v-for="a in quickActions" :key="a.label" @click="$router.push(a.path)">
        <div class="action-icon"><el-icon :size="24"><component :is="a.icon" /></el-icon></div>
        <span class="action-label">{{ a.label }}</span>
      </div>
    </section>

    <!-- 统计卡片 -->
    <section class="stats-row">
      <div class="stat-card" v-for="s in stats" :key="s.label">
        <div class="stat-num">{{ s.value }}</div>
        <div class="stat-label">{{ s.label }}</div>
      </div>
    </section>

    <!-- 内容双栏 -->
    <section class="two-col">
      <div class="card panel">
        <h3 class="panel-title">热门素材</h3>
        <div class="empty-hint">加载中...</div>
      </div>
      <div class="card panel">
        <h3 class="panel-title">学习动态</h3>
        <div class="empty-hint">暂无学习记录</div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Search, Collection, Reading, Star } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const user = computed(() => userStore.user)

const quickActions = [
  { label: '关键词检索', icon: Search, path: '/search' },
  { label: '朝代分类', icon: Collection, path: '/category' },
  { label: '思政课程', icon: Reading, path: '/course' },
  { label: '我的收藏', icon: Star, path: '/favorites' }
]

const stats = [
  { label: '素材总数', value: '—' },
  { label: '学习时长', value: '—' },
  { label: '收藏素材', value: '—' },
  { label: '完成阅读', value: '—' }
]
</script>

<style scoped>
.dashboard { max-width: 960px; margin: 0 auto; }

.welcome { margin-bottom: 28px; }
.welcome-title { font-size: 24px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.welcome-wave { font-size: 24px; margin-left: 6px; }
.welcome-desc { margin-top: 6px; color: var(--ink-gray); font-size: 14px; }

.quick-actions {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 28px;
}
.action-card {
  display: flex; flex-direction: column; align-items: center; gap: 10px;
  padding: 20px 12px; background: var(--paper-white); border: 1px solid var(--ink-wash);
  border-radius: 6px; cursor: pointer; transition: all 0.3s;
}
.action-card:hover { border-color: var(--accent-seal); box-shadow: var(--shadow-md); transform: translateY(-2px); }
.action-card:hover .action-icon { color: var(--accent-seal); }
.action-icon { color: var(--ink-gray); transition: color 0.3s; }
.action-label { font-size: 13px; color: var(--ink-dark); letter-spacing: 1px; }

.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 28px; }
.stat-card {
  text-align: center; padding: 20px 12px; background: var(--paper-white);
  border: 1px solid var(--ink-wash); border-radius: 6px;
}
.stat-num { font-size: 28px; font-weight: 700; color: var(--ink-black); font-family: var(--font-calligraphy); }
.stat-label { margin-top: 4px; font-size: 12px; color: var(--ink-light); letter-spacing: 1px; }

.two-col { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.panel { padding: 20px; }
.panel-title { font-size: 15px; font-weight: 600; color: var(--ink-black); margin-bottom: 12px; letter-spacing: 1px; }
.empty-hint { text-align: center; padding: 40px 0; color: var(--ink-light); font-size: 13px; }

@media (max-width: 768px) {
  .quick-actions, .stats-row, .two-col { grid-template-columns: repeat(2, 1fr); }
}
</style>
