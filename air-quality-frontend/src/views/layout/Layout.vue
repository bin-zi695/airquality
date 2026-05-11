<template>
  <el-container class="layout-container">
    <el-header class="layout-header">
      <div class="header-left">
        <el-icon :size="26" color="#4CAF50"><Sunny /></el-icon>
        <span class="logo-text">城市空气质量管理系统</span>
      </div>
      <div class="header-right" v-if="userStore.isLoggedIn">
        <el-badge :value="alertCount" :hidden="!alertCount" class="header-badge">
          <el-icon :size="20" color="#E6A23C"><Bell /></el-icon>
        </el-badge>
        <el-dropdown @command="handleCommand" trigger="click">
          <span class="user-info">
            <el-avatar :size="34" :key="userStore.userInfo?.avatar" :icon="UserFilled" :src="userStore.userInfo?.avatar" />
            <span class="username">{{ userStore.userInfo?.username }}</span>
            <el-icon class="arrow-icon"><ArrowDown /></el-icon>
          </span>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item command="profile">
                <el-icon><User /></el-icon>个人中心
              </el-dropdown-item>
              <el-dropdown-item command="logout" divided>
                <el-icon><SwitchButton /></el-icon>退出登录
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
      <div class="header-right" v-else>
        <el-button type="primary" size="default" round @click="$router.push('/login')">登 录</el-button>
        <el-button size="default" round @click="$router.push('/register')" class="reg-btn">注 册</el-button>
      </div>
    </el-header>
    <el-container class="body-container">
      <el-aside width="230px" class="layout-aside">
        <div class="aside-brand">
          <div class="aside-avatar">
            <el-avatar :size="44" :key="'side-' + userStore.userInfo?.avatar" :icon="UserFilled" :src="userStore.userInfo?.avatar" v-if="userStore.isLoggedIn" />
            <el-avatar :size="44" icon="UserFilled" v-else />
          </div>
          <div class="aside-user" v-if="userStore.isLoggedIn">
            <div class="aside-nickname">{{ userStore.userInfo?.username }}</div>
            <div class="aside-role">
              <el-tag size="small" :type="userStore.isAdmin ? 'danger' : 'success'" effect="dark" round>
                {{ userStore.isAdmin ? '管理员' : '普通用户' }}
              </el-tag>
            </div>
          </div>
        </div>
        <el-menu
          :default-active="activeMenu"
          router
          background-color="transparent"
          text-color="rgba(255,255,255,0.65)"
          active-text-color="#fff"
          class="side-menu"
        >
          <el-menu-item index="/dashboard">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </el-menu-item>
          <el-menu-item index="/air-quality">
            <el-icon><Search /></el-icon>
            <span>空气质量查询</span>
          </el-menu-item>
          <el-menu-item index="/charts">
            <el-icon><TrendCharts /></el-icon>
            <span>数据可视化</span>
          </el-menu-item>
          <el-menu-item index="/favorites">
            <el-icon><Star /></el-icon>
            <span>我的收藏</span>
          </el-menu-item>
          <el-menu-item index="/articles">
            <el-icon><Reading /></el-icon>
            <span>科普资讯</span>
          </el-menu-item>

          <template v-if="userStore.isAdmin">
            <div class="menu-divider"></div>
            <el-sub-menu index="admin">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/admin/users"><el-icon><UserFilled /></el-icon><span>用户管理</span></el-menu-item>
              <el-menu-item index="/admin/cities"><el-icon><OfficeBuilding /></el-icon><span>城市管理</span></el-menu-item>
              <el-menu-item index="/admin/air-data"><el-icon><Document /></el-icon><span>数据管理</span></el-menu-item>
              <el-menu-item index="/admin/alert"><el-icon><Setting /></el-icon><span>预警配置</span></el-menu-item>
              <el-menu-item index="/admin/articles"><el-icon><Edit /></el-icon><span>资讯管理</span></el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </el-aside>
      <el-main class="layout-main">
        <div class="page-enter">
          <router-view />
        </div>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { User, UserFilled, SwitchButton, Bell, Sunny, OfficeBuilding, Document, Edit } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const activeMenu = computed(() => route.path)
const alertCount = ref(0)

function handleCommand(command) {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  }
}
</script>

<style scoped>
.layout-container {
  height: 100vh;
  background: #f0f2f5;
}

.layout-header {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 60%, #0f3460 100%);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 60px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
  position: relative;
  z-index: 10;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.logo-icon {
  font-size: 26px;
}

.logo-text {
  color: #fff;
  font-size: 17px;
  font-weight: 600;
  letter-spacing: 0.5px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 16px;
}

.header-badge {
  cursor: pointer;
}

.reg-btn {
  color: #fff;
  border-color: rgba(255, 255, 255, 0.4);
}

.reg-btn:hover {
  border-color: #fff;
  color: #fff;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
  cursor: pointer;
  padding: 4px 8px;
  border-radius: 8px;
  transition: background 0.2s;
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.08);
}

.username {
  font-size: 14px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow-icon {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.55);
}

.body-container {
  height: calc(100vh - 60px);
}

.layout-aside {
  background: linear-gradient(180deg, #1e2a3a 0%, #1a2332 50%, #15202a 100%);
  overflow-y: auto;
  overflow-x: hidden;
  border-right: 1px solid rgba(255, 255, 255, 0.05);
}

.aside-brand {
  padding: 24px 20px 16px;
  display: flex;
  align-items: center;
  gap: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
  margin-bottom: 8px;
}

.aside-nickname {
  color: #fff;
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 4px;
}

.aside-role {
  font-size: 12px;
}

.menu-divider {
  height: 1px;
  background: rgba(255, 255, 255, 0.06);
  margin: 8px 16px;
}

.side-menu {
  border-right: none;
}

.side-menu :deep(.el-menu-item) {
  margin: 2px 8px;
  border-radius: 8px;
  transition: all 0.2s;
}

.side-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.06);
}

.side-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.35), rgba(118, 75, 162, 0.25));
  border-radius: 8px;
}

.side-menu :deep(.el-sub-menu__title) {
  margin: 2px 8px;
  border-radius: 8px;
}

.side-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.06);
}

.side-menu :deep(.el-sub-menu .el-menu-item) {
  padding-left: 56px !important;
  font-size: 13px;
}

.layout-main {
  background: #f0f2f5;
  padding: 20px 24px;
  overflow-y: auto;
  position: relative;
}

.layout-main::before {
  content: '';
  position: fixed;
  width: 500px;
  height: 500px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.03) 0%, transparent 70%);
  top: 100px;
  right: -100px;
  pointer-events: none;
  z-index: 0;
}

.layout-main > * {
  position: relative;
}
</style>
