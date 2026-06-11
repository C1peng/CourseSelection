<template>
  <div class="form">
    <el-alert 
      v-if="!isInCreateTimeRange" 
      title="当前不在课程创建时间范围内" 
      type="warning" 
      show-icon 
    />
    
    <el-form ref="courseOfferForm" :model="formData" label-width="100px" :label-position="'right'" style="padding: 20px;">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="courseId" label="课程编号">
            <el-input v-model="formData.courseId" placeholder="请输入课程编号" size="large"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="teacherId" label="教师编号">
            <el-input v-model="formData.teacherId" placeholder="请输入教师编号" size="large"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="semester" label="学期">
            <el-input v-model="formData.semester" placeholder="请输入学期" size="large" @change="checkCreateTimeRange"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="status" label="状态">
            <el-select size="large" v-model="formData.status" placeholder="请选择状态">
              <el-option v-for="(item) in typeEnum.COURSESTATUS" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
  </div>
</template>

<style scoped>
@import "@/assets/css/manager/form.css";
</style>

<script setup>
import { reactive, ref, onMounted } from "vue";
import typeEnum from '@/constant/module/type.js';
import request from '@/utils/request.js';

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['formDataChange']);

// 时间范围检查状态
const isInCreateTimeRange = ref(true);

// 根据枚举对象和值获取对应的常量对象
const getConstantByValue = (enumObj, value) => {
  if (!enumObj || value === undefined || value === null) return null;
  
  // 遍历枚举对象的所有属性
  for (const key in enumObj) {
    if (enumObj.hasOwnProperty(key)) {
      const enumItem = enumObj[key];
      // 将两个值都转换为字符串进行比较，以处理类型不匹配的情况
      if (String(enumItem.value) === String(value)) {
        return enumItem;
      }
    }
  }
  
  return null;
};

const formData = reactive({
  id: '',
  courseId: '',
  teacherId: '',
  semester: '',
  status: typeEnum.COURSESTATUS.DISABLED.value // 使用常量值：启用（注意：DISABLED表示启用状态）
});

// 检查课程创建时间范围
const checkCreateTimeRange = async () => {
  if (formData.semester) {
    try {
      const res = await request.get(`/semester/time/check/create/${formData.semester}`);
      if (res.code === '200') {
        isInCreateTimeRange.value = res.data;
      }
    } catch (error) {
      console.error('检查时间范围失败:', error);
    }
  }
};

// 监听props变化，更新表单数据
const watchProps = () => {
  if (props.initialData) {
    // 获取传入的status值对应的常量对象
    const statusObj = getConstantByValue(typeEnum.COURSESTATUS, props.initialData.status);
    
    Object.assign(formData, {
      id: props.initialData.id || '',
      courseId: props.initialData.courseId || '',
      teacherId: props.initialData.teacherId || '',
      semester: props.initialData.semester || '',
      // 使用常量对象的value属性，确保使用的是有效的枚举值
      status: statusObj ? statusObj.value : typeEnum.COURSESTATUS.DISABLED.value
    });
    
    // 检查时间范围
    checkCreateTimeRange();
  }
};

// 初始化时调用
watchProps();

// 暴露方法给父组件
const getFormData = () => {
  return { ...formData };
};

const resetForm = () => {
  Object.assign(formData, {
    id: '',
    courseId: '',
    teacherId: '',
    semester: '',
    status: typeEnum.COURSESTATUS.DISABLED.value // 使用常量值：启用（注意：DISABLED表示启用状态）
  });
  isInCreateTimeRange.value = true;
};

defineExpose({
  getFormData,
  resetForm,
  watchProps,
  isInCreateTimeRange
});
</script>