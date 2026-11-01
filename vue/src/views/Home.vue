<template>
  <div class="home-container">
    <!-- 天安门背景横幅 -->
    <div class="banner-section">
      <div class="banner-overlay">
        <div class="banner-content">
          <h1>{{ $t('common.title') }}</h1>
          <p class="subtitle">{{ $t('common.welcomeSubtitle') }}</p>
          <el-button v-if="!userStore.isGov" type="primary" size="large" class="apply-btn" @click="$router.push('/services')">
            {{ $t('common.submitApply') }}
          </el-button>
          <el-button v-else type="primary" size="large" class="apply-btn" @click="$router.push('/service-manage?tab=items')">
            {{ $t('common.serviceManage') }}
          </el-button>
        </div>
      </div>
    </div>

    <!-- 政务服务分类（仅显示真实可用的） -->
    <div class="services-section">
      <h2 class="section-title">{{ $t('common.services') }}</h2>
      <p class="section-desc">{{ $t('common.servicesDesc') }}</p>
      <el-button v-if="!userStore.isGov" type="primary" @click="$router.push('/services')" class="view-all-btn">
        {{ $t('common.viewAllServices') }} <el-icon class="el-icon--right"><ArrowRight /></el-icon>
      </el-button>
      <el-button v-else type="primary" @click="$router.push('/service-manage?tab=items')" class="view-all-btn">
        {{ $t('common.enterServiceManage') }} <el-icon class="el-icon--right"><ArrowRight /></el-icon>
      </el-button>
    </div>

    <!-- 快捷功能入口（居中、留白更舒适） -->
    <div class="features-section">
      <el-row :gutter="24" class="features-row" justify="center" align="stretch">
        <el-col :xs="24" :sm="12" :md="8" :lg="7" v-for="feature in features" :key="feature.id">
          <el-card class="feature-card" shadow="hover" @click="handleFeatureClick(feature)">
            <div class="feature-icon">
              <el-icon :size="48"><component :is="feature.icon" /></el-icon>
            </div>
            <h3>{{ feature.title }}</h3>
            <p>{{ feature.desc }}</p>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { computed, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { Document, List, ChatDotRound, ArrowRight, Search, UserFilled, Setting } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const { t } = useI18n()

// 页面加载时确保用户信息完整
onMounted(async () => {
  if (userStore.isLoggedIn && userStore.token && (!userStore.userInfo?.role)) {
    try {
      await userStore.getProfile(false)
      console.log('Home页面：用户信息已刷新，role:', userStore.userInfo?.role)
    } catch (e) {
      console.warn('Home页面：获取用户信息失败:', e.message || e)
    }
  }
})

// 根据角色显示不同的功能入口
const features = computed(() => {
  if (userStore.isGov) {
    // 政务人员功能
    return [
      {
        id: 1,
        icon: List,
        title: t('common.pendingReview'),
        desc: t('common.reviewApplications'),
        route: '/service-manage'
      },
      {
        id: 2,
        icon: Setting,
        title: t('common.serviceManage'),
        desc: t('common.manageItems'),
        route: '/service-manage?tab=items'
      },
      {
        id: 3,
        icon: UserFilled,
        title: t('nav.userManagement'),
        desc: t('common.manageUsers'),
        route: '/user-management'
      }
    ]
  } else {
    // 居民功能
    return [
      {
        id: 1,
        icon: Document,
        title: t('common.submitApply'),
        desc: t('common.onlineSubmit'),
        route: '/services'
      },
      {
        id: 2,
        icon: Search,
        title: t('common.progress'),
        desc: t('common.queryProgress'),
        route: '/applications'
      },
      {
        id: 3,
        icon: ChatDotRound,
        title: t('common.consultation'),
        desc: t('common.smartConsultation'),
        route: '/consultation'
      }
    ]
  }
})

const handleFeatureClick = (feature) => {
  router.push(feature.route)
}
</script>

<style scoped>
.home-container {
  width: 100%;
  min-height: calc(100vh - 140px);
  margin: -20px;
  padding: 20px;
  box-sizing: border-box;
}

/* 天安门背景横幅 */
.banner-section {
  width: 100%;
  height: 400px;
  background-image: url('https://images.unsplash.com/photo-1508804185872-d7badad00f7d?w=1920&q=80');
  background-size: cover;
  background-position: center;
  position: relative;
  margin-bottom: 40px;
}

.banner-overlay {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(220, 38, 38, 0.85) 0%, rgba(239, 68, 68, 0.75) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-content {
  text-align: center;
  color: white;
  padding: 20px;
}

.banner-content h1 {
  font-size: 48px;
  font-weight: bold;
  margin-bottom: 20px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.subtitle {
  font-size: 20px;
  margin-bottom: 30px;
  opacity: 0.95;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
}

.apply-btn {
  font-size: 18px;
  padding: 15px 40px;
  border-radius: 25px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

/* 政务服务区 */
.services-section {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto 60px;
  padding: 40px 20px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 16px;
  text-align: center;
  box-sizing: border-box;
}

/* 快捷功能区 */
.features-section {
  max-width: 1200px;
  width: 100%;
  margin: 0 auto 60px;
  padding: 40px 20px;
  box-sizing: border-box;
}

.features-row {
  margin: 0 !important;
}

.feature-card {
  text-align: center;
  height: 220px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-radius: 12px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  margin-bottom: 0;
  box-sizing: border-box;
}

.feature-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.15);
}

.feature-icon {
  margin: 0 0 20px;
  color: #dc2626;
}

.feature-card h3 {
  font-size: 20px;
  margin-bottom: 12px;
  color: #333;
  font-weight: 600;
}

.feature-card p {
  color: #666;
  font-size: 14px;
  padding: 0 20px;
}

.section-title {
  font-size: 32px;
  color: #333;
  margin-bottom: 12px;
  font-weight: bold;
}

.section-desc {
  font-size: 16px;
  color: #666;
  margin-bottom: 30px;
}

.view-all-btn {
  font-size: 16px;
  padding: 12px 30px;
  border-radius: 20px;
}
</style>
