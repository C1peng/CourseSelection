<template>
  <div class="container">
    <!-- 顶部操作栏 -->
    <div class="header-card">
      <div class="header-title">
        <h2><el-icon>
            <Document />
          </el-icon>学院信息</h2>
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
          <el-input v-model="data.deptId" prefix-icon="Search" placeholder="请输入学院编号" clearable @clear="reset"
            @keyup.enter="load" />
        </el-col>
        <el-col :span="5">
          <el-input v-model="data.deptName" prefix-icon="Search" placeholder="请输入学院名称" clearable @clear="reset"
            @keyup.enter="load" />
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
          <h3 class="table-title">学院列表</h3>
          <el-tag class="total-record-tag">共 {{ data.total }} 条记录</el-tag>
        </div>
      </div>
      <el-table v-loading="loading" stripe :data="data.tableData" @selection-change="handleSelectionChange"
        :header-cell-style="{ backgroundColor: '#fafafa' }">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="序号" align="center" width="80" />
        <el-table-column prop="deptId" label="学院编号" align="center" width="130" />
        <el-table-column prop="deptName" label="学院名" align="center" min-width="150">
          <template v-slot="scope">
            <span class="dept-name" @click="handleDetail(scope.row)">{{ scope.row.deptName }}</span>
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

    <!-- 编辑对话框 -->
    <el-dialog :title="drawerTitle" v-model="data.formVisible" :width="dialogWidth" destroy-on-close center>
      <div style="margin-bottom: 20px; background-color: #f8f9fa; padding: 15px; border-radius: 8px;" v-if="isEditMode">
        <el-radio-group v-model="data.formType" class="form-type-radio-group">
          <el-radio-button :value="'deptInfo'" class="form-type-button">学院信息</el-radio-button>
          <el-radio-button :value="'creditLine'" class="form-type-button">学分要求</el-radio-button>
        </el-radio-group>
      </div>

      <!-- 使用独立的组件 -->
      <DepartmentForm v-if="data.formType === 'deptInfo'" ref="deptFormRef" :initial-data="deptInitialData" />
      <CreditLineForm v-else-if="data.formType === 'creditLine'" ref="creditLineFormRef" :initial-data="creditLineInitialData" :is-edit="isEditMode" />

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 详情对话框 -->
    <el-dialog :title="selectedDepartment?.deptName || '学院详情'" v-model="detailVisible" width="40%" destroy-on-close>
      <div v-if="selectedDepartment" class="detail">
        <div class="detail-item"><span class="label">序号：</span><span class="value">{{ selectedDepartment.id }}</span>
        </div>
        <div class="detail-item"><span class="label">学院编号：</span><span class="value">{{ selectedDepartment.deptId
            }}</span>
        </div>
        <div class="detail-item"><span class="label">学院名称：</span><span class="value">{{ selectedDepartment.deptName
            }}</span></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
// 1. Vue核心API导入
import { reactive, ref, computed, onMounted, watch } from "vue";

// 2. 外部UI组件和图标导入
import { ElMessage, ElMessageBox, ElLoading } from "element-plus";
import { Delete, Edit, Document, Plus, Search } from "@element-plus/icons-vue";

// 3. 工具函数导入
import request from "@/utils/request.js";

// 4. 导入表单组件
import DepartmentForm from './DepartmentForm.vue';
import CreditLineForm from './CreditLineForm.vue';

// ===== 组件引用 =====
const deptFormRef = ref(null);
const creditLineFormRef = ref(null);

// ===== 响应式数据 - 列表数据 =====
const data = reactive({
  formVisible: false,
  formType: 'deptInfo', // 'deptInfo' 或 'creditLine'
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  deptId: null,
  deptName: null,
  ids: []
});

// ===== 初始数据 =====
const deptInitialData = ref({});
const creditLineInitialData = ref({});

// ===== 状态控制变量 =====
const isEditMode = ref(false);
const loading = ref(false);

// ===== 详情对话框相关 =====
const detailVisible = ref(false);
const selectedDepartment = ref(null);

// ===== 计算属性 =====
const drawerTitle = computed(() => {
  return isEditMode.value ? '编辑学院信息' : '新增学院信息';
});

// 动态对话框宽度
const dialogWidth = computed(() => {
  // 根据表单类型调整对话框宽度，与CreditLine.vue保持一致
  return data.formType === 'deptInfo' ? '45%' : '50%';
});

// ===== 生命周期钩子 =====
onMounted(() => {
  load();
});

// ===== 核心数据加载函数 =====
const load = () => {
  loading.value = true;

  // 准备查询参数
  const deptDTO = {
    pageNum: data.pageNum || 1,
    pageSize: data.pageSize || 10,
    deptId: data.deptId || undefined,
    deptName: data.deptName || undefined
  };

  // 发送请求
  request.post('/dept/selectPage', deptDTO)
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
  data.deptId = null;
  data.deptName = null;
  load();
};

// ===== 表单操作相关函数 =====
const handleAdd = () => {
  // 新增时强制设置为学院信息表单
  data.formType = 'deptInfo';
  isEditMode.value = false;

  // 重置初始数据
  deptInitialData.value = {};
  creditLineInitialData.value = {};
  
  // 重置表单
  if (deptFormRef.value) {
    deptFormRef.value.resetForm();
  }
  if (creditLineFormRef.value) {
    creditLineFormRef.value.resetForm();
  }
  
  data.formVisible = true;
};

const handleEdit = (row) => {
  data.formType = 'deptInfo';
  isEditMode.value = true;

  // 设置初始数据
  deptInitialData.value = { ...row };
  
  // 为学分要求表单设置初始数据
  creditLineInitialData.value = {
    deptId: row.deptId,
    deptName: row.deptName,
    grade: new Date().getFullYear() // 默认使用当前年份作为年级
  };
  
  // 更新表单数据
  if (deptFormRef.value) {
    deptFormRef.value.watchProps();
  }
  if (creditLineFormRef.value) {
    creditLineFormRef.value.watchProps();
  }
  
  data.formVisible = true;
};
const save = async () => {
  // 根据当前选择的表单类型单独提交数据
  if (data.formType === 'deptInfo' && deptFormRef.value) {
    try {
      // 先验证表单
      await deptFormRef.value.validate();
      // 验证通过后获取学院信息表单数据
      const formData = deptFormRef.value.getFormData();
      
      if (formData.id) {
        // 更新操作
        request.put('/dept/update', formData).then(res => {
          if (res.code === '200') {
            ElMessage.success('学院信息更新成功');
            data.formVisible = false;
            load();
          } else {
            ElMessage.error(res.msg);
          }
        });
      } else {
        // 添加操作
        request.post('/dept/add', formData).then(res => {
          if (res.code === '200') {
            ElMessage.success('学院信息添加成功');
            data.formVisible = false;
            load();
          } else {
            ElMessage.error(res.msg);
          }
        });
      }
    } catch (error) {
      // 验证失败，不提交数据
      console.log('表单验证失败:', error);
    }
  } else if (data.formType === 'creditLine' && creditLineFormRef.value) {
    try {
    // 验证表单
    const isValid = await creditLineFormRef.value.validateForm();
    if (!isValid) return;

    // 获取表单数据
    const formData = creditLineFormRef.value.getFormData();
    // 无论是否处于编辑模式，都只使用添加请求
    const url = '/creditLine/add';

    request.post(url, formData)
      .then(res => {
        if (res.code === '200') {
          ElMessage.success('学分要求添加成功');
          data.formVisible = false;
          load();
        } else {
          ElMessage.error(res.msg || '学分要求添加失败');
        }
      })
      .catch(err => {
        ElMessage.error('学分要求添加失败');
        console.error('保存学分要求错误:', err);
      });
  } catch (error) {
    // 验证失败，不提交数据
    console.log('表单验证失败:', error);
  }
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
      request.delete("/dept/delete", { data: ids }).then(res => {
        if (res.code === '200') {
          ElMessage.success('操作成功');
          load();
        } else {
          ElMessage.error(res.msg);
        }
      });
    })
    .catch(err => {
      console.error('删除操作被取消:', err);
    });
};

// 单条删除函数
const del = (id) => {
  deleteItems([id]);
};

// 批量删除函数
const delBatch = () => {
  deleteItems(data.ids);
};

// ===== 其他事件处理函数 =====
const handleSelectionChange = (rows) => {
  data.ids = rows.map(v => v.id);
};

// 查看学院详情
const handleDetail = (row) => {
  selectedDepartment.value = { ...row };
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
        if (newType === 'deptInfo' && deptFormRef.value) {
          deptFormRef.value.watchProps();
        } else if (newType === 'creditLine' && creditLineFormRef.value) {
          creditLineFormRef.value.watchProps();
        }
      }, 100);
    }
  }
);
</script>

<style scoped>
/* 导入后台列表页样式 */
@import "@/assets/css/manager/list.css";

/* 学院名称样式 */
.dept-name {
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

.dept-name:hover {
  color: var(--manager-primary);
  background-color: rgba(5, 150, 105, 0.1);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(5, 150, 105, 0.2);
}

.dept-name:active {
  transform: translateY(0);
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