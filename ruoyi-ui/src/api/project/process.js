import request from "../../utils/request";

export function processList(query) {
  return request({
    url: '/reim/process/list',
    method: 'get',
    params: query
  })
}
