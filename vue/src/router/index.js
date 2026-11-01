import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/faq',
    name: 'Faq',
    component: () => import('@/views/Faq.vue'),
    meta: { title: '常见问题', requiresAuth: false }
  },
  {
    path: '/',
    component: () => import('@/layouts/MainLayout.vue'),
    redirect: '/home',
    children: [
      {
        path: 'home',
        name: 'Home',
        component: () => import('@/views/Home.vue'),
        meta: { title: '首页' }
      },
      {
        path: 'services',
        name: 'Services',
        component: () => import('@/views/Services.vue'),
        meta: { title: '政务服务' }
      },
      {
        path: 'applications',
        name: 'Applications',
        component: () => import('@/views/Applications.vue'),
        meta: { title: '我的申请', requiresAuth: true }
      },
      {
        path: 'applications/:id',
        name: 'ApplicationDetail',
        component: () => import('@/views/ApplicationDetail.vue'),
        meta: { title: '申请详情', requiresAuth: true }
      },
      {
        path: 'consultation',
        name: 'Consultation',
        component: () => import('@/views/Consultation.vue'),
        meta: { title: '智能咨询' }
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      },
      {
        path: 'user-management',
        name: 'UserManagement',
        component: () => import('@/views/UserManagement.vue'),
        meta: { title: '用户管理', requiresAuth: true }
      },
      {
        path: 'service-manage',
        name: 'ServiceManage',
        component: () => import('@/views/ServiceManage.vue'),
        meta: { title: '事项管理', requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
