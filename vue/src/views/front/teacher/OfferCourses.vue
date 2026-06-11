<template>
  <div class="container">
    <!-- 查询条件卡片 -->
    <el-card class="query-card" shadow="hover">
      <div class="query-title">
        <h3>搜索查询</h3>
      </div>

      <!-- 基础查询字段行 -->
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input 
            v-model="data.queryParams.courseName" 
            prefix-icon="Search" 
            placeholder="请输入课程名称" 
            clearable
            @clear="resetQuery" 
            @keyup.enter="handleQuery" 
          />
        </el-col>
        <el-col :span="4">
          <el-select v-model="data.queryParams.creditsType" placeholder="请选择学分类型" clearable>
            <el-option 
              v-for="item in Object.values(CREDIT_TYPE)" 
              :key="item.value" 
              :label="item.label" 
              :value="item.value" 
            />
          </el-select>
        </el-col>
        <el-col :span="6">
          <div class="query-buttons">
            <el-button type="primary" @click="handleQuery">
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button @click="resetQuery">重置</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 主内容区 -->
    <el-card class="table-card" shadow="hover">
      <div class="table-header">
        <div class="table-info">
          <h3 class="table-title">课程列表</h3>
          <el-tag class="total-record-tag">共 {{ data.total }} 条记录</el-tag>
        </div>
      </div>

      <!-- 主表格 - 显示基本信息 -->
      <el-table v-loading="data.loading" stripe :data="data.sections" :header-cell-style="headerCellStyle">
        <!-- <el-table-column prop="id" label="序号" align="center" min-width="80" /> -->
        <el-table-column prop="courseName" label="课程名称" align="center" min-width="100" />
        <el-table-column prop="creditsType" label="学分类型" align="center" min-width="120">
          <template v-slot:default="scope">
            <el-tag :type="getCreditTypeTagType(scope.row.creditsType)" class="credit-tag">
              {{ getCreditTypeLabel(scope.row.creditsType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="number" label="人数上限" align="center" min-width="100" />
        <el-table-column label="操作" fixed="right" align="center" width="240">
          <template v-slot:default="scope">
            <div class="table-actions">
              <el-button type="primary" size="small" :icon="Edit" @click="handleEditCourse(scope.row)" title="编辑" />
            </div>
          </template>
        </el-table-column>
      </el-table>
      
      <!-- 分页 -->
      <div class="pagination-container" v-if="data.total">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          v-model:current-page="data.pagination.currentPage"
          v-model:page-size="data.pagination.pageSize"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          :total="data.total"
        />
      </div>
    </el-card>

    <!-- 课程编辑对话框 -->
    <el-dialog
      :title="'编辑课程'"
      v-model="data.dialogVisible"
      width="60%"
      destroy-on-close
      center
      :close-on-click-modal="false"
    >
      <!-- 使用 CSDetailsForm 组件 -->
      <CSDetailsForm
        ref="detailsFormRef"
        :initial-data="data.selectedRow"
        :is-edit="true"
        :lock-teacher-id="user.role === 'TEACHER'"
      />
      <template #footer>
        <span>
          <el-button @click="data.dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="data.isSubmitting">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import { getUserInfo } from '@/utils/userStorage.js'
import { Edit, Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request.js'
import CSDetailsForm from '@/views/communal/CSDetailsForm.vue'
import { CREDIT_TYPE } from '@/constant/module/type.js'

const user = ref(getUserInfo() || {})

// 表单引用
const detailsFormRef = ref(null)

// 常量定义
const headerCellStyle = {
  backgroundColor: '#fafafa',
  fontWeight: '600',
  color: '#606266'
}

// 响应式数据对象
const data = reactive({
  // 页面状态
  selectedRow: null,
  dialogVisible: false,
  isSubmitting: false,

  // 数据加载状态
  loading: false,

  // 数据集合
  sections: [],
  total: 0,

  // 查询参数
  queryParams: {
    courseId: null,
    courseName: '',
    creditsType: ''
  },

  // 分页参数
  pagination: {
    currentPage: 1,
    pageSize: 10
  },


})



/**
 * 数据获取相关函数
 */
const fetchSections = async () => {
  data.loading = true
  try {
    let params = {
      pageNum: data.pagination.currentPage,
      pageSize: data.pagination.pageSize,
      courseId: data.queryParams.courseId !== null ? Number(data.queryParams.courseId) : null,
      courseName: data.queryParams.courseName || undefined,
      creditsType: data.queryParams.creditsType || undefined,
      teacherId: user.value.id || null
    }

    const filteredParams = {}
    Object.keys(params).forEach(key => {
      if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
        filteredParams[key] = params[key]
      }
    })

    const res = await request.post('/teacher/selectInfo', filteredParams)
    if (res.code === '200') {
      data.sections = res.data?.list || []
      data.total = res.data?.total || 0
    } else {
      ElMessage.error(res.msg || '获取班次列表失败')
    }
  } catch (error) {
    ElMessage.error('获取班次列表失败')
    console.error('获取班次列表错误:', error)
  } finally {
    data.loading = false
  }
}

/**
 * 页面交互相关函数
 */
const handleQuery = async () => {
  let courseId = data.queryParams.courseId
  courseId = Number(courseId)
  
  data.pagination.currentPage = 1
  await fetchSections()
}

/**
 * 表单操作相关函数
 */
const handleEditCourse = (row) => {
  // 复制行数据
  data.selectedRow = { ...row }
  
  // 如果是教师角色，确保教师ID为当前用户ID
  if (user.value.role === 'TEACHER' && user.value.id) {
    data.selectedRow.teacherId = user.value.id
  }
  
  data.dialogVisible = true
}

const resetForm = () => {
  data.selectedRow = null
  if (detailsFormRef.value) {
    detailsFormRef.value.resetForm()
  }
}

const submitForm = async () => {
  try {
    data.isSubmitting = true
    
    if (!detailsFormRef.value) {
      ElMessage.error('表单组件未初始化')
      return
    }
    
    // 获取表单数据
    const formData = detailsFormRef.value.getFormData()
    
    // 设置教师ID
    formData.teacherId = user.value.id || formData.teacherId
    
    const url = '/c_select/addDetails'
    const method = 'post'
    
    const res = await request[method](url, formData)
    
    if (res.code === '200') {
      data.dialogVisible = false
      ElMessage.success('编辑成功')
      await fetchSections()
    } else {
      ElMessage.error(res.msg || '编辑失败')
    }
  } catch (error) {
    ElMessage.error('编辑失败')
    console.error('表单提交错误:', error)
  } finally {
    data.isSubmitting = false
  }
}

/**
 * 分页相关函数
 */
const handleSizeChange = (size) => {
  data.pagination.pageSize = size
  fetchSections()
}

const handleCurrentChange = (current) => {
  data.pagination.currentPage = current
  fetchSections()
}

/**
 * 查询重置函数
 */
const resetQuery = () => {
  data.queryParams = {
    courseId: null,
    courseName: '',
    creditsType: ''
  }
  data.pagination.currentPage = 1
  fetchSections()
}

/**
 * 辅助函数
 */
const getCreditTypeLabel = (value) => {
  const creditType = Object.values(CREDIT_TYPE).find(item => item.value === value)
  return creditType?.label || value
}

const getCreditTypeTagType = (value) => {
  const typeMap = {
    PUBLIC: 'primary',
    FOREIGN_LANGUAGE: 'success',
    SPORT: 'warning',
    ART: 'info',
    OTHER: 'danger'
  }
  return typeMap[value] || 'info'
}

// 初始化
onMounted(() => {
  fetchSections()
})
</script>

<style scoped>
/* 导入前台公共样式 */
@import "@/assets/css/layouts/page.css";

/* 学分类型标签样式 */
.credit-tag {
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-xs);
  padding: 2px var(--space-sm);
}

/* 表格操作按钮样式 */
.table-actions {
  display: flex;
  justify-content: center;
  gap: var(--space-sm);
}

/* 查询按钮样式 */
.query-buttons {
  display: flex;
  gap: var(--space-sm);
}

/* 查询标题样式 */
.query-title {
  margin-bottom: var(--space-md);
}

.query-title h3 {
  margin: 0;
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: var(--space-lg) 0;
  margin-top: var(--space-lg);
}

/* 优化查询输入框样式 */
:deep(.query-card .el-input),
:deep(.query-card .el-select) {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .el-col {
    margin-bottom: var(--space-md);
  }

  .query-title {
    margin-bottom: var(--space-sm);
  }

  .table-header {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-sm);
  }

  .query-buttons {
    flex-direction: column;
    gap: var(--space-sm);
  }
}
</style>