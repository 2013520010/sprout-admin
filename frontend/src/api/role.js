import request from '@/utils/request'

// 分页查询角色
export function listRole(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}

// 查询全部角色
export function listAllRole() {
  return request({
    url: '/system/role/all',
    method: 'get'
  })
}

// 新增角色
export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

// 修改角色
export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

// 删除角色
export function delRole(roleIds) {
  return request({
    url: `/system/role/${roleIds}`,
    method: 'delete'
  })
}

// 查询角色已有菜单
export function getRoleMenus(roleId) {
  return request({
    url: `/system/role/${roleId}/menus`,
    method: 'get'
  })
}

// 分配菜单
export function assignRoleMenus(roleId, menuIds) {
  return request({
    url: `/system/role/${roleId}/menus`,
    method: 'put',
    data: menuIds
  })
}
