
<script setup>
import { computed, onMounted, ref } from 'vue'
import useChatStore from '../stores/useChatStore'
import ChatLayout from '../components/ChatLayout.vue'
import { streamSse } from '../services/sse'

const store = useChatStore()
const loading = ref(false)

const ensureConversation = () => {
  if (!store.state.activeConversationId) {
    store.createConversation()
  }
}

onMounted(() => {
  ensureConversation()
})

const activeConversation = computed(() => store.getActiveConversation())

const handleCreate = () => {
  store.createConversation()
}

const handleSelect = (id) => {
  store.setActiveConversation(id)
}

const handleSend = async (message) => {
  const conversation = store.getActiveConversation()
  store.appendUserMessage(message)
  loading.value = true
  const chatId = conversation?.id
  const url = `http://localhost:8123/api/ai/love_app/chat/stream?message=${encodeURIComponent(message)}&chatId=${encodeURIComponent(chatId ?? '')}`

  await streamSse(
    url,
    (chunk) => {
      store.appendAssistantMessage(chunk)
    },
    () => {
      loading.value = false
    },
  )
  loading.value = false
}
</script>

<template>
  <ChatLayout
    title="AI 助手"
    :conversations="store.state.conversations"
    :active-id="store.state.activeConversationId"
    :messages="activeConversation?.messages || []"
    :active-title="activeConversation?.title || '新对话'"
    :loading="loading"
    @create="handleCreate"
    @select="handleSelect"
    @send="handleSend"
  />
</template>
