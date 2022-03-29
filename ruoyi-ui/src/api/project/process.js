import request from "../../utils/request";

export function processList(query) {
  return request({
    url: '/reim/process/list',
    method: 'get',
    params: query
  })
}

export function complete(data,projectId) {
  return request({
    url: '/reim/task/complete/'+projectId+'/'+data.procInsId,
    method: 'post',
    data: data
  })
}

export function  stopProcess(procInsId){
  return request(
    {
      url:'/reim/process/stop/'+procInsId,
      method:'get',
    }
  )
}
