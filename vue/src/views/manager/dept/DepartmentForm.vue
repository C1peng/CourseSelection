<template>
  <div class="form">
    <el-form ref="deptFormRef" :model="formData" :rules="rules" label-width="120px" style="padding: 20px;">
      <el-form-item label="学院编号" prop="deptId" required>
        <el-input v-model="formData.deptId" placeholder="请输入学院编号" />
      </el-form-item>
      <el-form-item label="学院名称" prop="deptName" required>
        <el-input v-model="formData.deptName" placeholder="请输入学院名称" />
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { reactive, ref } from "vue";

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['formDataChange']);

const formData = reactive({
  id: '',
  deptId: '',
  deptName: ''
});

// 添加表单引用
const deptFormRef = ref(null);

// 监听props变化，更新表单数据
const watchProps = () => {
  if (props.initialData) {
    Object.assign(formData, {
      id: props.initialData.id || '',
      deptId: props.initialData.deptId || '',
      deptName: props.initialData.deptName || ''
    });
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
    deptId: '',
    deptName: ''
  });
  // 使用表单引用重置表单
  if (deptFormRef.value) {
    deptFormRef.value.resetFields();
  }
};

// 添加表单验证方法
const validate = () => {
  return new Promise((resolve, reject) => {
    if (deptFormRef.value) {
      deptFormRef.value.validate((valid, fields) => {
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
  validate // 暴露验证方法
});

// 表单验证规则，增加blur和change触发时机
const rules = reactive({
  deptId: [
    { required: true, message: '学院编号不能为空', trigger: ['submit', 'blur', 'change'] }
  ],
  deptName: [
    { required: true, message: '学院名称不能为空', trigger: ['submit', 'blur', 'change'] }
  ]
});
</script>

<style scoped>
@import "@/assets/css/manager/form.css";
</style>