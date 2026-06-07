<template>
  <div class="page fade-up visible">
    <header class="page-header">
      <h1 class="page-title">个人中心</h1>
      <p class="page-desc">管理个人资料与账号设置</p>
    </header>

    <div class="card profile-card">
      <div class="profile-header">
        <el-avatar :size="64" :src="user?.avatar" class="avatar">
          {{ user?.nickname?.charAt(0) || '用' }}
        </el-avatar>
        <div class="profile-info">
          <h2 class="nickname">{{ user?.nickname || '用户' }}</h2>
          <p class="role-badge">
            <el-tag :type="user?.role === 'teacher' ? 'warning' : ''" size="small">
              {{ user?.role === 'teacher' ? '教师' : '学生' }}
            </el-tag>
          </p>
        </div>
      </div>

      <el-divider />

      <el-form label-width="80px" class="profile-form">
        <el-form-item label="用户名">
          <el-input :model-value="user?.username" disabled />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input :model-value="user?.nickname" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input placeholder="未设置" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input placeholder="未设置" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary">保存修改</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="card profile-card" style="margin-top: 16px;">
      <h3 style="font-size:15px;font-weight:600;margin-bottom:16px;color:var(--ink-black);">账号安全</h3>
      <el-button plain>修改密码</el-button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useUserStore } from '../stores/user'

const userStore = useUserStore()
const user = computed(() => userStore.user)
</script>

<style scoped>
.page { max-width: 640px; margin: 0 auto; }
.page-header { margin-bottom: 24px; }
.page-title { font-size: 22px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.page-desc { margin-top: 6px; color: var(--ink-gray); font-size: 13px; }

.profile-card { padding: 24px; }
.profile-header { display: flex; align-items: center; gap: 16px; }
.avatar { background: var(--ink-dark); color: var(--paper-white); font-family: var(--font-calligraphy); font-size: 24px; }
.nickname { font-size: 18px; font-weight: 600; color: var(--ink-black); }
.role-badge { margin-top: 4px; }

.profile-form { margin-top: 8px; }
</style>
