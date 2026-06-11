import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

// 导入用户存储管理工具
import { isLoggedIn, getUserRole } from '@/utils/userStorage.js'

// 路由配置
const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 重定向路由
    { path: '/', redirect: '/login' },
    
    // 管理员路由
    {
      path: '/manager',
      redirect: '/manager/home',
      component: () => import('@/views/manager/Manager.vue'),
      children: [
        { path: 'home', meta: { name: '系统首页' }, component: () => import('@/views/manager/Home.vue') },
        { path: 'person', meta: { name: '个人资料' }, component: () => import('@/views/communal/Person.vue') },
        { path: 'notice', meta: { name: '系统公告' }, component: () => import('@/views/manager/Notice.vue') },
        { path: 'notification', meta: { name: '通知管理' }, component: () => import('@/views/manager/Notification.vue') },
        // 学院管理
        { path: 'dept', meta: { name: '学院信息' }, component: () => import('@/views/manager/dept/Department.vue') },
        { path: 'creditline', meta: { name: '学分信息' }, component: () => import('@/views/manager/dept/CreditLine.vue') },
        // 用户管理
        { path: 'admin', meta: { name: '管理员信息' }, component: () => import('@/views/manager/user/Admin.vue') },
        { path: 'teacher', meta: { name: '教师信息' }, component: () => import('@/views/manager/user/Teacher.vue') },
        { path: 'student', meta: { name: '学生信息' }, component: () => import('@/views/manager/user/Student.vue') },
        // 课程管理
        { path: 'course', meta: { name: '课程信息' }, component: () => import('@/views/manager/course/Course.vue') },
        { path: 'cselection', meta: { name: '选课信息' }, component: () => import('@/views/manager/course/CSelection.vue') },
        { path: 'studentscore', meta: { name: '学生分数管理' }, component: () => import('@/views/manager/course/StudentScore.vue') },
        { path: 'timerange', meta: { name: '时间范围设置' }, component: () => import('@/views/manager/course/SemesterTimeRange.vue') },
        // 权限管理
        { path: 'permission', meta: { name: '权限管理' }, component: () => import('@/views/manager/permission/Permission.vue') },
        { path: 'role', meta: { name: '角色管理' }, component: () => import('@/views/manager/permission/Role.vue') },
        { path: 'user-role', meta: { name: '角色分配管理' }, component: () => import('@/views/manager/permission/UserRole.vue') }
      ]
    },
    
    // 学生路由
    {
      path: '/student',
      component: () => import('@/views/Front.vue'),
      children: [
        { path: 'home', component: () => import('@/views/front/Home.vue') },
        { path: 'courses', component: () => import('@/views/front/student/StudentCourses.vue') },
        { path: 'selection', component: () => import('@/views/front/student/CourseSelection.vue') },
        { path: 'all-courses', component: () => import('@/views/front/student/AllCourses.vue') },
        { path: 'person', component: () => import('@/views/front/Person.vue') }
      ]
    },
    
    // 教师路由
    {
      path: '/teacher',
      component: () => import('@/views/Front.vue'),
      children: [
        { path: 'dashboard', component: () => import('@/views/front/Home.vue') },
        { path: 'courses', component: () => import('@/views/front/teacher/TeacherCourses.vue') },
        { path: 'all-courses', component: () => import('@/views/front/teacher/OfferCourses.vue') },
        { path: 'students', component: () => import('@/views/front/teacher/StudentsScore.vue') },
        { path: 'person', component: () => import('@/views/front/Person.vue') }
      ]
    },
    
    // 登录相关路由
    { path: '/login', component: () => import('@/views/Login.vue') },
    { path: '/adminlogin', component: () => import('@/views/AdminLogin.vue') },
    
    // 错误页面
    { path: '/404', component: () => import('@/views/communal/404.vue') },
    { path: '/:pathMatch(.*)', redirect: '/404' }
  ]
})

// 路由守卫
router.beforeEach((to, from, next) => {
  // 检查是否需要登录
  const needLoginRoutes = ['/manager', '/student', '/teacher']
  const needLogin = needLoginRoutes.some(route => to.path.startsWith(route))
  
  if (needLogin) {
    // 需要登录的路由
    if (isLoggedIn()) {
      next() // 已登录，继续访问
    } else {
      // 未登录，跳转到登录页面
      ElMessage.warning('请先登录')
      next('/login')
    }
  } else if (to.path === '/login' || to.path === '/adminlogin') {
    // 登录页面，已登录则重定向到对应首页
    if (isLoggedIn()) {
      redirectToHomePage(next)
    } else {
      next()
    }
  } else if (to.path === '/') {
    // 根路径，已登录则重定向到对应首页
    if (isLoggedIn()) {
      redirectToHomePage(next)
    } else {
      next() // 未登录，继续重定向到登录页面
    }
  } else {
    // 其他不需要登录的路由
    next()
  }
})



// 重定向到对应角色的首页
function redirectToHomePage(next) {
  const role = getUserRole()
  switch (role) {
    case 'STUDENT':
      next('/student/home')
      break
    case 'TEACHER':
      next('/teacher/dashboard')
      break
    default:
      next('/manager/home')
      break
  }
}

export default router