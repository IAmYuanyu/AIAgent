<script setup>
import { computed, onMounted, ref } from 'vue'
import useChatStore from '../stores/useChatStore'
import ChatLayout from '../components/ChatLayout.vue'
import { streamSse } from '../services/sse'
import { API_BASE_URL } from '../services/api'

const store = useChatStore('agent')
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

const handleDelete = (id) => {
  store.deleteConversation(id)
}

const handleSend = async (message) => {
  store.appendUserMessage(message)
  loading.value = true
  const url = `${API_BASE_URL}/ai/manus/chat?message=${encodeURIComponent(message)}`
  const stepRegex = /Step\s*\d+\s*:/gi
  let buffer = ''
  let stepMode = false

  const findPartialStep = (text) => {
    const index = text.lastIndexOf('Step')
    if (index === -1) return -1
    const tail = text.slice(index)
    if (/^Step\s*\d+\s*:/.test(tail)) return -1
    if (/^Step\s*\d*\s*:?$/.test(tail)) return index
    return -1
  }

  const processBuffer = () => {
    const matches = []
    let match
    while ((match = stepRegex.exec(buffer)) !== null) {
      matches.push({ index: match.index, length: match[0].length })
    }

    if (matches.length === 0) {
      if (stepMode && buffer) {
        const partialIndex = findPartialStep(buffer)
        if (partialIndex >= 0) {
          const ready = buffer.slice(0, partialIndex)
          if (ready) {
            store.appendAssistantMessage(ready)
          }
          buffer = buffer.slice(partialIndex)
        } else {
          store.appendAssistantMessage(buffer)
          buffer = ''
        }
      }
      return
    }

    let cursor = 0
    matches.forEach((item) => {
      const segment = buffer.slice(cursor, item.index)
      if (stepMode && segment.trim()) {
        store.appendAssistantMessage(segment)
      }
      if (!stepMode) {
        stepMode = true
      }
      store.createAssistantMessage('')
      cursor = item.index + item.length
    })

    const remaining = buffer.slice(cursor)
    if (stepMode && remaining) {
      const partialIndex = findPartialStep(remaining)
      if (partialIndex >= 0) {
        const ready = remaining.slice(0, partialIndex)
        if (ready) {
          store.appendAssistantMessage(ready)
        }
        buffer = remaining.slice(partialIndex)
      } else {
        store.appendAssistantMessage(remaining)
        buffer = ''
      }
    } else {
      buffer = remaining
    }
  }

  await streamSse(
    url,
    (chunk) => {
      buffer += chunk
      processBuffer()
    },
    () => {
      loading.value = false
    },
  )

  if (!stepMode) {
    const finalContent = buffer.trim()
    if (finalContent) {
      store.createAssistantMessage(finalContent)
    }
  }
  loading.value = false
}
</script>

<template>
  <ChatLayout
    title="AI 智能体"
    :conversations="store.state.conversations"
    :active-id="store.state.activeConversationId"
    :messages="activeConversation?.messages || []"
    :active-title="activeConversation?.title || '新对话'"
    :loading="loading"
    @create="handleCreate"
    @select="handleSelect"
    @delete="handleDelete"
    @send="handleSend"
  />
</template>
