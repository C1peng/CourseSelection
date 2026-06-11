<template>
  <div class="student-courses-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>我的课程</h1>
      <!-- 总学分统计 -->
      <div class="total-stats">
        <span v-for="(credit, type) in totalCreditStats" :key="type" class="total-info-item">
          <el-icon><CreditCard /></el-icon>
          {{ getCreditTypeLabel(type) }}: {{ credit }} 学分
        </span>
      </div>
    </div>

    <!-- 学期列表 -->
    <div class="semester-list">
      <div 
        v-for="(semesterData, index) in semesters" 
        :key="semesterData.semester"
        class="semester-bar"
        @click="toggleSemester(index)"
      >
        <div class="semester-bar-header">
          <span class="semester-title">{{ semesterData.semester }}</span>
          <!-- 学分统计信息 -->
          <div class="header-info">
            <span v-for="(credit, type) in getCreditStats(semesterData)" :key="type" class="info-item">
              <el-icon><CreditCard /></el-icon>
              {{ getCreditTypeLabel(type) }}: {{ credit }} 学分
            </span>
          </div>
        </div>
        
        <!-- 展开内容 -->
        <div v-if="expandedSemesterIndices.includes(index)" class="semester-content">
          <!-- 课程列表表格 -->
          <div class="courses-table-container">
            <el-empty v-if="semesterData.slinfo.length === 0 && !loading" description="暂无课程" />
            <div v-else>
              <el-table 
                v-loading="loading"
                stripe 
                :data="semesterData.slinfo"
                class="courses-table"
                style="width: 100%"
              >
                <el-table-column prop="courseName" label="课程名称" align="center" min-width="200" />
                <el-table-column prop="creditsType" label="学分类型" align="center" width="120">
                  <template v-slot="scope">
                    {{ getCreditTypeLabel(scope.row.creditsType) }}
                  </template>
                </el-table-column>
                <el-table-column prop="credits" label="学分" align="center" width="80" />
                <el-table-column prop="ordinaryCredit" label="平时成绩" align="center" width="100" />
                <el-table-column prop="examCredit" label="考试成绩" align="center" width="100" />
                <el-table-column prop="totalCredit" label="总评成绩" align="center" width="100" />
                <el-table-column prop="status" label="状态" align="center" width="100">
                  <template v-slot="scope">
                    <el-tag :type="scope.row.status === 1 ? 'success' : 'danger'" size="small">
                      {{ scope.row.status === 1 ? '及格' : scope.row.status === 0 ? '不及格' : '进行中' }}
                    </el-tag>
                  </template>
                </el-table-column>
              </el-table>

              <!-- 数据统计 -->
              <div class="data-stats">
                <span>共 {{ semesterData.slinfo.length }} 条数据</span>
              </div>
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
import { CreditCard, Calendar, ArrowUp, ArrowDown } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request.js'
import typeEnum, { CREDIT_TYPE } from '@/constant/module/type.js'

const user = ref(getUserInfo() || {})

// 课程数据
const semesters = ref([])
const expandedSemesterIndices = ref([]) // 存储展开的学期索引
const loading = ref(false)

// 获取学分类型标签
const getCreditTypeLabel = (type) => {
  // 处理 FOREIGN_LANGUAGE 映射到 FOREIGN
  if (type === 'FOREIGN_LANGUAGE') {
    return CREDIT_TYPE.FOREIGN?.label || type
  }
  // 其他类型直接查找
  return CREDIT_TYPE[type]?.label || type
}

// 学分统计
const getCreditStats = (semesterData) => {
  const stats = {}
  if (semesterData) {
    semesterData.slinfo.forEach(course => {
      const type = course.creditsType
      // 只有当status为1（及格）时才统计
      if (type && course.status === 1) {
        stats[type] = (stats[type] || 0) + (course.credits || 0)
      }
    })
  }
  return stats
}

// 总学分统计（所有学期）
const totalCreditStats = computed(() => {
  const stats = {}
  semesters.value.forEach(semesterData => {
    if (semesterData) {
      semesterData.slinfo.forEach(course => {
        const type = course.creditsType
        // 只有当status为1（及格）时才统计
        if (type && course.status === 1) {
          stats[type] = (stats[type] || 0) + (course.credits || 0)
        }
      })
    }
  })
  return stats
})

// 切换学期展开/折叠
const toggleSemester = (index) => {
  const currentIndex = expandedSemesterIndices.value.indexOf(index)
  if (currentIndex > -1) {
    // 已展开，折叠
    expandedSemesterIndices.value.splice(currentIndex, 1)
  } else {
    // 未展开，展开
    expandedSemesterIndices.value.push(index)
  }
}

// 加载课程数据
const loadCourses = async () => {
  loading.value = true
  try {
    // 实际的API请求，使用后端提供的接口
    const res = await request.get('/student/SCourseLine', {
      params: {
        studentId: user.value.id
      }
    })
    
    if (res.code === '200') {
      if (res.data && res.data.list) {
        semesters.value = res.data.list
      } else {
        semesters.value = []
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
  flex-wrap: wrap;
  gap: 15px;
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

/* 总学分统计 */
.total-stats {
  display: flex;
  gap: 15px;
  align-items: center;
  flex-wrap: wrap;
}

.total-info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  padding: 8px 16px;
  background: linear-gradient(135deg, #f0f9eb, #e1f3d8);
  border: 1px solid #d9f7be;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-weight: 600;
  color: #67c23a;
}

.total-info-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(103, 194, 58, 0.2);
}

.page-header h1::before {
  content: '';
  width: 4px;
  height: 24px;
  background: linear-gradient(135deg, #409eff, #66b1ff);
  border-radius: 2px;
}

/* 学期列表 */
.semester-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 学期横条 */
.semester-bar {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.semester-bar:hover {
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

/* 学期横条头部 */
.semester-bar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: linear-gradient(135deg, #f8f9fa, #f0f2f5);
  cursor: pointer;
  transition: all 0.3s ease;
  border-bottom: 1px solid #ebeef5;
  flex-wrap: wrap;
  gap: 10px;
}

.semester-bar-header:hover {
  background: linear-gradient(135deg, #e9ecef, #dee2e6);
}

.semester-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

/* 展开内容 */
.semester-content {
  padding: 20px;
  background: #fff;
  animation: slideDown 0.3s ease-out;
}

/* 学分统计信息 */
.header-info {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #666;
  padding: 6px 12px;
  background: #fff;
  border: 1px solid #e6e9ed;
  border-radius: 8px;
  transition: all 0.3s ease;
  font-weight: 500;
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

@keyframes slideDown {
  from {
    opacity: 0;
    max-height: 0;
    overflow: hidden;
  }

  to {
    opacity: 1;
    max-height: 1000px;
    overflow: visible;
  }
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .student-courses-container {
    padding: 20px;
  }
  
  .header-info {
    gap: 12px;
  }
  
  .info-item {
    padding: 6px 12px;
    font-size: 13px;
  }
  
  .semester-bar-header {
    padding: 12px 20px;
  }
  
  .semester-content {
    padding: 16px;
  }
}

@media (max-width: 768px) {
  .student-courses-container {
    padding: 15px;
  }
  
  .header-info {
    width: 100%;
    flex-wrap: wrap;
  }
  
  .semester-bar-header {
    padding: 10px 16px;
  }
  
  .semester-title {
    font-size: 14px;
  }
  
  .semester-content {
    padding: 12px;
  }
  
  .courses-table-container {
    padding: 0;
  }
  
  .courses-table th,
  .courses-table td {
    padding: 12px 8px;
    font-size: 13px;
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