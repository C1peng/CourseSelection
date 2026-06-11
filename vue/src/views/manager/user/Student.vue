// 修改后的 Student.vue 文件
<template>
  <div class="container">
    <!-- 顶部操作栏 -->
    <div class="header-card">
      <div class="header-title">
        <h2><el-icon>
            <User />
          </el-icon>学生信息</h2>
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
        <el-dropdown>
          <el-button type="success">
            <el-icon>
              <Download />
            </el-icon>导出
            <el-icon class="el-icon--right">
              <ArrowDown />
            </el-icon>
          </el-button>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="exportAll">导出全部</el-dropdown-item>
              <el-dropdown-item @click="exportSelected" :disabled="data.ids.length === 0">导出选中</el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
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
        <el-col :span="5">
          <el-input v-model="data.grade" prefix-icon="Search" placeholder="请输入年级" clearable @clear="reset"
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
          <h3 class="table-title">学生列表</h3>
          <el-tag class="total-record-tag">共 {{ data.total }} 条记录</el-tag>
        </div>
      </div>
      <el-table v-loading="loading" stripe :data="data.tableData" @selection-change="handleSelectionChange"
        :header-cell-style="{ backgroundColor: '#fafafa' }">
        <el-table-column type="selection" width="55" @selection-change="handleSelectionChange" />
        <el-table-column prop="id" label="序号" align="center" width="100" />
        <el-table-column prop="username" label="账号" align="center" min-width="120" />
        <el-table-column prop="avatar" label="头像" align="center" width="100">
          <template v-slot="scope">
            <el-image style="width: 40px; height: 40px; border-radius: 50%; display: block; margin: 0 auto"
              class="avatar-hover" v-if="scope.row.avatar" :src="scope.row.avatar"
              :preview-src-list="[scope.row.avatar]" preview-teleported></el-image>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="姓名" align="center" min-width="100" />
        <el-table-column prop="gender" label="性别" align="center" min-width="80">
          <template v-slot="scope">
            <span>{{ scope.row.gender === 1 ? '男' : '女' }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deptName" label="学院名称" align="center" min-width="120" />
        <el-table-column prop="grade" label="年级" align="center" min-width="100" />
        <el-table-column prop="sort" label="阶段" align="center" min-width="80">
          <template v-slot="scope">
            <span>{{ scope.row.sort === 1 ? '大一' : scope.row.sort === 2 ? '大二' : scope.row.sort === 3 ? '大三' : scope.row.sort === 4 ? '大四' : '' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="学分信息" align="center" min-width="120">
          <template v-slot="scope">
            <el-tooltip placement="top" effect="light" :enterable="false" popper-class="credit-tooltip">
              <template #content>
                <div class="credit-tooltip-content">
                  <div class="credit-tooltip-header">
                    <el-icon><Timer /></el-icon> 学分详情
                  </div>
                  <div class="credit-tooltip-body">
                    <div class="credit-item">
                      <span class="credit-label">公共学分：</span>
                      <span class="credit-value">{{ scope.row.publicCredits || 0 }}</span>
                    </div>
                    <div class="credit-item">
                      <span class="credit-label">外语学分：</span>
                      <span class="credit-value">{{ scope.row.foreignLanguageCredits || 0 }}</span>
                    </div>
                    <div class="credit-item">
                      <span class="credit-label">体育学分：</span>
                      <span class="credit-value">{{ scope.row.sportCredits || 0 }}</span>
                    </div>
                    <div class="credit-item">
                      <span class="credit-label">艺术类学分：</span>
                      <span class="credit-value">{{ scope.row.artCredits || 0 }}</span>
                    </div>
                    <div class="credit-total">
                      <span class="total-label">总学分：</span>
                      <span class="total-value">
                        {{ (scope.row.publicCredits || 0) + (scope.row.foreignLanguageCredits || 0) + (scope.row.sportCredits || 0) + (scope.row.artCredits || 0) }}
                      </span>
                    </div>
                  </div>
                </div>
              </template>
              <el-button link size="small" class="credit-button">查看学分</el-button>
            </el-tooltip>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" align="center" min-width="100">
          <template v-slot="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : scope.row.status === 1 ? 'warning' : 'danger'">
              {{ scope.row.status === 0 ? '在读' : scope.row.status === 1 ? '休学' : '离校' }}
            </el-tag>
          </template>
        </el-table-column>
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
    <el-dialog title="学生信息" v-model="data.formVisible" width="700px" :before-close="handleClose" destroy-on-close>
      <StudentForm ref="studentFormRef" :initial-data="studentFormData" />
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
import { Delete, Edit, Search, Plus, User, Timer, Download, ArrowDown } from "@element-plus/icons-vue";
import StudentForm from './StudentForm.vue';
import ExcelJS from 'exceljs';
import { saveAs } from 'file-saver';

// 状态管理
const data = reactive({
  formVisible: false,
  tableData: [],
  pageNum: 1,
  pageSize: 10,
  total: 0,
  userInfo: null,
  grade: null,
  ids: []
})

const loading = ref(false);
const studentFormRef = ref(null);
const studentFormData = ref({});

// 加载学生列表数据
const load = () => {
  loading.value = true;
  const studentDTO = {
    pageNum: data.pageNum || 1,
    pageSize: data.pageSize || 10,
    id: data.id,
    userInfo: data.userInfo,
    grade: data.grade
  }
  request.post('/student/selectPage', studentDTO)
    .then(res => {
      if (res.code === '200') {
        data.tableData = res.data?.list || []
        data.total = res.data?.total || 0
      } else {
        ElMessage.error(res.msg || '获取数据失败')
      }
    }).catch(error => {
      console.error('加载学生列表失败:', error);
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

// 处理新增学生
const handleAdd = () => {
  studentFormData.value = {};
  data.formVisible = true;
  // 确保组件已经挂载后重置表单
  setTimeout(() => {
    if (studentFormRef.value) {
      studentFormRef.value.resetForm();
    }
  }, 100);
}

// 处理编辑学生
const handleEdit = (row) => {
  studentFormData.value = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
  // 确保组件已经挂载后更新表单数据
  setTimeout(() => {
    if (studentFormRef.value) {
      studentFormRef.value.watchProps?.();
    }
  }, 100);
}

// 添加新学生
const add = () => {
  const formData = studentFormRef.value?.getFormData();
  if (!formData) return;

  request.post('/student/add', formData).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg || '添加失败')
    }
  }).catch(error => {
    console.error('添加学生失败:', error);
    ElMessage.error('添加失败，请重试');
  })
}

// 更新学生信息
const update = () => {
  const formData = studentFormRef.value?.getFormData();
  if (!formData) return;

  request.put('/student/update', formData).then(res => {
    if (res.code === '200') {
      ElMessage.success('操作成功')
      data.formVisible = false
      load()
    } else {
      ElMessage.error(res.msg || '更新失败')
    }
  }).catch(error => {
    console.error('更新学生失败:', error);
    ElMessage.error('更新失败，请重试');
  })
}

// 保存学生信息（新增或更新）
const save = async () => {
  try {
    // 调用表单验证
    await studentFormRef.value?.validate();

    const formData = studentFormRef.value?.getFormData();
    if (!formData) {
      ElMessage.warning('获取表单数据失败');
      return;
    }

    formData.id ? update() : add();
  } catch (error) {
    // 表单验证失败，不执行提交
    ElMessage.warning('请完成必填项');
  }
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
      request.delete('/student/delete', { data: ids }).then(res => {
        if (res.code === '200') {
          ElMessage.success('操作成功')
          data.ids = [] // 清空选中状态
          load()
        } else {
          ElMessage.error(res.msg || '批量删除失败')
        }
      }).catch(error => {
        console.error('批量删除学生失败:', error);
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
  data.grade = null;
  load()
}

// 导出全部数据
const exportAll = async () => {
  try {
    loading.value = true;
    ElMessage.info('正在准备导出数据...');
    
    // 获取所有学生数据（不分页）
    const studentDTO = {
      pageNum: 1,
      pageSize: 999999, // 一个足够大的数字
      id: data.id,
      userInfo: data.userInfo,
      grade: data.grade
    };
    
    const res = await request.post('/student/selectPage', studentDTO);
    if (res.code === '200') {
      const allData = res.data?.list || [];
      await exportToExcel(allData, '学生信息全部数据');
      ElMessage.success('导出成功！');
    } else {
      ElMessage.error(res.msg || '获取数据失败');
    }
  } catch (error) {
    console.error('导出失败:', error);
    ElMessage.error('导出失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 导出选中数据
const exportSelected = async () => {
  if (data.ids.length === 0) {
    ElMessage.warning('请选择要导出的数据');
    return;
  }
  
  try {
    loading.value = true;
    ElMessage.info('正在准备导出数据...');
    
    // 获取选中的学生数据
    const selectedData = data.tableData.filter(row => data.ids.includes(row.id));
    await exportToExcel(selectedData, '学生信息选中数据');
    ElMessage.success('导出成功！');
  } catch (error) {
    console.error('导出失败:', error);
    ElMessage.error('导出失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 导出到Excel
const exportToExcel = async (dataList, fileName) => {
  // 创建工作簿
  const workbook = new ExcelJS.Workbook();
  workbook.creator = '学生管理系统';
  workbook.lastModifiedBy = 'System';
  workbook.created = new Date();
  workbook.modified = new Date();
  
  // 创建工作表
  const worksheet = workbook.addWorksheet('学生信息');
  
  // 设置列宽
  worksheet.columns = [
    { header: '序号', key: 'id', width: 10 },
    { header: '账号', key: 'username', width: 15 },
    { header: '姓名', key: 'name', width: 12 },
    { header: '性别', key: 'gender', width: 8 },
    { header: '学院名称', key: 'deptName', width: 18 },
    { header: '年级', key: 'grade', width: 10 },
    { header: '阶段', key: 'sort', width: 10 },
    { header: '公共学分', key: 'publicCredits', width: 12 },
    { header: '外语学分', key: 'foreignLanguageCredits', width: 12 },
    { header: '体育学分', key: 'sportCredits', width: 12 },
    { header: '艺术类学分', key: 'artCredits', width: 14 },
    { header: '总学分', key: 'totalCredits', width: 10 },
    { header: '状态', key: 'status', width: 10 },
    { header: '电话', key: 'phone', width: 15 },
    { header: '邮箱', key: 'email', width: 25 }
  ];
  
  // 添加数据行
  dataList.forEach(row => {
    worksheet.addRow({
      id: row.id,
      username: row.username,
      name: row.name,
      gender: row.gender === 1 ? '男' : '女',
      deptName: row.deptName,
      grade: row.grade,
      sort: row.sort === 1 ? '大一' : row.sort === 2 ? '大二' : row.sort === 3 ? '大三' : row.sort === 4 ? '大四' : '',
      publicCredits: row.publicCredits || 0,
      foreignLanguageCredits: row.foreignLanguageCredits || 0,
      sportCredits: row.sportCredits || 0,
      artCredits: row.artCredits || 0,
      totalCredits: (row.publicCredits || 0) + (row.foreignLanguageCredits || 0) + (row.sportCredits || 0) + (row.artCredits || 0),
      status: row.status === 0 ? '在读' : row.status === 1 ? '休学' : '离校',
      phone: row.phone,
      email: row.email
    });
  });
  
  // 格式美化
  // 设置表头样式
  worksheet.getRow(1).font = {
    bold: true,
    color: { argb: 'FFFFFF' }
  };
  worksheet.getRow(1).fill = {
    type: 'pattern',
    pattern: 'solid',
    fgColor: { argb: '409EFF' }
  };
  worksheet.getRow(1).alignment = {
    horizontal: 'center',
    vertical: 'middle'
  };
  
  // 设置数据行样式
  for (let i = 2; i <= dataList.length + 1; i++) {
    const row = worksheet.getRow(i);
    row.alignment = {
      horizontal: 'center',
      vertical: 'middle'
    };
    // 隔行变色
    if (i % 2 === 0) {
      row.fill = {
        type: 'pattern',
        pattern: 'solid',
        fgColor: { argb: 'F5F7FA' }
      };
    }
  }
  
  // 自动调整列宽
  worksheet.columns.forEach(column => {
    column.width = Math.max(column.width, 10);
  });
  
  // 生成Excel文件并下载
  const buffer = await workbook.xlsx.writeBuffer();
  const blob = new Blob([buffer], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
  saveAs(blob, `${fileName}_${new Date().toISOString().slice(0, 10)}.xlsx`);
};

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

/* 学分按钮样式 */
.credit-button {
  color: var(--manager-primary);
  font-weight: var(--font-weight-medium);
}

.credit-button:hover {
  color: var(--manager-primary-light);
  text-decoration: underline;
}
</style>

<!-- 非scoped样式，用于美化tooltip -->
<style>
/* 学分tooltip样式 */
.credit-tooltip {
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  padding: 8px;
}

.credit-tooltip-content {
  min-width: 200px;
}

.credit-tooltip-header {
  font-weight: 600;
  font-size: 14px;
  color: #303133;
  padding: 8px 12px;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.credit-tooltip-body {
  padding: 0 12px 8px;
}

.credit-item {
  display: flex;
  justify-content: space-between;
  padding: 4px 0;
  font-size: 13px;
}

.credit-label {
  color: #606266;
}

.credit-value {
  color: #303133;
  font-weight: 500;
}

.credit-total {
  display: flex;
  justify-content: space-between;
  padding: 8px 0 4px;
  margin-top: 8px;
  border-top: 1px dashed #e4e7ed;
  font-size: 14px;
  font-weight: 600;
}

.total-label {
  color: #303133;
}

.total-value {
  color: #409eff;
  font-weight: 700;
}
</style>