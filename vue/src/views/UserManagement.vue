<template>
  <div class="user-mgmt-container">
    <h2 class="page-title">{{ $t('userManagement.title') }}</h2>
    
    <!-- 统计卡片 -->
    <el-row :gutter="20" style="margin-bottom: 24px">
      <el-col :xs="24" :sm="12" :md="6" v-for="stat in statistics" :key="stat.label">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-label">{{ stat.label }}</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和筛选 -->
    <el-card style="margin-bottom: 20px">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="searchKeyword"
            :placeholder="$t('userManagement.searchPlaceholder')"
            clearable
            @clear="fetchUsers"
            @keyup.enter="fetchUsers"
          >
            <template #prefix>
              <el-icon><Search /></el-icon>
            </template>
          </el-input>
        </el-col>
        <el-col :span="4">
          <el-select v-model="selectedRole" :placeholder="$t('userManagement.filterByRole')" @change="fetchUsers" clearable>
            <el-option :label="$t('userManagement.allRoles')" value="" />
            <el-option :label="$t('common.resident')" value="居民" />
            <el-option :label="$t('common.governmentStaff')" value="政务人员" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-select v-model="frozenFilter" :placeholder="$t('userManagement.filterByStatus')" @change="fetchUsers" clearable>
            <el-option :label="$t('userManagement.allStatus')" value="" />
            <el-option :label="$t('userManagement.normal')" value="false" />
            <el-option :label="$t('userManagement.frozen')" value="true" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="fetchUsers" :loading="loading">
            <el-icon><Search /></el-icon> {{ $t('common.search') }}
          </el-button>
          <el-button @click="resetSearch">{{ $t('common.reset') }}</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 用户列表 -->
    <el-card>
      <el-table :data="userList" border style="width:100%" v-loading="loading">
        <el-table-column prop="userId" :label="$t('userManagement.userId')" width="80" sortable />
        <el-table-column prop="account" :label="$t('userManagement.account')" width="120" />
        <el-table-column prop="name" :label="$t('userManagement.name')" width="120" />
        <el-table-column prop="phoneNumber" :label="$t('userManagement.phoneNumber')" width="130" />
        <el-table-column prop="ethnicity" :label="$t('userManagement.ethnicity')" width="100" />
        <el-table-column prop="role" :label="$t('userManagement.role')" width="100">
          <template #default="{row}">
            <el-tag :type="row.role === '政务人员' ? 'warning' : 'success'">
              {{ row.role }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="realNameVerified" :label="$t('userManagement.realNameVerified')" width="100">
          <template #default="{row}">
            <el-tag :type="row.realNameVerified ? 'success' : 'info'">
              {{ row.realNameVerified ? $t('userManagement.verified') : $t('userManagement.unverified') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="frozen" :label="$t('userManagement.accountStatus')" width="100">
          <template #default="{row}">
            <el-tag :type="row.frozen ? 'danger' : 'success'">
              {{ row.frozen ? $t('userManagement.frozen') : $t('userManagement.normal') }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column :label="$t('userManagement.operations')" width="220" fixed="right">
          <template #default="{row}">
            <el-button size="small" @click="toggleFreeze(row)" :type="row.frozen?'warning':'primary'">
              {{ row.frozen ? $t('userManagement.unfreeze') : $t('userManagement.freeze') }}
            </el-button>
            <el-popconfirm 
              :title="$t('userManagement.deleteConfirm')" 
              @confirm="deleteUser(row)"
              :disabled="row.role === '政务人员' && currentUserId === row.userId"
            >
              <template #reference>
                <el-button 
                  size="small" 
                  type="danger"
                  :disabled="row.role === '政务人员' && currentUserId === row.userId"
                >
                  {{ $t('common.delete') }}
                </el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 空状态 -->
      <el-empty v-if="!loading && userList.length === 0" :description="$t('userManagement.noData')" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useI18n } from 'vue-i18n'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'

const { t } = useI18n()
const userStore = useUserStore()
const userList = ref([])
const loading = ref(false)
const searchKeyword = ref('')
const selectedRole = ref('')
const frozenFilter = ref('')
const currentUserId = userStore.userInfo?.userId

// 统计信息
const statistics = computed(() => {
  const total = userList.value.length
  const frozen = userList.value.filter(u => u.frozen).length
  const gov = userList.value.filter(u => u.role === '政务人员').length
  const resident = userList.value.filter(u => u.role === '居民').length
  return [
    { label: t('userManagement.totalUsers'), value: total },
    { label: t('userManagement.frozenUsers'), value: frozen },
    { label: t('userManagement.governmentStaff'), value: gov },
    { label: t('userManagement.residents'), value: resident }
  ]
})

const fetchUsers = async () => {
  loading.value = true
  try {
    const res = await request.get('/user/all')
    let list = Array.isArray(res.data) ? res.data : []
    
    // 搜索过滤
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase()
      list = list.filter(u => 
        (u.account && u.account.toLowerCase().includes(keyword)) ||
        (u.name && u.name.toLowerCase().includes(keyword)) ||
        (u.phoneNumber && u.phoneNumber.includes(keyword))
      )
    }
    
    // 角色过滤
    if (selectedRole.value) {
      list = list.filter(u => u.role === selectedRole.value)
    }
    
    // 冻结状态过滤
    if (frozenFilter.value !== '') {
      const isFrozen = frozenFilter.value === 'true'
      list = list.filter(u => !!u.frozen === isFrozen)
    }
    
    userList.value = list
  } catch (e) {
    console.error('获取用户列表失败:', e)
    ElMessage.error(t('common.updateFailed'))
  } finally {
    loading.value = false
  }
}

const resetSearch = () => {
  searchKeyword.value = ''
  selectedRole.value = ''
  frozenFilter.value = ''
  fetchUsers()
}

const toggleFreeze = async (row) => {
  try {
    const newFrozenState = !row.frozen
    await request.put(`/user/${row.userId}`, { ...row, frozen: newFrozenState })
    ElMessage.success(newFrozenState ? t('userManagement.freezeSuccess') : t('userManagement.unfreezeSuccess'))
    await fetchUsers()
  } catch (e) {
    console.error('冻结/解冻失败:', e)
    ElMessage.error(t('common.updateFailed'))
  }
}

const deleteUser = async (row) => {
  if (row.role === '政务人员' && currentUserId === row.userId) {
    ElMessage.warning(t('userManagement.cannotDeleteSelf'))
    return
  }
  try {
    await request.delete(`/user/${row.userId}`)
    ElMessage.success(t('common.delete') + t('common.success'))
    await fetchUsers()
  } catch (e) {
    console.error('删除用户失败:', e)
    ElMessage.error(t('common.updateFailed'))
  }
}

onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-mgmt-container { max-width: 1400px; margin: 0 auto; }
.page-title { font-size: 28px; margin-bottom: 24px; color: #333; }
.stat-card { margin-bottom: 0; }
.stat-content { text-align: center; }
.stat-value { font-size: 32px; font-weight: bold; color: #409eff; margin-bottom: 8px; }
.stat-label { font-size: 14px; color: #666; }
</style>
