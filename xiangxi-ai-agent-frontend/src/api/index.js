import axios from 'axios'

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 30000,
})

export default api

export const APP_ENV = import.meta.env.VITE_APP_ENV || 'development'
export const IS_PROD = import.meta.env.PROD
export const IS_DEV = import.meta.env.DEV