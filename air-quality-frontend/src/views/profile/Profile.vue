<template>
  <div class="profile-page">
    <div class="page-header animate__animated animate__fadeInDown">
      <h2 class="page-title"><el-icon style="margin-right:6px"><User /></el-icon>个人中心</h2>
      <p class="page-desc">管理您的个人信息</p>
    </div>

    <el-card class="profile-card animate__animated animate__fadeInUp" shadow="never">
      <template #header>
        <span class="card-title"><span class="title-dot"></span>个人信息</span>
      </template>
      <el-form :model="form" label-width="80px" class="profile-form">
        <el-form-item label="用户名">
          <el-input v-model="form.username" disabled size="large" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" disabled size="large" />
        </el-form-item>
        <el-form-item label="昵称">
          <el-input v-model="form.nickname" size="large" placeholder="请输入昵称" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" size="large" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" round @click="saveProfile" :loading="saving">
            <el-icon style="margin-right:4px"><FolderChecked /></el-icon>保存修改
          </el-button>
        </el-form-item>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { useUserStore } from '@/store/user'
import { userApi } from '@/api'
import { ElMessage } from 'element-plus'
import { FolderChecked } from '@element-plus/icons-vue'

const userStore = useUserStore()
const saving = ref(false)

const form = reactive({
  username: userStore.userInfo?.username || '',
  email: userStore.userInfo?.email || '',
  nickname: userStore.userInfo?.nickname || '',
  phone: userStore.userInfo?.phone || '',
})

async function saveProfile() {
  saving.value = true
  try {
    await userApi.update(userStore.userId, { nickname: form.nickname, phone: form.phone })
    userStore.userInfo = {
      ...userStore.userInfo,
      nickname: form.nickname,
      phone: form.phone,
    }
    localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    ElMessage.success('修改成功')
  } finally {
    saving.value = false
  }
}
</script>

<style scoped>
.page-header { margin-bottom: 20px; }
.page-title { font-size: 20px; font-weight: 700; color: #1a1a2e; margin-bottom: 4px; display: flex; align-items: center; }
.page-desc { font-size: 13px; color: #999; }

.profile-card {
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  margin-bottom: 16px;
  max-width: 560px;
}
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  display: flex;
  align-items: center;
  gap: 6px;
}
.title-dot {
  width: 8px; height: 8px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6, #06b6d4);
  display: inline-block;
}
.profile-form {
  max-width: 460px;
}
</style>
