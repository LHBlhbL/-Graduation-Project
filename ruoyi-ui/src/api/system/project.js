import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listProject(query) {
  return request({
    url: '/system/project/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getProject(projectId) {
  return request({
    url: '/system/project/' + projectId,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addProject(data) {
  return request({
    url: '/system/project',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateProject(data) {
  return request({
    url: '/system/project',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delProject(projectId) {
  return request({
    url: '/system/project/' + projectId,
    method: 'delete'
  })
}

// 导出【请填写功能名称】
export function exportProject(query) {
  return request({
    url: '/system/project/export',
    method: 'get',
    params: query
  })
}