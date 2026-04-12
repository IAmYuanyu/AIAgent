const textDecoder = new TextDecoder('utf-8')

const parseSseLines = (buffer, onMessage) => {
  const lines = buffer.split('\n')
  let remaining = ''

  if (!buffer.endsWith('\n')) {
    remaining = lines.pop() || ''
  }

  lines.forEach((line) => {
    const trimmed = line.trim()
    if (trimmed.startsWith('data:')) {
      const payload = trimmed.replace(/^data:\s?/, '')
      if (payload && payload !== '[DONE]') {
        onMessage(payload)
      }
    }
  })

  return remaining
}

export const streamSse = async (url, onMessage, onError) => {
  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: {
        Accept: 'text/event-stream',
      },
    })

    if (!response.ok || !response.body) {
      throw new Error(`SSE request failed: ${response.status}`)
    }

    const reader = response.body.getReader()
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      buffer += textDecoder.decode(value, { stream: true })
      buffer = parseSseLines(buffer, onMessage)
    }
  } catch (error) {
    if (onError) {
      onError(error)
    }
  }
}
