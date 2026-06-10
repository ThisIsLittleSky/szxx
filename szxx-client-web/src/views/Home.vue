<template>
  <div class="home">
    <!-- ======== 区域一：首页 / 英雄区 ======== -->
    <section id="home" class="hero">
      <div class="ink-splash" style="width:600px;height:300px;top:10%;left:-10%;"></div>
      <div class="ink-splash" style="width:400px;height:200px;bottom:20%;right:-5%;animation-delay:-4s;"></div>
      <div class="ink-splash" style="width:300px;height:150px;top:40%;right:15%;animation-delay:-2s;"></div>

      <div class="hero-content fade-up" ref="heroContent">
        <div class="hero-tag">汇五千年文脉 &middot; 铸新时代思政</div>
        <h1>
          让每一份<br />
          <span class="brush">传统文化瑰宝</span><br />
          在思政课堂熠熠生辉
        </h1>
        <p>新时代中国传统优秀文化成就思政素材智能检索学习系统V1.0</p>

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

    <!-- ======== 区域二：素材总览 ======== -->
    <section id="materials" class="section">
      <div class="section-header fade-up">
        <h2>素材总览</h2>
        <div class="divider"></div>
        <p>六大文化品类，涵盖中华五千年文明精髓</p>
      </div>
      <div class="cat-grid">
        <div class="cat-card fade-up" v-for="(cat, i) in categories" :key="cat.name" :style="{ transitionDelay: i * 0.08 + 's' }">
          <span class="icon">{{ cat.icon }}</span>
          <h3>{{ cat.name }}</h3>
          <p>{{ cat.desc }}</p>
          <span class="count">{{ cat.count }} 份素材 &rarr;</span>
        </div>
      </div>

      <!-- 精选推荐 -->
      <div class="section-header fade-up" style="margin-top: 80px;">
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

    <!-- ======== 区域三：功能介绍 ======== -->
    <section id="features" class="section section-alt">
      <div class="section-header fade-up">
        <h2>功能介绍</h2>
        <div class="divider"></div>
        <p>专为思政教育打造的传统文化素材平台</p>
      </div>
      <div class="feat-grid">
        <div class="feat-card fade-up" v-for="(feat, i) in features" :key="feat.title" :style="{ transitionDelay: i * 0.1 + 's' }">
          <span class="feat-icon">{{ feat.icon }}</span>
          <h3>{{ feat.title }}</h3>
          <p>{{ feat.desc }}</p>
        </div>
      </div>
    </section>

    <!-- ======== 区域四：关于我们 ======== -->
    <section id="about" class="section">
      <div class="section-header fade-up">
        <h2>关于我们</h2>
        <div class="divider"></div>
      </div>
      <div class="about-content fade-up">
        <p class="about-lead">
          "新时代中国传统优秀文化成就思政素材智能检索学习系统V1.0"
        </p>
        <p class="about-text">
          致力于将中华五千年优秀传统文化与新时代思想政治教育深度融合。<br />
          以诸子思想启迪心智，以非遗技艺涵养审美，以民俗风情厚植情怀，<br />
          以红色基因铸魂育人，为思政教育工作者提供权威、丰富、便捷的素材支撑。
        </p>
        <div class="about-seal">
          <div class="seal-lg">文</div>
          <span>新时代中国传统优秀文化成就思政素材智能检索学习系统V1.0</span>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()
const keyword = ref('')
const heroContent = ref<HTMLElement>()

const categories = [
  { icon: '📜', name: '诸子文化', desc: '儒道墨法、兵农纵横，诸子百家思想精华', count: '2,340' },
  { icon: '🏮', name: '传统非遗', desc: '剪纸皮影、年画泥塑，民间艺术活态传承', count: '1,860' },
  { icon: '🎊', name: '民俗', desc: '岁时节令、婚丧嫁娶，千年民俗风情画卷', count: '3,120' },
  { icon: '🔧', name: '传统技艺', desc: '陶瓷织造、冶铸髹饰，匠心独运巧夺天工', count: '2,750' },
  { icon: '🚩', name: '红色传统文化', desc: '革命文物、红色家书，赓续红色血脉基因', count: '1,580' },
  { icon: '📖', name: '历代人文典故', desc: '经史子集、典故轶事，五千年人文智慧结晶', count: '4,050' },
]

const featuredMaterials = [
  {
    icon: '📜',
    category: '诸子文化',
    title: '《论语》选读 — 学而篇',
    excerpt: '子曰："学而时习之，不亦说乎？有朋自远方来⋯" 适合中学思政课堂的经典导读素材。',
    dynasty: '春秋战国',
    stage: '高中',
    learnCount: '1.2万次学习',
  },
  {
    icon: '🏮',
    category: '传统非遗',
    title: '中国剪纸 — 指尖上的千年艺术',
    excerpt: '一把剪刀，一张红纸，剪出大千世界。了解剪纸背后的民俗文化与审美意蕴。',
    dynasty: '近现代',
    stage: '初中',
    learnCount: '8.6万次学习',
  },
  {
    icon: '🚩',
    category: '红色传统文化',
    title: '《可爱的中国》— 方志敏',
    excerpt: '"朋友，我相信，到那时，到处都是活跃跃的创造⋯" 感悟革命先辈的赤子之心。',
    dynasty: '近现代',
    stage: '高中',
    learnCount: '3.4万次学习',
  },
]

const features = [
  {
    icon: '📚',
    title: '分类浏览',
    desc: '六大文化品类系统分类，精准导航传统文化思政素材，让每一堂课都有据可依、有源可溯。',
  },
  {
    icon: '🎓',
    title: '学段适配',
    desc: '覆盖小学、初中、高中、大学各学段，根据教学大纲智能匹配适宜难度的传统文化教学内容。',
  },
  {
    icon: '🏛️',
    title: '思政融合',
    desc: '深挖传统文化的思政教育价值，每份素材标注思政融入点与教学建议，让文化传承与立德树人同向同行。',
  },
]

function handleSearch() {
  const q = keyword.value.trim() || undefined
  router.push({ path: '/login', query: q ? { redirect: '/search', q } : undefined })
}

onMounted(() => {
  setTimeout(() => {
    heroContent.value?.classList.add('visible')
  }, 200)
})
</script>

<style scoped>
/* ======== 通用区域 ======== */
.section {
  padding: 80px 60px;
  max-width: 1200px;
  margin: 0 auto;
}
.section-alt {
  background: rgba(0, 0, 0, 0.015);
  max-width: none;
  padding: 80px 60px;
}
.section-alt .section-header {
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}
.section-header {
  text-align: center;
  margin-bottom: 60px;
}
.section-header h2 {
  font-size: clamp(24px, 3.5vw, 32px);
  font-weight: 700;
  letter-spacing: 3px;
  color: var(--ink-black);
}
.divider {
  width: 60px;
  height: 1px;
  background: var(--ink-wash);
  margin: 20px auto;
}
.section-header p {
  color: var(--ink-gray);
  font-size: 14px;
  letter-spacing: 1px;
  margin-top: 8px;
}

/* ======== 英雄区 ======== */
.hero {
  position: relative;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 120px 40px 80px;
  overflow: hidden;
}

.ink-splash {
  position: absolute;
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

.hero-content {
  max-width: 900px;
  position: relative;
  z-index: 2;
}

.hero-tag {
  display: inline-block;
  padding: 4px 16px;
  border: 1px solid var(--ink-light);
  font-size: 12px;
  letter-spacing: 6px;
  color: var(--ink-gray);
  margin-bottom: 24px;
  text-transform: uppercase;
}

.hero h1 {
  font-size: clamp(36px, 6vw, 64px);
  font-weight: 900;
  line-height: 1.3;
  margin-bottom: 16px;
  letter-spacing: 4px;
  color: var(--ink-black);
}

.brush {
  position: relative;
  display: inline-block;
}
.brush::after {
  content: '';
  position: absolute;
  bottom: 2px;
  left: -5%;
  width: 0;
  height: 12px;
  background: rgba(0, 0, 0, 0.06);
  z-index: -1;
  animation: brushStroke 1.5s 0.8s var(--ease-ink) forwards;
}
@keyframes brushStroke {
  to { width: 110%; }
}

.hero p {
  font-size: clamp(14px, 2vw, 17px);
  color: var(--ink-gray);
  line-height: 1.8;
  margin-bottom: 40px;
  letter-spacing: 2px;
  white-space: nowrap;
}

/* ---- 搜索 ---- */
.search-box {
  display: flex;
  align-items: center;
  gap: 0;
  max-width: 560px;
  margin: 0 auto;
  background: var(--paper-white);
  border: 1px solid var(--ink-wash);
  border-radius: 2px;
  overflow: hidden;
  transition: all 0.4s var(--ease-ink);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}
.search-box:focus-within {
  border-color: var(--ink-dark);
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.08);
  transform: translateY(-1px);
}
.search-box input {
  flex: 1;
  padding: 16px 24px;
  border: none;
  background: transparent;
  font-family: var(--font-body);
  font-size: 15px;
  color: var(--ink-dark);
  outline: none;
  letter-spacing: 1px;
}
.search-box input::placeholder {
  color: var(--ink-light);
}
.search-box button {
  padding: 16px 28px;
  border: none;
  background: var(--ink-dark);
  color: var(--paper);
  font-family: var(--font-body);
  font-size: 15px;
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
  gap: 16px;
  justify-content: center;
  margin-top: 20px;
  flex-wrap: wrap;
}
.search-suggest a {
  font-size: 13px;
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
  bottom: 40px;
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
  height: 30px;
  background: linear-gradient(180deg, var(--ink-light), transparent);
}
@keyframes scrollFloat {
  0%, 100% { transform: translateX(-50%) translateY(0); opacity: 1; }
  50%      { transform: translateX(-50%) translateY(8px); opacity: 0.5; }
}

/* ======== 分类卡片 ======== */
.cat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 32px;
}
.cat-card {
  background: var(--paper-white);
  border: 1px solid transparent;
  padding: 40px 32px;
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
  background: radial-gradient(circle at 50% 0%, rgba(0, 0, 0, 0.02) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.5s;
}
.cat-card:hover {
  transform: translateY(-6px);
  border-color: var(--ink-wash);
}
.cat-card:hover::before {
  opacity: 1;
}
.cat-card .icon {
  font-size: 48px;
  display: block;
  margin-bottom: 20px;
  filter: grayscale(0.5);
  transition: filter 0.5s, transform 0.5s;
}
.cat-card:hover .icon {
  filter: grayscale(0);
  transform: scale(1.1);
}
.cat-card h3 {
  font-size: 20px;
  letter-spacing: 2px;
  margin-bottom: 8px;
  font-weight: 600;
  color: var(--ink-black);
}
.cat-card p {
  font-size: 13px;
  color: var(--ink-gray);
  line-height: 1.8;
}
.cat-card .count {
  display: inline-block;
  margin-top: 16px;
  font-size: 12px;
  color: var(--accent-seal);
  letter-spacing: 1px;
  border-bottom: 1px solid transparent;
  transition: border-color 0.3s;
}
.cat-card:hover .count {
  border-bottom-color: var(--accent-seal);
}

/* ======== 素材卡片 ======== */
.mat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 28px;
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
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06);
}
.mat-card-img {
  height: 180px;
  background: linear-gradient(135deg, #e8e4dc 0%, #d4d0c8 50%, #c8c4b8 100%);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 56px;
  opacity: 0.7;
}
.cover-icon {
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
  margin-bottom: 10px;
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
  gap: 16px;
  margin-top: 12px;
  font-size: 11px;
  color: var(--ink-light);
  letter-spacing: 1px;
}

/* ======== 功能介绍卡片 ======== */
.section-alt .feat-grid {
  max-width: 1200px;
  margin: 0 auto;
}
.feat-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 36px;
}
.feat-card {
  background: var(--paper-white);
  border: 1px solid transparent;
  padding: 48px 36px;
  text-align: center;
  transition: all 0.5s var(--ease-ink);
  position: relative;
  overflow: hidden;
}
.feat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 50% 0%, rgba(0, 0, 0, 0.02) 0%, transparent 60%);
  opacity: 0;
  transition: opacity 0.5s;
}
.feat-card:hover {
  transform: translateY(-6px);
  border-color: var(--ink-wash);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.06);
}
.feat-card:hover::before {
  opacity: 1;
}
.feat-icon {
  display: block;
  font-size: 56px;
  margin-bottom: 24px;
  filter: grayscale(0.4);
  transition: filter 0.5s, transform 0.5s;
}
.feat-card:hover .feat-icon {
  filter: grayscale(0);
  transform: scale(1.08);
}
.feat-card h3 {
  font-size: 20px;
  font-weight: 600;
  letter-spacing: 2px;
  color: var(--ink-black);
  margin-bottom: 12px;
}
.feat-card p {
  font-size: 14px;
  color: var(--ink-gray);
  line-height: 1.9;
  letter-spacing: 0.5px;
}

/* ======== 关于我们 ======== */
.about-content {
  max-width: 780px;
  margin: 0 auto;
  text-align: center;
}
.about-lead {
  font-family: var(--font-calligraphy);
  font-size: clamp(18px, 2.5vw, 24px);
  color: var(--ink-black);
  letter-spacing: 2px;
  line-height: 1.8;
  margin-bottom: 28px;
  white-space: nowrap;
}
.about-text {
  font-size: 14px;
  color: var(--ink-gray);
  line-height: 2.2;
  letter-spacing: 1px;
  margin-bottom: 40px;
}
.about-seal {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}
.seal-lg {
  width: 64px;
  height: 64px;
  border: 2px solid var(--accent-seal);
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 36px;
  font-weight: 700;
  color: var(--accent-seal);
  transform: rotate(-5deg);
}
.about-seal span {
  font-family: var(--font-calligraphy);
  font-size: 18px;
  color: var(--ink-gray);
  letter-spacing: 3px;
}

/* ======== 响应式 ======== */
@media (max-width: 768px) {
  .hero {
    padding: 100px 24px 60px;
  }
  .section {
    padding: 60px 24px;
  }
  .section-alt {
    padding: 60px 24px;
  }
}
</style>
