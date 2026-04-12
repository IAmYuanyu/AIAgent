<template>
  <div class="chat-message" :class="role">
    <div class="markdown-body" v-html="renderedContent"></div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import MarkdownIt from 'markdown-it'

const props = defineProps({
  role: { type: String, required: true },
  content: { type: String, required: true },
})

const markdown = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true,
})

const renderedContent = computed(() => markdown.render(props.content || ''))
</script>
