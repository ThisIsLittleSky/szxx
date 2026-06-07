<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">上传素材</h1>
      <p class="page-desc">上传 Word/PDF 文档，系统将自动解析图文内容入库</p>
    </header>

    <div class="card upload-card">
      <el-upload
        drag
        multiple
        :limit="10"
        accept=".doc,.docx,.pdf"
        action="/api/v1/files/upload/image"
        :headers="uploadHeaders"
      >
        <el-icon :size="48" color="var(--ink-wash)"><Upload /></el-icon>
        <div class="upload-text">
          <p class="upload-hint">将 Word 或 PDF 文件拖到此处</p>
          <p class="upload-sub">支持 .doc .docx .pdf，单次最多10个文件</p>
        </div>
      </el-upload>
    </div>

    <div class="card manual-card" style="margin-top: 20px;">
      <h3 class="card-title">手动录入</h3>
      <el-form label-width="80px" class="upload-form">
        <el-form-item label="标题">
          <el-input placeholder="素材标题" />
        </el-form-item>
        <el-form-item label="作者/来源">
          <el-input placeholder="作者或来源" />
        </el-form-item>
        <el-form-item label="朝代">
          <el-select placeholder="选择朝代" style="width:100%">
            <el-option v-for="d in dynasties" :key="d" :label="d" :value="d" />
          </el-select>
        </el-form-item>
        <el-form-item label="文化品类">
          <el-select placeholder="选择分类" style="width:100%">
            <el-option v-for="c in categories" :key="c" :label="c" :value="c" />
          </el-select>
        </el-form-item>
        <el-form-item label="思政学段">
          <el-select placeholder="选择学段" style="width:100%">
            <el-option v-for="l in levels" :key="l" :label="l" :value="l" />
          </el-select>
        </el-form-item>
        <el-form-item label="标签">
          <el-input placeholder="请输入标签，用逗号分隔" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary">提交素材</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Upload } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const uploadHeaders = computed(() => ({
  Authorization: `Bearer ${userStore.token}`
}))

const dynasties = ['先秦', '秦汉', '魏晋南北朝', '隋唐', '宋', '元', '明', '清', '近现代']
const categories = ['诸子文化', '传统非遗', '民俗文化', '传统技艺', '红色传统文化', '人文典故', '诗词歌赋', '古代科技']
const levels = ['小学(1-3年级)', '小学(4-6年级)', '初中', '高中', '大学']
</script>

<style scoped>
.page { max-width: 800px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.page-desc { margin-top: 6px; color: var(--ink-gray); font-size: 13px; }

.upload-card { padding: 20px; }
.upload-text { margin-top: 12px; text-align: center; }
.upload-hint { font-size: 14px; color: var(--ink-dark); }
.upload-sub { margin-top: 4px; font-size: 12px; color: var(--ink-light); }

.manual-card { padding: 24px; }
.card-title { font-size: 15px; font-weight: 600; color: var(--ink-black); margin-bottom: 16px; letter-spacing: 1px; }
.upload-form { max-width: 480px; }
</style>
