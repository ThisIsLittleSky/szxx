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

    <!-- 分类统计图表 -->
    <el-card style="margin-top: 20px;">
      <h3>素材分类分布</h3>
      <div ref="chartRef" class="chart-box"></div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import * as echarts from 'echarts'
import { ElMessage } from 'element-plus'
import { getDashboardData, getMaterialCategoryStats } from '@/api/dashboard'

const chartRef = ref<HTMLDivElement | null>(null)
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
const loadChart = async () => {
  if (!chartRef.value) return
  try {
    const res = await getMaterialCategoryStats()
    const xData = res.data.map((item: any) => item.category)
    const yData = res.data.map((item: any) => item.count)

    const myChart = echarts.init(chartRef.value)
    myChart.setOption({
      tooltip: { trigger: 'axis' },
      xAxis: { type: 'category', data: xData },
      yAxis: { type: 'value' },
      series: [{ type: 'bar', data: yData, barWidth: '40%' }]
    })
    window.addEventListener('resize', () => myChart.resize())
  } catch (err) {
    ElMessage.error('加载图表数据失败')
  }
}

onMounted(() => {
  loadDashboard()
  loadChart()
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
.chart-box { width: 100%; height: 320px; }
</style>