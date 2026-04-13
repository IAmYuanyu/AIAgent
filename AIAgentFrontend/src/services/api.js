import axios from 'axios'

export const API_BASE_URL = import.meta.env.PROD
  ? '/api'
  : 'http://localhost:8123/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 300000,
})

export default api
