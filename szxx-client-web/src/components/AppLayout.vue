<template>
  <div class="layout">
    <nav class="nav" :class="{ scrolled }">
      <a href="#home" class="nav-logo" @click.prevent="scrollTo('#home')">
        <div class="seal">文</div>
        新时代中国传统优秀文化成就思政素材智能检索学习系统V1.0
      </a>
      <ul class="nav-links">
        <li><a href="#home" @click.prevent="scrollTo('#home')">首页</a></li>
        <li><a href="#materials" @click.prevent="scrollTo('#materials')">素材总览</a></li>
        <li><a href="#features" @click.prevent="scrollTo('#features')">功能介绍</a></li>
        <li><a href="#about" @click.prevent="scrollTo('#about')">关于我们</a></li>
      </ul>
      <div class="nav-auth">
        <router-link to="/login" class="btn-outline">登录</router-link>
        <router-link to="/register" class="btn-filled">注册</router-link>
      </div>
    </nav>

    <main class="main">
      <router-view />
    </main>

    <footer class="footer">
      <p>新时代中国传统优秀文化成就思政素材智能检索学习系统V1.0 &copy; 2026</p>
    </footer>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted } from 'vue'

const scrolled = ref(false)

function scrollTo(selector: string) {
  document.querySelector(selector)?.scrollIntoView({ behavior: 'smooth' })
}

function onScroll() {
  scrolled.value = window.scrollY > 50
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
  padding: 16px 60px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: linear-gradient(180deg, rgba(245, 240, 232, 0.95) 60%, rgba(245, 240, 232, 0) 100%);
  transition: all 0.3s;
}
.nav.scrolled {
  background: rgba(245, 240, 232, 0.98);
  box-shadow: 0 1px 20px rgba(0, 0, 0, 0.06);
}

.nav-logo {
  font-family: var(--font-calligraphy);
  font-size: 18px;
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
  width: 36px;
  height: 36px;
  border: 2px solid var(--accent-seal);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-family: var(--font-body);
  font-size: 20px;
  font-weight: 700;
  color: var(--accent-seal);
  transform: rotate(-5deg);
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
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 1.5px;
  background: var(--ink-black);
  transition: width 0.4s var(--ease-ink);
}
.nav-links a:hover {
  color: var(--ink-black);
}
.nav-links a:hover::after {
  width: 100%;
}
.nav-links a.router-link-active {
  color: var(--ink-black);
}
.nav-links a.router-link-active::after {
  width: 100%;
}

.nav-auth {
  display: flex;
  gap: 12px;
}
.btn-outline,
.btn-filled {
  display: inline-flex;
  align-items: center;
  padding: 8px 24px;
  font-size: 14px;
  letter-spacing: 1px;
  border-radius: 2px;
  transition: all 0.3s;
  cursor: pointer;
  font-family: inherit;
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
    padding: 12px 24px;
  }
  .nav-links {
    display: none;
  }
  .footer {
    padding: 20px;
  }
}
</style>
