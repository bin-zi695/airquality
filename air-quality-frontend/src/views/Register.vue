<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-left">
        <div class="left-content animate__animated animate__fadeInLeft">
          <div class="brand-icon">
            <el-icon :size="40" color="#4CAF50"><Sunny /></el-icon>
          </div>
          <h1 class="brand-title">加入我们</h1>
          <p class="brand-desc">
            创建账号，开启空气质量监测之旅
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
              <strong>免费</strong>
              <span>注册使用</span>
            </div>
            <div class="data-badge">
              <strong>安全</strong>
              <span>数据加密</span>
            </div>
            <div class="data-badge">
              <strong>秒级</strong>
              <span>响应速度</span>
            </div>
          </div>
        </div>
      </div>

      <div class="register-right">
        <div class="form-wrapper animate__animated animate__fadeInRight">
          <h2 class="form-title">创建账号</h2>
          <p class="form-subtitle">填写信息完成注册</p>
          <el-form :model="form" :rules="rules" ref="formRef" size="large" class="register-form">
            <el-form-item prop="username">
              <el-input
                v-model="form.username"
                placeholder="请输入用户名"
                :prefix-icon="User"
                class="custom-input"
              />
            </el-form-item>
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
                placeholder="请设置密码（至少6位）"
                :prefix-icon="Lock"
                show-password
                class="custom-input"
              />
            </el-form-item>
            <el-form-item prop="confirmPassword">
              <el-input
                v-model="form.confirmPassword"
                type="password"
                placeholder="请再次输入密码"
                :prefix-icon="Lock"
                show-password
                class="custom-input"
                @keyup.enter="handleRegister"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="loading" class="submit-btn" @click="handleRegister">
                注 册
              </el-button>
            </el-form-item>
          </el-form>
          <div class="form-footer">
            <span>已有账号？</span>
            <router-link to="/login" class="link">立即登录</router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({ username: '', email: '', password: '', confirmPassword: '' })

const validateConfirm = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 20, message: '用户名长度2-20位', trigger: 'blur' },
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少6位', trigger: 'blur' },
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' },
  ],
}

async function handleRegister() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.register({ username: form.username, email: form.email, password: form.password })
    ElMessage.success('注册成功，请登录')
    router.push('/login')
  } catch {
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #e8edf5 0%, #f0f5ff 50%, #eef2f9 100%);
  padding: 20px;
}

.register-container {
  display: flex;
  width: 960px;
  min-height: 580px;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.12), 0 8px 24px rgba(0, 0, 0, 0.06);
}

.register-left {
  flex: 1;
  background: linear-gradient(160deg, #065f8a 0%, #1e293b 40%, #0f172a 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px 40px;
  position: relative;
  overflow: hidden;
}

.register-left::before {
  content: '';
  position: absolute;
  width: 300px;
  height: 300px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(16, 185, 129, 0.15) 0%, transparent 70%);
  bottom: -80px;
  left: -80px;
}

.register-left::after {
  content: '';
  position: absolute;
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(59, 130, 246, 0.12) 0%, transparent 70%);
  top: -40px;
  right: -50px;
}

.left-content {
  position: relative;
  z-index: 1;
  color: #fff;
}

.brand-icon { margin-bottom: 24px; }

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
  margin-bottom: 12px;
  letter-spacing: 1px;
  background: linear-gradient(90deg, #fff, rgba(255,255,255,0.8));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
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
  padding: 8px 12px;
  border-radius: 10px;
  background: rgba(255,255,255,0.03);
  transition: background 0.2s, transform 0.2s;
}

.feature-item:hover {
  background: rgba(255,255,255,0.07);
  transform: translateX(4px);
}

.feature-icon { font-size: 18px; }

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
}

.register-right {
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

.register-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  padding: 4px 12px;
  box-shadow: 0 0 0 1px #e4e7ed inset;
  transition: box-shadow 0.3s;
}

.register-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #c0c4cc inset;
}

.register-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.25) inset;
}

.register-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

.submit-btn {
  width: 100%;
  height: 44px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 4px;
  background: linear-gradient(135deg, #10b981 0%, #34d399 100%);
  border: none;
  transition: transform 0.2s, box-shadow 0.2s;
}

.submit-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 8px 24px rgba(16, 185, 129, 0.35);
}

.form-footer {
  text-align: center;
  font-size: 13px;
  color: #999;
  margin-top: 8px;
}

.form-footer .link {
  color: #10b981;
  font-weight: 500;
  text-decoration: none;
}

.form-footer .link:hover {
  color: #34d399;
}

@media (max-width: 768px) {
  .register-container {
    flex-direction: column;
    width: 100%;
  }
  .register-left {
    padding: 32px 24px;
    min-height: 200px;
  }
  .brand-title { font-size: 22px; }
  .register-right { padding: 32px 24px; }
}
</style>
