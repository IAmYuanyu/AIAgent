import { reactive, readonly } from 'vue'

const state = reactive({
  assistant: {
    conversations: [],
    activeConversationId: null,
  },
  agent: {
    conversations: [],
    activeConversationId: null,
  },
})

const getScope = (scope) => state[scope] || state.assistant

const createConversation = (scope) => {
  const target = getScope(scope)
  const id = Date.now()
  const conversation = {
    id,
    title: '新对话',
    messages: [],
  }
  target.conversations.unshift(conversation)
  target.activeConversationId = id
  return conversation
}

const setActiveConversation = (scope, id) => {
  const target = getScope(scope)
  target.activeConversationId = id
}

const getActiveConversation = (scope) => {
  const target = getScope(scope)
  return target.conversations.find((item) => item.id === target.activeConversationId) || null
}

const appendUserMessage = (scope, text) => {
  const conversation = getActiveConversation(scope)
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

const appendAssistantMessage = (scope, chunk) => {
  const conversation = getActiveConversation(scope)
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

const resetConversation = (scope) => {
  createConversation(scope)
}

const getState = (scope) => readonly(getScope(scope))

export default function useChatStore(scope = 'assistant') {
  return {
    state: getState(scope),
    createConversation: () => createConversation(scope),
    setActiveConversation: (id) => setActiveConversation(scope, id),
    getActiveConversation: () => getActiveConversation(scope),
    appendUserMessage: (text) => appendUserMessage(scope, text),
    appendAssistantMessage: (chunk) => appendAssistantMessage(scope, chunk),
    resetConversation: () => resetConversation(scope),
  }
}
