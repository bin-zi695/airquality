import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { authApi } from '@/api'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userInfo = ref(JSON.parse(localStorage.getItem('userInfo') || 'null'))

  const isLoggedIn = computed(() => !!token.value)
  const isAdmin = computed(() => userInfo.value?.role === 'admin')
  const userId = computed(() => userInfo.value?.id)

  async function login(loginData) {
    const res = await authApi.login(loginData)
    token.value = res.data.token
    userInfo.value = {
      id: res.data.id,
      username: res.data.username,
      email: res.data.email,
      nickname: res.data.nickname,
      role: res.data.role,
    }
    localStorage.setItem('token', token.value)
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }

  async function register(data) {
    await authApi.register(data)
  }

  function logout() {
    token.value = ''
    userInfo.value = null
    localStorage.clear()
  }

  return { token, userInfo, isLoggedIn, isAdmin, userId, login, register, logout }
})
