<template>
  <div class="admin-layout">
    <el-container style="height: 100vh;">
      <!-- 侧边栏 -->
      <el-aside width="220px" style="background-color: #001529;">
        <div class="logo-box">
          <h3 style="color: #fff; text-align: center; line-height: 60px; margin: 0;">思政学习后台</h3>
        </div>
        <el-menu
          router
          background-color="#001529"
          text-color="#fff"
          active-text-color="#1890ff"
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><House /></el-icon>
            <span>数据总览</span>
          </el-menu-item>
          <el-menu-item index="/admin/material/list">
            <el-icon><Document /></el-icon>
            <span>素材管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/user">
            <el-icon><User /></el-icon>
            <span>用户管理</span>
          </el-menu-item>
          <el-menu-item index="/admin/tag">
            <el-icon><CollectionTag /></el-icon>
            <span>标签管理</span>
          </el-menu-item>
        </el-menu>
      </el-aside>

      <!-- 右侧主区域 -->
      <el-container>
        <!-- 顶部栏 -->
        <el-header style="background: #fff; border-bottom: 1px solid #eee; display: flex; justify-content: flex-end; align-items: center; padding: 0 20px;">
          <span style="margin-right: 16px;">欢迎，{{ userName }}</span>
          <el-button type="text" @click="handleLogout">退出登录</el-button>
        </el-header>

        <!-- 子路由出口 -->
        <el-main style="background: #f5f7fa; padding: 20px;">
          <router-view />
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { House, Document, User, CollectionTag } from '@element-plus/icons-vue'
// 导入登出接口
import { logout } from '@/api/auth'

const router = useRouter()
// 读取本地用户信息
const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const userName = ref(userInfo.nickname || '管理员')

// 正式退出登录
const handleLogout = async () => {
  try {
    await logout()
  } catch (err) {
    console.log('登出接口异常', err)
  }
  // 清空本地存储
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  ElMessage.success('已退出登录')
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}
.logo-box {
  height: 60px;
  background: rgba(255,255,255,0.1);
}
.el-header {
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}
</style>