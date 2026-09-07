import request from '@/utils/request'

// 分页查询登录日志
export function listLoginLog(params) {
  return request({
    url: '/monitor/log/login/list',
    method: 'get',
    params
  })
}

// 分页查询操作日志
export function listOperLog(params) {
  return request({
    url: '/monitor/log/oper/list',
    method: 'get',
    params
  })
}

// 删除操作日志
export function delOperLog(operIds) {
  return request({
    url: `/monitor/log/oper/${operIds}`,
    method: 'delete'
  })
}

// 清空操作日志
export function cleanOperLog() {
  return request({
    url: '/monitor/log/oper/clean',
    method: 'delete'
  })
}
