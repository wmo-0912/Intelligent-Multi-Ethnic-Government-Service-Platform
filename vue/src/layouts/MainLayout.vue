<template>
  <div class="main-layout">
    <el-container>
      <el-header class="header">
        <div class="header-left">
          <div class="logo">
            <el-icon><OfficeBuilding /></el-icon>
            <span>{{ $t('common.title') }}</span>
          </div>
        </div>
        <div class="header-right">
          <el-select v-model="currentLocale" @change="handleLocaleChange" class="language-select">
            <el-option label="中文" value="zh-CN" />
            <el-option label="维吾尔语" value="ug" />
            <el-option label="藏语" value="bo" />
            <el-option label="蒙古语" value="mn" />
          </el-select>
          <el-dropdown v-if="isLoggedIn" @command="handleCommand">
            <span class="user-info">
              <el-icon><User /></el-icon>
              {{ userInfo?.name || userInfo?.username }}
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">{{ $t('nav.profile') }}</el-dropdown-item>
                <!-- 新增政务人员专属用户管理入口 -->
                <el-dropdown-item v-if="userInfo && userInfo.role && userInfo.role.indexOf('政务')>-1" command="user-management">{{ $t('nav.userManagement') }}</el-dropdown-item>
                <!-- 新增政务专属事项管理导航 -->
                <el-dropdown-item v-if="userInfo && userInfo.role && userInfo.role.indexOf('政务')>-1" command="service-manage">{{ $t('nav.serviceManage') }}</el-dropdown-item>
                <el-dropdown-item command="logout">{{ $t('common.logout') }}</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
          <el-button v-else @click="router.push('/login')" type="primary" link>{{ $t('common.login') }}</el-button>
        </div>
      </el-header>
      <el-main class="main-content">
        <router-view />
      </el-main>
      <el-footer class="footer">
        <el-menu mode="horizontal" :default-active="activeMenu" @select="handleMenuSelect">
          <el-menu-item index="home"><el-icon><HomeFilled /></el-icon><span>{{ $t('nav.home') }}</span></el-menu-item>
          <!-- 居民菜单 -->
          <template v-if="!isLoggedIn || !isGov">
            <el-menu-item index="services"><el-icon><Document /></el-icon><span>{{ $t('nav.services') }}</span></el-menu-item>
            <el-menu-item index="applications"><el-icon><List /></el-icon><span>{{ $t('nav.applications') }}</span></el-menu-item>
            <el-menu-item index="consultation"><el-icon><ChatDotRound /></el-icon><span>{{ $t('nav.consultation') }}</span></el-menu-item>
          </template>
                 <!-- 政务人员菜单 -->
                 <template v-else>
                   <el-menu-item index="service-manage"><el-icon><Setting /></el-icon><span>{{ $t('nav.serviceManage') }}</span></el-menu-item>
                   <el-menu-item index="user-management"><el-icon><User /></el-icon><span>{{ $t('nav.userManagement') }}</span></el-menu-item>
                 </template>
        </el-menu>
      </el-footer>
    </el-container>
  </div>
</template>
<script setup>
import { computed, watch, ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import {
  OfficeBuilding, User, HomeFilled, Document, List, ChatDotRound, Setting
} from '@element-plus/icons-vue'
const router = useRouter()
const route = useRoute()
const { locale } = useI18n()
const userStore = useUserStore()
const isLoggedIn = computed(() => userStore.isLoggedIn)
const isGov = computed(() => userStore.isGov)
const userInfo = computed(() => userStore.userInfo)
const currentLocale = ref(userStore.locale)

// 初始化时同步 i18n locale
locale.value = userStore.locale || 'zh-CN'

// 监听 userStore.locale 变化，同步更新 i18n
watch(() => userStore.locale, (newLocale) => {
  if (newLocale) {
    locale.value = newLocale
    currentLocale.value = newLocale
  }
}, { immediate: true })

// 页面加载时，如果已登录，强制刷新用户信息以确保role等字段完整
onMounted(async () => {
  if (userStore.isLoggedIn && userStore.token) {
    // 强制刷新用户信息，确保role等字段是最新的
    try {
      await userStore.getProfile(false) // clearOnError=false，避免清除token
      console.log('用户信息已刷新，role:', userStore.userInfo?.role, 'isGov:', userStore.isGov)
    } catch (e) {
      console.warn('页面加载时获取用户信息失败:', e.message || e)
      // 即使失败，如果localStorage中有userInfo，也应该能显示
      if (!userStore.userInfo || !userStore.userInfo.role) {
        // 如果本地也没有role，尝试从localStorage重新读取
        const cachedInfo = localStorage.getItem('userInfo')
        if (cachedInfo) {
          try {
            const parsed = JSON.parse(cachedInfo)
            if (parsed && parsed.role) {
              userStore.userInfo = parsed
              console.log('从localStorage恢复userInfo，role:', parsed.role)
            }
          } catch (parseErr) {
            console.error('解析localStorage中的userInfo失败:', parseErr)
          }
        }
      }
    }
  }
})
const activeMenu = computed(() => {
  const name = route.name
  if (!name) return 'home'
  const nameStr = String(name).toLowerCase()
  // 特殊处理：将驼峰式转换为短横线式
  if (nameStr === 'servicemanage') return 'service-manage'
  if (nameStr === 'usermanagement') return 'user-management'
  return nameStr === 'home' ? 'home' : nameStr
})
const handleLocaleChange = async (val) => {
  // 先更新 i18n locale（立即生效）
  locale.value = val
  currentLocale.value = val
  // 然后保存到后端和 store
  await userStore.setLocale(val)
}
const handleCommand = (command) => {
  if (command === 'profile') router.push('/profile')
  else if (command === 'user-management') router.push('/user-management')
  else if (command === 'service-manage') router.push('/service-manage?tab=items')
  else if (command === 'logout') { userStore.logout(); router.push('/login') }
}
const handleMenuSelect = (key) => {
  // 特殊处理路由映射
  if (key === 'service-manage') {
    router.push('/service-manage?tab=items')
  } else if (key === 'user-management') {
    router.push('/user-management')
  } else {
    router.push(`/${key}`)
  }
}
watch(() => route.path, () => {}, { immediate: true })
</script>
<style scoped>
.main-layout { min-height: 100vh; background-color: #f5f7fa; }
.header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; display: flex; justify-content: space-between; align-items: center; padding: 0 30px; box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1); }
.header-left { display: flex; align-items: center; }
.logo { display: flex; align-items: center; gap: 10px; font-size: 20px; font-weight: bold; }
.header-right { display: flex; align-items: center; gap: 20px; }
.language-select { width: 120px; }
.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  padding: 8px 12px;
  border-radius: 4px;
  transition: background-color 0.3s, color 0.15s;
  background: rgba(255,255,255,0.85); /* 浅底增强对比 */
  color: #4527a0;
  font-weight: 600;
  box-shadow: 0 2px 8px 0 rgba(0,0,0,0.07);
}
.user-info:hover {
  background-color: #ede7f6;
  color: #090b26;
}
.main-content { min-height: calc(100vh - 140px); padding: 20px; }
.footer { background-color: white; border-top: 1px solid #e4e7ed; padding: 0; }
.footer :deep(.el-menu) { display: flex; justify-content: center; border: none; }
.footer :deep(.el-menu-item) { flex: 1; justify-content: center; }
.footer :deep(.el-menu-item.is-active) { color: #667eea; background-color: #f5f7fa; }
</style>
