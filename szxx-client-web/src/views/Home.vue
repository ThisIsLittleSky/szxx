<template>
  <div class="home">
    <!-- ======== 英雄区 ======== -->
    <section class="hero">
      <div class="hero-content fade-up" ref="heroContent">
        <div class="hero-tag">汇五千年文脉 &middot; 铸新时代思政</div>
        <h1>
          让每一份<br />
          <span class="brush">传统文化瑰宝</span><br />
          在思政课堂熠熠生辉
        </h1>
        <p>新时代中国传统优秀文化成就思政素材智能检索学习系统</p>

        <div class="search-box">
          <input
            v-model="keyword"
            type="text"
            placeholder="输入关键词，探索千年文化⋯"
            @keyup.enter="handleSearch"
          />
          <button @click="handleSearch">搜 索</button>
        </div>
        <div class="search-suggest">
          <a href="#" @click.prevent="keyword = '论语'; handleSearch()">论语</a>
          <a href="#" @click.prevent="keyword = '唐诗'; handleSearch()">唐诗</a>
          <a href="#" @click.prevent="keyword = '宋词'; handleSearch()">宋词</a>
          <a href="#" @click.prevent="keyword = '书法'; handleSearch()">书法</a>
          <a href="#" @click.prevent="keyword = '国画'; handleSearch()">国画</a>
          <a href="#" @click.prevent="keyword = '戏曲'; handleSearch()">戏曲</a>
        </div>
      </div>

      <div class="scroll-hint">
        <span>向下探索</span>
        <div class="line"></div>
      </div>
    </section>

    <!-- ======== 分类区 ======== -->
    <section class="categories">
      <div class="section-header fade-up">
        <h2>按文化品类浏览</h2>
        <div class="divider"></div>
        <p>八大文化品类，涵盖中华五千年文明精髓</p>
      </div>
      <div class="cat-grid">
        <div class="cat-card fade-up" v-for="(cat, i) in categories" :key="cat.name" :style="{ transitionDelay: i * 0.08 + 's' }">
          <span class="icon">{{ cat.icon }}</span>
          <h3>{{ cat.name }}</h3>
          <p>{{ cat.desc }}</p>
          <span class="count">{{ cat.count }} 份素材 &rarr;</span>
        </div>
      </div>
    </section>

    <!-- ======== 精选素材 ======== -->
    <section class="materials">
      <div class="section-header fade-up">
        <h2>精选推荐</h2>
        <div class="divider"></div>
        <p>根据学段和兴趣，智能推荐传统文化素材</p>
      </div>
      <div class="mat-grid">
        <div class="mat-card fade-up" v-for="(mat, i) in featuredMaterials" :key="mat.title" :style="{ transitionDelay: i * 0.12 + 's' }">
          <div class="mat-card-img">
            <div class="ink-overlay"></div>
            <span class="cover-icon">{{ mat.icon }}</span>
          </div>
          <div class="mat-card-body">
            <span class="badge">{{ mat.category }}</span>
            <h4>{{ mat.title }}</h4>
            <p class="desc">{{ mat.excerpt }}</p>
            <div class="meta">
              <span>{{ mat.dynasty }}</span><span>&middot;</span>
              <span>{{ mat.stage }}</span><span>&middot;</span>
              <span>{{ mat.learnCount }}</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

const keyword = ref('')
const heroContent = ref<HTMLElement>()

const categories = [
  { icon: '\u{1F4DC}', name: '经典文献', desc: '四书五经、诸子百家、历代典籍', count: '2,340' },
  { icon: '\u{1F3B5}', name: '诗词歌赋', desc: '诗经楚辞、唐诗宋词、元曲明清', count: '5,120' },
  { icon: '✍️', name: '书法艺术', desc: '篆隶楷行草，笔墨之间的精神气韵', count: '1,860' },
  { icon: '\u{1F3A8}', name: '国画美术', desc: '山水花鸟、工笔写意、丹青妙笔', count: '3,450' },
]

const featuredMaterials = [
  {
    icon: '\u{1F4DC}',
    category: '经典文献',
    title: '《论语》选读 — 学而篇',
    excerpt: '子曰："学而时习之，不亦说乎？有朋自远方来⋯" 适合中学思政课堂的经典导读素材。',
    dynasty: '春秋战国',
    stage: '高中',
    learnCount: '1.2万次学习',
  },
  {
    icon: '\u{1F3B5}',
    category: '诗词歌赋',
    title: '《春望》— 杜甫',
    excerpt: '"国破山河在，城春草木深。" 以诗证史，感悟家国情怀与时代担当。',
    dynasty: '唐朝',
    stage: '初中',
    learnCount: '8.6万次学习',
  },
  {
    icon: '✍️',
    category: '书法艺术',
    title: '《兰亭序》— 王羲之',
    excerpt: '"天下第一行书"，感受魏晋风骨与中国书法美学的至高境界。',
    dynasty: '魏晋',
    stage: '高中',
    learnCount: '3.4万次学习',
  },
]

function handleSearch() {
  if (keyword.value.trim()) {
    ElMessage.info(`搜索：${keyword.value}（搜索功能开发中）`)
  }
}

onMounted(() => {
  // 英雄区立即显示
  setTimeout(() => {
    heroContent.value?.classList.add('visible')
  }, 150)
})
</script>

<style scoped>
/* ---- 英雄区 ---- */
.hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 100px 40px 80px;
  overflow: hidden;
}

.hero-content {
  max-width: 700px;
  position: relative;
  z-index: 2;
}

.hero-tag {
  display: inline-block;
  padding: 4px 18px;
  border: 1px solid var(--ink-light);
  font-size: 12px;
  letter-spacing: 5px;
  color: var(--ink-gray);
  margin-bottom: 28px;
  text-transform: uppercase;
}

.hero h1 {
  font-size: clamp(34px, 5.5vw, 60px);
  font-weight: 900;
  line-height: 1.35;
  margin-bottom: 16px;
  letter-spacing: 3px;
  color: var(--ink-black);
}

.brush {
  position: relative;
  display: inline-block;
}
.brush::after {
  content: '';
  position: absolute;
  bottom: 4px;
  left: -3%;
  width: 0;
  height: 10px;
  background: rgba(0, 0, 0, 0.05);
  z-index: -1;
  animation: brushStroke 1.6s 0.6s var(--ease-ink) forwards;
}
@keyframes brushStroke {
  to { width: 106%; }
}

.hero p {
  font-size: clamp(14px, 2vw, 16px);
  color: var(--ink-gray);
  line-height: 1.8;
  margin-bottom: 38px;
  letter-spacing: 1px;
}

/* ---- 搜索 ---- */
.search-box {
  display: flex;
  max-width: 520px;
  margin: 0 auto;
  background: var(--paper-white);
  border: 1px solid var(--ink-wash);
  border-radius: 2px;
  overflow: hidden;
  transition: all 0.4s var(--ease-ink);
  box-shadow: var(--shadow-sm);
}
.search-box:focus-within {
  border-color: var(--ink-dark);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.07);
  transform: translateY(-1px);
}
.search-box input {
  flex: 1;
  padding: 15px 22px;
  border: none;
  background: transparent;
  font-family: var(--font-body);
  font-size: 14px;
  color: var(--ink-dark);
  outline: none;
  letter-spacing: 1px;
}
.search-box input::placeholder {
  color: var(--ink-light);
}
.search-box button {
  padding: 15px 26px;
  border: none;
  background: var(--ink-dark);
  color: var(--paper);
  font-family: var(--font-body);
  font-size: 14px;
  cursor: pointer;
  letter-spacing: 2px;
  transition: background 0.3s;
  white-space: nowrap;
}
.search-box button:hover {
  background: var(--accent-seal);
}

.search-suggest {
  display: flex;
  gap: 14px;
  justify-content: center;
  margin-top: 18px;
  flex-wrap: wrap;
}
.search-suggest a {
  font-size: 12px;
  color: var(--ink-light);
  letter-spacing: 1px;
  position: relative;
  transition: color 0.3s;
}
.search-suggest a::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 1px;
  background: var(--ink-gray);
  transition: width 0.4s;
}
.search-suggest a:hover {
  color: var(--ink-dark);
}
.search-suggest a:hover::after {
  width: 100%;
}

/* ---- 滚动提示 ---- */
.scroll-hint {
  position: absolute;
  bottom: 36px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  animation: scrollFloat 2s ease-in-out infinite;
}
.scroll-hint span {
  font-size: 11px;
  letter-spacing: 4px;
  color: var(--ink-light);
}
.scroll-hint .line {
  width: 1px;
  height: 28px;
  background: linear-gradient(180deg, var(--ink-light), transparent);
}
@keyframes scrollFloat {
  0%, 100% { transform: translateX(-50%) translateY(0); opacity: 1; }
  50%      { transform: translateX(-50%) translateY(8px); opacity: 0.4; }
}

/* ---- 分类区 ---- */
.categories {
  padding: 80px 56px;
  max-width: 1160px;
  margin: 0 auto;
}
.section-header {
  text-align: center;
  margin-bottom: 52px;
}
.section-header h2 {
  font-size: clamp(22px, 3vw, 28px);
  font-weight: 700;
  letter-spacing: 3px;
  color: var(--ink-black);
}
.divider {
  width: 50px;
  height: 1px;
  background: var(--ink-wash);
  margin: 16px auto;
}
.section-header p {
  color: var(--ink-gray);
  font-size: 13px;
  letter-spacing: 1px;
}

.cat-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 28px;
}
.cat-card {
  background: var(--paper-white);
  border: 1px solid transparent;
  padding: 36px 28px 28px;
  cursor: pointer;
  transition: all 0.5s var(--ease-ink);
  overflow: hidden;
  position: relative;
}
.cat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 50% 0%, rgba(0, 0, 0, 0.015) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.5s;
}
.cat-card:hover {
  transform: translateY(-6px);
  border-color: var(--ink-wash);
  box-shadow: var(--shadow-lg);
}
.cat-card:hover::before {
  opacity: 1;
}
.cat-card .icon {
  font-size: 44px;
  display: block;
  margin-bottom: 18px;
  filter: grayscale(0.5);
  transition: filter 0.5s, transform 0.5s;
}
.cat-card:hover .icon {
  filter: grayscale(0);
  transform: scale(1.1);
}
.cat-card h3 {
  font-size: 18px;
  letter-spacing: 2px;
  margin-bottom: 6px;
  font-weight: 600;
  color: var(--ink-black);
}
.cat-card p {
  font-size: 12px;
  color: var(--ink-gray);
  line-height: 1.7;
}
.cat-card .count {
  display: inline-block;
  margin-top: 14px;
  font-size: 12px;
  color: var(--accent-seal);
  letter-spacing: 1px;
  border-bottom: 1px solid transparent;
  transition: border-color 0.3s;
}
.cat-card:hover .count {
  border-bottom-color: var(--accent-seal);
}

/* ---- 素材区 ---- */
.materials {
  padding: 40px 56px 80px;
  max-width: 1160px;
  margin: 0 auto;
}
.mat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 24px;
}
.mat-card {
  background: var(--paper-white);
  overflow: hidden;
  cursor: pointer;
  border: 1px solid transparent;
  transition: all 0.5s var(--ease-ink);
}
.mat-card:hover {
  transform: translateY(-4px);
  border-color: var(--ink-wash);
  box-shadow: var(--shadow-lg);
}
.mat-card-img {
  height: 170px;
  background: linear-gradient(135deg, #e8e4dc 0%, #d4d0c8 50%, #c8c4b8 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.cover-icon {
  font-size: 50px;
  opacity: 0.6;
  position: relative;
  z-index: 1;
}
.ink-overlay {
  position: absolute;
  inset: 0;
  background: radial-gradient(circle at 70% 30%, rgba(0, 0, 0, 0.05) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.5s;
}
.mat-card:hover .ink-overlay {
  opacity: 1;
}
.mat-card-body {
  padding: 20px 24px;
}
.badge {
  display: inline-block;
  padding: 2px 10px;
  font-size: 11px;
  border: 1px solid var(--ink-wash);
  letter-spacing: 2px;
  margin-bottom: 8px;
  color: var(--ink-gray);
}
.mat-card-body h4 {
  font-size: 17px;
  letter-spacing: 1px;
  margin-bottom: 6px;
  font-weight: 600;
  color: var(--ink-black);
}
.mat-card-body .desc {
  font-size: 13px;
  color: var(--ink-gray);
  line-height: 1.7;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}
.meta {
  display: flex;
  gap: 14px;
  margin-top: 12px;
  font-size: 11px;
  color: var(--ink-light);
  letter-spacing: 1px;
}

@media (max-width: 900px) {
  .cat-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 768px) {
  .hero {
    padding: 90px 20px 60px;
  }
  .categories, .materials {
    padding: 60px 20px;
  }
  .cat-grid {
    grid-template-columns: 1fr;
  }
}
</style>
