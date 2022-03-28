import request from "../../utils/request";

export function remiList(){
  return request({
    url:'/reim/list',
    method:'get',
  })
}

export function todoList(query){
  return request({
    url:'/reim/todoList',
    method:'get',
    params:query
  })
}

export function definitionStart(procDefId,data,projectId) {
  return request({
    url: '/reim/definition/start/' + procDefId+"/"+projectId,
    method: 'post',
    data:data
  })
}

export function startProcess(data){
  return request(
    {
      url:'/reim/process/start',
      method:'put',
      data:data

    }
  )

}

export function queList(data)
{
  return request(
    {
      url:'reim/queryList',
      method:"post",
      data:data
    }
  )
}
