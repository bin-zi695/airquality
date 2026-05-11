<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-left">
        <div class="left-content">
          <div class="brand-icon">
            <el-icon :size="40" color="#4CAF50"><Sunny /></el-icon>
          </div>
          <h1 class="brand-title">城市空气质量<br />数据管理系统</h1>
          <p class="brand-desc">
            实时监测，科学分析，守护每一口呼吸
          </p>
          <div class="feature-list">
            <div class="feature-item">
              <el-icon class="feature-icon"><TrendCharts /></el-icon>
              <span>多维度空气质量数据可视化</span>
            </div>
            <div class="feature-item">
              <el-icon class="feature-icon"><Bell /></el-icon>
              <span>AQI 超标智能预警通知</span>
            </div>
            <div class="feature-item">
              <el-icon class="feature-icon"><Star /></el-icon>
              <span>城市收藏与个性化数据展示</span>
            </div>
            <div class="feature-item">
              <el-icon class="feature-icon"><Reading /></el-icon>
              <span>空气质量科普与防护指南</span>
            </div>
          </div>
          <div class="left-footer">
            <div class="data-badge">
              <strong>10+</strong>
              <span>覆盖城市</span>
            </div>
            <div class="data-badge">
              <strong>6</strong>
              <span>核心指标</span>
            </div>
            <div class="data-badge">
              <strong>24h</strong>
              <span>定时更新</span>
            </div>
          </div>
        </div>
      </div>

      <div class="login-right">
        <div class="form-wrapper">
          <h2 class="form-title">欢迎回来</h2>
          <p class="form-subtitle">登录您的账号以继续</p>
          <el-form :model="form" :rules="rules" ref="formRef" size="large" class="login-form">
            <el-form-item prop="email">
              <el-input
                v-model="form.email"
                placeholder="请输入邮箱地址"
                :prefix-icon="Message"
                class="custom-input"
              />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="form.password"
                type="password"
                placeholder="请输入密码"
                :prefix-icon="Lock"
                show-password
                class="custom-input"
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" class="submit-btn" @click="handleLogin">
                登 录
              </el-button>
            </el-form-item>
          </el-form>
          <div class="form-footer">
            <span>还没有账号？</span>
            <router-link to="/register" class="link">立即注册</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'
import { Message, Lock } from '@element-plus/icons-vue'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ email: '', password: '' })
const rules = {
  email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function handleLogin() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login({ email: form.email, password: form.password })
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8edf5 0%, #f0f5ff 50%, #eef2f9 100%);
  padding: 20px;
}

.login-container {
  display: flex;
  width: 960px;
  min-height: 580px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.12), 0 8px 24px rgba(0, 0, 0, 0.06);
}

.login-left {
  flex: 1;
  background: linear-gradient(160deg, #1a1a2e 0%, #16213e 40%, #0f3460 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
  position: relative;
  overflow: hidden;
}

.login-left::before {
  content: '';
  position: absolute;
  width: 360px;
  height: 360px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(102, 126, 234, 0.15) 0%, transparent 70%);
  top: -60px;
  right: -100px;
}

.login-left::after {
  content: '';
  position: absolute;
  width: 240px;
  height: 240px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(118, 75, 162, 0.12) 0%, transparent 70%);
  bottom: -40px;
  left: -60px;
}

.left-content {
  position: relative;
  z-index: 1;
  color: #fff;
}

.brand-icon {
  margin-bottom: 24px;
}

.icon-leaf {
  font-size: 48px;
  display: inline-block;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-8px); }
}

.brand-title {
  font-size: 30px;
  font-weight: 700;
  line-height: 1.3;
  margin-bottom: 12px;
  letter-spacing: 1px;
}

.brand-desc {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.6);
  margin-bottom: 32px;
  letter-spacing: 0.5px;
}

.feature-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.75);
}

.feature-icon {
  font-size: 18px;
}

.left-footer {
  display: flex;
  gap: 32px;
}

.data-badge {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.data-badge strong {
  font-size: 22px;
  font-weight: 700;
}

.data-badge span {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.45);
  text-transform: uppercase;
}

.login-right {
  flex: 1;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
}

.form-wrapper {
  width: 100%;
  max-width: 360px;
}

.form-title {
  font-size: 26px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 6px;
}

.form-subtitle {
  font-size: 13px;
  color: #999;
  margin-bottom: 36px;
}

.login-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 12px;
  box-shadow: 0 0 0 1px #e4e7ed inset;
  transition: box-shadow 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.25) inset;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.submit-btn {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  transition: transform 0.2s, box-shadow 0.2s;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.35);
}

.form-footer {
  text-align: center;
  font-size: 13px;
  color: #999;
  margin-top: 8px;
}

.form-footer .link {
  color: #667eea;
  font-weight: 500;
  text-decoration: none;
}

.form-footer .link:hover {
  color: #764ba2;
}

@media (max-width: 768px) {
  .login-container {
    flex-direction: column;
    width: 100%;
  }
  .login-left {
    padding: 32px 24px;
    min-height: 200px;
  }
  .brand-title {
    font-size: 22px;
  }
  .login-right {
    padding: 32px 24px;
  }
}
</style>
