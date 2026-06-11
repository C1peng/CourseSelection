<template>
  <div class="container">
    <!-- 顶部操作栏 -->
    <div class="header-card">
      <div class="header-title">
        <h2><el-icon><User /></el-icon>学生分数管理</h2>
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
                    <div class="course-tags">
                      <el-tag v-if="course.teacherName" size="small" type="info" class="teacher-tag">
                        {{ course.teacherName }}
                      </el-tag>
                      <el-tag :type="getCourseStatusType(course.courseStatus)" size="small" class="status-tag">
                        {{ getCourseStatusLabel(course.courseStatus) }}
                      </el-tag>
                    </div>
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
                  <div class="course-title-wrapper">
                    <h2>{{ selectedCourse.courseName }}</h2>
                    <el-tag v-if="selectedCourse.teacherName" size="large" type="info" class="teacher-tag-large">
                      {{ selectedCourse.teacherName }}
                    </el-tag>
                    <el-tag :type="getCourseStatusType(selectedCourse.courseStatus)" size="large" class="status-tag-large">
                      {{ getCourseStatusLabel(selectedCourse.courseStatus) }}
                    </el-tag>
                  </div>
                  <el-tag type="info" size="large" class="student-count-tag">
                    {{ selectedCourse.stSInfo.length }} 名学生
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
                  <el-button type="primary" @click="saveCourseCredits(selectedCourse)" :loading="saving">
                    <el-icon><Check /></el-icon>保存本课程
                  </el-button>
                  <el-button type="danger" @click="handleBatchDelete" :disabled="selectedStudents.length === 0">
                    <el-icon><Delete /></el-icon>删除选中学生
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
                      <span class="stat-label">已录入学分</span>
                      <span class="stat-value">{{ getSavedCount(selectedCourse.stSInfo) }}</span>
                    </div>
                    <div class="stat-item">
                      <span class="stat-label">平均总学分</span>
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
                :header-cell-style="headerCellStyle"
                :cell-style="{ 'text-align': 'center', 'vertical-align': 'middle' }"
                @selection-change="handleSelectionChange"
              >
                <el-table-column type="selection" width="40" />
                <el-table-column type="index" label="序号" width="60" />
                <el-table-column prop="studentName" label="学生姓名" width="140" />
                <el-table-column label="平时学分" width="160">
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
                    />
                  </template>
                </el-table-column>
                <el-table-column label="考试学分" width="160">
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
                    />
                  </template>
                </el-table-column>
                <el-table-column label="总学分" width="140">
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
                <el-table-column label="操作" width="200" fixed="right">
                  <template #default="scope">
                    <div class="table-actions">
                      <el-button 
                        size="small" 
                        type="primary" 
                        @click="saveStudentCredit(scope.row)"
                        :loading="savingStudents.includes(scope.row.id)"
                        class="save-button"
                      >
                        <el-icon><Check /></el-icon>保存
                      </el-button>
                      <el-button 
                        size="small" 
                        type="danger" 
                        @click="handleDeleteStudent(scope.row.id)"
                      >
                        <el-icon><Delete /></el-icon>删除
                      </el-button>
                    </div>
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getUserInfo } from '@/utils/userStorage.js'
import { Download, Search, User, Refresh, Document, Check, Timer, ArrowRight, Delete } from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { COURSE_STATUS } from '@/constant/module/type.js'

const user = ref(getUserInfo() || {})

// 表格头部样式
const headerCellStyle = {
  backgroundColor: '#fafafa',
  fontWeight: '600',
  color: '#606266'
};

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

// 选中的学生
const selectedStudents = ref([])

// 分页
const pagination = ref({
  current: 1,
  size: 10
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

// 删除单个学生
const handleDeleteStudent = async (id) => {
  try {
    await ElMessageBox.confirm(
      '删除后数据无法恢复，您确定删除吗？',
      '删除确认',
      { type: 'warning' }
    )

    const res = await request.delete('/c_select/deleteStudent', { data: [id] })
    if (res.code === '200') {
      ElMessage.success('删除成功')
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    if (error.message && !error.message.includes('cancel')) {
      ElMessage.error('删除失败')
      console.error('删除错误:', error)
    }
  }
}

// 处理选择变化
const handleSelectionChange = (val) => {
  selectedStudents.value = val
}

// 批量删除选中学生
const handleBatchDelete = async () => {
  if (selectedStudents.value.length === 0) {
    ElMessage.warning('请选择要删除的学生')
    return
  }

  try {
    await ElMessageBox.confirm(
      `确定删除选中的 ${selectedStudents.value.length} 名学生记录吗？`,
      '删除确认',
      { type: 'warning' }
    )

    const ids = selectedStudents.value.map(student => student.id)
    const res = await request.delete('/c_select/deleteStudent', { data: ids })
    if (res.code === '200') {
      ElMessage.success('删除成功')
      selectedStudents.value = []
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    if (error.message && !error.message.includes('cancel')) {
      ElMessage.error('删除失败')
      console.error('删除错误:', error)
    }
  }
}

// 删除课程所有学生
const handleDeleteCourseStudents = async (course) => {
  try {
    await ElMessageBox.confirm(
      `确定删除课程「${course.courseName}」的所有学生记录吗？`,
      '删除确认',
      { type: 'warning' }
    )

    const ids = course.stSInfo.map(student => student.id)
    const res = await request.delete('/c_select/deleteStudent', { data: ids })
    if (res.code === '200') {
      ElMessage.success('删除成功')
      selectedStudents.value = []
      loadData()
    } else {
      ElMessage.error(res.msg)
    }
  } catch (error) {
    if (error.message && !error.message.includes('cancel')) {
      ElMessage.error('删除失败')
      console.error('删除错误:', error)
    }
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
    const response = await request.post('/c_select/selectStudent', {
      pageNum: pagination.value.current,
      pageSize: pagination.value.size,
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
    } else {
      console.error('获取数据失败:', response.msg || '未知错误')
    }
  } catch (error) {
    console.error('请求失败:', error)
  } finally {
    loading.value = false
  }
}

// 导出学生列表
const exportStudents = async () => {
  try {
    const response = await request.post('/c_select/exportStudents', {}, {
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
/* 导入公共列表页样式 */
@import "@/assets/css/manager/list.css";

/* 课程布局 */
.course-layout {
  display: flex;
  gap: var(--space-lg);
  min-height: 600px;
}

/* 左侧课程导航 */
.course-sidebar {
  width: 280px;
  background: var(--bg-white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
  border: 1px solid var(--border-color);
}

.sidebar-header {
  padding: var(--space-lg);
  border-bottom: 1px solid var(--border-color);
  background: var(--manager-primary-gradient);
  color: white;
  border-radius: var(--radius-md) var(--radius-md) 0 0;
}

.sidebar-header h3 {
  margin: 0 0 var(--space-xs) 0;
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
}

.course-count {
  font-size: var(--font-size-xs);
  opacity: 0.9;
}

.sidebar-search {
  padding: var(--space-md);
  border-bottom: 1px solid var(--border-color);
}

.course-menu {
  flex: 1;
  overflow-y: auto;
  padding: var(--space-sm) 0;
}

.course-menu-item {
  padding: var(--space-md) var(--space-lg);
  cursor: pointer;
  transition: all var(--transition-normal);
  border-left: 3px solid transparent;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--space-sm);
}

.course-menu-item:hover {
  background-color: var(--bg-gray);
  border-left-color: var(--manager-primary);
}

.course-menu-item.active {
  background-color: rgba(5, 150, 105, 0.1);
  border-left-color: var(--manager-primary);
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
  font-weight: var(--font-weight-medium);
  color: var(--text-primary);
  flex: 1;
  margin-right: var(--space-sm);
}

.course-tags {
  display: flex;
  gap: var(--space-sm);
  align-items: center;
}

.teacher-tag {
  font-size: 11px;
  padding: 2px var(--space-sm);
  border-radius: 12px;
  background-color: rgba(5, 150, 105, 0.1);
  color: var(--manager-primary);
  border: 1px solid rgba(5, 150, 105, 0.2);
  white-space: nowrap;
}

.status-tag {
  font-size: 11px;
  padding: 2px var(--space-sm);
  border-radius: 12px;
  white-space: nowrap;
}

.course-item-stats {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  display: flex;
  gap: var(--space-md);
}

.active-icon {
  color: var(--manager-primary);
  font-size: 18px;
}

/* 右侧课程内容 */
.course-content-area {
  flex: 1;
  background: var(--bg-white);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
  padding: var(--space-xl);
  border: 1px solid var(--border-color);
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
  margin-bottom: var(--space-lg);
  padding-bottom: var(--space-lg);
  border-bottom: 2px solid var(--border-color);
}

.course-title-section {
  display: flex;
  align-items: center;
  gap: var(--space-md);
  flex-wrap: wrap;
}

.course-title-wrapper {
  display: flex;
  align-items: center;
  gap: var(--space-sm);
  flex-wrap: wrap;
}

.course-title-section h2 {
  margin: 0;
  font-size: 24px;
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

.teacher-tag-large {
  font-size: var(--font-size-md);
  padding: var(--space-xs) var(--space-sm);
  border-radius: 16px;
  background-color: rgba(5, 150, 105, 0.1);
  color: var(--manager-primary);
  border: 1px solid rgba(5, 150, 105, 0.2);
  font-weight: var(--font-weight-medium);
  margin-right: var(--space-sm);
}

.status-tag-large {
  font-size: var(--font-size-md);
  padding: var(--space-xs) var(--space-sm);
  border-radius: 16px;
  font-weight: var(--font-weight-medium);
  margin-right: var(--space-sm);
}

.student-count-tag {
  font-size: var(--font-size-md);
  padding: var(--space-xs) var(--space-sm);
  border-radius: 16px;
  background-color: rgba(103, 58, 183, 0.1);
  color: #7b1fa2;
  border: 1px solid rgba(103, 58, 183, 0.2);
  font-weight: var(--font-weight-medium);
}

.course-header-actions {
  display: flex;
  gap: var(--space-sm);
}

/* 课程统计概览 */
.course-overview {
  margin-bottom: var(--space-lg);
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: var(--space-md);
}

.stats-grid .stat-item {
  text-align: center;
  padding: var(--space-md);
  background-color: var(--bg-gray);
  border-radius: var(--radius-md);
  border: 1px solid var(--border-color);
  transition: all var(--transition-normal);
}

.stats-grid .stat-item:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
  border-color: var(--manager-primary);
}

.stats-grid .stat-label {
  display: block;
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  margin-bottom: var(--space-sm);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stats-grid .stat-value {
  display: block;
  font-size: 20px;
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

/* 表格操作按钮样式 */
.table-actions {
  display: flex;
  justify-content: center;
  gap: var(--space-sm);
}

/* 输入框和选择器样式优化 */
.credit-input :deep(.el-input__inner) {
  border-radius: var(--radius-sm);
  transition: all var(--transition-normal);
}

.credit-input :deep(.el-input__inner):hover {
  border-color: var(--manager-primary);
  box-shadow: 0 0 0 2px rgba(5, 150, 105, 0.1);
}

.total-credit-input :deep(.el-input__inner) {
  background-color: var(--bg-gray);
  border-color: var(--border-color);
  cursor: not-allowed;
  border-radius: var(--radius-sm);
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

/* 响应式设计 */
@media (max-width: 768px) {
  .course-layout {
    flex-direction: column;
  }
  
  .course-sidebar {
    width: 100%;
    max-height: 300px;
  }
  
  .course-content-area {
    padding: var(--space-md);
  }
  
  .course-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-sm);
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
</style>
