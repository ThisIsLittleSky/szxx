<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">忘记密码</h1>
      <p class="page-desc">通过注册邮箱找回密码</p>
    </header>

    <div class="card form-card">
      <el-form label-width="80px">
        <el-form-item label="用户名">
          <el-input placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input placeholder="请输入注册邮箱" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary">发送重置链接</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<style scoped>
.page { max-width: 480px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.page-desc { margin-top: 6px; color: var(--ink-gray); font-size: 13px; }

.form-card { padding: 24px; }
</style>
