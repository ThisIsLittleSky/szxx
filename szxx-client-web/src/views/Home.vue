<template>
  <div class="home">
    <h1>欢迎使用思政学习系统</h1>
    <p>新时代中国传统优秀文化成就思政素材智能检索学习系统 V1.0</p>
    <el-button type="primary" @click="handleLogout">退出登录</el-button>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()

function handleLogout() {
  userStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.home { text-align: center; padding-top: 100px; }
.home h1 { color: #303133; margin-bottom: 12px; }
.home p { color: #909399; margin-bottom: 24px; }
</style>
