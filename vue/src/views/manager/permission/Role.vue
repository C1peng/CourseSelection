<template>
  <div class="container">
    <!-- 顶部操作栏 -->
    <div class="header-card">
      <div class="header-title">
        <h2><el-icon>
            <UserFilled />
          </el-icon>角色管理</h2>
      </div>
      <div class="header-actions">
        <el-button type="primary" @click="handleAddRole">
          <el-icon>
            <Plus />
          </el-icon>新增
        </el-button>
        <!-- 批量删除按钮已删除 -->
      </div>
    </div>

    <!-- 主体内容区域 -->
    <el-card class="main-card" shadow="hover">
      <div class="main-content">
        <!-- 左侧角色列表 -->
        <div class="role-list-section">
          <!-- 角色列表标题 -->
          <div class="role-section-header">
            <h3>角色列表</h3>
            <el-tag size="small" type="info">{{ roleList.length }} 个角色</el-tag>
          </div>
          
          <!-- 角色列表 -->
          <div class="role-list">
            <div
              v-for="role in roleList"
              :key="role.id"
              :class="['role-list-item', { 'active': currentRole?.id === role.id }]"
              @click="handleRoleSelect(role)"
            >
              <div class="role-item-content">
                <div class="role-item-main">
                  <div class="role-item-header">
                    <div class="role-item-title">{{ role.roleName }}</div>
                    <el-tag size="small" :type="role.roleCode === 'ADMIN' ? 'danger' : role.roleCode === 'TEACHER' ? 'success' : 'info'">
                      {{ role.roleCode }}
                    </el-tag>
                  </div>
                  <div class="role-item-info">
                    <span class="role-permission-count">
                      <el-icon><Check /></el-icon>
                      {{ role.permissionNames ? Object.keys(role.permissionNames).length : 0 }} 个权限
                    </span>
                  </div>
                </div>
                <div class="role-item-actions">
                  <el-button type="primary" size="small" circle @click.stop="handleEditRole(role)" title="编辑">
                    <el-icon><Edit /></el-icon>
                  </el-button>
                  <el-button type="danger" size="small" circle @click.stop="del(role.id)" title="删除">
                    <el-icon><Delete /></el-icon>
                  </el-button>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 右侧权限与用户分配 -->
        <div class="permission-assign-section">
          <!-- 权限分配区域 - 仅在选择角色时显示 -->
          <div v-if="currentRole">
            <div class="permission-header">
              <h3>{{ currentRole.roleName }} - 权限分配</h3>
              <div class="permission-header-info">
                <el-tag size="small" type="info">{{ currentRole.roleCode }}</el-tag>
              </div>
            </div>
            
            <!-- 权限分配表单 -->
            <div class="assign-content">
              <PermissionAssignForm
                :role-id="currentRole.id"
                :initial-permissions="permissionNamesObj"
                @permission-updated="handlePermissionUpdated"
              />
            </div>
          </div>
          <!-- 空状态提示 - 未选择角色时显示 -->
          <div v-else class="permission-empty">
            <el-empty description="请选择一个角色进行权限分配" />
          </div>
        </div>
      </div>
    </el-card>

    <!-- 角色编辑对话框 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" destroy-on-close center>
      <RoleForm ref="roleFormRef" :initial-data="roleFormData" />
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
import { ref, reactive, computed, onMounted, watch } from "vue";

// 2. 外部UI组件和图标导入
import { ElMessage, ElMessageBox, ElLoading } from "element-plus";
import { Delete, Edit, Setting, Plus, Search, UserFilled, Check, Refresh } from "@element-plus/icons-vue";
import RoleForm from './RoleForm.vue';
import PermissionAssignForm from './PermissionAssignForm.vue';

// 3. 工具函数导入
import request from "@/utils/request.js";

// ===== 组件引用 =====
const roleFormRef = ref(null);

// 响应式数据 =====
// 角色相关
const roleList = ref([]);
const currentRole = ref(null);
const currentRoleId = ref(null);
// selectedRoleIds已删除

// 权限相关
const permissionNamesObj = ref({}); // 存储permissionNames对象

// 表单相关
const roleFormData = ref({});
const isEditMode = ref(false);
const loading = ref(false);
const dialogVisible = ref(false);

// 标签页相关
const activeTab = ref('permissions');

// 事件处理函数 - 权限更新
const handlePermissionUpdated = (data) => {
  // 重新加载角色列表，获取最新的permissionNames
  loadRoles().then(() => {
    // 重新选择当前角色，更新permissionNamesObj和已分配权限列表
    const updatedRole = roleList.value.find(role => role.id === data.roleId);
    if (updatedRole) {
      handleRoleSelect(updatedRole);
    }
  });
};

// 计算属性
const dialogTitle = computed(() => {
  return isEditMode.value ? '编辑角色' : '新增角色';
});

// ===== 生命周期钩子 =====
onMounted(() => {
  loadRoles();
});

// ===== 核心数据加载函数 =====
// 加载角色列表
const loadRoles = () => {
  loading.value = true;
  // 返回Promise，以便调用方可以等待请求完成
  return request.post('/role/selectPage', {
    pageNum: 1,
    pageSize: 100, // 加载所有角色
    status: 1
  })
    .then(res => {
      if (res.code === '200') {
        roleList.value = res.data?.list || [];
      }
      return res;
    }).finally(() => {
      loading.value = false;
    });
};


// ===== 事件处理函数 =====

// 选择角色 - 实现二次点击收起功能
const handleRoleSelect = (data) => {
  // 检查是否点击的是当前选中的角色
  if (currentRole.value && currentRole.value.id === data.id) {
    // 二次点击，清空当前角色，显示用户分配
    currentRole.value = null;
    currentRoleId.value = null;
    permissionNamesObj.value = {};
  } else {
    // 点击新角色，显示权限分配
    currentRole.value = data;
    currentRoleId.value = data.id;
    // 保存permissionNames对象
    permissionNamesObj.value = data.permissionNames || {};
  }
};

// ===== 表单操作相关函数 =====
const handleAddRole = () => {
  isEditMode.value = false;
  roleFormData.value = {};
  dialogVisible.value = true;
  // 确保组件已经挂载后重置表单
  setTimeout(() => {
    if (roleFormRef.value) {
      roleFormRef.value.resetForm();
    }
  }, 100);
};

const handleEditRole = (row) => {
  isEditMode.value = true;
  roleFormData.value = JSON.parse(JSON.stringify(row));
  dialogVisible.value = true;
  // 确保组件已经挂载后更新表单数据
  setTimeout(() => {
    if (roleFormRef.value) {
      roleFormRef.value.watchProps?.();
    }
  }, 100);
};

const handleSubmit = async () => {
  try {
    const isValid = await roleFormRef.value.validate();
    if (!isValid) {
      ElMessage.warning('表单验证失败，请检查必填项');
      return;
    }
    
    const formData = roleFormRef.value.getFormData();
    
    if (formData.id) {
      // 更新操作
      request.put('/role/update', formData).then(res => {
        if (res.code === '200') {
          ElMessage.success('角色更新成功');
          dialogVisible.value = false;
          loadRoles();
          // 如果更新的是当前角色，重新加载权限
          if (currentRole.value && currentRole.value.id === formData.id) {
            currentRole.value = formData;
          }
        } else {
          ElMessage.error(res.msg);
        }
      });
    } else {
      // 添加操作
      request.post('/role/add', formData).then(res => {
        if (res.code === '200') {
          ElMessage.success('角色添加成功');
          dialogVisible.value = false;
          loadRoles();
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
      request.delete("/role/delete", { data: ids }).then(res => {
        if (res.code === '200') {
          ElMessage.success('操作成功');
          loadRoles();
          // 如果删除的是当前角色，清空当前角色
          if (currentRole.value && ids.includes(currentRole.value.id)) {
            currentRole.value = null;
            currentRoleId.value = null;
            checkedPermissions.value = [];
          }
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

// 批量删除函数已删除

// 监听选中角色变化
watch(() => currentRoleId.value, (newId) => {
  if (newId) {
    // 不再需要发送请求，直接从角色数据中获取权限
  }
});

</script>

<style scoped>
/* 导入公共列表页样式 */
@import "@/assets/css/manager/list.css";

.main-card {
  margin-top: var(--space-lg);
}

.main-content {
  display: flex;
  gap: var(--space-lg);
  min-height: 600px;
}

/* 左侧角色列表 */
.role-list-section {
  flex: 1;
  min-width: 250px;
  max-width: 300px;
  border-right: 1px solid var(--border-color);
  padding-right: var(--space-lg);
}

.role-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-lg);
  padding-bottom: var(--space-sm);
  border-bottom: 2px solid var(--manager-primary);
}

.role-section-header h3 {
  margin: 0;
  font-size: var(--font-size-md);
  font-weight: var(--font-weight-bold);
}

.role-list {
  height: 500px;
  overflow-y: auto;
}

.role-list-item {
  transition: all var(--transition-normal);
  cursor: pointer;
  border-radius: var(--radius-sm);
  margin-bottom: var(--space-sm);
  border: 1px solid transparent;
}

.role-list-item:hover {
  background-color: var(--bg-gray);
  border-color: var(--border-color);
}

.role-list-item.active {
  background-color: rgba(5, 150, 105, 0.1);
  border-color: var(--manager-primary);
}

.role-item-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: var(--space-md) var(--space-lg);
  width: 100%;
}

.role-item-main {
  flex: 1;
  min-width: 0;
}

.role-item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-sm);
}

.role-item-title {
  font-size: 15px;
  font-weight: var(--font-weight-semibold);
  color: var(--text-primary);
  margin-bottom: 0;
}

.role-item-info {
  display: flex;
  gap: var(--space-sm);
  align-items: center;
  margin-top: var(--space-sm);
}

.role-permission-count {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  display: flex;
  align-items: center;
  gap: var(--space-xs);
  background-color: var(--bg-gray);
  padding: 2px var(--space-sm);
  border-radius: 12px;
}

.role-item-actions {
  display: flex;
  gap: var(--space-sm);
  margin-left: var(--space-md);
  margin-top: 4px;
}

/* 右侧权限分配 */
.permission-assign-section {
  flex: 2;
  min-width: 500px;
}

.permission-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-lg);
  padding-bottom: var(--space-sm);
  border-bottom: 1px solid var(--border-color);
}

.permission-header h3 {
  margin: 0;
}

.permission-header-info {
  display: flex;
  gap: var(--space-sm);
  align-items: center;
}

/* 标签页样式 */
.assign-tabs {
  margin-top: var(--space-lg);
}

/* 空状态 */
.permission-empty {
  flex: 2;
  min-width: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  height: 500px;
  background-color: var(--bg-gray);
  border-radius: var(--radius-sm);
}

/* 响应式设计 */
@media screen and (max-width: 1024px) {
  .main-content {
    flex-direction: column;
  }
  
  .role-list-section {
    max-width: 100%;
    border-right: none;
    border-bottom: 1px solid var(--border-color);
    padding-right: 0;
    padding-bottom: var(--space-lg);
  }
  
  .role-list {
    height: 200px;
  }
  
  .permission-assign-section,
  .permission-empty {
    min-width: 100%;
  }
}
</style>