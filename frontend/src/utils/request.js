import axios from 'axios'
import { ElMessage } from 'element-plus'
import { getToken, removeToken } from './auth'

// 创建 axios 实例
const service = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000
})

// 请求拦截器：附加 token
service.interceptors.request.use(
  (config) => {
    const token = getToken()
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器：统一处理返回结果与错误
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // 后端统一返回 { code, message, data }
    if (res.code === 200) {
      return res.data
    }
    ElMessage.error(res.message || '请求失败')
    if (res.code === 401) {
      handleUnauthorized()
    }
    return Promise.reject(new Error(res.message || '请求失败'))
  },
  (error) => {
    const status = error.response?.status
    const data = error.response?.data
    if (status === 401) {
      ElMessage.error(data?.message || '登录已过期，请重新登录')
      handleUnauthorized()
    } else if (status === 403) {
      ElMessage.error(data?.message || '没有操作权限')
    } else {
      ElMessage.error(data?.message || error.message || '网络异常')
    }
    return Promise.reject(error)
  }
)

function handleUnauthorized() {
  removeToken()
  // 跳转登录页
  if (window.location.pathname !== '/login') {
    window.location.href = '/login'
  }
}

export default service
