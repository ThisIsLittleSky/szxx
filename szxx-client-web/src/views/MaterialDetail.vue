<template>
  <div class="page fade-up visible">
    <!-- 加载中 -->
    <div v-if="loading" class="card detail-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Loading /></el-icon>
        <p>素材详情加载中...</p>
      </div>
    </div>

    <!-- 错误 -->
    <div v-else-if="errorMsg" class="card detail-card">
      <div class="empty-hint">
        <el-icon :size="48" color="var(--ink-wash)"><Warning /></el-icon>
        <p>{{ errorMsg }}</p>
        <el-button style="margin-top:12px;" @click="$router.back()">返回</el-button>
      </div>
    </div>

    <!-- 详情 -->
    <template v-else-if="detail">
      <header class="page-header">
        <button class="back-btn" @click="$router.back()">
          <el-icon><ArrowLeft /></el-icon>
          返回
        </button>
      </header>

      <div class="card detail-card">
        <!-- 封面图 -->
        <div class="cover-wrap" v-if="detail.coverImage" @click="previewImage(detail.coverImage)">
          <img :src="detail.coverImage" :alt="detail.title" referrerpolicy="no-referrer" />
          <div class="cover-overlay">
            <el-icon :size="24"><ZoomIn /></el-icon>
          </div>
        </div>

        <!-- 标题行 -->
        <div class="title-row">
          <h1 class="detail-title">{{ detail.title }}</h1>
          <el-button
            :type="detail.isFavorited ? 'warning' : 'default'"
            size="small"
            @click="handleFavorite"
          >
            <el-icon><Star /></el-icon>
            {{ detail.isFavorited ? '已收藏' : '收藏' }}
          </el-button>
        </div>

        <!-- 作者 -->
        <p class="detail-author" v-if="detail.author">作者：{{ detail.author }}</p>

        <!-- 元信息 -->
        <div class="meta-row">
          <span v-if="detail.dynasty"><el-icon><Collection /></el-icon> {{ detail.dynasty }}</span>
          <span v-if="detail.category"><el-icon><Folder /></el-icon> {{ detail.category }}</span>
          <span v-if="detail.educationLevel"><el-icon><School /></el-icon> {{ detail.educationLevel }}</span>
        </div>

        <!-- 标签 -->
        <div class="tag-row" v-if="detail.tags && detail.tags.length > 0">
          <el-tag v-for="t in detail.tags" :key="t" size="small" type="info">{{ t }}</el-tag>
        </div>

        <!-- 统计 -->
        <div class="stats-row">
          <span><el-icon><View /></el-icon> {{ detail.viewCount }} 次浏览</span>
          <span><el-icon><Star /></el-icon> {{ detail.favoriteCount }} 次收藏</span>
          <span class="uploader-info" v-if="detail.uploader">
            上传者：{{ detail.uploader.nickname }}
          </span>
          <span>{{ formatDate(detail.createdAt) }}</span>
        </div>

        <!-- 正文 -->
        <div class="content-section" v-if="detail.content">
          <h3 class="section-title">正文内容</h3>
          <div class="content-body" ref="contentRef" v-html="detail.content" @click="onContentClick"></div>
        </div>

        <!-- 视频 -->
        <div class="content-section" v-if="detail.videoUrl">
          <h3 class="section-title">视频链接</h3>
          <a class="video-link" @click.prevent="previewVideo(detail.videoUrl)">{{ detail.videoUrl }}</a>
        </div>

        <!-- 知识点 -->
        <div class="content-section" v-if="detail.knowledgePoints && detail.knowledgePoints.length > 0">
          <h3 class="section-title">思政知识点</h3>
          <div class="kp-list">
            <div v-for="kp in detail.knowledgePoints" :key="kp.id" class="kp-item">
              <h4>{{ kp.title }}</h4>
              <p v-if="kp.content">{{ kp.content }}</p>
            </div>
          </div>
        </div>

        <!-- 附件 -->
        <div class="content-section" v-if="detail.attachments && detail.attachments.length > 0">
          <h3 class="section-title">附件下载</h3>
          <div class="attachment-list">
            <div
              v-for="att in detail.attachments"
              :key="att.id"
              class="attachment-item"
              @click="previewAttachment(att)"
            >
              <el-icon><Document /></el-icon>
              <span class="att-name">{{ att.filename }}</span>
              <span class="att-size">{{ formatSize(att.fileSize) }}</span>
              <el-icon><View /></el-icon>
            </div>
          </div>
        </div>
      </div>
    </template>

    <!-- 预览弹窗 -->
    <el-dialog
      v-model="preview.visible"
      :title="preview.title"
      width="80%"
      top="5vh"
      destroy-on-close
      :close-on-click-modal="true"
    >
      <div class="preview-body">
        <div v-if="preview.loading" class="preview-loading">
          <el-icon :size="48" color="var(--ink-wash)"><Loading /></el-icon>
          <p>加载中...</p>
        </div>
        <!-- 图片预览 -->
        <img v-else-if="preview.type === 'image'" :src="preview.blobUrl" class="preview-image" />
        <!-- 视频预览 -->
        <div v-else-if="preview.type === 'video'" class="preview-video-wrap">
          <iframe
            v-if="isVideoEmbed(preview.url)"
            :src="getEmbedUrl(preview.url)"
            class="preview-iframe"
            allowfullscreen
          ></iframe>
          <video v-else :src="preview.url" controls class="preview-video"></video>
        </div>
        <!-- PDF/文档预览 -->
        <iframe
          v-else-if="preview.type === 'file'"
          :src="preview.blobUrl"
          class="preview-iframe"
        ></iframe>
      </div>
      <template #footer>
        <el-button @click="closePreview">关闭</el-button>
        <el-button v-if="preview.downloadUrl" type="primary" @click="downloadFile(preview.downloadUrl)">
          <el-icon><Download /></el-icon>下载
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import axios from 'axios'
import {
  ArrowLeft, Loading, Warning, Star, View, ZoomIn,
  Collection, Folder, School, Document, Download
} from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import { getMaterialDetail, toggleFavorite } from '../api/materials'
import { reportLearning } from '../api/learning'

interface UploaderInfo {
  id: number
  nickname: string
}

interface KnowledgePoint {
  id: number
  title: string
  content: string
  sortOrder: number
}

interface Attachment {
  id: number
  filename: string
  filePath: string
  fileType: string
  fileSize: number
}

interface MaterialDetail {
  id: number
  title: string
  author: string
  dynasty: string
  category: string
  educationLevel: string
  tags: string[]
  coverImage: string
  content: string
  videoUrl: string
  knowledgePoints: KnowledgePoint[]
  attachments: Attachment[]
  uploader: UploaderInfo
  viewCount: number
  favoriteCount: number
  isFavorited: boolean
  createdAt: string
}

const route = useRoute()
const detail = ref<MaterialDetail | null>(null)
const loading = ref(true)
const errorMsg = ref('')
const contentRef = ref<HTMLElement | null>(null)
const startTime = ref(0)
let reportTimer: ReturnType<typeof setInterval> | null = null

const preview = reactive({
  visible: false,
  loading: false,
  type: 'image' as 'image' | 'video' | 'file',
  title: '',
  url: '',
  blobUrl: '',
  downloadUrl: ''
})

onMounted(() => {
  fetchDetail()
})

onBeforeUnmount(() => {
  if (reportTimer) {
    clearInterval(reportTimer)
    reportTimer = null
  }
  reportCurrentDuration()
})

function startTiming() {
  startTime.value = Date.now()
  reportTimer = setInterval(() => {
    reportCurrentDuration()
    startTime.value = Date.now()
  }, 30000)
}

function reportCurrentDuration() {
  if (!detail.value || startTime.value <= 0) return
  const elapsed = Math.floor((Date.now() - startTime.value) / 1000)
  if (elapsed <= 0) return
  reportLearning({
    materialId: detail.value.id,
    duration: elapsed,
    completed: false
  }).catch(() => {})
}

async function fetchDetail() {
  const id = route.params.id as string
  loading.value = true
  errorMsg.value = ''
  try {
    const res = await getMaterialDetail(id)
    detail.value = (res.data || res) as MaterialDetail
    startTiming()
    await nextTick()
    bindContentImageClicks()
  } catch (e: any) {
    errorMsg.value = e.message || '素材不存在或无权查看'
  } finally {
    loading.value = false
  }
}

function bindContentImageClicks() {
  if (!contentRef.value) return
  const imgs = contentRef.value.querySelectorAll('img')
  imgs.forEach(img => {
    img.style.cursor = 'pointer'
    if (!img.hasAttribute('data-preview-bound')) {
      img.setAttribute('data-preview-bound', '1')
      img.addEventListener('click', (e) => {
        e.stopPropagation()
        previewImage(img.src)
      })
    }
  })
}

function onContentClick(e: MouseEvent) {
  const target = e.target as HTMLElement
  if (target.tagName === 'IMG') {
    previewImage((target as HTMLImageElement).src)
  }
}

async function previewImage(url: string) {
  preview.type = 'image'
  preview.title = '图片预览'
  preview.url = url
  preview.downloadUrl = url
  preview.loading = true
  preview.visible = true
  preview.blobUrl = await fetchAsBlobUrl(url, true)
  preview.loading = false
}

function previewVideo(url: string) {
  // If the stored URL is an iframe embed code, extract the src
  let cleanedUrl = url
  const iframeSrcMatch = url.match(/<iframe[^>]+src=["']([^"']+)["'][^>]*>/i)
  if (iframeSrcMatch) {
    cleanedUrl = iframeSrcMatch[1]
    if (cleanedUrl.startsWith('//')) {
      cleanedUrl = 'https:' + cleanedUrl
    }
  }
  preview.type = 'video'
  preview.title = '视频预览'
  preview.url = cleanedUrl
  preview.downloadUrl = ''
  preview.blobUrl = ''
  preview.visible = true
}

async function previewAttachment(att: Attachment) {
  const downloadUrl = `/api/v1/files/download/${att.id}`
  const previewUrl = `/api/v1/files/download/${att.id}?download=false`
  const viewable = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg', 'bmp', 'pdf']
  if (viewable.includes(att.fileType.toLowerCase())) {
    preview.type = att.fileType.toLowerCase() === 'pdf' ? 'file' : 'image'
    preview.title = att.filename
    preview.url = previewUrl
    preview.downloadUrl = downloadUrl
    preview.loading = true
    preview.visible = true
    preview.blobUrl = await fetchAsBlobUrl(previewUrl, false)
    preview.loading = false
  } else {
    downloadFile(downloadUrl)
  }
}

async function fetchAsBlobUrl(url: string, isPublic: boolean): Promise<string> {
  try {
    const config: any = { responseType: 'blob' }
    if (!isPublic) {
      const token = localStorage.getItem('token')
      if (token) config.headers = { Authorization: `Bearer ${token}` }
    }
    const res = await axios.get(url, config)
    const blob = res.data instanceof Blob ? res.data : new Blob([res.data])
    return URL.createObjectURL(blob)
  } catch {
    ElMessage.error('文件加载失败')
    return ''
  }
}

function closePreview() {
  if (preview.blobUrl) {
    URL.revokeObjectURL(preview.blobUrl)
    preview.blobUrl = ''
  }
  preview.visible = false
}

function isVideoEmbed(url: string): boolean {
  return /bilibili\.com|youtube\.com|youku\.com|qq\.com/.test(url)
}

function getEmbedUrl(url: string): string {
  // Extract bvid from any B站 format (direct link, player link, iframe src)
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

function downloadFile(url: string) {
  window.open(url, '_blank')
}

async function handleFavorite() {
  if (!detail.value) return
  try {
    const res = await toggleFavorite(detail.value.id)
    const data = res.data || res
    detail.value.isFavorited = data.isFavorited
    detail.value.favoriteCount = data.favoriteCount
    ElMessage.success(data.isFavorited ? '已收藏' : '已取消收藏')
  } catch {
    // handled by interceptor
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

function formatSize(bytes: number): string {
  if (!bytes) return ''
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}
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

.detail-card { padding: 24px; min-height: 400px; }
.empty-hint {
  display: flex; flex-direction: column; align-items: center; gap: 12px;
  padding: 80px 0; color: var(--ink-light); font-size: 14px;
}

.cover-wrap {
  position: relative;
  width: 100%;
  max-height: 400px;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
  background: var(--el-fill-color-light);
  cursor: pointer;
}
.cover-wrap img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}
.cover-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(0,0,0,0);
  color: #fff;
  opacity: 0;
  transition: all 0.3s;
}
.cover-wrap:hover .cover-overlay {
  background: rgba(0,0,0,0.35);
  opacity: 1;
}

.title-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 12px;
}
.detail-title { font-size: 24px; font-weight: 700; color: var(--ink-black); margin: 0; letter-spacing: 1px; }

.detail-author { font-size: 14px; color: var(--ink-gray); margin: 0 0 12px; }

.meta-row {
  display: flex; flex-wrap: wrap; gap: 20px;
  font-size: 13px; color: var(--ink-gray); margin-bottom: 12px;
}
.meta-row span { display: inline-flex; align-items: center; gap: 4px; }

.tag-row { display: flex; flex-wrap: wrap; gap: 8px; margin-bottom: 16px; }

.stats-row {
  display: flex; flex-wrap: wrap; gap: 20px;
  font-size: 13px; color: var(--ink-light);
  padding-bottom: 20px;
  border-bottom: 1px solid var(--el-border-color-light);
  margin-bottom: 24px;
}
.stats-row span { display: inline-flex; align-items: center; gap: 4px; }
.uploader-info { color: var(--ink-gray); }

.content-section { margin-bottom: 24px; }
.section-title {
  font-size: 16px; font-weight: 600; color: var(--ink-black);
  margin: 0 0 12px; padding-left: 10px; border-left: 3px solid var(--accent-seal);
}
.content-body {
  line-height: 1.8;
  color: var(--ink-dark);
  font-size: 14px;
}
.content-body :deep(p) { margin: 8px 0; }
.content-body :deep(img) { max-width: 100%; border-radius: 4px; cursor: pointer; transition: opacity 0.2s; }
.content-body :deep(img:hover) { opacity: 0.85; }

.video-link { color: var(--accent-seal); font-size: 14px; word-break: break-all; cursor: pointer; }
.video-link:hover { text-decoration: underline; }

.kp-list { display: flex; flex-direction: column; gap: 12px; }
.kp-item {
  padding: 12px 16px;
  background: var(--el-fill-color-light);
  border-radius: 6px;
}
.kp-item h4 { font-size: 14px; font-weight: 600; color: var(--ink-black); margin: 0 0 4px; }
.kp-item p { font-size: 13px; color: var(--ink-gray); margin: 0; line-height: 1.6; }

.attachment-list { display: flex; flex-direction: column; gap: 8px; }
.attachment-item {
  display: flex; align-items: center; gap: 8px;
  padding: 10px 14px;
  background: var(--el-fill-color-light);
  border-radius: 6px;
  text-decoration: none;
  color: var(--ink-dark);
  font-size: 13px;
  cursor: pointer;
  transition: background 0.2s;
}
.attachment-item:hover { background: var(--el-fill-color); }
.att-name { flex: 1; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.att-size { color: var(--ink-light); font-size: 12px; flex-shrink: 0; }

/* 预览弹窗 */
.preview-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 60px 0;
  color: var(--ink-light);
}
.preview-body {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  max-height: 70vh;
  overflow: auto;
}
.preview-image {
  max-width: 100%;
  max-height: 65vh;
  object-fit: contain;
  border-radius: 4px;
}
.preview-video-wrap {
  width: 100%;
  aspect-ratio: 16 / 9;
  max-height: 65vh;
}
.preview-video {
  width: 100%;
  height: 100%;
  border-radius: 4px;
}
.preview-iframe {
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 4px;
}
</style>
