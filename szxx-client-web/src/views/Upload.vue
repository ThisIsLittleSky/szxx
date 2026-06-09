<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">上传素材</h1>
      <p class="page-desc">上传图文素材或视频链接，丰富思政教学资源库</p>
    </header>

    <div class="card upload-card">
      <el-tabs v-model="uploadType" class="upload-tabs">
        <el-tab-pane label="上传图文" name="image">
          <el-upload
            ref="uploadRef"
            drag
            multiple
            :limit="10"
            :auto-upload="false"
            :show-file-list="false"
            accept=".jpg,.jpeg,.png,.gif,.webp,.doc,.docx,.pdf"
            :file-list="fileList"
            @change="onFileChange"
          >
            <el-icon :size="48" color="var(--ink-wash)"><Upload /></el-icon>
            <div class="upload-text">
              <p class="upload-hint">将图文文件拖到此处，或点击上传</p>
              <p class="upload-sub">支持 jpg / png / gif / webp / doc / docx / pdf，单次最多10个文件</p>
            </div>
          </el-upload>

          <div v-if="previewItems.length > 0" class="preview-panel">
            <h4 class="preview-title">文件预览</h4>
            <div class="preview-grid">
              <div
                v-for="(item, i) in previewItems"
                :key="i"
                class="preview-item"
              >
                <img
                  v-if="item.isImage"
                  :src="item.url"
                  :alt="item.name"
                  class="preview-img"
                  @click="previewVisible = true; previewSrc = item.url"
                />
                <div v-else class="preview-file-icon">
                  <el-icon :size="40"><Document /></el-icon>
                  <span class="preview-file-ext">{{ item.ext }}</span>
                </div>
                <p class="preview-name" :title="item.name">{{ item.name }}</p>
                <p class="preview-size">{{ item.sizeText }}</p>
                <el-button
                  type="danger"
                  size="small"
                  circle
                  class="preview-remove"
                  @click="removeFile(i)"
                >
                  <el-icon><Close /></el-icon>
                </el-button>
              </div>
            </div>
          </div>

          <el-dialog v-model="previewVisible" :show-close="false" width="auto" class="preview-dialog">
            <img :src="previewSrc" class="preview-full" @click="previewVisible = false" />
          </el-dialog>
        </el-tab-pane>
        <el-tab-pane label="上传视频URL" name="video">
          <div class="video-url-input">
            <el-input
              v-model="videoUrl"
              placeholder="请输入视频链接地址（如 B站、优酷等平台视频链接）"
              clearable
            >
              <template #prefix>
                <el-icon><Link /></el-icon>
              </template>
            </el-input>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>

    <div class="card info-card">
      <h3 class="card-title">素材信息</h3>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px" class="upload-form">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入素材标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者" maxlength="50" />
        </el-form-item>
        <el-form-item label="来源" prop="source">
          <el-input v-model="form.source" placeholder="请输入来源" maxlength="100" />
        </el-form-item>
        <el-form-item label="朝代" prop="dynasty">
          <el-select v-model="form.dynasty" placeholder="选择朝代" style="width:100%">
            <el-option v-for="d in dynasties" :key="d" :label="d" :value="d" />
          </el-select>
        </el-form-item>
        <el-form-item label="文化品类" prop="category">
          <el-select v-model="form.category" placeholder="选择分类" style="width:100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="思政学段" prop="level">
          <el-select v-model="form.level" placeholder="选择学段" style="width:100%">
            <el-option v-for="l in levels" :key="l" :label="l" :value="l" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <div class="tag-input-area">
            <div class="tag-input-row">
              <el-input
                v-model="tagInput"
                placeholder="输入标签（6字以内）"
                maxlength="6"
                class="tag-input"
                @keyup.enter="addTag"
              />
              <el-button @click="addTag" :disabled="!tagInput.trim()">确定</el-button>
            </div>
            <div class="tag-list" v-if="form.tags.length > 0">
              <el-tag
                v-for="(tag, i) in form.tags"
                :key="i"
                closable
                class="tag-item"
                @close="removeTag(i)"
              >
                {{ tag }}
              </el-tag>
            </div>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="submitForm" :loading="submitting">
            提交素材
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { Upload, Link, Document, Close } from '@element-plus/icons-vue'
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import { ElMessage } from 'element-plus'
import { useUserStore } from '../stores/user'
import { createMaterial } from '../api/materials'

const userStore = useUserStore()

const uploadType = ref<'image' | 'video'>('image')
const uploadRef = ref()
const formRef = ref<FormInstance>()
const fileList = ref<UploadFile[]>([])
const videoUrl = ref('')
const tagInput = ref('')
const submitting = ref(false)
const previewVisible = ref(false)
const previewSrc = ref('')

const IMAGE_TYPES = ['jpg', 'jpeg', 'png', 'gif', 'webp']

interface PreviewItem {
  url: string
  name: string
  sizeText: string
  isImage: boolean
  ext: string
}

const previewItems = reactive<PreviewItem[]>([])

function onFileChange(_file: UploadFile, changedFileList: UploadFile[]) {
  fileList.value = changedFileList
  syncPreviewItemsFromList(changedFileList)
}

function syncPreviewItemsFromList(list: UploadFile[]) {
  // revoke old blob URLs
  previewItems.forEach((item) => {
    if (item.isImage && item.url.startsWith('blob:')) URL.revokeObjectURL(item.url)
  })
  previewItems.length = 0

  list.forEach((file) => {
    const ext = file.name.split('.').pop()?.toLowerCase() || ''
    const isImage = IMAGE_TYPES.includes(ext)
    // Element Plus may create an object URL (file.url); also try file.raw
    let url = file.url || ''
    if (!url && file.raw) url = URL.createObjectURL(file.raw)
    const item: PreviewItem = {
      url,
      name: file.name,
      sizeText: formatFileSize(file.size || 0),
      isImage,
      ext: ext.toUpperCase() || 'FILE',
    }
    previewItems.push(item)
  })
}

const form = reactive({
  title: '',
  author: '',
  source: '',
  dynasty: '',
  category: '',
  level: '',
  tags: [] as string[]
})

const rules: FormRules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  source: [{ required: true, message: '请输入来源', trigger: 'blur' }],
  dynasty: [{ required: true, message: '请选择朝代', trigger: 'change' }],
  category: [{ required: true, message: '请选择文化品类', trigger: 'change' }],
  level: [{ required: true, message: '请选择思政学段', trigger: 'change' }],
}

const dynasties = ['先秦', '秦汉', '魏晋南北朝', '隋唐', '宋', '元', '明', '清', '近现代']
const categories = ['诸子文化', '传统非遗', '民俗文化', '传统技艺', '红色传统文化', '人文典故', '诗词歌赋', '古代科技']
const levels = ['小学(1-3年级)', '小学(4-6年级)', '初中', '高中', '大学']

function formatFileSize(bytes: number): string {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(1) + ' MB'
}

function removeFile(index: number) {
  fileList.value.splice(index, 1)
  syncPreviewItemsFromList(fileList.value)
}

function addTag() {
  const text = tagInput.value.trim()
  if (!text) return
  if (form.tags.includes(text)) {
    ElMessage.warning('标签已存在')
    return
  }
  if (form.tags.length >= 10) {
    ElMessage.warning('最多添加10个标签')
    return
  }
  form.tags.push(text)
  tagInput.value = ''
}

function removeTag(index: number) {
  form.tags.splice(index, 1)
}

async function submitForm() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return

  if (uploadType.value === 'image' && fileList.value.length === 0) {
    ElMessage.warning('请选择要上传的图文文件')
    return
  }
  if (uploadType.value === 'video' && !videoUrl.value.trim()) {
    ElMessage.warning('请输入视频链接')
    return
  }

  submitting.value = true
  try {
    const fd = new FormData()
    fd.append('title', form.title)
    fd.append('author', form.author)
    fd.append('dynasty', form.dynasty)
    fd.append('category', form.category)
    fd.append('educationLevel', form.level)
    if (form.source) fd.append('content', form.source)
    if (form.tags.length > 0) fd.append('tags', form.tags.join(','))
    if (uploadType.value === 'video') {
      fd.append('videoUrl', videoUrl.value.trim())
    }
    if (uploadType.value === 'image') {
      fileList.value.forEach((file, i) => {
        if (i === 0 && isImageExt(file.name)) {
          fd.append('coverImage', file.raw!)
        }
        fd.append('attachments', file.raw!)
      })
    }

    await createMaterial(fd)

    ElMessage.success('素材提交成功')
    formRef.value?.resetFields()
    form.tags = []
    fileList.value = []
    previewItems.forEach((item) => {
      if (item.isImage && item.url.startsWith('blob:')) URL.revokeObjectURL(item.url)
    })
    previewItems.length = 0
    videoUrl.value = ''
    uploadRef.value?.clearFiles()
  } catch (e: any) {
    ElMessage.error(e.message || '提交失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

function isImageExt(filename: string): boolean {
  const ext = filename.split('.').pop()?.toLowerCase() || ''
  return IMAGE_TYPES.includes(ext)
}
</script>

<style scoped>
.page { max-width: 800px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.page-desc { margin-top: 6px; color: var(--ink-gray); font-size: 13px; }

.upload-card { padding: 4px 20px 20px; }
.upload-tabs { margin-top: 0; }

.upload-text { margin-top: 12px; text-align: center; }
.upload-hint { font-size: 14px; color: var(--ink-dark); }
.upload-sub { margin-top: 4px; font-size: 12px; color: var(--ink-light); }

.video-url-input { padding: 32px 0; }

.info-card { padding: 24px; margin-top: 20px; }
.card-title { font-size: 15px; font-weight: 600; color: var(--ink-black); margin-bottom: 16px; letter-spacing: 1px; }
.upload-form { max-width: 520px; }

.tag-input-area { width: 100%; }
.tag-input-row { display: flex; gap: 8px; }
.tag-input { flex: 1; }
.tag-list { display: flex; flex-wrap: wrap; gap: 8px; margin-top: 10px; }
.tag-item { cursor: default; }

.preview-panel { margin-top: 20px; }
.preview-title { font-size: 13px; font-weight: 600; color: var(--ink-dark); margin-bottom: 10px; }
.preview-grid { display: flex; flex-wrap: wrap; gap: 12px; }
.preview-item {
  width: 112px;
  text-align: center;
  position: relative;
}
.preview-img {
  width: 112px;
  height: 112px;
  object-fit: cover;
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);
  cursor: pointer;
  transition: transform 0.2s;
}
.preview-img:hover { transform: scale(1.05); }
.preview-file-icon {
  width: 112px;
  height: 112px;
  border-radius: 8px;
  border: 1px solid var(--el-border-color-light);
  background: var(--el-fill-color-light);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--ink-gray);
}
.preview-file-ext {
  margin-top: 4px;
  font-size: 11px;
  font-weight: 600;
  color: var(--ink-light);
}
.preview-name {
  margin-top: 6px;
  font-size: 11px;
  color: var(--ink-dark);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}
.preview-size {
  font-size: 10px;
  color: var(--ink-light);
}
.preview-remove {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 20px;
  height: 20px;
  font-size: 10px;
}
.preview-dialog { display: flex; align-items: center; justify-content: center; }
.preview-dialog :deep(.el-dialog__body) { padding: 0; }
.preview-full { max-width: 85vw; max-height: 85vh; object-fit: contain; cursor: zoom-out; }
</style>
