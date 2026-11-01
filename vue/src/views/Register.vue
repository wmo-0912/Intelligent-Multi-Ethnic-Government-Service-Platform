<template>
  <div class="register-container">
    <div class="register-box">
      <div class="register-header">
        <h2>{{ $t('register.title') }}</h2>
        <p>{{ $t('register.subtitle') }}</p>
      </div>
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" @submit.prevent="handleRegister">
        <el-form-item prop="account">
          <el-input v-model="registerForm.account" :placeholder="$t('register.accountRequired')" size="large"/>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="registerForm.password" :placeholder="$t('login.password')" type="password" size="large" show-password/>
        </el-form-item>
        <el-form-item prop="name">
          <el-input v-model="registerForm.name" :placeholder="$t('register.name')" size="large"/>
        </el-form-item>
        <el-form-item prop="phoneNumber">
          <el-input v-model="registerForm.phoneNumber" :placeholder="$t('register.phoneNumber')" size="large"/>
        </el-form-item>
        <el-form-item prop="ethnicity">
          <el-input v-model="registerForm.ethnicity" :placeholder="$t('register.ethnicityPlaceholder')" size="large"/>
        </el-form-item>
              <el-form-item prop="role">
                <el-select v-model="registerForm.role" :placeholder="$t('register.selectRole')" size="large" @change="handleRoleChange">
                  <el-option :label="$t('common.resident')" value="居民"/>
                  <el-option :label="$t('common.governmentStaff')" value="政务人员"/>
                </el-select>
              </el-form-item>
              <!-- 政务人员需要输入注册码 -->
              <el-form-item v-if="registerForm.role === '政务人员'" prop="registrationCode">
                <el-input 
                  v-model="registerForm.registrationCode" 
                  :placeholder="$t('register.registrationCodePlaceholder')" 
                  size="large"
                  clearable
                />
                <div class="form-item-hint">{{ $t('register.registrationCodeHint') }}</div>
              </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="handleRegister" :loading="loading" style="width: 100%">{{ $t('common.register') }}</el-button>
        </el-form-item>
        <div class="register-footer">
          <span class="link-text" @click="$router.push('/login')">{{ $t('common.returnLogin') }}</span>
        </div>
      </el-form>
    </div>
  </div>
</template>
<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const { t } = useI18n()

const router = useRouter()
const userStore = useUserStore()
const registerFormRef = ref(null)
const loading = ref(false)
      const registerForm = ref({
        account: '',
        password: '',
        name: '',
        phoneNumber: '',
        ethnicity: '',
        role: '居民',
        registrationCode: '' // 政务人员注册码
      })
      const registerRules = computed(() => {
        const rules = {
          account: [
            { required: true, message: t('register.accountRequired'), trigger: 'blur' }
          ],
          password: [
            { required: true, message: t('register.passwordRequired'), trigger: 'blur' },
            { min: 6, message: t('register.passwordMin'), trigger: 'blur' }
          ],
          name: [
            { required: true, message: t('register.nameRequired'), trigger: 'blur' }
          ],
          phoneNumber: [
            { required: true, message: t('register.phoneRequired'), trigger: 'blur' },
            { pattern: /^1\d{10}$/, message: t('register.phonePattern'), trigger: 'blur' }
          ],
          ethnicity: [
            { required: true, message: t('register.ethnicityRequired'), trigger: 'blur' }
          ],
          role: [
            { required: true, message: t('register.roleRequired'), trigger: 'change' }
          ]
        }
        
        // 如果是政务人员，注册码为必填
        if (registerForm.value.role === '政务人员') {
          rules.registrationCode = [
            { required: true, message: t('register.registrationCodeRequired'), trigger: 'blur' }
          ]
        }
        
        return rules
      })
      
      const handleRoleChange = () => {
        // 切换角色时清空注册码
        if (registerForm.value.role !== '政务人员') {
          registerForm.value.registrationCode = ''
        }
      }
const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        await userStore.register(registerForm.value)
        // 自动登录并跳转首页
        try {
          await userStore.login({ account: registerForm.value.account, password: registerForm.value.password })
          ElMessage.success(t('register.registerSuccess'))
          router.push('/home')
        } catch (loginError) {
          ElMessage.success(t('register.registerSuccessManual'))
          router.push('/login')
        }
      } catch (error) {
        // 更详细的报错输出
        console.error('注册失败:', error)
        ElMessage.error(error.message || (error.data && error.data.message) || '注册失败，请检查信息')
      } finally {
        loading.value = false
      }
    }
  })
}
</script>
<style scoped>
.register-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
}

.register-box {
  width: 100%;
  max-width: 400px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

.register-header {
  text-align: center;
  margin-bottom: 30px;
}

.register-header h2 {
  color: #333;
  margin-bottom: 8px;
}

.register-header p {
  color: #666;
  font-size: 14px;
}

.register-footer {
  text-align: center;
  margin-top: 10px;
}

.link-text {
  color: #667eea;
  cursor: pointer;
  font-size: 14px;
}

.link-text:hover {
  text-decoration: underline;
}

.form-item-hint {
  margin-top: 4px;
  font-size: 12px;
  color: #909399;
}
</style>
