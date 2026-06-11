<template>
  <div class="form">
    <el-form
      ref="courseSelectionForm"
      :model="formData"
      label-width="100px"
      label-position="right"
      style="padding: 20px; width: 100%; box-sizing: border-box"
    >
      <!-- 课程信息 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <!-- 修改prop="Id"为prop="id"，与数据绑定保持一致 -->
          <el-form-item prop="id" label="编号">
            <el-input
              v-model="formData.id"
              disabled
              placeholder="请输入编号"
              size="large"
            />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="courseName" label="课程名称">
            <el-input
              v-model="formData.courseName"
              disabled
              placeholder="请输入课程名称"
              size="large"
            />
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 课程详情 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="creditsType" label="学分类型">
            <el-select
              v-model="formData.creditsType"
              placeholder="请选择学分类型"
              size="large"
            >
              <el-option
                v-for="item in typeEnum.CREDITTYPE"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="number" label="人数">
            <el-input
              v-model="formData.number"
              placeholder="请输入人数"
              size="large"
            />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<script setup>
// 修改 CourseSelectionForm.vue 文件

// 在 script setup 部分添加表单引用
import { reactive, watch, ref } from 'vue';
import typeEnum from '@/constant/module/type.js';

// 组件属性定义
const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({})
  }
});

// 响应式表单数据
const formData = reactive({
  id: '',
  courseName: '',
  creditsType: '',
  number: ''
});

/**
 * 更新表单数据
 * @param {Object} params - 包含initialData的参数对象
 */
const updateFormData = (params) => {
  // 调整参数处理逻辑，支持两种传递格式
  const data = params?.initialData || params || props.initialData;
  if (!data) return;
    
  Object.assign(formData, {
    id: data.courseId || data.id || '',
    courseName: data.courseName || '',
    number: data.number || '',
    creditsType: data.creditsType || ''
  });
};

/**
 * watchProps方法，保持与CSBasicForm兼容
 * @param {Object} params - 包含initialData的参数对象
 */
const watchProps = (params) => {
  updateFormData(params);
};

/**
 * 获取当前表单数据
 * @returns {Object} 表单数据
 */
const getFormData = () => {
  return {
    courseId: formData.id,
    creditsType: formData.creditsType,
    number: formData.number
  };
};

/**
 * 重置表单数据
 */
const resetForm = () => {
  Object.assign(formData, {
    id: '',
    courseName: '',
    number: '',
    creditsType: ''
  });
};

// 监听props变化，实时更新表单数据
watch(
  () => props.initialData,
  (newData) => {
    // console.log('props变化:', newData); // 添加调试日志
    if (newData) {
      updateFormData(newData);
    }
  },
  { deep: true, immediate: true }
);

// 暴露方法给父组件
// 添加表单引用
const courseSelectionForm = ref(null);

// 添加validate方法
const validate = () => {
  return new Promise((resolve, reject) => {
    if (courseSelectionForm.value) {
      courseSelectionForm.value.validate((valid, fields) => {
        if (valid) {
          resolve();
        } else {
          reject({ message: 'validation failed', fields });
        }
      });
    } else {
      reject(new Error('表单引用未初始化'));
    }
  });
};

// 在defineExpose中添加validate方法
defineExpose({
  getFormData,
  resetForm,
  updateFormData,
  watchProps,
  validate // 暴露validate方法
});
</script>

<style scoped>
</style>