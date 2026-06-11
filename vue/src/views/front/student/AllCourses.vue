<template>
  <div class="student-courses-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>全部课程</h1>
      <div class="semester-display">
        <div class="semester-info">
          <el-icon><Calendar /></el-icon>
          <span>{{ currentSemester }}</span>
        </div>
      </div>
    </div>

    <!-- 筛选和搜索 -->
    <div class="filter-section">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="课程名称">
          <el-input v-model="searchForm.courseName" placeholder="请输入课程名称" clearable />
        </el-form-item>
        <el-col :span="4">
          <el-select v-model="searchForm.creditsType" placeholder="请选择学分类型" clearable>
            <el-option
                v-for="(item) in typeEnum.CREDITTYPE"
                :key="item.value"
                :label="item.label"
                :value="item.value"
            ></el-option>
          </el-select>
        </el-col>
        <el-form-item>
          <el-button type="primary" @click="search">
            <el-icon><Search /></el-icon>搜索
          </el-button>
          <el-button @click="reset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 课程列表表格 -->
    <div class="courses-table-container">
      <h2 class="panel-title">课程列表</h2>
      <el-empty v-if="courses.length === 0" description="暂无课程" />
      <div v-else>
        <el-table 
          v-loading="loading"
          stripe 
          :data="courses"
          :header-cell-style="headerCellStyle" 
          class="courses-table"
          style="width: 100%"
        >
          <el-table-column prop="courseName" label="课程名称" align="center" min-width="200" />
          <el-table-column prop="credits" label="学分" align="center" width="80" />
          <el-table-column prop="creditsType" label="学分类型" align="center" width="120">
            <template v-slot="scope">
              {{ getCreditTypeLabel(scope.row.creditsType) }}
            </template>
          </el-table-column>
          <el-table-column label="班次数量" align="center" width="80">
            <template v-slot="scope">
              {{ scope.row.csDetails.length }}
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="120">
            <template v-slot="scope">
              <el-button size="small" type="primary" @click="openCourseDetails(scope.row)">
                查看班次
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页 -->
        <div class="pagination-container">
          <el-pagination 
            @size-change="handleSizeChange" 
            @current-change="handleCurrentChange"
            v-model:current-page="pagination.current"
            v-model:page-size="pagination.size" 
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper" 
            :total="total" 
          />
        </div>
      </div>
    </div>

    <!-- 课程班次详情弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="`${selectedCourse?.courseName || ''} - 班次详情`"
      width="90%"
      top="5vh"
    >
      <div v-if="selectedCourse">
        <el-table 
          :data="selectedCourse.csDetails"
          :header-cell-style="headerCellStyle" 
          class="details-table"
          style="width: 100%"
        >
          <el-table-column prop="id" label="序号" align="center" width="80" />
          <el-table-column prop="teacherName" label="教师姓名" align="center" width="120" />
          <el-table-column prop="place" label="地点" align="center" width="100">
            <template v-slot="scope">
              {{ scope.row.place || '未设置' }}
            </template>
          </el-table-column>
          <el-table-column prop="courseTotal" label="课程节数" align="center" width="100" />
          <el-table-column prop="cycle" label="周次类型" align="center" width="100">
            <template v-slot="scope">
              <el-tag :type="getCycleTypeTagType(scope.row.cycle)" class="cycle-tag">
                {{ getCycleTypeLabel(scope.row.cycle) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="上课时间" align="center" min-width="120">
            <template v-slot="scope">
              {{ scope.row.weekTime ? formatWeekTime(scope.row.weekTime) : '未设置' }}
            </template>
          </el-table-column>
          <el-table-column label="已选/上限" align="center" width="100">
            <template v-slot="scope">
              {{ scope.row.currentStudents }} / {{ scope.row.number }}
            </template>
          </el-table-column>
          <el-table-column label="状态" align="center" width="80">
            <template v-slot="scope">
              <el-tag :type="getCourseStatusType(getCourseStatusByCapacity(scope.row.currentStudents, scope.row.number))" size="small">
                {{ getCourseStatusByCapacity(scope.row.currentStudents, scope.row.number) === 'available' ? '可选' : '已满' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="180">
            <template v-slot="scope">
              <div class="table-actions">
                <el-button size="small" @click="viewDetails(scope.row.id)">查看详情</el-button>
                <el-button 
                  v-if="getCourseStatusByCapacity(scope.row.currentStudents, scope.row.number) === 'available'" 
                  size="small" 
                  type="primary" 
                  @click="selectCourse(scope.row.id)"
                >
                  立即选课
                </el-button>
                <el-button 
                  v-else 
                  size="small" 
                  disabled
                >
                  已满
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div v-else>
        <el-empty description="暂无课程详情" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Search, Calendar } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request.js'
import typeEnum, { CREDIT_TYPE, CYCLE_TYPE } from '@/constant/module/type.js'

// 课程数据
const courses = ref([])
const selectedCourse = ref(null)
const loading = ref(false)
const total = ref(0)
const dialogVisible = ref(false)

// 表格头部样式
const headerCellStyle = {
  backgroundColor: '#fafafa',
  fontWeight: '600',
  color: '#606266'
};

// 搜索表单
const searchForm = ref({
  courseName: '',
  courseType: '',
  creditsType: ''
})

// 分页
const pagination = ref({
  current: 1,
  size: 10
})

// 计算当前学期
const currentSemester = computed(() => {
  const now = new Date()
  const year = now.getFullYear()
  const month = now.getMonth() + 1 // 月份从0开始，所以+1
  
  // 计算学年：9月到次年8月为一个学年
  let startYear, endYear, semester
  if (month >= 9) {
    startYear = year
    endYear = year + 1
    semester = '第一学期'
  } else {
    startYear = year - 1
    endYear = year
    semester = '第二学期'
  }
  
  return `${startYear}-${endYear}学年${semester}`
})

// 解析weekTime函数
const formatWeekTime = (weekTime) => {
  if (!weekTime) return ''
  
  // 将数字转换为字符串，确保格式统一
  const timeStr = weekTime.toString()
  
  // 解析规则：20304 -> 星期二 3-4节
  // 第一位：星期几（1=星期一，2=星期二，...，5=星期五）
  // 中间两位：开始节数
  // 最后两位：结束节数
  
  const weekDay = parseInt(timeStr[0])
  const startSection = parseInt(timeStr.substring(1, 3))
  const endSection = parseInt(timeStr.substring(3))
  
  // 星期文本映射
  const weekdayMap = {
    1: '一',
    2: '二',
    3: '三',
    4: '四',
    5: '五'
  }
  
  return `星期${weekdayMap[weekDay]} ${startSection}-${endSection}节`
}

// 根据状态值获取标签类型
const getCourseStatusType = (status) => {
  switch (status) {
    case 'available':
      return 'success'
    case 'full':
      return 'danger'
    case 'selected':
      return 'primary'
    default:
      return 'info'
  }
}

// 根据已选人数和上限人数获取课程状态
const getCourseStatusByCapacity = (selectedCount, capacity) => {
  if (selectedCount >= capacity) {
    return 'full'
  } else {
    return 'available'
  }
}

// 获取学分类型标签
const getCreditTypeLabel = (type) => {
  // 处理 FOREIGN_LANGUAGE 映射到 FOREIGN
  if (type === 'FOREIGN_LANGUAGE') {
    return CREDIT_TYPE.FOREIGN?.label || type
  }
  // 其他类型直接查找
  return CREDIT_TYPE[type]?.label || type
}

// 获取周次类型标签
const getCycleTypeLabel = (value) => {
  const entry = Object.entries(CYCLE_TYPE).find(([_, item]) => item.value === value);
  return entry ? entry[1].label : value;
};

// 获取周次类型标签类型
const getCycleTypeTagType = (value) => {
  const typeMap = {
    'SINGLE': 'primary',
    'DOUBLE': 'success',
    'ALL': 'warning'
  };
  return typeMap[value] || 'info';
};

// 打开课程详情弹窗
const openCourseDetails = (course) => {
  selectedCourse.value = course
  dialogVisible.value = true
}

// API相关函数
// 加载课程列表
const loadCourses = async () => {
  loading.value = true
  try {
    const params = {
      courseName: searchForm.value.courseName || undefined,
      teacherName: searchForm.value.teacherName || undefined,
      creditsType: searchForm.value.creditsType || undefined,
      semester: currentSemester.value,
      pageNum: pagination.value.current,
      pageSize: pagination.value.size
    }

    const res = await request.post('/student/selectSCourseInfo', params)
    if (res.code === '200') {
      if (res.data && res.data.list) {
        courses.value = res.data.list
        total.value = res.data.total || courses.value.length
      } else {
        courses.value = []
        total.value = 0
      }
    } else {
      ElMessage.error('获取课程列表失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('获取课程列表错误:', error)
  } finally {
    loading.value = false
  }
}

// 事件处理函数
// 搜索课程
const search = () => {
  // 重置页码为1
  pagination.value.current = 1
  // 调用loadCourses从API获取数据
  loadCourses()
}

// 重置搜索
const reset = () => {
  searchForm.value = {
    courseName: '',
    teacherName: '',
    creditsType: '',
    status: ''
  }
  // 重置页码为1
  pagination.value.current = 1
  // 调用loadCourses从API获取数据
  loadCourses()
}

// 查看课程详情
const viewDetails = (courseId) => {
  console.log('查看课程详情:', courseId)
  // 实际应跳转到课程详情页
}

// 选课
const selectCourse = async (courseId) => {
  try {
    const res = await request.post('/student/selectCourse', { courseId })
    if (res.code === '200') {
      ElMessage.success('选课成功')
      // 重新加载课程数据
      loadCourses()
    } else {
      ElMessage.error(res.msg || '选课失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('选课错误:', error)
  }
}

// 分页处理函数
// 分页处理
const handleSizeChange = (size) => {
  pagination.value.size = size
  // 调用loadCourses从API获取数据
  loadCourses()
}

const handleCurrentChange = (current) => {
  pagination.value.current = current
  // 调用loadCourses从API获取数据
  loadCourses()
}

onMounted(() => {
  loadCourses()
})
</script>

<style scoped>
/* 主容器样式 */
.student-courses-container {
  padding: 30px;
  max-width: 1280px;
  margin: 0 auto;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.08);
  animation: fadeIn 0.5s ease-out;
}

/* 页面标题 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f2f5;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-header h1::before {
  content: '';
  width: 4px;
  height: 24px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border-radius: 2px;
}

/* 学期信息 */
.semester-info {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 16px;
  background: linear-gradient(135deg, #f0f9eb, #e1f3d8);
  border: 1px solid #d9f7be;
  border-radius: 8px;
  color: #67c23a;
  font-size: 14px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.15);
  transition: all 0.3s ease;
}

.semester-info:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.2);
}

.semester-info .el-icon {
  font-size: 18px;
  color: #67c23a;
}

/* 筛选和搜索区域 */
.filter-section {
  background: linear-gradient(135deg, #f8f9fa, #f0f2f5);
  padding: 24px;
  border-radius: 12px;
  margin-bottom: 30px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.filter-section:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}

.search-form {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.search-form .el-form-item {
  margin-bottom: 0;
}

.search-form .el-form-item__label {
  font-weight: 600;
  color: #606266;
  font-size: 14px;
}

.search-form .el-input,
.search-form .el-select {
  width: 200px;
  transition: all 0.3s ease;
}

.search-form .el-input:hover,
.search-form .el-select:hover {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.search-form .el-button {
  border-radius: 8px;
  padding: 10px 20px;
  font-weight: 600;
  transition: all 0.3s ease;
}

.search-form .el-button--primary {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border: none;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.search-form .el-button--primary:hover {
  background: linear-gradient(135deg, #66b1ff, #91caff);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.search-form .el-button--default {
  background: #fff;
  border: 1px solid #dcdfe6;
  color: #606266;
}

.search-form .el-button--default:hover {
  background: #f5f7fa;
  border-color: #c6e2ff;
  color: #409eff;
  transform: translateY(-1px);
}

/* 课程表格容器 */
.courses-table-container {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  width: 100%;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.courses-table-container:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
}

/* 面板标题 */
.panel-title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
  margin: 0;
  padding: 20px 24px;
  background: linear-gradient(135deg, #f8f9fa, #f0f2f5);
  border-bottom: 1px solid #ebeef5;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.panel-title::after {
  content: '';
  width: 40px;
  height: 3px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border-radius: 1.5px;
}

/* 表格样式 */
.courses-table,
.details-table {
  width: 100%;
  background: #fff;
  border: none;
  box-shadow: none;
}

/* 表格单元格样式 */
.courses-table th,
.courses-table td,
.details-table th,
.details-table td {
  padding: 16px 12px;
  font-size: 14px;
  line-height: 1.5;
  border-bottom: 1px solid #f0f2f5;
  text-align: center;
  transition: all 0.3s ease;
}

/* 表格表头样式 */
.courses-table th,
.details-table th {
  background: linear-gradient(135deg, #f8f9fa, #f0f2f5);
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #e6e9ed;
  text-transform: uppercase;
  font-size: 12px;
  letter-spacing: 0.5px;
  position: relative;
}

.courses-table th::after,
.details-table th::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg, transparent, #409eff, transparent);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.courses-table th:hover::after,
.details-table th:hover::after {
  opacity: 1;
}

/* 表格行样式 */
.courses-table tr,
.details-table tr {
  transition: all 0.3s ease;
}

.courses-table tr:hover,
.details-table tr:hover {
  background-color: #f8f9ff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

/* 表格操作按钮样式 */
.table-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
  align-items: center;
}

.table-actions .el-button {
  font-size: 12px;
  padding: 6px 16px;
  border-radius: 8px;
  min-width: 70px;
  transition: all 0.3s ease;
  border: 1px solid transparent;
}

.table-actions .el-button--primary {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border: none;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.table-actions .el-button--primary:hover {
  background: linear-gradient(135deg, #66b1ff, #91caff);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
}

.table-actions .el-button--default {
  background: #f8f9fa;
  border: 1px solid #e6e9ed;
  color: #6c757d;
  font-size: 12px;
  font-weight: 500;
}

.table-actions .el-button--default:hover {
  background: #e9ecef;
  border-color: #dee2e6;
  color: #495057;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, #f8f9fa, #f0f2f5);
  border-top: 1px solid #ebeef5;
  margin-top: 0;
}

/* 分页组件样式 */
.pagination-container .el-pagination {
  font-size: 13px;
}

.pagination-container .el-pagination__sizes {
  margin-right: 20px;
}

.pagination-container .el-pagination__jump {
  margin-left: 20px;
}

.pagination-container .el-pagination__button {
  border-radius: 6px;
  transition: all 0.3s ease;
  border: 1px solid #dcdfe6;
}

.pagination-container .el-pagination__button:hover {
  color: #409eff;
  border-color: #409eff;
  transform: translateY(-1px);
}

.pagination-container .el-pagination__active {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border-color: #409eff;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.pagination-container .el-pagination__active:hover {
  background: linear-gradient(135deg, #66b1ff, #91caff);
  border-color: #66b1ff;
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .student-courses-container {
    padding: 20px;
  }
  
  .search-form .el-input,
  .search-form .el-select {
    width: 180px;
  }
}

@media (max-width: 768px) {
  .student-courses-container {
    padding: 15px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .search-form {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .search-form .el-input,
  .search-form .el-select {
    width: 100%;
  }
  
  .courses-table-container {
    padding: 0;
  }
  
  .panel-title {
    padding: 16px 20px;
    font-size: 16px;
  }
  
  .courses-table th,
  .courses-table td,
  .details-table th,
  .details-table td {
    padding: 12px 8px;
    font-size: 13px;
  }
  
  .table-actions {
    flex-direction: column;
    align-items: center;
    gap: 6px;
  }
  
  .table-actions .el-button {
    width: 100%;
    margin-bottom: 4px;
  }
  
  .pagination-container {
    justify-content: center;
    padding: 16px 20px;
  }
}

/* 空状态样式 */
.el-empty {
  padding: 60px 0;
}

.el-empty__description {
  font-size: 14px;
  color: #909399;
}

/* 弹窗样式 */
.el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.el-dialog__header {
  background: linear-gradient(135deg, #f8f9fa, #f0f2f5);
  border-bottom: 1px solid #ebeef5;
  padding: 20px 24px;
}

.el-dialog__title {
  font-size: 18px;
  font-weight: 600;
  color: #303133;
}

.el-dialog__body {
  padding: 24px;
}

.el-dialog__footer {
  background: #f8f9fa;
  border-top: 1px solid #ebeef5;
  padding: 16px 24px;
}

/* 标签样式 */
.el-tag {
  border-radius: 6px;
  padding: 2px 10px;
  font-size: 12px;
  font-weight: 500;
}
</style>