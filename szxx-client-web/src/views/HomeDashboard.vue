<template>
  <div class="dashboard fade-up visible">
    <!-- 欢迎区 -->
    <section class="welcome">
      <h1 class="welcome-title">
        {{ greeting }}，{{ user?.nickname || '同学' }}
        <span class="welcome-wave">👋</span>
      </h1>
      <p class="welcome-desc">今天想探索哪段传统文化？</p>
    </section>

    <!-- 快捷入口 -->
    <section class="quick-actions">
      <div class="action-card" v-for="a in quickActions" :key="a.label" @click="$router.push(a.path)">
        <div class="action-icon"><el-icon :size="24"><component :is="a.icon" /></el-icon></div>
        <span class="action-label">{{ a.label }}</span>
      </div>
    </section>

    <!-- 统计卡片 -->
    <section class="stats-row">
      <div class="stat-card" v-for="s in stats" :key="s.label">
        <div class="stat-num">{{ s.value }}</div>
        <div class="stat-label">{{ s.label }}</div>
      </div>
      <!-- 时长排行卡片（特殊设计） -->
      <div class="rank-card" @click="openRanking()">
        <div class="rank-card-glow"></div>
        <div class="rank-card-icon">
          <el-icon :size="22"><Trophy /></el-icon>
        </div>
        <div class="rank-card-text">
          <span class="rank-card-title">学习排行</span>
          <span class="rank-card-sub">日积月累，跬步千里</span>
        </div>
        <div class="rank-card-arrow">
          <el-icon :size="16"><ArrowRight /></el-icon>
        </div>
      </div>
    </section>

    <!-- 时长排行弹窗 -->
    <el-dialog
      v-model="showRanking"
      width="600px" top="6vh" destroy-on-close
      :show-close="true"
      class="ranking-dialog"
    >
      <template #header>
        <div class="dialog-header">
          <div class="dialog-header-icon">
            <el-icon :size="24"><Trophy /></el-icon>
          </div>
          <div>
            <h3 class="dialog-title">学习时长排行</h3>
            <p class="dialog-sub">学如逆水行舟，不进则退</p>
          </div>
        </div>
      </template>

      <div v-if="rankingList.length === 0" class="ranking-empty">
        <el-icon :size="56"><Reading /></el-icon>
        <p>暂无排行数据</p>
        <span>快去学习，成为第一个上榜的人吧！</span>
      </div>

      <template v-else>
        <!-- 前三名领奖台 -->
        <div class="podium">
          <div class="podium-item" v-for="p in podiumData" :key="p.rank" :class="'podium-' + p.rank">
            <div class="podium-avatar-wrap">
              <img
                class="podium-avatar"
                :src="p.avatar || '/default-avatar.png'"
                @error="($event.target as HTMLImageElement).style.display='none'"
              />
              <span class="podium-crown">{{ p.crown }}</span>
            </div>
            <span class="podium-name">{{ p.nickname }}</span>
            <span class="podium-account">@{{ p.username }}</span>
            <span class="podium-time">{{ p.duration }}</span>
            <div class="podium-bar">{{ p.rank }}</div>
          </div>
        </div>

        <!-- 排行榜列表（第4名起） -->
        <div class="ranking-scroll">
          <div
            class="ranking-row"
            :class="{ 'is-me': item.userId === user?.id }"
            v-for="item in restList"
            :key="item.userId"
          >
            <span class="r-rank">{{ item.rank }}</span>
            <img
              class="r-avatar"
              :src="item.avatar || '/default-avatar.png'"
              @error="($event.target as HTMLImageElement).style.display='none'"
            />
            <span class="r-name">{{ item.nickname }}</span>
            <span class="r-account">@{{ item.username }}</span>
            <span class="r-time">{{ formatDuration(item.totalDuration) }}</span>
          </div>
        </div>

        <!-- 当前用户排名卡片 -->
        <div class="my-rank-card" v-if="myRankItem">
          <div class="my-rank-left">
            <span class="my-rank-num">NO.{{ myRankItem.rank }}</span>
            <span class="my-rank-tip">{{ rankEncourage(myRankItem.rank) }}</span>
          </div>
          <div class="my-rank-right">
            <span>{{ formatDuration(myRankItem.totalDuration) }}</span>
          </div>
        </div>
      </template>
    </el-dialog>

    <!-- 内容双栏 -->
    <section class="two-col">
      <!-- 热门素材 -->
      <div class="card panel">
        <h3 class="panel-title">热门素材</h3>
        <div v-if="hotLoading" class="empty-hint">加载中...</div>
        <div v-else-if="hotMaterials.length === 0" class="empty-hint">暂无素材</div>
        <div v-else class="material-list">
          <div
            v-for="m in hotMaterials"
            :key="m.id"
            class="material-item"
            @click="$router.push(`/material/${m.id}`)"
          >
            <div class="material-info">
              <span class="material-title">{{ m.title }}</span>
              <span class="material-meta">{{ m.author }} · {{ m.dynasty }}</span>
            </div>
            <span class="material-views">{{ m.viewCount }} 浏览 · {{ m.favoriteCount }} 收藏</span>
          </div>
        </div>
      </div>

      <!-- 学习动态 -->
      <div class="card panel">
        <h3 class="panel-title">学习动态</h3>
        <div v-if="recordsLoading" class="empty-hint">加载中...</div>
        <div v-else-if="recentRecords.length === 0" class="empty-hint">暂无学习记录</div>
        <div v-else class="record-list">
          <div
            v-for="r in recentRecords"
            :key="r.id"
            class="record-item"
            @click="$router.push(`/material/${r.materialId}`)"
          >
            <div class="record-main">
              <span class="record-title">{{ r.materialTitle }}</span>
              <span class="record-badge" :class="r.completed === 1 ? 'badge-done' : 'badge-progress'">
                {{ r.completed === 1 ? '已完成' : '进行中' }}
              </span>
            </div>
            <div class="record-meta">
              <span>{{ r.materialAuthor }}</span>
              <span>学习 {{ formatDuration(r.duration) }}</span>
              <span class="record-date">{{ formatDate(r.updatedAt) }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Search, Collection, Reading, Star, Trophy, ArrowRight } from '@element-plus/icons-vue'
import { useUserStore } from '../stores/user'
import { getLearningStats, getLearningRecords, getLearningRanking } from '../api/learning'
import { getMaterials, getFavorites } from '../api/materials'

interface MaterialItem {
  id: string | number
  title: string
  author: string
  dynasty: string
  viewCount: number
  favoriteCount: number
}

interface RankItem {
  rank: number
  userId: number
  nickname: string
  username: string
  avatar: string
  totalDuration: number
}

interface RecordItem {
  id: number
  materialId: number
  materialTitle: string
  materialAuthor: string
  duration: number
  completed: number
  updatedAt: string
}

const userStore = useUserStore()
const user = computed(() => userStore.user)

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h >= 5 && h < 12) return '早安'
  if (h >= 12 && h < 18) return '下午好'
  return '晚上好'
})

const totalMaterials = ref(0)
const totalDuration = ref(0)
const favoriteCount = ref(0)

const showRanking = ref(false)
const rankingList = ref<RankItem[]>([])

const hotMaterials = ref<MaterialItem[]>([])
const recentRecords = ref<RecordItem[]>([])
const hotLoading = ref(true)
const recordsLoading = ref(true)

const quickActions = [
  { label: '关键词检索', icon: Search, path: '/search' },
  { label: '朝代分类', icon: Collection, path: '/category' },
  { label: '思政课程', icon: Reading, path: '/course' },
  { label: '我的收藏', icon: Star, path: '/favorites' }
]

const stats = computed(() => [
  { label: '学习素材数', value: totalMaterials.value },
  { label: '学习时长', value: formatDuration(totalDuration.value) },
  { label: '收藏素材', value: favoriteCount.value }
])

const podiumData = computed(() => {
  const top3 = rankingList.value.slice(0, 3)
  const crowns = ['🥇', '🥈', '🥉']
  // Podium order: 2nd, 1st, 3rd (center is tallest)
  const order = [1, 0, 2]
  return order
    .map(i => {
      const item = top3[i]
      if (!item) return null
      return {
        rank: item.rank,
        nickname: item.nickname,
        username: item.username,
        avatar: item.avatar,
        duration: formatDuration(item.totalDuration),
        crown: crowns[item.rank - 1]
      }
    })
    .filter((item): item is NonNullable<typeof item> => item != null)
})

const restList = computed(() => rankingList.value.slice(3))

const myRankItem = computed(() => {
  if (!user.value) return null
  return rankingList.value.find(r => r.userId === user.value!.id) || null
})

onMounted(() => {
  fetchStats()
  fetchHotMaterials()
  fetchRecentRecords()
})

async function fetchStats() {
  try {
    const res = await getLearningStats()
    const data = (res as any).data || res
    totalMaterials.value = data.totalMaterials || 0
    totalDuration.value = data.totalDuration || 0
  } catch { /* */ }

  try {
    const favRes = await getFavorites({ page: 1, size: 1 })
    const favData = (favRes as any).data || favRes
    favoriteCount.value = favData.total || 0
  } catch { /* */ }
}

async function openRanking() {
  showRanking.value = true
  try {
    const res = await getLearningRanking(20)
    const data = (res as any).data || res
    rankingList.value = data || []
  } catch { /* */ }
}

async function fetchHotMaterials() {
  hotLoading.value = true
  try {
    const res = await getMaterials({ sort: 'hot', size: 10 })
    const data = (res as any).data || res
    hotMaterials.value = data.records || []
  } catch { /* */ }
  finally { hotLoading.value = false }
}

async function fetchRecentRecords() {
  recordsLoading.value = true
  try {
    const res = await getLearningRecords({ page: 1, size: 5 })
    const data = (res as any).data || res
    recentRecords.value = data.records || []
  } catch { /* */ }
  finally { recordsLoading.value = false }
}

function rankEncourage(rank: number): string {
  if (rank === 1) return '独占鳌头，继续保持！'
  if (rank <= 3) return '名列前茅，了不起！'
  if (rank <= 10) return '稳居前十，再接再厉！'
  if (rank <= 20) return '榜上有名，继续加油！'
  return '坚持不懈，终有收获！'
}

function formatDuration(seconds: number) {
  if (!seconds || seconds === 0) return '0 分钟'
  const h = Math.floor(seconds / 3600)
  const m = Math.floor((seconds % 3600) / 60)
  if (h > 0) return `${h} 小时 ${m} 分钟`
  return `${m} 分钟`
}

function formatDate(dateStr: string) {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const month = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${month}-${day}`
}
</script>

<style scoped>
.dashboard { max-width: 960px; margin: 0 auto; }

.welcome { margin-bottom: 28px; }
.welcome-title { font-size: 24px; font-weight: 600; color: var(--ink-black); letter-spacing: 1px; }
.welcome-wave { font-size: 24px; margin-left: 6px; }
.welcome-desc { margin-top: 6px; color: var(--ink-gray); font-size: 14px; }

.quick-actions {
  display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 28px;
}
.action-card {
  display: flex; flex-direction: column; align-items: center; gap: 10px;
  padding: 20px 12px; background: var(--paper-white); border: 1px solid var(--ink-wash);
  border-radius: 6px; cursor: pointer; transition: all 0.3s;
}
.action-card:hover { border-color: var(--accent-seal); box-shadow: var(--shadow-md); transform: translateY(-2px); }
.action-card:hover .action-icon { color: var(--accent-seal); }
.action-icon { color: var(--ink-gray); transition: color 0.3s; }
.action-label { font-size: 13px; color: var(--ink-dark); letter-spacing: 1px; }

.stats-row { display: grid; grid-template-columns: repeat(4, 1fr); gap: 16px; margin-bottom: 28px; }
.stat-card {
  text-align: center; padding: 20px 12px; background: var(--paper-white);
  border: 1px solid var(--ink-wash); border-radius: 6px;
}
.stat-num { font-size: 28px; font-weight: 700; color: var(--ink-black); font-family: var(--font-calligraphy); }
.stat-label { margin-top: 4px; font-size: 12px; color: var(--ink-light); letter-spacing: 1px; }

/* 排行激励卡片 */
.rank-card {
  position: relative; display: flex; align-items: center; gap: 12px;
  padding: 16px 16px; border-radius: 6px; cursor: pointer;
  background: linear-gradient(135deg, #fef9e7 0%, #fdebd0 50%, #fef9e7 100%);
  border: 1px solid #f0d78c;
  overflow: hidden;
  transition: all 0.35s cubic-bezier(0.4, 0, 0.2, 1);
}
.rank-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(230, 162, 60, 0.25);
  border-color: #e6a23c;
}
.rank-card-glow {
  position: absolute; top: -50%; left: -50%; width: 200%; height: 200%;
  background: radial-gradient(circle, rgba(230,162,60,0.12) 0%, transparent 70%);
  animation: rankGlow 3s ease-in-out infinite;
}
@keyframes rankGlow {
  0%, 100% { transform: translate(0, 0); }
  50% { transform: translate(5%, 5%); }
}
.rank-card-icon {
  width: 40px; height: 40px; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #e6a23c, #f5c842); border-radius: 10px;
  color: #fff; flex-shrink: 0; position: relative; z-index: 1;
}
.rank-card-text {
  display: flex; flex-direction: column; gap: 2px; position: relative; z-index: 1;
}
.rank-card-title {
  font-size: 14px; font-weight: 600; color: #b06a20; letter-spacing: 1px;
}
.rank-card-sub {
  font-size: 11px; color: #c9944a; letter-spacing: 0.5px;
}
.rank-card-arrow {
  margin-left: auto; color: #d4942b; flex-shrink: 0; position: relative; z-index: 1;
  transition: transform 0.3s;
}
.rank-card:hover .rank-card-arrow { transform: translateX(4px); }

.two-col { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
.panel { padding: 20px; }
.panel-title { font-size: 15px; font-weight: 600; color: var(--ink-black); margin-bottom: 12px; letter-spacing: 1px; }
.empty-hint { text-align: center; padding: 40px 0; color: var(--ink-light); font-size: 13px; }

/* 热门素材列表 */
.material-list { display: flex; flex-direction: column; }
.material-item {
  display: flex; align-items: center; justify-content: space-between;
  padding: 10px 0; border-bottom: 1px solid var(--ink-wash); cursor: pointer;
  transition: background 0.2s;
}
.material-item:last-child { border-bottom: none; }
.material-item:hover { background: var(--bg-wash); margin: 0 -8px; padding-left: 8px; padding-right: 8px; border-radius: 4px; }
.material-info { display: flex; flex-direction: column; gap: 2px; overflow: hidden; }
.material-title {
  font-size: 13px; color: var(--ink-black); font-weight: 500;
  white-space: nowrap; overflow: hidden; text-overflow: ellipsis;
}
.material-meta { font-size: 11px; color: var(--ink-light); }
.material-views { font-size: 11px; color: var(--ink-light); white-space: nowrap; margin-left: 12px; }

/* 学习记录列表 */
.record-list { display: flex; flex-direction: column; }
.record-item {
  display: flex; flex-direction: column; gap: 4px;
  padding: 10px 0; border-bottom: 1px solid var(--ink-wash); cursor: pointer;
  transition: background 0.2s;
}
.record-item:last-child { border-bottom: none; }
.record-item:hover { background: var(--bg-wash); margin: 0 -8px; padding-left: 8px; padding-right: 8px; border-radius: 4px; }
.record-main { display: flex; align-items: center; gap: 8px; }
.record-title { font-size: 13px; color: var(--ink-black); }
.record-badge { font-size: 10px; padding: 1px 6px; border-radius: 3px; }
.badge-done { background: #e8f5e9; color: #2e7d32; }
.badge-progress { background: #fff3e0; color: #ef6c00; }
.record-meta { display: flex; align-items: center; gap: 12px; font-size: 11px; color: var(--ink-light); }
.record-date { color: var(--ink-light); }

/* ========== 排行弹窗 ========== */
.dialog-header { display: flex; align-items: center; gap: 14px; }
.dialog-header-icon {
  width: 44px; height: 44px; display: flex; align-items: center; justify-content: center;
  background: linear-gradient(135deg, #e6a23c, #f5c842); border-radius: 12px; color: #fff;
}
.dialog-title { font-size: 18px; font-weight: 700; color: var(--ink-black); margin: 0; }
.dialog-sub { font-size: 12px; color: var(--ink-light); margin: 4px 0 0; }

.ranking-empty {
  display: flex; flex-direction: column; align-items: center; gap: 12px;
  padding: 40px 0; color: var(--ink-light);
}
.ranking-empty p { font-size: 15px; color: var(--ink-gray); margin: 0; }
.ranking-empty span { font-size: 12px; }

/* 领奖台 */
.podium {
  display: flex; align-items: flex-end; justify-content: center; gap: 20px;
  padding: 30px 0 0; margin-bottom: 24px;
}
.podium-item {
  display: flex; flex-direction: column; align-items: center; gap: 8px;
  flex: 1; max-width: 140px;
}
.podium-avatar-wrap { position: relative; }
.podium-avatar {
  width: 56px; height: 56px; border-radius: 50%; object-fit: cover;
  border: 3px solid #e0d5c1; background: var(--el-fill-color-light);
}
.podium-1 .podium-avatar { width: 68px; height: 68px; border-color: #f5c842; box-shadow: 0 0 20px rgba(245,200,66,0.4); }
.podium-2 .podium-avatar { border-color: #c0c4cc; box-shadow: 0 0 16px rgba(192,196,204,0.4); }
.podium-3 .podium-avatar { border-color: #d4a574; box-shadow: 0 0 16px rgba(212,165,116,0.4); }
.podium-crown { position: absolute; top: -18px; left: 50%; transform: translateX(-50%); font-size: 22px; }
.podium-name {
  font-size: 13px; font-weight: 500; color: var(--ink-black);
  max-width: 120px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.podium-account {
  font-size: 11px; color: var(--ink-gray);
  max-width: 120px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;
}
.podium-time { font-size: 11px; color: var(--ink-light); }
.podium-bar {
  width: 100%; display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 20px; font-weight: 800; border-radius: 6px 6px 0 0;
  letter-spacing: 1px;
}
.podium-1 .podium-bar { height: 72px; background: linear-gradient(180deg, #f5c842, #e6a23c); }
.podium-2 .podium-bar { height: 52px; background: linear-gradient(180deg, #d0d5dd, #909399); }
.podium-3 .podium-bar { height: 38px; background: linear-gradient(180deg, #e0b894, #b8733b); }

/* 剩余排行列表 */
.ranking-scroll { max-height: 280px; overflow-y: auto; margin-bottom: 16px; }
.ranking-row {
  display: flex; align-items: center; gap: 12px; padding: 10px 12px;
  border-radius: 6px; transition: background 0.2s;
}
.ranking-row:hover { background: var(--el-fill-color-light); }
.ranking-row.is-me {
  background: linear-gradient(90deg, #fef9e7 0%, #fdf6ec 100%);
  border: 1px solid #f0d78c; border-radius: 6px;
}
.r-rank { width: 28px; text-align: center; font-size: 14px; font-weight: 700; color: var(--ink-gray); flex-shrink: 0; }
.r-avatar {
  width: 34px; height: 34px; border-radius: 50%; object-fit: cover;
  background: var(--el-fill-color-light); flex-shrink: 0;
}
.r-name { flex: 1; font-size: 13px; color: var(--ink-black); overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.r-account { font-size: 11px; color: var(--ink-gray); margin-left: 8px; flex-shrink: 0; }
.r-time { font-size: 13px; color: var(--ink-gray); flex-shrink: 0; font-weight: 500; }

/* 我的排名卡片 */
.my-rank-card {
  display: flex; align-items: center; justify-content: space-between;
  padding: 14px 18px; border-radius: 8px;
  background: linear-gradient(135deg, #fef9e7, #fdf6ec);
  border: 1px solid #f0d78c;
}
.my-rank-left { display: flex; flex-direction: column; gap: 2px; }
.my-rank-num { font-size: 18px; font-weight: 800; color: #b06a20; letter-spacing: 1px; }
.my-rank-tip { font-size: 12px; color: #c9944a; }
.my-rank-right { font-size: 15px; font-weight: 600; color: #b06a20; }

@media (max-width: 768px) {
  .quick-actions, .stats-row, .two-col { grid-template-columns: repeat(2, 1fr); }
  .rank-card-sub { display: none; }
  .podium { gap: 12px; }
  .podium-1 .podium-bar { height: 56px; }
  .podium-2 .podium-bar { height: 40px; }
  .podium-3 .podium-bar { height: 30px; }
}
</style>

<style>
/* 排行弹窗全局样式（el-dialog teleported to body） */
.ranking-dialog .el-dialog__header {
  padding: 20px 24px 0; border-bottom: none;
}
.ranking-dialog .el-dialog__body {
  padding: 16px 24px 24px;
}
.ranking-dialog .el-dialog__footer {
  display: none;
}
</style>
