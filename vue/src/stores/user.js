import { defineStore } from 'pinia'
import request from '@/utils/request'

export const useUserStore = defineStore('user', {
  state: () => {
    // 安全地解析localStorage中的userInfo
    let userInfo = null
    try {
      const cached = localStorage.getItem('userInfo')
      if (cached && cached !== 'null') {
        userInfo = JSON.parse(cached)
      }
    } catch (e) {
      console.error('解析localStorage中的userInfo失败:', e)
    }
    return {
      token: localStorage.getItem('token') || '',
      userInfo: userInfo,
      locale: localStorage.getItem('locale') || 'zh-CN',
      direction: localStorage.getItem('direction') || 'ltr',
      verticalWriting: false
    }
  },
  getters: {
    isLoggedIn: (state) => !!state.token,
    currentLocale: (state) => state.locale,
    permissions: (state) => (state.userInfo && state.userInfo.permissions) ? state.userInfo.permissions : [],
    isGov: (state) => {
      if (!state.userInfo) {
        return false
      }
      const role = state.userInfo.role
      if (!role) {
        return false
      }
      // 支持 "政务人员" 或包含"政务"的字符串
      return String(role).indexOf('政务') > -1
    }
  },
  actions: {
    // 注册
    async register(params) {
      return await request.post('/user/register', params)
    },
    // 登录
    async login({ account, password }) {
      const res = await request.post('/user/login', { account, password })
      const { i18nConfig, ...user } = res.data
      this.token = i18nConfig.token
      localStorage.setItem('token', this.token)
      
      // 保存用户信息（确保包含role）- 登录响应中包含role
      console.log('登录响应数据:', { userId: user.userId, name: user.name, role: user.role, permissions: user.permissions })
      this.userInfo = {
        userId: user.userId,
        name: user.name,
        role: user.role, // 登录响应中应该包含role
        permissions: user.permissions,
        languageType: user.languageType,
        ...user
      }
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo))
      console.log('登录后保存的userInfo:', this.userInfo, 'isGov:', this.isGov)
      
      this.locale = user.languageType || 'zh-CN'
      localStorage.setItem('locale', this.locale)
      
      // 方向和排版
      this.direction = i18nConfig.direction || 'ltr'
      this.verticalWriting = i18nConfig.verticalWriting || false
      localStorage.setItem('direction', this.direction)
      document.body.setAttribute('dir', this.direction)
      document.body.style.direction = this.direction
      document.body.style.writingMode = this.verticalWriting ? 'vertical-rl' : 'horizontal-tb'
      
      // 登录后立即获取完整用户信息（确保role等字段完整）
      // 注意：getProfile会使用this.token，此时token已经设置好
      // clearOnError=false：如果getProfile失败，不清除token，继续使用登录返回的信息
      try {
        const profileData = await this.getProfile(false)
        console.log('登录后getProfile成功，role:', profileData?.role, 'isGov:', this.isGov)
        // getProfile已经更新了userInfo和localStorage，包含完整的role等信息
      } catch (e) {
        // 如果getProfile失败，继续使用登录返回的信息（已包含role）
        console.warn('登录后获取完整用户信息失败，使用登录返回的信息:', e.message || e)
        console.log('当前userInfo.role:', this.userInfo?.role, 'isGov:', this.isGov)
      }
      
      return res
    },
    // 登出
    async logout() {
      try {
        await request.post('/user/logout')
      } catch(err){}
      this.token = ''
      localStorage.removeItem('token')
      this.userInfo = null
      localStorage.removeItem('userInfo')
      this.locale = 'zh-CN'
      localStorage.removeItem('locale')
      this.direction = 'ltr'
      localStorage.removeItem('direction')
      this.verticalWriting = false
      // 清理页面方向等设置
      document.body.removeAttribute('dir')
      document.body.style.direction = ''
      document.body.style.writingMode = ''
    },
    // 获取个人信息
    async getProfile(clearOnError = true) {
      try {
        const res = await request.get('/user/profile')
        console.log('getProfile返回的数据:', res.data)
        this.userInfo = res.data
        localStorage.setItem('userInfo', JSON.stringify(res.data))
        console.log('getProfile后保存的userInfo.role:', res.data?.role, 'isGov:', this.isGov)
        this.locale = res.data.languageType || 'zh-CN'
        localStorage.setItem('locale', this.locale)
        return res.data
      } catch (error) {
        // clearOnError=false时，不清理缓存（用于登录后自动获取完整信息的场景）
        if (clearOnError) {
          // getProfile失败时不调用后端logout（避免token被拉黑），只清理本地缓存
          this.token = ''
          localStorage.removeItem('token')
          this.userInfo = null
          localStorage.removeItem('userInfo')
          throw new Error('登录已失效，请重新登录')
        } else {
          // 不清理缓存，只抛出错误
          throw error
        }
      }
    },
    // 设置语言
    async setLocale(locale) {
      this.locale = locale
      localStorage.setItem('locale', locale)
      await request.put(`/user/me/language?languageType=${locale}`)
    },
  }
})
