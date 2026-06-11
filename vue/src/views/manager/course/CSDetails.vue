<template>
  <div v-if="selectedCourseData" class="details-container">
    <div class="details-header">
      <h3 class="details-title">
        <el-icon><List /></el-icon>课程详情
      </h3>
      <el-button @click="backToBasicTab">
        <el-icon><ArrowLeft /></el-icon>返回基础列表
      </el-button>
    </div>
    
    <!-- 详情概览卡片 -->
    <el-row :gutter="20" class="overview-cards">
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-value">{{ relatedTotal }}</div>
            <div class="stat-label">总班次数量</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card success-card">
          <div class="stat-content">
            <div class="stat-value">{{ activeSections }}</div>
            <div class="stat-label">启用班次</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card warning-card">
          <div class="stat-content">
            <div class="stat-value">{{ totalStudents }}</div>
            <div class="stat-label">总选课人数</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover" class="stat-card info-card">
          <div class="stat-content">
            <div class="stat-value">{{ avgCapacity }}%</div>
            <div class="stat-label">平均使用率</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 详细数据表格 -->
    <div class="related-table-container">
      <el-table 
        v-loading="relatedSectionsLoading" 
        stripe 
        :data="relatedSections"
        @selection-change="handleSelectionChange" 
        :header-cell-style="headerCellStyle" 
        class="related-table">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="序号" align="center" width="80" />
        <el-table-column prop="courseName" label="课程名称" align="center" width="100" />
        <el-table-column prop="teacherName" label="教师姓名" align="center" width="100" />
        <el-table-column prop="creditsType" label="学分类型" align="center" width="120">
          <template v-slot="scope">
            <el-tag :type="getCreditTypeTagType(scope.row.creditsType)" class="credit-tag">
              {{ getCreditTypeLabel(scope.row.creditsType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="place" label="课程地点" align="center" width="120" />
        <el-table-column prop="number" label="人数上限" align="center" width="100" />
        <el-table-column prop="currentStudents" label="当前人数" align="center" width="100" />
        <el-table-column prop="status" label="状态" align="center" width="80">
          <template v-slot="scope">
            <el-tag :type="getCourseStatusTagType(scope.row.status)">
              {{ getCourseStatusLabel(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="semester" label="学期" align="center" width="120" />
        <el-table-column prop="courseTotal" label="课程节数" align="center" width="100" />
        <el-table-column prop="cycle" label="周次类型" align="center" width="100">
          <template v-slot="scope">
            <el-tag :type="getCycleTypeTagType(scope.row.cycle)" class="cycle-tag">
              {{ getCycleTypeLabel(scope.row.cycle) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="weekTime" label="上课时间" align="center" width="100">
          <template v-slot="scope">
            {{ formatWeekTime(scope.row.weekTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="createdTime" label="创建时间" align="center" width="160" />

        <el-table-column label="操作" fixed="right" align="center" width="240">
          <template v-slot="scope">
            <div class="table-actions">
              <el-button type="primary" size="small" :icon="Edit" @click="handleEditSection(scope.row)" title="编辑" />
              <el-button type="danger" size="small" :icon="Delete" @click="handleDelete(scope.row.id)" title="删除" />
            </div>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container" v-if="relatedTotal">
        <el-pagination 
          @size-change="handleRelatedSizeChange" 
          @current-change="handleRelatedCurrentChange"
          v-model:current-page="relatedPagination.currentPage"
          v-model:page-size="relatedPagination.pageSize" 
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper" 
          :total="relatedTotal" 
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { List, ArrowLeft, Edit, Delete } from '@element-plus/icons-vue';
import typeEnum, { CYCLE_TYPE, WEEKDAY, COURSE_STATUS } from '@/constant/module/type.js';

const props = defineProps({
  selectedCourseData: {
    type: Object,
    default: null
  },
  relatedSections: {
    type: Array,
    default: () => []
  },
  relatedTotal: {
    type: Number,
    default: 0
  },
  relatedSectionsLoading: {
    type: Boolean,
    default: false
  },
  relatedPagination: {
    type: Object,
    default: () => ({
      currentPage: 1,
      pageSize: 10
    })
  }
});

const emit = defineEmits([
  'back-to-basic-tab',
  'edit-section',
  'delete-section',
  'selection-change',
  'related-size-change',
  'related-current-change'
]);

const headerCellStyle = {
  backgroundColor: '#fafafa',
  fontWeight: '600',
  color: '#606266'
};

// 计算属性：统计信息
const activeSections = computed(() => {
  return props.relatedSections.filter(section => section.status === 1).length;
});

const totalStudents = computed(() => {
  return props.relatedSections.reduce((sum, section) => sum + (section.currentStudents || 0), 0);
});

const avgCapacity = computed(() => {
  const totalCapacity = props.relatedSections.reduce((sum, section) => {
    return section.number > 0 ? sum + ((section.currentStudents || 0) / section.number * 100) : sum;
  }, 0);
  return props.relatedSections.length > 0 ? Math.round(totalCapacity / props.relatedSections.length) : 0;
});

// 事件处理函数
const backToBasicTab = () => emit('back-to-basic-tab');
const handleEditSection = (row) => emit('edit-section', row);
const handleDelete = (id) => emit('delete-section', id);
const handleSelectionChange = (val) => emit('selection-change', val);
const handleRelatedSizeChange = (size) => emit('related-size-change', size);
const handleRelatedCurrentChange = (current) => emit('related-current-change', current);

// 辅助函数
const getCreditTypeLabel = (value) => {
  const entry = Object.entries(typeEnum.CREDITTYPE).find(([_, item]) => item.value === value);
  return entry ? entry[1].label : value;
};

const getCreditTypeTagType = (value) => {
  const typeMap = {
    'PUBLIC': 'primary',
    'FOREIGN_LANGUAGE': 'success',
    'SPORT': 'warning',
    'ART': 'info',
    'OTHER': 'danger'
  };
  return typeMap[value] || 'info';
};

const getCycleTypeLabel = (value) => {
  const entry = Object.entries(CYCLE_TYPE).find(([_, item]) => item.value === value);
  return entry ? entry[1].label : value;
};

const getCycleTypeTagType = (value) => {
  const typeMap = {
    'SINGLE': 'primary',
    'DOUBLE': 'success',
    'ALL': 'warning'
  };
  return typeMap[value] || 'info';
};

// 获取课程状态标签
const getCourseStatusLabel = (value) => {
  const entry = Object.entries(COURSE_STATUS).find(([_, item]) => item.value === value);
  return entry ? entry[1].label : value;
};

// 获取课程状态标签类型
const getCourseStatusTagType = (value) => {
  const typeMap = {
    0: 'danger', // 未开始
    1: 'success', // 进行中
    2: 'info' // 已结课
  };
  return typeMap[value] || 'info';
};

// 新增：解析weekTime函数，使用WEEKDAY常量
const formatWeekTime = (weekTime) => {
  if (!weekTime) return '';
  
  // 将数字转换为字符串，确保格式统一
  const timeStr = weekTime.toString();
  
  // 解析规则：20304 -> 星期二 3-4节
  // 第一位：星期几（1=星期一，2=星期二，...，7=星期日）
  // 中间两位：开始节数
  // 最后两位：结束节数
  
  const weekDay = parseInt(timeStr[0]);
  const startSection = parseInt(timeStr.substring(1, 3));
  const endSection = parseInt(timeStr.substring(3));
  
  // 使用type.js中的WEEKDAY常量
  const weekdayMap = {
    '1': WEEKDAY.MONDAY.label,
    '2': WEEKDAY.TUESDAY.label,
    '3': WEEKDAY.WEDNESDAY.label,
    '4': WEEKDAY.THURSDAY.label,
    '5': WEEKDAY.FRIDAY.label,
  };
  
  return `星期${weekdayMap[weekDay]} ${startSection}-${endSection}节`;
};
</script>

<style scoped>
/* 详情页面样式 */
.details-container {
  animation: fadeIn 0.3s ease;
  background: #fff;
  border-radius: 6px;
  padding: 0px;
  box-shadow: 0 2px 10px 0 rgba(0, 0, 0, 0.05);
  position: relative;
}

.details-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
}

.details-title {
  font-size: 16px;
  font-weight: 600;
  margin: 0;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

/* 统计卡片样式 */
.overview-cards {
  margin-bottom: 24px;
}

.stat-card {
  border-radius: 8px;
  transition: all 0.3s ease;
  height: auto;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 12px 0 rgba(0, 0, 0, 0.08);
}

.stat-content {
  text-align: center;
  padding: 10px 12px;
}

.stat-value {
  font-size: 22px;
  font-weight: bold;
  line-height: 1.2;
  margin-bottom: 4px;
  transition: all 0.3s ease;
}

.stat-card:hover .stat-value {
  transform: scale(1.05);
}

.stat-label {
  font-size: 13px;
  color: #606266;
  font-weight: 500;
}

/* 统计卡片背景颜色变体 */
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

/* 表格操作按钮样式 */
.table-actions {
  display: flex;
  justify-content: center;
  gap: 8px;
}


/* 分页样式 - 修复布局问题 */
.pagination-container {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding: 20px 0;
  margin-top: 20px;
}

/* 动画效果 */
@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }

  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>