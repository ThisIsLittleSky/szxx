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
import { ref, onMounted, onUnmounted } from 'vue'

const cursorRef = ref<HTMLElement>()

function onMouseMove(e: MouseEvent) {
  if (!cursorRef.value) return
  cursorRef.value.style.left = e.clientX + 'px'
  cursorRef.value.style.top = e.clientY + 'px'
}

function onMouseEnter() { cursorRef.value?.classList.add('hover') }
function onMouseLeave() { cursorRef.value?.classList.remove('hover') }

let observer: IntersectionObserver | null = null

onMounted(() => {
  document.addEventListener('mousemove', onMouseMove)

  // 墨点光标 hover 效果
  const interactives = document.querySelectorAll('a, button, .cat-card, .mat-card, input, .el-button')
  interactives.forEach(el => {
    el.addEventListener('mouseenter', onMouseEnter)
    el.addEventListener('mouseleave', onMouseLeave)
  })

  // 入场动画
  observer = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add('visible')
      }
    })
  }, { threshold: 0.12, rootMargin: '0px 0px -40px 0px' })

  // 延迟观察以等待 DOM 渲染
  setTimeout(() => {
    document.querySelectorAll('.fade-up').forEach(el => observer?.observe(el))
  }, 100)
})

onUnmounted(() => {
  document.removeEventListener('mousemove', onMouseMove)
  observer?.disconnect()
})
</script>

<style>
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
  opacity: 0.05;
  background: radial-gradient(ellipse at center, #000 0%, transparent 70%);
  border-radius: 50%;
  filter: blur(40px);
  animation: inkBreathe 10s ease-in-out infinite;
}

@keyframes inkBreathe {
  0%, 100% { transform: scale(1) rotate(0deg); opacity: 0.04; }
  33%      { transform: scale(1.1) rotate(1deg); opacity: 0.07; }
  66%      { transform: scale(0.95) rotate(-1deg); opacity: 0.03; }
}

/* 墨点光标 */
.ink-cursor {
  position: fixed;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  pointer-events: none;
  z-index: 9999;
  background: radial-gradient(circle at center, rgba(0, 0, 0, 0.06) 0%, transparent 70%);
  transform: translate(-50%, -50%);
  transition: width 0.35s var(--ease-ink), height 0.35s var(--ease-ink),
              background 0.35s var(--ease-ink);
}
.ink-cursor.hover {
  width: 80px;
  height: 80px;
  background: radial-gradient(circle at center, rgba(0, 0, 0, 0.12) 0%, transparent 70%);
}
</style>
