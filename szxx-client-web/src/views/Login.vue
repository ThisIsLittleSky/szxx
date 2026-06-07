<template>
  <div class="auth-container">
    <div class="ink-decor">
      <div class="ink-splash" style="width:500px;height:250px;top:-80px;left:-120px;"></div>
      <div class="ink-splash" style="width:350px;height:180px;bottom:-60px;right:-80px;animation-delay:-4s;"></div>
    </div>

    <div class="auth-card">
      <router-link to="/" class="auth-logo">
        <div class="seal">文</div>
        思政素材
      </router-link>
      <h2>欢迎回来</h2>
      <p class="subtitle">登录以继续您的学习之旅</p>

      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleLogin">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名" prefix-icon="User" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码" prefix-icon="Lock" show-password size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" size="large" class="submit-btn">登 录</el-button>
        </el-form-item>
      </el-form>

      <p class="tip">还没有账号？<router-link to="/register">立即注册</router-link></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '../api/auth'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  const valid = await formRef.value?.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    const res: any = await login(form)
    userStore.setAuth(res.data.token, res.data.user)
    ElMessage.success('登录成功')
    router.push('/')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.auth-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  position: relative;
  overflow: hidden;
}

.ink-decor {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}
.ink-splash {
  position: absolute;
  opacity: 0.05;
  background: radial-gradient(ellipse at center, #000 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(40px);
  animation: inkBreathe 10s ease-in-out infinite;
}
@keyframes inkBreathe {
  0%, 100% { transform: scale(1); opacity: 0.04; }
  50%      { transform: scale(1.1); opacity: 0.07; }
}

.auth-card {
  position: relative;
  z-index: 2;
  width: 400px;
  background: var(--paper-white);
  border: 1px solid var(--ink-wash);
  border-radius: 2px;
  padding: 48px 40px 36px;
  box-shadow: var(--shadow-md);
}

.auth-logo {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-family: var(--font-calligraphy);
  font-size: 28px;
  color: var(--ink-black);
  letter-spacing: 2px;
  margin-bottom: 8px;
  transition: color 0.3s;
}
.auth-logo:hover {
  color: var(--accent-seal);
}
.auth-logo .seal {
  width: 32px;
  height: 32px;
  border: 2px solid var(--accent-seal);
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-body);
  font-size: 16px;
  font-weight: 700;
  color: var(--accent-seal);
  transform: rotate(-3deg);
}

h2 {
  text-align: center;
  font-size: 22px;
  font-weight: 700;
  color: var(--ink-black);
  letter-spacing: 2px;
  margin-bottom: 4px;
}
.subtitle {
  text-align: center;
  font-size: 13px;
  color: var(--ink-gray);
  margin-bottom: 32px;
}

.submit-btn {
  width: 100%;
  letter-spacing: 4px;
}

.tip {
  text-align: center;
  font-size: 13px;
  color: var(--ink-light);
  margin-top: 8px;
}
.tip a {
  color: var(--accent-seal);
  transition: color 0.3s;
  margin-left: 4px;
}
.tip a:hover {
  color: var(--accent-seal-dark);
}
</style>
