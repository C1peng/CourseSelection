<template>
  <div class="main-content">
    <!-- 学生首页 -->
    <div v-if="user.role === 'STUDENT'" class="student-home">
      <!-- 欢迎横幅 -->
      <div class="welcome-banner student-banner">
        <div class="banner-content">
          <div class="banner-left">
            <div class="welcome-text">
              <h2>欢迎回来，{{ user.name }}同学！</h2>
              <p class="date-text">
                <el-icon><Calendar /></el-icon>
                {{ currentDate }}
              </p>
              <p class="greeting-text">祝您学习愉快，收获满满！</p>
            </div>
            <div class="quick-actions">
              <el-button type="primary" size="large" @click="goToSelection">
                <el-icon><Plus /></el-icon>
                选课中心
              </el-button>
              <el-button size="large" @click="router.push('/student/courses')">
                <el-icon><Document /></el-icon>
                我的课程
              </el-button>
              <el-button size="large" @click="router.push(user.role === 'STUDENT' ? '/student/all-courses' : '/teacher/all-courses')">
                <el-icon><Grid /></el-icon>
                全部课程
              </el-button>
            </div>
          </div>
          <div class="banner-right">
            <div class="banner-image student-image">
              <div class="image-placeholder">
                <el-icon class="placeholder-icon"><Reading /></el-icon>
                <p>学习中心</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card stat-card-1">
          <div class="stat-icon-wrapper">
            <el-icon class="stat-icon"><Document /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ courseCount }}</div>
            <div class="stat-label">已选课程</div>
            <div class="stat-trend">
              <el-icon><TrendCharts /></el-icon>
              <span>持续学习中</span>
            </div>
          </div>
          <div class="stat-decoration"></div>
        </div>
        <div class="stat-card stat-card-2">
          <div class="stat-icon-wrapper">
            <el-icon class="stat-icon"><Timer /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ studyHours }}</div>
            <div class="stat-label">本周学习时长</div>
            <div class="stat-trend">
              <el-icon><TrendCharts /></el-icon>
              <span>小时</span>
            </div>
          </div>
          <div class="stat-decoration"></div>
        </div>
        <div class="stat-card stat-card-3">
          <div class="stat-icon-wrapper">
            <el-icon class="stat-icon"><Check /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ completedCourses }}</div>
            <div class="stat-label">已完成课程</div>
            <div class="stat-trend">
              <el-icon><TrendCharts /></el-icon>
              <span>继续加油</span>
            </div>
          </div>
          <div class="stat-decoration"></div>
        </div>
        <div class="stat-card stat-card-4">
          <div class="stat-icon-wrapper">
            <el-icon class="stat-icon"><DataAnalysis /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">85%</div>
            <div class="stat-label">平均进度</div>
            <div class="stat-trend">
              <el-icon><TrendCharts /></el-icon>
              <span>优秀</span>
            </div>
          </div>
          <div class="stat-decoration"></div>
        </div>
      </div>

      <!-- 我的课程卡片 -->
      <el-card class="course-card modern-card">
        <template #header>
          <div class="card-header-modern">
            <div class="header-title-section">
              <el-icon class="header-icon"><Reading /></el-icon>
              <span class="header-title">我的课程</span>
            </div>
            <div class="header-actions">
              <div class="semester-selector-modern">
                <el-icon><Calendar /></el-icon>
                <el-input-number v-model="startYear" :min="2000" :max="2100" size="small" controls-position="right" />
                <span class="separator">-</span>
                <el-input-number v-model="endYear" :min="2000" :max="2100" size="small" controls-position="right" />
                <span class="year-label">学年</span>
                <el-select v-model="semesterType" placeholder="学期" size="small">
                  <el-option label="第一学期" value="第一学期" />
                  <el-option label="第二学期" value="第二学期" />
                </el-select>
              </div>
              <el-button type="primary" size="small" @click="loadStudentCourses" circle>
                <el-icon><Refresh /></el-icon>
              </el-button>
            </div>
          </div>
        </template>
        <div class="course-list">
          <el-empty v-if="courses.length === 0" description="暂无课程，快去选课吧！" />
          <div v-else class="course-schedule">
            <div class="schedule-header">
              <div class="time-header"></div>
              <div class="weekday-header" v-for="day in weekdays" :key="day.value">
                {{ day.label }}
              </div>
            </div>
            <div class="schedule-body">
              <!-- 为每节课创建一个单元格 -->
              <div v-for="section in sections" :key="section" class="schedule-row">
                <div class="section-number">{{ section }}节</div>
                <div v-for="day in weekdays" :key="day.value" class="schedule-cell">
                  <!-- 检查当前单元格是否有课程开始 -->
                  <template v-for="course in courses" :key="course?.id">
                    <div v-if="course && isCourseAtCell(course, day.value, section)"
                         :class="['course-item', { 'course-span': getCourseSpan(course) > 1 }]"
                         :style="getCourseStyle(course, section)"
                         @click="enterCourse(course.id)">
                      <div class="course-info">
                        <div class="course-name">{{ course?.courseName || '未知课程' }}</div>
                        <div class="course-meta">
                          <div class="course-cycle">{{ course?.cycle ? getCycleTypeLabel(course.cycle) : '' }}</div>
                          <div class="place">{{ course?.place || '未知' }}</div>
                        </div>
                      </div>
                    </div>
                  </template>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>

    <!-- 教师首页 -->
    <div v-else-if="user.role === 'TEACHER'" class="teacher-home">
      <!-- 欢迎横幅 -->
      <div class="welcome-banner">
        <div class="banner-content">
          <div class="banner-left">
            <div class="welcome-text">
              <h2>欢迎回来，{{ user.name }}老师！</h2>
              <p class="date-text">
                <el-icon><Calendar /></el-icon>
                {{ currentDate }}
              </p>
              <p class="greeting-text">祝您工作顺利，教学愉快！</p>
            </div>
            <div class="quick-actions">
              <el-button type="primary" size="large" @click="goToCourses">
                <el-icon><Notebook /></el-icon>
                我的课程
              </el-button>
              <el-button size="large" @click="router.push('/teacher/students')">
                <el-icon><UserFilled /></el-icon>
                管理学生
              </el-button>
              <el-button size="large" @click="router.push('/teacher/all-courses')">
                <el-icon><Grid /></el-icon>
                开设课程
              </el-button>
            </div>
          </div>
          <div class="banner-right">
            <div class="banner-image">
              <div class="image-placeholder">
                <el-icon class="placeholder-icon"><School /></el-icon>
                <p>教师工作台</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 统计卡片 -->
      <div class="stats-grid">
        <div class="stat-card stat-card-1">
          <div class="stat-icon-wrapper">
            <el-icon class="stat-icon"><Notebook /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ teachingCourses }}</div>
            <div class="stat-label">授课课程</div>
            <div class="stat-trend">
              <el-icon><TrendCharts /></el-icon>
              <span>{{ getSemesterString() }}</span>
            </div>
          </div>
          <div class="stat-decoration"></div>
        </div>
        <div class="stat-card stat-card-2">
          <div class="stat-icon-wrapper">
            <el-icon class="stat-icon"><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ studentCount }}</div>
            <div class="stat-label">指导学生</div>
            <div class="stat-trend">
              <el-icon><TrendCharts /></el-icon>
              <span>活跃学生 95%</span>
            </div>
          </div>
          <div class="stat-decoration"></div>
        </div>
        <div class="stat-card stat-card-3">
          <div class="stat-icon-wrapper">
            <el-icon class="stat-icon"><Message /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ unreadMessages }}</div>
            <div class="stat-label">未读消息</div>
            <div class="stat-trend">
              <el-icon><Bell /></el-icon>
              <span>待处理</span>
            </div>
          </div>
          <div class="stat-decoration"></div>
        </div>
        <div class="stat-card stat-card-4">
          <div class="stat-icon-wrapper">
            <el-icon class="stat-icon"><DataAnalysis /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">98%</div>
            <div class="stat-label">课程完成率</div>
            <div class="stat-trend">
              <el-icon><TrendCharts /></el-icon>
              <span>优秀</span>
            </div>
          </div>
          <div class="stat-decoration"></div>
        </div>
      </div>

      <el-card class="teaching-card modern-card">
        <template #header>
          <div class="card-header-modern">
            <div class="header-title-section">
              <el-icon class="header-icon"><Reading /></el-icon>
              <span class="header-title">我的授课</span>
            </div>
            <div class="header-actions">
              <div class="semester-selector-modern">
                <el-icon><Calendar /></el-icon>
                <el-input-number v-model="startYear" :min="2000" :max="2100" size="small" controls-position="right" />
                <span class="separator">-</span>
                <el-input-number v-model="endYear" :min="2000" :max="2100" size="small" controls-position="right" />
                <span class="year-label">学年</span>
                <el-select v-model="semesterType" placeholder="学期" size="small">
                  <el-option label="第一学期" value="第一学期" />
                  <el-option label="第二学期" value="第二学期" />
                </el-select>
              </div>
              <el-button type="primary" size="small" @click="loadTeachingCourses" circle>
                <el-icon><Refresh /></el-icon>
              </el-button>
              <el-button type="primary" size="small" @click="goToCourses">
                <el-icon><Setting /></el-icon>
                课程管理
              </el-button>
            </div>
          </div>
        </template>
        <div class="teaching-list">
          <el-empty v-if="teachingList.length === 0" description="暂无授课" />
          <div v-else class="course-schedule">
            <div class="schedule-header">
              <div class="time-header"></div>
              <div class="weekday-header" v-for="day in weekdays" :key="day.value">
                {{ day.label }}
              </div>
            </div>
            <div class="schedule-body">
              <!-- 为每节课创建一个单元格 -->
              <div v-for="section in sections" :key="section" class="schedule-row">
                <div class="section-number">{{ section }}节</div>
                <div v-for="day in weekdays" :key="day.value" class="schedule-cell">
                  <!-- 检查当前单元格是否有课程开始 -->
                  <template v-for="course in teachingList" :key="course?.id">
                    <div v-if="course && isCourseAtCell(course, day.value, section)"
                         :class="['course-item', { 'course-span': getCourseSpan(course) > 1 }]"
                         :style="getCourseStyle(course, section)">
                      <div class="course-info">
                        <div class="course-name">{{ course?.courseName || '未知课程' }}</div>
                        <div class="course-meta">
                          <div class="course-cycle">{{ course?.cycle ? getCycleTypeLabel(course.cycle) : '' }}</div>
                          <div class="place">{{ course?.place || '未知' }}</div>
                        </div>
                      </div>
                    </div>
                  </template>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- <div class="bottom-section">
        <el-card class="student-card modern-card">
          <template #header>
            <div class="card-header-modern">
              <div class="header-title-section">
                <el-icon class="header-icon"><Bell /></el-icon>
                <span class="header-title">近期学生动态</span>
              </div>
              <el-button text size="small" @click="router.push('/teacher/students')">
                查看更多
                <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <div class="student-activity">
            <el-empty v-if="studentActivities.length === 0" description="暂无动态" />
            <el-timeline v-else class="modern-timeline">
              <el-timeline-item 
                v-for="(activity, index) in studentActivities" 
                :key="index"
                :timestamp="activity.time"
                type="primary"
                size="large"
              >
                <el-card class="activity-card" shadow="hover">
                  <div class="activity-header">
                    <div class="activity-avatar">
                      <el-icon><UserFilled /></el-icon>
                    </div>
                    <div class="activity-info">
                      <h4 class="activity-student">{{ activity.studentName }}</h4>
                      <p class="activity-content">{{ activity.content }}</p>
                    </div>
                  </div>
                </el-card>
              </el-timeline-item>
            </el-timeline>
          </div>
        </el-card>

        快捷功能卡片
        <el-card class="quick-actions-card modern-card">
          <template #header>
            <div class="card-header-modern">
              <div class="header-title-section">
                <el-icon class="header-icon"><Grid /></el-icon>
                <span class="header-title">快捷功能</span>
              </div>
            </div>
          </template>
          <div class="quick-functions">
            <div class="function-item" @click="goToCourses">
              <div class="function-icon-wrapper function-icon-1">
                <el-icon class="function-icon"><Plus /></el-icon>
              </div>
              <div class="function-content">
                <div class="function-title">添加课程</div>
                <div class="function-desc">创建新的授课课程</div>
              </div>
            </div>
            <div class="function-item" @click="router.push('/teacher/students')">
              <div class="function-icon-wrapper function-icon-2">
                <el-icon class="function-icon"><UserFilled /></el-icon>
              </div>
              <div class="function-content">
                <div class="function-title">学生管理</div>
                <div class="function-desc">查看和管理学生信息</div>
              </div>
            </div>
            <div class="function-item" @click="router.push('/teacher/person')">
              <div class="function-icon-wrapper function-icon-3">
                <el-icon class="function-icon"><User /></el-icon>
              </div>
              <div class="function-content">
                <div class="function-title">个人中心</div>
                <div class="function-desc">修改个人信息</div>
              </div>
            </div>
            <div class="function-item">
              <div class="function-icon-wrapper function-icon-4">
                <el-icon class="function-icon"><DataAnalysis /></el-icon>
              </div>
              <div class="function-content">
                <div class="function-title">数据分析</div>
                <div class="function-desc">查看教学数据统计</div>
              </div>
            </div>
          </div>
        </el-card>
      </div> -->
    </div>

    <!-- 默认页面 -->
    <div v-else class="default-home">
      <el-card>
        <template #header>
          <div class="card-header">
            <span>欢迎访问系统</span>
          </div>
        </template>
        <div class="default-content">
          <p>请登录后查看个性化内容</p>
          <el-button type="primary" @click="goToLogin">去登录</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { getUserInfo } from '@/utils/userStorage.js'
import router from '@/router'
import request from '@/utils/request.js'
import { ElMessage } from 'element-plus'
import { 
  Document, Timer, Check, Notebook, User, Message, Bell, 
  Calendar, Plus, UserFilled, TrendCharts, DataAnalysis,
  Reading, Refresh, Setting, ArrowRight, Grid, School
} from '@element-plus/icons-vue'

// 获取进度条颜色
const getProgressColor = (percentage) => {
  if (percentage >= 80) {
    return '#67c23a'
  } else if (percentage >= 60) {
    return '#409eff'
  } else if (percentage >= 40) {
    return '#e6a23c'
  } else {
    return '#f56c6c'
  }
}
import { CYCLE_TYPE, WEEKDAY } from '@/constant/module/type.js'

const user = ref(getUserInfo() || {})

const currentDate = computed(() => {
  return new Date().toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 学生数据
const courseCount = ref(8)
const studyHours = ref(12)
const completedCourses = ref(3)
const courses = ref([])

// 教师数据
const teachingCourses = ref(5)
const studentCount = ref(120)
const unreadMessages = ref(8)
const teachingList = ref([])
const studentActivities = ref([
  { studentName: '张三', content: '提交了高等数学作业', time: '2024-01-20 15:30' },
  { studentName: '李四', content: '提问了关于线性代数的问题', time: '2024-01-20 14:20' },
  { studentName: '王五', content: '完成了概率论测验', time: '2024-01-20 10:15' }
])

// 课表数据
const weekdays = [
  { value: 1, label: '周一' },
  { value: 2, label: '周二' },
  { value: 3, label: '周三' },
  { value: 4, label: '周四' },
  { value: 5, label: '周五' }
]

const sections = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12]

// 获取当前年份和月份
const now = new Date()
const currentYear = now.getFullYear()
const currentMonth = now.getMonth() + 1 // 月份从0开始，所以加1

// 学期选择：9月到次年8月为一个学年
let initStartYear, initEndYear, initSemester
if (currentMonth >= 9) {
  initStartYear = currentYear
  initEndYear = currentYear + 1
  initSemester = '第一学期'
} else {
  initStartYear = currentYear - 1
  initEndYear = currentYear
  initSemester = '第二学期'
}

const startYear = ref(initStartYear)
const endYear = ref(initEndYear)
const semesterType = ref(initSemester)

// 监听年份变化，保持相差一年
watch(startYear, (newValue) => {
  endYear.value = newValue + 1
})

watch(endYear, (newValue) => {
  startYear.value = newValue - 1
})

// 构建学期字符串
const getSemesterString = () => {
  return `${startYear.value}-${endYear.value}学年${semesterType.value}`
}

// 辅助函数
const getCycleTypeLabel = (value) => {
  const entry = Object.entries(CYCLE_TYPE).find(([_, item]) => item.value === value);
  return entry ? entry[1].label : value;
};

const getCycleTypeTagType = (value) => {
  const typeMap = {
    'SINGLE': 'primary',
    'DOUBLE': 'success',
    'ALL': 'warning'
  };
  return typeMap[value] || 'info';
};

// 解析weekTime函数，使用WEEKDAY常量
const formatWeekTime = (weekTime) => {
  if (!weekTime) return '';
  
  // 将数字转换为字符串，确保格式统一
  const timeStr = weekTime.toString();
  
  // 解析规则：20304 -> 星期二 3-4节
  // 第一位：星期几（1=星期一，2=星期二，...，5=星期五）
  // 中间两位：开始节数
  // 最后两位：结束节数
  
  const weekDay = parseInt(timeStr[0]);
  const startSection = parseInt(timeStr.substring(1, 3));
  const endSection = parseInt(timeStr.substring(3));
  
  // 只处理1-5的星期值
  if (weekDay < 1 || weekDay > 5) return '';
  
  // 使用WEEKDAY常量
  const weekdayMap = {
    '1': WEEKDAY.MONDAY.label,
    '2': WEEKDAY.TUESDAY.label,
    '3': WEEKDAY.WEDNESDAY.label,
    '4': WEEKDAY.THURSDAY.label,
    '5': WEEKDAY.FRIDAY.label
  };
  
  return `星期${weekdayMap[weekDay]} ${startSection}-${endSection}节`;
};

// 判断课程是否在当前单元格开始
const isCourseAtCell = (course, day, section) => {
  try {
    // 添加参数验证
    if (!course || !course.weekTime) {
      return false;
    }
    
    const timeStr = course.weekTime.toString();
    const weekDay = parseInt(timeStr[0]);
    const startSection = parseInt(timeStr.substring(1, 3));
    
    // 只在课程开始节数的单元格显示课程
    return weekDay === day && startSection === section;
  } catch (error) {
    console.error('处理课程数据时出错:', error);
    return false;
  }
};

// 获取课程的跨格数
const getCourseSpan = (course) => {
  try {
    // 添加参数验证
    if (!course || !course.weekTime) {
      return 1;
    }
    
    const timeStr = course.weekTime.toString();
    const startSection = parseInt(timeStr.substring(1, 3));
    const endSection = parseInt(timeStr.substring(3));
    
    return endSection - startSection + 1;
  } catch (error) {
    console.error('处理课程数据时出错:', error);
    return 1;
  }
};

// 获取课程的样式
const getCourseStyle = (course, section) => {
  try {
    // 添加参数验证
    if (!course) {
      return {
        height: '40px',
        top: '2px'
      };
    }
    
    const span = getCourseSpan(course);
    // 计算课程的高度和位置
    const height = span * 40;
    
    return {
      height: `${height}px`,
      top: '2px',
      zIndex: span > 1 ? 2 : 1
    };
  } catch (error) {
    console.error('处理课程数据时出错:', error);
    return {
      height: '40px',
      top: '2px'
    };
  }
};

const goToSelection = () => {
  router.push('/student/selection')
}

const goToCourses = () => {
  router.push('/teacher/courses')
}

const enterCourse = (courseId) => {
  console.log('进入学习:', courseId)
  // 实际应跳转到课程学习页
  ElMessage.info('进入课程学习页面')
}

const goToLogin = () => {
  router.push('/login')
}

const viewStudents = (courseId) => {
  // 查看课程学生
  console.log('查看课程学生:', courseId)
}

const editCourse = (courseId) => {
  // 编辑课程
  console.log('编辑课程:', courseId)
}

// 通用课程加载函数
const loadCourses = (role, callback) => {
  if (user.value.role === role && user.value.id) {
    const semester = getSemesterString()
    // 调用后端接口获取课程列表
    request.get('courseTimetable', {
      params: {
        semester: semester
      }
    }).then(res => {
      if (res.code === '200') {
        // 确保返回的数据始终是一个数组
        let courseList = []
        if (res.data && Array.isArray(res.data.list)) {
          // 处理分页接口返回的格式 {list: [...], total: ...}
          courseList = res.data.list
        }
        callback(courseList)
      } else {
        console.error('获取课程失败:', res.msg)
        callback([])
      }
    }).catch(err => {
      console.error('请求失败:', err)
      callback([])
    })
  }
}

// 加载教师授课课程
const loadTeachingCourses = () => {
  loadCourses('TEACHER', (courseList) => {
    teachingList.value = courseList
    teachingCourses.value = courseList.length
  })
}

// 加载学生课程
const loadStudentCourses = () => {
  loadCourses('STUDENT', (courseList) => {
    courses.value = courseList
    courseCount.value = courseList.length
  })
}

onMounted(() => {
  // 这里可以添加实际的数据获取逻辑
  console.log('用户信息:', user.value)
  // 加载教师授课课程
  if (user.value.role === 'TEACHER') {
    loadTeachingCourses()
  } else if (user.value.role === 'STUDENT') {
    // 加载学生课程
    loadStudentCourses()
  }
})
</script>

<style scoped>
/* ==================== 导入公共样式 ==================== */
@import "@/assets/css/layouts/page.css";
@import "@/assets/css/common/card.css";

/* ==================== 页面布局 ==================== */
.main-content {
  padding: var(--space-lg);
  background: var(--front-bg);
  min-height: 100%;
  max-width: 1400px;
  margin: 0 auto;
}

/* 学生 / 教师首页整体布局 */
.student-home,
.teacher-home {
  display: flex;
  flex-direction: column;
  gap: var(--space-xl);
}

/* ==================== 欢迎横幅 ==================== */
.welcome-banner {
  position: relative;
  background: var(--front-primary-gradient);
  border-radius: var(--radius-xl);
  padding: var(--space-2xl);
  color: #fff;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(102, 126, 234, 0.3);
}

/* 学生端横幅特殊样式 */
.student-banner {
  background: linear-gradient(135deg, #00b894 0%, #00cec9 100%);
  box-shadow: 0 20px 60px rgba(0, 184, 148, 0.3);
}

.student-banner .welcome-banner__title,
.student-banner .welcome-banner__subtitle,
.student-banner .welcome-banner__greeting {
  color: #fff;
}

.student-image {
  width: 200px;
  height: 200px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
}

.banner-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  position: relative;
  z-index: 1;
}

.banner-left {
  flex: 1;
}

.welcome-text h2 {
  font-size: 32px;
  font-weight: var(--font-weight-bold);
  margin: 0 0 var(--space-sm) 0;
  color: #fff;
}

.date-text {
  font-size: var(--font-size-md);
  opacity: 0.9;
  margin-bottom: var(--space-md);
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.greeting-text {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-medium);
  margin-bottom: var(--space-lg);
}

.quick-actions {
  display: flex;
  gap: var(--space-md);
}

.banner-right {
  flex-shrink: 0;
  margin-left: var(--space-xl);
}

.banner-image {
  width: 200px;
  height: 200px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.15);
}

.image-placeholder {
  text-align: center;
  color: rgba(255, 255, 255, 0.9);
}

.placeholder-icon {
  font-size: 64px;
  margin-bottom: var(--space-sm);
  display: block;
}

.image-placeholder p {
  margin: 0;
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
}

/* ==================== 统计卡片网格 ==================== */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: var(--space-lg);
}

.stat-card {
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  padding: var(--space-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  gap: var(--space-md);
  transition: all var(--transition-normal);
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-lg);
}

.stat-card-1 {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
}

.stat-card-2 {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
  border: none;
}

.stat-card-3 {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: #fff;
  border: none;
}

.stat-card-4 {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: #fff;
  border: none;
}

.stat-icon-wrapper {
  width: 60px;
  height: 60px;
  border-radius: var(--radius-md);
  background: rgba(255, 255, 255, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon {
  font-size: 28px;
  color: #fff;
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: var(--font-weight-bold);
  margin-bottom: var(--space-xs);
}

.stat-label {
  font-size: var(--font-size-sm);
  opacity: 0.9;
}

.stat-trend {
  font-size: var(--font-size-xs);
  opacity: 0.8;
}

.stat-decoration {
  position: absolute;
  top: -20px;
  right: -20px;
  width: 100px;
  height: 100px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
}

/* ==================== 现代化卡片样式 ==================== */
.modern-card {
  background: var(--bg-white);
  border-radius: var(--radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  overflow: hidden;
  transition: all var(--transition-normal);
}

.modern-card:hover {
  box-shadow: var(--shadow-md);
}

.card-header-modern {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--space-lg);
  border-bottom: 1px solid var(--border-color);
}

.header-title-section {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
}

.header-icon {
  width: 40px;
  height: 40px;
  border-radius: var(--radius-md);
  background: var(--front-primary-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
}

.header-title {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.header-actions {
  display: flex;
  gap: var(--space-sm);
}

.semester-selector-modern {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  background: var(--bg-gray);
  padding: var(--space-sm) var(--space-md);
  border-radius: 10px;
  border: 1px solid var(--border-color);
  flex-wrap: nowrap;
  min-width: fit-content;
  flex-shrink: 0;
}

.semester-selector-modern .el-icon {
  color: var(--front-primary);
  font-size: 16px;
  flex-shrink: 0;
}

.semester-selector-modern :deep(.el-input-number) {
  width: 90px !important;
  min-width: 90px;
}

.semester-selector-modern :deep(.el-input-number .el-input__wrapper) {
  width: 100%;
  padding-right: 30px;
}

.semester-selector-modern :deep(.el-input-number .el-input__inner) {
  width: 100% !important;
  text-align: center;
  font-size: 13px;
  font-weight: 500;
}

.semester-selector-modern .separator {
  color: var(--text-secondary);
  margin: 0 4px;
  flex-shrink: 0;
  font-weight: 500;
}

.semester-selector-modern .year-label {
  color: var(--text-secondary);
  font-size: 13px;
  margin-right: var(--space-sm);
  flex-shrink: 0;
  white-space: nowrap;
}

.semester-selector-modern :deep(.el-select) {
  width: 100px;
  min-width: 100px;
  flex-shrink: 0;
}

.bottom-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: var(--space-xl);
  margin-top: var(--space-xl);
}

.student-card,
.quick-actions-card {
  min-height: 400px;
  display: flex;
  flex-direction: column;
}

.student-card :deep(.el-card__body),
.quick-actions-card :deep(.el-card__body) {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0;
}

.student-activity {
  padding: var(--space-lg);
  min-height: 300px;
  background: var(--bg-white);
  flex: 1;
  display: flex;
  flex-direction: column;
}

.student-activity :deep(.el-empty) {
  padding: 60px 0;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.student-activity :deep(.el-empty__description) {
  color: var(--text-secondary);
  font-size: var(--font-size-md);
  margin-top: var(--space-sm);
}

.student-activity :deep(.el-timeline) {
  flex: 1;
}

.modern-timeline {
  padding: var(--space-lg) 0;
}

.activity-card {
  border-radius: var(--radius-lg);
  border: 1px solid var(--border-color);
  transition: all var(--transition-normal);
  margin-bottom: var(--space-sm);
}

.activity-card:hover {
  border-color: var(--front-primary);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.15);
}

.activity-header {
  display: flex;
  gap: var(--space-md);
  align-items: flex-start;
}

.activity-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--front-primary-gradient);
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  flex-shrink: 0;
}

.activity-info {
  flex: 1;
}

.activity-student {
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin: 0 0 var(--space-sm) 0;
}

.activity-content {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
  margin: 0;
  line-height: 1.6;
}

/* 快捷功能卡片 */
.quick-functions {
  padding: var(--space-lg);
  display: flex;
  flex-direction: column;
  gap: var(--space-md);
  background: var(--bg-white);
  min-height: 300px;
  flex: 1;
}

.function-item {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  padding: var(--space-md);
  border-radius: var(--radius-lg);
  background: var(--bg-gray);
  cursor: pointer;
  transition: all var(--transition-normal);
  border: 1px solid transparent;
}

.function-item:hover {
  background: var(--bg-white);
  border-color: var(--front-primary);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.1);
  transform: translateX(4px);
}

.function-icon-wrapper {
  width: 48px;
  height: 48px;
  border-radius: var(--radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.function-icon-1 {
  background: var(--front-primary-gradient);
  color: #fff;
}

.function-icon-2 {
  background: linear-gradient(135deg, #00b894 0%, #00cec9 100%);
  color: #fff;
}

.function-icon-3 {
  background: linear-gradient(135deg, #ff7675 0%, #ff9f43 100%);
  color: #fff;
}

.function-icon-4 {
  background: linear-gradient(135deg, #6c5ce7 0%, #a29bfe 100%);
  color: #fff;
}

.function-icon {
  font-size: 24px;
}

.function-content {
  flex: 1;
}

.function-title {
  font-size: 15px;
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: var(--space-xs);
}

.function-desc {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

/* 表格风格统一 */
.teaching-card .el-table,
.course-card .el-table {
  --el-table-border-color: var(--border-color-light);
  --el-table-header-bg-color: var(--bg-gray-light);
  --el-table-row-hover-bg-color: #f5f7ff;
  --el-table-current-row-bg-color: #edf3ff;
  border-radius: var(--radius-lg);
  overflow: hidden;
}

.teaching-card .el-table th.el-table__cell,
.course-card .el-table th.el-table__cell {
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  font-size: var(--font-size-sm);
}

.teaching-card .el-table td.el-table__cell,
.course-card .el-table td.el-table__cell {
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.teaching-card .el-button,
.course-card .el-button {
  border-radius: var(--radius-md);
}

.teaching-list,
.course-list {
  padding: var(--space-sm);
  min-height: 280px;
}

/* 课表样式 */
.course-schedule {
  margin-top: var(--space-sm);
  overflow-x: auto;
}

.schedule-header {
  display: grid;
  grid-template-columns: 60px repeat(5, 1fr);
  gap: 1px;
  background-color: var(--border-color);
  margin-bottom: 1px;
}

.time-header {
  background-color: var(--bg-gray);
  padding: var(--space-sm);
  font-weight: var(--font-weight-semibold);
  text-align: center;
}

.weekday-header {
  background-color: var(--bg-gray);
  padding: var(--space-md) var(--space-sm);
  font-weight: var(--font-weight-semibold);
  text-align: center;
  color: var(--text-primary);
}

.schedule-body {
  display: grid;
  grid-template-columns: 60px repeat(5, 1fr);
  gap: 1px;
  background-color: var(--border-color);
}

.schedule-row {
  display: contents;
}

.section-number {
  background-color: var(--bg-gray);
  padding: var(--space-sm);
  font-weight: var(--font-weight-semibold);
  text-align: center;
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
}

.schedule-cell {
  background-color: var(--bg-white);
  height: 40px;
  position: relative;
  padding: 2px;
  overflow: visible;
}

.course-item {
  background: var(--front-primary-gradient);
  color: #ffffff;
  border-radius: var(--radius-sm);
  padding: var(--space-sm);
  font-size: var(--font-size-xs);
  cursor: pointer;
  transition: all var(--transition-normal);
  position: absolute;
  left: 2px;
  right: 2px;
  z-index: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.course-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.course-span {
  z-index: 2;
}

.course-name {
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--space-xs);
  line-height: 1.2;
}

.course-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-sm);
}

.course-cycle {
  flex: 1;
  font-size: 10px;
  opacity: 0.9;
}

.place {
  flex: 2;
  text-align: right;
  font-size: 10px;
  opacity: 0.9;
}

.course-list :deep(.el-button) {
  border-radius: var(--radius-md);
  font-weight: var(--font-weight-medium);
}

.default-home .el-card {
  border-radius: var(--radius-xl);
  border: none;
  box-shadow: var(--shadow-md);
}

.default-content {
  text-align: center;
  padding: 60px 0;
}

.default-content p {
  margin-bottom: var(--space-xl);
  font-size: var(--font-size-md);
  color: var(--text-secondary);
}

/* 周次类型标签样式 */
.cycle-tag {
  border-radius: 999px;
  font-size: var(--font-size-xs);
  padding: 2px var(--space-sm);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .main-content {
    padding: var(--space-md);
  }

  .welcome-banner {
    padding: var(--space-lg);
  }

  .banner-content {
    flex-direction: column;
    text-align: center;
  }

  .banner-right {
    margin-left: 0;
    margin-top: var(--space-lg);
  }

  .welcome-text h2 {
    font-size: 24px;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .bottom-section {
    grid-template-columns: 1fr;
  }

  .schedule-header {
    grid-template-columns: 50px repeat(5, 1fr);
  }
  
  .schedule-body {
    grid-template-columns: 50px repeat(5, 1fr);
  }
  
  .section-number {
    padding: var(--space-xs);
    font-size: 10px;
  }
  
  .weekday-header {
    padding: var(--space-sm) var(--space-xs);
    font-size: var(--font-size-xs);
  }
  
  .schedule-cell {
    min-height: 50px;
  }
  
  .course-item {
    padding: var(--space-xs);
    font-size: 10px;
  }
}
</style>