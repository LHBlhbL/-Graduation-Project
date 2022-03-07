import request from "../../utils/request";

export function remiList(){
  return request({
    url:'/reim/list',
    method:'get'
  })
}
