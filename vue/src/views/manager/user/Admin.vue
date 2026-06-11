<template>
  <div class="container">
    <!-- 顶部操作栏 -->
    <div class="header-card">
      <div class="header-title">
        <h2><el-icon>
            <User />
          </el-icon>管理员信息</h2>
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
          <el-input v-model="data.id" prefix-icon="Search" placeholder="请输入序号" clearable @clear="reset"
            @keyup.enter="load" />
        </el-col>
        <el-col :span="5">
          <el-input v-model="data.userInfo" prefix-icon="Search" placeholder="请输入账号/姓名" clearable @clear="reset"
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
          <h3 class="table-title">管理员列表</h3>
          <el-tag class="total-record-tag">共 {{ data.total }} 条记录</el-tag>
        </div>
      </div>
      <el-table v-loading="loading" stripe :data="data.tableData" @selection-change="handleSelectionChange"
        :header-cell-style="{ backgroundColor: '#fafafa' }">
        <el-table-column type="selection" width="55" @selection-change="handleSelectionChange" />
        <el-table-column prop="id" label="序号" align="center" width="80" />
        <el-table-column prop="username" label="账号" align="center" min-width="120" />
        <el-table-column prop="avatar" label="头像" align="center" width="100">
          <template v-slot="scope">
            <el-image style="width: 40px; height: 40px; border-radius: 50%; display: block; margin: 0 auto"
              class="avatar-hover" v-if="scope.row.avatar" :src="scope.row.avatar"
              :preview-src-list="[scope.row.avatar]" preview-teleported></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" align="center" min-width="100" />
        <el-table-column prop="phone" label="电话" align="center" min-width="120" />
        <el-table-column prop="email" label="邮箱" align="center" min-width="150" />
        <el-table-column label="操作" width="160" fixed="right" align="center">
          <template v-slot="scope">
            <div style="display: flex; justify-content: center; gap: 8px;">
              <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(scope.row)" title="编辑"></el-button>
              <el-button type="danger" size="small" :icon="Delete" @click="del(scope.row.id)" title="删除"></el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页区域 -->
      <div class="pagination-container" v-if="data.total > 0">
        <el-pagination v-model:current-page="data.pageNum" v-model:page-size="data.pageSize"
          :page-sizes="[10, 20, 50, 100]" layout="total, sizes, prev, pager, next, jumper" :total="data.total"
          @size-change="handleSizeChange" @current-change="handleCurrentChange" />
      </div>
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog title="管理员信息" v-model="data.formVisible" width="700px" :before-close="handleClose" destroy-on-close>
      <AdminForm ref="adminFormRef" :initial-data="adminFormData" />
      <template #footer>
        <span style="display: flex; justify-content: center; width: 100%;">
          <el-button @click="handleClose">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </span>
      </template>
    </el-dialog>

  </div>
</template>

<script setup>
import { reactive, ref } from "vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete, Edit, Search, Plus, User } from "@element-plus/icons-vue";
import AdminForm from './AdminForm.vue';

// 添加emits选项，声明组件接收updateUser事件
const emit = defineEmits(['updateUser']);

// 状态管理
const data = reactive({
  formVisible: false,
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  userInfo: null,
  ids: []
})

const loading = ref(false);
const adminFormRef = ref(null);
const adminFormData = ref({});

// 加载管理员列表数据
const load = () => {
  loading.value = true;
  const adminDTO = {
    pageNum: data.pageNum || 1,
    pageSize: data.pageSize || 10,
    id: data.id || undefined,
    userInfo: data.userInfo || undefined
  }
  request.post('/admin/selectPage', adminDTO)
    .then(res => {
      if (res.code === '200') {
        data.tableData = res.data?.list || []
        data.total = res.data?.total || 0
      } else {
        ElMessage.error(res.msg || '获取数据失败')
      }
    }).catch(error => {
      console.error('加载管理员列表失败:', error);
      ElMessage.error('加载数据失败，请重试');
    }).finally(() => {
      loading.value = false;
    })
}

// 分页大小变化处理
const handleSizeChange = (size) => {
  data.pageSize = size;
  data.pageNum = 1;
  load();
}

// 当前页变化处理
const handleCurrentChange = (current) => {
  data.pageNum = current;
  load();
}

// 处理新增管理员
const handleAdd = () => {
  adminFormData.value = {};
  data.formVisible = true;
  // 确保组件已经挂载后重置表单
  setTimeout(() => {
    if (adminFormRef.value) {
      adminFormRef.value.resetForm();
    }
  }, 100);
}

// 处理编辑管理员
const handleEdit = (row) => {
  adminFormData.value = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
  // 确保组件已经挂载后更新表单数据
  setTimeout(() => {
    if (adminFormRef.value) {
      adminFormRef.value.watchProps?.();
    }
  }, 100);
}

// 添加新管理员
const add = () => {
  const formData = adminFormRef.value?.getFormData();
  if (!formData) return;

  request.post('/admin/add', formData).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg || '添加失败')
    }
  }).catch(error => {
    console.error('添加管理员失败:', error);
    ElMessage.error('添加失败，请重试');
  })
}

// 更新管理员信息
const update = () => {
  const formData = adminFormRef.value?.getFormData();
  if (!formData) return;

  request.put('/admin/update', formData).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg || '更新失败')
    }
  }).catch(error => {
    console.error('更新管理员失败:', error);
    ElMessage.error('更新失败，请重试');
  })
}

// 保存管理员信息（新增或更新）
const save = async () => {
  // 验证表单
  const isValid = await adminFormRef.value?.validate();
  if (!isValid) {
    ElMessage.warning('表单验证失败，请检查必填项');
    return;
  }

  const formData = adminFormRef.value?.getFormData();
  if (!formData) {
    ElMessage.warning('获取表单数据失败');
    return;
  }

  formData.id ? update() : add()
}

// ===== 删除相关函数 =====
// 统一的删除函数，接收ID数组参数
const deleteItems = (ids) => {
  if (!ids || ids.length === 0) {
    ElMessage.warning("请选择要删除的数据");
    return;
  }

  ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' })
    .then(() => {
      request.delete('/admin/delete', { data: ids }).then(res => {
        if (res.code === '200') {
          ElMessage.success('操作成功')
          data.ids = [] // 清空选中状态
          load()
        } else {
          ElMessage.error(res.msg || '批量删除失败')
        }
      }).catch(error => {
        console.error('批量删除管理员失败:', error);
        ElMessage.error('批量删除失败，请重试');
      })
    })
    .catch(() => {
      // 用户取消删除
    })
}
// 单条删除函数
const del = (id) => {
  deleteItems([id]); // 将单个ID转为数组传入
};

// 批量删除函数
const delBatch = () => {
  deleteItems(data.ids);
};

// 处理表格选择变化
const handleSelectionChange = (rows) => {
  data.ids = rows.map(v => v.id)
}

// 对话框关闭处理
const handleClose = () => {
  data.formVisible = false;
}

// 重置搜索条件
const reset = () => {
  data.userInfo = null;
  data.id = null;
  load()
}

// 页面初始化加载数据
load();
</script>

<style scoped>
/* 导入公共列表页样式 */
@import "@/assets/css/manager/list.css";

/* 头像悬停效果 */
.avatar-hover {
  transition: all var(--transition-normal);
}

.avatar-hover:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}
</style>