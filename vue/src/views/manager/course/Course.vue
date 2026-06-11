<template>
  <div class="container">
    <!-- 顶部操作栏 -->
    <div class="header-card">
      <div class="header-title">
        <h2><el-icon>
            <Document />
          </el-icon>课程信息</h2>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleAdd">
          <el-icon>
            <Plus />
          </el-icon>新增
        </el-button>
        <el-button type="danger" :disabled="data.ids.length === 0" @click="delBatch">
          <el-icon>
            <Delete />
          </el-icon>批量删除
        </el-button>
      </div>
    </div>

    <!-- 查询条件卡片 -->
    <el-card class="query-card" shadow="hover">
      <div class="query-title">
        <h3>搜索查询</h3>
      </div>
      <el-row :gutter="20">
        <el-col :span="5">
          <el-input v-model="data.courseInfo" prefix-icon="Search" placeholder="请输入课程编号/名称" clearable @clear="reset"
            @keyup.enter="load" />
        </el-col>
        <el-col :span="5">
          <el-select v-model="data.courseType" placeholder="请选择课程类型" clearable>
            <el-option v-for="(item) in typeEnum.COURSETYPE" :key="item.value" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-col>
        <el-col :span="5">
          <el-select v-model="data.status" placeholder="请选择课程状态" clearable>
            <el-option v-for="(item) in typeEnum.COURSESTATUS" :key="item.value" :label="item.label"
              :value="item.value"></el-option>
          </el-select>
        </el-col>
        <el-col :span="4">
          <div style="display: flex; gap: 8px;">
            <el-button type="primary" @click="load">
              <el-icon>
                <Search />
              </el-icon>查询
            </el-button>
            <el-button @click="reset" class="btn-effect">重置</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 列表展示区域 -->
    <el-card class="table-card" shadow="hover">
      <div class="table-header">
        <div class="table-info">
          <h3 class="table-title">课程列表</h3>
          <el-tag class="total-record-tag">共 {{ data.total }} 条记录</el-tag>
        </div>
      </div>
      <el-table v-loading="loading" stripe :data="data.tableData" @selection-change="handleSelectionChange"
        :header-cell-style="{ backgroundColor: '#fafafa' }">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="序号" align="center" width="80" />
        <el-table-column prop="courseId" label="课程编号" align="center" width="120" />
        <el-table-column prop="courseName" label="课程名" align="center" min-width="150">
          <template v-slot="scope">
            <span class="course-name" @click="handleDetail(scope.row)">{{ scope.row.courseName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="hours" label="学时" align="center" width="80" />
        <el-table-column prop="credits" label="学分" align="center" width="80">
          <template v-slot="scope">
            <span class="credit-badge">
              {{ scope.row.credits }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="courseType" label="课程类型" align="center" width="120">
          <template v-slot="scope">
            <el-tag :type="scope.row.courseType === 0 ? 'primary' : 'info'">
              {{ $enum.getDescByValue('COURSETYPE', scope.row.courseType) }}
            </el-tag>
          </template>
        </el-table-column>

        <el-table-column prop="deptName" label="学院" align="center" min-width="100" />

        <el-table-column prop="status" label="状态" align="center" width="100">
          <template v-slot="scope">
            <el-tag :type="scope.row.status === 0 ? 'warning' : 'success'">
              {{ $enum.getDescByValue('COURSESTATUS', scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template v-slot="scope">
            <div style="display: flex; justify-content: center; gap: 8px;">
              <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(scope.row)" title="编辑"></el-button>
              <el-button type="danger" size="small" :icon="Delete" @click="del(scope.row.id)" title="删除"></el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <div class="pagination-container" v-if="data.total">
        <el-pagination @size-change="handleSizeChange" @current-change="handleCurrentChange"
          :current-page="data.pageNum" :page-sizes="[10, 20, 50, 100]" :page-size="data.pageSize"
          layout="total, sizes, prev, pager, next, jumper" :total="data.total" />
      </div>
    </el-card>

    <!-- 居中对话框 -->
    <el-dialog :title="drawerTitle" v-model="data.formVisible"
      :width="data.formType === 'courseSelection' ? '70%' : '60%'" destroy-on-close center>
      <div style="margin-bottom: 20px; background-color: #f8f9fa; padding: 15px; border-radius: 8px;" v-if="isEditMode">
        <el-radio-group v-model="data.formType" class="form-type-radio-group">
          <el-radio-button :value="'courseInfo'" class="form-type-button">课程信息</el-radio-button>
          <el-radio-button :value="'courseOffer'" class="form-type-button">开课信息</el-radio-button>
          <el-radio-button :value="'courseSelection'" class="form-type-button">选课信息</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 使用独立的组件 -->
      <CourseInfoForm v-if="data.formType === 'courseInfo'" ref="courseInfoFormRef"
        :initial-data="courseInfoInitialData" />
      <CourseOfferForm v-else-if="data.formType === 'courseOffer'" ref="courseOfferFormRef"
        :initial-data="courseOfferInitialData" />
      <CourseSelectionForm v-else-if="data.formType === 'courseSelection'" ref="courseSelectionFormRef"
        :initial-data="courseSelectionInitialData" />

      <template #footer>
        <span>
          <el-button @click="data.formVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog :title="selectedCourse?.courseName || '课程详情'" v-model="detailVisible" width="40%" destroy-on-close>
      <div v-if="selectedCourse" class="detail">
        <div class="detail-item"><span class="label">课程编号：</span><span class="value">{{ selectedCourse.courseId
            }}</span></div>
        <div class="detail-item"><span class="label">课程名称：</span><span class="value">{{
          selectedCourse.courseName
            }}</span></div>
        <div class="detail-item"><span class="label">学时：</span><span class="value">{{ selectedCourse.hours }}
            学时</span>
        </div>
        <div class="detail-item"><span class="label">学分：</span><span class="value">{{ selectedCourse.credits }}
            学分</span></div>
        <div class="detail-item"><span class="label">学院：</span><span class="value">{{ selectedCourse.deptName }}</span>
        </div>
        <div class="detail-item"><span class="label">课程类型：</span><span class="value">{{
          $enum.getDescByValue('COURSETYPE', selectedCourse.courseType) }}</span></div>
        <div class="detail-item"><span class="label">状态：</span><span class="value">{{
          $enum.getDescByValue('COURSESTATUS', selectedCourse.status) }}</span></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
// 1. Vue核心API导入
import { reactive, ref, computed, watch, onMounted } from "vue";

// 2. 外部UI组件和图标导入
import { ElMessage, ElMessageBox, ElLoading, ElTag } from "element-plus";
import { Delete, Edit, Document, Plus, Search } from "@element-plus/icons-vue";

// 3. 工具函数导入
import request from "@/utils/request.js";

// 4. 常量和类型导入
import typeEnum from '@/constant/module/type.js';

// 5. 子组件导入
import CourseInfoForm from './CourseInfoForm.vue';
import CourseOfferForm from './CourseOfferForm.vue';
import CourseSelectionForm from './CourseSelectionForm.vue';

// ===== 组件引用 =====
const courseInfoFormRef = ref(null);
const courseOfferFormRef = ref(null);
const courseSelectionFormRef = ref(null);

// ===== 响应式数据 - 列表数据 =====
const data = reactive({
  formVisible: false,
  formType: 'courseInfo', // 'courseInfo' 或 'courseOffer'
  tableData: [], // 建议定义具体的CourseItem类型
  pageNum: 1,
  pageSize: 10,
  total: 0,
  courseInfo: null,
  courseType: null,
  status: null,
  ids: []
});

// ===== 状态控制变量 =====
const isEditMode = ref(false);
const loading = ref(false);

// ===== 初始数据 =====
const courseInfoInitialData = ref({});
const courseOfferInitialData = ref({});
const courseSelectionInitialData = ref({});

// ===== 详情对话框相关 =====
const detailVisible = ref(false);
const selectedCourse = ref(null);

// ===== 计算属性 =====
const drawerTitle = computed(() => {
  return isEditMode.value ? '课程管理' : '新增课程信息';
});

// ===== 生命周期钩子 =====
onMounted(() => {
  load();
});

// ===== 核心数据加载函数 =====
const load = () => {
  loading.value = true;

  // 准备查询参数，处理null值并设置默认值
  const courseDTO = {
    pageNum: data.pageNum || 1,  // 确保默认值为1
    pageSize: data.pageSize || 10,  // 确保默认值为10
    courseInfo: data.courseInfo || undefined,
    courseType: data.courseType ?? undefined,
    status: data.status ?? undefined
  };

  // 直接发送courseDTO作为请求体
  request.post('/course/selectPage', courseDTO)
    .then(res => {
      if (res.code === '200') {
        data.tableData = res.data?.list || [];
        data.total = res.data?.total || 0;
      }
    }).finally(() => {
      loading.value = false;
    });
};

// ===== 分页相关函数 =====
const handleSizeChange = (size) => {
  data.pageSize = size;
  data.pageNum = 1;
  load();
};

const handleCurrentChange = (current) => {
  data.pageNum = current;
  load();
};

// ===== 查询相关函数 =====
const reset = () => {
  data.courseInfo = null;
  data.courseType = null;
  data.status = null;
  load();
};

// ===== 表单操作相关函数 =====
const handleAdd = () => {
  // 新增时强制设置为课程信息表单
  data.formType = 'courseInfo';
  isEditMode.value = false;

  // 重置初始数据
  courseInfoInitialData.value = {};
  courseOfferInitialData.value = {};
  courseSelectionInitialData.value = {};
  data.formVisible = true;

  // 延迟调用，确保组件已渲染
  setTimeout(() => {
    if (courseInfoFormRef.value) {
      courseInfoFormRef.value.resetForm();
    }
    if (courseOfferFormRef.value) {
      courseOfferFormRef.value.resetForm();
    }
    if (courseSelectionFormRef.value) {
      courseSelectionFormRef.value.resetForm();
    }
  }, 100);
};

const handleEdit = (row) => {
  data.formType = 'courseInfo';
  isEditMode.value = true;

  // 设置初始数据
  courseInfoInitialData.value = {
    id: row.id,
    courseId: row.courseId,
    courseName: row.courseName,
    hours: row.hours,
    credits: row.credits,
    courseType: row.courseType, // 保持原值传递，让子组件处理枚举值
    status: row.status, // 保持原值传递，让子组件处理枚举值
    deptId: row.deptId
  };

  courseOfferInitialData.value = {
    id: row.id,
    courseId: row.courseId,
    status: row.status // 保持原值传递，让子组件处理枚举值
  };

  courseSelectionInitialData.value = {
    id: row.id,
    // courseId: row.courseId,
    courseName: row.courseName
  };

  data.formVisible = true;

  // 延迟调用，确保组件已渲染
  setTimeout(() => {
    if (courseInfoFormRef.value) {
      courseInfoFormRef.value.watchProps();
    }
    if (courseOfferFormRef.value) {
      courseOfferFormRef.value.watchProps();
    }
    if (courseSelectionFormRef.value) {
      courseSelectionFormRef.value.watchProps();
    }
  }, 100);
};

const save = async () => {
  // 根据当前选择的表单类型单独提交数据
  if (data.formType === 'courseInfo' && courseInfoFormRef.value) {
    try {
      // 调用表单验证
      await courseInfoFormRef.value.validate();
      
      // 验证通过后获取课程信息表单数据
      const submitData = courseInfoFormRef.value.getFormData();

      if (submitData.id) {
        // 更新操作 - 使用专门的课程信息更新接口
        request.put('/course/update', submitData).then(res => {
          if (res.code === '200') {
            ElMessage.success('课程信息更新成功');
            data.formVisible = false;
            load();
          } else {
            ElMessage.error(res.msg);
          }
        });
      } else {
        // 添加操作 - 使用专门的课程信息添加接口
        request.post('/course/add', submitData).then(res => {
          if (res.code === '200') {
            ElMessage.success('课程信息添加成功');
            data.formVisible = false;
            load();
          } else {
            ElMessage.error(res.msg);
          }
        });
      }
    } catch (error) {
      // 验证失败，不提交表单
      console.log('表单验证失败:', error);
    }
  } else if (data.formType === 'courseOffer' && courseOfferFormRef.value) {
    // 获取开课信息表单数据
    const submitData = courseOfferFormRef.value.getFormData();

    if (submitData.id) {
      // 更新操作 - 使用专门的课程信息更新接口
      request.put('/course/update/offer', submitData).then(res => {
        if (res.code === '200') {
          ElMessage.success('开课信息更新成功');
          data.formVisible = false;
          load();
        } else {
          ElMessage.error(res.msg);
        }
      });
    } else {
      // 添加操作 - 使用专门的课程信息添加接口
      request.post('/course/add/offer', submitData).then(res => {
        if (res.code === '200') {
          ElMessage.success('开课信息添加成功');
          data.formVisible = false;
          load();
        } else {
          ElMessage.error(res.msg);
        }
      });
    }
  } else if (data.formType === 'courseSelection' && courseSelectionFormRef.value) {
    // 获取选课信息表单数据
    const submitData = courseSelectionFormRef.value.getFormData();
    // 添加操作 - 使用专门的选课信息添加接口
    request.post('/c_select/addBasic', submitData).then(res => {
      if (res.code === '200') {
        ElMessage.success('选课信息添加成功');
        data.formVisible = false;
      } else {
        ElMessage.error(res.msg);
      }
    });
  }
};

// ===== 删除相关函数 =====
// 统一的删除函数，接收ID数组参数
const deleteItems = (ids) => {
  if (!ids || ids.length === 0) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }

  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' })
    .then(() => {
      request.delete("/course/delete", { data: ids }).then(res => {
        if (res.code === '200') {
          ElMessage.success('操作成功');
          load();
        } else {
          ElMessage.error(res.msg);
        }
      });
    })
    .catch(err => {
      // 静默处理取消操作，不需要记录错误
    });
};

// 单条删除函数
const del = (id) => {
  deleteItems([id]); // 将单个ID转为数组传入
};

// 批量删除函数
const delBatch = () => {
  deleteItems(data.ids);
};

// ===== 其他事件处理函数 =====
const handleSelectionChange = (rows) => {
  data.ids = rows.map(v => v.id);
};

// 查看课程详情
const handleDetail = (row) => {
  selectedCourse.value = { ...row };
  detailVisible.value = true;
};

// ===== 监听器 =====
// 监听表单类型变化，在编辑模式下切换时重新调用watchProps
watch(
  () => data.formType,
  (newType, oldType) => {
    if (isEditMode.value && newType !== oldType) {
      // 延迟调用，确保组件已渲染
      setTimeout(() => {
        if (newType === 'courseInfo' && courseInfoFormRef.value) {
          courseInfoFormRef.value.watchProps();
        } else if (newType === 'courseOffer' && courseOfferFormRef.value) {
          courseOfferFormRef.value.watchProps();
        } else if (newType === 'courseSelection' && courseSelectionFormRef.value) {
          courseSelectionFormRef.value.watchProps();
        }
      }, 100);
    }
  }
);
</script>

<style scoped>
/* 导入公共列表页样式 */
@import "@/assets/css/manager/list.css";

/* 学分徽章 */
.credit-badge {
  display: inline-block;
  min-width: 20px;
  height: 20px;
  line-height: 20px;
  text-align: center;
  background-color: var(--manager-primary);
  color: white;
  border-radius: 10px;
  font-weight: var(--font-weight-medium);
  font-size: var(--font-size-xs);
  padding: 0 var(--space-xs);
}

/* 表单类型选择按钮样式 */
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

.form-type-button :deep(.el-radio-button__inner) {
  border: none !important;
  color: var(--text-secondary);
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-medium);
  border-radius: var(--radius-sm) !important;
  transition: all var(--transition-normal);
}

.form-type-button.is-active :deep(.el-radio-button__inner) {
  background-color: var(--manager-primary) !important;
  color: #ffffff !important;
  box-shadow: 0 2px 8px rgba(5, 150, 105, 0.3);
}

.form-type-button:hover :deep(.el-radio-button__inner:not(.is-active)) {
  color: var(--manager-primary);
  background-color: rgba(5, 150, 105, 0.1);
}

/* 课程名称样式 */
.course-name {
  cursor: pointer;
  color: var(--text-primary);
  font-weight: var(--font-weight-semibold);
  font-size: 15px;
  letter-spacing: 0.5px;
  line-height: 1.6;
  position: relative;
  transition: all var(--transition-normal);
  text-decoration: none;
  display: inline-block;
  padding: 2px var(--space-xs);
  border-radius: var(--radius-sm);
}

.course-name:hover {
  color: var(--manager-primary);
  background-color: rgba(5, 150, 105, 0.1);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(5, 150, 105, 0.2);
}

.course-name:active {
  transform: translateY(0);
}

/* 响应式调整 */
@media screen and (max-width: 768px) {
  .detail-item {
    flex-direction: column;
  }

  .detail-item .label {
    width: 100%;
    text-align: left;
    margin-right: 0;
    margin-bottom: var(--space-xs);
  }
}
</style>