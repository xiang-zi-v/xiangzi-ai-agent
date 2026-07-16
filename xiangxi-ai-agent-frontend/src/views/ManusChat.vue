<template>
  <ChatRoom
    title="AI 超级智能体"
    :endpoint="'/ai/manus/chat'"
    :needChatId="false"
    aiName="超级智能体"
    emptyText="让 AI 超级智能体帮你解决各种复杂问题"
    seoTitle="AI 超级智能体"
    seoDesc="AI 超级智能体 — 多智能体编排引擎。支持工具链调用、自主任务规划与执行，用最前沿的AI技术解决您的工作与生活难题。"
    avatarColor1="#a78bfa"
    avatarColor2="#7c3aed"
    :aiAvatarSVG="manusAvatarSVG"
  />
</template>

<script setup>
import ChatRoom from '../components/ChatRoom.vue'

const manusAvatarSVG = `<rect x="6" y="12" width="36" height="26" rx="5" fill="none" stroke="url(#_aiGrad)" stroke-width="2.5"/>
<line x1="6" y1="20" x2="42" y2="20" stroke="url(#_aiGrad)" stroke-width="1.5" opacity="0.5"/>
<circle cx="18" cy="32" r="3" fill="url(#_aiGrad)" opacity="0.8"/>
<circle cx="30" cy="32" r="3" fill="url(#_aiGrad)" opacity="0.8"/>
<rect x="14" y="8" width="4" height="6" rx="2" fill="url(#_aiGrad)" opacity="0.6"/>
<rect x="30" y="8" width="4" height="6" rx="2" fill="url(#_aiGrad)" opacity="0.6"/>`
</script>
