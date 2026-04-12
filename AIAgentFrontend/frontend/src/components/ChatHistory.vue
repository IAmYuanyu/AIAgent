<template>
  <div class="chat-history">
    <div
      v-for="conversation in conversations"
      :key="conversation.id"
      class="history-item"
      :class="{ active: conversation.id === activeId }"
      @click="emit('select', conversation.id)"
    >
      <div class="history-main">
        <div class="history-meta">
          <span>{{ conversation.title }}</span>
          <small>{{ conversation.messages.length }} 条对话</small>
        </div>
        <button class="history-delete" @click.stop="emit('delete', conversation.id)">×</button>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  conversations: { type: Array, required: true },
  activeId: { type: [Number, null], default: null },
})

const emit = defineEmits(['select', 'delete'])
</script>
