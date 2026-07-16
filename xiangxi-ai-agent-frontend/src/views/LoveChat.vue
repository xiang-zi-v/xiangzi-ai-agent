<template>
  <ChatRoom
    title="AI 恋爱大师"
    :endpoint="'/ai/love_app/chat/sse'"
    :needChatId="true"
    aiName="恋爱大师"
    emptyText="和 AI 恋爱大师聊聊你的情感困惑吧"
    seoTitle="AI 恋爱大师"
    seoDesc="AI 恋爱大师 — 智能情感分析引擎，7×24小时在线。基于深度学习的情感理解模型，为您提供专业、温暖的情感建议与陪伴。"
    avatarColor1="#ff6b9d"
    avatarColor2="#c44569"
    :aiAvatarSVG="loveAvatarSVG"
  />
</template>

<script setup>
import ChatRoom from '../components/ChatRoom.vue'

const loveAvatarSVG = `<path d="M24 42C24 42 6 28 6 16C6 9.4 11.4 4 18 4C21.6 4 24.9 5.9 27 8.5C29.1 5.9 32.4 4 36 4C42.6 4 48 9.4 48 16C48 28 30 42 24 42Z" fill="url(#_aiGrad)" opacity="0.9"/>`
</script>
