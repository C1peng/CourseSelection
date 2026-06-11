<template>
  <div class="teacher-students-container">
    <!-- 页面头部 -->
    <header class="page-header">
      <div class="header-content">
        <div class="header-info">
          <h1 class="page-title">学分管理</h1>
          <!-- <p class="teacher-info">欢迎，{{ user.name || '教师' }} <span class="teacher-id">(ID: {{ user.teacherId || '未知' }})</span></p> -->
        </div>
        <div class="header-actions">
          <el-button type="primary" @click="exportStudents">
            <el-icon><Download /></el-icon>导出学生列表
          </el-button>
          <el-button @click="refreshData">
            <el-icon><Refresh /></el-icon>刷新数据
          </el-button>
        </div>
      </div>
    </header>

    <!-- 数据概览 -->
    <div class="overview-section" v-if="overviewData.totalCourses > 0">
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="overview-card total-courses">
            <div class="card-content">
              <div class="card-icon"><el-icon><Document /></el-icon></div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.totalCourses }}</div>
                <div class="card-label">课程总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overview-card total-students">
            <div class="card-content">
              <div class="card-icon"><el-icon><User /></el-icon></div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.totalStudents }}</div>
                <div class="card-label">学生总数</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overview-card pending-students">
            <div class="card-content">
              <div class="card-icon"><el-icon><Timer /></el-icon></div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.pendingStudents }}</div>
                <div class="card-label">待录入成绩</div>
              </div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="overview-card saved-students">
            <div class="card-content">
              <div class="card-icon"><el-icon><Check /></el-icon></div>
              <div class="card-info">
                <div class="card-value">{{ overviewData.savedStudents }}</div>
                <div class="card-label">已录入成绩</div>
              </div>
            </div>
          </el-card>
        </el-col>
      </el-row>
    </div>



    <!-- 课程列表 -->
    <div class="courses-section">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="5" animated />
      </div>
      <div v-else-if="coursesData.length === 0" class="empty-container">
        <el-empty description="暂无课程数据" />
      </div>
      <div v-else class="courses-list">
        <div class="course-layout">
          <!-- 左侧课程导航 -->
          <div class="course-sidebar">
            <div class="sidebar-header">
              <h3>课程列表</h3>
              <span class="course-count">共 {{ coursesData.length }} 门课程</span>
            </div>
            <div class="sidebar-search">
              <el-input
                v-model="courseSearchKeyword"
                placeholder="搜索课程名称"
                prefix-icon="Search"
                clearable
                size="small"
                @clear="loadData"
                @keyup.enter="loadData"
              />
            </div>
            <div class="course-menu">
              <div
                v-for="course in filteredCourses"
                :key="course.courseName"
                :class="['course-menu-item', { active: selectedCourse?.courseName === course.courseName }]"
                @click="selectCourse(course)"
              >
                <div class="course-item-info">
                  <div class="course-item-header">
                    <div class="course-item-name">{{ course.courseName }}</div>
                    <el-tag :type="getCourseStatusType(course.courseStatus)" size="small">
                      {{ getCourseStatusLabel(course.courseStatus) }}
                    </el-tag>
                  </div>
                  <div class="course-item-stats">
                    <span>{{ course.stSInfo.length }} 名学生</span>
                    <span>完成率 {{ getCompletionRate(course.stSInfo) }}%</span>
                  </div>
                </div>
                <el-icon v-if="selectedCourse?.courseName === course.courseName" class="active-icon">
                  <ArrowRight />
                </el-icon>
              </div>
            </div>
          </div>

          <!-- 右侧课程内容 -->
          <div class="course-content-area">
            <div v-if="selectedCourse" class="course-detail">
              <!-- 课程标题和操作 -->
              <div class="course-header">
                <div class="course-title-section">
                  <h2>{{ selectedCourse.courseName }}</h2>
                  <el-tag type="info" size="large">{{ selectedCourse.stSInfo.length }} 名学生</el-tag>
                  <el-tag :type="getCourseStatusType(selectedCourse.courseStatus)" size="large">
                    {{ getCourseStatusLabel(selectedCourse.courseStatus) }}
                  </el-tag>
                </div>
                <div class="course-header-actions">
                  <el-input
                    v-model="studentSearchKeyword"
                    placeholder="搜索学生姓名"
                    prefix-icon="Search"
                    clearable
                    size="default"
                    style="width: 200px;"
                    @clear="loadData"
                    @keyup.enter="loadData"
                  />
                  <el-button v-if="isCourseEnded" type="primary" @click="saveCourseCredits(selectedCourse)" :loading="saving">
                    <el-icon><Check /></el-icon>保存本课程
                  </el-button>
                </div>
              </div>

              <!-- 课程统计概览 -->
              <div class="course-overview">
                <el-card shadow="hover" class="stats-card">
                  <div class="stats-grid">
                    <div class="stat-item">
                      <span class="stat-label">学生总数</span>
                      <span class="stat-value">{{ selectedCourse.stSInfo.length }}</span>
                    </div>
                    <div class="stat-item">
                      <span class="stat-label">已录入成绩</span>
                      <span class="stat-value">{{ getSavedCount(selectedCourse.stSInfo) }}</span>
                    </div>
                    <div class="stat-item">
                      <span class="stat-label">平均总成绩</span>
                      <span class="stat-value">{{ getAverageCredit(selectedCourse.stSInfo) }}</span>
                    </div>
                    <div class="stat-item">
                      <span class="stat-label">完成率</span>
                      <span class="stat-value">{{ getCompletionRate(selectedCourse.stSInfo) }}%</span>
                    </div>
                    <div class="stat-item">
                      <span class="stat-label">通过率</span>
                      <span class="stat-value">{{ getPassRate(selectedCourse.stSInfo) }}%</span>
                    </div>
                  </div>
                </el-card>
              </div>

              <!-- 学生列表 -->
              <el-table 
                :data="filteredStudents" 
                size="default" 
                class="student-table"
                border
                :cell-style="{ 'text-align': 'center', 'vertical-align': 'middle' }"
                :header-cell-style="{ 'text-align': 'center', 'vertical-align': 'middle' }"
              >
                <el-table-column type="index" label="序号" width="80" />
                <el-table-column prop="studentName" label="学生姓名" width="140" />
                <el-table-column label="平时成绩" width="160">
                  <template #default="scope">
                    <el-input 
                      v-model="scope.row.ordinaryCredit" 
                      type="number" 
                      placeholder="0-100"
                      @input="calculateTotalCredit(scope.row)"
                      size="default"
                      class="credit-input"
                      :style="{ width: '100%' }"
                      :min="0"
                      :max="100"
                      step="0.5"
                      :disabled="!isCourseEnded"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="考试成绩" width="160">
                  <template #default="scope">
                    <el-input 
                      v-model="scope.row.examCredit" 
                      type="number" 
                      placeholder="0-100"
                      @input="calculateTotalCredit(scope.row)"
                      size="default"
                      class="credit-input"
                      :style="{ width: '100%' }"
                      :min="0"
                      :max="100"
                      step="0.5"
                      :disabled="!isCourseEnded"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="总成绩" width="140">
                  <template #default="scope">
                    <el-input 
                      v-model="scope.row.totalCredit" 
                      disabled 
                      size="default"
                      class="total-credit-input"
                      :style="{ width: '100%', textAlign: 'center' }"
                    />
                  </template>
                </el-table-column>
                <el-table-column label="状态" width="140">
                  <template #default="scope">
                    <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="default">
                      {{ scope.row.status === 1 ? '通过' : scope.row.status === 0 ? '未通过' : '待计算' }}
                    </el-tag>
                  </template>
                </el-table-column>
                <el-table-column label="操作" width="120">
                  <template #default="scope">
                    <el-button 
                      v-if="isCourseEnded"
                      size="default" 
                      type="primary" 
                      @click="saveStudentCredit(scope.row)"
                      :loading="savingStudents.includes(scope.row.id)"
                      class="save-button"
                      :style="{ width: '100%' }"
                    >
                      <el-icon><Check /></el-icon>保存
                    </el-button>
                    <el-tag v-else type="info" size="default" :style="{ width: '100%', textAlign: 'center' }">
                      课程未结束
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>
            </div>
            <div v-else class="empty-selection">
              <el-empty description="请从左侧选择一个课程查看详情" />
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 保存成功提示 -->
    <el-message
      v-if="saveSuccess"
      message="保存成功"
      type="success"
      :duration="2000"
      @close="saveSuccess = false"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getUserInfo } from '@/utils/userStorage.js'
import { Download, Search, User, Refresh, Document, Check, Timer, ArrowRight } from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import { ElMessage } from 'element-plus'
import { COURSE_STATUS } from '@/constant/module/type.js'

const user = ref(getUserInfo() || {})

// 课程数据
const coursesData = ref([])
const total = ref(0)
const loading = ref(false)
const saving = ref(false)
const saveSuccess = ref(false)
const savingStudents = ref([])
const selectedCourse = ref(null)
const courseSearchKeyword = ref('')
const studentSearchKeyword = ref('')

// 分页
const pagination = ref({
  current: 1,
  size: 10
})

// 数据概览
const overviewData = ref({
  totalCourses: 0,
  totalStudents: 0,
  pendingStudents: 0,
  savedStudents: 0
})

// 计算过滤后的课程数据
const filteredCourses = computed(() => {
  let result = [...coursesData.value]
  
  // 按课程名称过滤
  if (courseSearchKeyword.value) {
    result = result.filter(course => 
      course.courseName.toLowerCase().includes(courseSearchKeyword.value.toLowerCase())
    )
  }
  
  return result
})

// 计算过滤后的学生数据
const filteredStudents = computed(() => {
  if (!selectedCourse.value) return []
  
  let result = [...selectedCourse.value.stSInfo]
  
  // 按学生姓名过滤
  if (studentSearchKeyword.value) {
    result = result.filter(student => 
      student.studentName.toLowerCase().includes(studentSearchKeyword.value.toLowerCase())
    )
  }
  
  return result
})

// 计算课程是否已结课
const isCourseEnded = computed(() => {
  return selectedCourse.value && selectedCourse.value.courseStatus === COURSE_STATUS.ENDED.value
})

// 选择课程
const selectCourse = (course) => {
  selectedCourse.value = course
}

// 计算总学分和状态
const calculateTotalCredit = (student) => {
  // 转换为数字并验证
  const ordinaryCredit = parseFloat(student.ordinaryCredit)
  const examCredit = parseFloat(student.examCredit)
  
  if (!isNaN(ordinaryCredit) && !isNaN(examCredit) && ordinaryCredit >= 0 && ordinaryCredit <= 100 && examCredit >= 0 && examCredit <= 100) {
    // 计算总学分
    const totalCredit = (ordinaryCredit * 0.4 + examCredit * 0.6)
    student.totalCredit = totalCredit.toFixed(1)
    
    // 计算状态：总学分大于60为通过，否则为未通过
    student.status = totalCredit >= 60 ? 1 : 0
  } else {
    // 输入无效时清空总学分和状态
    student.totalCredit = null
    student.status = null
  }
}

// 保存单个学生学分
const saveStudentCredit = async (student) => {
  savingStudents.value.push(student.id)
  try {
    const response = await request.post('/c_select/updateStudentCredit', [{
      id: student.id,
      ordinaryCredit: student.ordinaryCredit,
      examCredit: student.examCredit,
      totalCredit: student.totalCredit,
      status: student.status
    }])
    
    if (response.code === '200') {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.msg || '保存失败，请重试')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    savingStudents.value = savingStudents.value.filter(id => id !== student.id)
  }
}

// 保存课程所有学生学分
const saveCourseCredits = async (course) => {
  saving.value = true
  try {
    const studentsData = course.stSInfo.map(student => ({
      id: student.id,
      ordinaryCredit: student.ordinaryCredit,
      examCredit: student.examCredit,
      totalCredit: student.totalCredit,
      status: student.status
    }))
    
    const response = await request.post('/c_select/updateStudentCredit', studentsData)
    
    if (response.code === '200') {
      ElMessage.success('保存成功')
    } else {
      ElMessage.error(response.msg || '保存失败，请重试')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败，请重试')
  } finally {
    saving.value = false
  }
}



// 获取已保存学生数
const getSavedCount = (students) => {
  return students.filter(student => student.totalCredit !== null && student.totalCredit !== undefined).length
}

// 获取平均学分
const getAverageCredit = (students) => {
  const validStudents = students.filter(student => student.totalCredit !== null && student.totalCredit !== undefined)
  if (validStudents.length === 0) return '0.0'
  const sum = validStudents.reduce((acc, student) => acc + parseFloat(student.totalCredit), 0)
  return (sum / validStudents.length).toFixed(1)
}

// 获取完成率
const getCompletionRate = (students) => {
  if (students.length === 0) return 0
  const savedCount = getSavedCount(students)
  return Math.round((savedCount / students.length) * 100)
}

// 获取通过率
const getPassRate = (students) => {
  if (students.length === 0) return 0
  const passedCount = students.filter(student => student.status === 1).length
  return Math.round((passedCount / students.length) * 100)
}

// 获取课程状态标签
const getCourseStatusLabel = (status) => {
  return COURSE_STATUS[Object.keys(COURSE_STATUS).find(key => COURSE_STATUS[key].value === status)]?.label || '未知状态'
}

// 获取课程状态标签类型
const getCourseStatusType = (status) => {
  switch (status) {
    case COURSE_STATUS.NORMAL.value:
      return 'info'
    case COURSE_STATUS.DISABLED.value:
      return 'warning'
    case COURSE_STATUS.ENDED.value:
      return 'success'
    default:
      return 'info'
  }
}

// 加载课程和学生数据
const loadData = async () => {
  loading.value = true
  try {
    const teacherId = user.value.id || 1
    
    const response = await request.post('/c_select/selectStudent', {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size,
      teacherId: teacherId,
      courseName: courseSearchKeyword.value || null,
      studentName: studentSearchKeyword.value || null
    })
    
    if (response.code === '200') {
        const data = response.data
        coursesData.value = data.list || []
        total.value = data.total || 0
        
        // 初始化默认选中第一个课程
        if (coursesData.value.length > 0) {
          selectedCourse.value = coursesData.value[0]
        }
        
        // 计算概览数据
        calculateOverviewData()
    } else {
      console.error('获取数据失败:', response.msg || '未知错误')
    }
  } catch (error) {
    console.error('请求失败:', error)
  } finally {
    loading.value = false
  }
}

// 计算概览数据
const calculateOverviewData = () => {
  const courses = coursesData.value
  const allStudents = courses.flatMap(course => course.stSInfo)
  const savedStudents = allStudents.filter(student => student.totalCredit !== null && student.totalCredit !== undefined)
  
  overviewData.value = {
    totalCourses: courses.length,
    totalStudents: allStudents.length,
    pendingStudents: allStudents.length - savedStudents.length,
    savedStudents: savedStudents.length
  }
}

// 导出学生列表
const exportStudents = async () => {
  try {
    const teacherId = user.value.teacherId || 1
    
    const response = await request.post('/c_select/exportStudents', {
      teacherId: teacherId
    }, {
      responseType: 'blob'
    })
    
    // 创建下载链接
    const url = window.URL.createObjectURL(new Blob([response]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `学生列表_${new Date().getTime()}.xlsx`)
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败，请重试')
  }
}

// 刷新数据
const refreshData = () => {
  loadData()
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.value.size = size
  loadData()
}

const handleCurrentChange = (current) => {
  pagination.value.current = current
  loadData()
}

// 初始化加载数据
onMounted(() => {
  loadData()
})
</script>

<style scoped>
/* 全局容器 */
.teacher-students-container {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  min-height: 100vh;
  background: #f5f7fa;
}

/* 页面头部 */
.page-header {
  margin-bottom: 30px;
  background: #fff;
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #e8eaed;
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.header-info {
  flex: 1;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 10px 0;
  color: #1a1a1a;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

/* .teacher-info {
  font-size: 16px;
  color: #666;
  margin: 0;
}

.teacher-id {
  font-size: 14px;
  color: #999;
  margin-left: 8px;
} */

.header-actions {
  display: flex;
  gap: 12px;
}

.header-actions .el-button {
  border-radius: 10px;
  font-weight: 600;
  padding: 10px 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.header-actions .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 数据概览 */
.overview-section {
  margin-bottom: 20px;
}

.overview-card {
  border-radius: 16px;
  border: none;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;
}

.overview-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.overview-card.total-courses {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.overview-card.total-students {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.overview-card.pending-students {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.overview-card.saved-students {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.card-content {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 10px;
  color: #fff;
}

.card-icon {
  font-size: 32px;
  opacity: 0.9;
}

.card-info {
  flex: 1;
}

.card-value {
  font-size: 36px;
  font-weight: 700;
  margin: 0;
  line-height: 1;
}

.card-label {
  font-size: 14px;
  opacity: 0.9;
  margin: 8px 0 0 0;
}

/* 加载状态 */
.loading-container {
  background: #fff;
  padding: 60px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #e8eaed;
}

/* 空状态 */
.empty-container {
  background: #fff;
  padding: 80px;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #e8eaed;
  display: flex;
  justify-content: center;
  align-items: center;
}

/* 课程列表 */
.courses-section {
  margin-bottom: 30px;
}

.courses-list {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #e8eaed;
  overflow: hidden;
}

/* 学生表格 */
.student-table {
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
}

.student-table :deep(.el-table__header) {
  background: #f8f9fa;
}

.student-table :deep(.el-table th) {
  background: transparent;
  color: #1a1a1a;
  font-weight: 600;
  font-size: 14px;
  border-bottom: 2px solid #e8eaed;
}

.student-table :deep(.el-table td) {
  border-bottom: 1px solid #f0f0f0;
  font-size: 14px;
  color: #606266;
}

.student-table :deep(.el-table__row:hover) {
  background: #f5f7fa;
}

/* 输入框和选择器样式优化 */
.credit-input :deep(.el-input__inner) {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.credit-input :deep(.el-input__inner):hover {
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.1);
}

.total-credit-input :deep(.el-input__inner) {
  background-color: #f8f9fa;
  border-color: #e6e8eb;
  cursor: not-allowed;
  border-radius: 6px;
}

/* 按钮样式优化 */
.save-button :deep(.el-button) {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.save-button :deep(.el-button):hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(52, 152, 219, 0.4);
}

/* 课程布局 */
.course-layout {
  display: flex;
  gap: 20px;
  min-height: 600px;
}

/* 左侧课程导航 */
.course-sidebar {
  width: 280px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
  border: 1px solid #e6e8eb;
}

.sidebar-header {
  padding: 20px;
  border-bottom: 1px solid #e6e8eb;
  background: linear-gradient(135deg, #3498db 0%, #2980b9 100%);
  color: white;
  border-radius: 8px 8px 0 0;
}

.sidebar-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
}

.course-count {
  font-size: 12px;
  opacity: 0.9;
}

.sidebar-search {
  padding: 16px;
  border-bottom: 1px solid #e6e8eb;
}

.course-menu {
  flex: 1;
  overflow-y: auto;
  padding: 12px 0;
}

.course-menu-item {
  padding: 16px 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  border-left: 3px solid transparent;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 12px;
}

.course-menu-item:hover {
  background-color: #f8f9fa;
  border-left-color: #3498db;
}

.course-menu-item.active {
  background-color: #eef2f7;
  border-left-color: #3498db;
}

.course-item-info {
  flex: 1;
}

.course-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.course-item-name {
  font-size: 15px;
  font-weight: 500;
  color: #2c3e50;
  flex: 1;
  margin-right: 8px;
}

.course-item-header .el-tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 12px;
  white-space: nowrap;
}

.course-item-stats {
  font-size: 12px;
  color: #7f8c8d;
  display: flex;
  gap: 12px;
}

.active-icon {
  color: #3498db;
  font-size: 18px;
}

/* 右侧课程内容 */
.course-content-area {
  flex: 1;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  padding: 24px;
  border: 1px solid #e6e8eb;
  overflow-y: auto;
}

.course-detail {
  animation: fadeIn 0.3s ease-out;
}

.empty-selection {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 400px;
}

/* 课程标题和操作 */
.course-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 20px;
  border-bottom: 2px solid #e6e8eb;
}

.course-title-section {
  display: flex;
  align-items: center;
  gap: 16px;
}

.course-title-section h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color: #2c3e50;
}

.course-header-actions {
  display: flex;
  gap: 12px;
}

/* 课程统计概览 */
.course-overview {
  margin-bottom: 24px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 16px;
}

.stats-grid .stat-item {
  text-align: center;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e6e8eb;
  transition: all 0.3s ease;
}

.stats-grid .stat-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border-color: #3498db;
}

.stats-grid .stat-label {
  display: block;
  font-size: 12px;
  color: #666;
  margin-bottom: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stats-grid .stat-value {
  display: block;
  font-size: 20px;
  font-weight: 600;
  color: #2c3e50;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .teacher-students-container {
    padding: 10px;
  }
  
  .page-header {
    padding: 20px;
  }
  
  .header-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .page-title {
    font-size: 24px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .overview-card {
    margin-bottom: 20px;
  }
  
  .loading-container {
    padding: 40px;
  }
  
  .empty-container {
    padding: 60px 20px;
  }
  
  /* 课程布局响应式 */
  .course-layout {
    flex-direction: column;
  }
  
  .course-sidebar {
    width: 100%;
    max-height: 300px;
  }
  
  .course-content-area {
    padding: 16px;
  }
  
  .course-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .course-title-section {
    width: 100%;
    flex-direction: column;
    align-items: flex-start;
  }
  
  .course-header-actions {
    width: 100%;
    justify-content: flex-start;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
  transition: background 0.3s ease;
}

::-webkit-scrollbar-thumb:hover {
  background: #3498db;
}
</style>