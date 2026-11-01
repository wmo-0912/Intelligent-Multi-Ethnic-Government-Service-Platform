import axios from 'axios'
import { ElMessage } from 'element-plus'

const BASE_URL = 'http://localhost:8080/api'

const service = axios.create({
  baseURL: BASE_URL,
  timeout: 15000,
})

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = 'Bearer ' + token
    }
    return config
  },
  (error) => Promise.reject(error)
)

service.interceptors.response.use(
  (response) => {
    const res = response.data
    if (res.code !== 200) {
      ElMessage.error(res.message || '请求出错')
      return Promise.reject(res)
    }
    return res
  },
  (error) => {
    const { response } = error
    if (response) {
      const errorMsg = response.data?.message || response.data?.error || '请求失败'
      switch (response.status) {
        case 400:
          // 400错误通常是参数错误，显示具体错误信息
          ElMessage.error(errorMsg || '请求参数错误')
          break
        case 401:
          ElMessage.error(errorMsg || '登录失效，请重新登录')
          localStorage.removeItem('token')
          localStorage.removeItem('userInfo')
          setTimeout(() => {
            window.location.href = '/login'
          }, 1200)
          break
        case 403:
          ElMessage.error(errorMsg || '无权限访问')
          break
        case 404:
          ElMessage.error(errorMsg || '接口不存在')
          break
        case 500:
          ElMessage.error(errorMsg || '服务器错误')
          break
        default:
          ElMessage.error(errorMsg || '未知错误')
      }
      return Promise.reject(response.data || error)
    } else {
      // 网络错误或其他错误
      console.error('Request error:', error)
      return Promise.reject(error)
    }
  }
)

export default service
