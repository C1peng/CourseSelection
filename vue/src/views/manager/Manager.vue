<template>
  <div class="manager-container">
    <!-- 顶部导航栏 -->
    <header class="top-navbar">
      <div class="navbar-left">
        <button class="sidebar-toggle-btn" :class="{ active: data.sidebarCollapsed }" @click="toggleSidebar">
          <el-icon>
            <Menu />
          </el-icon>
        </button>
        <div class="logo-container">
          <img src="@/assets/imgs/logo.png" alt="系统Logo" class="logo" />
          <span class="system-title">管理系统</span>
        </div>
      </div>

      <!-- 标签页导航栏 -->
      <div class="navbar-center">
        <div class="tabs-container">
          <div class="tabs-scroll-wrapper">
            <el-tabs 
              v-model="data.activeTab" 
              type="card" 
              :closable="data.tabs.length > 1" 
              @tab-click="handleTabClick"
              @tab-remove="handleTabRemove" 
              class="main-tabs">
              <el-tab-pane 
                v-for="tab in data.tabs" 
                :key="tab.path" 
                :label="getTabLabel(tab)" 
                :name="tab.path">
              </el-tab-pane>
            </el-tabs>
          </div>
          
          <!-- 右侧操作按钮区域 -->
          <div class="tabs-actions">
            <el-dropdown trigger="click" placement="bottom-end">
              <div class="more-actions-btn">
                <el-icon><More /></el-icon>
              </div>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item @click="closeOtherTabs" :disabled="data.tabs.length <= 1">
                    <el-icon><CircleClose /></el-icon>关闭其他标签
                  </el-dropdown-item>
                  <el-dropdown-item @click="closeAllTabs" :disabled="data.tabs.length <= 1">
                    <el-icon><CircleClose /></el-icon>关闭全部标签
                  </el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </div>
      </div>

      <div class="navbar-right">
        <!-- 通知图标 -->
        <div class="nav-item notification-btn">
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
                <div v-if="data.notifications.length === 0" class="empty-notifications">
                  暂无新通知
                </div>
                <div v-else class="notification-item" v-for="item in data.notifications" :key="item.id">
                  <div class="notification-content">{{ item.content }}</div>
                  <div class="notification-time">{{ formatTime(item.time) }}</div>
                </div>
              </div>
            </div>
          </el-popover>
        </div>

        <!-- 用户头像和下拉菜单 -->
        <div class="user-profile">
          <el-dropdown trigger="click" @command="handleUserMenuCommand">
            <div class="user-info">
              <img :src="data.user.avatar || defaultAvatar" alt="用户头像" class="user-avatar" />
              <span class="user-name">{{ data.user.username || '未登录' }}</span>
              <el-icon class="dropdown-arrow">
                <ArrowDown />
              </el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <el-icon>
                    <User />
                  </el-icon>
                  <span>个人资料</span>
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon>
                    <SwitchButton />
                  </el-icon>
                  <span>退出登录</span>
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </div>
    </header>

    <!-- 主体内容区域 -->
    <div class="main-content">
      <!-- 侧边栏导航 -->
      <aside class="sidebar" :class="{ collapsed: data.sidebarCollapsed }"
        :style="{ width: data.sidebarCollapsed ? data.sidebarWidthCollapsed + 'px' : data.sidebarWidth + 'px' }">
        <div class="sidebar-content">
          <!-- 主菜单 -->
          <el-menu 
            :default-active="currentRoutePath" 
            :collapse="data.sidebarCollapsed" 
            :collapse-transition="true"
            router 
            class="main-menu" 
            @select="handleMenuSelect"
            :class="{ 'sidebar-collapsed': data.sidebarCollapsed }"
          >
            <!-- 系统首页 -->
            <el-menu-item index="/manager/home" class="menu-item">
              <el-icon class="menu-icon"><HomeFilled /></el-icon>
              <span>系统首页</span>
            </el-menu-item>
            
            <!-- 分隔线 -->
            <div class="menu-divider" v-if="!data.sidebarCollapsed"></div>
            
            <!-- 信息管理 -->
            <el-sub-menu index="1" class="menu-sub">
              <template #title>
                <el-icon class="menu-icon"><Document /></el-icon>
                <span>信息模块</span>
              </template>
              <el-menu-item index="/manager/notice" class="menu-item">
                <span>系统公告</span>
              </el-menu-item>
              <el-menu-item index="/manager/notification" class="menu-item">
                <span>通知管理</span>
              </el-menu-item>
            </el-sub-menu>
            
            <!-- 管理模块 -->
            <!-- <el-sub-menu index="2" class="menu-sub">
              <template #title>
                <el-icon class="menu-icon"><User /></el-icon>
                <span>管理模块</span>
              </template>
              
            </el-sub-menu> -->
            
            <!-- 学院管理 -->
            <el-sub-menu index="3" class="menu-sub">
              <template #title>
                <el-icon class="menu-icon"><OfficeBuilding /></el-icon>
                <span>学院模块</span>
              </template>
              <el-menu-item index="/manager/dept" class="menu-item">
                <span>学院信息</span>
              </el-menu-item>
              <el-menu-item index="/manager/creditline" class="menu-item">
                <span>学分信息</span>
              </el-menu-item>
            </el-sub-menu>
            
            <!-- 用户管理 -->
            <el-sub-menu index="4" class="menu-sub">
              <template #title>
                <el-icon class="menu-icon"><User /></el-icon>
                <span>用户模块</span>
              </template>
              <el-menu-item index="/manager/admin" class="menu-item">
                <span>管理员信息</span>
              </el-menu-item>
              <el-menu-item index="/manager/teacher" class="menu-item">
                <span>教师信息</span>
              </el-menu-item>
              <el-menu-item index="/manager/student" class="menu-item">
                <span>学生信息</span>
              </el-menu-item>
            </el-sub-menu>
            
            <!-- 课程管理 -->
            <el-sub-menu index="5" class="menu-sub">
              <template #title>
                <el-icon class="menu-icon"><Reading /></el-icon>
                <span>课程模块</span>
              </template>
              <el-menu-item index="/manager/course" class="menu-item">
                <span>课程信息</span>
              </el-menu-item>
              <el-menu-item index="/manager/cselection" class="menu-item">
                <span>选课信息</span>
              </el-menu-item>
              <el-menu-item index="/manager/studentscore" class="menu-item">
                <span>学生分数管理</span>
              </el-menu-item>
              <el-menu-item index="/manager/timerange" class="menu-item">
                <span>时间范围设置</span>
              </el-menu-item>
            </el-sub-menu>
            
            <!-- 权限管理 -->
            <el-sub-menu index="6" class="menu-sub">
              <template #title>
                <el-icon class="menu-icon"><Lock /></el-icon>
                <span>权限模块</span>
              </template>
              <el-menu-item index="/manager/permission" class="menu-item">
                <span>权限管理</span>
              </el-menu-item>
              <el-menu-item index="/manager/role" class="menu-item">
                <span>角色管理</span>
              </el-menu-item>
              <el-menu-item index="/manager/user-role" class="menu-item">
                <span>角色分配管理</span>
              </el-menu-item>
            </el-sub-menu>
          </el-menu>
        </div>
      </aside>

      <!-- 内容展示区域 -->
      <main class="content-wrapper">
        <!-- 移除复杂的Transition嵌套结构，使用简单方式 -->
        <RouterView @updateUser="updateUser" />
      </main>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed, onMounted, onUnmounted, watch, ref } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage, ElMessageBox } from 'element-plus';
import request from '@/utils/request.js';
import {
  Menu, Bell, ArrowDown, User, Lock, SwitchButton,
  HomeFilled, Document, OfficeBuilding, Reading,
  More, CircleClose
} from '@element-plus/icons-vue';

// 页面路由映射配置
const routeMap = {
  '/manager/home': '系统首页',
  '/manager/notice': '系统公告',
  '/manager/dept': '学院信息',
  '/manager/creditline': '学分信息',
  '/manager/admin': '管理员信息',
  '/manager/teacher': '教师信息',
  '/manager/student': '学生信息',
  '/manager/course': '课程信息',
  '/manager/cselection': '选课信息',
  '/manager/studentscore': '学生分数管理',
  '/manager/timerange': '时间范围设置',
  '/manager/permission': '权限管理',
  '/manager/role': '角色管理',
  '/manager/user-role': '角色分配管理',
  '/manager/person': '个人资料'
};

// 响应式数据
const data = reactive({
    user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
    sidebarCollapsed: false,
    sidebarWidth: 240,
    sidebarWidthCollapsed: 72,
    notifications: [],
    // 添加标签页相关数据
    tabs: [
      { path: '/manager/home', title: '系统首页' }
    ],
    activeTab: '/manager/home'
  });

// 路由相关
const router = useRouter();
const route = useRoute();

// 计算属性
const currentRoutePath = computed(() => route.path);
const notificationCount = ref(0);
const defaultAvatar = '/src/assets/imgs/default-avatar.png';

// 获取通知列表
const loadNotifications = async () => {
  try {
    const res = await request.get('/notification/list');
    if (res.code === '200' && res.data) {
      data.notifications = res.data.map(item => ({
        id: item.id,
        content: item.content,
        time: new Date(item.createTime).getTime()
      }));
    }
  } catch (error) {
    console.error('获取通知列表失败:', error);
  }
};

// 获取未读通知数量
const loadUnreadCount = async () => {
  try {
    const res = await request.get('/notification/unread/count');
    if (res.code === '200') {
      notificationCount.value = res.data || 0;
    }
  } catch (error) {
    console.error('获取未读通知数量失败:', error);
  }
};

// 标记所有通知为已读
const markAllAsRead = async () => {
  try {
    const res = await request.put('/notification/read/all');
    if (res.code === '200') {
      await loadNotifications();
      await loadUnreadCount();
      ElMessage.success('已全部标记为已读');
    }
  } catch (error) {
    console.error('标记已读失败:', error);
  }
};

// 获取标签页显示名称（支持动态标题）
const getTabLabel = (tab) => {
  return tab.customTitle || routeMap[tab.path] || tab.title;
};

// 侧边栏切换
const toggleSidebar = () => {
  data.sidebarCollapsed = !data.sidebarCollapsed;
  // 保存状态到本地存储
  localStorage.setItem('sidebar-collapsed', data.sidebarCollapsed.toString());
};

// 格式化时间
const formatTime = (timestamp) => {
  const now = new Date().getTime();
  const diff = now - timestamp;

  if (diff < 60000) {
    return '刚刚';
  } else if (diff < 3600000) {
    return Math.floor(diff / 60000) + '分钟前';
  } else if (diff < 86400000) {
    return Math.floor(diff / 3600000) + '小时前';
  } else {
    return Math.floor(diff / 86400000) + '天前';
  }
};

// 处理用户菜单命令
const handleUserMenuCommand = (command) => {
  switch (command) {
    case 'profile':
      openTab('/manager/person', '个人资料');
      break;
    case 'password':
      openTab('/manager/password', '修改密码');
      break;
    case 'logout':
      confirmLogout();
      break;
  }
};

// 退出登录确认
const confirmLogout = () => {
  ElMessageBox.confirm(
    '确定要退出登录吗？',
    '退出确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    logout();
  }).catch(() => {
    // 取消退出
  });
};

// 退出登录
const logout = () => {
  localStorage.removeItem('xm-user');
  router.push('/adminlogin');
  ElMessage.success('已成功退出登录');
};

// 更新用户信息
const updateUser = () => {
  data.user = JSON.parse(localStorage.getItem('xm-user') || '{}');
};

// 监听窗口大小变化，在小屏幕上自动折叠侧边栏
const handleResize = () => {
  if (window.innerWidth < 768) {
    data.sidebarCollapsed = true;
  }
};

// 添加新标签页
const openTab = (path, title) => {
  // 检查标签是否已存在
  const existingTab = data.tabs.find(tab => tab.path === path);
  if (!existingTab) {
    data.tabs.push({ path, title });
  }
  // 激活标签页
  data.activeTab = path;
  // 导航到对应路由
  router.push(path);
};

// 处理标签页点击
const handleTabClick = (tab) => {
  router.push(tab.paneName);
};

// 处理标签页关闭
const handleTabRemove = (path) => {
  // 不允许关闭首页
  if (path === '/manager/home') {
    ElMessage.warning('首页标签页不可关闭');
    return;
  }

  // 移除标签
  const index = data.tabs.findIndex(tab => tab.path === path);
  if (index > -1) {
    data.tabs.splice(index, 1);
  }

  // 如果关闭的是当前激活的标签，切换到上一个标签
  if (path === data.activeTab) {
    const newActiveIndex = index > 0 ? index - 1 : 0;
    data.activeTab = data.tabs[newActiveIndex]?.path || '/manager/home';
    router.push(data.activeTab);
  }
};

// 处理菜单选择
const handleMenuSelect = (key) => {
  const title = routeMap[key];
  if (title) {
    openTab(key, title);
  }
};

// 监听路由变化，更新当前激活标签
watch(() => route.path, (newPath) => {
  const title = routeMap[newPath];
  if (title) {
    // 检查标签是否已存在
    const existingTab = data.tabs.find(tab => tab.path === newPath);
    if (!existingTab) {
      data.tabs.push({ path: newPath, title });
    }
    data.activeTab = newPath;
  }
}, { immediate: true });

// 生命周期钩子
onMounted(async () => {
  // 从本地存储恢复侧边栏状态
  const savedState = localStorage.getItem('sidebar-collapsed');
  if (savedState !== null) {
    data.sidebarCollapsed = savedState === 'true';
  }

  // 监听窗口大小变化
  window.addEventListener('resize', handleResize);
  handleResize(); // 初始检查

  // 检查登录状态
  if (!data.user.id) {
    logout();
  }

  // 加载通知
  await loadNotifications();
  await loadUnreadCount();

  // 初始化当前路由标签
  const currentPath = route.path;
  const currentTitle = routeMap[currentPath];
  if (currentTitle && currentPath !== '/manager/home') {
    openTab(currentPath, currentTitle);
  }
});

onUnmounted(() => {
  window.removeEventListener('resize', handleResize);
});

// 关闭其他标签页
const closeOtherTabs = () => {
  const currentTab = data.tabs.find(tab => tab.path === data.activeTab);
  
  if (!currentTab || currentTab.path === '/manager/home') {
    // 如果当前是首页，不执行操作
    ElMessage.info('首页不能被关闭');
    return;
  }
  
  ElMessageBox.confirm(
    '确定要关闭其他标签页吗？',
    '关闭确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    data.tabs = data.tabs.filter(tab => tab.path === '/manager/home' || tab.path === data.activeTab);
    ElMessage.success('已关闭其他标签页');
  });
};

// 关闭全部标签页（除了首页）
const closeAllTabs = () => {
  if (data.tabs.length <= 1) return;

  ElMessageBox.confirm(
    '确定要关闭全部标签页吗？(首页标签页将被保留)',
    '关闭标签确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    // 只保留首页标签
    data.tabs = [{ path: '/manager/home', title: '系统首页' }];
    // 切换到首页
    data.activeTab = '/manager/home';
    router.push('/manager/home');
    ElMessage.success('已关闭全部标签页');
  }).catch(() => {
    // 取消关闭
  });
};
</script>


<style scoped>
/* 导入 CSS 变量（必须在最前面） */
@import "@/assets/css/variables.css";
/* 导入公共布局样式 */
@import "@/assets/css/layouts/header.css";
@import "@/assets/css/layouts/sidebar.css";

/* Manager 特定样式 */
.manager-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.top-navbar {
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

.navbar-left {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  flex-shrink: 0;
  padding-left: var(--space-lg);
}

.sidebar-toggle-btn {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  border: none;
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-fast);
}

.sidebar-toggle-btn:hover {
  background: var(--bg-gray);
}

.sidebar-toggle-btn.active {
  background: rgba(5, 150, 105, 0.1);
  color: var(--manager-primary);
}

.logo-container {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.logo {
  width: 36px;
  height: 36px;
  border-radius: var(--radius-sm);
}

.system-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.navbar-center {
  flex: 1;
  padding: 0 var(--space-lg);
  min-width: 0;
  overflow: hidden;
}

.tabs-container {
  display: flex;
  align-items: center;
  background: var(--bg-gray);
  border-radius: var(--radius-md);
  padding: var(--space-xs);
}

.tabs-scroll-wrapper {
  flex: 1;
  overflow-x: auto;
  /* 自定义滚动条样式 */
  scrollbar-width: thin;
  scrollbar-color: var(--manager-primary-light) transparent;
}

/* Webkit 浏览器滚动条样式 */
.tabs-scroll-wrapper::-webkit-scrollbar {
  height: 4px;
}

.tabs-scroll-wrapper::-webkit-scrollbar-track {
  background: transparent;
}

.tabs-scroll-wrapper::-webkit-scrollbar-thumb {
  background: var(--manager-primary-light);
  border-radius: var(--radius-xs);
}

.tabs-scroll-wrapper::-webkit-scrollbar-thumb:hover {
  background: var(--manager-primary);
}

.tabs-scroll-wrapper :deep(.el-tabs__header) {
  margin: 0;
  border: none;
}

.tabs-scroll-wrapper :deep(.el-tabs__nav) {
  border: none;
}

.tabs-scroll-wrapper :deep(.el-tabs__item) {
  border: none;
  border-radius: var(--radius-sm);
  margin-right: var(--space-xs);
  transition: all var(--transition-fast);
  color: var(--text-secondary);
  font-weight: var(--font-weight-medium);
  position: relative;
}

.tabs-scroll-wrapper :deep(.el-tabs__item:hover) {
  color: var(--text-primary);
  background: rgba(5, 150, 105, 0.05);
}

.tabs-scroll-wrapper :deep(.el-tabs__item.is-active) {
  background: var(--bg-white);
  box-shadow: var(--shadow-sm);
  color: var(--manager-primary);
  font-weight: var(--font-weight-semibold);
}

/* 选中标签底部绿色高亮条 */
.tabs-scroll-wrapper :deep(.el-tabs__item.is-active::after) {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--manager-primary);
  border-radius: var(--radius-xs) var(--radius-xs) 0 0;
}

.tabs-actions {
  padding-left: var(--space-sm);
}

.more-actions-btn {
  width: 32px;
  height: 32px;
  border-radius: var(--radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all var(--transition-fast);
}

.more-actions-btn:hover {
  background: var(--bg-white);
}

.navbar-right {
  padding-right: var(--space-xl);
  display: flex;
  align-items: center;
  gap: var(--space-md);
  flex-shrink: 0;
}

.notification-btn {
  position: relative;
  cursor: pointer;
  padding: var(--space-sm);
  border-radius: var(--radius-md);
  transition: background var(--transition-fast);
}

.notification-btn:hover {
  background: var(--bg-gray);
}

.nav-icon {
  font-size: 20px;
  color: var(--text-secondary);
}

.user-profile {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  cursor: pointer;
  transition: all var(--transition-normal);
  background: rgba(5, 150, 105, 0.05);
}

.user-info:hover {
  background: rgba(5, 150, 105, 0.1);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(5, 150, 105, 0.2);
}

.user-avatar {
  width: 42px;
  height: 42px;
  border-radius: 50%;
  border: 2px solid var(--bg-white);
  box-shadow: var(--shadow-sm);
  object-fit: cover;
  transition: all var(--transition-normal);
}

.user-info:hover .user-avatar {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(5, 150, 105, 0.3);
}

.user-name {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  line-height: 1.2;
}

.dropdown-arrow {
  font-size: var(--font-size-sm);
  color: var(--text-muted);
  transition: transform var(--transition-normal);
}

.user-info:hover .dropdown-arrow {
  transform: translateY(2px);
}

.notification-panel {
  min-width: 320px;
}

.notification-panel .panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-md);
  border-bottom: 1px solid var(--border-color);
}

.notification-panel .panel-header h3 {
  margin: 0;
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
}

.notification-list {
  max-height: 300px;
  overflow-y: auto;
}

.empty-notifications {
  padding: var(--space-xl);
  text-align: center;
  color: var(--text-muted);
}

.notification-item {
  padding: var(--space-md);
  border-bottom: 1px solid var(--border-color-light);
}

.notification-item:last-child {
  border-bottom: none;
}

.notification-content {
  font-size: var(--font-size-sm);
  color: var(--text-primary);
  margin-bottom: var(--space-xs);
}

.notification-time {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
}

.main-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.sidebar {
  background: var(--bg-white);
  border-right: 1px solid var(--border-color);
  transition: width var(--transition-normal);
  overflow-x: hidden;
  overflow-y: auto;
}

.sidebar.collapsed {
  width: 72px;
}

.sidebar.collapsed .main-menu :deep(.el-menu-item),
.sidebar.collapsed .main-menu :deep(.el-sub-menu__title) {
  padding: 0 20px !important;
}

.sidebar.collapsed .main-menu :deep(.el-sub-menu__title) {
  display: flex;
  justify-content: center;
}

.sidebar.collapsed .menu-icon {
  margin-right: 0;
}

.main-menu {
  border-right: none !important;
  background: transparent !important;
}

.main-menu :deep(.el-menu-item),
.main-menu :deep(.el-sub-menu__title) {
  height: 48px;
  line-height: 48px;
  margin: var(--space-xs) var(--space-md);
  border-radius: var(--radius-sm);
  color: var(--text-secondary);
  transition: all var(--transition-fast);
}

.main-menu :deep(.el-menu-item:hover),
.main-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(5, 150, 105, 0.08);
  color: var(--manager-primary);
}

.main-menu :deep(.el-menu-item.is-active) {
  background: rgba(5, 150, 105, 0.15);
  color: var(--manager-primary);
  font-weight: var(--font-weight-semibold);
}

.main-menu :deep(.el-sub-menu .el-menu-item) {
  height: 40px;
  line-height: 40px;
  padding-left: 52px !important;
  margin: 2px var(--space-md);
  font-size: var(--font-size-sm);
}

.menu-icon {
  font-size: 18px;
  margin-right: var(--space-sm);
}

.menu-divider {
  height: 1px;
  background: var(--border-color);
  margin: var(--space-md) var(--space-lg);
}

.content-wrapper {
  flex: 1;
  padding: var(--space-lg);
  overflow-y: auto;
  background: var(--manager-bg);
}

/* 响应式 */
@media (max-width: 768px) {
  .system-title {
    display: none;
  }
  
  .tabs-container {
    display: none;
  }
  
  .user-name {
    display: none;
  }
}
</style>
