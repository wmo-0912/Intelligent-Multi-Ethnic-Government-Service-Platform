<template>
  <div class="services-container">
    <h2 class="page-title">{{ $t('services.title') }}</h2>
    <!-- 政务人员：显示提示信息，引导到事项管理 -->
    <div v-if="userStore.isGov" class="gov-hint">
      <el-alert
        :title="$t('services.govHint')"
        :description="$t('services.govDesc')"
        type="info"
        :closable="false"
        show-icon
      >
        <template #default>
          <div style="margin-top: 10px;">
            <el-button type="primary" @click="$router.push('/service-manage')">{{ $t('services.enterServiceManage') }}</el-button>
          </div>
        </template>
      </el-alert>
    </div>
    <!-- 居民：显示可申请的事项列表 -->
    <el-row v-else :gutter="20">
      <el-col :xs="24" :sm="12" :md="8" v-for="service in services" :key="service.itemId">
        <el-card class="service-card" shadow="hover" @click="openApplyDialog(service)">
          <template #header>
            <div class="service-header">
              <h3>{{ service.itemName }}</h3>
              <el-tag v-if="service.isEnabled" type="success">{{ $t('services.canApply') }}</el-tag>
            </div>
          </template>
          <p class="service-desc">{{ service.handleRequirement }}</p>
          <div class="service-footer">
            <span class="service-time">{{ service.departmentName }}</span>
            <el-button type="primary" link>{{ $t('services.applyNow') }}</el-button>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 申请对话框（带材料说明+上传）- 仅居民可用 -->
    <el-dialog v-if="!userStore.isGov" v-model="applyDialogVisible" :title="applyForm.item?.itemName || $t('services.title')">
      <el-form :model="applyForm" label-width="90px">
        <el-form-item :label="$t('common.applicationMaterials')">
          <span style="white-space:pre-line;">{{ applyForm.item?.handleRequirement }}</span>
        </el-form-item>
        <el-form-item :label="$t('common.supplementDesc')">
          <el-input v-model="applyForm.applicationMaterials" :placeholder="$t('common.supplementDesc')" type="textarea" />
        </el-form-item>
        <el-form-item :label="$t('common.uploadMaterials')">
          <input type="file" ref="fileInput" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="applyDialogVisible=false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmitApply">{{ $t('common.submit') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const { t } = useI18n()

const userStore = useUserStore()
const services = ref([])
const loading = ref(false)
const applyDialogVisible = ref(false)
const applyForm = ref({ item: null, applicationMaterials: '' })
const submitLoading = ref(false)
const fileInput = ref(null)

// 分类标签已移除，直接展示全部可用事项

// 拉取事项（支持多语言）- 仅居民需要
const fetchServices = async () => {
  if (userStore.isGov) return // 政务人员不需要
  loading.value = true
  const lang = userStore.locale || 'zh-CN'
  try {
    const res = await request.get('/service-item/enabled', { params: { languageType: lang } })
    services.value = res.data || []
  } catch (e) {} finally {
    loading.value = false
  }
}
onMounted(() => { fetchServices() })

// 不再按前端分类筛选，统一展示

const openApplyDialog = (item) => {
  if (!userStore.token) {
    ElMessage.warning(t('common.login'))
    return
  }
  applyForm.value = { item, applicationMaterials: '' }
  applyDialogVisible.value = true
}
const handleSubmitApply = async () => {
  if (!applyForm.value.item) return
  submitLoading.value = true
  const file = fileInput.value?.files[0]
  const userId = userStore.userInfo?.userId
  try {
    // 第一步：先创建申请记录，获取recordId
    const applyRes = await request.post('/service-item/apply', {
      userId,
      itemId: applyForm.value.item.itemId,
      applicationMaterials: applyForm.value.applicationMaterials
    })
    
    const recordId = applyRes.data?.recordId || applyRes.data?.record_id || applyRes.data?.id
    console.log('申请创建成功，recordId:', recordId, '完整响应:', applyRes.data)
    
    // 第二步：如果有文件，上传文件并关联到recordId
    if (file && recordId) {
      const formData = new FormData()
      formData.append('file', file)
      formData.append('fileCategory', 'OTHER')
      formData.append('relatedRecordId', String(recordId)) // 关联到申请记录
      
      try {
        const fileRes = await request.post('/file/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        console.log('文件上传成功，fileId:', fileRes.data?.fileId, '完整响应:', fileRes.data)
        console.log('文件已关联到申请记录，fileId:', fileRes.data?.fileId, 'recordId:', recordId)
      } catch (fileErr) {
        console.error('文件上传失败:', fileErr)
        ElMessage.warning(t('common.fileUploadFailed'))
        // 即使文件上传失败，申请已经创建成功
      }
    }
    
    ElMessage.success(t('services.applicationSubmitted'))
    applyDialogVisible.value = false
  } catch (err) {
    console.error('提交申请失败:', err)
    // 错误提示已由axios统一处理
  } finally {
    submitLoading.value = false
    if (fileInput.value) fileInput.value.value = '' // 清空上传框
  }
}
</script>

<style scoped>
.services-container {
  max-width: 1200px;
  margin: 0 auto;
}
.page-title {
  font-size: 28px;
  margin-bottom: 24px;
  color: #333;
}
.gov-hint {
  max-width: 800px;
  margin: 40px auto;
}
.service-tabs { display:none; }
.service-card { margin-bottom: 20px; cursor: pointer; transition: all 0.3s; }
.service-card:hover { box-shadow: 0 4px 12px rgba(0,0,0,0.15); transform: translateY(-2px); }
.service-header { display: flex; justify-content: space-between; align-items: center; }
.service-header h3 { margin: 0; font-size: 18px; color: #333; }
.service-desc { color: #666; font-size: 14px; margin: 12px 0; min-height: 40px; white-space:pre-line; }
.service-footer { display: flex; justify-content: space-between; align-items: center; margin-top: 12px; }
.service-time { color: #999; font-size: 12px; }
</style>
