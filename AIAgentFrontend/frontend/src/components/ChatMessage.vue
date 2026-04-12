<template>
  <div class="chat-message" :class="role">
    <p>{{ content }}</p>
  </div>
</template>

<script setup>
const props = defineProps({
  role: { type: String, required: true },
  content: { type: String, required: true },
})
</script>
