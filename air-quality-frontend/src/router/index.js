import { createRouter, createWebHashHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    component: () => import('@/views/layout/Layout.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('@/views/dashboard/Dashboard.vue'),
        meta: { title: '首页' },
      },
      {
        path: 'air-quality',
        name: 'AirQuality',
        component: () => import('@/views/air-quality/AirQualityList.vue'),
        meta: { title: '空气质量数据' },
      },
      {
        path: 'air-quality/:id',
        name: 'AirQualityDetail',
        component: () => import('@/views/air-quality/AirQualityDetail.vue'),
        meta: { title: '数据详情' },
      },
      {
        path: 'charts',
        name: 'Charts',
        component: () => import('@/views/charts/Charts.vue'),
        meta: { title: '数据可视化' },
      },
      {
        path: 'favorites',
        name: 'Favorites',
        component: () => import('@/views/favorites/Favorites.vue'),
        meta: { title: '我的收藏' },
      },
      {
        path: 'articles/:id',
        name: 'ArticleDetail',
        component: () => import('@/views/articles/ArticleDetail.vue'),
        meta: { title: '资讯详情' },
      },
      {
        path: 'articles',
        name: 'Articles',
        component: () => import('@/views/articles/Articles.vue'),
        meta: { title: '科普资讯' },
      },
      {
        path: 'profile',
        name: 'Profile',
        component: () => import('@/views/profile/Profile.vue'),
        meta: { title: '个人中心' },
      },
      // Admin routes
      {
        path: 'admin/users',
        name: 'AdminUsers',
        component: () => import('@/views/admin/Users.vue'),
        meta: { title: '用户管理', requiresAdmin: true },
      },
      {
        path: 'admin/cities',
        name: 'AdminCities',
        component: () => import('@/views/admin/Cities.vue'),
        meta: { title: '城市管理', requiresAdmin: true },
      },
      {
        path: 'admin/air-data',
        name: 'AdminAirData',
        component: () => import('@/views/admin/AirData.vue'),
        meta: { title: '数据管理', requiresAdmin: true },
      },
      {
        path: 'admin/alert',
        name: 'AdminAlert',
        component: () => import('@/views/admin/AlertThreshold.vue'),
        meta: { title: '预警配置', requiresAdmin: true },
      },
      {
        path: 'admin/logs/operation',
        name: 'AdminOperationLogs',
        component: () => import('@/views/admin/OperationLogs.vue'),
        meta: { title: '操作日志', requiresAdmin: true },
      },
      {
        path: 'admin/logs/access',
        name: 'AdminAccessLogs',
        component: () => import('@/views/admin/AccessLogs.vue'),
        meta: { title: '访问日志', requiresAdmin: true },
      },
      {
        path: 'admin/articles',
        name: 'AdminArticles',
        component: () => import('@/views/admin/Articles.vue'),
        meta: { title: '资讯管理', requiresAdmin: true },
      },
    ],
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userInfo = JSON.parse(localStorage.getItem('userInfo') || 'null')

  if (to.meta.requiresAdmin && userInfo?.role !== 'admin') {
    next('/dashboard')
    return
  }

  if (!token && to.path !== '/login' && to.path !== '/register') {
    next('/login')
  } else {
    next()
  }
})

export default router
