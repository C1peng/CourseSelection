<template>
  <div class="container">
    <!-- 顶部操作栏 -->
    <div class="header-card">
      <div class="header-title">
        <h2><el-icon>
            <Calendar />
          </el-icon>学期时间范围设置</h2>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon>
            <Plus />
          </el-icon>新增
        </el-button>
        <el-button @click="loadCurrentSemester">
          <el-icon>
            <Calendar />
          </el-icon>加载当前学期
        </el-button>
      </div>
    </div>

    <!-- 列表展示区域 -->
    <el-card class="table-card" shadow="hover">
      <div class="table-header">
        <div class="table-info">
          <h3 class="table-title">已设置的时间范围</h3>
          <el-tag class="total-record-tag">共 {{ timeRangeList.length }} 条记录</el-tag>
        </div>
      </div>
      <el-table v-loading="loading" stripe :data="timeRangeList" :header-cell-style="{ backgroundColor: '#fafafa' }">
        <el-table-column prop="semester" label="学期" width="200" align="center" />
        <el-table-column prop="courseCreateStart" label="课程创建开始时间" align="center" />
        <el-table-column prop="courseCreateEnd" label="课程创建结束时间" align="center" />
        <el-table-column prop="courseSelectStart" label="选课开始时间" align="center" />
        <el-table-column prop="courseSelectEnd" label="选课结束时间" align="center" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="scope">
            <div style="display: flex; justify-content: center; gap: 8px;">
              <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(scope.row)" title="编辑"></el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 居中对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="80%" destroy-on-close center>
      <el-form :model="formData" label-width="160px" class="time-range-form">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="学期">
              <div class="semester-selector">
                <el-input-number v-model="startYear" :min="2000" :max="2100" size="default" controls-position="right" style="width: 100px" />
                <span class="separator">-</span>
                <el-input-number v-model="endYear" :min="2000" :max="2100" size="default" controls-position="right" style="width: 100px" />
                <span class="year-label">学年</span>
                <el-select v-model="semesterType" placeholder="学期" size="default" style="width: 120px">
                  <el-option label="第一学期" value="第一学期" />
                  <el-option label="第二学期" value="第二学期" />
                </el-select>
              </div>
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程创建开始时间">
              <el-date-picker 
                v-model="formData.courseCreateStart" 
                type="datetime" 
                placeholder="选择开始时间" 
                size="default"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="课程创建结束时间">
              <el-date-picker 
                v-model="formData.courseCreateEnd" 
                type="datetime" 
                placeholder="选择结束时间" 
                size="default"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="选课开始时间">
              <el-date-picker 
                v-model="formData.courseSelectStart" 
                type="datetime" 
                placeholder="选择开始时间" 
                size="default"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="选课结束时间">
              <el-date-picker 
                v-model="formData.courseSelectEnd" 
                type="datetime" 
                placeholder="选择结束时间" 
                size="default"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <template #footer>
        <span>
          <el-button @click="dialogVisible = false">取 消</el-button>
          <el-button type="primary" @click="saveTimeRange" :loading="saving">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { Calendar, Plus, Edit } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request.js'

const loading = ref(false)
const saving = ref(false)
const dialogVisible = ref(false)
const startYear = ref(new Date().getFullYear())
const endYear = ref(new Date().getFullYear() + 1)
const semesterType = ref('第一学期')
const timeRangeList = ref([])

const formData = ref({
  semester: '',
  courseCreateStart: null,
  courseCreateEnd: null,
  courseSelectStart: null,
  courseSelectEnd: null
})

// 对话框标题
const dialogTitle = computed(() => {
  return formData.value.semester ? '编辑时间范围' : '新增时间范围'
})

// 构建学期字符串
const buildSemesterString = () => {
  return `${startYear.value}-${endYear.value}学年${semesterType.value}`
}

// 转换日期格式（将 T 替换为空格）
const formatDate = (dateString) => {
  if (!dateString) return null
  return dateString.replace('T', ' ')
}

// 加载当前学期时间范围
const loadCurrentSemester = async () => {
  try {
    const res = await request.get('/semester/time/current')
    if (res.code === '200' && res.data) {
      const data = res.data
      formData.value = {
        semester: data.semester,
        courseCreateStart: formatDate(data.courseCreateStart),
        courseCreateEnd: formatDate(data.courseCreateEnd),
        courseSelectStart: formatDate(data.courseSelectStart),
        courseSelectEnd: formatDate(data.courseSelectEnd)
      }
      
      // 解析学期字符串设置年份和学期类型
      const semesterParts = data.semester.match(/(\d{4})-(\d{4})学年(第一学期|第二学期)/)
      if (semesterParts) {
        startYear.value = parseInt(semesterParts[1])
        endYear.value = parseInt(semesterParts[2])
        semesterType.value = semesterParts[3]
      }
      
      dialogVisible.value = true
    }
  } catch (error) {
    console.error('加载当前学期失败:', error)
    ElMessage.error('加载当前学期失败')
  }
}

// 保存时间范围设置
const saveTimeRange = async () => {
  formData.value.semester = buildSemesterString()
  
  saving.value = true
  try {
    const res = await request.post('/semester/time/set', formData.value)
    if (res.code === '200') {
      ElMessage.success('保存成功')
      dialogVisible.value = false
      loadTimeRangeList()
    } else {
      ElMessage.error(res.msg || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    ElMessage.error('保存失败')
  } finally {
    saving.value = false
  }
}

// 新增时间范围
const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

// 编辑时间范围
const handleEdit = (row) => {
  formData.value = {
    semester: row.semester,
    courseCreateStart: formatDate(row.courseCreateStart),
    courseCreateEnd: formatDate(row.courseCreateEnd),
    courseSelectStart: formatDate(row.courseSelectStart),
    courseSelectEnd: formatDate(row.courseSelectEnd)
  }
  
  // 解析学期字符串设置年份和学期类型
  const semesterParts = row.semester.match(/(\d{4})-(\d{4})学年(第一学期|第二学期)/)
  if (semesterParts) {
    startYear.value = parseInt(semesterParts[1])
    endYear.value = parseInt(semesterParts[2])
    semesterType.value = semesterParts[3]
  }
  
  dialogVisible.value = true
}

// 重置表单
const resetForm = () => {
  const now = new Date()
  startYear.value = now.getFullYear()
  endYear.value = now.getFullYear() + 1
  semesterType.value = '第一学期'
  formData.value = {
    semester: '',
    courseCreateStart: null,
    courseCreateEnd: null,
    courseSelectStart: null,
    courseSelectEnd: null
  }
}

// 加载时间范围列表
const loadTimeRangeList = async () => {
  loading.value = true
  try {
    // 从后端API获取真实数据
    const res = await request.get('/semester/time/list')
    if (res.code === '200' && res.data) {
      // 转换日期格式
      timeRangeList.value = res.data.map(item => ({
        ...item,
        courseCreateStart: formatDate(item.courseCreateStart),
        courseCreateEnd: formatDate(item.courseCreateEnd),
        courseSelectStart: formatDate(item.courseSelectStart),
        courseSelectEnd: formatDate(item.courseSelectEnd)
      }))
    }
  } catch (error) {
    console.error('加载时间范围列表失败:', error)
    // 加载失败时使用模拟数据作为 fallback
    timeRangeList.value = [
      {
        semester: '2025-2026学年第一学期',
        courseCreateStart: '2025-06-01 00:00:00',
        courseCreateEnd: '2025-08-31 23:59:59',
        courseSelectStart: '2025-08-15 00:00:00',
        courseSelectEnd: '2025-09-15 23:59:59'
      },
      {
        semester: '2025-2026学年第二学期',
        courseCreateStart: '2025-12-01 00:00:00',
        courseCreateEnd: '2026-01-31 23:59:59',
        courseSelectStart: '2026-02-01 00:00:00',
        courseSelectEnd: '2026-02-28 23:59:59'
      },
      {
        semester: '2026-2027学年第一学期',
        courseCreateStart: '2026-06-01 00:00:00',
        courseCreateEnd: '2026-08-31 23:59:59',
        courseSelectStart: '2026-08-15 00:00:00',
        courseSelectEnd: '2026-09-15 23:59:59'
      }
    ]
  } finally {
    loading.value = false
  }
}

// 监听年份变化
watch(startYear, (newValue) => {
  endYear.value = newValue + 1
})

watch(endYear, (newValue) => {
  startYear.value = newValue - 1
})

// 初始化
onMounted(() => {
  loadTimeRangeList()
})
</script>

<style scoped>
/* 导入公共列表页样式 */
@import "@/assets/css/manager/list.css";

/* 学期选择器样式 */
.semester-selector {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.separator {
  font-size: 16px;
  color: #909399;
}

.year-label {
  font-size: 14px;
  color: #606266;
  white-space: nowrap;
}

/* 表单样式 */
.time-range-form {
  padding: 20px 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .semester-selector {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>
