import request from '@/utils/request'

// 查询【请填写功能名称】列表
export function listProject(query) {
  return request({
    url: '/project/list',
    method: 'get',
    params: query
  })
}
export function listProjectPrincipal(){
  return request({
    url: '/Info/list',
    method: 'get'
  })
}
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
    url: '/project/changeStatus',
    method: 'put',
    data: data
  })
}


// 部署配置实例
export function addProjectPro(projectId,deployId,procDefId) {
  const data = {
    projectId,
    deployId,
    procDefId
  }
  return request({
    url: '/project/flow/add',
    method: 'put',
    data:data
  })
}

export function getProcess(projectId)
{
  return request({
    url:'/project/flow/form',
    method:'put',
    data:projectId
  })
}

export function checkProjectDeployment(id)
{
  return request({
    url:'/project/flow/check',
    method:'put',
    data:id
  })
}

// 查询流程定义列表
export function listDefinitionFlow(query) {
  return request({
    url: '/project/definition/list',
    method: 'get',
    params: query
  })
}

export function informationOfProject(id){
  return request({
    url: '/Info/information/'+id,
    method: 'get',
  })
}

export function mailtest() {
  return request({
    url: '/project/mail/test',
    method: 'get',
  })
}


