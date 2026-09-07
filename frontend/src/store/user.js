import { defineStore } from 'pinia'
import { login, logout, getInfo } from '@/api/auth'
import { getToken, setToken, removeToken } from '@/utils/auth'

export const useUserStore = defineStore('user', {
  state: () => ({
    token: getToken() || '',
    userId: null,
    username: '',
    nickname: '',
    roles: [],
    permissions: []
  }),

  actions: {
    // 登录
    async login(username, password) {
      const data = await login({ username, password })
      this.token = data.token
      setToken(data.token)
      return data
    },

    // 获取用户信息
    async getInfo() {
      const data = await getInfo()
      this.userId = data.user?.userId
      this.username = data.user?.username
      this.nickname = data.user?.nickname
      this.roles = data.roles || []
      this.permissions = data.permissions || []
      return data
    },

    // 登出
    async logout() {
      try {
        await logout()
      } catch (e) {
        // 忽略登出接口异常
      }
      this.reset()
    },

    // 重置状态
    reset() {
      this.token = ''
      this.userId = null
      this.username = ''
      this.nickname = ''
      this.roles = []
      this.permissions = []
      removeToken()
    },

    // 判断是否拥有某权限
    hasPermission(perm) {
      return this.permissions.includes('*:*:*') || this.permissions.includes(perm)
    }
  }
})
