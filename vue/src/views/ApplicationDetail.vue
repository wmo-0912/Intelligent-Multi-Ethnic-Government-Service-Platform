<template>
  <div class="detail-container">
    <div class="detail-header">
      <h2>{{ $t('applications.title') }}</h2>
      <el-button @click="router.back()">{{ $t('common.back') }}</el-button>
    </div>
    <el-card v-if="detail" class="detail-card">
      <el-descriptions :column="2" border>
        <el-descriptions-item :label="$t('applications.applicationNumber')">{{ detail.displayRecordId }}</el-descriptions-item>
        <el-descriptions-item :label="$t('applications.serviceName')">{{ detail.displayItemName }}</el-descriptions-item>
        <el-descriptions-item :label="$t('applications.applyTime')">{{ detail.displayApplyTime }}</el-descriptions-item>
        <el-descriptions-item :label="$t('applications.status')">
          <el-tag>{{ detail.status }}</el-tag>
        </el-descriptions-item>
      <el-descriptions-item :label="$t('applicationDetail.handler')">{{ detail.handler || '\u2014' }}</el-descriptions-item>
      <el-descriptions-item :label="$t('applicationDetail.phone')">{{ detail.phone || '\u2014' }}</el-descriptions-item>
      <el-descriptions-item :label="$t('applications.applicationMaterials')" :span="2">
        <div>
          <div style="white-space: pre-wrap; margin-bottom: 12px;">{{ getMaterialsText(detail.applicationMaterials) || $t('serviceManage.none') }}</div>
          <div v-if="detail.attachments && detail.attachments.length > 0" style="margin-top: 12px;">
            <div style="font-weight: 500; margin-bottom: 8px; color: #409eff;">{{ $t('common.attachments') }} ({{ detail.attachments.length }})</div>
            <div v-for="file in detail.attachments" :key="file.fileId || file.FileID" style="margin-bottom: 8px; display: flex; align-items: center; gap: 8px; padding: 8px; background: #f5f7fa; border-radius: 4px;">
              <el-icon style="color: #409eff;"><Document /></el-icon>
              <span style="flex: 1;">{{ file.fileName || file.FileName || '未知文件' }}</span>
              <el-button size="small" type="primary" link @click="previewFile(file)">
                <el-icon><View /></el-icon> {{ $t('common.preview') }}
              </el-button>
              <el-button size="small" type="success" link @click="downloadFile(file)">
                <el-icon><Download /></el-icon> {{ $t('common.download') }}
              </el-button>
            </div>
          </div>
          <div v-else-if="detail.attachments !== undefined" style="color: #909399; font-size: 12px; margin-top: 8px;">
            {{ $t('common.noAttachments') }}
          </div>
          <div v-else style="color: #909399; font-size: 12px; margin-top: 8px;">
            {{ $t('common.loadingAttachments') }}
          </div>
        </div>
      </el-descriptions-item>
    </el-descriptions>
    <el-divider>{{ $t('applicationDetail.progressInfo') }}</el-divider>
      <el-timeline>
        <el-timeline-item v-for="(step, index) in detail.progressSteps || []" :key="index" :timestamp="step.time" placement="top">
          <el-card>
            <h4>{{ step.title }}</h4>
            <p>{{ step.content }}</p>
          </el-card>
        </el-timeline-item>
      </el-timeline>
    </el-card>

    <!-- 文件预览对话框 -->
    <el-dialog 
      v-model="previewDialogVisible" 
      :title="previewFileInfo?.fileName || previewFileInfo?.FileName || $t('common.preview')" 
      width="80%" 
      top="5vh"
      :close-on-click-modal="true"
      :close-on-press-escape="true"
      @close="handleClosePreview"
    >
      <div v-if="previewFileInfo" style="text-align: center; max-height: 70vh; overflow: auto;">
        <!-- 图片预览 -->
        <div v-if="isImageFile(previewFileInfo.fileType || previewFileInfo.FileType)">
          <img 
            v-if="previewImageUrl"
            :src="previewImageUrl"
            style="max-width: 100%; max-height: 70vh;"
            alt="预览"
            @error="handlePreviewError"
            @load="handleImageLoad"
          />
          <div v-else style="padding: 40px;">
            <el-icon class="is-loading" :size="32"><Loading /></el-icon>
            <p style="margin-top: 16px; color: #909399;">{{ $t('common.loading') }}</p>
          </div>
        </div>
        <!-- PDF预览 -->
        <div v-else-if="isPdfFile(previewFileInfo.fileType || previewFileInfo.FileType)">
          <iframe 
            v-if="previewPdfUrl"
            :src="previewPdfUrl"
            style="width: 100%; height: 70vh; border: none;"
          />
          <div v-else style="padding: 40px;">
            <el-icon class="is-loading" :size="32"><Loading /></el-icon>
            <p style="margin-top: 16px; color: #909399;">{{ $t('common.loading') }}</p>
          </div>
        </div>
        <!-- 其他文件类型 -->
        <div v-else style="padding: 40px;">
          <el-icon :size="64" style="color: #909399;"><Document /></el-icon>
          <p style="margin-top: 16px; color: #909399;">{{ $t('common.previewNotSupported') }}</p>
          <el-button type="primary" @click="downloadFile(previewFileInfo)" style="margin-top: 16px;">
            <el-icon><Download /></el-icon> {{ $t('common.download') }}
          </el-button>
        </div>
      </div>
      <div v-else style="padding: 40px; text-align: center;">
        <el-icon class="is-loading" :size="32"><Loading /></el-icon>
        <p style="margin-top: 16px; color: #909399;">{{ $t('common.loading') }}</p>
      </div>
      <template #footer>
        <el-button @click="handleClosePreview">{{ $t('common.close') }}</el-button>
        <el-button v-if="previewFileInfo && !isImageFile(previewFileInfo.fileType || previewFileInfo.FileType) && !isPdfFile(previewFileInfo.fileType || previewFileInfo.FileType)" type="primary" @click="downloadFile(previewFileInfo)">
          <el-icon><Download /></el-icon> {{ $t('common.download') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, onMounted, computed, nextTick } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { ElMessage } from 'element-plus'
import { View, Download, Document, Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const { t } = useI18n()
const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const id = computed(() => route.params.id)
const detail = ref(null)
const serviceMap = ref({})
const previewDialogVisible = ref(false)
const previewFileInfo = ref(null)
const previewImageUrl = ref('')
const previewPdfUrl = ref('')

const fetchServicesMap = async () => {
  const lang = userStore.locale || 'zh-CN'
  const res = await request.get('/service-item/enabled', { params: { languageType: lang } })
  if(Array.isArray(res.data)) {
    serviceMap.value = Object.fromEntries(
      res.data.map(item => [item.itemId || item.id, item.itemName || item.name || item.serviceName || ''])
    )
  }
}

// 为申请记录加载附件列表
const loadAttachmentsForRecord = async (record) => {
  // 尝试多种可能的recordId字段名
  const recordId = record.recordId || record.RecordID || record.record_id || record.id
  console.log('ApplicationDetail - loadAttachmentsForRecord, recordId:', recordId, '完整record:', record)
  
  if (!recordId) {
    console.warn('ApplicationDetail - 无法找到recordId字段', record)
    record.attachments = []
    return
  }
  
  try {
    console.log('ApplicationDetail - 正在加载附件，recordId:', recordId)
    const fileRes = await request.get(`/file/record/${recordId}`)
    console.log('ApplicationDetail - 附件加载响应:', fileRes)
    
    // 处理不同的响应格式
    let attachments = []
    if (Array.isArray(fileRes.data)) {
      attachments = fileRes.data
    } else if (fileRes.data && Array.isArray(fileRes.data.data)) {
      attachments = fileRes.data.data
    } else if (fileRes.data && fileRes.data.list) {
      attachments = fileRes.data.list
    }
    
    // 确保每个附件都有正确的字段名
    attachments = attachments.map(file => ({
      fileId: file.fileId || file.FileID || file.file_id || file.id,
      fileName: file.fileName || file.FileName || file.file_name || file.name,
      fileType: file.fileType || file.FileType || file.file_type || file.type,
      fileSize: file.fileSize || file.FileSize || file.file_size || file.size,
      storagePath: file.storagePath || file.StoragePath || file.storage_path,
      uploadTime: file.uploadTime || file.UploadTime || file.upload_time,
      ...file
    }))
    
    console.log('ApplicationDetail - 解析后的附件列表:', attachments)
    record.attachments = attachments
  } catch (e) {
    console.error('ApplicationDetail - 获取附件列表失败:', e)
    record.attachments = []
  }
}

// 解析申请材料文本（去除附件标记）
const getMaterialsText = (materials) => {
  if (!materials) return ''
  // 移除 [附件:数字] 格式的标记
  return materials.replace(/\[附件:\d+\]/g, '').trim()
}

// 预览文件
const previewFile = async (file) => {
  if (!file) {
    ElMessage.error('文件信息不存在')
    return
  }
  
  previewFileInfo.value = file
  const fileId = file.fileId || file.FileID || file.file_id || file.id
  const fileType = file.fileType || file.FileType || file.file_type || file.type
  
  if (!fileId) {
    ElMessage.error('文件ID不存在')
    previewFileInfo.value = null
    return
  }
  
  // 先清空之前的预览URL
  if (previewImageUrl.value) {
    window.URL.revokeObjectURL(previewImageUrl.value)
    previewImageUrl.value = ''
  }
  if (previewPdfUrl.value && previewPdfUrl.value.startsWith('blob:')) {
    window.URL.revokeObjectURL(previewPdfUrl.value)
    previewPdfUrl.value = ''
  }
  
  try {
    // 获取token
    const token = userStore.token || localStorage.getItem('token')
    
    if (!token) {
      ElMessage.error('未登录，请重新登录')
      previewFileInfo.value = null
      return
    }
    
    if (isImageFile(fileType)) {
      // 使用vite代理路径，确保token正确传递
      const axiosInstance = (await import('axios')).default
      const response = await axiosInstance.get(`/api/file/${fileId}/preview`, {
        headers: {
          'Authorization': `Bearer ${token}`
        },
        responseType: 'blob'
      })
      
      // 验证响应
      if (!response.data || !(response.data instanceof Blob)) {
        throw new Error('无效的文件响应')
      }
      
      // 验证blob类型
      if (response.data.size === 0) {
        throw new Error('文件为空')
      }
      
      console.log('图片blob创建成功, size:', response.data.size, 'type:', response.data.type)
      
      // 创建blob URL
      const blobUrl = window.URL.createObjectURL(response.data)
      previewImageUrl.value = blobUrl
      console.log('图片blob URL创建成功:', blobUrl)
      
      // 等待下一个tick确保Vue更新了DOM
      await nextTick()
      previewDialogVisible.value = true
    } else if (isPdfFile(fileType)) {
      // 对于PDF，使用axios获取blob，然后创建blob URL
      try {
        const axiosInstance = (await import('axios')).default
        const response = await axiosInstance.get(`/api/file/${fileId}/preview`, {
          headers: {
            'Authorization': `Bearer ${token}`
          },
          responseType: 'blob'
        })
        // 创建blob URL
        previewPdfUrl.value = window.URL.createObjectURL(response.data)
        previewDialogVisible.value = true
      } catch (pdfErr) {
        console.error('PDF预览失败:', pdfErr)
        ElMessage.error('PDF预览失败: ' + (pdfErr.message || ''))
        previewDialogVisible.value = true
      }
    } else {
      previewDialogVisible.value = true
    }
  } catch (e) {
    console.error('预览文件失败:', e)
    ElMessage.error('预览文件失败: ' + (e.message || ''))
    previewDialogVisible.value = true
  }
}

// 处理图片加载成功
const handleImageLoad = () => {
  console.log('图片加载成功')
}

// 处理预览错误
const handlePreviewError = (e) => {
  console.error('图片加载失败:', e)
  console.error('图片URL:', previewImageUrl.value)
  console.error('文件信息:', previewFileInfo.value)
  // 只有当previewImageUrl有值时才显示错误（避免在加载中显示错误）
  if (previewImageUrl.value) {
    ElMessage.error('图片加载失败，请尝试下载')
  }
}

// 关闭预览对话框（清理blob URL）
const handleClosePreview = () => {
  if (previewImageUrl.value) {
    window.URL.revokeObjectURL(previewImageUrl.value)
    previewImageUrl.value = ''
  }
  if (previewPdfUrl.value && previewPdfUrl.value.startsWith('blob:')) {
    window.URL.revokeObjectURL(previewPdfUrl.value)
    previewPdfUrl.value = ''
  }
  previewDialogVisible.value = false
}

// 判断是否为图片文件
const isImageFile = (fileType) => {
  if (!fileType) return false
  const type = fileType.toLowerCase()
  return type.startsWith('image/')
}

// 判断是否为PDF文件
const isPdfFile = (fileType) => {
  if (!fileType) return false
  const type = fileType.toLowerCase()
  return type === 'application/pdf' || type.includes('pdf')
}

// 判断是否为Word文档
const isWordFile = (fileType) => {
  if (!fileType) return false
  const type = fileType.toLowerCase()
  return type.includes('word') || type.includes('msword') || type.includes('officedocument.wordprocessingml')
}

// 下载文件（使用axios，自动携带token）
const downloadFile = async (file) => {
  try {
    const token = userStore.token || localStorage.getItem('token')
    if (!token) {
      ElMessage.error('未登录，请重新登录')
      return
    }
    
    const fileId = file.fileId || file.FileID || file.file_id || file.id
    if (!fileId) {
      ElMessage.error('文件ID不存在')
      return
    }
    
    // 使用vite代理路径，确保token正确传递
    const axiosInstance = (await import('axios')).default
    const response = await axiosInstance.get(`/api/file/${fileId}/download`, {
      headers: {
        'Authorization': `Bearer ${token}`
      },
      responseType: 'blob'
    })
    // 创建下载链接
    const url = window.URL.createObjectURL(response.data)
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', file.fileName || 'file')
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success(t('common.downloadSuccess'))
  } catch (e) {
    console.error('下载文件失败:', e)
    ElMessage.error(t('common.downloadFailed'))
  }
}

onMounted(async () => {
  const lang = userStore.locale || 'zh-CN'
  await fetchServicesMap()
  const res = await request.get('/service-item/applications/me', { params: { languageType: lang } })
  if (Array.isArray(res.data)) {
    const list = res.data.map((it) => ({
      ...it,
      displayRecordId: it.recordId || it.id || it.applicationId,
      displayItemName: serviceMap.value[it.itemId || it.serviceId] || '',
      displayApplyTime: it.applyTime || it.applicationTime || it.createTime || it.submitTime || ''
    }))
    detail.value = list.find(d => String(d.recordId || d.id || d.applicationId) === String(id.value)) || list[0]
    // 加载附件列表
    if (detail.value && detail.value.recordId) {
      await loadAttachmentsForRecord(detail.value)
    }
  }
})
</script>
<style scoped>
.detail-container { max-width: 1000px; margin: 0 auto; }
.detail-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; }
.detail-header h2 { margin: 0; font-size: 28px; color: #333; }
.detail-card { margin-bottom: 20px; }
</style>
