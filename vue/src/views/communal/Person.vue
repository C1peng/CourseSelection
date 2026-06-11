<template>
  <div class="person-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>信息管理</h1>
    </div>

    <div class="profile-content">
      <!-- 左侧信息卡片 -->
      <div class="profile-sidebar">
        <div class="profile-card">
          <!-- 头像区域 -->
          <div class="avatar-section">
            <el-upload :action="baseUrl + '/files/upload'" :on-success="handleFileUpload" :show-file-list="false"
              :before-upload="beforeAvatarUpload" :headers="{token: getToken()}" class="avatar-uploader">
              <div class="avatar-wrapper">
                <img v-if="data.user.avatar" :src="data.user.avatar" class="avatar" />
                <div v-else class="avatar-placeholder">
                  <el-icon class="avatar-icon">
                    <User />
                  </el-icon>
                </div>
                <div class="avatar-edit-overlay">
                  <el-icon class="edit-icon">
                    <Edit />
                  </el-icon>
                  <span>更换头像</span>
                </div>
              </div>
            </el-upload>
            <h3 class="user-name">{{ data.user.name || '未设置姓名' }}</h3>
            <p class="user-role">{{ getUserRoleText(data.user.role) }}</p>
          </div>

          <!-- 快捷信息 -->
          <div class="quick-info">
            <div class="info-item">
              <span class="info-label">用户名</span>
              <span class="info-value">{{ data.user.username }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">角色</span>
              <span class="info-value">{{ getUserRoleText(data.user.role) }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="profile-main">
        <el-card class="form-card">
          <template #header>
            <div class="card-header">
              <span>个人资料编辑</span>
              <span class="header-tip">修改个人基本信息</span>
            </div>
          </template>

          <el-form ref="userForm" :model="data.user" label-width="100px" :rules="rules" class="profile-form">
            <!-- 账户信息组 -->
            <div class="form-section">
              <h3 class="section-title">账户信息</h3>
              <div class="form-row">
                <el-form-item prop="name" label="姓名" class="form-col">
                  <el-input disabled v-model="data.user.name" class="disabled-input" />
                </el-form-item>
                <el-form-item prop="username" label="用户名" class="form-col">
                  <el-input v-model="data.user.username" placeholder="请输入用户名" />
                </el-form-item>
              </div>
            </div>

            <!-- 联系方式组 -->
            <div class="form-section">
              <h3 class="section-title">联系方式</h3>
              <div class="form-row">
                <el-form-item prop="phone" label="手机号码" class="form-col">
                  <el-input v-model="data.user.phone" placeholder="请输入手机号码" />
                </el-form-item>
                <el-form-item prop="email" label="电子邮箱" class="form-col">
                  <el-input v-model="data.user.email" placeholder="请输入电子邮箱" />
                </el-form-item>
              </div>
            </div>

            <!-- 操作按钮 -->
            <div class="form-actions">
              <el-button @click="resetForm">取消</el-button>
              <el-button type="primary" :loading="isLoading" @click="update">保存修改</el-button>
            </div>
          </el-form>
        </el-card>

        <!-- 安全设置卡片 -->
        <el-card class="security-card">
          <template #header>
            <div class="card-header">
              <span>安全设置</span>
              <span class="header-tip">账户安全相关配置</span>
            </div>
          </template>
          <div class="security-options">
            <!-- 使用PasswordForm组件替换原来的修改密码按钮 -->
            <div class="security-item">
              <div class="security-info">
                <h4>修改密码</h4>
                <p>定期更换密码以保障账户安全</p>
              </div>
              <el-button type="primary" size="small" @click="showPasswordForm = !showPasswordForm">
                {{ showPasswordForm ? '收起' : '修改密码' }}
              </el-button>
            </div>

            <!-- 密码表单区域 -->
            <div v-if="showPasswordForm" class="password-form-section">
              <PasswordForm ref="passwordFormRef" :initial-data="data.user" />
              <div class="password-actions">
                <el-button @click="cancelPasswordChange">取消</el-button>
                <el-button type="primary" @click="updatePassword">确认修改</el-button>
              </div>
            </div>

            <!-- <div class="security-item">
              <div class="security-info">
                <h4>双因素认证</h4>
                <p>开启后登录需验证二次密码</p>
              </div>
              <el-switch v-model="data.user.twoFactorEnabled" active-text="已开启" inactive-text="已关闭" />
            </div> -->
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref, nextTick, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import request from '@/utils/request.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Edit, User } from '@element-plus/icons-vue'
import PasswordForm from './PasswordForm.vue'
import { getToken } from '@/utils/userStorage.js'

// 初始化路由
const router = useRouter()

// 基础配置和状态
const baseUrl = import.meta.env.VITE_BASE_URL
const userForm = ref(null)
const passwordFormRef = ref(null)
const isLoading = ref(false)
const showPasswordForm = ref(false)
const originalUserData = ref({})

// 表单验证规则
const rules = {
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' },
    { min: 2, max: 20, message: '姓名长度应在 2 到 20 个字符之间', trigger: 'blur' }
  ],
  phone: [
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  email: [
    {
      pattern: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
      message: '请输入正确的邮箱地址',
      trigger: 'blur'
    }
  ]
}

// 响应式数据
const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}')
})

// 获取用户信息
const fetchUserInfo = async () => {
  try {
    // 发起请求获取用户详细信息
    const response = await request.get('/info')
    
    if (response.code === '200') {
      // 更新响应式数据
      data.user.role = response.data.role
      data.user.name = response.data.name
      data.user.avatar = response.data.avatar
      data.user.phone = response.data.phone
      data.user.email = response.data.email
      
      // 同时更新本地存储
      localStorage.setItem('xm-user', JSON.stringify(data.user))
      
      // 更新初始数据副本
      originalUserData.value = JSON.parse(JSON.stringify(data.user))
    } else {
      ElMessage.error(response.msg || '获取用户信息失败')
    }
  } catch (error) {
    console.error('获取用户信息失败:', error)
    ElMessage.error('获取用户信息失败')
  }
}

// 组件挂载时获取用户信息
onMounted(() => {
  fetchUserInfo()
})

// 根据角色获取对应的中文文本
const getUserRoleText = (role) => {
  const roleMap = {
    'ADMIN': '管理员',
    'TEACHER': '教师',
    'STUDENT': '学生'
  }
  return roleMap[role] || '普通用户'
}

// 头像上传前校验
const beforeAvatarUpload = (file) => {
  const isPNGJPG = file.type === 'image/png' || file.type === 'image/jpeg'
  if (!isPNGJPG) {
    ElMessage.error('只能上传 PNG/JPG 格式的图片!')
    return false
  }
  const isLt2M = file.size / 1024 / 1024 < 2
  if (!isLt2M) {
    ElMessage.error('上传头像大小不能超过 2MB!')
    return false
  }
  return true
}

// 处理头像上传结果
const handleFileUpload = (res) => {
  if (res.code === '200') {
    data.user.avatar = res.data
    ElMessage.success('头像上传成功')
    // 更新本地存储
    localStorage.setItem('xm-user', JSON.stringify(data.user))
    
    // 直接更新到数据库
    if (data.user.role === 'ADMIN') {
      const updateData = { ...data.user }
      request.put('/admin/update', updateData).then(res => {
        if (res.code !== '200') {
          ElMessage.error('头像更新到数据库失败')
        }
      })
    }
  } else {
    ElMessage.error('头像上传失败')
  }
}

// 定义事件发射器
const emit = defineEmits(['updateUser'])

// 更新个人信息
const update = () => {
  userForm.value.validate((valid) => {
    if (valid) {
      if (data.user.role === 'ADMIN') {
        isLoading.value = true
        // 直接使用用户数据进行更新
        const updateData = { ...data.user }

        request.put('/admin/update', updateData).then(res => {
          if (res.code === '200') {
            ElMessage.success('保存成功')
            // 更新本地存储
            localStorage.setItem('xm-user', JSON.stringify(data.user))
            // 更新初始数据
            originalUserData.value = JSON.parse(JSON.stringify(data.user))
            emit('updateUser')

            // 添加表单提交成功的视觉反馈
            const formElement = userForm.value.$el
            formElement.style.opacity = '0.8'
            formElement.style.transform = 'scale(0.98)'

            nextTick(() => {
              setTimeout(() => {
                formElement.style.opacity = '1'
                formElement.style.transform = 'scale(1)'
                formElement.style.transition = 'all 0.3s ease'
              }, 100)
            })
          } else {
            ElMessage.error(res.msg || '保存失败')
          }
        }).finally(() => {
          isLoading.value = false
        })
      } else {
        ElMessage.warning('当前用户无权限修改信息')
      }
    }
  })
}

// 重置表单
const resetForm = () => {
  if (hasChanges()) {
    ElMessageBox.confirm('确定要放弃所有修改吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      // 先恢复原始数据到响应式对象
      Object.assign(data.user, originalUserData.value)
      // 然后重置表单验证状态
      userForm.value?.clearValidate()
    }).catch(() => { })
  } else {
    userForm.value?.clearValidate()
  }
}

// 检查是否有修改
const hasChanges = () => {
  return JSON.stringify(data.user) !== JSON.stringify(originalUserData.value)
}

// 取消密码修改
const cancelPasswordChange = () => {
  if (passwordFormRef.value) {
    passwordFormRef.value.resetForm()
  }
  showPasswordForm.value = false
}

// 更新密码
const updatePassword = async () => {
  if (passwordFormRef.value) {
    // 验证表单
    const isValid = await passwordFormRef.value.validateForm()
    if (isValid) {
      const submitData = passwordFormRef.value.getFormData()

      isLoading.value = true
      // 在提交时将表单数据合并到用户对象中
      const userData = {...submitData }
      request.put('/updatePassword', userData).then(res => {
        if (res.code === '200') {
          ElMessage.success('密码修改成功，请重新登录')
          showPasswordForm.value = false
          passwordFormRef.value.resetForm()
          logout() // 修改成功后退出登录
        } else {
          ElMessage.error(res.msg || '密码修改失败')
        }
      }).finally(() => {
        isLoading.value = false
      })
    }
  }
}

// 退出登录
const logout = () => {
  localStorage.removeItem('xm-user')
  router.push('/login')
}
</script>

<style scoped>
@import "@/assets/css/person.css";
</style>