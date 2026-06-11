<template>
  <div class="form">
    <el-form ref="studentForm" :model="formData" :rules="rules" label-width="100px" :label-position="'right'" style="padding: 20px; width: 100%; box-sizing: border-box;">
      <!-- 学生基本信息 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="username" label="账号">
            <el-input v-model="formData.username" placeholder="请输入账号" size="large"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="name" label="姓名">
            <el-input v-model="formData.name" placeholder="请输入姓名" size="large"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="gender" label="性别">
            <el-radio-group v-model="formData.gender">
              <el-radio :value="GENDER.MALE.value">{{ GENDER.MALE.label }}</el-radio>
              <el-radio :value="GENDER.FEMALE.value">{{ GENDER.FEMALE.label }}</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="deptId" label="部门编号">
            <el-input v-model="formData.deptId" placeholder="请输入部门编号" size="large"></el-input>
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

      <!-- 学业相关信息 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="grade" label="年级">
            <el-input v-model="formData.grade" placeholder="请输入入学年份（如2023）" size="large"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="sort" label="阶段">
            <el-select v-model="formData.sort" placeholder="请选择年级阶段" size="large">
              <el-option :label="'大一'" :value="1"></el-option>
              <el-option :label="'大二'" :value="2"></el-option>
              <el-option :label="'大三'" :value="3"></el-option>
              <el-option :label="'大四'" :value="4"></el-option>
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 学分信息 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="publicCredits" label="公共学分">
            <el-input v-model="formData.publicCredits" placeholder="请输入公共学分要求" size="large" type="number"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="foreignLanguageCredits" label="外语学分">
            <el-input v-model="formData.foreignLanguageCredits" placeholder="请输入外语学分要求" size="large" type="number"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="sportCredits" label="体育学分">
            <el-input v-model="formData.sportCredits" placeholder="请输入体育学分要求" size="large" type="number"></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item prop="artCredits" label="艺术类学分">
            <el-input v-model="formData.artCredits" placeholder="请输入艺术类学分要求" size="large" type="number"></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 状态信息 -->
      <el-row :gutter="20">
        <el-col :span="12">
          <el-form-item prop="status" label="在读状态">
            <el-select v-model="formData.status" placeholder="请选择在读状态" size="large">
              <el-option :label="'在读'" :value="0"></el-option>
              <el-option :label="'休学'" :value="1"></el-option>
              <el-option :label="'离校'" :value="2"></el-option>
            </el-select>
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
import { GENDER } from "@/constant/module/type.js";

const props = defineProps({
  initialData: {
    type: Object,
    default: () => ({})
  }
});

const emit = defineEmits(['formDataChange']);

const baseUrl = import.meta.env.VITE_BASE_URL;
const studentForm = ref(null);
// 获取当前年份作为默认值
const currentYear = new Date().getFullYear();
const formData = reactive({
  id: '',
  username: '',
  avatar: '',
  name: '',
  gender: GENDER.MALE.value, // 性别默认值为1（男）
  deptId: '', // 部门编号
  phone: '',
  email: '',
  grade: currentYear.toString(), // 年级（入学年份），默认本年
  sort: '', // 排序，不要默认值
  publicCredits: 0, // 公共学分
  foreignLanguageCredits: 0, // 外语学分
  sportCredits: 0, // 体育学分
  artCredits: 0, // 艺术类学分
  status: 0, // 在读状态（0：在读，1：休学，2：离校）
});

// 表单验证规则
const rules = {
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' }
  ],
  name: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  deptId: [
    { required: true, message: '请输入部门编号', trigger: 'blur' }
  ],
  grade: [
    { required: true, message: '请输入年级', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择在读状态', trigger: 'change' }
  ]
};

// 监听props变化，更新表单数据
const watchProps = () => {
  if (props.initialData) {
    Object.assign(formData, {
      id: props.initialData.id || '',
      username: props.initialData.username || '',
      avatar: props.initialData.avatar || '',
      name: props.initialData.name || '',
      gender: props.initialData.gender !== undefined ? props.initialData.gender : GENDER.MALE.value, // 跟随后端数据变化，无数据时默认1
      deptId: props.initialData.deptId || '', // 部门编号
      phone: props.initialData.phone || '',
      email: props.initialData.email || '',
      grade: props.initialData.grade || currentYear.toString(),
      sort: props.initialData.sort || '', // 不要默认值
      publicCredits: props.initialData.publicCredits || 0,
      foreignLanguageCredits: props.initialData.foreignLanguageCredits || 0,
      sportCredits: props.initialData.sportCredits || 0,
      artCredits: props.initialData.artCredits || 0,
      status: props.initialData.status || 0,
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

// 表单验证方法
const validate = () => {
  return new Promise((resolve, reject) => {
    studentForm.value.validate((valid) => {
      if (valid) {
        resolve(true);
      } else {
        reject(false);
      }
    });
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
    avatar: '',
    name: '',
    gender: GENDER.MALE.value, // 重置时默认值为1（男）
    deptId: '', // 部门编号
    phone: '',
    email: '',
    grade: currentYear.toString(),
    sort: '', // 不要默认值
    publicCredits: 0,
    foreignLanguageCredits: 0,
    sportCredits: 0,
    artCredits: 0,
    status: 0,
  });
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