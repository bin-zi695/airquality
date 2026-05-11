<template>
  <div class="profile-page">
    <el-card class="profile-card" shadow="never">
      <template #header>
        <span class="card-title"><span class="title-dot"></span>个人信息</span>
      </template>
      <div class="avatar-section">
        <el-avatar :size="80" :src="avatarPreview" class="avatar-preview">
          <el-icon :size="36"><UserFilled /></el-icon>
        </el-avatar>
        <div class="avatar-actions">
          <el-upload
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
            accept="image/*"
          >
            <el-button type="primary" round size="small">
              <el-icon><Upload /></el-icon> 上传头像
            </el-button>
          </el-upload>
          <el-input
            v-model="form.avatar"
            placeholder="或粘贴图片URL地址"
            size="small"
            clearable
            @change="avatarPreview = form.avatar || userStore.userInfo?.avatar"
            style="width: 240px; margin-top: 4px"
          >
            <template #prefix>
              <el-icon><Link /></el-icon>
            </template>
          </el-input>
        </div>
      </div>
      <el-divider />
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
          <el-button type="primary" size="large" round @click="saveProfile" :loading="saving"><el-icon style="margin-right:4px"><FolderChecked /></el-icon>保存修改</el-button>
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
import { UserFilled, Upload, Link } from '@element-plus/icons-vue'
import axios from 'axios'

const userStore = useUserStore()
const saving = ref(false)

const form = reactive({
  username: userStore.userInfo?.username || '',
  email: userStore.userInfo?.email || '',
  nickname: userStore.userInfo?.nickname || '',
  phone: userStore.userInfo?.phone || '',
  avatar: userStore.userInfo?.avatar || '',
})

const avatarPreview = ref(userStore.userInfo?.avatar || '')

function beforeUpload(file) {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB')
    return false
  }
  return true
}

async function handleUpload({ file }) {
  const formData = new FormData()
  formData.append('file', file)
  try {
    const token = localStorage.getItem('token')
    const res = await axios.post('/api/upload/avatar', formData, {
      headers: { Authorization: `Bearer ${token}` },
    })
    if (res.data.code === 200) {
      form.avatar = res.data.data.url
      avatarPreview.value = res.data.data.url
      ElMessage.success('头像上传成功')
    }
  } catch {
    ElMessage.error('上传失败')
  }
}

async function saveProfile() {
  saving.value = true
  try {
    await userApi.update(userStore.userId, { nickname: form.nickname, phone: form.phone, avatar: form.avatar })
    userStore.userInfo = {
      ...userStore.userInfo,
      nickname: form.nickname,
      phone: form.phone,
      avatar: form.avatar,
    }
    localStorage.setItem('userInfo', JSON.stringify(userStore.userInfo))
    ElMessage.success('修改成功')
  } finally {
    saving.value = false
  }
}

</script>

<style scoped>
.profile-page { }
.profile-card {
  border-radius: 14px;
  border: none;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  margin-bottom: 16px;
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
  background: linear-gradient(135deg, #667eea, #764ba2);
  display: inline-block;
}
.avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 4px;
}
.avatar-preview {
  flex-shrink: 0;
  box-shadow: 0 4px 14px rgba(0,0,0,0.1);
}
.avatar-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.profile-form {
  max-width: 460px;
}
</style>
