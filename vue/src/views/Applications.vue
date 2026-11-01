<template>
  <div class="applications-container">
    <h2 class="page-title">{{ userStore.isGov ? $t('applications.pendingReview') : $t('applications.title') }}</h2>
    <!-- 政务人员：显示待审核申请 -->
    <div v-if="userStore.isGov" class="gov-hint">
      <el-alert
        :title="$t('applications.reviewHint')"
        :description="$t('applications.reviewDesc')"
        type="info"
        :closable="false"
        show-icon
      >
        <template #default>
          <div style="margin-top: 10px;">
            <el-button type="primary" @click="$router.push('/service-manage')">{{ $t('applications.enterServiceManage') }}</el-button>
          </div>
        </template>
      </el-alert>
    </div>
    <!-- 居民：显示我的申请列表 -->
    <el-table v-else :data="applications" style="width: 100%" v-loading="loading">
      <el-table-column prop="displayRecordId" :label="$t('applications.applicationNumber')" width="180" />
      <el-table-column prop="displayItemName" :label="$t('applications.serviceName')" />
      <el-table-column prop="displayApplyTime" :label="$t('applications.applyTime')" width="180" />
      <el-table-column prop="status" :label="$t('applications.status')" width="120">
        <template #default="{ row }">
          <el-tag>
            {{ row.status }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column :label="$t('common.view')" width="100">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleViewDetail(row)">{{ $t('common.view') }}</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const applications = ref([])
const serviceMap = ref({})

const fetchServicesMap = async () => {
  // 获取所有已启用事项并转成map (itemId=>itemName)
  const lang = userStore.locale || 'zh-CN'
  const res = await request.get('/service-item/enabled', { params: { languageType: lang } })
  if(Array.isArray(res.data)) {
    serviceMap.value = Object.fromEntries(
      res.data.map(item => [item.itemId || item.id, item.itemName || item.name || item.serviceName || ''])
    )
  }
}
const fetchApplications = async () => {
  if (userStore.isGov) return // 政务人员不需要
  loading.value = true
  try {
    // 先拉服务map
    await fetchServicesMap()
    const lang = userStore.locale || 'zh-CN'
    const res = await request.get('/service-item/applications/me', { params: { languageType: lang } })
    const list = Array.isArray(res.data) ? res.data : []
    applications.value = list.map((it) => ({
      ...it,
      displayRecordId: it.recordId || it.id || it.applicationId,
      displayItemName: serviceMap.value[it.itemId || it.serviceId] || '',
      displayApplyTime: it.applyTime || it.applicationTime || it.createTime || it.submitTime || ''
    }))
  } finally {
    loading.value = false
  }
}
onMounted(fetchApplications)
const handleViewDetail = (row) => {
  router.push(`/applications/${row.recordId || row.id}`)
}
</script>
<style scoped>
.applications-container { max-width: 1200px; margin: 0 auto; }
.page-title { font-size: 28px; margin-bottom: 24px; color: #333; }
.gov-hint {
  max-width: 800px;
  margin: 40px auto;
}
</style>
