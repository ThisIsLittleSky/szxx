<template>
  <div class="layout">
    <nav class="nav" :class="{ scrolled }">
      <router-link to="/" class="nav-logo">
        <div class="seal">文</div>
        思政素材
      </router-link>
      <ul class="nav-links">
        <li><router-link to="/">首页</router-link></li>
        <li><a href="#">素材浏览</a></li>
        <li><a href="#">智能检索</a></li>
        <li><a href="#">学习记录</a></li>
      </ul>
      <div class="nav-auth">
        <router-link to="/login" class="btn-outline">登录</router-link>
        <router-link to="/register" class="btn-filled">注册</router-link>
      </div>
    </nav>

    <main class="main">
      <slot />
    </main>

    <footer class="footer">
      <p>新时代中国传统优秀文化成就思政素材智能检索学习系统 &copy; 2025</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const scrolled = ref(false)

function onScroll() {
  scrolled.value = window.scrollY > 40
}

onMounted(() => window.addEventListener('scroll', onScroll, { passive: true }))
onUnmounted(() => window.removeEventListener('scroll', onScroll))
</script>

<style scoped>
.layout {
  position: relative;
  z-index: 1;
}

/* ---- 导航 ---- */
.nav {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  height: 64px;
  padding: 0 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(180deg, rgba(245, 240, 232, 0.94) 60%, rgba(245, 240, 232, 0) 100%);
  transition: all 0.35s var(--ease-ink);
}
.nav.scrolled {
  background: rgba(245, 240, 232, 0.97);
  box-shadow: 0 1px 20px rgba(0, 0, 0, 0.05);
}

.nav-logo {
  font-family: var(--font-calligraphy);
  font-size: 26px;
  color: var(--ink-black);
  display: flex;
  align-items: center;
  gap: 10px;
  letter-spacing: 2px;
  transition: color 0.3s;
}
.nav-logo:hover {
  color: var(--accent-seal);
}
.seal {
  width: 34px;
  height: 34px;
  border: 2px solid var(--accent-seal);
  border-radius: 3px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-body);
  font-size: 18px;
  font-weight: 700;
  color: var(--accent-seal);
  transform: rotate(-3deg);
}

.nav-links {
  display: flex;
  gap: 32px;
}
.nav-links a {
  font-size: 14px;
  color: var(--ink-gray);
  position: relative;
  padding: 4px 0;
  letter-spacing: 1px;
  transition: color 0.3s;
}
.nav-links a::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 1.5px;
  background: var(--ink-black);
  transform: scaleX(0);
  transform-origin: right;
  transition: transform 0.45s var(--ease-ink);
}
.nav-links a:hover {
  color: var(--ink-black);
}
.nav-links a:hover::after {
  transform: scaleX(1);
  transform-origin: left;
}
.nav-links a.router-link-active {
  color: var(--ink-black);
}
.nav-links a.router-link-active::after {
  transform: scaleX(1);
}

.nav-auth {
  display: flex;
  gap: 12px;
}
.btn-outline,
.btn-filled {
  padding: 7px 22px;
  font-size: 13px;
  letter-spacing: 1px;
  border-radius: 2px;
  transition: all 0.3s;
}
.btn-outline {
  border: 1px solid var(--ink-dark);
  color: var(--ink-dark);
  background: transparent;
}
.btn-outline:hover {
  background: var(--ink-dark);
  color: var(--paper);
}
.btn-filled {
  border: none;
  background: var(--ink-dark);
  color: var(--paper);
}
.btn-filled:hover {
  background: var(--accent-seal);
}

/* ---- 主体 ---- */
.main {
  min-height: 100vh;
}

/* ---- 页脚 ---- */
.footer {
  text-align: center;
  padding: 28px 40px;
  border-top: 1px solid var(--ink-wash);
  color: var(--ink-light);
  font-size: 12px;
  letter-spacing: 2px;
}

@media (max-width: 768px) {
  .nav {
    padding: 0 20px;
    height: 56px;
  }
  .nav-links {
    display: none;
  }
  .footer {
    padding: 20px;
  }
}
</style>
