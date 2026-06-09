<template>
  <div class="ink-shell">
    <div class="paper-texture"></div>
    <div class="ink-splash" style="width:600px;height:300px;top:5%;left:-8%;"></div>
    <div class="ink-splash" style="width:400px;height:200px;bottom:15%;right:-5%;animation-delay:-4s;"></div>
    <div class="ink-cursor" ref="cursorRef"></div>
    <router-view />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import { useRouter } from 'vue-router'

const cursorRef = ref<HTMLElement>()
const router = useRouter()

function onMouseMove(e: MouseEvent) {
  if (!cursorRef.value) return
  cursorRef.value.style.left = e.clientX + 'px'
  cursorRef.value.style.top = e.clientY + 'px'
}

let observer: IntersectionObserver | null = null

function observeFadeUps() {
  document.querySelectorAll('.fade-up').forEach(el => {
    if (!(el as HTMLElement).dataset.observed) {
      (el as HTMLElement).dataset.observed = '1'
      observer?.observe(el)
    }
  })
}

onMounted(() => {
  document.addEventListener('mousemove', onMouseMove)

  // 入场动画
  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible')
      }
    })
  }, { threshold: 0.15, rootMargin: '0px 0px -50px 0px' })

  observeFadeUps()

  // 路由切换后重新观察（处理懒加载组件）
  watch(() => router.currentRoute.value.path, () => {
    nextTick(() => {
      setTimeout(observeFadeUps, 50)
    })
  })
})

onUnmounted(() => {
  document.removeEventListener('mousemove', onMouseMove)
  observer?.disconnect()
})
</script>

<style>
html {
  scroll-behavior: smooth;
}

.ink-shell {
  position: relative;
  min-height: 100vh;
}

/* 纸纹背景 */
.paper-texture {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 0;
  background:
    repeating-linear-gradient(
      0deg,
      transparent,
      transparent 2px,
      rgba(139, 115, 85, 0.015) 2px,
      rgba(139, 115, 85, 0.015) 4px
    ),
    radial-gradient(ellipse at 20% 80%, rgba(139, 115, 85, 0.04) 0%, transparent 50%),
    radial-gradient(ellipse at 80% 20%, rgba(139, 115, 85, 0.03) 0%, transparent 50%);
}

/* 墨韵装饰 */
.ink-splash {
  position: fixed;
  pointer-events: none;
  z-index: 0;
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

/* 墨点光标 */
.ink-cursor {
  position: fixed;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  pointer-events: none;
  z-index: 9999;
  background: radial-gradient(circle at center, rgba(0, 0, 0, 0.08) 0%, transparent 70%);
  transform: translate(-50%, -50%);
  transition: width 0.3s, height 0.3s, background 0.3s;
}
.ink-cursor.hover {
  width: 80px;
  height: 80px;
  background: radial-gradient(circle at center, rgba(0, 0, 0, 0.15) 0%, transparent 70%);
}
</style>
