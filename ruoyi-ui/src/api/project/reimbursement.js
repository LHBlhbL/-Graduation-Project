import request from "../../utils/request";

export function remiList(query){
  return request({
    url:'/reim/list',
    method:'get',
    params:query
  })
}

export function todoList(query){
  return request({
    url:'/reim/todoList',
    method:'get',
    params:query
  })
}

export function definitionStart(data) {
  return request({
    url: '/reim/definition/start/' ,
    method: 'post',
    data:data
  })
}

// 用户图片上传
export function uploadImage(data) {
  return request({
    url: '/reim/img',
    method: 'post',
    data: data
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
