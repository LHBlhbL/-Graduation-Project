import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listProcinst(query) {
  return request({
    url: '/system/procinst/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getProcinst(id) {
  return request({
    url: '/system/procinst/' + id,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addProcinst(data) {
  return request({
    url: '/system/procinst',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateProcinst(data) {
  return request({
    url: '/system/procinst',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delProcinst(id) {
  return request({
    url: '/system/procinst/' + id,
    method: 'delete'
  })
}

// 导出【请填写功能名称】
export function exportProcinst(query) {
  return request({
    url: '/system/procinst/export',
    method: 'get',
    params: query
  })
}
