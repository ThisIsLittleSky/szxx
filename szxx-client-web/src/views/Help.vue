<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">使用帮助</h1>
      <p class="page-desc">快速了解系统功能与操作方法</p>
    </header>

    <div class="card help-card">
      <div class="help-section" v-for="s in sections" :key="s.title">
        <h3>{{ s.title }}</h3>
        <p>{{ s.content }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
const sections = [
  { title: '关键词检索', content: '在搜索框中输入关键词，系统将自动进行中文分词，对素材标题、标签、正文进行全文检索。支持多关键词组合搜索。' },
  { title: '分类筛选', content: '通过朝代、文化品类、思政学段三个维度组合筛选素材，快速定位到感兴趣的传统文化内容。' },
  { title: '素材学习', content: '点击素材卡片进入详情页，可在线阅读图文内容、观看微课视频、查看知识点解析。系统将自动记录学习进度。' },
  { title: '收藏管理', content: '学习过程中可收藏感兴趣的素材，在收藏夹中统一管理，支持分类查看和批量删除。' },
  { title: '素材上传（教师）', content: '教师用户可上传 Word/PDF 文档，系统将自动解析图文内容。上传后需等待管理员审核通过方可公开。' },
  { title: '课件导出（教师）', content: '勾选多条素材后可导出为 Word 文档，便于备课和课堂教学使用。' }
]
</script>

<style scoped>
.page { max-width: 720px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.page-desc { margin-top: 6px; color: var(--ink-gray); font-size: 13px; }

.help-card { padding: 24px; }
.help-section { margin-bottom: 20px; }
.help-section:last-child { margin-bottom: 0; }
.help-section h3 { font-size: 15px; font-weight: 600; color: var(--ink-black); margin-bottom: 6px; letter-spacing: 1px; }
.help-section p { font-size: 13px; color: var(--ink-gray); line-height: 1.8; }
</style>
