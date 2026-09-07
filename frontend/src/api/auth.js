import request from '@/utils/request'

// 登录
export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

// 登出
export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

// 获取当前用户信息
export function getInfo() {
  return request({
    url: '/auth/info',
    method: 'get'
  })
}

// 获取当前用户路由菜单
export function getRouters() {
  return request({
    url: '/auth/routers',
    method: 'get'
  })
}
