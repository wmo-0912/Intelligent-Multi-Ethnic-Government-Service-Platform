<template>
  <div class="service-manage-container">
    <h2 class="page-title">{{ $t('serviceManage.title') }}</h2>
    
    <el-tabs v-model="activeTab" @tab-change="handleTabChange">
      <!-- 待审核申请 -->
      <el-tab-pane :label="$t('serviceManage.pendingApplications')" name="pending">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <span>{{ $t('serviceManage.pendingApplications') }}</span>
              <el-button size="small" @click="getPending" :loading="pendingLoading">
                <el-icon><Refresh /></el-icon> {{ $t('common.refresh') }}
              </el-button>
            </div>
          </template>
          
          <el-table :data="pendingList" border v-loading="pendingLoading" style="width: 100%">
            <el-table-column prop="recordId" :label="$t('serviceManage.applicationId')" width="100" sortable />
            <el-table-column prop="userId" :label="$t('serviceManage.userId')" width="100" />
            <el-table-column prop="itemId" :label="$t('serviceManage.itemId')" width="100" />
            <el-table-column :label="$t('serviceManage.serviceCategory')" width="150">
              <template #default="{row}">
                {{ getItemName(row.itemId) || '\u2014' }}
              </template>
            </el-table-column>
            <el-table-column prop="status" :label="$t('serviceManage.status')" width="120">
              <template #default="{row}">
                <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="submitTime" :label="$t('serviceManage.applyTime')" width="180">
              <template #default="{row}">
                {{ formatTime(row.submitTime || row.applyTime) }}
              </template>
            </el-table-column>
            <el-table-column prop="applicationMaterials" :label="$t('serviceManage.applicationMaterials')" min-width="300">
              <template #default="{row}">
                <div>
                  <div style="margin-bottom: 8px;">{{ getMaterialsText(row.applicationMaterials) || '\u2014' }}</div>
                  <div v-if="row.attachments && Array.isArray(row.attachments) && row.attachments.length > 0" style="margin-top: 8px; padding: 8px; background: #f5f7fa; border-radius: 4px;">
                    <div style="font-weight: 500; margin-bottom: 8px; color: #409eff;">{{ $t('common.attachments') }} ({{ row.attachments.length }})</div>
                    <div v-for="(file, idx) in row.attachments" :key="file.fileId || file.FileID || idx" style="margin-bottom: 6px; display: flex; align-items: center; gap: 8px;">
                      <el-icon style="color: #409eff;"><Document /></el-icon>
                      <span style="flex: 1; font-size: 12px; color: #666;">{{ file.fileName || file.FileName || '未知文件' }}</span>
                      <el-button 
                        size="small" 
                        type="primary" 
                        link
                        @click="previewFile(file)"
                      >
                        <el-icon><View /></el-icon> {{ $t('common.preview') }}
                      </el-button>
                      <el-button 
                        size="small" 
                        type="success" 
                        link
                        @click="downloadFile(file)"
                      >
                        <el-icon><Download /></el-icon> {{ $t('common.download') }}
                      </el-button>
                    </div>
                  </div>
                  <div v-else-if="row.attachments !== undefined" style="color: #909399; font-size: 12px; margin-top: 4px;">
                    {{ $t('common.noAttachments') }}
                  </div>
                  <!-- 调试信息（可以注释掉） -->
                  <!-- <div style="font-size: 10px; color: #999; margin-top: 4px;">
                    Debug: attachments={{ row.attachments ? row.attachments.length : 'undefined' }}, 
                    isArray={{ Array.isArray(row.attachments) }}
                  </div> -->
                </div>
              </template>
            </el-table-column>
            <el-table-column :label="$t('userManagement.operations')" width="150" fixed="right">
              <template #default="{row}">
                <el-button size="small" type="primary" @click="openApproveDialog(row)">{{ $t('serviceManage.approve') }}</el-button>
                <el-button size="small" @click="viewDetail(row)">{{ $t('common.view') }}</el-button>
              </template>
            </el-table-column>
          </el-table>
          
          <el-empty v-if="!pendingLoading && pendingList.length === 0" :description="$t('serviceManage.noPending')" />
        </el-card>
      </el-tab-pane>

      <!-- 事项列表 -->
      <el-tab-pane :label="$t('serviceManage.serviceItems')" name="items">
        <el-card>
          <template #header>
            <div style="display: flex; justify-content: space-between; align-items: center">
              <div>
                <el-input
                  v-model="itemSearchKeyword"
                  :placeholder="$t('serviceManage.searchPlaceholder')"
                  style="width: 300px; margin-right: 10px"
                  clearable
                  @keyup.enter="getItemList"
                >
                  <template #prefix>
                    <el-icon><Search /></el-icon>
                  </template>
                </el-input>
                <el-button type="primary" @click="getItemList" :loading="itemLoading">{{ $t('common.search') }}</el-button>
              </div>
              <el-button type="primary" @click="openCreateDialog">
                <el-icon><Plus /></el-icon> {{ $t('serviceManage.createItem') }}
              </el-button>
            </div>
          </template>
          
          <el-table :data="filteredItemList" border v-loading="itemLoading" style="width: 100%">
            <el-table-column prop="itemId" :label="$t('serviceManage.itemId')" width="100" sortable />
            <el-table-column prop="itemName" :label="$t('serviceManage.itemName')" min-width="200" />
            <el-table-column prop="departmentName" :label="$t('serviceManage.departmentName')" width="150" />
            <el-table-column prop="handleRequirement" :label="$t('serviceManage.handleRequirement')" min-width="250" show-overflow-tooltip />
            <el-table-column prop="isEnabled" :label="$t('serviceManage.status')" width="100">
              <template #default="{row}">
                <el-switch 
                  v-model="row.isEnabled" 
                  @change="toggleEnable(row)" 
                  :loading="row.toggling"
                />
              </template>
            </el-table-column>
            <el-table-column prop="createTime" :label="$t('serviceManage.createTime')" width="180">
              <template #default="{row}">
                {{ formatTime(row.createTime) }}
              </template>
            </el-table-column>
            <el-table-column :label="$t('userManagement.operations')" width="200" fixed="right">
              <template #default="{row}">
                <el-button size="small" @click="openEditDialog(row)">{{ $t('common.edit') }}</el-button>
                <el-popconfirm 
                  :title="$t('serviceManage.deleteConfirm')" 
                  @confirm="deleteItem(row)"
                >
                  <template #reference>
                    <el-button size="small" type="danger">{{ $t('common.delete') }}</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
          
          <el-empty v-if="!itemLoading && filteredItemList.length === 0" :description="$t('serviceManage.noItems')" />
        </el-card>
      </el-tab-pane>
    </el-tabs>

    <!-- 审批对话框 -->
    <el-dialog v-model="approveDialogVisible" :title="$t('serviceManage.approveTitle')" width="600px">
      <el-descriptions :column="1" border v-if="approveRecord">
        <el-descriptions-item :label="$t('serviceManage.applicationId')">{{ approveRecord.recordId }}</el-descriptions-item>
        <el-descriptions-item :label="$t('serviceManage.userId')">{{ approveRecord.userId }}</el-descriptions-item>
        <el-descriptions-item :label="$t('serviceManage.itemId')">{{ approveRecord.itemId }}</el-descriptions-item>
        <el-descriptions-item :label="$t('serviceManage.applyTime')">{{ formatTime(approveRecord.submitTime || approveRecord.applyTime) }}</el-descriptions-item>
        <el-descriptions-item :label="$t('serviceManage.applicationMaterials')">
          <div>
            <div style="white-space: pre-wrap; margin-bottom: 12px;">{{ getMaterialsText(approveRecord.applicationMaterials) || $t('serviceManage.none') }}</div>
            <div v-if="approveRecord.attachments && approveRecord.attachments.length > 0" style="margin-top: 12px;">
              <div style="font-weight: 500; margin-bottom: 8px;">{{ $t('common.attachments') }} ({{ approveRecord.attachments.length }})</div>
              <div v-for="file in approveRecord.attachments" :key="file.fileId" style="margin-bottom: 8px; display: flex; align-items: center; gap: 8px;">
                <span style="flex: 1;">{{ file.fileName }}</span>
                <el-button size="small" type="primary" link @click="previewFile(file)">
                  <el-icon><View /></el-icon> {{ $t('common.preview') }}
                </el-button>
                <el-button size="small" type="success" link @click="downloadFile(file)">
                  <el-icon><Download /></el-icon> {{ $t('common.download') }}
                </el-button>
              </div>
            </div>
            <div v-else-if="approveRecord.attachments !== undefined" style="color: #909399; font-size: 12px; margin-top: 8px;">
              {{ $t('common.noAttachments') }}
            </div>
            <div v-else style="color: #909399; font-size: 12px; margin-top: 8px;">
              {{ $t('common.loadingAttachments') }}
            </div>
          </div>
        </el-descriptions-item>
      </el-descriptions>
      
      <el-form :model="approveForm" label-width="100px" style="margin-top: 20px">
        <el-form-item :label="$t('serviceManage.approveStatus')" required>
          <el-select v-model="approveForm.status" :placeholder="$t('serviceManage.selectStatus')" style="width: 100%">
            <el-option :label="$t('applications.processing')" value="处理中" />
            <el-option :label="$t('applications.approved')" value="已通过" />
            <el-option :label="$t('applications.completed')" value="已完成" />
            <el-option :label="$t('serviceManage.rejected')" value="驳回" />
          </el-select>
        </el-form-item>
        <el-form-item :label="$t('serviceManage.approveReason')" required>
          <el-input 
            v-model="approveForm.handleResult" 
            type="textarea" 
            :rows="4" 
            :placeholder="$t('serviceManage.reasonPlaceholder')"
          />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="approveDialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="submitApprove" :loading="approving">{{ $t('serviceManage.submitApprove') }}</el-button>
      </template>
    </el-dialog>

    <!-- 事项编辑/新建对话框 -->
    <el-dialog 
      v-model="itemDialogVisible" 
      :title="editingItem ? $t('serviceManage.editItem') : $t('serviceManage.createItem')" 
      width="600px"
    >
      <el-form :model="itemForm" label-width="100px" :rules="itemRules" ref="itemFormRef">
        <el-form-item :label="$t('serviceManage.itemName')" prop="itemName">
          <el-input v-model="itemForm.itemName" :placeholder="$t('serviceManage.itemNamePlaceholder')" />
        </el-form-item>
        <el-form-item :label="$t('serviceManage.departmentName')" prop="departmentName">
          <el-input v-model="itemForm.departmentName" :placeholder="$t('serviceManage.departmentNamePlaceholder')" />
        </el-form-item>
        <el-form-item :label="$t('serviceManage.handleRequirement')" prop="handleRequirement">
          <el-input 
            v-model="itemForm.handleRequirement" 
            type="textarea" 
            :rows="4" 
            :placeholder="$t('serviceManage.handleRequirementPlaceholder')"
          />
        </el-form-item>
        <el-form-item :label="$t('serviceManage.enableStatus')" v-if="editingItem">
          <el-switch v-model="itemForm.isEnabled" />
        </el-form-item>
      </el-form>
      
      <template #footer>
        <el-button @click="itemDialogVisible = false">{{ $t('common.cancel') }}</el-button>
        <el-button type="primary" @click="submitItem" :loading="saving">{{ $t('common.save') }}</el-button>
      </template>
    </el-dialog>

    <!-- 申请详情对话框 -->
    <el-dialog v-model="detailDialogVisible" :title="$t('serviceManage.detailTitle')" width="700px">
      <el-descriptions :column="2" border v-if="detailRecord">
        <el-descriptions-item :label="$t('serviceManage.applicationId')">{{ detailRecord.recordId }}</el-descriptions-item>
        <el-descriptions-item :label="$t('serviceManage.userId')">{{ detailRecord.userId }}</el-descriptions-item>
        <el-descriptions-item :label="$t('serviceManage.itemId')">{{ detailRecord.itemId }}</el-descriptions-item>
        <el-descriptions-item :label="$t('serviceManage.currentStatus')">
          <el-tag :type="getStatusType(detailRecord.status)">{{ detailRecord.status }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('serviceManage.applyTime')">{{ formatTime(detailRecord.submitTime || detailRecord.applyTime) }}</el-descriptions-item>
        <el-descriptions-item :label="$t('serviceManage.handleTime')" v-if="detailRecord.handleTime">
          {{ formatTime(detailRecord.handleTime) }}
        </el-descriptions-item>
        <el-descriptions-item :label="$t('serviceManage.applicationMaterials')" :span="2">
          <div>
            <div style="white-space: pre-wrap; margin-bottom: 12px;">{{ getMaterialsText(detailRecord.applicationMaterials) || $t('serviceManage.none') }}</div>
            <div v-if="detailRecord.attachments && detailRecord.attachments.length > 0" style="margin-top: 12px;">
              <div style="font-weight: 500; margin-bottom: 8px;">{{ $t('common.attachments') }} ({{ detailRecord.attachments.length }})</div>
              <div v-for="file in detailRecord.attachments" :key="file.fileId" style="margin-bottom: 8px; display: flex; align-items: center; gap: 8px;">
                <span style="flex: 1;">{{ file.fileName }}</span>
                <el-button size="small" type="primary" link @click="previewFile(file)">
                  <el-icon><View /></el-icon> {{ $t('common.preview') }}
                </el-button>
                <el-button size="small" type="success" link @click="downloadFile(file)">
                  <el-icon><Download /></el-icon> {{ $t('common.download') }}
                </el-button>
              </div>
            </div>
            <div v-else-if="detailRecord.attachments !== undefined" style="color: #909399; font-size: 12px; margin-top: 8px;">
              {{ $t('common.noAttachments') }}
            </div>
            <div v-else style="color: #909399; font-size: 12px; margin-top: 8px;">
              {{ $t('common.loadingAttachments') }}
            </div>
          </div>
        </el-descriptions-item>
        <el-descriptions-item :label="$t('serviceManage.handleResult')" :span="2" v-if="detailRecord.handleResult">
          <div style="white-space: pre-wrap;">{{ detailRecord.handleResult }}</div>
        </el-descriptions-item>
      </el-descriptions>
      
      <template #footer>
        <el-button @click="detailDialogVisible = false">{{ $t('common.close') }}</el-button>
      </template>
    </el-dialog>

    <!-- 文件预览对话框 -->
    <el-dialog v-model="previewDialogVisible" :title="previewFileInfo?.fileName || previewFileInfo?.FileName || $t('common.preview')" width="80%" top="5vh" :close-on-click-modal="true" :close-on-press-escape="true" @close="handleClosePreview">
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
        <el-button v-if="previewFileInfo && !isImageFile(previewFileInfo.fileType) && !isPdfFile(previewFileInfo.fileType)" type="primary" @click="downloadFile(previewFileInfo)">
          <el-icon><Download /></el-icon> {{ $t('common.download') }}
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, nextTick, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import { useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search, Plus, View, Download, Document, Loading } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const { t } = useI18n()
const route = useRoute()

const userStore = useUserStore()
// 根据路由查询参数设置默认标签页，如果 tab=items 则显示事项列表，否则显示待审核申请
const activeTab = ref(route.query.tab === 'items' ? 'items' : 'pending')
const pendingList = ref([])
const pendingLoading = ref(false)
const itemList = ref([])
const itemLoading = ref(false)
const itemSearchKeyword = ref('')
const currentUserId = userStore.userInfo?.userId

// 事项名称映射表（用于快速查找事项名称）
const itemNameMap = ref({})

// 审批相关
const approveDialogVisible = ref(false)
const approveRecord = ref(null)
const approveForm = ref({ status: '已通过', handleResult: '' })
const approving = ref(false)

// 事项编辑相关
const itemDialogVisible = ref(false)
const editingItem = ref(null)
const itemFormRef = ref(null)
const itemForm = ref({
  itemName: '',
  departmentName: '',
  handleRequirement: '',
  isEnabled: true
})
const saving = ref(false)

// 详情相关
const detailDialogVisible = ref(false)
const detailRecord = ref(null)

// 文件预览对话框
const previewDialogVisible = ref(false)
const previewFileInfo = ref(null)
const previewImageUrl = ref('')
const previewPdfUrl = ref('')

// 事项表单验证规则
const itemRules = computed(() => ({
  itemName: [{ required: true, message: t('serviceManage.itemNamePlaceholder'), trigger: 'blur' }],
  departmentName: [{ required: true, message: t('serviceManage.departmentNamePlaceholder'), trigger: 'blur' }],
  handleRequirement: [{ required: true, message: t('serviceManage.handleRequirementPlaceholder'), trigger: 'blur' }]
}))

// 过滤事项列表
const filteredItemList = computed(() => {
  if (!itemSearchKeyword.value) return itemList.value
  const keyword = itemSearchKeyword.value.toLowerCase()
  return itemList.value.filter(item => 
    (item.itemName && item.itemName.toLowerCase().includes(keyword)) ||
    (item.departmentName && item.departmentName.toLowerCase().includes(keyword))
  )
})

// 获取事项名称（带缓存）
const getItemName = (itemId) => {
  if (!itemId) return ''
  return itemNameMap.value[itemId] || ''
}

// 加载事项名称映射
const loadItemNames = async () => {
  try {
    const lang = userStore.locale || 'zh-CN'
    const res = await request.get('/service-item/all', { params: { languageType: lang } })
    const items = Array.isArray(res.data) ? res.data : []
    const map = {}
    items.forEach(item => {
      if (item.itemId || item.id) {
        const id = item.itemId || item.id
        map[id] = item.itemName || item.name || item.serviceName || ''
      }
    })
    itemNameMap.value = map
    console.log('事项名称映射加载完成:', map)
  } catch (e) {
    console.warn('加载事项名称映射失败:', e)
  }
}

// 获取待审核申请
const getPending = async () => {
  pendingLoading.value = true
  try {
    const lang = userStore.locale || 'zh-CN'
    const res = await request.get('/service-item/applications/pending', {
      params: { languageType: lang }
    })
    const list = Array.isArray(res.data) ? res.data : []
    console.log('获取待审核申请列表:', list)
    
    // 加载事项名称映射
    await loadItemNames()
    
    // 为每个申请加载附件列表
    for (const item of list) {
      console.log('正在为记录加载附件，recordId:', item.recordId)
      await loadAttachmentsForRecord(item)
      console.log('附件加载完成，recordId:', item.recordId, 'attachments:', item.attachments)
    }
    pendingList.value = list
    console.log('最终列表:', pendingList.value)
  } catch (e) {
    console.error('获取待审核申请失败:', e)
    ElMessage.error(e.message || t('serviceManage.fetchPendingFailed'))
    pendingList.value = []
  } finally {
    pendingLoading.value = false
  }
}

// 为申请记录加载附件列表
const loadAttachmentsForRecord = async (record) => {
  // 尝试多种可能的recordId字段名
  const recordId = record.recordId || record.RecordID || record.record_id || record.id
  console.log('loadAttachmentsForRecord - 完整record对象:', JSON.stringify(record, null, 2))
  console.log('loadAttachmentsForRecord - 尝试的recordId值:', recordId)
  
  if (!recordId) {
    console.error('loadAttachmentsForRecord: 无法找到recordId字段', {
      'record.recordId': record.recordId,
      'record.RecordID': record.RecordID,
      'record.record_id': record.record_id,
      'record.id': record.id,
      '所有字段': Object.keys(record)
    })
    record.attachments = []
    return
  }
  
  try {
    console.log('正在加载附件，recordId:', recordId, 'URL:', `/file/record/${recordId}`)
    const fileRes = await request.get(`/file/record/${recordId}`)
    console.log('附件加载完整响应:', JSON.stringify(fileRes, null, 2))
    console.log('附件加载响应.data:', fileRes.data)
    console.log('附件加载响应.data类型:', typeof fileRes.data)
    console.log('附件加载响应.data是否为数组:', Array.isArray(fileRes.data))
    
    // 处理不同的响应格式
    let attachments = []
    if (Array.isArray(fileRes.data)) {
      attachments = fileRes.data
    } else if (fileRes.data && Array.isArray(fileRes.data.data)) {
      attachments = fileRes.data.data
    } else if (fileRes.data && fileRes.data.list) {
      attachments = fileRes.data.list
    }
    
    console.log('解析后的附件列表:', attachments)
    console.log('附件数量:', attachments.length)
    
    // 确保每个附件都有正确的字段名（处理可能的字段名不匹配）
    attachments = attachments.map(file => {
      // 处理可能的字段名不匹配
      return {
        fileId: file.fileId || file.FileID || file.file_id || file.id,
        fileName: file.fileName || file.FileName || file.file_name || file.name,
        fileType: file.fileType || file.FileType || file.file_type || file.type,
        fileSize: file.fileSize || file.FileSize || file.file_size || file.size,
        storagePath: file.storagePath || file.StoragePath || file.storage_path,
        uploadTime: file.uploadTime || file.UploadTime || file.upload_time,
        ...file // 保留其他字段
      }
    })
    
    record.attachments = attachments
    console.log('最终设置的附件列表:', record.attachments)
    console.log('record对象更新后:', record)
  } catch (e) {
    console.error('获取附件列表失败，详细错误:', e)
    console.error('错误响应:', e.response)
    console.error('错误数据:', e.response?.data)
    record.attachments = []
  }
}

// 获取事项列表
const getItemList = async () => {
  itemLoading.value = true
  try {
    const lang = userStore.locale || 'zh-CN'
    // 政务人员可以使用 /all 接口获取所有事项
    const res = await request.get('/service-item/all', {
      params: { languageType: lang }
    })
    itemList.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    // 如果 /all 不可用，尝试使用 /search
    try {
      const lang = userStore.locale || 'zh-CN'
      const searchRes = await request.get('/service-item/search', {
        params: {
          onlyEnabled: false,
          languageType: lang,
          page: 1,
          size: 1000
        }
      })
      if (searchRes.data && searchRes.data.records) {
        itemList.value = searchRes.data.records
      } else if (Array.isArray(searchRes.data)) {
        itemList.value = searchRes.data
      } else {
        itemList.value = []
      }
    } catch (e2) {
      ElMessage.error(e2.message || e.message || t('serviceManage.fetchItemsFailed'))
      itemList.value = []
    }
  } finally {
    itemLoading.value = false
  }
}

// 打开审批对话框
const openApproveDialog = async (row) => {
  approveRecord.value = { ...row }
  console.log('打开审批对话框，row:', row, 'attachments:', row.attachments)
  // 确保附件已加载（即使已经加载，也强制重新加载以确保最新）
  await loadAttachmentsForRecord(approveRecord.value)
  console.log('审批对话框附件加载后:', approveRecord.value.attachments)
  approveForm.value = {
    status: row.status === '待审核' ? '已通过' : (row.status || '已通过'),
    handleResult: row.handleResult || ''
  }
  approveDialogVisible.value = true
}

// 提交审批
const submitApprove = async () => {
  if (!approveForm.value.status || !approveForm.value.handleResult) {
    ElMessage.warning(t('serviceManage.fillApproveInfo'))
    return
  }
  
  approving.value = true
  try {
    await request.put(
      `/service-item/applications/${approveRecord.value.recordId}/handle`,
      null,
      {
        params: {
          status: approveForm.value.status,
          handleResult: approveForm.value.handleResult,
          handleUserId: currentUserId
        }
      }
    )
    ElMessage.success(t('serviceManage.approveSuccess'))
    approveDialogVisible.value = false
    getPending()
  } catch (e) {
    ElMessage.error(e.message || t('serviceManage.approveFailed'))
  } finally {
    approving.value = false
  }
}

// 查看详情
const viewDetail = async (row) => {
  detailRecord.value = { ...row }
  console.log('打开详情对话框，row:', row, 'attachments:', row.attachments)
  // 加载附件列表（即使已经加载，也强制重新加载以确保最新）
  await loadAttachmentsForRecord(detailRecord.value)
  console.log('详情对话框附件加载后:', detailRecord.value.attachments)
  detailDialogVisible.value = true
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
    // 即使失败也打开对话框，显示错误信息
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

// 启用/禁用事项
const toggleEnable = async (row) => {
  row.toggling = true
  try {
    await request.put(`/service-item/${row.itemId}/status?isEnabled=${row.isEnabled}`)
    ElMessage.success(row.isEnabled ? t('serviceManage.enabled') : t('serviceManage.disabled'))
  } catch (e) {
    row.isEnabled = !row.isEnabled // 回滚
    ElMessage.error(e.message || t('common.updateFailed'))
  } finally {
    row.toggling = false
  }
}

// 删除事项
const deleteItem = async (row) => {
  try {
    await request.delete(`/service-item/${row.itemId}`)
    ElMessage.success(t('common.delete') + t('common.success'))
    getItemList()
  } catch (e) {
    ElMessage.error(e.message || t('common.delete') + t('common.updateFailed'))
  }
}

// 打开新建对话框
const openCreateDialog = () => {
  editingItem.value = null
  itemForm.value = {
    itemName: '',
    departmentName: '',
    handleRequirement: '',
    isEnabled: true
  }
  itemDialogVisible.value = true
  nextTick(() => {
    itemFormRef.value?.clearValidate()
  })
}

// 打开编辑对话框
const openEditDialog = (row) => {
  editingItem.value = row
  itemForm.value = {
    itemName: row.itemName || '',
    departmentName: row.departmentName || '',
    handleRequirement: row.handleRequirement || '',
    isEnabled: row.isEnabled !== undefined ? row.isEnabled : true
  }
  itemDialogVisible.value = true
  nextTick(() => {
    itemFormRef.value?.clearValidate()
  })
}

// 提交事项（新建或更新）
const submitItem = async () => {
  if (!itemFormRef.value) return
  
  await itemFormRef.value.validate(async (valid) => {
    if (!valid) return
    
    saving.value = true
    try {
      if (editingItem.value) {
        // 更新事项
        await request.put(`/service-item/${editingItem.value.itemId}`, itemForm.value)
        ElMessage.success(t('serviceManage.updateSuccess'))
      } else {
        // 创建事项
        await request.post('/service-item', itemForm.value)
        ElMessage.success(t('serviceManage.createSuccess'))
      }
      itemDialogVisible.value = false
      getItemList()
    } catch (e) {
      ElMessage.error(e.message || t('common.updateFailed'))
    } finally {
      saving.value = false
    }
  })
}

// Tab切换
const handleTabChange = (tabName) => {
  if (tabName === 'pending') {
    getPending()
  } else if (tabName === 'items') {
    getItemList()
  }
}

// 工具函数
const formatTime = (time) => {
  if (!time) return '-'
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

const getStatusType = (status) => {
  const statusMap = {
    '待审核': 'warning',
    '待处理': 'warning',
    '处理中': 'info',
    '已通过': 'success',
    '已完成': 'success',
    '驳回': 'danger'
  }
  return statusMap[status] || 'info'
}

// 监听路由查询参数变化
watch(() => route.query.tab, (newTab) => {
  if (newTab === 'items') {
    activeTab.value = 'items'
    if (activeTab.value === 'items' && itemList.value.length === 0) {
      getItemList()
    }
  } else if (newTab === 'pending') {
    activeTab.value = 'pending'
    if (activeTab.value === 'pending' && pendingList.value.length === 0) {
      getPending()
    }
  }
}, { immediate: true })

// 生命周期
onMounted(() => {
  // 根据初始标签页加载数据
  if (activeTab.value === 'items') {
    getItemList()
  } else {
    getPending()
  }
})
</script>

<style scoped>
.service-manage-container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 28px;
  margin-bottom: 24px;
  color: #333;
  font-weight: 600;
}
</style>
