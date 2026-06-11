<template>
  <div class="form">
    <el-form ref="courseInfoForm" :model="formData" :rules="rules" label-width="100px" :label-position="'right'" style="padding: 20px;">
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="courseId" label="课程编号" required>
            <el-input v-model="formData.courseId" placeholder="请输入课程编号" size="large"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="courseName" label="课程名" required>
            <el-input v-model="formData.courseName" placeholder="请输入课程名" size="large"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="hours" label="学时">
            <el-input v-model.number="formData.hours" placeholder="请输入学时" type="number" size="large"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="credits" label="学分" required>
            <el-input v-model.number="formData.credits" placeholder="请输入学分" type="number" size="large"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="courseType" label="课程类型">
            <el-select size="large" v-model="formData.courseType" placeholder="请选择课程类型">
              <el-option v-for="(item) in typeEnum.COURSETYPE" :key="item.value" :label="item.label" :value="item.value"></el-option>
            </el-select>
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
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="deptId" label="学院编号" required>
            <el-input v-model="formData.deptId" placeholder="请输入学院编号" size="large"></el-input>
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
import { reactive, ref } from "vue";
import typeEnum from '@/constant/module/type.js';

const courseInfoForm = ref(null);

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['formDataChange']);

const getConstantByValue = (enumObj, value) => {
  if (!enumObj || value === undefined || value === null) return null;
  
  for (const key in enumObj) {
    if (enumObj.hasOwnProperty(key)) {
      const enumItem = enumObj[key];
      if (String(enumItem.value) === String(value)) {
        return enumItem;
      }
    }
  }
  
  return null;
};

const formData = reactive({
  id: null,
  courseId: '',
  courseName: '',
  hours: null,
  credits: null,
  courseType: typeEnum.COURSETYPE.SELECTION.value,
  status: typeEnum.COURSESTATUS.DISABLED.value,
  deptId: ''
});

// 重新编写的验证规则
const rules = reactive({
  courseId: [
    { required: true, message: '课程编号不能为空', trigger: ['submit', 'blur', 'change'] }
  ],
  courseName: [
    { required: true, message: '课程名不能为空', trigger: ['submit', 'blur', 'change'] }
  ],
  hours: [
    { required: true, message: '学时不能为空', trigger: ['submit', 'blur', 'change'] },
    { type: 'number', message: '学时必须是数字', trigger: ['submit', 'blur', 'change'] },
    { 
      validator: (rule, value, callback) => {
        if (value < 1 || value > 200) {
          callback(new Error('学时必须在1到200之间'));
        } else {
          callback();
        }
      }, 
      trigger: ['submit', 'blur', 'change'] 
    }
  ],
  credits: [
    { required: true, message: '学分不能为空', trigger: ['submit', 'blur', 'change'] },
    { type: 'number', message: '学分必须是数字', trigger: ['submit', 'blur', 'change'] },
    { 
      validator: (rule, value, callback) => {
        if (value < 0 || value > 3) {
          callback(new Error('学分必须在0到3之间'));
        } else {
          callback();
        }
      }, 
      trigger: ['submit', 'blur', 'change'] 
    }
  ],
  courseType: [
    { required: true, message: '课程类型不能为空', trigger: ['submit', 'blur', 'change'] }
  ],
  status: [
    { required: true, message: '状态不能为空', trigger: ['submit', 'blur', 'change'] }
  ],
  deptId: [
    { required: true, message: '学院编号不能为空', trigger: ['submit', 'blur', 'change'] }
  ]
});

const watchProps = () => {
  if (props.initialData) {
    const courseTypeObj = getConstantByValue(typeEnum.COURSETYPE, props.initialData.courseType);
    const statusObj = getConstantByValue(typeEnum.COURSESTATUS, props.initialData.status);
    
    Object.assign(formData, {
      id: props.initialData.id || null,
      courseId: props.initialData.courseId || '',
      courseName: props.initialData.courseName || '',
      hours: props.initialData.hours || null,
      credits: props.initialData.credits || null,
      courseType: courseTypeObj ? courseTypeObj.value : typeEnum.COURSETYPE.SELECTION.value,
      status: statusObj ? statusObj.value : typeEnum.COURSESTATUS.DISABLED.value,
      deptId: props.initialData.deptId || ''
    });
  }
};

watchProps();

const getFormData = () => {
  return { ...formData };
};

const resetForm = () => {
  Object.assign(formData, {
    id: null,
    courseId: '',
    courseName: '',
    hours: null,
    credits: null,
    courseType: typeEnum.COURSETYPE.SELECTION.value,
    status: typeEnum.COURSESTATUS.DISABLED.value,
    deptId: ''
  });
};

const validate = () => {
  return new Promise((resolve, reject) => {
    if (courseInfoForm.value) {
      courseInfoForm.value.validate((valid, fields) => {
        if (valid) {
          resolve(true);
        } else {
          reject(fields);
        }
      });
    } else {
      reject(new Error('表单引用未获取到'));
    }
  });
};

defineExpose({
  getFormData,
  resetForm,
  watchProps,
  validate
});
</script>