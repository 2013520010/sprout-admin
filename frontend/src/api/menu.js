import request from '@/utils/request'

// 查询菜单树
export function listMenuTree() {
  return request({
    url: '/system/menu/tree',
    method: 'get'
  })
}

// 查询菜单详情
export function getMenu(menuId) {
  return request({
    url: `/system/menu/${menuId}`,
    method: 'get'
  })
}

// 新增菜单
export function addMenu(data) {
  return request({
    url: '/system/menu',
    method: 'post',
    data
  })
}

// 修改菜单
export function updateMenu(data) {
  return request({
    url: '/system/menu',
    method: 'put',
    data
  })
}

// 删除菜单
export function delMenu(menuId) {
  return request({
    url: `/system/menu/${menuId}`,
    method: 'delete'
  })
}
