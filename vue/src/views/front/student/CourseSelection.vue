<template>
  <div class="course-selection-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>课程信息</h1>
      <div class="header-info">
        <span v-for="(credit, type) in creditTypeStats" :key="type" class="info-item">
          <el-icon><CreditCard /></el-icon>
          {{ getCreditTypeLabel(type) }}: {{ credit }} 学分
        </span>
      </div>
    </div>

    <!-- 时间范围提示 -->
    <el-alert 
      v-if="!isInSelectTimeRange" 
      title="当前不在选课时间范围内" 
      type="warning" 
      show-icon 
    />

    <!-- 课程列表表格 -->
    <div v-if="isInSelectTimeRange" class="courses-table-container">
      <h2 class="panel-title">
        课程列表
        <div class="semester-info">
          <el-icon><Calendar /></el-icon>
          <span>{{ currentSemester }}</span>
        </div>
      </h2>
      <el-empty v-if="courses.length === 0 && !loading" description="暂无课程" />
      <div v-else>
        <el-table 
          v-loading="loading"
          stripe 
          :data="courses"
          class="courses-table"
          style="width: 100%"
        >
          <el-table-column prop="courseName" label="课程名称" align="center" min-width="200" />
          <el-table-column prop="teacherName" label="授课教师" align="center" width="120" />
          <el-table-column prop="credits" label="学分" align="center" width="80" />
          <el-table-column prop="creditsType" label="学分类型" align="center" width="120">
            <template v-slot="scope">
              {{ getCreditTypeLabel(scope.row.creditsType) }}
            </template>
          </el-table-column>
          <el-table-column prop="place" label="上课地点" align="center" min-width="120" />
          <el-table-column prop="courseTotal" label="课程节数" align="center" width="100" />
          <el-table-column prop="cycle" label="周次类型" align="center" width="100">
            <template v-slot="scope">
              <el-tag :type="getCycleTypeTagType(scope.row.cycle)" size="small">
                {{ getCycleTypeLabel(scope.row.cycle) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="上课时间" align="center" min-width="150">
            <template v-slot="scope">
              {{ formatWeekTime(scope.row.weekTime) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" align="center" width="160">
            <template v-slot="scope">
              <div class="table-actions">
                <el-button 
                  size="small" 
                  type="primary" 
                  @click="selectCourse(scope.row.id)"
                >
                  选课
                </el-button>
                <el-button 
                  size="small" 
                  type="warning" 
                  @click="dropCourse(scope.row.id)"
                >
                  退课
                </el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>

        <!-- 数据统计 -->
        <div class="data-stats">
          <span>共 {{ total }} 条数据</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getUserInfo } from '@/utils/userStorage.js'
import { CreditCard, Calendar } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request.js'
import typeEnum, { CREDIT_TYPE, CYCLE_TYPE } from '@/constant/module/type.js'

const user = ref(getUserInfo() || {})

// 时间范围检查状态
const isInSelectTimeRange = ref(true)

// 课程数据
const courses = ref([])
const total = ref(0)
const loading = ref(false)

// 学分类型统计
const creditTypeStats = computed(() => {
  const stats = {}
  courses.value.forEach(course => {
    const type = course.creditsType
    if (type) {
      stats[type] = (stats[type] || 0) + (course.credits || 0)
    }
  })
  return stats
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

// 检查选课时间范围
const checkSelectTimeRange = async () => {
  try {
    const res = await request.get(`/semester/time/check/select/${currentSemester.value}`);
    if (res.code === '200') {
      isInSelectTimeRange.value = res.data;
    }
  } catch (error) {
    console.error('检查时间范围失败:', error);
  }
};

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

// 加载课程列表
const loadCourses = async () => {
  // 只在选课时间范围内加载课程数据
  if (!isInSelectTimeRange.value) {
    courses.value = []
    total.value = 0
    return
  }
  
  loading.value = true
  try {
    const res = await request.get('/student/selectSCDetailInfo', {
      params: {
        semester: currentSemester.value
      }
    })
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

// 退课
const dropCourse = async (courseId) => {
  try {
    const res = await request.post('/student/dropCourse', { courseId })
    if (res.code === '200') {
      ElMessage.success('退课成功')
      // 重新加载课程数据
      loadCourses()
    } else {
      ElMessage.error(res.msg || '退课失败')
    }
  } catch (error) {
    ElMessage.error('网络错误，请稍后重试')
    console.error('退课错误:', error)
  }
}

onMounted(async () => {
  // 检查选课时间范围
  await checkSelectTimeRange();
  // 加载课程列表
  loadCourses();
})
</script>

<style scoped>
/* 主容器样式 */
.course-selection-container {
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

/* 学分信息 */
.header-info {
  display: flex;
  gap: 20px;
  align-items: center;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  padding: 8px 16px;
  background: linear-gradient(135deg, #f8f9fa, #f0f2f5);
  border: 1px solid #e6e9ed;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.info-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
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

/* 表格样式 */
.courses-table {
  width: 100%;
  background: #fff;
  border: none;
  box-shadow: none;
}

/* 表格单元格样式 */
.courses-table th,
.courses-table td {
  padding: 16px 12px;
  font-size: 14px;
  line-height: 1.5;
  border-bottom: 1px solid #f0f2f5;
  text-align: center;
  transition: all 0.3s ease;
}

/* 表格表头样式 */
.courses-table th {
  background: linear-gradient(135deg, #f8f9fa, #f0f2f5);
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #e6e9ed;
  text-transform: uppercase;
  font-size: 12px;
  letter-spacing: 0.5px;
  position: relative;
}

.courses-table th::after {
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

.courses-table th:hover::after {
  opacity: 1;
}

/* 表格行样式 */
.courses-table tr {
  transition: all 0.3s ease;
}

.courses-table tr:hover {
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

.table-actions .el-button--warning {
  background: linear-gradient(135deg, #e6a23c, #ebb563);
  border: none;
  font-size: 12px;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(230, 162, 60, 0.3);
}

.table-actions .el-button--warning:hover {
  background: linear-gradient(135deg, #ebb563, #f0c48c);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(230, 162, 60, 0.4);
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

/* 数据统计样式 */
.data-stats {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(135deg, #f8f9fa, #f0f2f5);
  border-top: 1px solid #ebeef5;
  margin-top: 0;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
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
  .course-selection-container {
    padding: 20px;
  }
  
  .header-info {
    gap: 12px;
  }
  
  .info-item {
    padding: 6px 12px;
    font-size: 13px;
  }
}

@media (max-width: 768px) {
  .course-selection-container {
    padding: 15px;
  }
  
  .page-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .header-info {
    width: 100%;
    justify-content: space-between;
  }
  
  .courses-table-container {
    padding: 0;
  }
  
  .panel-title {
    padding: 16px 20px;
    font-size: 16px;
  }
  
  .courses-table th,
  .courses-table td {
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

/* 标签样式 */
.el-tag {
  border-radius: 6px;
  padding: 2px 10px;
  font-size: 12px;
  font-weight: 500;
}
</style>