<template>
  <div class="form">
    <el-form ref="sectionFormRef" :model="formData" :rules="rules" label-width="120px" label-position="right" style="padding: 10px;">
      <!-- 课程基本信息 -->
      <el-divider content-position="left">基本信息</el-divider>
      <el-row :gutter="15">
        <el-col :span="12" v-if="isAdmin">
          <el-form-item prop="courseId" label="课程编号" required>
            <el-input v-model.number="formData.courseId" :disabled="isEdit" placeholder="请输入课程编号" type="number" />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="isAdmin">
          <el-form-item prop="creditsType" label="学分类型">
            <el-input v-model="creditTypeLabel" :disabled="isEdit" placeholder="请选择学分类型" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="place" label="课程地点">
            <el-input v-model="formData.place" placeholder="请输入课程地点" />
          </el-form-item>
        </el-col>
        <el-col :span="12" v-if="isAdmin">
          <el-form-item prop="teacherId" label="教师ID" required>
            <el-input v-model.number="formData.teacherId" placeholder="请输入教师ID" type="number" :disabled="lockTeacherId" />
          </el-form-item>
        </el-col>
        
        
      <!-- </el-row>
      <el-row :gutter="15"> -->
        <el-col :span="12" v-if="isAdmin">
          <el-form-item prop="semester" label="学期">
            <el-input v-model="formData.semester" placeholder="格式：2024-2025学年第一学期" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="number" label="人数上限" required>
            <el-input v-model.number="formData.number" placeholder="请输入人数上限" type="number" />
          </el-form-item>
        </el-col>
      <!-- </el-row>
      <el-row :gutter="15"> -->
        <el-col :span="12">
          <el-form-item prop="status" label="状态">
            <el-select v-model.number="formData.status" placeholder="请选择状态">
              <el-option v-for="statusItem in Object.values(typeEnum.COURSESTATUS)" :key="statusItem.value" :value="parseInt(statusItem.value)" :label="statusItem.label" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 时间安排 -->
      <el-divider content-position="left">时间安排</el-divider>
      <el-row :gutter="15">
        <el-col :span="12">
          <el-form-item prop="courseTotal" label="课程节数" required>
            <el-input v-model.number="formData.courseTotal" placeholder="请输入课程节数" type="number" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="cycle" label="周次类型" required>
            <el-select v-model="formData.cycle" placeholder="请选择周次类型">
              <el-option v-for="cycleItem in Object.values(CYCLE_TYPE)" :key="cycleItem.value" :value="cycleItem.value" :label="cycleItem.label" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item prop="weekTime" label="上课时间" required>
            <el-card class="cs-schedule-card" shadow="hover" style="border-radius: 8px; overflow: hidden;">
              <!-- 周几选择 -->
              <div class="cs-schedule-card__section">
                <div class="cs-schedule-card__row">
                  <div class="cs-schedule-card__title">
                    <span class="cs-schedule-card__title-text">选择星期</span>
                  </div>
                  <div class="cs-schedule-card__weekday-selector">
                    <el-radio-group v-model="selectedWeekday" class="cs-schedule-card__weekday-group">
                      <el-radio v-for="day in weekdays" :key="day.value" :value="day.value" class="cs-schedule-card__weekday-radio">
                        {{ day.label }}
                      </el-radio>
                    </el-radio-group>
                  </div>
                </div>
              </div>
              
              <!-- 节数选择 -->
              <div class="cs-schedule-card__section">
                <div class="cs-schedule-card__row">
                  <div class="cs-schedule-card__title">
                    <span class="cs-schedule-card__title-text">选择节数范围</span>
                  </div>
                  <div class="cs-schedule-card__section-range-container">
                    <el-slider
                      v-model="sectionRangeValues"
                      range
                      :min="1"
                      :max="12"
                      :marks="sectionMarks"
                      :format-tooltip="formatTooltip"
                      @change="handleSectionChange"
                      class="cs-schedule-card__section-slider"
                    />
                  </div>
                </div>
              </div>
              
              <!-- 操作按钮和预览 -->
              <div class="cs-schedule-card__actions">
                <div v-if="formData.weekTime" class="cs-schedule-card__time-preview">
                  <el-tag type="success" effect="light" class="cs-schedule-card__preview-tag">
                    {{ displayWeekTime }}
                  </el-tag>
                </div>
                <el-button 
                  type="primary" 
                  class="cs-schedule-card__confirm-button"
                  @click="updateWeekTime"
                  :disabled="!selectedWeekday || !startSection || !endSection"
                >
                  确认时间
                </el-button>
              </div>
            </el-card>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref, computed } from 'vue';
import typeEnum from '@/constant/module/type.js';
import { WEEKDAY, CYCLE_TYPE } from '@/constant/module/type.js';
import { getUserRole } from '@/utils/userStorage.js';

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({})
  },
  isEdit: {
    type: Boolean,
    default: false
  },
  lockTeacherId: {
    type: Boolean,
    default: false
  }
});

const emit = defineEmits(['formDataChange']);

// 表单引用
const sectionFormRef = ref(null);

// 计算属性：判断是否是管理员
const isAdmin = computed(() => {
  const role = getUserRole();
  return role === 'ADMIN';
});

// 表单数据 - 根据后端CSDetailsDTO类型调整字段类型
const formData = reactive({
  id: null, 
  courseId: null, 
  creditsType: '',
  teacherId: null,
  place: null,
  semester: null,
  number: 0,
  currentStudents: 0,
  status: 0,
  weekTime: null,
  courseTotal: null, 
  cycle: null
});

// 上课时间相关数据
const selectedWeekday = ref('');
const startSection = ref('1');
const endSection = ref('2');
const sectionRangeValues = ref([1, 2]);

// 从type.js导入的星期常量转换为数组形式
const weekdays = Object.values(WEEKDAY).map(item => ({
  value: item.value,
  label: `周${item.label}`
}));

// 星期文本到数值的映射（用于解析旧格式字符串）
const weekdayTextToValue = Object.values(WEEKDAY).reduce((map, item) => {
  map[item.label] = item.value;
  return map;
}, {});

// 星期文本映射
const weekdayTextMap = Object.values(WEEKDAY).reduce((map, item) => {
  map[item.value] = `周${item.label}`;
  return map;
}, {});

// 节数标记（用于滑块）
const sectionMarks = {};
for (let i = 1; i <= 12; i++) {
  sectionMarks[i] = i.toString();
}

// 滑块提示格式化
const formatTooltip = (val) => `${val}节`;

// 调整节数范围，防止跨时间段选择（4-5或8-9）
const adjustSectionRange = (start, end) => {
  let adjustedStart = start;
  let adjustedEnd = end;
  
  if ((adjustedStart === 4 && adjustedEnd === 5) || (adjustedStart === 8 && adjustedEnd === 9)) {
    if (adjustedStart === 4) {
      adjustedStart = 3;
      adjustedEnd = 4;
    } else {
      adjustedStart = 7;
      adjustedEnd = 8;
    }
  }
  
  return { start: adjustedStart, end: adjustedEnd };
};

// 生成后端用的数值类型weekTime
const generateWeekTime = (weekday, startSection, endSection) => {
  const weekdayNum = String(weekday).padStart(2, '0');
  const startNum = String(startSection).padStart(2, '0');
  const endNum = String(endSection).padStart(2, '0');
  
  return parseInt(weekdayNum + startNum + endNum);
};

// 解析weekTime值为星期、开始节数和结束节数的对象
const parseWeekTimeToComponents = (weekTimeValue) => {
  const weekTimeStr = String(weekTimeValue);
  const paddedStr = weekTimeStr.padStart(6, '0');
  
  const weekday = parseInt(paddedStr.substring(0, 2));
  const start = parseInt(paddedStr.substring(2, 4));
  const end = parseInt(paddedStr.substring(4, 6));
  
  const adjusted = adjustSectionRange(start, end);
  
  return {
    weekday: String(weekday),
    start: adjusted.start,
    end: adjusted.end
  };
};

// 处理节数滑块变化
const handleSectionChange = (values) => {
  const newStart = values[0];
  const newEnd = values[1];
  
  // 单个节数选择
  if (newStart === newEnd) {
    startSection.value = newStart;
    endSection.value = newStart;
    sectionRangeValues.value = [newStart, newEnd];
    return;
  }
  
  // 防止跨时间段选择（4-5或8-9）
  const adjusted = adjustSectionRange(newStart, newEnd);
  if (adjusted.start !== newStart || adjusted.end !== newEnd) {
    startSection.value = adjusted.start;
    endSection.value = adjusted.end;
    sectionRangeValues.value = [adjusted.start, adjusted.end];
    return;
  }
  
  // 联动逻辑
  if (newStart !== startSection.value) {
    // 拖动了开始滑块
    startSection.value = newStart;
    endSection.value = Math.min(newStart + 1, 12);
    sectionRangeValues.value = [newStart, Math.min(newStart + 1, 12)];
  } else if (newEnd !== endSection.value) {
    // 拖动了结束滑块
    endSection.value = newEnd;
    startSection.value = Math.max(newEnd - 1, 1);
    sectionRangeValues.value = [Math.max(newEnd - 1, 1), newEnd];
  }
  
  // 再次检查联动后是否产生了无效的跨时段选择
  const finalAdjusted = adjustSectionRange(startSection.value, endSection.value);
  if (finalAdjusted.start !== startSection.value || finalAdjusted.end !== endSection.value) {
    startSection.value = finalAdjusted.start;
    endSection.value = finalAdjusted.end;
    sectionRangeValues.value = [finalAdjusted.start, finalAdjusted.end];
  }
};

// 更新上课时间
const updateWeekTime = () => {
  if (!selectedWeekday.value || !startSection.value || !endSection.value) {
    return;
  }
  
  let startVal = startSection.value;
  let endVal = endSection.value;
  
  // 验证：防止跨时间段选择（4-5或8-9）
  const adjusted = adjustSectionRange(startVal, endVal);
  if (adjusted.start !== startVal || adjusted.end !== endVal) {
    startVal = adjusted.start;
    endVal = adjusted.end;
    
    // 更新组件值
    startSection.value = startVal;
    endSection.value = endVal;
    sectionRangeValues.value = [startVal, endVal];
  }
  
  // 存储实际数值类型weekTime
  formData.weekTime = generateWeekTime(selectedWeekday.value, startVal, endVal);
  
  emit('formDataChange', getFormData());
};

// 计算属性：学分类型显示
const creditTypeLabel = computed(() => {
  if (!formData.creditsType) return '';
  
  for (const key in typeEnum.CREDITTYPE) {
    if (typeEnum.CREDITTYPE[key].value === formData.creditsType) {
      return typeEnum.CREDITTYPE[key].label;
    }
  }
  
  return '';
});

// 显示用的计算属性
const displayWeekTime = computed(() => {
  if (!formData.weekTime) return '';
  
  // 处理数值类型的weekTime
  const components = parseWeekTimeToComponents(formData.weekTime);
  
  // 转换为显示文本
  return `${weekdayTextMap[components.weekday] || ''} 第${components.start}-${components.end}节`;
});

// 解析weekTime数值，更新选择器值
const parseWeekTime = (weekTimeValue) => {
  if (!weekTimeValue) return;
  
  // 处理字符串和数值两种情况
  const weekTimeStr = String(weekTimeValue);
  
  // 如果是旧格式字符串，保持兼容处理
  if (weekTimeStr.includes('周') && weekTimeStr.includes('节')) {
    const match = weekTimeStr.match(/周([一二三四五])\s*第(\d+)-(\d+)节/);
    if (match) {
      const [, weekdayText, start, end] = match;
      
      // 使用从type.js导入的WEEKDAY常量进行映射
      selectedWeekday.value = weekdayTextToValue[weekdayText] || '';
      
      // 防止跨时间段选择（4-5或8-9）
      let startVal = parseInt(start);
      let endVal = parseInt(end);
      
      const adjusted = adjustSectionRange(startVal, endVal);
      startVal = adjusted.start;
      endVal = adjusted.end;
      
      startSection.value = startVal;
      endSection.value = endVal;
      sectionRangeValues.value = [startVal, endVal];
      
      // 更新formData.weekTime
      updateWeekTime();
    }
    return;
  }
  
  // 新格式：2位周期、2位开始节数、2位结束节数
  const components = parseWeekTimeToComponents(weekTimeValue);
  
  // 转换为选择器的值
  selectedWeekday.value = components.weekday;
  startSection.value = components.start;
  endSection.value = components.end;
  sectionRangeValues.value = [components.start, components.end];
  
  // 更新formData.weekTime
  formData.weekTime = generateWeekTime(components.weekday, components.start, components.end);
};

// 监听props变化，更新表单数据
const watchProps = (params) => {
  const data = params?.initialData || props.initialData;
  if (data) {
    Object.assign(formData, {
      id: data.id || null,
      courseId: data.courseId || null,
      creditsType: data.creditsType || '',
      teacherId: data.teacherId || null,
      place: data.place || null,
      semester: data.semester || null,
      number: data.number || 0,
      currentStudents: data.currentStudents || 0,
      status: data.status !== undefined ? data.status : 0,
      weekTime: data.weekTime || null,
      courseTotal: data.courseTotal || null,
      cycle: data.cycle || null
    });
    
    parseWeekTime(data.weekTime);
  }
};

// 初始化时调用
watchProps();

// 暴露方法给父组件
const getFormData = () => ({ ...formData });

const resetForm = () => {
  // 尝试通过表单引用重置
  if (sectionFormRef.value) {
    sectionFormRef.value.resetFields();
  }

  Object.assign(formData, {
    id: null,
    courseId: null,
    creditsType: '',
    teacherId: null,
    place: null,
    semester: null,
    number: 0,
    currentStudents: 0,
    status: 0,
    weekTime: null,
    courseTotal: null,
    cycle: null
  });
  
  // 重置选择器
  selectedWeekday.value = '';
  startSection.value = '1';
  endSection.value = '2';
  sectionRangeValues.value = [1, 2];
};

// 学期格式验证
const validateSemesterFormat = (rule, value, callback) => {
  if (!value) {
    callback();
    return;
  }
  
  const semesterRegex = /^20\d{2}-20\d{2}学年第(一|二)学期$/;
  if (semesterRegex.test(value)) {
    callback();
  } else {
    callback(new Error('学期格式不正确，应为：20**-20**学年第(一/二)学期'));
  }
};

// 课程节数验证
const validateCourseTotal = (rule, value, callback) => {
  if (!value) {
    callback();
    return;
  }
  
  if (formData.cycle === CYCLE_TYPE.ALL.value) {
    if (0 < value && value <= 20) {
      callback();
    } else {
      callback(new Error('全周类型课程节数:1-20'));
    }
  } else {
    if (0 < value && value <= 10) {
      callback();
    } else {
      callback(new Error('非全周类型课程节数:1-10'));
    }
  }
};

// 表单验证规则
const rules = reactive({
  teacherId: [
    { required: true, message: '教师ID不能为空', trigger: ['submit'] }
  ],
  number: [
    { required: true, message: '人数上限不能为空', trigger: ['submit'] }
  ],
  courseTotal: [
    { required: true, message: '课程节数不能为空', trigger: ['submit'] },
    { validator: validateCourseTotal, trigger: ['submit'] }
  ],
  cycle: [
    { required: true, message: '周次类型不能为空', trigger: ['submit'] }
  ],
  weekTime: [
    { required: true, message: '上课时间不能为空', trigger: ['submit'] }
  ],
  semester: [
    { validator: validateSemesterFormat, trigger: ['submit'] }
  ]
});

defineExpose({ getFormData, resetForm, watchProps, sectionFormRef });
</script>

<style scoped>
/* 导入公共表单样式 */
@import "@/assets/css/common/form.css";

/* CSS变量定义 */
.form {
  --primary-color: #409eff;
  --primary-hover-color: #66b1ff;
  --border-color: #e4e7ed;
  --text-primary: #303133;
  --text-secondary: #606266;
  --bg-primary: #ffffff;
  --bg-secondary: #f5f7fa;
  --border-radius: 6px;
  --margin-base: 10px;
  --padding-base: 6px;
}

/* 分割线样式 */
:deep(.el-divider) {
  background-color: var(--border-color) !important;
  border: none !important;
  height: 1px !important;
  margin: 12px 0 !important;
}

:deep(.el-divider__text) {
  background-color: var(--bg-secondary) !important;
  padding: 0 12px !important;
  color: var(--text-secondary);
  font-size: 13px;
}

:deep(.el-divider::before),
:deep(.el-divider::after) {
  background-color: var(--border-color) !important;
  border: none !important;
}

/* 上课时间卡片样式 */
.cs-schedule-card {
  background-color: var(--bg-primary);
  border: 1px solid var(--border-color);
  border-radius: 8px;
  padding: 8px;
  overflow: hidden;
}

.cs-schedule-card__section {
  margin-bottom: var(--margin-base);
}

/* 行项目容器 */
.cs-schedule-card__row {
  display: flex;
  align-items: center;
  margin-bottom: 6px;
}

/* 标题样式 */
.cs-schedule-card__title {
  display: flex;
  align-items: center;
  width: 100px;
  margin-right: var(--margin-base);
  flex-shrink: 0;
}

.cs-schedule-card__title-text {
  font-size: 13px;
  font-weight: 500;
  color: var(--text-primary);
  white-space: nowrap;
}

/* 星期选择器样式 */
.cs-schedule-card__weekday-selector {
  flex: 1;
  padding: var(--padding-base);
  border-radius: var(--border-radius);
  background-color: var(--bg-primary);
  min-width: 0;
  overflow-x: auto;
  overflow-y: hidden;
}

.cs-schedule-card__weekday-group {
  display: flex;
  justify-content: flex-start;
  flex-wrap: nowrap;
  gap: 4px;
  align-items: center;
  white-space: nowrap;
  overflow: visible;
  scrollbar-width: none;
  -ms-overflow-style: none;
}

/* 隐藏滚动条但保留功能 */
.cs-schedule-card__weekday-group::-webkit-scrollbar {
  display: none;
}

.cs-schedule-card__weekday-radio {
  padding: 0 2px;
  flex-shrink: 0;
}

:deep(.cs-schedule-card__weekday-radio .el-radio__label) {
  font-size: 13px;
  font-weight: 500;
  padding: 4px 6px;
  border-radius: 4px;
  transition: all 0.3s ease;
  white-space: nowrap;
}

/* 节数选择样式 */
.cs-schedule-card__section-range-container {
  flex: 1;
  padding: 3px 0;
  margin-left: var(--margin-base);
}

.cs-schedule-card__section-slider {
  width: 100%;
}

:deep(.cs-schedule-card__section-slider .el-slider__marks-text) {
  color: var(--text-secondary);
  font-size: 11px;
  margin-top: 3px;
}

:deep(.cs-schedule-card__section-slider .el-slider__button) {
  width: 14px;
  height: 14px;
  border-color: var(--primary-color);
}

:deep(.cs-schedule-card__section-slider .el-slider__button:hover) {
  border-color: var(--primary-hover-color);
}

:deep(.cs-schedule-card__section-slider .el-slider__button.is-active) {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(64, 158, 255, 0.2);
}

/* 操作按钮和预览区域 */
.cs-schedule-card__actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
  padding-top: 8px;
  border-top: 1px dashed var(--border-color);
}

.cs-schedule-card__confirm-button {
  padding: 6px 20px;
  background-color: var(--primary-color);
  border-color: var(--primary-color);
  font-weight: 500;
  margin-left: auto;
}

.cs-schedule-card__confirm-button:hover {
  background-color: var(--primary-hover-color);
  border-color: var(--primary-hover-color);
}

.cs-schedule-card__time-preview {
  flex: 1;
  text-align: left;
}

.cs-schedule-card__preview-tag {
  font-size: 13px;
  padding: 6px 12px;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .cs-schedule-card__weekday-group {
    flex-wrap: wrap;
    gap: 6px;
  }
}

@media (max-width: 768px) {
  .cs-schedule-card__row {
    flex-direction: column;
    align-items: stretch;
  }
  
  .cs-schedule-card__title {
    width: 100%;
    margin-right: 0;
    margin-bottom: 6px;
  }
  
  .cs-schedule-card__weekday-selector {
    width: 100%;
  }
  
  .cs-schedule-card__section-range-container {
    margin-left: 0;
  }
  
  .cs-schedule-card__weekday-radio {
    width: 50%;
    text-align: center;
    margin-bottom: 6px;
  }
  
  .cs-schedule-card__actions {
    flex-direction: column;
    align-items: stretch;
  }
  
  .cs-schedule-card__time-preview {
    margin-top: 8px;
    text-align: center;
  }
}

/* 滑块触摸支持 */
:deep(.el-slider) {
  touch-action: pan-y;
}
</style>