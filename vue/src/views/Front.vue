<template>
  <div class="front-layout">
    <!-- 公告栏 -->
    <div class="front-notice">
      <el-icon class="notice-icon"><Bell /></el-icon>
      <span class="notice-text">公告：{{ data.top }}</span>
    </div>
    
    <!-- 导航栏 -->
    <div class="front-header">
      <div class="front-header-left">
        <div class="logo-container">
          <img src="@/assets/imgs/logo.png" alt="Logo" class="logo-img">
          <div class="title">
            {{ data.user.role === 'STUDENT' ? '学生中心' : data.user.role === 'TEACHER' ? '教师工作台' : '项目前台' }}
          </div>
        </div>
      </div>
      <div class="front-header-center">
        <el-menu 
          :default-active="router.currentRoute.value.path" 
          router 
          mode="horizontal"
          class="custom-menu"
        >
          <!-- 学生菜单 -->
          <template v-if="data.user.role === 'STUDENT'">
            <el-menu-item index="/student/home">
              <el-icon><HomeFilled /></el-icon>
              <span>课程首页</span>
            </el-menu-item>
            <el-menu-item index="/student/courses">
              <el-icon><Document /></el-icon>
              <span>我的课程</span>
            </el-menu-item>
            <el-menu-item index="/student/selection">
              <el-icon><Select /></el-icon>
              <span>已选课程</span>
            </el-menu-item>
            <el-menu-item index="/student/all-courses">
              <el-icon><Grid /></el-icon>
              <span>全部课程</span>
            </el-menu-item>
            <el-menu-item index="/student/person">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-menu-item>
          </template>
          
          <!-- 教师菜单 -->
          <template v-else-if="data.user.role === 'TEACHER'">
            <el-menu-item index="/teacher/dashboard">
              <el-icon><Grid /></el-icon>
              <span>工作台</span>
            </el-menu-item>
            <el-menu-item index="/teacher/courses">
              <el-icon><Notebook /></el-icon>
              <span>我的授课</span>
            </el-menu-item>
            <el-menu-item index="/teacher/students">
              <el-icon><Timer /></el-icon>
              <span>学分管理</span>
            </el-menu-item>
            <el-menu-item index="/teacher/all-courses">
              <el-icon><Grid /></el-icon>
              <span>开设课程</span>
            </el-menu-item>
            <el-menu-item index="/teacher/person">
              <el-icon><User /></el-icon>
              <span>个人中心</span>
            </el-menu-item>
          </template>
          
          <!-- 默认菜单 -->
          <template v-else>
            <el-menu-item index="/front/home">首页</el-menu-item>
            <el-menu-item index="/front/person">个人中心</el-menu-item>
          </template>
        </el-menu>
      </div>
      <div class="front-header-right">
        <!-- 通知图标 - 仅学生和教师显示 -->
        <div v-if="data.user.role === 'STUDENT' || data.user.role === 'TEACHER'" class="nav-item notification-btn">
          <el-popover placement="bottom" width="360" trigger="click">
            <template #reference>
              <el-badge :value="notificationCount" class="nav-icon" v-if="notificationCount > 0">
                <el-icon>
                  <Bell />
                </el-icon>
              </el-badge>
              <el-icon class="nav-icon" v-else>
                <Bell />
              </el-icon>
            </template>
            <div class="notification-panel">
              <div class="panel-header">
                <h3>系统通知</h3>
                <el-link type="primary" :underline="'never'" size="small" @click="markAllAsRead">全部标为已读</el-link>
              </div>
              <div class="notification-list">
                <div v-if="notifications.length === 0" class="empty-notifications">
                  暂无新通知
                </div>
                <div v-else class="notification-item" v-for="item in notifications" :key="item.id">
                  <div class="notification-content">{{ item.content }}</div>
                  <div class="notification-time">{{ formatTime(item.time) }}</div>
                </div>
              </div>
            </div>
          </el-popover>
        </div>
        <div v-if="!data.user.id" class="auth-buttons">
          <el-button type="primary" plain @click="router.push('/login')">登录</el-button>
          <el-button type="primary" @click="router.push('/register')">注册</el-button>
        </div>
        <div v-else class="user-info">
          <el-dropdown trigger="click" placement="bottom-end">
            <div class="user-dropdown-trigger">
              <div class="avatar-wrapper">
                <img 
                  :src="data.user.avatar || defaultAvatar" 
                  alt="Avatar" 
                  class="user-avatar"
                  @error="handleAvatarError"
                >
              </div>
              <div class="user-details">
                <span class="user-name">{{ data.user.name }}</span>
                <span class="user-role" :class="data.user.role.toLowerCase()">
                  {{ data.user.role === 'STUDENT' ? '学生' : data.user.role === 'TEACHER' ? '教师' : '' }}
                </span>
              </div>
              <el-icon class="dropdown-icon"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu class="user-dropdown-menu">
                <el-dropdown-item @click="goToPerson">
                  <el-icon><User /></el-icon>
                  <span>个人资料</span>
                </el-dropdown-item>
                <el-dropdown-item divided @click="logout">
                  <el-icon><SwitchButton /></el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </div>
    <div class="main-body">
      <RouterView @updateUser="updateUser" />
    </div>
    <AIChat v-if="data.user.role === 'STUDENT'" />
  </div>
</template>

<script setup>
  import router from "@/router/index.js";
  import { reactive, onMounted, onBeforeUnmount, ref } from "vue";
  import request from "@/utils/request.js";
  import { getUserInfo } from "@/utils/userStorage.js";
  import { ElMessage } from 'element-plus'
  import { 
    Bell, ArrowDown, HomeFilled, Document, Select, User, 
    Grid, Notebook, UserFilled, MoreFilled, SwitchButton 
  } from '@element-plus/icons-vue';
  import AIChat from './front/student/AIChat.vue'
  
  const defaultAvatar = 'data:image/svg+xml,%3Csvg xmlns=%22http://www.w3.org/2000/svg%22 viewBox=%220 0 24 24%22%3E%3Cpath fill=%22%23ccc%22 d=%22M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z%22/%3E%3C/svg%3E';
  
  const handleAvatarError = (event) => {
    event.target.src = defaultAvatar;
  };

  const data = reactive({
    user: getUserInfo() || {},
    top: '',
    noticeData: []
  })

  const notifications = ref([])
  const notificationCount = ref(0)

  const logout = () => {
    localStorage.removeItem('xm-user')
    router.push('/login')
  }

  const updateUser = () => {
    data.user = getUserInfo() || {}
  }

  const goToPerson = () => {
    if (data.user.role === 'STUDENT') {
      router.push('/student/person')
    } else if (data.user.role === 'TEACHER') {
      router.push('/teacher/person')
    } else {
      router.push('/front/person')
    }
  }

  const loadNotifications = async () => {
    try {
      const res = await request.get('/notification/list')
      if (res.code === '200' && res.data) {
        notifications.value = res.data.map(item => ({
          id: item.id,
          content: item.content,
          time: new Date(item.createTime).getTime()
        }))
      }
    } catch (error) {
      console.error('获取通知列表失败:', error)
    }
  }

  const loadUnreadCount = async () => {
    try {
      const res = await request.get('/notification/unread/count')
      if (res.code === '200') {
        notificationCount.value = res.data || 0
      }
    } catch (error) {
      console.error('获取未读通知数量失败:', error)
    }
  }

  const markAllAsRead = async () => {
    try {
      const res = await request.put('/notification/read/all')
      if (res.code === '200') {
        await loadNotifications()
        await loadUnreadCount()
        ElMessage.success('已全部标记为已读')
      }
    } catch (error) {
      console.error('标记已读失败:', error)
    }
  }

  const formatTime = (timestamp) => {
    const now = new Date().getTime()
    const diff = now - timestamp

    if (diff < 60000) {
      return '刚刚'
    } else if (diff < 3600000) {
      return Math.floor(diff / 60000) + '分钟前'
    } else if (diff < 86400000) {
      return Math.floor(diff / 3600000) + '小时前'
    } else {
      return Math.floor(diff / 86400000) + '天前'
    }
  }

  let noticeInterval = null
  
  const loadNotice = () => {
    request.get('/notice/selectAll').then(res => {
      if (res.code === '200' && res.data) {
        data.noticeData = res.data
        let i = 0
        if (data.noticeData && data.noticeData.length) {
          data.top = data.noticeData[0].content
          noticeInterval = setInterval(() => {
            data.top = data.noticeData[i].content
            i++
            if (i === data.noticeData.length) {
              i = 0
            }
          }, 2500)
        }
      }
    })
  }

  onMounted(() => {
    loadNotice()
    if (data.user.role === 'STUDENT' || data.user.role === 'TEACHER') {
      loadNotifications()
      loadUnreadCount()
    }
  })
  
  onBeforeUnmount(() => {
    if (noticeInterval) {
      clearInterval(noticeInterval)
    }
  })
</script>

<style scoped>
/* 导入 CSS 变量（必须在最前面） */
@import "@/assets/css/variables.css";
/* 导入公共布局样式 */
@import "@/assets/css/layouts/header.css";

.front-layout {
  background: var(--front-bg);
}

/* 公告栏样式 */
.front-notice {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  padding: 10px 30px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.15);
}

.notice-icon {
  font-size: 16px;
  animation: bell-ring 2s ease-in-out infinite;
}

@keyframes bell-ring {
  0%, 100% { transform: rotate(0deg); }
  10%, 30% { transform: rotate(-10deg); }
  20%, 40% { transform: rotate(10deg); }
  50% { transform: rotate(0deg); }
}

.notice-text {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 导航栏样式 - 继承自 header.css，仅添加特定覆盖 */
.front-header {
  display: flex;
  height: 70px;
  align-items: center;
  background: var(--bg-white);
  box-shadow: var(--shadow-sm);
  border-bottom: 1px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: var(--z-sticky);
  backdrop-filter: blur(10px);
}

.front-header-left {
  width: 280px;
  display: flex;
  align-items: center;
  padding-left: 30px;
}

.logo-container {
  display: flex;
  align-items: center;
  gap: 12px;
}

.logo-img {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.logo-img:hover {
  transform: scale(1.05) rotate(5deg);
}

.title {
  color: #1a1a1a;
  cursor: pointer;
  font-size: 20px;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  transition: all 0.3s ease;
}

.title:hover {
  transform: translateX(2px);
}

.front-header-center {
  flex: 1;
  display: flex;
  justify-content: center;
}

.custom-menu {
  border-bottom: none !important;
  background: transparent !important;
  height: 70px;
}

.custom-menu :deep(.el-menu-item) {
  height: 70px;
  line-height: 70px;
  font-size: 15px;
  font-weight: 500;
  color: #606266;
  padding: 0 24px;
  margin: 0 4px;
  border-radius: 8px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.custom-menu :deep(.el-menu-item:hover) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
  transform: translateY(-2px);
}

.custom-menu :deep(.el-menu-item.is-active) {
  color: #667eea;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15) 0%, rgba(118, 75, 162, 0.15) 100%);
  font-weight: 600;
  position: relative;
}

.custom-menu :deep(.el-menu-item.is-active::after) {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 60%;
  height: 3px;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  border-radius: 2px 2px 0 0;
}

.custom-menu :deep(.el-sub-menu) {
  height: 70px;
}

.custom-menu :deep(.el-sub-menu__title) {
  height: 70px;
  line-height: 70px;
  font-size: 15px;
  font-weight: 500;
  color: #606266;
  padding: 0 24px;
  margin: 0 4px;
  border-radius: 8px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 6px;
}

.custom-menu :deep(.el-sub-menu__title:hover) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
}

.front-header-right {
  padding-right: 30px;
  display: flex;
  align-items: center;
}

.auth-buttons {
  display: flex;
  gap: 12px;
}

.user-info {
  display: flex;
  align-items: center;
}

.user-dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 12px;
  border-radius: 24px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.05) 0%, rgba(118, 75, 162, 0.05) 100%);
}

.user-dropdown-trigger:hover {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.avatar-wrapper {
  position: relative;
}

.user-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 2px solid #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  object-fit: cover;
  transition: all 0.3s ease;
}

.user-dropdown-trigger:hover .user-avatar {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.user-details {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  line-height: 1.2;
}

.user-role {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
  line-height: 1.2;
}

.user-role.student {
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  color: #1890ff;
}

.user-role.teacher {
  background: linear-gradient(135deg, #f6ffed 0%, #d9f7be 100%);
  color: #52c41a;
}

.dropdown-icon {
  font-size: 14px;
  color: #909399;
  transition: transform 0.3s ease;
}

.user-dropdown-trigger:hover .dropdown-icon {
  transform: translateY(2px);
}

.user-dropdown-menu :deep(.el-dropdown-menu__item) {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  font-size: 14px;
  transition: all 0.3s ease;
}

.user-dropdown-menu :deep(.el-dropdown-menu__item:hover) {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  color: #667eea;
}

.main-body {
  padding: var(--space-xl);
  background: var(--front-bg);
  position: relative;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .front-header-left {
    width: 200px;
    padding-left: 20px;
  }
  
  .title {
    font-size: 18px;
  }
  
  .custom-menu :deep(.el-menu-item) {
    padding: 0 16px;
    font-size: 14px;
  }
  
  .main-body {
    padding: 20px;
  }
}

@media (max-width: 768px) {
  .front-header {
    flex-wrap: wrap;
    height: auto;
    min-height: 70px;
  }
  
  .front-header-left {
    width: 100%;
    padding: 10px 20px;
  }
  
  .front-header-center {
    width: 100%;
    order: 3;
  }
  
  .custom-menu {
    height: auto;
  }
  
  .custom-menu :deep(.el-menu-item) {
    height: 50px;
    line-height: 50px;
    padding: 0 12px;
    font-size: 13px;
  }
  
  .front-header-right {
    padding: 10px 20px;
  }
  
  .user-dropdown-trigger {
    padding: 4px 8px;
  }
  
  .user-name {
    display: none;
  }
  
  .main-body {
    padding: 15px;
  }
}

/* 通知面板样式 */
.notification-btn {
  margin-right: 15px;
}

.notification-panel {
  max-height: 400px;
  overflow-y: auto;
}

.notification-panel .panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
  margin-bottom: 10px;
}

.notification-panel .panel-header h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

.notification-panel .notification-list {
  max-height: 300px;
  overflow-y: auto;
}

.notification-panel .empty-notifications {
  text-align: center;
  padding: 30px 0;
  color: #999;
  font-size: 14px;
}

.notification-panel .notification-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.notification-panel .notification-item:last-child {
  border-bottom: none;
}

.notification-panel .notification-content {
  font-size: 14px;
  color: #333;
  line-height: 1.5;
  white-space: pre-wrap;
  word-break: break-word;
}

.notification-panel .notification-time {
  font-size: 12px;
  color: #999;
  margin-top: 5px;
}

.nav-icon {
  font-size: 20px;
  cursor: pointer;
  color: #666;
  transition: color 0.3s;
}

.nav-icon:hover {
  color: #667eea;
}
</style>