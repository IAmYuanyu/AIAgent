import { reactive, readonly } from 'vue'

const state = reactive({
  conversations: [],
  activeConversationId: null,
})

const createConversation = () => {
  const id = Date.now()
  const conversation = {
    id,
    title: '新对话',
    messages: [],
  }
  state.conversations.unshift(conversation)
  state.activeConversationId = id
  return conversation
}

const setActiveConversation = (id) => {
  state.activeConversationId = id
}

const getActiveConversation = () =>
  state.conversations.find((item) => item.id === state.activeConversationId) || null

const appendUserMessage = (text) => {
  const conversation = getActiveConversation()
  if (!conversation) return
  conversation.messages.push({
    id: `${conversation.id}-user-${conversation.messages.length}`,
    role: 'user',
    content: text,
  })
  if (conversation.title === '新对话') {
    conversation.title = text.trim().slice(0, 20) || '新对话'
  }
}

const appendAssistantMessage = (chunk) => {
  const conversation = getActiveConversation()
  if (!conversation) return
  const last = conversation.messages[conversation.messages.length - 1]
  if (!last || last.role !== 'assistant') {
    conversation.messages.push({
      id: `${conversation.id}-assistant-${conversation.messages.length}`,
      role: 'assistant',
      content: chunk,
    })
  } else {
    last.content += chunk
  }
}

const resetConversation = () => {
  createConversation()
}

export default function useChatStore() {
  return {
    state: readonly(state),
    createConversation,
    setActiveConversation,
    getActiveConversation,
    appendUserMessage,
    appendAssistantMessage,
    resetConversation,
  }
}
