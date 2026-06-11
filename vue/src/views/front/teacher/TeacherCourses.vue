<template>
  <div class="courses-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>我的授课</h1>
      <el-button type="primary" @click="goToTeacherCourses">
        <el-icon><Plus /></el-icon>开设课程
      </el-button>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.courseName" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-form-item >
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
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="search">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 课程列表 -->
    <div class="courses-list">
      <el-empty v-if="courses.length === 0" description="暂无课程" />
      <el-table v-else :data="courses" style="width: 100%" class="course-table">
        <el-table-column prop="courseName" label="课程名称" align="center" width="150" />
        <el-table-column label="学生人数" align="center" width="100">
          <template #default="scope">{{ scope.row.studentCount }}</template>
        </el-table-column>
        <el-table-column prop="number" label="总人数" align="center" width="100"/>

        <el-table-column prop="schedule" label="上课时间" align="center" width="150" />
        <el-table-column prop="place" label="地点" align="center" width="150" />
        <el-table-column label="课程节数" align="center" width="100">
          <template #default="scope">{{ scope.row.courseTotal || scope.row.sectionCount || '' }}</template>
        </el-table-column>
        <el-table-column label="周次类型" align="center" width="100">
          <template #default="scope">
            <el-tag :type="getCycleTypeTagType(scope.row.cycle)" size="small" class="cycle-tag">
              {{ getCycleTypeLabel(scope.row.cycle) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" align="center" width="120" />
        <el-table-column label="状态" align="center" width="100">
          <template #default="scope">
            <el-tag :type="getCourseStatusType(scope.row.statusText)" size="small">
              {{ scope.row.statusText === 'ongoing' ? '进行中' : scope.row.statusText === 'completed' ? '已结束' : '未开始' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="250">
          <template #default="scope">
            <div class="table-actions">
              <el-button type="primary" size="small" :icon="Edit" @click="editCourse(scope.row.id)" title="编辑" />
              <el-button type="danger" size="small" :icon="Delete" @click="deleteCourse(scope.row.id)" title="删除" />
              <el-button v-if="scope.row.status === 1" type="warning" size="small" :icon="Check" @click="endCourse(scope.row.id)" title="结课" />
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 分页 -->
    <div v-if="courses.length > 0" class="pagination">
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>

    <!-- 课程编辑对话框 -->
    <el-dialog
      :title="'编辑课程'"
      v-model="dialogVisible"
      width="60%"
      destroy-on-close
      center
      :close-on-click-modal="false"
    >
      <!-- 使用 CSDetailsForm 组件 -->
      <CSDetailsForm
        ref="detailsFormRef"
        :initial-data="selectedRow"
        :is-edit="true"
        :lock-teacher-id="true"
      />
      <template #footer>
        <span>
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="isSubmitting">保存</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { getUserInfo } from '@/utils/userStorage.js'
import router from '@/router'
import request from '@/utils/request.js'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Calendar, Edit, Delete, Check } from '@element-plus/icons-vue'
import { CYCLE_TYPE, WEEKDAY } from '@/constant/module/type.js'
import CSDetailsForm from '@/views/communal/CSDetailsForm.vue'

const user = ref(getUserInfo() || {})

// 课程数据
const courses = ref([])
const total = ref(0)

// 表单引用
const detailsFormRef = ref(null)

// 对话框相关变量
const dialogVisible = ref(false)
const isSubmitting = ref(false)
const selectedRow = ref(null)

// 搜索表单
const searchForm = ref({
  courseName: '',
  semester: ''
})

// 分页
const pagination = ref({
  current: 1,
  size: 10
})

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

// 获取课程状态标签类型
const getCourseStatusType = (status) => {
  switch (status) {
    case 'ongoing':
      return 'primary'
    case 'completed':
      return 'success'
    case 'upcoming':
      return 'info'
    default:
      return 'info'
  }
}

// 处理课程数据的通用函数
const processCourseData = (course) => {
  return {
    id: course.id,
    courseId: course.courseId,
    courseName: course.courseName,
    description: course.description,
    teacherId: course.teacherId,
    teacherName: course.teacherName,
    studentCount: course.currentStudents || course.studentCount || 0,
    number: course.number,
    schedule: course.schedule || (course.weekTime ? formatWeekTime(course.weekTime) : ''),
    place: course.place,
    weekTime: course.weekTime || null,
    courseTotal: course.courseTotal || course.sectionCount || 0,
    cycle: course.cycle,
    semester: course.semester,
    creditsType: course.creditsType,
    // 保留原始的数字状态，用于表单编辑
    status: course.status,
    // 将后端的数字状态转换为前端的字符串状态，用于显示
    statusText: course.status === 1 ? 'ongoing' : course.status === 2 ? 'completed' : 'upcoming'
  }
}

// 处理API响应的通用函数
const handleApiResponse = (res) => {
  if (res.code === '200') {
    // 处理后端返回的数据，确保字段匹配
    if (Array.isArray(res.data)) {
      courses.value = res.data.map(processCourseData)
      total.value = courses.value.length
    } else if (res.data && Array.isArray(res.data.list)) {
      // 处理分页接口返回的格式 {list: [...], total: ...}
      courses.value = res.data.list.map(processCourseData)
      total.value = res.data.total
    } else {
      courses.value = []
      total.value = 0
    }
  } else {
    ElMessage.error('获取课程列表失败')
    courses.value = []
    total.value = 0
  }
}

// 处理API错误的通用函数
const handleApiError = (error) => {
  ElMessage.error('网络异常，请稍后重试')
  courses.value = []
  total.value = 0
}

// 加载课程数据
const loadCourses = (isSearch = false) => {
  if (!user.value.id) return
  
  // 构建通用参数
  const commonParams = {
    pageNum: pagination.value.current,
    pageSize: pagination.value.size,
    courseInfo: searchForm.value.courseName
  }
  
  // 只有在搜索时才添加学期参数
  if (isSearch) {
    commonParams.semester = getSemesterString()
  }
  
  // 教师角色使用 /teacher/selectDetailInfo 接口
  const params = {
    ...commonParams,
    teacherId: user.value.id
  }
  
  request.post('/teacher/selectDetailInfo', params)
    .then(handleApiResponse)
    .catch(handleApiError)
}

// 解析weekTime函数，使用WEEKDAY常量
const formatWeekTime = (weekTime) => {
  if (!weekTime) return '';
  
  // 将数字转换为字符串，确保格式统一
  const timeStr = weekTime.toString();
  
  const weekDay = parseInt(timeStr[0]);
  const startSection = parseInt(timeStr.substring(1, 3));
  const endSection = parseInt(timeStr.substring(3));
  
  // 只处理1-5的星期值
  if (weekDay < 1 || weekDay > 5) return '';
  
  // 使用WEEKDAY常量的label属性获取完整的星期几名称
  const weekdayMap = {
    1: WEEKDAY.MONDAY.label,
    2: WEEKDAY.TUESDAY.label,
    3: WEEKDAY.WEDNESDAY.label,
    4: WEEKDAY.THURSDAY.label,
    5: WEEKDAY.FRIDAY.label
  };
  
  return `星期${weekdayMap[weekDay]} ${startSection}-${endSection}节`;
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

// 搜索课程
const search = () => {
  // 重置页码为1
  pagination.value.current = 1
  // 调用loadCourses从API获取数据，传递isSearch=true参数
  loadCourses(true)
}

// 重置搜索
const reset = () => {
  searchForm.value = {
    courseName: '',
    semester: ''
  }
  // 重置页码为1
  pagination.value.current = 1
  // 调用loadCourses从API获取数据，不传递isSearch参数，加载所有学期的数据
  loadCourses()
}

// 跳转到教师课程页面
const goToTeacherCourses = () => {
  router.push('/teacher/all-courses')
}

// 编辑课程
const editCourse = (courseId) => {
  const course = courses.value.find(c => c.id === courseId)
  if (course) {
    // 复制课程数据
    selectedRow.value = { ...course }
    
    // 确保教师ID为当前用户ID
    if (user.value.id) {
      selectedRow.value.teacherId = user.value.id
    }
    
    dialogVisible.value = true
  }
}

// 提交表单
const submitForm = async () => {
  try {
    isSubmitting.value = true
    
    if (!detailsFormRef.value) {
      ElMessage.error('表单组件未初始化')
      return
    }
    
    // 获取表单数据
    const formData = detailsFormRef.value.getFormData()
    
    // 设置教师ID
    formData.teacherId = user.value.id || formData.teacherId
    
    const url = '/c_select/updateDetails'
    const method = 'post'
    
    const res = await request[method](url, formData)
    
    if (res.code === '200') {
      dialogVisible.value = false
      ElMessage.success('编辑成功')
      // 重新加载课程数据
      await loadCourses()
    } else {
      ElMessage.error(res.msg || '编辑失败')
    }
  } catch (error) {
    ElMessage.error('编辑失败')
    console.error('表单提交错误:', error)
  } finally {
    isSubmitting.value = false
  }
}

// 删除课程
const deleteCourse = (courseId) => {
  // 显示确认对话框
  ElMessageBox.confirm('确定要删除这门课程吗？删除后不可恢复。', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 调用API删除课程，将单个id包装成List格式
      const res = await request.delete('/c_select/delete', {
        data: [courseId]
      })
      
      if (res.code === '200') {
        // 删除成功，从本地数组中移除
        const index = courses.value.findIndex(c => c.id === courseId)
        if (index > -1) {
          courses.value.splice(index, 1)
          total.value = courses.value.length
        }
        ElMessage.success('删除成功')
      } else {
        ElMessage.error(res.msg || '删除失败')
      }
    } catch (error) {
      ElMessage.error('网络异常，请稍后重试')
      console.error('删除课程错误:', error)
    }
  }).catch(() => {
    // 取消删除
    ElMessage.info('已取消删除')
  })
}

// 结课
const endCourse = (courseId) => {
  // 显示确认对话框
  ElMessageBox.confirm('确定要结课吗？结课后课程状态将变为已结束。', '结课确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      // 调用API更新课程状态
      const res = await request.post('/c_select/updateStatus', {
        id: courseId,
        status: 2
      })
      
      if (res.code === '200') {
        // 更新成功，刷新课程列表
        await loadCourses()
        ElMessage.success('结课成功')
      } else {
        ElMessage.error(res.msg || '结课失败')
      }
    } catch (error) {
      ElMessage.error('网络异常，请稍后重试')
      console.error('结课错误:', error)
    }
  }).catch(() => {
    // 取消结课
    ElMessage.info('已取消结课')
  })
}

// 分页处理
const handleSizeChange = (size) => {
  pagination.value.size = size
  // 调用loadCourses从API获取数据，不传递isSearch参数，加载所有学期的数据
  loadCourses()
}

const handleCurrentChange = (current) => {
  pagination.value.current = current
  // 调用loadCourses从API获取数据，不传递isSearch参数，加载所有学期的数据
  loadCourses()
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
.courses-container {
  padding: 0;
  max-width: 1400px;
  margin: 0 auto;
}

/* 表格操作按钮样式 */
.table-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 24px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 16px;
  color: #fff;
  box-shadow: 0 8px 24px rgba(102, 126, 234, 0.25);
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  margin: 0;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.filter-section {
  background: #fff;
  padding: 24px;
  border-radius: 16px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid #e8eaed;
}

.search-form {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.search-form :deep(.el-form-item) {
  margin-bottom: 0;
}

/* 学期选择器样式 */
.semester-selector-modern {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #f8f9fa;
  padding: 8px 16px;
  border-radius: 10px;
  border: 1px solid #e8eaed;
  flex-wrap: nowrap;
  flex-shrink: 0;
}

.semester-selector-modern .el-icon {
  color: #667eea;
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
  color: #909399;
  margin: 0 4px;
  flex-shrink: 0;
  font-weight: 500;
}

.semester-selector-modern .year-label {
  color: #606266;
  font-size: 13px;
  margin-right: 8px;
  flex-shrink: 0;
  white-space: nowrap;
}

.semester-selector-modern :deep(.el-select) {
  width: 100px;
  min-width: 100px;
  flex-shrink: 0;
}

.courses-list {
  background: #fff;
  border-radius: 16px;
  border: 1px solid #e8eaed;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  margin-bottom: 24px;
}

.course-table {
  border-radius: 16px;
  overflow: hidden;
}

.course-table :deep(.el-table__header-wrapper) {
  background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
}

.course-table :deep(.el-table__header th) {
  background: transparent;
  font-weight: 600;
  color: #1a1a1a;
  border-bottom: 1px solid #e8eaed;
}

.course-table :deep(.el-table__body tr:hover) {
  background: #f8f9fa;
}

.course-table :deep(.el-table__body td) {
  border-bottom: 1px solid #f0f0f0;
}

.course-table :deep(.el-button) {
  border-radius: 8px;
  font-weight: 500;
  margin-right: 8px;
}

.course-table :deep(.el-button:last-child) {
  margin-right: 0;
}

/* 周次类型标签样式 */
.cycle-tag {
  border-radius: 999px;
  font-size: 12px;
  padding: 2px 8px;
}

.pagination {
  display: flex;
  justify-content: center;
  margin-top: 32px;
  padding: 24px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .course-table :deep(.el-button) {
    margin-right: 4px;
    margin-bottom: 4px;
  }
  
  .semester-selector-modern {
    flex-wrap: wrap;
    gap: 6px;
    padding: 8px 12px;
  }
  
  .semester-selector-modern :deep(.el-input-number) {
    width: 80px !important;
    min-width: 80px;
  }
}

@media (max-width: 768px) {
  .courses-container {
    padding: 0;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
    padding: 20px;
  }
  
  .page-header h1 {
    font-size: 24px;
  }
  
  .filter-section {
    padding: 20px;
  }
  
  .search-form {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .semester-selector-modern {
    flex: 1;
    min-width: 280px;
    flex-wrap: wrap;
    gap: 6px;
    padding: 8px 12px;
    margin-right: 0;
  }
  
  .semester-selector-modern :deep(.el-input-number) {
    width: 75px !important;
    min-width: 75px;
    flex: 0 0 auto;
  }
  
  .semester-selector-modern :deep(.el-select) {
    width: 90px;
    min-width: 90px;
    flex: 0 0 auto;
  }
  
  .courses-list {
    overflow-x: auto;
  }
  
  .course-table {
    min-width: 800px;
  }
  
  .course-table :deep(.el-button) {
    margin-right: 4px;
    margin-bottom: 4px;
    font-size: 12px;
    padding: 4px 8px;
  }
}
</style>