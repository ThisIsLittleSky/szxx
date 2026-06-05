<template>
  <div class="dashboard">
    <h1>管理后台</h1>
    <p>数据仪表盘（待实现）</p>
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
.dashboard { text-align: center; padding-top: 100px; }
</style>
