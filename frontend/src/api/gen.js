import request from '@/utils/request'

// 查询可导入的数据库表
export function listDbTable() {
  return request({
    url: '/tool/gen/db/list',
    method: 'get'
  })
}

// 导入表
export function importTable(tableNames) {
  return request({
    url: '/tool/gen/import',
    method: 'post',
    data: tableNames
  })
}

// 分页查询生成配置
export function listGenTable(params) {
  return request({
    url: '/tool/gen/list',
    method: 'get',
    params
  })
}

// 查询生成配置详情
export function getGenTable(tableId) {
  return request({
    url: `/tool/gen/${tableId}`,
    method: 'get'
  })
}

// 修改生成配置
export function updateGenTable(data) {
  return request({
    url: '/tool/gen',
    method: 'put',
    data
  })
}

// 删除生成配置
export function delGenTable(tableIds) {
  return request({
    url: `/tool/gen/${tableIds}`,
    method: 'delete'
  })
}

// 预览代码
export function previewCode(tableId) {
  return request({
    url: `/tool/gen/preview/${tableId}`,
    method: 'get'
  })
}

// 生成代码下载地址
export function genDownloadUrl(tableIds) {
  return `/tool/gen/download/${tableIds}`
}
