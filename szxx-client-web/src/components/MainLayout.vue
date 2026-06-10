<template>
  <div class="main-layout" :class="{ 'sidebar-collapsed': collapsed }">
    <!-- 遮罩（移动端） -->
    <div class="sidebar-overlay" v-if="mobileOpen" @click="mobileOpen = false"></div>

    <!-- 侧边栏 -->
    <aside class="sidebar" :class="{ open: mobileOpen }">
      <div class="sidebar-brand">
        <router-link to="/home" class="brand-link">
          <div class="seal">文</div>
          <span class="brand-text">学习空间</span>
        </router-link>
      </div>

      <nav class="sidebar-nav">
        <template v-for="group in menuGroups" :key="group.label">
          <div class="nav-group-label">{{ group.label }}</div>

          <template v-for="item in group.items" :key="item.label">
            <!-- 有子菜单 -->
            <div
              v-if="item.children"
              class="nav-submenu"
              :class="{ open: expandedMenus.has(item.label) }"
            >
              <button class="nav-item" @click="toggleMenu(item.label)">
                <el-icon class="nav-icon"><component :is="item.icon" /></el-icon>
                <span class="nav-label">{{ item.label }}</span>
                <el-icon class="nav-arrow"><ArrowDown /></el-icon>
              </button>
              <div class="submenu" v-show="expandedMenus.has(item.label)">
                <router-link
                  v-for="child in item.children"
                  :key="child.path"
                  :to="child.path!"
                  class="nav-item sub"
                  active-class="active"
                >
                  <span class="nav-label">{{ child.label }}</span>
                </router-link>
              </div>
            </div>

            <!-- 单菜单项 -->
            <router-link
              v-else
              :to="item.path!"
              class="nav-item"
              active-class="active"
              :exact="item.path === '/home'"
            >
              <el-icon class="nav-icon"><component :is="item.icon" /></el-icon>
              <span class="nav-label">{{ item.label }}</span>
            </router-link>
          </template>
        </template>
      </nav>

      <button class="collapse-btn" @click="collapsed = !collapsed">
        <el-icon :size="16"><Fold /></el-icon>
      </button>
    </aside>

    <!-- 右侧主区域 -->
    <div class="main-area">
      <!-- 顶栏 -->
      <header class="topbar">
        <div class="topbar-left">
          <button class="mobile-menu-btn" @click="mobileOpen = !mobileOpen">
            <el-icon :size="20"><Menu /></el-icon>
          </button>
        </div>
        <div class="topbar-right">
          <el-badge :value="0" :max="99" class="notify-badge">
            <el-icon :size="20"><Bell /></el-icon>
          </el-badge>
          <el-dropdown trigger="click" popper-class="user-dropdown-popper">
            <div class="user-info">
              <el-avatar :size="32" :src="user?.avatar" class="user-avatar">
                {{ user?.nickname?.charAt(0) || '用' }}
              </el-avatar>
              <span class="user-name">{{ user?.nickname || '用户' }}</span>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item>
                  <router-link to="/profile" class="dropdown-link">
                    <el-icon><User /></el-icon>
                    个人中心
                  </router-link>
                </el-dropdown-item>
                <el-dropdown-item divided @click="handleLogout">
                  <span class="dropdown-link logout">
                    <el-icon><SwitchButton /></el-icon>
                    退出登录
                  </span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <!-- 内容区 -->
      <main class="content">
        <div class="content-inner">
          <router-view v-slot="{ Component, route }">
            <transition name="ink-spread" mode="out-in">
              <component :is="Component" :key="route.path" />
            </transition>
          </router-view>
        </div>
      </main>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import {
  HomeFilled, Search, Reading, Star, User,
  Upload, Document, Fold, Menu,
  ArrowDown, Bell, SwitchButton
} from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'

const router = useRouter()
const userStore = useUserStore()
const user = computed(() => userStore.user)

const collapsed = ref(false)
const mobileOpen = ref(false)
const expandedMenus = ref(new Set<string>(['探索发现']))

interface MenuItem {
  path?: string
  label: string
  icon?: any
  children?: { path: string; label: string }[]
  roles?: string[]
}

const menuGroups = computed(() => {
  const role = user.value?.role || 'student'
  const groups: { label: string; items: MenuItem[] }[] = []

  // 主导航
  groups.push({
    label: '主导航',
    items: [
      { path: '/home', label: '首页', icon: HomeFilled }
    ]
  })

  // 探索发现
  groups.push({
    label: '探索发现',
    items: [
      {
        label: '探索发现',
        icon: Search,
        children: [
          { path: '/search', label: '关键词检索' },
          { path: '/category', label: '朝代分类' },
          { path: '/course', label: '思政课程' }
        ]
      }
    ]
  })

  // 学习
  groups.push({
    label: '学习',
    items: [
      { path: '/learning', label: '学习中心', icon: Reading }
    ]
  })

  // 收藏
  groups.push({
    label: '收藏',
    items: [
      { path: '/favorites', label: '我的收藏', icon: Star }
    ]
  })

  // 素材管理（教师可见）
  if (role === 'teacher' || role === 'admin') {
    groups.push({
      label: '素材管理',
      items: [
        { path: '/upload', label: '上传素材', icon: Upload },
        { path: '/my-materials', label: '我的素材', icon: Document }
      ]
    })
  }

  // 个人
  groups.push({
    label: '个人',
    items: [
      { path: '/profile', label: '个人中心', icon: User }
    ]
  })

  return groups
})

function toggleMenu(label: string) {
  if (expandedMenus.value.has(label)) {
    expandedMenus.value.delete(label)
  } else {
    expandedMenus.value.add(label)
  }
}

function handleLogout() {
  userStore.logout()
  ElMessage.success('已退出登录')
  router.push('/')
}

// 移动端打开菜单时关闭侧边栏收起状态
watch(mobileOpen, (v) => {
  if (v) collapsed.value = false
})
</script>

<style scoped>
/* ================================================
   整体布局
   ================================================ */
.main-layout {
  display: flex;
  height: 100vh;
  overflow: hidden;
}

/* ================================================
   侧边栏
   ================================================ */
.sidebar {
  width: 220px;
  min-width: 220px;
  height: 100vh;
  background: var(--ink-dark);
  display: flex;
  flex-direction: column;
  transition: width 0.3s var(--ease-ink), min-width 0.3s var(--ease-ink);
  overflow: hidden;
  z-index: 200;
  position: relative;
}

.sidebar-collapsed .sidebar {
  width: 64px;
  min-width: 64px;
}

.sidebar-overlay {
  display: none;
}

/* 品牌区 */
.sidebar-brand {
  padding: 20px 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-link {
  display: flex;
  align-items: center;
  gap: 10px;
  white-space: nowrap;
}

.seal {
  width: 32px;
  height: 32px;
  min-width: 32px;
  border: 2px solid var(--accent-seal);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: 700;
  color: var(--accent-seal);
  transform: rotate(-5deg);
  transition: transform 0.3s;
}

.brand-link:hover .seal {
  transform: rotate(0deg);
}

.brand-text {
  font-family: var(--font-calligraphy);
  font-size: 22px;
  color: var(--paper-white);
  letter-spacing: 2px;
  transition: opacity 0.3s;
}

.sidebar-collapsed .brand-text {
  opacity: 0;
  width: 0;
}

/* 导航区 */
.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
  padding: 12px 0;
}

.sidebar-nav::-webkit-scrollbar {
  width: 3px;
}
.sidebar-nav::-webkit-scrollbar-thumb {
  background: rgba(255, 255, 255, 0.1);
  border-radius: 2px;
}

.nav-group-label {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.3);
  letter-spacing: 2px;
  padding: 16px 20px 6px;
  transition: opacity 0.3s;
  white-space: nowrap;
  overflow: hidden;
}

.sidebar-collapsed .nav-group-label {
  opacity: 0;
  height: 0;
  padding: 0;
}

/* 菜单项 */
.nav-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 11px 20px;
  margin: 1px 8px;
  border-radius: 4px;
  color: rgba(255, 255, 255, 0.65);
  font-size: 14px;
  letter-spacing: 1px;
  cursor: pointer;
  transition: all 0.25s ease;
  white-space: nowrap;
  position: relative;
  border: none;
  background: none;
  width: calc(100% - 16px);
  text-decoration: none;
  font-family: inherit;
}

.nav-item:hover {
  color: rgba(255, 255, 255, 0.9);
  background: rgba(255, 255, 255, 0.06);
}

.nav-item.active {
  color: var(--paper-white);
  background: rgba(194, 59, 34, 0.15);
}

.nav-item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 6px;
  bottom: 6px;
  width: 3px;
  background: var(--accent-seal);
  border-radius: 0 2px 2px 0;
  animation: borderGrow 0.35s var(--ease-ink);
}

@keyframes borderGrow {
  from { transform: scaleY(0); }
  to { transform: scaleY(1); }
}

.nav-item .nav-icon {
  font-size: 18px;
  min-width: 18px;
  transition: transform 0.3s;
}

.nav-item:hover .nav-icon {
  transform: scale(1.1);
}

.nav-item .nav-label {
  transition: opacity 0.25s;
}

.sidebar-collapsed .nav-label {
  opacity: 0;
  width: 0;
}

/* 子菜单 */
.nav-submenu .nav-item {
  padding-right: 28px;
}

.nav-arrow {
  margin-left: auto;
  font-size: 12px;
  transition: transform 0.3s;
}

.nav-submenu.open .nav-arrow {
  transform: rotate(180deg);
}

.sidebar-collapsed .nav-arrow {
  display: none;
}

.submenu {
  overflow: hidden;
}

.submenu .nav-item.sub {
  padding-left: 48px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.5);
}

.submenu .nav-item.sub:hover {
  color: rgba(255, 255, 255, 0.8);
}

.submenu .nav-item.sub.active {
  color: var(--paper-white);
  background: rgba(194, 59, 34, 0.12);
}

/* 收起按钮 */
.collapse-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 14px;
  border: none;
  background: none;
  color: rgba(255, 255, 255, 0.35);
  cursor: pointer;
  transition: all 0.3s;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  font-family: inherit;
}

.collapse-btn:hover {
  color: rgba(255, 255, 255, 0.7);
  background: rgba(255, 255, 255, 0.04);
}

.sidebar-collapsed .collapse-btn {
  transform: rotate(180deg);
}

/* ================================================
   右侧主区域
   ================================================ */
.main-area {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-width: 0;
  background: var(--paper);
}

/* ================================================
   顶栏
   ================================================ */
.topbar {
  height: 56px;
  min-height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  background: rgba(250, 248, 243, 0.85);
  backdrop-filter: blur(8px);
  border-bottom: 1px solid var(--ink-wash);
  z-index: 100;
}

.topbar-left {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 1;
  max-width: 480px;
}

.mobile-menu-btn {
  display: none;
  border: none;
  background: none;
  color: var(--ink-gray);
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
}

.global-search {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: var(--paper-white);
  border: 1px solid var(--ink-wash);
  border-radius: 6px;
  transition: all 0.3s;
}

.global-search:focus-within {
  border-color: var(--accent-seal);
  box-shadow: 0 0 0 3px rgba(194, 59, 34, 0.06);
}

.search-icon {
  color: var(--ink-light);
  font-size: 16px;
}

.search-input {
  flex: 1;
  border: none;
  outline: none;
  background: none;
  font-family: var(--font-body);
  font-size: 13px;
  color: var(--ink-dark);
}

.search-input::placeholder {
  color: var(--ink-light);
}

.search-kbd {
  font-size: 11px;
  color: var(--ink-light);
  background: var(--paper-dark);
  padding: 1px 6px;
  border-radius: 3px;
  font-family: monospace;
  letter-spacing: 0;
}

.topbar-right {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-left: 24px;
}

.notify-badge {
  cursor: pointer;
  color: var(--ink-gray);
  transition: color 0.3s;
}

.notify-badge:hover {
  color: var(--ink-dark);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 6px;
  transition: background 0.2s;
}

.user-info:hover {
  background: rgba(0, 0, 0, 0.03);
}

.user-avatar {
  background: var(--ink-dark);
  color: var(--paper-white);
  font-family: var(--font-calligraphy);
}

.user-name {
  font-size: 13px;
  color: var(--ink-dark);
  letter-spacing: 1px;
}

.dropdown-icon {
  font-size: 11px;
  color: var(--ink-light);
  transition: transform 0.3s;
}

.dropdown-link {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: var(--ink-dark);
}

.dropdown-link.logout {
  color: var(--accent-seal);
}

/* ================================================
   内容区
   ================================================ */
.content {
  flex: 1;
  overflow-y: auto;
  overflow-x: hidden;
}

.content-inner {
  padding: 24px;
  min-height: 100%;
}

/* ================================================
   页面切换动画 — 水墨晕染
   ================================================ */
.ink-spread-enter-active {
  transition: opacity 0.35s ease, transform 0.35s var(--ease-ink);
}

.ink-spread-leave-active {
  transition: opacity 0.15s ease;
}

.ink-spread-enter-from {
  opacity: 0;
  transform: scale(0.97);
}

.ink-spread-leave-to {
  opacity: 0;
}

/* ================================================
   响应式
   ================================================ */
@media (max-width: 1024px) {
  .sidebar {
    width: 64px;
    min-width: 64px;
  }
  .sidebar .brand-text,
  .sidebar .nav-group-label,
  .sidebar .nav-label,
  .sidebar .nav-arrow {
    display: none;
  }

  .sidebar-collapsed .sidebar {
    width: 64px;
    min-width: 64px;
  }
}

@media (max-width: 768px) {
  .sidebar {
    position: fixed;
    left: -240px;
    top: 0;
    bottom: 0;
    width: 240px;
    min-width: 240px;
    transition: left 0.3s var(--ease-ink);
  }

  .sidebar.open {
    left: 0;
  }

  .sidebar .brand-text,
  .sidebar .nav-group-label,
  .sidebar .nav-label,
  .sidebar .nav-arrow {
    display: initial;
  }

  .sidebar-overlay {
    display: block;
    position: fixed;
    inset: 0;
    background: rgba(0, 0, 0, 0.4);
    z-index: 199;
  }

  .mobile-menu-btn {
    display: block;
  }

  .sidebar-collapsed .sidebar {
    width: 240px;
    min-width: 240px;
  }

  .collapse-btn {
    display: none;
  }

  .topbar {
    padding: 0 16px;
  }

  .content-inner {
    padding: 16px;
  }

  .search-kbd {
    display: none;
  }

  .user-name {
    display: none;
  }
}
</style>
