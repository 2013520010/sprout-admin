import router from './router'
import { useUserStore } from './store/user'
import { getToken } from './utils/auth'
import { ElMessage } from 'element-plus'

const whiteList = ['/login']

router.beforeEach(async (to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 新芽 Admin` : '新芽 Admin'

  const token = getToken()

  if (token) {
    if (to.path === '/login') {
      next({ path: '/' })
    } else {
      const userStore = useUserStore()
      // 未加载用户信息则先加载
      if (!userStore.username) {
        try {
          await userStore.getInfo()
          next()
        } catch (e) {
          userStore.reset()
          ElMessage.error('登录状态已失效，请重新登录')
          next(`/login`)
        }
      } else {
        next()
      }
    }
  } else {
    if (whiteList.includes(to.path)) {
      next()
    } else {
      next(`/login`)
    }
  }
})
