<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">关于系统</h1>
      <p class="page-desc">新时代中国传统优秀文化成就思政素材智能检索学习系统V1.0</p>
    </header>

    <div class="card about-card">
      <div class="seal-large">文</div>
      <h2 class="sys-name">新时代中国传统优秀文化成就思政素材智能检索学习系统V1.0</h2>
      <p class="sys-ver">V1.0</p>
      <p class="sys-desc">
        本系统面向大中小学思政教育，收录中华优秀传统文化素材（诸子文化、传统非遗、民俗、传统技艺、红色传统文化、历代人文典故），为教师备课和学生自主学习提供智能检索、分类筛选、在线学习和课件导出服务。
      </p>
    </div>
  </div>
</template>

<style scoped>
.page { max-width: 640px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.page-desc { margin-top: 6px; color: var(--ink-gray); font-size: 13px; }

.about-card { padding: 40px 24px; text-align: center; }
.seal-large {
  width: 64px; height: 64px; margin: 0 auto 16px;
  border: 3px solid var(--accent-seal); border-radius: 6px;
  display: flex; align-items: center; justify-content: center;
  font-size: 32px; font-weight: 700; color: var(--accent-seal);
  transform: rotate(-5deg);
}
.sys-name { font-size: 18px; font-weight: 600; color: var(--ink-black); letter-spacing: 2px; }
.sys-ver { margin-top: 4px; font-size: 12px; color: var(--ink-light); }
.sys-desc { margin-top: 16px; font-size: 13px; color: var(--ink-gray); line-height: 1.8; text-align: left; text-indent: 2em; }
</style>
