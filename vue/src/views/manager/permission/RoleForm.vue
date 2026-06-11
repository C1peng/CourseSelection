<template>
  <div class="form">
    <el-form ref="roleForm" :model="formData" :rules="rules" label-width="100px" :label-position="'right'" style="padding: 20px; width: 100%; box-sizing: border-box;">
      <!-- 角色基本信息 -->
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item prop="roleCode" label="角色编码">
            <el-input v-model="formData.roleCode" placeholder="请输入角色编码" size="large"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item prop="roleName" label="角色名称">
            <el-input v-model="formData.roleName" placeholder="请输入角色名称" size="large"></el-input>
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

const roleForm = ref(null);
const formData = reactive({
  id: '',
  roleCode: '',
  roleName: ''
});

// 验证规则
const rules = {
  roleCode: [
    { required: true, message: '请输入角色编码', trigger: 'blur' }
  ],
  roleName: [
    { required: true, message: '请输入角色名称', trigger: 'blur' }
  ]
};

// 监听props变化，更新表单数据
const watchProps = () => {
  if (props.initialData) {
    Object.assign(formData, {
      id: props.initialData.id || '',
      roleCode: props.initialData.roleCode || '',
      roleName: props.initialData.roleName || ''
    });
  }
};

// 表单验证
const validate = () => {
  return new Promise((resolve, reject) => {
    if (roleForm.value) {
      roleForm.value.validate((valid) => {
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
    roleCode: '',
    roleName: ''
  });
  if (roleForm.value) {
    roleForm.value.clearValidate();
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