<template>
  <div class="chat-history">
    <button
      v-for="conversation in conversations"
      :key="conversation.id"
      class="history-item"
      :class="{ active: conversation.id === activeId }"
      @click="emit('select', conversation.id)"
    >
      <span>{{ conversation.title }}</span>
      <small>{{ conversation.messages.length }} 条对话</small>
    </button>
  </div>
</template>

<script setup>
const props = defineProps({
  conversations: { type: Array, required: true },
  activeId: { type: [Number, null], default: null },
})

const emit = defineEmits(['select'])
</script>
