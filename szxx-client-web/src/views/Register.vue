<template>
  <div class="auth-container">
    <div class="ink-decor">
      <div class="ink-splash" style="width:450px;height:220px;top:-60px;right:-100px;"></div>
      <div class="ink-splash" style="width:380px;height:200px;bottom:-70px;left:-90px;animation-delay:-5s;"></div>
    </div>

    <div class="auth-card">
      <h2>创建账号</h2>
      <p class="subtitle">加入我们，开启传统文化学习之旅</p>

      <el-form :model="form" :rules="rules" ref="formRef" @submit.prevent="handleRegister">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名（3-20位字母、数字和下划线）" size="large" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="密码（6-20位字母和数字）" show-password size="large" />
        </el-form-item>
        <el-form-item prop="nickname">
          <el-input v-model="form.nickname" placeholder="昵称" size="large" />
        </el-form-item>
        <el-form-item prop="role">
          <el-select v-model="form.role" placeholder="选择角色" size="large" style="width:100%">
            <el-option label="学生" value="student" />
            <el-option label="教师" value="teacher" />
          </el-select>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="邮箱（选填）" size="large" />
        </el-form-item>
        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="手机号（选填）" size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" native-type="submit" :loading="loading" size="large" class="submit-btn">注 册</el-button>
        </el-form-item>
      </el-form>

      <p class="tip">已有账号？<router-link to="/login">立即登录</router-link></p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '../api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  nickname: '',
  role: '',
  email: '',
  phone: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名为3-20位', trigger: 'blur' },
    { pattern: /^[a-zA-Z0-9_]+$/, message: '用户名只能包含字母、数字和下划线', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码为6-20位', trigger: 'blur' },
    { pattern: /^(?=.*[a-zA-Z])(?=.*\d)/, message: '密码需包含字母和数字', trigger: 'blur' }
  ],
  nickname: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

async function handleRegister() {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    await register(form)
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
    // 错误已在 request 拦截器中处理
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
  opacity: 0.06;
  background: radial-gradient(ellipse at center, #000 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(20px);
  animation: inkBreathe 8s ease-in-out infinite;
}
@keyframes inkBreathe {
  0%, 100% { transform: scale(1) rotate(0deg); opacity: 0.05; }
  33%      { transform: scale(1.15) rotate(1deg); opacity: 0.08; }
  66%      { transform: scale(0.95) rotate(-1deg); opacity: 0.04; }
}

.auth-card {
  position: relative;
  z-index: 2;
  width: 440px;
  background: var(--paper-white);
  border: 1px solid var(--ink-wash);
  border-radius: 2px;
  padding: 42px 40px 32px;
  box-shadow: var(--shadow-md);
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
  margin-bottom: 28px;
}

.submit-btn {
  width: 100%;
  letter-spacing: 4px;
}

.tip {
  text-align: center;
  font-size: 13px;
  color: var(--ink-light);
  margin-top: 4px;
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
