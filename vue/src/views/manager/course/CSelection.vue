<template>
  <div class="container">
    <!-- 顶部操作栏 -->
    <div class="header-card">
      <div class="header-title">
        <h2><el-icon><Document /></el-icon>选课信息</h2>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleAddSection">
          <el-icon><Plus /></el-icon>新增
        </el-button>
        <el-button type="danger" :disabled="data.selectedRows.length === 0" @click="handleBatchDelete">
          <el-icon><Delete /></el-icon>批量删除
        </el-button>
      </div>
    </div>

    <!-- 查询条件卡片 -->
    <el-card class="query-card" shadow="hover">
      <div class="query-title">
        <h3>搜索查询</h3>
        <div class="mode-switch">
          <el-radio-group v-model="data.queryMode" size="small" class="query-mode-toggle">
            <el-radio-button :value="'basic'" class="mode-btn">基础列表</el-radio-button>
            <el-radio-button :value="'detailed'" class="mode-btn">详细列表</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 基础查询字段行 -->
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input 
            v-model="data.queryParams.courseId" 
            prefix-icon="Search" 
            placeholder="请输入课程编号" 
            clearable
            @clear="resetQuery" 
            @keyup.enter="handleQuery" 
          />
        </el-col>
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
              v-for="(item) in typeEnum.CREDITTYPE" 
              :key="item.value" 
              :label="item.label"
              :value="item.value"
            ></el-option>
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

      <!-- 详细查询字段 - 仅在详细模式下显示，使用单独的行并添加间距 -->
      <el-row v-if="data.queryMode === 'detailed'" :gutter="20" class="detailed-query-row">
        <el-col :span="6">
          <el-input 
            v-model="data.queryParams.teacherName" 
            prefix-icon="Search" 
            placeholder="请输入教师姓名" 
            clearable 
            @clear="resetQuery"
            @keyup.enter="handleQuery" 
          />
        </el-col>
        <el-col :span="6">
          <el-input 
            v-model="data.queryParams.semester" 
            prefix-icon="Search" 
            placeholder="请输入学期" 
            clearable 
            @clear="resetQuery"
            @keyup.enter="handleQuery" 
          />
        </el-col>
        <el-col :span="4">
          <el-select v-model="data.queryParams.status" placeholder="请选择课程状态" clearable>
            <el-option 
              v-for="(item) in typeEnum.COURSESTATUS" 
              :key="item.value" 
              :label="item.label"
              :value="item.value"
            ></el-option>
          </el-select>
        </el-col>
      </el-row>
    </el-card>

    <!-- 主内容区 - 使用标签页组织 -->
    <el-card class="table-card" shadow="hover">
      <el-tabs v-model="data.activeTab" class="content-tabs" stretch>
        <!-- 基础信息标签页 -->
        <el-tab-pane label="基础信息" name="basic">
          <div class="table-header">
            <div class="table-info">
              <h3 class="table-title">信息列表</h3>
              <el-tag class="total-record-tag">共 {{ data.total }} 条记录</el-tag>
            </div>
          </div>

          <!-- 主表格 - 显示基本信息 -->
          <el-table v-loading="data.loading" stripe :data="data.sections" :header-cell-style="headerCellStyle">
            <el-table-column prop="id" label="序号" align="center" min-width="80" />
            <el-table-column prop="courseName" label="课程名称" align="center" min-width="100" />
            <el-table-column prop="creditsType" label="学分类型" align="center" min-width="120">
              <template v-slot="scope">
                <el-tag :type="getCreditTypeTagType(scope.row.creditsType)" class="credit-tag">
                  {{ getCreditTypeLabel(scope.row.creditsType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="number" label="人数上限" align="center" min-width="100" />
            <el-table-column label="操作" fixed="right" align="center" width="240">
              <template v-slot="scope">
                <div class="table-actions">
                  <el-button type="success" size="small" :icon="View" @click="showDetailsTab(scope.row)" title="查看详情" />
                  <el-button type="primary" size="small" :icon="Edit" @click="handleEditSection(scope.row)" title="编辑" />
                  <el-button type="danger" size="small" :icon="Delete" @click="handleDelete(scope.row.id)" title="删除" />
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
        </el-tab-pane>

        <!-- 详细信息标签页 -->
        <el-tab-pane label="详细信息" name="details" :disabled="!data.selectedCourseData">
          <CSDetails
            :selected-course-data="data.selectedCourseData"
            :related-sections="data.relatedSections"
            :related-total="data.relatedTotal"
            :related-sections-loading="data.relatedSectionsLoading"
            :related-pagination="data.relatedPagination"
            @back-to-basic-tab="backToBasicTab"
            @edit-section="handleEditSection"
            @delete-section="handleDelete"
            @selection-change="handleSelectionChange"
            @related-size-change="handleRelatedSizeChange"
            @related-current-change="handleRelatedCurrentChange"
          />
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 班次详情/编辑对话框 -->
    <el-dialog 
      :title="data.isEdit ? '课程管理' : '新增课程班次'" 
      v-model="data.dialogVisible" 
      width="60%" 
      destroy-on-close 
      center
      :close-on-click-modal="false"
    >
      <!-- 添加表单类型切换 - 只在基础列表编辑时显示 -->
      <div v-if="data.isEdit && data.isFromBasicList" class="form-type-toggle">
        <el-radio-group v-model="data.formType" class="form-type-radio-group">
          <el-radio-button :value="'basic'" class="form-type-button">基础信息</el-radio-button>
          <el-radio-button :value="'details'" class="form-type-button">详细信息</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 根据表单类型显示不同的表单组件 -->
      <CourseSelectionForm 
        v-if="data.formType === 'basic'" 
        ref="basicFormRef" 
        :initial-data="data.selectedRow"
        :is-edit="data.isEdit" 
      />
      <CSDetailsForm 
        v-else-if="data.formType === 'details'" 
        ref="detailsFormRef" 
        :initial-data="data.selectedRow"
        :is-edit="data.isEdit" 
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
import { reactive, ref, onMounted, watch } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { 
  Document, Delete, Edit, Plus, Search, View, ArrowLeft 
} from '@element-plus/icons-vue';
import request from '@/utils/request.js';
import typeEnum from '@/constant/module/type.js';
import CSDetailsForm from '@/views/communal/CSDetailsForm.vue';
import CourseSelectionForm from './CourseSelectionForm.vue';
import CSDetails from './CSDetails.vue';

// 常量定义
const headerCellStyle = {
  backgroundColor: '#fafafa',
  fontWeight: '600',
  color: '#606266'
};

// 表单引用
const basicFormRef = ref(null);
const detailsFormRef = ref(null);

// 响应式数据对象
const data = reactive({
  // 页面状态
  activeTab: 'basic',
  selectedCourseData: null,
  selectedRow: null,
  selectedRows: [],
  dialogVisible: false,
  isEdit: false,
  isSubmitting: false,

  // 表单相关
  formType: 'details',
  isFromBasicList: false,

  // 查询模式
  queryMode: 'basic',

  // 数据加载状态
  loading: false,
  relatedSectionsLoading: false,

  // 数据集合
  sections: [],
  relatedSections: [],
  total: 0,
  relatedTotal: 0,

  // 查询参数
  queryParams: {
    courseId: null,
    courseName: '',
    creditsType: '',
    teacherName: '',
    semester: '',
    status: '',
  },

  // 分页参数
  pagination: {
    currentPage: 1,
    pageSize: 10
  },
  relatedPagination: {
    currentPage: 1,
    pageSize: 10
  }
});

/**
 * 数据获取相关函数
 */
const fetchSections = async () => {
  data.loading = true;
  try {
    let params = {
      courseId: data.queryParams.courseId !== null ? Number(data.queryParams.courseId) : null,
      courseName: data.queryParams.courseName || undefined,
      creditsType: data.queryParams.creditsType || undefined,
      pageNum: data.pagination.currentPage,
      pageSize: data.pagination.pageSize
    };

    const filteredParams = {};
    Object.keys(params).forEach(key => {
      if (params[key] !== undefined && params[key] !== null && params[key] !== '') {
        filteredParams[key] = params[key];
      }
    });

    const res = await request.post('/c_select/selectPage', filteredParams);
    if (res.code === '200') {
      data.sections = res.data?.list || [];
      data.total = res.data?.total || 0;
    }
  } catch (error) {
    ElMessage.error('获取班次列表失败');
    console.error('获取班次列表错误:', error);
  } finally {
    data.loading = false;
  }
};

const fetchRelatedSections = async (courseId) => {
  let numericCourseId;
  if (courseId === null || courseId === '') {
    numericCourseId = undefined;
  } else {
    numericCourseId = Number(courseId);
    if (isNaN(numericCourseId)) {
      ElMessage.error('无效的课程ID');
      return;
    }
  }

  data.relatedSectionsLoading = true;
  try {
    const params = {
      courseId: numericCourseId,
      courseName: data.queryParams.courseName || undefined,
      creditsType: data.queryParams.creditsType || undefined,
      teacherName: data.queryParams.teacherName || undefined,
      semester: data.queryParams.semester || undefined,
      status: data.queryParams.status ?? undefined,
      pageNum: data.relatedPagination.currentPage,
      pageSize: data.relatedPagination.pageSize
    };

    const res = await request.post('/c_select/selectDetailPage', params);
    if (res.code === '200') {
      data.relatedSections = res.data?.list || [];
      data.relatedTotal = res.data?.total || 0;
    }
  } catch (error) {
    ElMessage.error('获取详细信息失败');
    console.error('获取详细信息错误:', error);
  } finally {
    data.relatedSectionsLoading = false;
  }
};

/**
 * 页面交互相关函数
 */
const handleQuery = async () => {
  let courseId = data.queryParams.courseId;
  let numericCourseId;
  if (courseId === null || courseId === '') {
    numericCourseId = null;
  } else {
    numericCourseId = Number(courseId);
    if (isNaN(numericCourseId)) {
      ElMessage.error('无效的课程ID');
      return;
    }
  }
  data.selectedCourseData = { courseId: numericCourseId };

  if (data.queryMode === 'detailed') {
    data.relatedPagination.currentPage = 1;
    await fetchRelatedSections(numericCourseId);
    data.activeTab = 'details';
    document.querySelector('.container')?.scrollTo({ top: 0, behavior: 'smooth' });
  } else {
    data.pagination.currentPage = 1;
    await fetchSections();
    data.activeTab = 'basic';
  }
};

const showDetailsTab = async (row) => {
  data.selectedCourseData = { ...row };
  data.relatedPagination.currentPage = 1;
  
  if (row.csDetails && Array.isArray(row.csDetails)) {
    data.relatedSections = row.csDetails;
    data.relatedTotal = row.csDetails.length;
  } else {
    data.relatedSections = [];
    data.relatedTotal = 0;
  }

  data.activeTab = 'details';
  document.querySelector('.container')?.scrollTo({ top: 0, behavior: 'smooth' });
};

const backToBasicTab = () => {
  data.activeTab = 'basic';
  data.selectedCourseData = null;
  data.relatedSections = [];
  data.relatedTotal = 0;
};

/**
 * 表单操作相关函数
 */
const handleAddSection = () => {
  data.isEdit = false;
  data.formType = 'details';
  data.isFromBasicList = false;
  resetForm();
  data.dialogVisible = true;
};

const handleEditSection = (row) => {
  const originalTab = data.activeTab;
  data.isFromBasicList = originalTab === 'basic';
  data.formType = data.isFromBasicList ? 'basic' : 'details';
  
  data.isEdit = true;
  data.selectedRow = { ...row };
  
  setTimeout(() => {
    if (data.formType === 'basic' && basicFormRef.value) {
      basicFormRef.value.watchProps({ initialData: row });
    } else if (data.formType === 'details' && detailsFormRef.value) {
      detailsFormRef.value.watchProps({ initialData: row });
    }
  }, 100);
  
  data.dialogVisible = true;
};

const resetForm = () => {
  data.selectedRow = null;
  data.isFromBasicList = false;
  if (basicFormRef.value) basicFormRef.value.resetForm();
  if (detailsFormRef.value) detailsFormRef.value.resetForm();
};

const submitForm = async () => {
  try {
    let formData, url;

    if (data.formType === 'basic' && basicFormRef.value) {
      await basicFormRef.value.validate();
      formData = basicFormRef.value.getFormData();
      if (formData && formData.id !== undefined) {
        formData.courseId = formData.id;
        formData.id = data.selectedRow.id;
      }
      url = '/c_select/updateBasic';
    } else if (data.formType === 'details' && detailsFormRef.value) {
      await detailsFormRef.value.sectionFormRef.validate();
      formData = detailsFormRef.value.getFormData();
      url = data.isFromBasicList ? '/c_select/addDetails' : '/c_select/updateDetails';
    } else {
      return;
    }

    data.isSubmitting = true;
    const res = await request.post(url, formData);

    if (res.code === '200') {
      ElMessage.success(data.isEdit ? '编辑成功' : '新增成功');
      data.dialogVisible = false;

      if (data.formType === 'basic') {
        fetchSections();
      } else if (data.formType === 'details') {
        if (data.activeTab === 'details' && data.selectedCourseData) {
          fetchRelatedSections(data.selectedCourseData.courseId);
        } else {
          fetchSections();
        }
      }
    } else {
      ElMessage.error(res.msg || (data.isEdit ? '编辑失败' : '新增失败'));
    }
  } catch (error) {
    if (!error.message?.includes('validation failed')) {
      ElMessage.error(data.isEdit ? '编辑失败' : '新增失败');
      console.error('表单提交错误:', error);
    }
  } finally {
    data.isSubmitting = false;
  }
};

/**
 * 删除操作相关函数
 */
const deleteItems = async (ids) => {
  if (!ids || ids.length === 0) {
    ElMessage.warning('请选择要删除的数据');
    return;
  }

  try {
    await ElMessageBox.confirm(
      '删除后数据无法恢复，您确定删除吗？',
      '删除确认',
      { type: 'warning' }
    );

    const res = await request.delete('/c_select/delete', { data: ids });
    if (res.code === '200') {
      ElMessage.success('删除成功');
      fetchSections();

      if (data.activeTab === 'details' && data.selectedCourseData) {
        data.relatedSections = data.relatedSections.filter(item => !ids.includes(item.id));
        data.relatedTotal = data.relatedSections.length;
      }
    } else {
      ElMessage.error(res.msg);
    }
  } catch (error) {
    if (error.message && !error.message.includes('cancel')) {
      ElMessage.error('删除失败');
      console.error('删除错误:', error);
    }
  }
};

const handleDelete = (id) => deleteItems([id]);
const handleBatchDelete = () => {
  const ids = data.selectedRows.map(row => row.id);
  deleteItems(ids);
};

/**
 * 分页和选择相关函数
 */
const handleSelectionChange = (val) => {
  data.selectedRows = val;
};

const handleSizeChange = (size) => {
  data.pagination.pageSize = size;
  fetchSections();
};

const handleCurrentChange = (current) => {
  data.pagination.currentPage = current;
  fetchSections();
};

const handleRelatedSizeChange = (size) => {
  data.relatedPagination.pageSize = size;
  if (data.selectedCourseData) {
    fetchRelatedSections(data.selectedCourseData.courseId);
  }
};

const handleRelatedCurrentChange = (current) => {
  data.relatedPagination.currentPage = current;
  if (data.selectedCourseData) {
    fetchRelatedSections(data.selectedCourseData.courseId);
  }
};

/**
 * 查询重置函数
 */
const resetQuery = () => {
  data.queryParams = {
    courseId: null,
    courseName: '',
    creditsType: '',
    teacherName: '',
    semester: '',
    status: ''
  };
  data.pagination.currentPage = 1;
  data.activeTab = 'basic';
  fetchSections();
};

/**
 * 辅助函数
 */
const getCreditTypeLabel = (value) => {
  const entry = Object.entries(typeEnum.CREDITTYPE).find(([_, item]) => item.value === value);
  return entry ? entry[1].label : value;
};

const getCreditTypeTagType = (value) => {
  const typeMap = {
    'PUBLIC': 'primary',
    'FOREIGN_LANGUAGE': 'success',
    'SPORT': 'warning',
    'ART': 'info',
    'OTHER': 'danger'
  };
  return typeMap[value] || 'info';
};

// 监听器
watch(
  () => data.queryMode,
  (newMode) => {
    if (newMode === 'detailed') {
      let courseId = data.queryParams.courseId;
      let numericCourseId;
      if (courseId === null || courseId === '') {
        numericCourseId = null;
      } else {
        numericCourseId = Number(courseId);
        if (isNaN(numericCourseId)) {
          ElMessage.error('无效的课程ID');
          return;
        }
      }
      data.selectedCourseData = { courseId: numericCourseId };
      data.relatedPagination.currentPage = 1;
      fetchRelatedSections(numericCourseId);
      data.activeTab = 'details';
    } else {
      data.pagination.currentPage = 1;
      fetchSections();
      data.activeTab = 'basic';
    }
  }
);

// 初始化
onMounted(() => fetchSections());
</script>

<style scoped>
/* 导入公共列表页样式 */
@import "@/assets/css/manager/list.css";

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

/* 表单类型切换样式 */
.form-type-toggle {
  margin-bottom: var(--space-lg);
  background-color: var(--bg-gray);
  padding: var(--space-md);
  border-radius: var(--radius-md);
}

.form-type-radio-group {
  width: 100%;
  display: flex;
  gap: var(--space-sm);
  background-color: var(--bg-white);
  padding: var(--space-xs);
  border-radius: var(--radius-md);
  box-shadow: var(--shadow-sm);
}

.form-type-button {
  flex: 1;
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
  height: 40px;
  border-radius: var(--radius-sm) !important;
  transition: all var(--transition-normal);
}

:deep(.form-type-button .el-radio-button__inner) {
  border: none !important;
  color: var(--text-secondary);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-sm) !important;
  transition: all var(--transition-normal);
}

:deep(.form-type-button.is-active .el-radio-button__inner) {
  background-color: var(--manager-primary) !important;
  color: #ffffff !important;
  box-shadow: 0 2px 8px rgba(5, 150, 105, 0.3);
}

:deep(.form-type-button:hover .el-radio-button__inner:not(.is-active)) {
  color: var(--manager-primary);
  background-color: rgba(5, 150, 105, 0.1);
}

/* 查询模式切换样式 */
.query-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-md);
}

.mode-switch {
  display: flex;
  align-items: center;
}

.query-mode-toggle {
  display: flex;
  background-color: var(--bg-gray);
  border-radius: 20px;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.1);
  transition: all var(--transition-normal);
}

.mode-btn {
  padding: 6px 20px;
  font-size: 13px;
  font-weight: var(--font-weight-medium);
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
}

:deep(.query-mode-toggle .el-radio-button) {
  border: none;
  margin: 0;
}

:deep(.query-mode-toggle .el-radio-button .el-radio-button__inner) {
  border: none;
  background: transparent;
  color: var(--text-secondary);
  border-radius: 18px;
  padding: 6px 20px;
  font-size: 13px;
  font-weight: var(--font-weight-medium);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: none;
}

:deep(.query-mode-toggle .el-radio-button.is-active .el-radio-button__inner) {
  background: var(--manager-primary-gradient);
  color: white;
  box-shadow: 0 2px 8px rgba(5, 150, 105, 0.3);
  transform: translateY(-1px);
}

:deep(.query-mode-toggle .el-radio-button:not(.is-active):hover .el-radio-button__inner) {
  background-color: rgba(5, 150, 105, 0.1);
  color: var(--manager-primary);
}

/* 分页样式 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: var(--space-lg) 0;
  margin-top: var(--space-lg);
}

/* 详细查询行样式 */
.detailed-query-row {
  margin-top: 6px;
  padding-top: var(--space-sm);
  border-top: 1px dashed var(--border-color);
}

/* 优化查询输入框样式 */
:deep(.query-card .el-input),
:deep(.query-card .el-select) {
  width: 100%;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header-card {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-md);
  }

  .el-col {
    margin-bottom: var(--space-md);
  }

  .query-title {
    flex-direction: column;
    align-items: flex-start;
    gap: var(--space-sm);
  }

  .mode-switch {
    align-self: flex-end;
  }

  .query-mode-toggle {
    padding: 1px;
  }

  .mode-btn {
    padding: 5px 16px;
    font-size: var(--font-size-xs);
  }

  :deep(.query-mode-toggle .el-radio-button .el-radio-button__inner) {
    padding: 5px 16px;
    font-size: var(--font-size-xs);
  }
}
</style>