<template>
  <div class="home-page">
    <!-- 扫描线叠加 -->
    <div class="scanlines"></div>

    <!-- 粒子背景 -->
    <div class="particles">
      <span v-for="n in 50" :key="n" class="particle" :style="particleStyle(n)"></span>
    </div>

    <!-- 顶部光晕 -->
    <div class="glow-orb glow-orb-1"></div>
    <div class="glow-orb glow-orb-2"></div>

    <!-- 主内容 -->
    <div class="home-content">
      <!-- Logo 区域 -->
      <div class="logo-section">
        <div class="logo-icon">
          <svg viewBox="0 0 64 64" class="logo-svg">
            <defs>
              <linearGradient id="logoGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" stop-color="#00f0ff" />
                <stop offset="100%" stop-color="#ff00ff" />
              </linearGradient>
            </defs>
            <path d="M32 4 L56 20 L56 44 L32 60 L8 44 L8 20 Z" fill="none" stroke="url(#logoGrad)" stroke-width="2.5" />
            <path d="M32 16 L44 24 L44 40 L32 48 L20 40 L20 24 Z" fill="none" stroke="url(#logoGrad)" stroke-width="1.5" opacity="0.6" />
            <circle cx="32" cy="32" r="5" fill="url(#logoGrad)" opacity="0.8" />
            <line x1="32" y1="4" x2="32" y2="12" stroke="#00f0ff" stroke-width="1" opacity="0.4" />
            <line x1="56" y1="20" x2="48" y2="22" stroke="#00f0ff" stroke-width="1" opacity="0.4" />
            <line x1="56" y1="44" x2="48" y2="42" stroke="#ff00ff" stroke-width="1" opacity="0.4" />
            <line x1="32" y1="60" x2="32" y2="52" stroke="#ff00ff" stroke-width="1" opacity="0.4" />
            <line x1="8" y1="44" x2="16" y2="42" stroke="#ff00ff" stroke-width="1" opacity="0.4" />
            <line x1="8" y1="20" x2="16" y2="22" stroke="#00f0ff" stroke-width="1" opacity="0.4" />
          </svg>
        </div>
        <h1 class="main-title" data-text="XIANGZI_AI">
          <span class="title-text">XIANGZI_AI</span>
          <span class="title-cursor">▌</span>
        </h1>
        <p class="subtitle">
          <span class="prompt-char">&gt;</span> 新一代智能AI助手平台
          <span class="subtitle-dot">.</span>
          <span class="subtitle-dot">.</span>
          <span class="subtitle-dot">.</span>
        </p>
      </div>

      <!-- 应用卡片 -->
      <div class="cards-grid">
        <!-- 卡片 1: AI 恋爱大师 -->
        <router-link to="/love-chat" class="app-card app-card-love">
          <div class="card-header">
            <span class="card-dot dot-red"></span>
            <span class="card-dot dot-yellow"></span>
            <span class="card-dot dot-green"></span>
            <span class="card-title-bar">love_master.exe</span>
          </div>
          <div class="card-body">
            <div class="card-avatar avatar-love">
              <svg viewBox="0 0 48 48" class="avatar-svg">
                <defs>
                  <linearGradient id="loveGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" stop-color="#ff6b9d" />
                    <stop offset="100%" stop-color="#c44569" />
                  </linearGradient>
                </defs>
                <path d="M24 42C24 42 6 28 6 16C6 9.4 11.4 4 18 4C21.6 4 24.9 5.9 27 8.5C29.1 5.9 32.4 4 36 4C42.6 4 48 9.4 48 16C48 28 30 42 24 42Z" fill="url(#loveGrad)" />
              </svg>
            </div>
            <div class="card-text">
              <h3 class="card-name">
                <span class="terminal-arrow">&gt;</span> AI 恋爱大师
              </h3>
              <p class="card-desc">深度学习情感分析引擎 · 7×24小时在线 · 秒级响应</p>
              <div class="card-tags">
                <span class="tag">情感分析</span>
                <span class="tag">实时对话</span>
                <span class="tag">智能建议</span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <span class="card-status">● ONLINE</span>
            <span class="card-enter">[ ENTER ]</span>
          </div>
        </router-link>

        <!-- 卡片 2: AI 超级智能体 -->
        <router-link to="/manus-chat" class="app-card app-card-manus">
          <div class="card-header">
            <span class="card-dot dot-red"></span>
            <span class="card-dot dot-yellow"></span>
            <span class="card-dot dot-green"></span>
            <span class="card-title-bar">super_agent.exe</span>
          </div>
          <div class="card-body">
            <div class="card-avatar avatar-manus">
              <svg viewBox="0 0 48 48" class="avatar-svg">
                <defs>
                  <linearGradient id="manusGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" stop-color="#a78bfa" />
                    <stop offset="100%" stop-color="#7c3aed" />
                  </linearGradient>
                </defs>
                <rect x="6" y="12" width="36" height="26" rx="5" fill="none" stroke="url(#manusGrad)" stroke-width="2.5" />
                <line x1="6" y1="20" x2="42" y2="20" stroke="url(#manusGrad)" stroke-width="1.5" opacity="0.5" />
                <circle cx="18" cy="32" r="3" fill="url(#manusGrad)" opacity="0.8" />
                <circle cx="30" cy="32" r="3" fill="url(#manusGrad)" opacity="0.8" />
                <rect x="14" y="8" width="4" height="6" rx="2" fill="url(#manusGrad)" opacity="0.6" />
                <rect x="30" y="8" width="4" height="6" rx="2" fill="url(#manusGrad)" opacity="0.6" />
              </svg>
            </div>
            <div class="card-text">
              <h3 class="card-name">
                <span class="terminal-arrow">&gt;</span> AI 超级智能体
              </h3>
              <p class="card-desc">多智能体编排引擎 · 工具链调用 · 复杂任务自主规划</p>
              <div class="card-tags">
                <span class="tag">多智能体</span>
                <span class="tag">工具调用</span>
                <span class="tag">自主规划</span>
              </div>
            </div>
          </div>
          <div class="card-footer">
            <span class="card-status">● ONLINE</span>
            <span class="card-enter">[ ENTER ]</span>
          </div>
        </router-link>
      </div>

      <!-- 底部状态栏 -->
      <div class="status-bar">
        <span class="status-item"><span class="status-label">SYS:</span> NORMAL</span>
        <span class="status-item"><span class="status-label">NODES:</span> 02 ACTIVE</span>
        <span class="status-item"><span class="status-label">LAT:</span> &lt;50ms</span>
        <span class="status-item"><span class="status-label">UPTIME:</span> 99.99%</span>
      </div>
    </div>

    <!-- 底部版权 -->
    <footer class="site-footer">
      <div class="footer-content">
        <div class="footer-left">
          <span class="footer-prompt">&gt;</span> Xiangzi AI Agent
        </div>
        <div class="footer-right">
          <span>&copy; {{ currentYear }} Xiangzi. All rights reserved.</span>
          <span class="footer-sep">|</span>
<!--          <span>Powered by SpringBoot + Vue3</span>-->
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useSEO } from '../composables/useSEO.js'

useSEO({
  title: '首页',
  description: 'Xiangzi AI Agent - 新一代智能AI助手平台。赛博朋克风格的AI应用中心，提供AI恋爱大师、AI超级智能体等前沿AI服务。',
})

const currentYear = computed(() => new Date().getFullYear())

// 粒子随机样式
function randomBetween(min, max) {
  return Math.random() * (max - min) + min
}
function particleStyle(n) {
  const size = randomBetween(1, 3)
  const left = randomBetween(0, 100)
  const duration = randomBetween(3, 8)
  const delay = randomBetween(0, 5)
  const opacity = randomBetween(0.1, 0.4)
  return {
    width: `${size}px`,
    height: `${size}px`,
    left: `${left}%`,
    animationDuration: `${duration}s`,
    animationDelay: `${delay}s`,
    opacity,
    '--drift': `${randomBetween(-30, 30)}px`,
  }
}
</script>

<style scoped>
/* ============================================================
   主页 — 赛博朋克·极客风格
   ============================================================ */

.home-page {
  position: relative;
  min-height: 100vh;
  background: var(--color-bg-primary);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* ---- 网格背景 ---- */
.home-page::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 240, 255, 0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 240, 255, 0.04) 1px, transparent 1px);
  background-size: 60px 60px;
  pointer-events: none;
  z-index: 1;
}

/* ---- 扫描线叠加 ---- */
.scanlines {
  position: fixed;
  inset: 0;
  pointer-events: none;
  z-index: 100;
  background: repeating-linear-gradient(
    0deg,
    transparent,
    transparent 3px,
    rgba(0, 0, 0, 0.03) 3px,
    rgba(0, 0, 0, 0.03) 4px
  );
}

/* ---- 光晕 ---- */
.glow-orb {
  position: absolute;
  border-radius: 50%;
  filter: blur(120px);
  pointer-events: none;
  z-index: 0;
  opacity: 0.3;
  animation: orbPulse 6s ease-in-out infinite alternate;
}

.glow-orb-1 {
  top: -200px;
  left: -100px;
  width: 600px;
  height: 600px;
  background: var(--color-cyan);
}

.glow-orb-2 {
  bottom: -200px;
  right: -100px;
  width: 500px;
  height: 500px;
  background: var(--color-magenta);
  animation-delay: 3s;
}

@keyframes orbPulse {
  0% {
    opacity: 0.2;
    transform: scale(1);
  }
  100% {
    opacity: 0.4;
    transform: scale(1.2);
  }
}

/* ---- 粒子动画 ---- */
.particles {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 2;
}

.particle {
  position: absolute;
  bottom: -10px;
  background: var(--color-cyan);
  border-radius: 50%;
  animation: floatUp linear infinite;
  box-shadow: 0 0 6px var(--color-cyan);
}

@keyframes floatUp {
  0% {
    transform: translateY(0) translateX(0);
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  90% {
    opacity: 0.5;
  }
  100% {
    transform: translateY(-100vh) translateX(var(--drift, 20px));
    opacity: 0;
  }
}

/* ---- 主内容区 ---- */
.home-content {
  position: relative;
  z-index: 10;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--space-xl) var(--space-lg);
  max-width: 900px;
  margin: 0 auto;
  width: 100%;
}

/* ---- Logo 区域 ---- */
.logo-section {
  text-align: center;
  margin-bottom: var(--space-3xl);
}

.logo-icon {
  margin-bottom: var(--space-lg);
  display: inline-block;
  animation: logoFloat 4s ease-in-out infinite;
}

@keyframes logoFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.logo-svg {
  width: 80px;
  height: 80px;
  filter: drop-shadow(0 0 20px rgba(0, 240, 255, 0.5));
}

/* ---- 标题 故障效果 ---- */
.main-title {
  font-family: var(--font-mono);
  font-size: clamp(36px, 6vw, 64px);
  font-weight: 900;
  color: var(--color-cyan);
  letter-spacing: 0.15em;
  position: relative;
  display: inline-block;
  margin-bottom: var(--space-md);
  text-shadow: 0 0 30px rgba(0, 240, 255, 0.5), 0 0 60px rgba(0, 240, 255, 0.2);
}

/* 故障效果 ::before */
.main-title::before {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: 2px;
  width: 100%;
  height: 100%;
  color: var(--color-magenta);
  text-shadow: -2px 0 var(--color-magenta);
  animation: glitch1 2.5s infinite linear alternate-reverse;
  clip-path: polygon(0 15%, 100% 15%, 100% 35%, 0 35%);
  opacity: 0.7;
}

/* 故障效果 ::after */
.main-title::after {
  content: attr(data-text);
  position: absolute;
  top: 0;
  left: -2px;
  width: 100%;
  height: 100%;
  color: var(--color-cyan);
  text-shadow: 2px 0 var(--color-cyan);
  animation: glitch2 3s infinite linear alternate-reverse;
  clip-path: polygon(0 65%, 100% 65%, 100% 85%, 0 85%);
  opacity: 0.7;
}

@keyframes glitch1 {
  0% { clip-path: polygon(0 20%, 100% 20%, 100% 40%, 0 40%); transform: translate(0); }
  25% { clip-path: polygon(0 50%, 100% 50%, 100% 55%, 0 55%); transform: translate(-3px, 1px); }
  50% { clip-path: polygon(0 10%, 100% 10%, 100% 25%, 0 25%); transform: translate(3px); }
  75% { clip-path: polygon(0 70%, 100% 70%, 100% 80%, 0 80%); transform: translate(-2px, -1px); }
  100% { clip-path: polygon(0 30%, 100% 30%, 100% 45%, 0 45%); transform: translate(2px); }
}

@keyframes glitch2 {
  0% { clip-path: polygon(0 60%, 100% 60%, 100% 75%, 0 75%); transform: translate(0); }
  33% { clip-path: polygon(0 5%, 100% 5%, 100% 15%, 0 15%); transform: translate(2px, -1px); }
  66% { clip-path: polygon(0 80%, 100% 80%, 100% 95%, 0 95%); transform: translate(-3px); }
  100% { clip-path: polygon(0 40%, 100% 40%, 100% 55%, 0 55%); transform: translate(3px, 1px); }
}

.title-cursor {
  animation: blink 0.8s step-end infinite;
  color: var(--color-cyan);
  font-weight: 100;
}

@keyframes blink {
  50% { opacity: 0; }
}

/* ---- 副标题 ---- */
.subtitle {
  font-family: var(--font-mono);
  font-size: clamp(14px, 2.5vw, 18px);
  color: var(--color-text-secondary);
  letter-spacing: 0.1em;
}

.prompt-char {
  color: var(--color-green);
  margin-right: 4px;
}

.subtitle-dot {
  animation: dotPulse 1.5s ease-in-out infinite;
  color: var(--color-text-muted);
}

.subtitle-dot:nth-child(2) { animation-delay: 0.3s; }
.subtitle-dot:nth-child(3) { animation-delay: 0.6s; }

@keyframes dotPulse {
  0%, 100% { opacity: 0.3; }
  50% { opacity: 1; }
}

/* ---- 卡片网格 ---- */
.cards-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: var(--space-xl);
  width: 100%;
  max-width: 700px;
}

@media (max-width: 640px) {
  .cards-grid {
    grid-template-columns: 1fr;
    gap: var(--space-lg);
  }
}

/* ---- 终端卡片 ---- */
.app-card {
  display: flex;
  flex-direction: column;
  background: var(--color-bg-card);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-md);
  overflow: hidden;
  transition: all var(--transition-normal);
  position: relative;
}

.app-card::before {
  content: '';
  position: absolute;
  inset: -1px;
  border-radius: var(--radius-md);
  background: linear-gradient(135deg, transparent 40%, rgba(0, 240, 255, 0.15), transparent 60%);
  opacity: 0;
  transition: opacity var(--transition-normal);
  z-index: -1;
}

.app-card:hover {
  border-color: var(--color-border-glow);
  box-shadow: var(--shadow-neon-cyan), var(--shadow-card);
  transform: translateY(-4px);
}

.app-card:hover::before {
  opacity: 1;
}

.app-card-love:hover {
  border-color: rgba(255, 107, 157, 0.4);
  box-shadow: 0 0 30px rgba(255, 107, 157, 0.25), var(--shadow-card);
}

/* ---- 卡片标题栏 ---- */
.card-header {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 14px;
  background: var(--color-bg-secondary);
  border-bottom: 1px solid var(--color-border);
}

.card-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.dot-red {
  background: #ff5f57;
}

.dot-yellow {
  background: #febc2e;
}

.dot-green {
  background: #28c840;
}

.card-title-bar {
  margin-left: var(--space-sm);
  font-family: var(--font-mono);
  font-size: 11px;
  color: var(--color-text-muted);
  flex: 1;
}

/* ---- 卡片内容 ---- */
.card-body {
  display: flex;
  align-items: center;
  gap: var(--space-lg);
  padding: var(--space-lg);
  flex: 1;
}

@media (max-width: 640px) {
  .card-body {
    gap: var(--space-md);
    padding: var(--space-md);
  }
}

.card-avatar {
  width: 64px;
  height: 64px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
}

.avatar-love {
  background: linear-gradient(135deg, rgba(255, 107, 157, 0.2), rgba(196, 69, 105, 0.1));
  box-shadow: 0 0 20px rgba(255, 107, 157, 0.2);
}

.avatar-manus {
  background: linear-gradient(135deg, rgba(167, 139, 250, 0.2), rgba(124, 58, 237, 0.1));
  box-shadow: 0 0 20px rgba(167, 139, 250, 0.2);
}

.avatar-svg {
  width: 40px;
  height: 40px;
}

.card-text {
  flex: 1;
  min-width: 0;
}

.card-name {
  font-family: var(--font-mono);
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text-primary);
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.terminal-arrow {
  color: var(--color-green);
  margin-right: 4px;
}

.card-desc {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: var(--space-sm);
  line-height: 1.5;
}

.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag {
  font-family: var(--font-mono);
  font-size: 10px;
  padding: 3px 8px;
  border: 1px solid var(--color-border);
  border-radius: 4px;
  color: var(--color-text-muted);
  background: rgba(0, 240, 255, 0.05);
}

.app-card-love .tag {
  border-color: rgba(255, 107, 157, 0.2);
  color: rgba(255, 107, 157, 0.7);
}

.app-card-manus .tag {
  border-color: rgba(167, 139, 250, 0.2);
  color: rgba(167, 139, 250, 0.7);
}

/* ---- 卡片底栏 ---- */
.card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 14px;
  border-top: 1px solid var(--color-border);
  font-family: var(--font-mono);
  font-size: 10px;
}

.card-status {
  color: var(--color-green);
  letter-spacing: 0.05em;
}

.card-enter {
  color: var(--color-text-muted);
  transition: color var(--transition-fast);
}

.app-card:hover .card-enter {
  color: var(--color-cyan);
}

/* ---- 状态栏 ---- */
.status-bar {
  display: flex;
  gap: var(--space-xl);
  margin-top: var(--space-2xl);
  padding: var(--space-md) var(--space-xl);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  background: var(--color-bg-card);
  font-family: var(--font-mono);
  font-size: 11px;
}

@media (max-width: 640px) {
  .status-bar {
    gap: var(--space-md);
    padding: var(--space-sm) var(--space-md);
    flex-wrap: wrap;
    justify-content: center;
  }
}

.status-item {
  color: var(--color-text-secondary);
}

.status-label {
  color: var(--color-text-muted);
  margin-right: 4px;
}

/* ---- 页脚 ---- */
.site-footer {
  position: relative;
  z-index: 10;
  border-top: 1px solid var(--color-border);
  background: var(--color-bg-secondary);
  padding: var(--space-md) var(--space-xl);
  font-family: var(--font-mono);
  font-size: 12px;
}

.footer-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  flex-wrap: wrap;
  gap: var(--space-sm);
}

@media (max-width: 640px) {
  .footer-content {
    flex-direction: column;
    text-align: center;
  }
}

.footer-left {
  color: var(--color-text-secondary);
}

.footer-prompt {
  color: var(--color-green);
  margin-right: 6px;
}

.footer-right {
  color: var(--color-text-muted);
  display: flex;
  gap: var(--space-sm);
  flex-wrap: wrap;
  justify-content: center;
}

.footer-sep {
  color: var(--color-border);
}
</style>
