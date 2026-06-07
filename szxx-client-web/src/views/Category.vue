<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">朝代分类</h1>
      <p class="page-desc">按朝代、文化品类、思政学段三维筛选传统文化素材</p>
    </header>

    <div class="card filter-card">
      <div class="filter-row">
        <div class="filter-item">
          <label>朝代</label>
          <el-select v-model="filters.dynasty" placeholder="全部朝代" clearable>
            <el-option v-for="d in dynasties" :key="d" :label="d" :value="d" />
          </el-select>
        </div>
        <div class="filter-item">
          <label>文化品类</label>
          <el-select v-model="filters.category" placeholder="全部分类" clearable>
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </div>
        <div class="filter-item">
          <label>思政学段</label>
          <el-select v-model="filters.level" placeholder="全部学段" clearable>
            <el-option v-for="l in levels" :key="l" :label="l" :value="l" />
          </el-select>
        </div>
      </div>
    </div>

    <div class="card result-area">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Collection /></el-icon>
        <p>选择筛选条件浏览素材</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { Collection } from '@element-plus/icons-vue'

const dynasties = ['先秦', '秦汉', '魏晋南北朝', '隋唐', '宋', '元', '明', '清', '近现代']
const categories = ['诸子文化', '传统非遗', '民俗文化', '传统技艺', '红色传统文化', '人文典故', '诗词歌赋', '古代科技']
const levels = ['小学(1-3年级)', '小学(4-6年级)', '初中', '高中', '大学']

const filters = reactive({ dynasty: '', category: '', level: '' })
</script>

<style scoped>
.page { max-width: 960px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.page-desc { margin-top: 6px; color: var(--ink-gray); font-size: 13px; }

.filter-card { padding: 20px; margin-bottom: 20px; }
.filter-row { display: flex; gap: 20px; flex-wrap: wrap; }
.filter-item { display: flex; flex-direction: column; gap: 6px; min-width: 180px; }
.filter-item label { font-size: 12px; color: var(--ink-light); letter-spacing: 1px; }

.result-area { padding: 20px; min-height: 300px; }
.empty-hint { display: flex; flex-direction: column; align-items: center; gap: 12px; padding: 60px 0; color: var(--ink-light); font-size: 14px; }
</style>
