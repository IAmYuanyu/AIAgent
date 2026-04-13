
<template>
  <section class="chat-layout">
    <aside class="chat-sidebar">
      <div class="sidebar-header">
        <h2>{{ title }}</h2>
        <button class="ghost-btn" @click="onCreate">+ 新对话</button>
      </div>
      <ChatHistory
        :conversations="conversations"
        :active-id="activeId"
        @select="onSelect"
        @delete="onDelete"
      />
    </aside>
    <div class="chat-main">
      <header class="chat-header">
        <h2>{{ activeTitle }}</h2>
        <span class="chat-subtitle">会话已建立</span>
      </header>
      <ChatWindow :messages="messages" @send="onSend" :loading="loading" />
    </div>
  </section>
</template>

<script setup>
import ChatHistory from './ChatHistory.vue'
import ChatWindow from './ChatWindow.vue'

const props = defineProps({
  title: { type: String, required: true },
  conversations: { type: Array, required: true },
  activeId: { type: [Number, null], default: null },
  messages: { type: Array, required: true },
  activeTitle: { type: String, required: true },
  loading: { type: Boolean, default: false },
})

const emit = defineEmits(['create', 'select', 'delete', 'send'])

const onCreate = () => emit('create')
const onSelect = (id) => emit('select', id)
const onDelete = (id) => emit('delete', id)
const onSend = (message) => emit('send', message)
</script>
