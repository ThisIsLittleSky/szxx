<template>
  <div class="dashboard-container">
    <!-- 统计卡片 -->
    <div class="stat-grid">
      <el-card class="stat-card">
        <el-statistic title="素材总数" :value="stat.total_materials" />
      </el-card>
      <el-card class="stat-card">
        <el-statistic title="用户总数" :value="stat.total_users" />
      </el-card>
      <el-card class="stat-card">
        <el-statistic title="总访问量" :value="stat.total_views" />
      </el-card>
      <el-card class="stat-card">
        <el-statistic title="待审核素材" :value="stat.pending_review_count" />
      </el-card>
    </div>

    <!-- 图表区域：分类分布 + 热门排行 -->
    <div class="chart-grid">
      <el-card>
        <h3>素材分类分布</h3>
        <div ref="categoryChartRef" class="chart-box"></div>
      </el-card>
      <el-card>
        <h3>热门素材 TOP10</h3>
        <div ref="topChartRef" class="chart-box"></div>
      </el-card>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getDashboardData, getMaterialCategoryStats, getTopMaterials } from '@/api/dashboard'

const categoryChartRef = ref<HTMLDivElement | null>(null)
const topChartRef = ref<HTMLDivElement | null>(null)
// 统计数据
const stat = ref({
  total_materials: 0,
  total_users: 0,
  total_views: 0,
  total_downloads: 0,
  pending_review_count: 0
})

// 加载仪表盘总览数据
const loadDashboard = async () => {
  try {
    const res = await getDashboardData()
    stat.value = res.data
  } catch (err) {
    ElMessage.error('加载统计数据失败')
  }
}

// 加载分类图表
const loadCategoryChart = async () => {
  if (!categoryChartRef.value) return
  try {
    const res = await getMaterialCategoryStats()
    const xData = res.data.map((item: any) => item.category)
    const yData = res.data.map((item: any) => item.count)

    const chart = echarts.init(categoryChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: xData },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: yData, barWidth: '40%', itemStyle: { color: '#409eff' } }]
    })
    window.addEventListener('resize', () => chart.resize())
  } catch (err) {
    ElMessage.error('加载分类图表失败')
  }
}

// 加载热门素材排行
const loadTopChart = async () => {
  if (!topChartRef.value) return
  try {
    const res = await getTopMaterials({ type: 'views', limit: 10 })
    const names = res.data.map((item: any) => item.title.length > 10 ? item.title.slice(0, 10) + '...' : item.title)
    const values = res.data.map((item: any) => item.view_count)

    const chart = echarts.init(topChartRef.value)
    chart.setOption({
      tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
      grid: { left: '3%', right: '10%', bottom: '3%', containLabel: true },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: names.reverse(), inverse: true },
      series: [{ type: 'bar', data: values.reverse(), barWidth: '60%', itemStyle: { color: '#67c23a' } }]
    })
    window.addEventListener('resize', () => chart.resize())
  } catch (err) {
    ElMessage.error('加载热门素材失败')
  }
}

onMounted(() => {
  loadDashboard()
  loadCategoryChart()
  loadTopChart()
})
</script>

<style scoped>
.dashboard-container { padding: 0; }
.stat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}
.stat-card { text-align: center; }
.chart-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-top: 20px;
}
.chart-box { width: 100%; height: 320px; }
</style>