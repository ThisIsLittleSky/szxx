<template>
  <div class="login-container">
    <el-card class="login-card">
      <h2>思政素材智能检索系统 - 管理员登录</h2>
      <el-form 
        :model="form" 
        :rules="rules" 
        ref="formRef" 
        @submit.prevent="handleLogin"
      >
        <el-form-item prop="username">
          <el-input 
            v-model="form.username" 
            placeholder="请输入用户名" 
            size="large"
          />
        </el-form-item>
        <el-form-item prop="password">
          <el-input 
            v-model="form.password" 
            type="password" 
            placeholder="请输入密码" 
            show-password
            size="large"
          />
        </el-form-item>
        <el-form-item>
          <el-button 
            type="primary" 
            native-type="submit" 
            :loading="loading" 
            style="width:100%"
            size="large"
          >
            登录
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
// 导入登录接口
import { login, LoginParams } from '@/api/auth'

const router = useRouter()
const formRef = ref()
const loading = ref(false)

// 登录表单
const form = reactive<LoginParams>({
  username: '',
  password: ''
})

// 表单校验
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

// 正式登录逻辑
const handleLogin = async () => {
  if (!formRef.value) return
  let valid = false
  try {
    await formRef.value.validate()
    valid = true
  } catch {
    return
  }
  if (!valid) return

  loading.value = true
  try {
    // 调用登录接口
    const res = await login(form)
    // 存储 Token + 用户信息（持久化）
    localStorage.setItem('token', res.data.token)
    localStorage.setItem('userInfo', JSON.stringify(res.data.user))
    ElMessage.success('登录成功')
    // 跳转到后台首页
    router.push('/admin/dashboard')
  } catch (err) {
    console.error('登录失败', err)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-container {
  display: flex; 
  justify-content: center; 
  align-items: center;
  min-height: 100vh; 
  background: #f0f2f5;
}
.login-card { 
  width: 400px; 
  padding: 24px;
}
.login-card h2 { 
  text-align: center; 
  margin-bottom: 24px; 
  color: #303133; 
}
</style>