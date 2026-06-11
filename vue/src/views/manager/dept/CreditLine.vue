<template>
    <div class="container">
        <!-- 顶部操作栏 -->
        <div class="header-card">
            <div class="header-title">
                <h2>
                    <el-icon><Document /></el-icon>
                    选课分数标准管理
                </h2>
            </div>
            <div class="header-actions">
                <el-button type="primary" @click="handleAdd">
                    <el-icon><Plus /></el-icon>
                    新增
                </el-button>
                <el-button 
                    type="danger" 
                    :disabled="selectedIndices.length === 0" 
                    @click="delBatch"
                >
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
                <el-col :span="5">
                    <el-input v-model="queryParams.deptInfo" prefix-icon="Search" placeholder="请输入学院编号/名称" 
                        clearable @clear="handleReset" @keyup.enter="handleSearch"
                    />
                </el-col>
                <el-col :span="5">
                    <el-input 
                        v-model="queryParams.grade" 
                        prefix-icon="Search" 
                        placeholder="请输入年级" 
                        clearable
                        @clear="handleReset" 
                        @keyup.enter="handleSearch"
                    />
                </el-col>
                <el-col :span="4">
                    <div style="display: flex; gap: 8px;">
                        <el-button type="primary" @click="handleSearch">
                            <el-icon><Search /></el-icon>
                            查询
                        </el-button>
                        <el-button @click="handleReset" class="btn-effect">重置</el-button>
                    </div>
                </el-col>
            </el-row>
        </el-card>

        <!-- 列表展示区域 -->
        <el-card class="table-card" shadow="hover">
            <div class="table-header">
                <div class="table-info">
                    <h3 class="table-title">学分要求列表</h3>
                    <el-tag class="total-record-tag">共 {{ total }} 条记录</el-tag>
                </div>
            </div>

            <!-- 学分列表分为左右两个独立表格 -->
            <div class="credit-list-split">
                <!-- 左侧表格：基本信息和操作 -->
                <div class="credit-list-left-section">
                    <div class="section-title">基本信息</div>
                    <el-table 
                        v-loading="loading" 
                        stripe 
                        :data="allCreditLines" 
                        @selection-change="handleSelectionChange"
                        :header-cell-style="{ backgroundColor: '#f5f7fa' }"
                        style="width: 100%;"
                    >
                        <el-table-column type="selection" width="60" align="center" />
                        <!-- <el-table-column prop="id" label="编号" align="center" width="80" /> -->
                        <el-table-column prop="deptName" label="学院名称" align="center" width="140" />
                        <el-table-column prop="grade" label="年级" align="center" width="140" />
                        <el-table-column label="操作" width="220" fixed="right" align="center">
                            <template v-slot="scope">
                                <div style="display: flex; justify-content: center; gap: 8px;">
                                    <el-button 
                                        type="info" 
                                        size="small" 
                                        :icon="View" 
                                        @click="toggleExpand(scope.$index)" 
                                        title="展示"
                                    >
                                        {{ expandedRows.includes(scope.$index) ? '收起' : '展示' }}
                                    </el-button>
                                    <el-button type="primary" size="small" :icon="Edit" @click="handleEdit(scope.row)" title="编辑" />
                                    <el-button type="danger" size="small" :icon="Delete" @click="del(scope.$index)" title="删除" />
                                </div>
                            </template>
                        </el-table-column>
                    </el-table>
                </div>

                <!-- 右侧表格：学分信息 -->
                <div class="credit-list-right-section">
                    <div class="section-title">学分要求</div>
                    <div v-if="rightTableData.length > 0">
                        <el-table 
                            v-loading="loading" 
                            stripe 
                            :data="rightTableData"
                            :header-cell-style="{ backgroundColor: '#f5f7fa' }"
                            style="width: 100%;"
                        >
                            <el-table-column prop="stage" label="阶段" align="center" width="100">
                                <template v-slot="scope">
                                    <el-tag 
                                        v-if="scope.row.sort === 0" 
                                        type="info" 
                                        size="small"
                                        effect="light"
                                    >
                                        总学分
                                    </el-tag>
                                    <el-tag 
                                        v-else
                                        :type="getStageType(scope.row.sort)" 
                                        size="small"
                                        effect="light"
                                    >
                                        {{ getStageName(scope.row.sort) }}
                                    </el-tag>
                                </template>
                            </el-table-column>
                            <el-table-column prop="publicCredit" label="公共选修课" align="center" width="120" />
                            <el-table-column prop="foreignLanguageCredit" label="外语选修课" align="center" width="120" />
                            <el-table-column prop="sportCredit" label="体育选修课" align="center" width="120" />
                            <el-table-column prop="artCredit" label="艺术选修课" align="center" width="120" />
                        </el-table>
                    </div>
                    <div v-else class="empty-state">
                        <el-empty 
                            description="点击左侧'展示'按钮查看学分数据" 
                            :image-size="120"
                        />
                    </div>
                </div>
            </div>

            <!-- 分页 -->
            <div class="pagination-container" v-if="total">
                <el-pagination 
                    @size-change="handleSizeChange" 
                    @current-change="handleCurrentChange"
                    :current-page="pageNum" 
                    :page-sizes="[10, 20, 50, 100]" 
                    :page-size="pageSize"
                    layout="total, sizes, prev, pager, next, jumper" 
                    :total="total" 
                />
            </div>
        </el-card>

        <!-- 编辑对话框 -->
        <el-dialog 
            :title="dialogTitle" 
            v-model="dialogVisible" 
            width="50%" 
            destroy-on-close 
            center
        >
            <!-- 使用独立的表单组件 -->
            <CreditLineForm ref="creditLineFormRef" :initial-data="creditLineInitialData" :is-edit="isEdit" />

            <template #footer>
                <span class="dialog-footer">
                    <el-button @click="dialogVisible = false">取 消</el-button>
                    <el-button type="primary" @click="handleSave">确 定</el-button>
                </span>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
// Vue核心API导入
import { reactive, ref, computed, onMounted } from "vue";

// 外部UI组件和图标导入
import { ElMessage, ElMessageBox } from "element-plus";
import { Delete, Edit, Document, Plus, Search, View } from "@element-plus/icons-vue";

// 工具函数导入
import request from "@/utils/request.js";

// 导入表单组件
import CreditLineForm from './CreditLineForm.vue';

// ===== 组件引用 =====
const creditLineFormRef = ref(null);

// ===== 响应式数据 =====
// 查询参数
const queryParams = reactive({
    deptInfo: null,
    grade: null
});

// 分页数据
const pageNum = ref(1);
const pageSize = ref(10);
const total = ref(0);
const loading = ref(false);

// 列表数据
const allCreditLines = ref([]);
const expandedRows = ref([]); // 现在存储的是索引而不是ID
const selectedIndices = ref([]); // 现在存储的是索引而不是ID
const rightTableData = ref([]); // 右侧表格数据

// 对话框数据
const dialogVisible = ref(false);
const isEdit = ref(false);
const creditLineInitialData = ref({});

// ===== 计算属性 =====
// 对话框标题
const dialogTitle = computed(() => {
    return isEdit.value ? '编辑学分要求' : '新增学分要求';
});

// ===== 生命周期钩子 =====
onMounted(() => {
    fetchData();
});

// ===== 核心数据加载函数 =====
const fetchData = () => {
    loading.value = true;

    // 准备查询参数
    const params = {
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        deptInfo: queryParams.deptInfo || undefined,
        grade: queryParams.grade || undefined
    };

    // 发送请求
    request.post('/creditLine/selectPage', params)
        .then(res => {
            if (res.code === '200') {
                allCreditLines.value = res.data?.list || [];
                total.value = res.data?.total || 0;
                expandedRows.value = []; // 重置展开状态
                rightTableData.value = []; // 清空右侧表格数据
            }
        })
        .catch(err => {
            ElMessage.error('获取学分要求列表失败');
            console.error('获取学分要求列表错误:', err);
        })
        .finally(() => {
            loading.value = false;
        });
};

// ===== 分页相关函数 =====
const handleSizeChange = (size) => {
    pageSize.value = size;
    pageNum.value = 1;
    fetchData();
};

const handleCurrentChange = (current) => {
    pageNum.value = current;
    fetchData();
};

// ===== 查询相关函数 =====
const handleSearch = () => {
    pageNum.value = 1;
    fetchData();
};

const handleReset = () => {
    queryParams.deptInfo = null;
    queryParams.grade = null;
    pageNum.value = 1;
    fetchData();
};

// ===== 展开/折叠相关函数 =====
const toggleExpand = (index) => {
    const rowIndex = expandedRows.value.indexOf(index);
    if (rowIndex > -1) {
        // 收起
        expandedRows.value.splice(rowIndex, 1);
        rightTableData.value = [];
    } else {
        // 展开
        expandedRows.value = [index]; // 确保只展开一行
        const creditLine = allCreditLines.value[index];
        if (creditLine && creditLine.creditTypes) {
            // 构建右侧表格数据（按sort从小到大排序）
            rightTableData.value = creditLine.creditTypes
                .sort((a, b) => a.sort - b.sort) // 按sort字段从小到大排序
                .map(type => ({
                    sort: type.sort,
                    stage: type.sort === 0 ? '总学分' : getStageName(type.sort),
                    publicCredit: type.publicCredit,
                    foreignLanguageCredit: type.foreignLanguageCredit,
                    sportCredit: type.sportCredit,
                    artCredit: type.artCredit
                }));
        }
    }
};

// ===== 辅助函数 =====
// 获取阶段名称
const getStageName = (sort) => {
    const stageMap = {
        0: '总学分',  // 添加sort=0的映射
        1: '大一',
        2: '大二',
        3: '大三',
        4: '大四'
    };
    return stageMap[sort] || '未知阶段';
};

// 获取阶段类型（用于标签颜色）
const getStageType = (sort) => {
    const typeMap = {
        1: 'primary',  // 大一-蓝色
        2: 'success',  // 大二-绿色
        3: 'warning',  // 大三-橙色
        4: 'danger'    // 大四-红色
    };
    return typeMap[sort] || 'info';
};

// ===== 选择相关函数 =====
const handleSelectionChange = (rows) => {
    // 现在存储的是索引而不是ID
    selectedIndices.value = rows.map(row => allCreditLines.value.indexOf(row));
};

// ===== 对话框相关函数 =====
const handleAdd = () => {
    isEdit.value = false;
    // 重置初始数据
    creditLineInitialData.value = {};
    // 重置表单
    if (creditLineFormRef.value) {
        creditLineFormRef.value.resetForm();
    }
    dialogVisible.value = true;
};

const handleEdit = (row) => {
    isEdit.value = true;
    // 设置初始数据
    creditLineInitialData.value = { ...row };
    // 更新表单数据
    if (creditLineFormRef.value) {
        creditLineFormRef.value.watchProps();
    }
    dialogVisible.value = true;
};

const handleSave = async () => {
    if (!creditLineFormRef.value) return;

    // 验证表单
    const isValid = await creditLineFormRef.value.validateForm();
    if (!isValid) return;

    // 获取表单数据
    const formData = creditLineFormRef.value.getFormData();
    const url = isEdit.value ? '/creditLine/update' : '/creditLine/add';

    request.post(url, formData)
        .then(res => {
            if (res.code === '200') {
                ElMessage.success(isEdit.value ? '编辑成功' : '新增成功');
                dialogVisible.value = false;
                fetchData();
            } else {
                ElMessage.error(res.msg || (isEdit.value ? '编辑失败' : '新增失败'));
            }
        })
        .catch(err => {
            ElMessage.error(isEdit.value ? '编辑失败' : '新增失败');
            console.error('保存学分要求错误:', err);
        });
};

// ===== 删除相关函数 =====
// 统一的删除函数，接收索引数组参数 
const deleteItems = (indices) => { 
    // 增强类型检查，确保indices是数组且有长度
    if (!indices || typeof indices !== 'object' || !Array.isArray(indices) || indices.length === 0) { 
        ElMessage.warning("请选择要删除的数据"); 
        return; 
    } 

    // 构建要删除的ID数组，只包含分段ID
    const deleteIds = [];
    
    // 遍历传入的索引数组
    indices.forEach(index => {
        // 在所有学分要求中查找对应的记录
        const creditLine = allCreditLines.value[index];
        
        // 如果找到该记录，并且存在creditTypes数组，则将所有分段ID添加到删除数组
        if (creditLine && creditLine.creditTypes) {
            creditLine.creditTypes.forEach(type => {
                if (type.id) {
                    deleteIds.push(type.id);
                }
            });
        }
    });

    // 去重，确保每个ID只删除一次
    const uniqueDeleteIds = [...new Set(deleteIds)];

    ElMessageBox.confirm('删除后数据无法恢复，您确定删除吗？', '删除确认', { type: 'warning' }) 
        .then(() => { 
            // 传递包含分段ID的数组给后端
            request.post("/creditLine/delete", uniqueDeleteIds).then(res => { 
                if (res.code === '200') { 
                    ElMessage.success('操作成功'); 
                    fetchData(); 
                    // 清空选中的索引
                    selectedIndices.value = []; 
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
const del = (index) => { 
    deleteItems([index]); 
}; 

// 批量删除函数 
const delBatch = () => { 
    deleteItems(selectedIndices.value); 
};
</script>

<style scoped>
/* 导入公共列表页样式 */
@import "@/assets/css/manager/list.css";

/* 学分列表左右分割容器 */
.credit-list-split {
    display: flex;
    gap: var(--space-lg);
    margin-bottom: var(--space-lg);
}

/* 左侧表格区域 */
.credit-list-left-section {
    flex: 1;
    min-width: 0;
}

/* 右侧表格区域 */
.credit-list-right-section {
    flex: 1;
    min-width: 0;
}

/* 区域标题 */
.section-title {
    font-size: var(--font-size-md);
    font-weight: var(--font-weight-semibold);
    color: var(--text-primary);
    margin-bottom: var(--space-sm);
    padding-left: var(--space-xs);
}

/* 空状态样式 */
.empty-state {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 200px;
    background-color: var(--bg-gray);
    border-radius: var(--radius-sm);
    margin-top: var(--space-sm);
}

/* 响应式设计 */
@media screen and (max-width: 768px) {
    .credit-list-split {
        flex-direction: column;
        gap: var(--space-lg);
    }
    
    .credit-list-left-section,
    .credit-list-right-section {
        flex: none;
        width: 100%;
    }
}
</style>