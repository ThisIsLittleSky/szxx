<template>
  <div class="page-container">
    <el-card>
      <!-- 搜索栏 -->
      <div class="search-bar">
        <el-input
          v-model="searchKey"
          placeholder="搜索素材标题"
          style="width: 220px; margin-right: 16px;"
          clearable
          @keyup.enter="getList"
        />
        <el-select v-model="status" placeholder="审核状态" style="width: 140px; margin-right: 16px;" @change="getList">
          <el-option label="全部" :value="undefined" />
          <el-option label="待审核" value="pending" />
          <el-option label="已通过" value="approved" />
          <el-option label="已驳回" value="rejected" />
        </el-select>
      </div>

      <!-- 素材表格 -->
      <el-table
        :data="tableData"
        border
        style="width: 100%; margin: 20px 0;"
        v-loading="tableLoading"
      >
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="title" label="素材标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="100" align="center" />
        <el-table-column prop="dynasty" label="朝代" width="120" align="center">
          <template #default="{ row }">{{ getCategoryName(row.dynasty) }}</template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="120" align="center">
          <template #default="{ row }">{{ getCategoryName(row.category) }}</template>
        </el-table-column>
        <el-table-column prop="educationLevel" label="学段" width="120" align="center">
          <template #default="{ row }">{{ getCategoryName(row.educationLevel) }}</template>
        </el-table-column>
        <el-table-column prop="uploaderName" label="上传人" width="100" align="center" />
        <el-table-column prop="status" label="审核状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status === 'approved' ? 'success' : row.status === 'pending' ? 'warning' : 'danger'">
              {{ row.status === 'pending' ? '待审核' : row.status === 'approved' ? '已通过' : '已驳回' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180" align="center">
          <template #default="{ row }">{{ formatDate(row.createdAt) }}</template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="{ row }">
            <el-button type="info" size="small" @click="openDetailDialog(row)">详情</el-button>
            <el-button type="primary" size="small" @click="openReviewDialog(row)">审核</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        v-model:current-page="page"
        v-model:page-size="size"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="getList"
        @current-change="getList"
      />
    </el-card>

    <!-- 审核弹窗 -->
    <el-dialog v-model="reviewVisible" title="素材审核" width="450px">
      <el-radio-group v-model="reviewStatus">
        <el-radio label="approved">通过审核</el-radio>
        <el-radio label="rejected">驳回审核</el-radio>
      </el-radio-group>
      <el-input
        v-model="reviewComment"
        type="textarea"
        rows="3"
        placeholder="请输入审核意见"
        style="margin-top: 16px;"
      />
      <template #footer>
        <el-button @click="reviewVisible = false">取消</el-button>
        <el-button type="primary" :loading="reviewLoading" @click="submitReview">确定</el-button>
      </template>
    </el-dialog>

    <!-- 附件预览弹窗 -->
    <el-dialog v-model="previewVisible" :title="'预览：' + previewFilename" width="860px" @closed="cleanupBlobUrl">
      <div class="preview-container">
        <div v-if="previewCategory === 'image'" class="preview-image-wrapper">
          <el-image :src="previewUrl" fit="contain" style="max-height:70vh" :preview-src-list="[previewUrl]" />
        </div>
        <iframe v-else-if="previewCategory === 'pdf' || previewCategory === 'text'" :src="previewUrl" class="preview-iframe" />
        <video v-else-if="previewCategory === 'video'" :src="previewUrl" controls class="preview-video" />
        <audio v-else-if="previewCategory === 'audio'" :src="previewUrl" controls class="preview-audio" />
        <div v-else class="preview-unsupported">
          <el-icon :size="48"><Warning /></el-icon>
          <p>该文件类型不支持在线预览</p>
          <el-button type="primary" @click="downloadAttachment(previewAttachmentId)">下载文件</el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="素材详情" width="750px">
      <div v-loading="detailLoading">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="标题" :span="2">{{ detailMaterial?.title }}</el-descriptions-item>
          <el-descriptions-item label="作者">{{ detailMaterial?.author || '-' }}</el-descriptions-item>
          <el-descriptions-item label="朝代">{{ getCategoryName(detailMaterial?.dynasty || '') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="分类">{{ getCategoryName(detailMaterial?.category || '') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="学段">{{ getCategoryName(detailMaterial?.educationLevel || '') || '-' }}</el-descriptions-item>
          <el-descriptions-item label="上传人">{{ detailMaterial?.uploaderName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="标签">{{ detailMaterial?.tags || '-' }}</el-descriptions-item>
          <el-descriptions-item label="状态">
            <el-tag :type="detailMaterial?.status === 'approved' ? 'success' : detailMaterial?.status === 'pending' ? 'warning' : 'danger'" size="small">
              {{ detailMaterial?.status === 'pending' ? '待审核' : detailMaterial?.status === 'approved' ? '已通过' : '已驳回' }}
            </el-tag>
          </el-descriptions-item>

          <el-descriptions-item label="内容" :span="2">
            <div v-if="detailMaterial?.content" class="content-preview" v-html="detailMaterial.content"></div>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="视频" :span="2">
            <template v-if="detailMaterial?.videoUrl">
              <iframe
                v-if="isVideoEmbed(detailMaterial.videoUrl)"
                :src="getEmbedUrl(detailMaterial.videoUrl)"
                style="width:100%; height:360px; border:none; border-radius:4px"
                allowfullscreen
              ></iframe>
              <video
                v-else
                :src="detailMaterial.videoUrl"
                controls
                style="max-width:100%; max-height:300px; border-radius:4px"
              >
                您的浏览器不支持视频播放
              </video>
            </template>
            <span v-else>-</span>
          </el-descriptions-item>
          <el-descriptions-item label="浏览量">{{ detailMaterial?.viewCount ?? 0 }}</el-descriptions-item>
          <el-descriptions-item label="审核意见" :span="2">{{ detailMaterial?.reviewComment || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ formatDate(detailMaterial?.createdAt || '') }}</el-descriptions-item>
          <el-descriptions-item label="更新时间">{{ formatDate(detailMaterial?.updatedAt || '') }}</el-descriptions-item>
          <el-descriptions-item label="附件" :span="2">
            <div v-if="detailAttachments.length > 0" class="attachment-list">
              <div v-for="att in detailAttachments" :key="att.id" class="attachment-item">
                <el-icon><Document /></el-icon>
                <span class="attachment-name">{{ att.filename }}</span>
                <span class="attachment-size">{{ formatFileSize(att.fileSize) }}</span>
                <el-button type="primary" link size="small" @click="previewAttachment(att.id)">
                  预览
                </el-button>
                <el-button type="primary" link size="small" @click="downloadAttachment(att.id)">
                  下载
                </el-button>
              </div>
            </div>
            <span v-else>-</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button type="primary" @click="detailToReview">审核</el-button>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document, Warning } from '@element-plus/icons-vue'
import {
  getAdminMaterialList,
  getAdminMaterialDetail,
  deleteMaterial,
  reviewMaterial,
  type AdminMaterialItem,
  type AttachmentItem
} from '@/api/material'
import { getCategoryTree } from '@/api/common'
import { formatDate } from '@/utils/date'

// 分类名称映射表
const categoryNameMap: Record<string, string> = {}

function loadCategoryMap() {
  getCategoryTree().then((res: any) => {
    const data = res.data
    for (const list of Object.values(data) as any[]) {
      for (const item of list) {
        categoryNameMap[item.code] = item.name
      }
    }
  }).catch(() => {})
}

function getCategoryName(code: string): string {
  return categoryNameMap[code] || code
}

// 搜索&筛选
const searchKey = ref('')
const status = ref<string | undefined>(undefined)

// 分页
const page = ref(1)
const size = ref(10)
const total = ref(0)

// 表格数据
const tableData = ref<AdminMaterialItem[]>([])
const tableLoading = ref(false)

// 审核弹窗
const reviewVisible = ref(false)
const reviewLoading = ref(false)
const currentMaterialId = ref('')
const reviewStatus = ref('approved')
const reviewComment = ref('')

// 详情弹窗
const detailVisible = ref(false)
const detailLoading = ref(false)
const detailMaterial = ref<AdminMaterialItem | null>(null)
const detailAttachments = ref<AttachmentItem[]>([])

// 附件预览弹窗
const previewVisible = ref(false)
const previewUrl = ref('')
const previewFilename = ref('')
const previewCategory = ref('')
const previewAttachmentId = ref('')

// 获取素材列表
const getList = async () => {
  tableLoading.value = true
  try {
    const params: any = {
      page: page.value,
      size: size.value,
      keyword: searchKey.value
    }
    if (status.value) {
      params.status = status.value
    }

    const res = await getAdminMaterialList(params)
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (err) {
    ElMessage.error('获取素材列表失败')
  } finally {
    tableLoading.value = false
  }
}

// 打开审核弹窗
const openReviewDialog = (row: AdminMaterialItem) => {
  currentMaterialId.value = row.id
  reviewStatus.value = row.status === 'approved' ? 'approved' : 'pending'
  reviewComment.value = ''
  reviewVisible.value = true
}

// 提交审核
const submitReview = async () => {
  reviewLoading.value = true
  try {
    await reviewMaterial(currentMaterialId.value, {
      status: reviewStatus.value,
      reviewComment: reviewComment.value
    })
    ElMessage.success('审核操作成功')
    reviewVisible.value = false
    getList()
  } catch (err) {
    ElMessage.error('审核失败')
  } finally {
    reviewLoading.value = false
  }
}

// 打开详情弹窗
const openDetailDialog = async (row: AdminMaterialItem) => {
  detailVisible.value = true
  detailLoading.value = true
  detailMaterial.value = null
  detailAttachments.value = []
  try {
    const res = await getAdminMaterialDetail(row.id)
    detailMaterial.value = res.data.material
    detailAttachments.value = res.data.attachments || []
  } catch (err) {
    ElMessage.error('获取素材详情失败')
  } finally {
    detailLoading.value = false
  }
}

// 从详情弹窗跳转到审核
const detailToReview = () => {
  if (detailMaterial.value) {
    detailVisible.value = false
    openReviewDialog(detailMaterial.value)
  }
}

// 删除素材
const handleDelete = async (id: number) => {
  await ElMessageBox.confirm('确定删除该素材？', '提示', { type: 'warning' })
  try {
    await deleteMaterial(String(id))
    ElMessage.success('删除成功')
    getList()
  } catch (err) {
    ElMessage.error('删除失败')
  }
}

// 判断是否为平台视频链接（需 iframe 嵌入）
const isVideoEmbed = (url: string): boolean => {
  return /bilibili\.com|youtube\.com|youku\.com|qq\.com|iqiyi\.com/.test(url)
}

// 将平台视频链接转为可嵌入的 iframe URL
const getEmbedUrl = (url: string): string => {
  // Extract bvid from any B站 format
  const bvidMatch = url.match(/BV[0-9A-Za-z]{10}/)
  if (bvidMatch) {
    return `https://player.bilibili.com/player.html?bvid=${bvidMatch[0]}&page=1&high_quality=1`
  }
  if (/youtube\.com\/watch\?v=([\w-]+)/.test(url)) {
    return `https://www.youtube.com/embed/${RegExp.$1}`
  }
  if (/v\.qq\.com/.test(url)) {
    return url.replace('v.qq.com', 'v.qq.com/txp/iframe/player.html')
  }
  return url
}

// 格式化文件大小
const formatFileSize = (bytes: number) => {
  if (!bytes) return '0 B'
  const units = ['B', 'KB', 'MB', 'GB']
  let i = 0
  let size = bytes
  while (size >= 1024 && i < units.length - 1) {
    size /= 1024
    i++
  }
  return size.toFixed(1) + ' ' + units[i]
}

// 判断文件预览类型
const getFileCategory = (filename: string): string => {
  const ext = filename.split('.').pop()?.toLowerCase() || ''
  if (ext === 'pdf') return 'pdf'
  if (['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg', 'bmp', 'ico'].includes(ext)) return 'image'
  if (['mp4', 'webm', 'mov', 'avi', 'mkv'].includes(ext)) return 'video'
  if (['mp3', 'wav', 'ogg', 'flac', 'aac'].includes(ext)) return 'audio'
  if (['txt', 'html', 'css', 'js', 'json', 'xml', 'md', 'csv', 'ts', 'vue', 'java', 'py', 'yml', 'log'].includes(ext)) return 'text'
  return 'unsupported'
}

// 获取带认证头部的文件 Blob
const fetchBlobWithAuth = async (url: string): Promise<Blob> => {
  const token = localStorage.getItem('token')
  const response = await fetch(url, {
    headers: { Authorization: `Bearer ${token}` }
  })
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`)
  }
  return response.blob()
}

// 清理 blob URL
const cleanupBlobUrl = () => {
  if (previewUrl.value && previewUrl.value.startsWith('blob:')) {
    URL.revokeObjectURL(previewUrl.value)
    previewUrl.value = ''
  }
}

// 预览附件（弹窗）
const previewAttachment = async (attachmentId: string) => {
  const att = detailAttachments.value.find(a => a.id === attachmentId)
  if (!att) return
  previewAttachmentId.value = attachmentId
  previewFilename.value = att.filename
  previewCategory.value = getFileCategory(att.filename)
  previewVisible.value = true
  cleanupBlobUrl()
  try {
    const blob = await fetchBlobWithAuth(`/api/v1/files/download/${attachmentId}?download=false`)
    previewUrl.value = URL.createObjectURL(blob)
  } catch (err) {
    ElMessage.error('加载预览文件失败')
  }
}

// 下载附件
const downloadAttachment = async (attachmentId: string) => {
  const att = detailAttachments.value.find(a => a.id === attachmentId)
  try {
    const blob = await fetchBlobWithAuth(`/api/v1/files/download/${attachmentId}?download=true`)
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    if (att) a.download = att.filename
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
    URL.revokeObjectURL(url)
  } catch (err) {
    ElMessage.error('下载失败')
  }
}

onUnmounted(() => {
  cleanupBlobUrl()
})

onMounted(() => {
  loadCategoryMap()
  getList()
})
</script>

<style scoped>
.page-container {
  background: #fff;
  border-radius: 8px;
}
.search-bar {
  display: flex;
  align-items: center;
}
.content-preview {
  max-height: 200px;
  overflow-y: auto;
  word-break: break-all;
}
.attachment-list {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.attachment-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  background: #f5f7fa;
  border-radius: 6px;
}
.attachment-name {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.attachment-size {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
}
.preview-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}
.preview-image-wrapper {
  text-align: center;
}
.preview-iframe {
  width: 100%;
  height: 70vh;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}
.preview-video {
  max-width: 100%;
  max-height: 65vh;
  border-radius: 4px;
}
.preview-audio {
  width: 100%;
  margin: 20px 0;
}
.preview-unsupported {
  text-align: center;
  padding: 48px 0;
  color: #909399;
}
.preview-unsupported p {
  margin: 16px 0;
  font-size: 14px;
}
</style>
