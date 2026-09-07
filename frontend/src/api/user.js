import request from '@/utils/request'

// 分页查询用户
export function listUser(params) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params
  })
}

// 查询用户详情
export function getUser(userId) {
  return request({
    url: `/system/user/${userId}`,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

// 修改用户
export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

// 删除用户
export function delUser(userIds) {
  return request({
    url: `/system/user/${userIds}`,
    method: 'delete'
  })
}

// 重置密码
export function resetUserPwd(userId, password) {
  return request({
    url: '/system/user/resetPwd',
    method: 'put',
    data: { userId, password }
  })
}

// 查询用户已有角色
export function getUserRoles(userId) {
  return request({
    url: `/system/user/${userId}/roles`,
    method: 'get'
  })
}

// 分配角色
export function assignUserRoles(userId, roleIds) {
  return request({
    url: `/system/user/${userId}/roles`,
    method: 'put',
    data: roleIds
  })
}
