<template>
  <div class="profile-container">
    <h2 class="page-title">{{ $t('profile.title') }}</h2>
    <el-row :gutter="20">
      <el-col :xs="24" :md="8">
        <el-card class="profile-card">
          <div class="avatar-section">
            <el-avatar :size="80" :icon="UserFilled" />
            <h3>{{ userInfo?.name || userInfo?.nickname || userInfo?.account || userInfo?.username || $t('common.name') }}</h3>
            <p>{{ userInfo?.account || userInfo?.username || $t('register.account') }}</p>
          </div>
          <el-divider />
          <el-menu :default-active="activeMenu" @select="handleMenuSelect">
            <el-menu-item index="basic">
              <el-icon><Document /></el-icon>
              <span>{{ $t('profile.basicInfo') }}</span>
            </el-menu-item>
            <el-menu-item index="password">
              <el-icon><Lock /></el-icon>
              <span>{{ $t('profile.changePassword') }}</span>
            </el-menu-item>
            <el-menu-item index="services">
              <el-icon><List /></el-icon>
              <span>{{ $t('profile.myServices') }}</span>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="16">
        <el-card class="content-card">
          <template v-if="loading">
            <div style="padding:60px 0;text-align:center;">
              <el-icon><Loading /></el-icon>
              <span style="margin-left:10px;">{{ $t('common.loading') }}</span>
            </div>
          </template>
          <template v-else-if="errorMsg && !userInfo?.username">
            <div style="padding:54px 0;text-align:center;color:#f56c6c;">
              {{errorMsg}}
              <div style="margin-top:18px;">
                <el-button type="primary" size="small" @click="retrySync">{{ $t('common.retry') }}</el-button>
                <el-button type="danger" size="small" @click="gotoLogin">{{ $t('common.reLogin') }}</el-button>
              </div>
            </div>
          </template>
          <template v-else>
            <template v-if="activeMenu === 'basic'">
              <h3>{{ $t('profile.basicInfo') }}</h3>
              <el-descriptions :column="1" border>
                <el-descriptions-item :label="$t('register.account')">
                  {{ userInfo?.account || userInfo?.username }}
                </el-descriptions-item>
                <el-descriptions-item :label="$t('register.name')">
                  <el-input v-model="editInfo.name" style="width:200px;" :placeholder="$t('register.nameRequired')" />
                </el-descriptions-item>
                <el-descriptions-item :label="$t('register.phoneNumber')">
                  <el-input v-model="editInfo.phoneNumber" style="width:200px;" :placeholder="$t('register.phoneRequired')" />
                </el-descriptions-item>
                <el-descriptions-item :label="$t('register.ethnicity')">
                  <el-input v-model="editInfo.ethnicity" style="width:200px;" :placeholder="$t('register.ethnicityRequired')" />
                </el-descriptions-item>
                <el-descriptions-item :label="$t('common.role')">{{ userInfo?.role }}</el-descriptions-item>
                <el-descriptions-item :label="$t('common.language')">
                  <el-select v-model="currentLocale" @change="handleLocaleChange" style="width:130px">
                    <el-option :label="$t('common.zhCN')" value="zh-CN" />
                    <el-option :label="$t('common.ug')" value="ug" />
                    <el-option :label="$t('common.bo')" value="bo" />
                    <el-option :label="$t('common.mn')" value="mn" />
                  </el-select>
                </el-descriptions-item>
              </el-descriptions>
              <el-button type="primary" @click="handleSave" :loading="syncing" style="margin-top:18px">{{ $t('common.save') }}</el-button>
            </template>
            <template v-else-if="activeMenu === 'password'">
              <h3>{{ $t('profile.changePassword') }}</h3>
              <el-form :model="passwordForm" label-width="100px">
                <el-form-item :label="$t('profile.oldPassword')">
                  <el-input v-model="passwordForm.oldPassword" type="password" show-password />
                </el-form-item>
                <el-form-item :label="$t('profile.newPassword')">
                  <el-input v-model="passwordForm.newPassword" type="password" show-password />
                </el-form-item>
                <el-form-item :label="$t('profile.confirmPassword')">
                  <el-input v-model="passwordForm.confirmPassword" type="password" show-password />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" @click="handleChangePassword">{{ $t('common.save') }}</el-button>
                </el-form-item>
              </el-form>
            </template>
            <template v-else-if="activeMenu === 'services'">
              <h3>{{ $t('profile.myServices') }}</h3>
              <p>{{ $t('profile.servicesDesc') }}</p>
            </template>
          </template>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>
<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { UserFilled, Document, Lock, List, Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'

const { t } = useI18n()
const router = useRouter()
const loading = ref(true)
const syncing = ref(false)
const userStore = useUserStore()
const userInfo = ref({ ...userStore.userInfo }) // 优先本地缓存
const editInfo = ref({}) // 用于编辑不影响原始数据
const activeMenu = ref('basic')
const passwordForm = ref({ oldPassword: '', newPassword: '', confirmPassword: '' })
const currentLocale = ref(userStore.locale)
const errorMsg = ref('')
const loadUser = async () => {
  loading.value = true
  errorMsg.value = ''
  try {
    const data = await userStore.getProfile()
    if (data) {
      userInfo.value = { ...data }
      // 确保 editInfo 包含所有需要的字段，并设置默认值
      editInfo.value = {
        name: data.name || '',
        phoneNumber: data.phoneNumber || '',
        ethnicity: data.ethnicity || '',
        ...data
      }
    }
  } catch (e) {
    errorMsg.value = e.message || t('common.loadingFailed')
    // 如果本地有缓存，继续显示
    if (userStore.userInfo && userStore.userInfo.userId) {
      userInfo.value = { ...userStore.userInfo }
      editInfo.value = {
        name: userStore.userInfo.name || '',
        phoneNumber: userStore.userInfo.phoneNumber || '',
        ethnicity: userStore.userInfo.ethnicity || '',
        ...userStore.userInfo
      }
    }
  } finally {
    loading.value = false
  }
}
const retrySync = () => { loadUser() }
const gotoLogin = () => { userStore.logout(); router.replace('/login') }
const handleSave = async () => {
  syncing.value = true
  try {
    // 只发送后端需要的字段：name, phoneNumber, ethnicity
    const updateData = {
      name: editInfo.value.name || null,
      phoneNumber: editInfo.value.phoneNumber || null,
      ethnicity: editInfo.value.ethnicity || null
    }
    const res = await request.put('/user/profile', updateData)
    // 更新本地显示和store
    if (res.data) {
      userInfo.value = res.data
      editInfo.value = { ...res.data }
      userStore.userInfo = res.data
      localStorage.setItem('userInfo', JSON.stringify(res.data))
    }
    ElMessage.success(t('profile.updateSuccess'))
    await loadUser()
  } catch(e) {
    console.error('保存个人信息失败:', e)
    ElMessage.error(t('common.updateFailed') + ': ' + (e.message || ''))
  } finally {
    syncing.value = false
  }
}
onMounted(() => {
  // 页面优先回显本地userInfo
  if (userStore.userInfo) {
    userInfo.value = { ...userStore.userInfo }
    editInfo.value = {
      name: userStore.userInfo.name || '',
      phoneNumber: userStore.userInfo.phoneNumber || '',
      ethnicity: userStore.userInfo.ethnicity || '',
      ...userStore.userInfo
    }
  }
  loadUser()
})
const handleMenuSelect = (key) => { activeMenu.value = key }
const handleLocaleChange = async (val) => {
  await userStore.setLocale(val)
  await loadUser()
  ElMessage.success('切换语言成功，部分页面需刷新可见')
}
const handleChangePassword = () => {
  ElMessage.info('请在后端实现安全密码修改接口，本前端暂未实现')
  passwordForm.value = { oldPassword: '', newPassword: '', confirmPassword: '' }
}
</script>
<style scoped>
.profile-container { max-width: 1200px; margin: 0 auto; }
.page-title { font-size: 28px; margin-bottom: 24px; color: #333; }
.profile-card { height: 100%; }
.avatar-section { text-align: center; padding: 20px 0; }
.avatar-section h3 { margin: 16px 0 8px; color: #333; }
.avatar-section p { color: #999; font-size: 14px; }
.content-card h3 { margin-bottom: 20px; color: #333; }
</style>
