<template>
  <div class="permission-assign-form">
    <!-- 已分配权限列表 -->
    <div class="assigned-permissions-section" v-if="Object.keys(permissionNamesObj).length > 0">
      <div class="section-header">
        <h4>已分配权限</h4>
      </div>
      <el-scrollbar height="100px">
        <div class="assigned-permissions-list">
          <el-tag
            v-for="(permissionId, permissionName) in permissionNamesObj"
            :key="permissionId"
            size="small"
            type="primary"
            effect="dark"
            closable
            @close="removePermission(permissionId)"
          >
            {{ permissionName }}
          </el-tag>
        </div>
      </el-scrollbar>
    </div>
    
    <!-- 权限树 -->
    <div class="permission-tree-container">
      <el-tree
        v-loading="permissionLoading"
        :data="permissionTreeData"
        show-checkbox
        node-key="id"
        ref="permissionTreeRef"
        :default-expanded-keys="permissionExpandedKeys"
        :default-checked-keys="checkedPermissions"
        :props="permissionTreeProps"
        @check="handlePermissionCheck"
        class="permission-tree"
        :expand-on-click-node="false"
        :check-strictly="false"
        :accordion="false"
        :indent="16"
      >
        <template #default="{ node, data }">
          <div class="tree-node-content">
            <span class="tree-node-label">{{ node.label }}</span>
            <span v-if="data.children && data.children.length > 0" class="tree-node-count">
              ({{ data.children.length }})
            </span>
          </div>
        </template>
      </el-tree>
    </div>
    
    <!-- 权限操作按钮 -->
    <div class="permission-actions">
      <el-button type="primary" @click="savePermissions">
        <el-icon><Check /></el-icon>保存权限分配
      </el-button>
      <el-button @click="resetPermissions">
        <el-icon><Refresh /></el-icon>重置
      </el-button>
    </div>
  </div>
</template>

<style scoped>
/* 导入后台表单样式 */
@import "@/assets/css/manager/list.css";

.permission-assign-form {
  width: 100%;
}

/* 已分配权限部分 */
.assigned-permissions-section {
  margin-bottom: var(--space-md);
  padding: var(--space-sm);
  background-color: var(--bg-gray);
  border-radius: var(--radius-sm);
  border: 1px solid var(--border-color);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--space-xs);
}

.section-header h4 {
  margin: 0;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-bold);
  color: var(--text-primary);
}

.assigned-permissions-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--space-sm);
  padding: var(--space-sm) 0;
}

/* 权限树容器 */
.permission-tree-container {
  height: 300px;
  overflow-y: auto;
  margin-bottom: var(--space-md);
  border: 1px solid var(--border-color);
  border-radius: var(--radius-sm);
  padding: var(--space-sm);
  background-color: var(--bg-white);
  box-shadow: var(--shadow-sm);
}

.permission-tree {
  height: 100%;
}

/* 树节点样式 */
.tree-node-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  padding: var(--space-xs) 0;
}

.tree-node-label {
  flex: 1;
  font-size: var(--font-size-sm);
  color: var(--text-primary);
}

.tree-node-count {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  background-color: var(--bg-gray);
  padding: 1px var(--space-sm);
  border-radius: 10px;
  margin-left: var(--space-sm);
}

/* 自定义展开/折叠图标 */
.el-tree-node__expand-icon {
  font-size: var(--font-size-xs);
  color: var(--text-secondary);
  transition: all var(--transition-normal);
}

.el-tree-node__expand-icon.is-expanded {
  color: var(--manager-primary);
  transform: rotate(90deg);
}

/* 选中节点样式 */
.el-tree-node.is-current > .el-tree-node__content {
  background-color: rgba(5, 150, 105, 0.1);
}

.el-tree-node.is-current > .el-tree-node__content:hover {
  background-color: rgba(5, 150, 105, 0.1);
}

/* 悬停节点样式 */
.el-tree-node__content:hover {
  background-color: var(--bg-gray);
}

.permission-actions {
  display: flex;
  gap: var(--space-sm);
  justify-content: flex-end;
}
</style>

<script setup>
import { ref, reactive, watch, onMounted } from "vue";
import { ElMessage } from "element-plus";
import { Check, Refresh } from "@element-plus/icons-vue";
import request from "@/utils/request.js";

const props = defineProps({
  roleId: {
    type: Number,
    required: true
  },
  initialPermissions: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['permissionUpdated']);

// 组件引用
const permissionTreeRef = ref();

// 权限相关
const permissionTreeData = ref([]);
const checkedPermissions = ref([]); // 存储权限ID
const permissionNamesObj = ref({}); // 存储permissionNames对象
const permissionExpandedKeys = ref([]);
const permissionLoading = ref(false);
const permissionIdMap = ref({}); // 用于存储权限ID到名称的映射

// 树配置
const permissionTreeProps = {
  label: (data) => {
    // 根据后端返回的action和module字段构建树节点标签
    return data.action || data.module || data.name;
  },
  children: 'children'
};

// 生命周期钩子
onMounted(() => {
  fetchPermissionTree();
  updatePermissionsFromProps();
});

// 监听props变化，更新权限数据
watch(() => props.initialPermissions, (newPermissions) => {
  updatePermissionsFromProps();
}, { deep: true });

// 从props更新权限数据
const updatePermissionsFromProps = () => {
  // 保存permissionNames对象
  permissionNamesObj.value = props.initialPermissions || {};
  // 根据后端返回的权限对象，直接获取权限ID
  if (props.initialPermissions && typeof props.initialPermissions === 'object') {
    // permissionNames是对象，值就是权限ID
    checkedPermissions.value = Object.values(props.initialPermissions)
      .filter(id => id !== undefined && id !== null);
  } else {
    checkedPermissions.value = [];
  }
  
  // 刷新权限树的选中状态
  if (permissionTreeRef.value) {
    permissionTreeRef.value.setCheckedKeys(checkedPermissions.value);
  }
};

// 获取权限树
const fetchPermissionTree = () => {
  permissionLoading.value = true;
  request.get('/role/tree')
    .then(res => {
      if (res.code === '200') {
        permissionTreeData.value = res.data || [];
        // 构建权限ID到名称的映射
        buildPermissionIdMap(permissionTreeData.value);
        // 不展开任何权限节点，保持默认折叠状态
        permissionExpandedKeys.value = [];
      }
    }).finally(() => {
      permissionLoading.value = false;
    });
};

// 构建权限ID到名称的映射
const buildPermissionIdMap = (nodes) => {
  const map = {};
  
  const processNodes = (nodeList) => {
    nodeList.forEach(node => {
      // 根据后端返回的action或module字段构建权限名称
      const permissionName = node.action || node.module || node.name;
      map[node.id] = permissionName;
      if (node.children && node.children.length > 0) {
        processNodes(node.children);
      }
    });
  };
  
  processNodes(nodes);
  permissionIdMap.value = map;
};

// 权限选择
const handlePermissionCheck = () => {
  // 可以在这里添加权限选择的处理逻辑
};

// 保存权限分配
const savePermissions = () => {
  if (!props.roleId) {
    ElMessage.warning('请先选择一个角色');
    return;
  }
  
  // 获取选中的权限ID，并过滤掉null值
  const selectedPermissionIds = permissionTreeRef.value.getCheckedKeys()
    .filter(id => id !== undefined && id !== null);
  
  // 提交权限分配
  request.post('/role/assignPermissions', {
    roleId: props.roleId,
    permissionIds: selectedPermissionIds
  }).then(res => {
    if (res.code === '200') {
      ElMessage.success('权限分配成功');
      // 触发权限更新事件
      emit('permissionUpdated', {
        roleId: props.roleId,
        permissions: selectedPermissionIds
      });
    } else {
      ElMessage.error(res.msg);
    }
  });
};

// 重置权限
const resetPermissions = () => {
  // 重新从props更新权限数据
  updatePermissionsFromProps();
};

// 根据权限ID获取权限名称
const getPermissionName = (permissionId) => {
  // 使用权限ID映射来获取权限名称，这样更高效
  return permissionIdMap.value[permissionId] || '未知权限';
};

// 移除权限
const removePermission = (permissionId) => {
  // 从已选中权限中移除
  checkedPermissions.value = checkedPermissions.value.filter(id => id !== permissionId);
  // 更新权限树的选中状态
  if (permissionTreeRef.value) {
    permissionTreeRef.value.setCheckedKeys(checkedPermissions.value);
  }
};
</script>