<template>
  <div class="container">
    <!-- 顶部操作栏 -->
    <div class="header-card">
      <div class="header-title">
        <h2>
          <el-icon><Bell /></el-icon>
          通知管理
        </h2>
      </div>
      <div class="header-actions">
        <el-button type="danger" :disabled="!data.ids.length" @click="delBatch">
          <el-icon><Delete /></el-icon>
          批量删除
        </el-button>
      </div>
    </div>

    <!-- 查询条件卡片 -->
    <el-card class="query-card" shadow="hover">
      <div class="query-title">
        <h3>搜索查询</h3>
      </div>
      <el-row :gutter="20">
        <el-col :span="6">
          <el-input
            v-model="data.userId"
            prefix-icon="Search"
            placeholder="请输入用户ID查询"
            clearable
            @clear="reset"
            @keyup.enter="load"
          />
        </el-col>
        <el-col :span="6">
          <el-select
            v-model="data.type"
            placeholder="通知类型"
            clearable
            @clear="reset"
          >
            <el-option label="学分提醒" value="CREDIT_REMINDER" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <div style="display: flex; gap: 8px;">
            <el-button type="primary" @click="load">
              <el-icon><Search /></el-icon>
              查询
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
          <h3 class="table-title">通知列表</h3>
          <el-tag class="total-record-tag" v-if="data.total">共 {{ data.total }} 条记录</el-tag>
        </div>
      </div>
      <el-table stripe :data="data.tableData" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="userId" label="用户ID" />
        <el-table-column prop="type" label="通知类型">
          <template #default="scope">
            <el-tag v-if="scope.row.type === 'CREDIT_REMINDER'" type="primary">学分提醒</el-tag>
            <span v-else>{{ scope.row.type }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="通知内容" />
        <el-table-column prop="isRead" label="阅读状态">
          <template #default="scope">
            <el-tag v-if="scope.row.isRead === 0" type="warning">未读</el-tag>
            <el-tag v-else type="success">已读</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="120" fixed="right" align="center">
          <template v-slot="scope">
            <div style="display: flex; justify-content: center; gap: 8px;">
              <el-button type="danger" size="small" :icon="Delete" @click="del(scope.row.id)"></el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container" v-if="data.total">
        <el-pagination
          @current-change="load"
          background
          layout="prev, pager, next"
          :page-size="data.pageSize"
          :current-page="data.pageNum"
          :total="data.total"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { reactive, onMounted } from 'vue';
import { ElMessage, ElMessageBox } from 'element-plus';
import { Search, Plus, Delete, Edit, Bell } from '@element-plus/icons-vue';
import request from '@/utils/request.js';

const data = reactive({
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  ids: [],
  userId: '',
  type: ''
});

// 加载通知列表
const load = () => {
  request.get('/notification/manager/list', {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      userId: data.userId,
      type: data.type
    }
  }).then(res => {
    if (res.code === '200' && res.data) {
      data.tableData = res.data.list;
      data.total = res.data.total;
    }
  }).catch(error => {
    console.error('获取通知列表失败:', error);
    ElMessage.error('获取通知列表失败');
  });
};

// 重置查询条件
const reset = () => {
  data.userId = '';
  data.type = '';
  data.pageNum = 1;
  load();
};

// 处理选择
const handleSelectionChange = (selection) => {
  data.ids = selection.map(item => item.id);
};

// 单个删除
const del = (id) => {
  ElMessageBox.confirm(
    '确定要删除这条通知吗？',
    '删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    request.delete(`/notification/${id}`).then(res => {
      if (res.code === '200') {
        ElMessage.success('删除成功');
        load();
      }
    }).catch(error => {
      console.error('删除失败:', error);
      ElMessage.error('删除失败');
    });
  }).catch(() => {
    // 取消删除
  });
};

// 批量删除
const delBatch = () => {
  if (data.ids.length === 0) {
    ElMessage.warning('请选择要删除的通知');
    return;
  }
  
  ElMessageBox.confirm(
    `确定要删除选中的 ${data.ids.length} 条通知吗？`,
    '批量删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(() => {
    request.delete('/notification/batch', {
      data: { ids: data.ids }
    }).then(res => {
      if (res.code === '200') {
        ElMessage.success('批量删除成功');
        load();
        data.ids = [];
      }
    }).catch(error => {
      console.error('批量删除失败:', error);
      ElMessage.error('批量删除失败');
    });
  }).catch(() => {
    // 取消删除
  });
};

// 初始化
onMounted(() => {
  load();
});
</script>

<style scoped>
/* 导入公共列表页样式 */
@import "@/assets/css/manager/list.css";
</style>