import { reactive, readonly } from 'vue'

const STORAGE_KEY = 'ai-chat-v1'
const STORAGE_VERSION = 1
let persistTimer = null

const loadFromStorage = () => {
  if (typeof localStorage === 'undefined') return null
  try {
    const raw = localStorage.getItem(STORAGE_KEY)
    if (!raw) return null
    const parsed = JSON.parse(raw)
    if (!parsed || parsed.version !== STORAGE_VERSION) return null
    return parsed.scopes || null
  } catch (error) {
    return null
  }
}

const serializeScope = (scopeState) => ({
  activeConversationId: scopeState.activeConversationId,
  conversations: scopeState.conversations.map((conversation) => ({
    id: conversation.id,
    title: conversation.title,
    messages: conversation.messages.map((message) => ({
      id: message.id,
      role: message.role,
      content: message.content,
    })),
  })),
})

const persist = () => {
  if (typeof localStorage === 'undefined') return
  if (persistTimer) {
    clearTimeout(persistTimer)
  }
  persistTimer = setTimeout(() => {
    const payload = {
      version: STORAGE_VERSION,
      scopes: {
        assistant: serializeScope(state.assistant),
        agent: serializeScope(state.agent),
      },
    }
    localStorage.setItem(STORAGE_KEY, JSON.stringify(payload))
  }, 200)
}

const applyScope = (target, storedScope) => {
  const conversations = Array.isArray(storedScope?.conversations)
    ? storedScope.conversations.map((conversation, conversationIndex) => ({
        id: conversation?.id ?? Date.now() + conversationIndex,
        title: conversation?.title ?? '新对话',
        messages: Array.isArray(conversation?.messages)
          ? conversation.messages.map((message, messageIndex) => ({
              id:
                message?.id ??
                `${conversation?.id ?? conversationIndex}-msg-${messageIndex}`,
              role: message?.role ?? 'assistant',
              content: message?.content ?? '',
            }))
          : [],
      }))
    : []

  target.conversations = conversations
  const storedActiveId = storedScope?.activeConversationId ?? null
  const activeExists = conversations.some((item) => item.id === storedActiveId)
  target.activeConversationId = activeExists
    ? storedActiveId
    : conversations[0]?.id ?? null
}

const hydrateState = () => {
  const storedScopes = loadFromStorage()
  if (!storedScopes) return
  applyScope(state.assistant, storedScopes.assistant)
  applyScope(state.agent, storedScopes.agent)
}

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
  persist()
  return conversation
}

const deleteConversation = (scope, id) => {
  const target = getScope(scope)
  const index = target.conversations.findIndex((item) => item.id === id)
  if (index === -1) return
  target.conversations.splice(index, 1)
  if (target.activeConversationId === id) {
    target.activeConversationId = target.conversations[0]?.id ?? null
  }
  persist()
}

const setActiveConversation = (scope, id) => {
  const target = getScope(scope)
  target.activeConversationId = id
  persist()
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
  persist()
}

const createAssistantMessage = (scope, content = '') => {
  const conversation = getActiveConversation(scope)
  if (!conversation) return
  conversation.messages.push({
    id: `${conversation.id}-assistant-${conversation.messages.length}`,
    role: 'assistant',
    content,
  })
  persist()
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
  persist()
}

const resetConversation = (scope) => {
  createConversation(scope)
  persist()
}

const getState = (scope) => readonly(getScope(scope))

hydrateState()

export default function useChatStore(scope = 'assistant') {
  return {
    state: getState(scope),
    createConversation: () => createConversation(scope),
    setActiveConversation: (id) => setActiveConversation(scope, id),
    getActiveConversation: () => getActiveConversation(scope),
    appendUserMessage: (text) => appendUserMessage(scope, text),
    createAssistantMessage: (content) => createAssistantMessage(scope, content),
    appendAssistantMessage: (chunk) => appendAssistantMessage(scope, chunk),
    deleteConversation: (id) => deleteConversation(scope, id),
    resetConversation: () => resetConversation(scope),
  }
}
