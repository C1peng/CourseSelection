<template>
  <div class="form-wrapper">
    <el-form ref="creditLineForm" :model="formData" label-width="120px">
      <!-- 基本信息卡片 -->
      <el-card class="info-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><InfoFilled /></el-icon>
            <span>基本信息</span>
          </div>
        </template>
        
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item 
              label="学院编号" 
              prop="deptId"
              :rules="[{ required: true, message: '请输入学院编号', trigger: 'blur' }]"
            >
              <el-input 
                v-model="formData.deptId" 
                placeholder="请输入学院编号" 
                :disabled="isEdit"
                clearable
               :prefix-icon="House"
                style="max-width: 250px;"
              />
            </el-form-item>
          </el-col>
          
          <el-col :span="12">
            <el-form-item 
              label="年级" 
              prop="grade" 
              :rules="[{ required: true, message: '请输入年级', trigger: 'blur' }]"
            >
              <el-input 
                v-model="formData.grade" 
                placeholder="请输入年级（如：2023）" 
                clearable
                :prefix-icon="Calendar"
                style="max-width: 250px;"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-card>

      <!-- 学分要求卡片 -->
      <el-card class="credit-card" shadow="hover">
        <template #header>
          <div class="card-header">
            <el-icon><EditPen /></el-icon>
            <span>学分要求设置</span>
          </div>
        </template>
        
        <!-- 使用Element Plus表格展示学分要求 -->
        <el-table :data="creditTableData" border style="width: 100%;">
          <el-table-column prop="stageName" label="阶段" width="83" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.color">{{ scope.row.stageName }}</el-tag>
            </template>
          </el-table-column>
          
          <!-- 公共选修课学分 -->
          <el-table-column label="公共选修课" width="140" align="center">
            <template #default="scope">
              <el-input-number 
                v-model="formData.stages[scope.row.index].publicCredit" 
                :min="0" 
                :step="0.5"
                size="small"
                style="width: 90px;"
                placeholder="0"
              />
            </template>
          </el-table-column>
          
          <!-- 外语选修课学分 -->
          <el-table-column label="外语选修课" width="140" align="center">
            <template #default="scope">
              <el-input-number 
                v-model="formData.stages[scope.row.index].foreignLanguageCredit" 
                :min="0" 
                :step="0.5"
                size="small"
                style="width: 90px;"
                placeholder="0"
              />
            </template>
          </el-table-column>
          
          <!-- 体育选修课学分 -->
          <el-table-column label="体育选修课" width="140" align="center">
            <template #default="scope">
              <el-input-number 
                v-model="formData.stages[scope.row.index].sportCredit" 
                :min="0" 
                :step="0.5"
                size="small"
                style="width: 90px;"
                placeholder="0"
              />
            </template>
          </el-table-column>
          
          <!-- 艺术选修课学分 -->
          <el-table-column label="艺术选修课" width="140" align="center">
            <template #default="scope">
              <el-input-number 
                v-model="formData.stages[scope.row.index].artCredit" 
                :min="0" 
                :step="0.5"
                size="small"
                style="width: 90px;"
                placeholder="0"
              />
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from "vue";
import { 
  InfoFilled, 
  EditPen, 
  House, 
  Calendar 
} from "@element-plus/icons-vue";

// 表单引用
const creditLineForm = ref(null);

// 阶段配置
const stageConfig = [
  { id: 1, name: '大一', color: 'primary' },
  { id: 2, name: '大二', color: 'success' },
  { id: 3, name: '大三', color: 'warning' },
  { id: 4, name: '大四', color: 'danger' }
];

// 计算表格数据 - 现在基于年级阶段
const creditTableData = computed(() => {
  return formData.stages.map((stage, index) => ({
    index,
    stageName: stage.name,
    color: stage.color,
    id: stage.id
  }));
});

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({})
  },
  isEdit: {
    type: Boolean,
    default: false
  }
});

// 初始化表单数据结构
const formData = reactive({
  // id: '',
  deptId: '',
  grade: '',
  // 使用数组存储四个阶段的数据
  stages: stageConfig.map(config => ({
    ...config,
    sort: config.id,
    publicCredit: 0,
    foreignLanguageCredit: 0,
    sportCredit: 0,
    artCredit: 0,
    // 添加id字段，用于保存后端返回的CreditType id
    typeId: null
  }))
});

// 监听props变化，更新表单数据
const watchProps = () => {
  if (props.initialData) {
    // 更新基本信息
    // formData.id = props.initialData.id || '';
    formData.deptId = props.initialData.deptId || '';
    formData.grade = props.initialData.grade || '';
    
    // 更新各阶段数据
    if (props.initialData.creditTypes) {
      const creditTypes = props.initialData.creditTypes;
      
      formData.stages.forEach(stage => {
        const stageData = creditTypes.find(type => type.sort === stage.sort);
        if (stageData) {
          // 保存后端返回的CreditType id
          stage.typeId = stageData.id || null;
          stage.publicCredit = stageData.publicCredit || 0;
          stage.foreignLanguageCredit = stageData.foreignLanguageCredit || 0;
          stage.sportCredit = stageData.sportCredit || 0;
          stage.artCredit = stageData.artCredit || 0;
        }
      });
    }
  }
};

// 初始化时调用
watchProps();

// 暴露方法给父组件
const getFormData = () => {
  return {
    // id: formData.id,
    deptId: formData.deptId,
    grade: formData.grade,
    // 只构建creditTypes数组，不返回stages数组
    creditTypes: formData.stages.map(stage => ({
      // 包含后端返回的CreditType id
      id: stage.typeId,
      sort: stage.sort,
      publicCredit: stage.publicCredit,
      foreignLanguageCredit: stage.foreignLanguageCredit,
      sportCredit: stage.sportCredit,
      artCredit: stage.artCredit
    }))
  };
};

const resetForm = () => {
  // 重置基本信息
  // formData.id = '';
  formData.deptId = '';
  formData.grade = '';
  
  // 重置各阶段数据
  formData.stages.forEach(stage => {
    stage.typeId = null;
    stage.publicCredit = 0;
    stage.foreignLanguageCredit = 0;
    stage.sportCredit = 0;
    stage.artCredit = 0;
  });
};

// 表单验证
const validateForm = () => {
  return new Promise((resolve, reject) => {
    if (creditLineForm.value) {
      creditLineForm.value.validate((valid) => {
        resolve(valid);
      });
    } else {
      resolve(false);
    }
  });
};

defineExpose({
  getFormData,
  resetForm,
  watchProps,
  validateForm
});
</script>

<style scoped>
.form-wrapper {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: 100%;
}

.info-card {
  margin-bottom: 20px;
  border-radius: 8px;
}

.credit-card {
  border-radius: 8px;
}

.card-header {
  display: flex;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
  color: #333;
}

.card-header .el-icon {
  margin-right: 8px;
  font-size: 18px;
  color: #409eff;
}

/* 调整表格样式 */
:deep(.el-table) {
  border-radius: 8px;
  overflow: hidden;
}

:deep(.el-table__header-wrapper) {
  background-color: #f5f7fa;
}

:deep(.el-table__header th) {
  background-color: #f5f7fa;
  font-weight: bold;
  color: #333;
}

/* 调整卡片阴影 */
:deep(.el-card__body) {
  padding: 20px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-wrapper {
    padding: 10px;
  }

  .el-col {
    width: 100% !important;
  }

  :deep(.el-table) {
    width: 100% !important;
  }
}
</style>