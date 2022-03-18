import request from "../../utils/request";

export function remiList(){
  return request({
    url:'/reim/list',
    method:'get'
  })
}

export function todoList(query){
  return request({
    url:'/reim/todoList',
    method:'get',
    params:query
  })
}
