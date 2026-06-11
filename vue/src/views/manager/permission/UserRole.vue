<template>
  <div class="container">
    <!-- 顶部操作栏 -->
    <div class="header-card">
      <div class="header-title">
        <h2><el-icon><UserFilled /></el-icon>角色分配管理</h2>
      </div>
    </div>

    <!-- 查询条件卡片 -->
    <el-card class="query-card" shadow="hover">
      <div class="query-title">
        <h3>搜索查询</h3>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input v-model="data.searchQuery" placeholder="搜索用户名或姓名" prefix-icon="Search" clearable
            @keyup.enter="handleSearch" />
        </el-col>
        <el-col :span="4">
          <el-select v-model="data.selectedUserType" placeholder="选择用户类型" clearable>
            <el-option label="管理员" value="ADMIN"></el-option>
            <el-option label="教师" value="TEACHER"></el-option>
            <el-option label="学生" value="STUDENT"></el-option>
          </el-select>
        </el-col>
        <el-col :span="6">
          <div class="query-buttons">
            <el-button type="primary" @click="handleSearch">
              <el-icon><Search /></el-icon>查询
            </el-button>
            <el-button @click="reset">重置</el-button>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 列表展示区域 -->
    <el-card class="table-card" shadow="hover">
      <!-- 数据概览卡片和批量操作 -->
      <div class="overview-section">
        <el-row :gutter="20">
          <el-col :span="20">
            <el-row :gutter="20" class="overview-cards">
              <el-col :span="8">
                <el-card shadow="hover" class="stat-card">
                  <div class="stat-content">
                    <div class="stat-value">{{ assignedFilteredUsers.length }}</div>
                    <div class="stat-label">已分配权限用户</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="hover" class="stat-card warning-card">
                  <div class="stat-content">
                    <div class="stat-value">{{ unassignedFilteredUsers.length }}</div>
                    <div class="stat-label">未分配权限用户</div>
                  </div>
                </el-card>
              </el-col>
              <el-col :span="8">
                <el-card shadow="hover" class="stat-card success-card">
                  <div class="stat-content">
                    <div class="stat-value">{{ filteredUsers.length }}</div>
                    <div class="stat-label">总用户数</div>
                  </div>
                </el-card>
              </el-col>
            </el-row>
          </el-col>
          <el-col :span="4">
            <div class="batch-actions">
              <el-button type="success" @click="showBatchAssignModal"
                :disabled="data.activeTab === 'assigned' || data.ids.length === 0" style="width: 100%;">
                <el-icon><Plus /></el-icon>批量分配角色
              </el-button>
              <el-button type="danger" @click="delBatch"
                :disabled="data.activeTab === 'unassigned' || data.ids.length === 0" style="width: 100%;">
                <el-icon><Delete /></el-icon>批量删除
              </el-button>
            </div>
          </el-col>
        </el-row>
      </div>

      <el-tabs v-model="data.activeTab" class="content-tabs" stretch @tab-change="handleTabChange">
        <!-- 已分配权限用户 -->
        <el-tab-pane label="已分配权限用户" name="assigned">
          <div class="table-header">
            <div class="table-info">
              <h3 class="table-title">已分配权限用户列表</h3>
              <el-tag class="total-record-tag">共 {{ assignedFilteredUsers.length }} 条记录</el-tag>
            </div>
          </div>
          <el-table v-loading="loading" stripe :data="assignedCurrentPageUsers" fit
            @selection-change="handleSelectionChange" :header-cell-style="headerCellStyle" class="related-table">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column prop="username" label="用户名" min-width="150" align="center" />
            <el-table-column prop="name" label="姓名" min-width="120" align="center" />
            <el-table-column prop="userType" label="用户类型" min-width="120" align="center">
              <template #default="scope">
                <el-tag
                  :type="scope.row.userType === 'ADMIN' ? 'danger' : scope.row.userType === 'TEACHER' ? 'success' : 'info'">
                  {{ getUserTypeName(scope.row.userType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="当前角色" min-width="150" align="center">
              <template #default="scope">
                <el-tag type="primary">
                  {{ scope.row.roleName || '未分配' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="150" fixed="right" align="center">
              <template #default="scope">
                <div style="display: flex; justify-content: center; gap: 8px;">
                  <el-button type="primary" size="small" :icon="Edit" @click="showAssignModal(scope.row)"
                    title="修改角色"></el-button>
                  <el-button type="danger" size="small" :icon="Delete" @click="del(scope.row.roleId || scope.row.id)"
                    title="删除"></el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container" v-if="assignedFilteredUsers.length">
            <el-pagination @size-change="handleAssignedSizeChange" @current-change="handleAssignedCurrentChange"
              v-model:current-page="data.assignedPageNum" v-model:page-size="data.assignedPageSize"
              :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
              :total="assignedFilteredUsers.length" />
          </div>
        </el-tab-pane>

        <!-- 未分配权限用户 -->
        <el-tab-pane label="未分配权限用户" name="unassigned">
          <div class="table-header">
            <div class="table-info">
              <h3 class="table-title">未分配权限用户列表</h3>
              <el-tag class="total-record-tag">共 {{ unassignedFilteredUsers.length }} 条记录</el-tag>
            </div>
          </div>
          <el-table v-loading="loading" stripe :data="unassignedCurrentPageUsers" fit
            @selection-change="handleSelectionChange" :header-cell-style="headerCellStyle" class="related-table">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column prop="username" label="用户名" min-width="150" align="center" />
            <el-table-column prop="name" label="姓名" min-width="120" align="center" />
            <el-table-column prop="userType" label="用户类型" min-width="120" align="center">
              <template #default="scope">
                <el-tag
                  :type="scope.row.userType === 'ADMIN' ? 'danger' : scope.row.userType === 'TEACHER' ? 'success' : 'info'">
                  {{ getUserTypeName(scope.row.userType) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="当前角色" min-width="150" align="center">
              <template #default="scope">
                <el-tag type="warning">
                  未分配
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" min-width="150" fixed="right" align="center">
              <template #default="scope">
                <div style="display: flex; justify-content: center; gap: 8px;">
                  <el-button type="success" size="small" :icon="Plus" @click="showAssignModal(scope.row)"
                    title="分配角色"></el-button>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <!-- 分页 -->
          <div class="pagination-container" v-if="unassignedFilteredUsers.length">
            <el-pagination @size-change="handleUnassignedSizeChange" @current-change="handleUnassignedCurrentChange"
              v-model:current-page="data.unassignedPageNum" v-model:page-size="data.unassignedPageSize"
              :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper"
              :total="unassignedFilteredUsers.length" />
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 角色分配弹窗 -->
    <el-dialog v-model="data.formVisible" :title="data.dialogTitle" width="40%" destroy-on-close>
      <el-form :model="data.assignForm" label-width="80px" style="padding: 20px;">
        <el-form-item label="用户名" v-if="data.selectedUser">
          <span>{{ data.selectedUser?.username }}</span>
        </el-form-item>
        <el-form-item label="姓名" v-if="data.selectedUser">
          <span>{{ data.selectedUser?.name }}</span>
        </el-form-item>
        <el-form-item label="用户类型" v-if="data.selectedUser">
          <el-tag
            :type="data.selectedUser?.userType === 'ADMIN' ? 'danger' : data.selectedUser?.userType === 'TEACHER' ? 'success' : 'info'">
            {{ data.selectedUser?.userType ? getUserTypeName(data.selectedUser.userType) : '' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="当前角色" v-if="data.selectedUser">
          <el-tag :type="data.selectedUser?.roleName ? 'primary' : 'warning'">
            {{ data.selectedUser?.roleName || '未分配' }}
          </el-tag>
        </el-form-item>
        <el-form-item label="选择角色" required>
          <el-select v-model="data.assignForm.roleId" placeholder="请选择角色" style="width: 100%;">
            <el-option v-for="role in data.allRoles" :key="role.id" :label="role.roleName" :value="role.id">
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span>
          <el-button @click="data.formVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmAssign">确认分配</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// 1. Vue核心API导入
import { reactive, ref, computed, watch, onMounted } from "vue";

// 2. 外部UI组件和图标导入
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete, Edit, Plus, Search, UserFilled } from "@element-plus/icons-vue";

// 3. 工具函数导入
import request from "@/utils/request.js";

// ===== 常量定义 =====
// 用户类型映射
const userTypeMap = {
  'ADMIN': '管理员',
  'TEACHER': '教师',
  'STUDENT': '学生'
};

// 表格头部样式
const headerCellStyle = {
  backgroundColor: '#fafafa',
  fontWeight: '600',
  color: '#606266'
};

// ===== 响应式数据 - 列表数据 =====
const data = reactive({
  tableData: [],
  allRoles: [],
  searchQuery: '',
  selectedUserType: null,
  activeTab: 'assigned',
  assignedPageNum: 1,
  assignedPageSize: 10,
  unassignedPageNum: 1,
  unassignedPageSize: 10,
  ids: [],
  formVisible: false,
  dialogTitle: '分配角色',
  selectedUser: null,
  assignForm: {
    roleId: null
  }
});

// ===== 状态控制变量 =====
const loading = ref(false);

// ===== 计算属性 =====
// 获取用户类型名称
const getUserTypeName = (userType) => {
  return userTypeMap[userType] || userType;
};

// 过滤后的用户列表
const filteredUsers = computed(() => {
  return data.tableData.filter(user => {
    const query = data.searchQuery.toLowerCase();
    const matchSearch = !data.searchQuery ||
      (user.username && user.username.toLowerCase().includes(query)) ||
      (user.name && user.name.toLowerCase().includes(query));
    const matchUserType = !data.selectedUserType || user.userType === data.selectedUserType;
    return matchSearch && matchUserType;
  });
});

// 已分配权限用户列表
const assignedFilteredUsers = computed(() => {
  return filteredUsers.value.filter(user => user.roleId || user.id > 0);
});

// 未分配权限用户列表
const unassignedFilteredUsers = computed(() => {
  return filteredUsers.value.filter(user => !user.roleId && user.id < 0);
});

// 已分配权限当前页用户
const assignedCurrentPageUsers = computed(() => {
  const start = (data.assignedPageNum - 1) * data.assignedPageSize;
  const end = start + data.assignedPageSize;
  return assignedFilteredUsers.value.slice(start, end);
});

// 未分配权限当前页用户
const unassignedCurrentPageUsers = computed(() => {
  const start = (data.unassignedPageNum - 1) * data.unassignedPageSize;
  const end = start + data.unassignedPageSize;
  return unassignedFilteredUsers.value.slice(start, end);
});

// ===== 生命周期钩子 =====
onMounted(() => {
  loadAllRoles();
  loadAllUsers();
});

// ===== 核心数据加载函数 =====
// 加载所有角色
const loadAllRoles = () => {
  loading.value = true;
  request.post('/role/selectPage', {
    pageNum: 1,
    pageSize: 100
  }).then(res => {
    if (res.code === '200') {
      data.allRoles = res.data?.list || [];
    } else {
      ElMessage.error('加载角色失败：' + res.msg);
    }
  }).catch(err => {
    ElMessage.error('加载角色失败：' + err.message);
  }).finally(() => {
    loading.value = false;
  });
};

// 加载所有用户
const loadAllUsers = () => {
  loading.value = true;
  request.post('/u_role/userInfo', {
    pageNum: 1,
    pageSize: 100
  }).then(res => {
    if (res.code === '200') {
      data.tableData = res.data?.list || res.data || [];
    } else {
      ElMessage.error('加载用户失败：' + res.msg);
      data.tableData = [];
    }
  }).catch(err => {
    ElMessage.error('加载用户失败：' + err.message);
    data.tableData = [];
  }).finally(() => {
    loading.value = false;
  });
};

// ===== 查询相关函数 =====
const handleSearch = () => {
  data.assignedPageNum = 1;
  data.unassignedPageNum = 1;
  const total = filteredUsers.value.length;
  ElMessage.info(`搜索完成，共找到 ${total} 个用户`);
};

const reset = () => {
  data.searchQuery = '';
  data.selectedUserType = null;
  handleSearch();
};

// ===== 表格选择相关函数 =====
const handleSelectionChange = (rows) => {
  data.ids = rows.map(v => v.roleId || v.id).filter(id => id != null && id !== '');
};

// 标签页切换
const handleTabChange = () => {
  data.ids = [];
};

// ===== 分页相关函数 =====
// 已分配用户分页
const handleAssignedSizeChange = (size) => {
  data.assignedPageSize = size;
  data.assignedPageNum = 1;
};

const handleAssignedCurrentChange = (current) => {
  data.assignedPageNum = current;
};

// 未分配用户分页
const handleUnassignedSizeChange = (size) => {
  data.unassignedPageSize = size;
  data.unassignedPageNum = 1;
};

const handleUnassignedCurrentChange = (current) => {
  data.unassignedPageNum = current;
};

// ===== 角色分配相关函数 =====
const showAssignModal = (user) => {
  data.selectedUser = user;
  data.dialogTitle = user.roleId || user.id > 0 ? `修改 ${user.name} 的角色` : `为 ${user.name} 分配角色`;
  data.assignForm.roleId = user.roleId || null;
  data.formVisible = true;
};

const showBatchAssignModal = () => {
  if (data.ids.length === 0) {
    ElMessage.warning('请先选择要分配角色的用户');
    return;
  }
  data.dialogTitle = `批量分配角色（共 ${data.ids.length} 个用户）`;
  data.assignForm.roleId = null;
  data.selectedUser = null;
  data.formVisible = true;
};

const confirmAssign = () => {
  if (!data.assignForm.roleId) {
    ElMessage.warning('请先选择角色');
    return;
  }

  loading.value = true;

  // 判断是修改已有角色还是分配新角色
  if (data.selectedUser && (data.selectedUser.roleId || data.selectedUser.id > 0)) {
    // 修改已有角色分配
    const userRole = {
      id: data.selectedUser.roleId || data.selectedUser.id,
      roleId: data.assignForm.roleId,
      // userId: Math.abs(data.selectedUser.id),
      // userType: data.selectedUser.userType
    };

    request.put('/u_role/update', userRole).then(res => {
      if (res.code === '200') {
        ElMessage.success('角色修改成功');
        data.formVisible = false;
        loadAllUsers();
      } else {
        ElMessage.error('角色修改失败：' + res.msg);
      }
    }).catch(err => {
      ElMessage.error('角色修改失败：' + err.message);
    }).finally(() => {
      loading.value = false;
    });
  } else {
    // 分配新角色
    const usersToAssign = data.selectedUser ? [data.selectedUser] : data.tableData.filter(user =>
      data.ids.includes(user.roleId || user.id)
    );

    const userRoles = usersToAssign.map(user => ({
      userId: Math.abs(user.id),
      userType: user.userType,
      roleId: data.assignForm.roleId
    }));

    request.post('/u_role/add', userRoles).then(res => {
      if (res.code === '200') {
        ElMessage.success(`角色分配成功 ${userRoles.length} 个`);
        data.formVisible = false;
        loadAllUsers();
        data.ids = [];
      } else {
        ElMessage.error('角色分配失败：' + res.msg);
      }
    }).catch(err => {
      ElMessage.error('角色分配失败：' + err.message);
    }).finally(() => {
      loading.value = false;
    });
  }
};

// ===== 删除相关函数 =====
const deleteItems = (ids) => {
  if (!ids || ids.length === 0) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }

  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' })
    .then(() => {
      request.delete("/u_role/delete", { data: ids }).then(res => {
        if (res.code === '200') {
          ElMessage.success('操作成功');
          loadAllUsers();
          data.ids = [];
        } else {
          ElMessage.error(res.msg);
        }
      }).catch(err => {
        ElMessage.error('删除过程中发生错误：' + err.message);
      });
    })
    .catch(() => { });
};

const del = (id) => {
  deleteItems([id]);
};

const delBatch = () => {
  if (data.ids.length === 0) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }
  deleteItems(data.ids);
};
</script>

<style scoped>
/* 导入公共列表页样式 */
@import "@/assets/css/manager/list.css";

.overview-section {
  margin-bottom: var(--space-md);
  padding-bottom: var(--space-md);
  border-bottom: 1px solid var(--border-color);
}

.overview-cards {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.batch-actions {
  display: flex;
  flex-direction: column;
  gap: var(--space-sm);
  align-items: flex-end;
  justify-content: center;
  height: 100%;
  padding: 0;
  border-bottom: none;
}

.content-tabs {
  padding-top: 0;
}

:deep(.el-tabs__content) {
  padding-top: 0;
}

.stat-card {
  border-radius: var(--radius-md);
  transition: all var(--transition-normal);
  height: auto;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: var(--shadow-md);
}

.stat-content {
  text-align: center;
  padding: var(--space-sm) var(--space-md);
}

.stat-value {
  font-size: 22px;
  font-weight: var(--font-weight-bold);
  line-height: 1.2;
  margin-bottom: var(--space-xs);
  transition: all var(--transition-normal);
}

.stat-card:hover .stat-value {
  transform: scale(1.05);
}

.stat-label {
  font-size: 13px;
  color: var(--text-secondary);
  font-weight: var(--font-weight-medium);
}

.success-card {
  background: linear-gradient(135deg, #f6ffed 0%, #f0f9ff 100%);
}

.warning-card {
  background: linear-gradient(135deg, #fff7e6 0%, #fffbe6 100%);
}

.info-card {
  background: linear-gradient(135deg, #f0f5ff 0%, #ecf5ff 100%);
}

.stat-card:not(.success-card):not(.warning-card):not(.info-card) {
  background: linear-gradient(135deg, #fafafa 0%, #f5f5f5 100%);
}

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
}
</style>
