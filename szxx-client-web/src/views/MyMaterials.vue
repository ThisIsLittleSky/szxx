<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">我的素材</h1>
      <p class="page-desc">管理已上传的素材，查看审核状态</p>
    </header>

    <!-- 加载中 -->
    <div v-if="loading" class="card list-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Loading /></el-icon>
        <p>加载中...</p>
      </div>
    </div>

    <!-- 空状态 -->
    <div v-else-if="!loading && materials.length === 0" class="card list-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Document /></el-icon>
        <p>还没有上传过素材</p>
        <el-button type="primary" style="margin-top:12px;" @click="$router.push('/upload')">去上传</el-button>
      </div>
    </div>

    <!-- 素材列表 -->
    <template v-else>
      <div class="material-list">
        <div
          v-for="item in materials"
          :key="item.id"
          class="card material-card"
          @click="$router.push(`/material/${item.id}`)"
        >
          <div class="card-cover">
            <img v-if="item.coverImage" :src="item.coverImage" :alt="item.title" referrerpolicy="no-referrer" />
            <el-icon v-else :size="48" color="var(--ink-wash)"><Document /></el-icon>
          </div>
          <div class="card-body">
            <div class="card-top">
              <h3 class="card-title">{{ item.title }}</h3>
              <span class="status-tag" :class="'status-' + item.status">
                {{ statusMap[item.status] || item.status }}
              </span>
            </div>
            <p class="card-author" v-if="item.author">{{ item.author }}</p>
            <p class="card-summary" v-if="item.summary">{{ item.summary }}</p>
            <div class="card-meta">
              <span v-if="item.dynasty">{{ item.dynasty }}</span>
              <span v-if="item.category">{{ item.category }}</span>
              <span v-if="item.educationLevel">{{ item.educationLevel }}</span>
            </div>
            <div class="card-tags" v-if="item.tags && item.tags.length > 0">
              <el-tag v-for="tag in item.tags" :key="tag" size="small" type="info">{{ tag }}</el-tag>
            </div>
            <div class="card-footer">
              <span class="card-stats">
                <el-icon><View /></el-icon> {{ item.viewCount }}
                <el-icon style="margin-left:12px;"><Star /></el-icon> {{ item.favoriteCount }}
              </span>
              <span class="card-date">{{ formatDate(item.createdAt) }}</span>
              <el-button
                type="danger"
                size="small"
                text
                @click.stop="handleDelete(item)"
              >
                <el-icon><Delete /></el-icon>
              </el-button>
            </div>
          </div>
        </div>
      </div>

      <!-- 分页 -->
      <div class="pagination-wrap" v-if="total > pageSize">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="pageSize"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchMaterials"
        />
      </div>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Document, Loading, View, Star, Delete } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMyMaterials, deleteMaterial } from '../api/materials'

interface MaterialItem {
  id: number
  title: string
  author: string
  dynasty: string
  category: string
  educationLevel: string
  tags: string[]
  coverImage: string
  summary: string
  status: string
  viewCount: number
  favoriteCount: number
  createdAt: string
}

const statusMap: Record<string, string> = {
  pending: '待审核',
  approved: '已通过',
  rejected: '已驳回'
}

const materials = ref<MaterialItem[]>([])
const loading = ref(true)
const currentPage = ref(1)
const total = ref(0)
const pageSize = 10

onMounted(() => {
  fetchMaterials()
})

async function fetchMaterials() {
  loading.value = true
  try {
    const res = await getMyMaterials({ page: currentPage.value, size: pageSize })
    const data = res.data || res
    materials.value = data.records || []
    total.value = data.total || 0
  } catch {
    // error handled by interceptor
  } finally {
    loading.value = false
  }
}

async function handleDelete(item: MaterialItem) {
  try {
    await ElMessageBox.confirm(`确定要删除素材「${item.title}」吗？此操作不可恢复。`, '确认删除', {
      confirmButtonText: '删除',
      cancelButtonText: '取消',
      type: 'warning'
    })
  } catch {
    return
  }

  try {
    await deleteMaterial(item.id)
    ElMessage.success('删除成功')
    materials.value = materials.value.filter(m => m.id !== item.id)
    total.value--
  } catch {
    // error handled by interceptor
  }
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

.list-card { padding: 20px; min-height: 300px; }
.empty-hint {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  padding: 60px 0; color: var(--ink-light); font-size: 14px;
}

.material-list { display: flex; flex-direction: column; gap: 16px; }

.material-card {
  display: flex;
  padding: 0;
  overflow: hidden;
  cursor: pointer;
  transition: box-shadow 0.2s;
}
.material-card:hover { box-shadow: 0 4px 16px rgba(0,0,0,.08); }

.card-cover {
  width: 160px;
  min-height: 160px;
  flex-shrink: 0;
  background: var(--el-fill-color-light);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}
.card-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-body {
  flex: 1;
  padding: 16px 20px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  min-width: 0;
}

.card-top {
  display: flex;
  align-items: center;
  gap: 10px;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--ink-black);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.status-tag {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
  flex-shrink: 0;
}
.status-pending { background: #fdf6ec; color: #e6a23c; }
.status-approved { background: #f0f9eb; color: #67c23a; }
.status-rejected { background: #fef0f0; color: #f56c6c; }

.card-author { font-size: 13px; color: var(--ink-gray); margin: 0; }
.card-summary {
  font-size: 13px;
  color: var(--ink-light);
  margin: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex;
  gap: 16px;
  font-size: 12px;
  color: var(--ink-gray);
}
.card-meta span::before { content: '#'; margin-right: 2px; }

.card-tags { display: flex; flex-wrap: wrap; gap: 6px; }

.card-footer {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-top: auto;
  font-size: 12px;
  color: var(--ink-light);
}
.card-stats { display: flex; align-items: center; gap: 2px; }
.card-date { color: var(--ink-light); }

.pagination-wrap {
  display: flex;
  justify-content: center;
  margin-top: 24px;
}
</style>
