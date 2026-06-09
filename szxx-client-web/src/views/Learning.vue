<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">学习中心</h1>
      <p class="page-desc">查看学习记录、阅读进度与学习统计</p>
    </header>

    <!-- 统计卡片 -->
    <div class="stats-row">
      <div class="stat-card" v-for="s in stats" :key="s.label">
        <div class="stat-num">{{ s.value }}</div>
        <div class="stat-label">{{ s.label }}</div>
      </div>
    </div>

    <!-- 加载中 -->
    <div v-if="loading" class="card list-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Loading /></el-icon>
        <p>加载中...</p>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!loading && records.length === 0" class="card list-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Reading /></el-icon>
        <p>暂无学习记录</p>
        <p class="sub">浏览素材后将自动记录学习进度</p>
      </div>
    </div>

    <!-- 记录列表 -->
    <template v-else>
      <div class="card list-card">
        <div class="record-list">
          <div
            v-for="item in records"
            :key="item.id"
            class="record-item"
            @click="goDetail(item.materialId)"
          >
            <div class="record-main">
              <span class="record-title">{{ item.materialTitle || '未知素材' }}</span>
              <span class="record-author" v-if="item.materialAuthor">{{ item.materialAuthor }}</span>
              <span class="record-badge" :class="item.completed === 1 ? 'badge-done' : 'badge-progress'">
                {{ item.completed === 1 ? '已完成' : '进行中' }}
              </span>
            </div>
            <div class="record-meta">
              <span>学习 {{ formatDuration(item.duration) }}</span>
              <span class="record-date">{{ formatDate(item.updatedAt) }}</span>
            </div>
          </div>
        </div>

        <div class="pagination-wrap" v-if="total > pageSize">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchRecords"
          />
        </div>
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Reading, Loading } from '@element-plus/icons-vue'
import { getLearningStats, getLearningRecords } from '../api/learning'

interface LearningRecord {
  id: number
  userId: number
  materialId: number
  materialTitle: string
  materialAuthor: string
  duration: number
  completed: number
  createdAt: string
  updatedAt: string
}

interface StatsData {
  totalMaterials: number
  totalDuration: number
  completedCount: number
}

const router = useRouter()

const statsData = ref<StatsData>({ totalMaterials: 0, totalDuration: 0, completedCount: 0 })
const records = ref<LearningRecord[]>([])
const loading = ref(true)
const currentPage = ref(1)
const total = ref(0)
const pageSize = 10

const stats = computed(() => {
  const { totalMaterials, totalDuration } = statsData.value
  return [
    { label: '已学素材', value: totalMaterials },
    { label: '学习时长', value: formatDuration(totalDuration) }
  ]
})

onMounted(() => {
  fetchStats()
  fetchRecords()
})

async function fetchStats() {
  try {
    const res = await getLearningStats()
    const data = res.data || res
    statsData.value = {
      totalMaterials: data.totalMaterials || 0,
      totalDuration: data.totalDuration || 0,
      completedCount: data.completedCount || 0
    }
  } catch {
    // error handled by interceptor
  }
}

async function fetchRecords() {
  loading.value = true
  try {
    const res = await getLearningRecords({ page: currentPage.value, size: pageSize })
    const data = res.data || res
    records.value = data.records || []
    total.value = data.total || 0
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}

function goDetail(materialId: number) {
  router.push(`/material/${materialId}`)
}

function formatDuration(seconds: number): string {
  if (!seconds || seconds <= 0) return '0分钟'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  if (h > 0) return m > 0 ? `${h}小时${m}分钟` : `${h}小时`
  return `${m}分钟`
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

.stats-row { display: grid; grid-template-columns: repeat(2, 1fr); gap: 16px; margin-bottom: 24px; }
.stat-card {
  text-align: center; padding: 20px 12px; background: var(--paper-white);
  border: 1px solid var(--ink-wash); border-radius: 6px;
}
.stat-num { font-size: 28px; font-weight: 700; color: var(--ink-black); font-family: var(--font-calligraphy); }
.stat-label { margin-top: 4px; font-size: 12px; color: var(--ink-light); letter-spacing: 1px; }

.list-card { padding: 20px; min-height: 300px; }
.empty-hint { display: flex; flex-direction: column; align-items: center; gap: 8px; padding: 60px 0; color: var(--ink-light); font-size: 14px; }
.sub { font-size: 12px; }

.record-list { display: flex; flex-direction: column; }
.record-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 0; border-bottom: 1px solid var(--ink-wash);
  cursor: pointer; transition: background 0.15s;
}
.record-item:last-child { border-bottom: none; }
.record-item:hover { background: var(--el-fill-color-light); margin: 0 -20px; padding-left: 20px; padding-right: 20px; }

.record-main { display: flex; align-items: center; gap: 12px; }
.record-title { font-size: 14px; font-weight: 500; color: var(--ink-black); }
.record-author { font-size: 12px; color: var(--ink-light); }

.record-badge {
  font-size: 12px; padding: 2px 8px; border-radius: 4px;
}
.badge-done { background: #f0f9eb; color: #67c23a; }
.badge-progress { background: #fdf6ec; color: #e6a23c; }

.record-meta { display: flex; align-items: center; gap: 20px; font-size: 13px; color: var(--ink-light); }
.record-date { color: var(--ink-light); }

.pagination-wrap { display: flex; justify-content: center; margin-top: 24px; }
</style>
