import request from '@/utils/request'

// 查询部门树
export function listDeptTree() {
  return request({
    url: '/system/dept/tree',
    method: 'get'
  })
}

// 查询部门详情
export function getDept(deptId) {
  return request({
    url: `/system/dept/${deptId}`,
    method: 'get'
  })
}

// 新增部门
export function addDept(data) {
  return request({
    url: '/system/dept',
    method: 'post',
    data
  })
}

// 修改部门
export function updateDept(data) {
  return request({
    url: '/system/dept',
    method: 'put',
    data
  })
}

// 删除部门
export function delDept(deptId) {
  return request({
    url: `/system/dept/${deptId}`,
    method: 'delete'
  })
}
