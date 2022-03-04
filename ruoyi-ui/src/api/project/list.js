import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listProject(query) {
  return request({
    url: '/project/list',
    method: 'get',
    params: query
  })
}

// 查询【请填写功能名称】详细
export function getProject(projectId) {
  return request({
    url: '/project/' + projectId,
    method: 'get'
  })
}

// 新增【请填写功能名称】
export function addProject(data) {
  return request({
    url: '/project',
    method: 'post',
    data: data
  })
}

// 修改【请填写功能名称】
export function updateProject(data) {
  return request({
    url: '/project',
    method: 'put',
    data: data
  })
}

// 删除【请填写功能名称】
export function delProject(projectId) {
  return request({
    url: '/project/' + projectId,
    method: 'delete'
  })
}

// 导出【请填写功能名称】
export function exportProject(query) {
  return request({
    url: '/project/export',
    method: 'get',
    params: query
  })


}

export function userTreeselect(deptId) {
  return request({
    url: '/project/userTreeselect/'+deptId,
    method: 'get'
  })
}


// 项目状态修改
export function changeProjectStatus(projectId, status) {
  const data = {
    projectId,
    status
  }
  return request({
    url: '/system/user/changeStatus',
    method: 'put',
    data: data
  })
}
