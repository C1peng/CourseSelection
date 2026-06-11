<template>
  <div class="person-container">
    <el-card class="person-card">
      <template #header>
        <div class="card-header">
          <el-icon class="header-icon"><User /></el-icon>
          <span class="header-title">个人中心</span>
        </div>
      </template>
      <div class="person-content">
        <div class="avatar-section">
          <div class="avatar-wrapper">
            <el-upload
              :action="baseUrl + '/files/upload'"
              :on-success="handleFileUpload"
              :show-file-list="false"
              class="avatar-uploader"
            >
              <img v-if="data.user.avatar" :src="data.user.avatar" class="avatar" />
              <div v-else class="avatar-placeholder">
                <el-icon class="avatar-icon"><UserFilled /></el-icon>
                <span class="avatar-text">点击上传头像</span>
              </div>
              <div class="avatar-overlay">
                <el-icon><Camera /></el-icon>
                <span>更换头像</span>
              </div>
            </el-upload>
          </div>
          <div class="user-info-header">
            <h2 class="user-name">{{ data.user.name || '未设置' }}</h2>
            <p class="user-role">
              <el-tag :type="getRoleTagType(data.user.role)" size="large">
                {{ getRoleLabel(data.user.role) }}
              </el-tag>
            </p>
          </div>
        </div>
        
        <el-divider />
        
        <el-form ref="user" :model="data.user" label-width="100px" class="person-form">
          <el-form-item prop="username" label="用户名">
            <el-input 
              disabled 
              v-model="data.user.username" 
              placeholder="请输入用户名"
              size="large"
            >
              <template #prefix>
                <el-icon><User /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="name" label="姓名">
            <el-input 
              v-model="data.user.name" 
              placeholder="请输入姓名"
              size="large"
            >
              <template #prefix>
                <el-icon><Edit /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="phone" label="电话">
            <el-input 
              v-model="data.user.phone" 
              placeholder="请输入电话"
              size="large"
            >
              <template #prefix>
                <el-icon><Phone /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item prop="email" label="邮箱">
            <el-input 
              v-model="data.user.email" 
              placeholder="请输入邮箱"
              size="large"
            >
              <template #prefix>
                <el-icon><Message /></el-icon>
              </template>
            </el-input>
          </el-form-item>
          <el-form-item>
            <div class="form-actions">
              <el-button type="primary" size="large" @click="update">
                <el-icon><Check /></el-icon>
                保存修改
              </el-button>
              <el-button size="large" @click="resetForm">
                <el-icon><RefreshLeft /></el-icon>
                重置
              </el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import { 
  User, UserFilled, Camera, Edit, Phone, Message, 
  Check, RefreshLeft 
} from '@element-plus/icons-vue';

const baseUrl = import.meta.env.VITE_BASE_URL

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}')
})

const originalUser = ref(JSON.parse(JSON.stringify(data.user)))

const handleFileUpload = (res) => {
  data.user.avatar = res.data
  ElMessage.success('头像上传成功')
}

const getRoleLabel = (role) => {
  const roleMap = {
    'STUDENT': '学生',
    'TEACHER': '教师',
    'ADMIN': '管理员'
  }
  return roleMap[role] || '未知'
}

const getRoleTagType = (role) => {
  const typeMap = {
    'STUDENT': 'primary',
    'TEACHER': 'success',
    'ADMIN': 'warning'
  }
  return typeMap[role] || 'info'
}

const resetForm = () => {
  data.user = JSON.parse(JSON.stringify(originalUser.value))
  ElMessage.info('已重置为原始数据')
}

const emit = defineEmits(['updateUser'])
const update = () => {
  if (data.user.role === 'ADMIN') {
    request.put('/admin/update', data.user).then(res => {
      if (res.code === '200') {
        ElMessage.success('保存成功')
        localStorage.setItem('xm-user', JSON.stringify(data.user))
        originalUser.value = JSON.parse(JSON.stringify(data.user))
        emit('updateUser')
      } else {
        ElMessage.error(res.msg)
      }
    })
  } else if (data.user.role === 'STUDENT') {
    request.put('/student/update', data.user).then(res => {
      if (res.code === '200') {
        ElMessage.success('保存成功')
        localStorage.setItem('xm-user', JSON.stringify(data.user))
        originalUser.value = JSON.parse(JSON.stringify(data.user))
        emit('updateUser')
      } else {
        ElMessage.error(res.msg)
      }
    })
  } else if (data.user.role === 'TEACHER') {
    request.put('/teacher/update', data.user).then(res => {
      if (res.code === '200') {
        ElMessage.success('保存成功')
        localStorage.setItem('xm-user', JSON.stringify(data.user))
        originalUser.value = JSON.parse(JSON.stringify(data.user))
        emit('updateUser')
      } else {
        ElMessage.error(res.msg)
      }
    })
  }
}
</script>

<style scoped>
.person-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 0;
}

.person-card {
  border-radius: 20px;
  border: 1px solid #e8eaed;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.header-icon {
  font-size: 24px;
}

.header-title {
  font-size: 20px;
  font-weight: 600;
}

.person-content {
  padding: 40px;
}

.avatar-section {
  text-align: center;
  margin-bottom: 32px;
}

.avatar-wrapper {
  display: inline-block;
  position: relative;
  margin-bottom: 20px;
}

.avatar-uploader {
  position: relative;
  display: inline-block;
}

.avatar-uploader .el-upload {
  border: 3px dashed #e8eaed;
  border-radius: 50%;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
}

.avatar-uploader .el-upload:hover {
  border-color: #667eea;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
  transform: scale(1.05);
}

.avatar {
  width: 160px;
  height: 160px;
  display: block;
  border-radius: 50%;
  object-fit: cover;
}

.avatar-placeholder {
  width: 160px;
  height: 160px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border-radius: 50%;
}

.avatar-icon {
  font-size: 64px;
  color: #adb5bd;
  margin-bottom: 8px;
}

.avatar-text {
  font-size: 14px;
  color: #6c757d;
  font-weight: 500;
}

.avatar-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(102, 126, 234, 0.9);
  color: #fff;
  padding: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 0 0 50% 50%;
}

.avatar-uploader:hover .avatar-overlay {
  opacity: 1;
}

.user-info-header {
  margin-top: 20px;
}

.user-name {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 12px 0;
}

.user-role {
  margin: 0;
}

.person-form {
  max-width: 600px;
  margin: 0 auto;
}

.person-form :deep(.el-form-item__label) {
  font-weight: 600;
  color: #606266;
  font-size: 15px;
}

.person-form :deep(.el-input__wrapper) {
  border-radius: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
}

.person-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.person-form :deep(.el-input.is-focus .el-input__wrapper) {
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 24px;
}

.form-actions .el-button {
  min-width: 140px;
  border-radius: 10px;
  font-weight: 500;
  font-size: 15px;
}

.form-actions .el-button--primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.form-actions .el-button--primary:hover {
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.4);
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .person-content {
    padding: 24px;
  }
  
  .avatar {
    width: 120px;
    height: 120px;
  }
  
  .avatar-placeholder {
    width: 120px;
    height: 120px;
  }
  
  .avatar-icon {
    font-size: 48px;
  }
  
  .user-name {
    font-size: 24px;
  }
  
  .person-form {
    max-width: 100%;
  }
  
  .form-actions {
    flex-direction: column;
  }
  
  .form-actions .el-button {
    width: 100%;
  }
}
</style>