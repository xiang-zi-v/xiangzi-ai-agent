<template>
  <ChatRoom
    title="祥子健身教练"
    :endpoint="'/ai/fitness_app/chat/sse'"
    :needChatId="true"
    aiName="健身教练"
    emptyText="和祥子健身教练聊聊你的健身目标与训练计划吧"
    seoTitle="祥子健身教练"
    seoDesc="祥子健身教练 — 专业AI健身指导。个性化训练计划、饮食建议、动作纠正，助你科学健身、高效塑形。"
    avatarColor1="#00ff41"
    avatarColor2="#16a34a"
    :aiAvatarSVG="fitnessAvatarSVG"
  />
</template>

<script setup>
import ChatRoom from '../components/ChatRoom.vue'

const fitnessAvatarSVG = `<rect x="6" y="4" width="8" height="12" rx="3" fill="url(#_aiGrad)" opacity="0.9"/>
<rect x="34" y="4" width="8" height="12" rx="3" fill="url(#_aiGrad)" opacity="0.9"/>
<rect x="16" y="8" width="16" height="6" rx="3" fill="url(#_aiGrad)"/>
<rect x="6" y="4" width="4" height="4" rx="1" fill="url(#_aiGrad)" opacity="0.5"/>
<rect x="38" y="4" width="4" height="4" rx="1" fill="url(#_aiGrad)" opacity="0.5"/>
<path d="M18 32 L18 38 Q18 42 22 42 L26 42 Q30 42 30 38 L30 32" fill="none" stroke="url(#_aiGrad)" stroke-width="2.5" stroke-linecap="round"/>
<line x1="14" y1="36" x2="34" y2="36" stroke="url(#_aiGrad)" stroke-width="2" opacity="0.5"/>
<circle cx="24" cy="20" r="12" fill="none" stroke="url(#_aiGrad)" stroke-width="2" opacity="0.3"/>`
</script>
