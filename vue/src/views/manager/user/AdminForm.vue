<template>
  <div class="form">
    <el-form ref="adminForm" :model="formData" :rules="rules" label-width="100px" :label-position="'right'" style="padding: 20px; width: 100%; box-sizing: border-box;">
      <!-- 管理员基本信息 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="username" label="用户名">
            <el-input v-model="formData.username" placeholder="请输入用户名" size="large"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="name" label="姓名">
            <el-input v-model="formData.name" placeholder="请输入姓名" size="large"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 联系方式 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="phone" label="电话">
            <el-input v-model="formData.phone" placeholder="请输入电话" size="large"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="email" label="邮箱">
            <el-input v-model="formData.email" placeholder="请输入邮箱" size="large"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 头像上传 -->
      <el-row :gutter="20">
        <el-col :span="24">
          <el-form-item prop="avatar" label="头像">
            <el-upload :action="baseUrl + '/files/upload'" :on-success="handleFileUpload" list-type="picture">
              <el-button type="primary">点击上传</el-button>
            </el-upload>
            <div v-if="formData.avatar" class="upload-preview">
              <el-image :src="formData.avatar" style="width: 80px; height: 80px; border-radius: 4px;" 
                class="avatar-hover" :preview-src-list="[formData.avatar]" preview-teleported />
            </div>
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
import { ElMessage } from "element-plus";

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['formDataChange']);

const baseUrl = import.meta.env.VITE_BASE_URL;
const adminForm = ref(null);
const formData = reactive({
  id: '',
  username: '',
  name: '',
  phone: '',
  email: '',
  avatar: ''
});

// 验证规则
const rules = {
  username: [
    { required: true, message: '用户名不能为空', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '姓名不能为空', trigger: 'blur' }
  ]
};

// 监听props变化，更新表单数据
const watchProps = () => {
  if (props.initialData) {
    Object.assign(formData, {
      id: props.initialData.id || '',
      username: props.initialData.username || '',
      name: props.initialData.name || '',
      phone: props.initialData.phone || '',
      email: props.initialData.email || '',
      avatar: props.initialData.avatar || ''
    });
  }
};

// 文件上传处理
const handleFileUpload = (res) => {
  if (res.code === '200') {
    formData.avatar = res.data;
    ElMessage.success('上传成功');
  } else {
    ElMessage.error('上传失败');
  }
};

// 表单验证
const validate = () => {
  return new Promise((resolve, reject) => {
    if (adminForm.value) {
      adminForm.value.validate((valid) => {
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
    username: '',
    name: '',
    phone: '',
    email: '',
    avatar: ''
  });
  if (adminForm.value) {
    adminForm.value.clearValidate();
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