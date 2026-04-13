<template>
  <div class="chat-window">
    <div class="chat-messages">
      <ChatMessage
        v-for="message in messages"
        :key="message.id"
        :role="message.role"
        :content="message.content"
      />
      <div v-if="loading" class="chat-typing">AI 正在输入...</div>
    </div>
    <div class="chat-input">
      <textarea
        v-model="input"
        rows="2"
        placeholder="输入你的问题..."
        @keydown.enter.prevent="handleSend"
      ></textarea>
      <button class="primary-btn" :disabled="!input.trim() || loading" @click="handleSend">
        发送
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ChatMessage from './ChatMessage.vue'

const props = defineProps({
  messages: { type: Array, required: true },
  loading: { type: Boolean, default: false },
})

const emit = defineEmits(['send'])
const input = ref('')

const handleSend = () => {
  if (!input.value.trim() || props.loading) return
  emit('send', input.value.trim())
  input.value = ''
}
</script>
