<template>
  <div class="form">
    <el-form ref="permissionForm" :model="formData" :rules="rules" label-width="100px" :label-position="'right'" style="padding: 20px; width: 100%; box-sizing: border-box;">
      <!-- 权限基本信息 -->
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item prop="permissionName" label="权限名称">
            <el-input v-model="formData.permissionName" placeholder="请输入权限名称" size="large"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item prop="module" label="模块">
            <el-input v-model="formData.module" placeholder="请输入模块名称" size="large"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item prop="action" label="操作">
            <el-select v-model="formData.action" placeholder="请选择操作" size="large">
              <el-option value="ADD" label="ADD"></el-option>
              <el-option value="UPDATE" label="UPDATE"></el-option>
              <el-option value="DELECT" label="DELECT"></el-option>
              <el-option value="VIEW" label="VIEW"></el-option>
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
import { reactive, ref } from "vue";

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['formDataChange']);

const permissionForm = ref(null);
const formData = reactive({
  id: '',
  permissionName: '',
  module: '',
  action: ''
});

// 验证规则
const rules = {
  permissionName: [
    { required: true, message: '请输入权限名称', trigger: 'blur' }
  ],
  module: [
    { required: true, message: '请输入模块名称', trigger: 'blur' }
  ],
  action: [
    { required: true, message: '请选择操作', trigger: 'blur' }
  ]
};

// 监听props变化，更新表单数据
const watchProps = () => {
  if (props.initialData) {
    Object.assign(formData, {
      id: props.initialData.id || '',
      permissionName: props.initialData.permissionName || '',
      module: props.initialData.module || '',
      action: props.initialData.action || ''
    });
  }
};

// 表单验证
const validate = () => {
  return new Promise((resolve, reject) => {
    if (permissionForm.value) {
      permissionForm.value.validate((valid) => {
        resolve(valid);
      });
    } else {
      resolve(false);
    }
  });
};

// 暴露方法给父组件
const getFormData = () => {
  return { ...formData };
};

const resetForm = () => {
  Object.assign(formData, {
    id: '',
    permissionName: '',
    module: '',
    action: ''
  });
  if (permissionForm.value) {
    permissionForm.value.clearValidate();
  }
};

// 初始化时调用
watchProps();

defineExpose({
  getFormData,
  resetForm,
  watchProps,
  validate
});
</script>