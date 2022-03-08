import request from "../../utils/request";

export function onGoingList(query) {
  return request({
    url: '/flowable/process/onGoingList',
    method: 'get',
    params: query
  })
}
