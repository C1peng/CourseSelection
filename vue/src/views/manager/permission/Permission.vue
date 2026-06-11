<template>
  <div class="container">
    <!-- 顶部操作栏 -->
    <div class="header-card">
      <div class="header-title">
        <h2><el-icon>
            <Lock />
          </el-icon>权限管理</h2>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleAddPermission">
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
          <el-input v-model="data.name" prefix-icon="Search" placeholder="请输入权限名称" clearable @clear="reset"
            @keyup.enter="load" />
        </el-col>
        <el-col :span="5">
          <el-input v-model="data.code" prefix-icon="Search" placeholder="请输入权限编码" clearable @clear="reset"
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
          <h3 class="table-title">权限列表</h3>
          <el-tag class="total-record-tag">共 {{ data.total }} 条记录</el-tag>
        </div>
      </div>
      <el-table v-loading="loading" stripe :data="data.tableData" @selection-change="handleSelectionChange"
        :header-cell-style="{ backgroundColor: '#fafafa' }">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="序号" align="center" width="80" />
        <el-table-column prop="permissionName" label="权限名称" align="center" min-width="150" />
        <el-table-column prop="permissionCode" label="权限编码" align="center" min-width="150" />
        <el-table-column prop="module" label="模块" align="center" min-width="150" />
        <el-table-column prop="action" label="操作" align="center" min-width="150" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template #default="scope">
            <div style="display: flex; justify-content: center; gap: 8px;">
              <el-button type="primary" size="small" :icon="Edit" @click="handleEditPermission(scope.row)" title="编辑"></el-button>
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

    <!-- 权限编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" destroy-on-close center>
      <PermissionForm ref="permissionFormRef" :initial-data="permissionFormData" />
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 1. Vue核心API导入
import { ref, reactive, computed, onMounted } from "vue";

// 2. 外部UI组件和图标导入
import { ElMessage, ElMessageBox, ElLoading } from "element-plus";
import { Delete, Edit, Lock, Plus, Search } from "@element-plus/icons-vue";
import PermissionForm from './PermissionForm.vue';

// 3. 工具函数导入
import request from "@/utils/request.js";

// ===== 组件引用 =====
const permissionFormRef = ref(null);

// ===== 响应式数据 - 列表数据 =====
const data = reactive({
  formVisible: false,
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  name: null,
  code: null,
  ids: []
});

// ===== 权限表单数据 =====
const permissionFormData = ref({});

// ===== 状态控制变量 =====
const isEditMode = ref(false);
const loading = ref(false);
const dialogVisible = ref(false);

// ===== 计算属性 =====
const dialogTitle = computed(() => {
  return isEditMode.value ? '编辑权限' : '新增权限';
});

// ===== 生命周期钩子 =====
onMounted(() => {
  load();
});

// ===== 核心数据加载函数 =====
const load = () => {
  loading.value = true;

  // 准备查询参数
  const permissionDTO = {
    pageNum: data.pageNum || 1,
    pageSize: data.pageSize || 10,
    name: data.name || undefined,
    code: data.code || undefined
  };

  // 发送请求
  request.post('/permission/selectPage', permissionDTO)
    .then(res => {
      if (res.code === '200') {
        data.tableData = res.data?.list || [];
        data.total = res.data?.total || 0;
      } else {
        console.error('请求失败:', res.msg);
      }
    }).catch(error => {
      console.error('请求出错:', error);
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
  data.name = null;
  data.code = null;
  load();
};

// ===== 表单操作相关函数 =====
const handleAddPermission = () => {
  isEditMode.value = false;
  permissionFormData.value = {};
  dialogVisible.value = true;
  // 确保组件已经挂载后重置表单
  setTimeout(() => {
    if (permissionFormRef.value) {
      permissionFormRef.value.resetForm();
    }
  }, 100);
};

const handleEditPermission = (row) => {
  isEditMode.value = true;
  permissionFormData.value = JSON.parse(JSON.stringify(row));
  dialogVisible.value = true;
  // 确保组件已经挂载后更新表单数据
  setTimeout(() => {
    if (permissionFormRef.value) {
      permissionFormRef.value.watchProps?.();
    }
  }, 100);
};

const handleSubmit = async () => {
  try {
    const isValid = await permissionFormRef.value.validate();
    if (!isValid) {
      ElMessage.warning('表单验证失败，请检查必填项');
      return;
    }
    
    const formData = permissionFormRef.value.getFormData();
    
    if (formData.id) {
      // 更新操作
      request.put('/permission/update', formData).then(res => {
        if (res.code === '200') {
          ElMessage.success('权限更新成功');
          dialogVisible.value = false;
          load();
        } else {
          ElMessage.error(res.msg);
        }
      });
    } else {
      // 添加操作
      request.post('/permission/add', formData).then(res => {
        if (res.code === '200') {
          ElMessage.success('权限添加成功');
          dialogVisible.value = false;
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
      request.delete("/permission/delete", { data: ids }).then(res => {
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
</script>

<style scoped>
/* 导入公共列表页样式 */
@import "@/assets/css/manager/list.css";
</style>