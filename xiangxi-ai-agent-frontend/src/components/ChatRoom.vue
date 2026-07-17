<template>
  <div class="chat-room">
    <!-- 顶部导航 -->
    <header class="chat-header">
      <button class="back-btn" @click="$router.push('/')">
        <span class="back-arrow">◀</span>
        <span class="back-text">返回</span>
      </button>
      <div class="header-center">
        <div class="ai-avatar">
          <svg viewBox="0 0 48 48" class="ai-avatar-svg">
            <defs>
              <linearGradient id="_aiGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" :stop-color="avatarColor1" />
                <stop offset="100%" :stop-color="avatarColor2" />
              </linearGradient>
            </defs>
            <g v-html="aiAvatarSVG"></g>
          </svg>
        </div>
        <h2 class="header-title">{{ title }}</h2>
        <span class="online-badge">ONLINE</span>
      </div>
      <div class="header-right">
        <span class="chat-id" v-if="chatId" :title="chatId">
          ID: {{ shortChatId }}
        </span>
      </div>
    </header>

    <!-- 消息列表 -->
    <div class="messages-container" ref="messagesRef">
      <!-- 空状态 -->
      <div v-if="messages.length === 0" class="empty-state">
        <div class="empty-avatar">
          <svg viewBox="0 0 48 48" class="empty-svg">
            <defs>
              <linearGradient id="_aiGrad" x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%" :stop-color="avatarColor1" />
                <stop offset="100%" :stop-color="avatarColor2" />
              </linearGradient>
            </defs>
            <g v-html="aiAvatarSVG"></g>
          </svg>
        </div>
        <p class="empty-text">{{ emptyText }}</p>
        <p class="empty-hint">在下方输入消息开始对话</p>
      </div>

      <!-- 消息气泡 -->
      <div
        v-for="(msg, index) in messages"
        :key="msg.id"
        :class="['message-row', msg.role === 'user' ? 'row-user' : 'row-ai']"
      >
        <!-- AI 头像 -->
        <div v-if="msg.role === 'ai'" class="avatar-wrapper">
          <div class="avatar-bubble avatar-bubble-ai">
            <svg viewBox="0 0 48 48" class="avatar-svg-small">
              <g v-html="aiAvatarSVG"></g>
            </svg>
          </div>
        </div>

        <!-- 消息内容 -->
        <div :class="['message-bubble', msg.role === 'user' ? 'bubble-user' : 'bubble-ai']">
          <div class="bubble-name" v-if="msg.role === 'ai'">{{ aiName }}</div>
          <div class="message-content">
            {{ msg.content }}
            <span
              v-if="msg.role === 'ai' && isLastMessage(index) && streaming"
              class="cursor-blink"
            >▌</span>
          </div>
          <div class="bubble-time">{{ formatTime(msg.time) }}</div>
        </div>

        <!-- 用户头像 -->
        <div v-if="msg.role === 'user'" class="avatar-wrapper">
          <div class="avatar-bubble avatar-bubble-user">
            <svg viewBox="0 0 48 48" class="avatar-svg-small">
              <circle cx="24" cy="18" r="8" fill="#74b9ff" />
              <ellipse cx="24" cy="44" rx="17" ry="12" fill="#74b9ff" />
            </svg>
          </div>
        </div>
      </div>

      <!-- 滚动锚点 -->
      <div ref="scrollAnchorRef"></div>
    </div>

    <!-- 输入区 -->
    <div class="input-area">
      <div class="input-wrapper">
        <input
          ref="inputRef"
          v-model="inputText"
          class="input-field"
          :placeholder="streaming ? 'AI 正在回复中...' : '输入消息，按 Enter 发送...'"
          :disabled="streaming"
          @keydown.enter="sendMessage"
        />
        <!-- 停止按钮：流式输出期间显示 -->
        <button
          v-if="streaming"
          class="stop-btn"
          @click="stopGeneration"
          title="停止生成"
        >
          <span class="stop-icon">■</span>
        </button>
        <!-- 发送按钮：非流式期间显示 -->
        <button
          v-else
          class="send-btn"
          :disabled="!inputText.trim()"
          @click="sendMessage"
        >
          <span class="send-icon">▶</span>
        </button>
      </div>
    </div>

    <!-- 底部版权 -->
    <footer class="chat-footer">
      <span>&copy; {{ currentYear }} Xiangzi AI — {{ title }}</span>
    </footer>
  </div>
</template>

<script setup>
import { ref, computed, nextTick, onUnmounted, watch, onMounted } from 'vue'
import { useSEO } from '../composables/useSEO.js'

const props = defineProps({
  title: { type: String, required: true },
  endpoint: { type: String, required: true },
  needChatId: { type: Boolean, default: false },
  emptyText: { type: String, default: '开始一段新的对话吧' },
  aiName: { type: String, default: 'AI' },
  seoTitle: { type: String, default: '' },
  seoDesc: { type: String, default: '' },
  avatarColor1: { type: String, default: '#00f0ff' },
  avatarColor2: { type: String, default: '#b44dff' },
  aiAvatarSVG: { type: String, default: '' },
  useCancelEndpoint: { type: Boolean, default: false },
})

// SEO
useSEO({
  title: props.seoTitle || props.title,
  description: props.seoDesc || `${props.title} - Xiangzi AI 智能助手，实时AI对话`,
})

const messages = ref([])
const inputText = ref('')
const streaming = ref(false)
const chatId = ref('')
let abortController = null
let msgIdCounter = 0
let cancelToken = ''

const messagesRef = ref(null)
const scrollAnchorRef = ref(null)
const inputRef = ref(null)

const currentYear = computed(() => new Date().getFullYear())


const shortChatId = computed(() => {
  if (!chatId.value) return ''
  return chatId.value.length > 12
    ? chatId.value.substring(0, 8) + '...'
    : chatId.value
})

// 生成随机 chatId
function generateChatId() {
  return 'chat_' + Date.now() + '_' + Math.random().toString(36).substring(2, 10)
}

// 格式化时间
function formatTime(timestamp) {
  if (!timestamp) return ''
  const d = new Date(timestamp)
  const hh = String(d.getHours()).padStart(2, '0')
  const mm = String(d.getMinutes()).padStart(2, '0')
  return `${hh}:${mm}`
}

// 自动滚动到底部
async function scrollToBottom() {
  await nextTick()
  if (scrollAnchorRef.value) {
    scrollAnchorRef.value.scrollIntoView({ behavior: 'smooth' })
  }
}

// 监听消息变化自动滚动
watch(messages, () => {
  scrollToBottom()
}, { deep: true })

// 判断是否是最后一条消息
function isLastMessage(index) {
  return index === messages.value.length - 1
}

function addMessage(role, content = '') {
  messages.value.push({
    id: ++msgIdCounter,
    role,
    content,
    time: Date.now(),
  })
}

// 解析 SSE 数据行
function parseSSEData(line) {
  let data = line
  if (data.startsWith('data:')) {
    data = data.substring(5)
    if (data.startsWith(' ')) {
      data = data.substring(1)
    }
  }
  if (data.startsWith('"') && data.endsWith('"')) {
    try {
      data = JSON.parse(data)
    } catch {
      // 保持原样
    }
  }
  return data
}

// 处理 SSE 消息块
function processSSEPart(part) {
  const lines = part.split('\n')
  for (const line of lines) {
    if (line.startsWith('data:')) {
      const data = parseSSEData(line)
      const lastMsg = messages.value[messages.value.length - 1]
      if (lastMsg && lastMsg.role === 'ai') {
        lastMsg.content += data
      }
    }
  }
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || streaming.value) return

  if (abortController) {
    abortController.abort()
  }

  addMessage('user', text)
  inputText.value = ''

  addMessage('ai', '')
  streaming.value = true

  // 生成取消令牌：用于支持手动停止 AI 回复
  cancelToken = 'cancel_' + Date.now() + '_' + Math.random().toString(36).substring(2, 10)

  const params = new URLSearchParams({ message: text, cancelToken })
  if (props.needChatId) {
    if (!chatId.value) {
      chatId.value = generateChatId()
    }
    params.set('chatId', chatId.value)
  }

  const url = `/api${props.endpoint}?${params.toString()}`

  abortController = new AbortController()

  try {
    const response = await fetch(url, {
      signal: abortController.signal,
    })

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let buffer = ''

    while (true) {
      const { value, done } = await reader.read()
      if (done) break
      buffer += decoder.decode(value, { stream: true })
      const parts = buffer.split('\n\n')
      buffer = parts.pop() || ''
      parts.forEach(processSSEPart)
    }

    if (buffer.trim()) {
      processSSEPart(buffer)
    }
  } catch (err) {
    if (err.name === 'AbortError') return
    console.error('SSE 请求失败:', err)
    const lastMsg = messages.value[messages.value.length - 1]
    if (lastMsg && lastMsg.role === 'ai' && !lastMsg.content) {
      lastMsg.content = '抱歉，请求出现异常，请重试。'
    }
  } finally {
    streaming.value = false
    abortController = null
  }
}

// 手动停止 AI 生成
async function stopGeneration() {
  if (!streaming.value) return

  // 1. 断开 fetch 连接（对所有 SSE 端点有效）
  if (abortController) {
    abortController.abort()
  }

  // 2. SseEmitter 端点需要显式通知后端中断 Agent 循环
  if (props.useCancelEndpoint && cancelToken) {
    try {
      await fetch(`/api/ai/stop?cancelToken=${encodeURIComponent(cancelToken)}`, {
        method: 'POST',
      })
    } catch {
      // 忽略网络错误，abortController.abort() 已断开连接
    }
  }

  // 3. 更新 UI 状态
  streaming.value = false

  // 4. 在最后一条 AI 消息末尾标记已停止
  const lastMsg = messages.value[messages.value.length - 1]
  if (lastMsg && lastMsg.role === 'ai') {
    if (!lastMsg.content) {
      lastMsg.content = '已停止生成。'
    } else {
      lastMsg.content += '\n\n[已停止生成]'
    }
  }
}

onMounted(() => {
  nextTick(() => {
    inputRef.value?.focus()
  })
})

onUnmounted(() => {
  if (abortController) {
    abortController.abort()
  }
  // 组件卸载时尽力通知后端取消（fire-and-forget）
  if (props.useCancelEndpoint && cancelToken && streaming.value) {
    fetch(`/api/ai/stop?cancelToken=${encodeURIComponent(cancelToken)}`, {
      method: 'POST',
    }).catch(() => {})
  }
})
</script>

<style scoped>
/* ============================================================
   聊天室 — 赛博朋克·极客风格 · 响应式
   ============================================================ */

.chat-room {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background: var(--color-bg-primary);
  position: relative;
}

/* ---- 背景网格 ---- */
.chat-room::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(0, 240, 255, 0.025) 1px, transparent 1px),
    linear-gradient(90deg, rgba(0, 240, 255, 0.025) 1px, transparent 1px);
  background-size: 50px 50px;
  pointer-events: none;
  z-index: 0;
}

/* ===== 头部 ===== */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 12px 20px;
  background: var(--color-bg-secondary);
  border-bottom: 1px solid var(--color-border);
  flex-shrink: 0;
  z-index: 10;
  position: relative;
}

@media (max-width: 640px) {
  .chat-header {
    padding: 10px 12px;
  }
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: transparent;
  border: 1px solid var(--color-border);
  border-radius: var(--radius-sm);
  padding: 6px 14px;
  font-size: 13px;
  color: var(--color-text-secondary);
  transition: all var(--transition-fast);
  font-family: var(--font-mono);
  cursor: pointer;
}

.back-btn:hover {
  border-color: var(--color-cyan);
  color: var(--color-cyan);
  box-shadow: 0 0 12px rgba(0, 240, 255, 0.15);
}

.back-arrow {
  font-size: 10px;
}

@media (max-width: 640px) {
  .back-text {
    display: none;
  }
  .back-btn {
    padding: 6px 10px;
  }
}

.header-center {
  display: flex;
  align-items: center;
  gap: 10px;
}

.ai-avatar {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(0, 240, 255, 0.15), rgba(180, 77, 255, 0.1));
  border: 1px solid rgba(0, 240, 255, 0.2);
  flex-shrink: 0;
}

.ai-avatar-svg {
  width: 24px;
  height: 24px;
}

.header-title {
  font-family: var(--font-mono);
  font-size: 16px;
  font-weight: 700;
  color: var(--color-text-primary);
  white-space: nowrap;
}

@media (max-width: 640px) {
  .header-title {
    font-size: 14px;
  }
}

.online-badge {
  font-family: var(--font-mono);
  font-size: 9px;
  padding: 2px 8px;
  border: 1px solid var(--color-green);
  border-radius: 4px;
  color: var(--color-green);
  letter-spacing: 0.1em;
  animation: onlinePulse 2s ease-in-out infinite;
}

@keyframes onlinePulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.header-right {
  min-width: 60px;
  text-align: right;
}

.chat-id {
  font-family: var(--font-mono);
  font-size: 10px;
  color: var(--color-text-muted);
  background: var(--color-bg-input);
  padding: 3px 8px;
  border-radius: 4px;
}

@media (max-width: 640px) {
  .chat-id {
    display: none;
  }
}

/* ===== 消息区域 ===== */
.messages-container {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  position: relative;
  z-index: 1;
  scroll-behavior: smooth;
}

@media (max-width: 640px) {
  .messages-container {
    padding: 12px;
    gap: 12px;
  }
}

/* ---- 空状态 ---- */
.empty-state {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 40px 20px;
}

.empty-avatar {
  width: 72px;
  height: 72px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(0, 240, 255, 0.1), rgba(180, 77, 255, 0.05));
  border: 1px solid rgba(0, 240, 255, 0.15);
  margin-bottom: 8px;
}

.empty-svg {
  width: 48px;
  height: 48px;
  opacity: 0.6;
}

.empty-text {
  font-size: 16px;
  color: var(--color-text-secondary);
}

.empty-hint {
  font-family: var(--font-mono);
  font-size: 12px;
  color: var(--color-text-muted);
}

/* ---- 消息行 ---- */
.message-row {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  max-width: 75%;
  animation: msgFadeIn 0.3s ease;
}

@media (max-width: 640px) {
  .message-row {
    max-width: 88%;
    gap: 6px;
  }
}

@keyframes msgFadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.row-user {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.row-ai {
  align-self: flex-start;
}

/* ---- 头像 ---- */
.avatar-wrapper {
  flex-shrink: 0;
}

.avatar-bubble {
  width: 34px;
  height: 34px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.avatar-bubble-ai {
  background: linear-gradient(135deg, rgba(0, 240, 255, 0.2), rgba(180, 77, 255, 0.1));
  border: 1px solid rgba(0, 240, 255, 0.2);
}

.avatar-bubble-user {
  background: linear-gradient(135deg, rgba(116, 185, 255, 0.25), rgba(9, 132, 227, 0.15));
  border: 1px solid rgba(116, 185, 255, 0.3);
}

.avatar-svg-small {
  width: 22px;
  height: 22px;
}

/* ---- 消息气泡 ---- */
.message-bubble {
  padding: 12px 16px;
  border-radius: var(--radius-lg);
  line-height: 1.65;
  font-size: 14px;
  word-break: break-word;
  position: relative;
}

@media (max-width: 640px) {
  .message-bubble {
    padding: 10px 14px;
    font-size: 13px;
  }
}

.bubble-user {
  background: linear-gradient(135deg, #1a3a5c, #0d2137);
  color: var(--color-text-primary);
  border-bottom-right-radius: 6px;
  border: 1px solid rgba(0, 180, 255, 0.25);
}

.bubble-ai {
  background: var(--color-bg-card);
  color: var(--color-text-primary);
  border-bottom-left-radius: 6px;
  border: 1px solid var(--color-border);
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
}

.bubble-name {
  font-family: var(--font-mono);
  font-size: 10px;
  color: var(--color-cyan);
  margin-bottom: 4px;
  letter-spacing: 0.05em;
}

.message-content {
  white-space: pre-wrap;
  word-wrap: break-word;
}

.bubble-time {
  font-family: var(--font-mono);
  font-size: 10px;
  color: var(--color-text-muted);
  text-align: right;
  margin-top: 4px;
  opacity: 0.6;
}

/* ---- 光标闪烁 ---- */
.cursor-blink {
  animation: cursorBlink 0.7s step-end infinite;
  color: var(--color-cyan);
}

@keyframes cursorBlink {
  50% { opacity: 0; }
}

/* ===== 输入区 ===== */
.input-area {
  flex-shrink: 0;
  padding: 12px 20px;
  background: var(--color-bg-secondary);
  border-top: 1px solid var(--color-border);
  position: relative;
  z-index: 10;
}

@media (max-width: 640px) {
  .input-area {
    padding: 10px 12px;
  }
}

.input-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  background: var(--color-bg-input);
  border: 1px solid var(--color-border);
  border-radius: var(--radius-xl);
  padding: 4px 4px 4px 18px;
  transition: border-color var(--transition-fast);
}

.input-wrapper:focus-within {
  border-color: var(--color-cyan);
  box-shadow: 0 0 15px rgba(0, 240, 255, 0.1);
}

.input-field {
  flex: 1;
  background: transparent;
  border: none;
  color: var(--color-text-primary);
  font-size: 14px;
  padding: 8px 0;
  outline: none;
}

.input-field::placeholder {
  color: var(--color-text-muted);
}

.input-field:disabled {
  opacity: 0.5;
}

/* ---- 停止按钮 ---- */
.stop-btn {
  width: 38px;
  height: 38px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff6b6b, #ee5a24);
  color: #fff;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition-fast);
  flex-shrink: 0;
  animation: stopPulse 1.5s ease-in-out infinite;
}

.stop-btn:hover {
  transform: scale(1.08);
  box-shadow: 0 0 20px rgba(255, 107, 107, 0.5);
}

@keyframes stopPulse {
  0%, 100% { box-shadow: 0 0 8px rgba(255, 107, 107, 0.3); }
  50% { box-shadow: 0 0 18px rgba(255, 107, 107, 0.6); }
}

.send-btn {
  width: 38px;
  height: 38px;
  border: none;
  border-radius: 50%;
  background: linear-gradient(135deg, var(--color-cyan), var(--color-purple));
  color: var(--color-text-inverse);
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition-fast);
  flex-shrink: 0;
}

.send-btn:hover:not(:disabled) {
  transform: scale(1.08);
  box-shadow: 0 0 20px rgba(0, 240, 255, 0.4);
}

.send-btn:disabled {
  opacity: 0.3;
  cursor: not-allowed;
  transform: none;
}

.send-icon {
  font-size: 12px;
}

.send-loading {
  animation: loadingSpin 1s linear infinite;
}

@keyframes loadingSpin {
  to { transform: rotate(360deg); }
}

/* ===== 底部页脚 ===== */
.chat-footer {
  position: relative;
  z-index: 10;
  background: var(--color-bg-secondary);
  border-top: 1px solid var(--color-border);
  text-align: center;
  padding: 6px;
  font-family: var(--font-mono);
  font-size: 11px;
  color: var(--color-text-muted);
  flex-shrink: 0;
}

@media (max-width: 640px) {
  .chat-footer {
    font-size: 10px;
    padding: 5px;
  }
}
</style>
