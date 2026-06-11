<template>
  <div class="form">
    <el-form ref="passwordForm" :model="formData" :rules="rules" label-width="100px" :label-position="'right'" style="padding: 20px;">
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item prop="password" label="原密码">
            <el-input v-model="formData.password" placeholder="请输入原密码" show-password size="large"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item prop="newPassword" label="新密码">
            <el-input v-model="formData.newPassword" placeholder="请输入新密码" show-password size="large"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item prop="confirmPassword" label="确认密码">
            <el-input v-model="formData.confirmPassword" placeholder="请确认新密码" show-password size="large"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
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

const validatePass = (rule, value, callback) => {
  if (!value) {
    callback(new Error('请确认密码'))
  } else {
    if (value !== formData.newPassword) {
      callback(new Error("确认密码跟新密码不一致!"))
    }
    callback()
  }
}

const formData = reactive({
  id: '',
  password: '',
  newPassword: '',
  confirmPassword: '' // 显式添加confirmPassword字段
});

const rules = {
  password: [
    { required: true, message: '请输入原密码', trigger: 'blur' },
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
  ],
  confirmPassword: [
    { validator: validatePass, trigger: 'blur' }
  ]
};

// 监听props变化，更新表单数据
const watchProps = () => {
  if (props.initialData) {
    Object.assign(formData, {
      id: props.initialData.id || '',
      password: '',
      newPassword: '',
      confirmPassword: '' // 添加confirmPassword字段重置
    });
  }
};

// 初始化时调用
watchProps();

// 暴露方法给父组件 - 移除confirmPassword字段
const getFormData = () => {
  const data = { ...formData };
  delete data.confirmPassword; // 删除confirmPassword字段，不发送给后端
  return data;
};

const resetForm = () => {
  Object.assign(formData, {
    id: '',
    password: '',
    newPassword: '',
    confirmPassword: ''
  });
};

const validateForm = () => {
  return new Promise((resolve, reject) => {
    if (passwordForm.value) {
      passwordForm.value.validate((valid) => {
        resolve(valid);
      });
    } else {
      resolve(false);
    }
  });
};

const passwordForm = ref(null);

defineExpose({
  getFormData,
  resetForm,
  watchProps,
  validateForm
});
</script>

<style scoped>
/* 导入公共表单样式 */
@import "@/assets/css/common/form.css";
</style>