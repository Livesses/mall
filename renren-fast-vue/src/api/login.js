import request from '../utils/httpRequest'

export function login (username,password,uuid,code) {
  return request({
    url: '/sys/login',
    method: 'post',
    data: {username,password,uuid,code}
  })

}
