<template>
  <div class="login-page" ref="pageRef">
    <!-- 动态背景层 -->
    <div class="login-bg" aria-hidden="true">
      <div class="bg-orb orb-1"></div>
      <div class="bg-orb orb-2"></div>
      <div class="bg-orb orb-3"></div>
      <div class="bg-grid"></div>
    </div>

    <div class="login-shell">
      <!-- 左侧视觉区域 -->
      <div class="login-hero">
        <div class="hero-main">
          <div class="hero-icon">
            <span>选</span>
          </div>
          <h2 class="hero-title">高校选修课</h2>
          <p class="hero-subtitle">学分管理系统</p>

          <ul class="hero-feature-list">
            <li class="feature-item">
              <span class="feature-dot"></span>
              <span>公共选修课统一管理</span>
            </li>
            <li class="feature-item">
              <span class="feature-dot"></span>
              <span>实时掌握学分达标情况</span>
            </li>
            <li class="feature-item">
              <span class="feature-dot"></span>
              <span>支持学生 / 教师多角色登录</span>
            </li>
          </ul>
        </div>
      </div>

      <!-- 右侧表单区域 -->
      <div class="login-panel">
        <div class="panel-header">
          <div class="logo-row">
            <img src="@/assets/imgs/logo.png" alt="系统Logo" class="logo" />
            <div class="logo-text">
              <div class="system-name">高校选修课管理系统</div>
              <div class="system-subname">Student Elective Course System</div>
            </div>
          </div>
          <h1 class="login-title">欢迎登录</h1>
          <p class="login-subtitle">请输入账号信息以继续使用系统</p>
        </div>

        <el-form
          ref="formRef"
          :model="data.form"
          :rules="data.rules"
          class="login-form"
        >
          <el-form-item prop="username">
            <el-input
              :prefix-icon="User"
              size="large"
              v-model="data.form.username"
              placeholder="请输入学号 / 工号"
              class="form-input"
            ></el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              show-password
              :prefix-icon="Lock"
              size="large"
              v-model="data.form.password"
              placeholder="请输入密码"
              class="form-input"
            ></el-input>
          </el-form-item>

          <el-form-item prop="role">
            <el-select
              size="large"
              v-model="data.form.role"
              class="form-input"
              placeholder="请选择登录身份"
            >
              <el-option value="STUDENT" label="学生登录"></el-option>
              <el-option value="TEACHER" label="教师登录"></el-option>
            </el-select>
          </el-form-item>

          <div class="form-extra-row">
            <el-checkbox v-model="data.rememberMe" class="remember">
              记住密码
            </el-checkbox>
            <el-link type="info" :underline="'never'" class="forget-link">
              忘记密码？
            </el-link>
          </div>

          <el-button
            size="large"
            type="primary"
            class="login-button"
            @click="login"
          >
            登录系统
          </el-button>

          <div class="login-footer">
            <span>首次使用或账号异常？</span>
            <el-link type="primary" :underline="'never'">
              请联系辅导员或教务老师
            </el-link>
          </div>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from "vue";
import { User, Lock } from "@element-plus/icons-vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";
import router from "@/router/index.js";
import { saveUserInfo, clearUserInfo } from "@/utils/userStorage.js";

const data = reactive({
  form: { 
    username: '',
    password: '',
    role: 'STUDENT' // 默认角色为学生
  },
  rules: {
    username: [
      { required: true, message: '请输入学号', trigger: 'blur' }
    ],
    password: [
      { required: true, message: '请输入密码', trigger: 'blur' }
    ]
  },
  rememberMe: false
})

const formRef = ref()
const pageRef = ref(null)

let rafId = 0
const prefersReducedMotion = typeof window !== 'undefined'
  ? window.matchMedia?.('(prefers-reduced-motion: reduce)') ?? null
  : null

function setParallaxVars(px, py) {
  if (!pageRef.value) return
  const rx = py * -0.35
  const ry = px * 0.4
  pageRef.value.style.setProperty('--px', `${px}px`)
  pageRef.value.style.setProperty('--py', `${py}px`)
  pageRef.value.style.setProperty('--gpx', `${px * 0.2}px`)
  pageRef.value.style.setProperty('--gpy', `${py * 0.2}px`)
  pageRef.value.style.setProperty('--rx', `${rx}deg`)
  pageRef.value.style.setProperty('--ry', `${ry}deg`)
}

function onPointerMove(e) {
  if (!pageRef.value) return
  if (prefersReducedMotion?.matches) return

  const rect = pageRef.value.getBoundingClientRect()
  const cx = rect.left + rect.width / 2
  const cy = rect.top + rect.height / 2

  const dx = (e.clientX - cx) / (rect.width / 2)
  const dy = (e.clientY - cy) / (rect.height / 2)

  const x = Math.max(-1, Math.min(1, dx))
  const y = Math.max(-1, Math.min(1, dy))

  const px = x * 14
  const py = y * 10

  cancelAnimationFrame(rafId)
  rafId = requestAnimationFrame(() => setParallaxVars(px, py))
}

function onPointerLeave() {
  cancelAnimationFrame(rafId)
  setParallaxVars(0, 0)
}

onMounted(() => {
  setParallaxVars(0, 0)
  if (pageRef.value) {
    pageRef.value.addEventListener('pointermove', onPointerMove, { passive: true })
    pageRef.value.addEventListener('pointerleave', onPointerLeave, { passive: true })
  }
})

onBeforeUnmount(() => {
  cancelAnimationFrame(rafId)
  if (pageRef.value) {
    pageRef.value.removeEventListener('pointermove', onPointerMove)
    pageRef.value.removeEventListener('pointerleave', onPointerLeave)
  }
})

const login = () => {
  formRef.value.validate(valid => {
    if (valid) { // 表示表单校验通过
      request.post('/login', data.form).then(res => {
        if (res.code === '200') {
          ElMessage.success('登录成功')
          // 清除旧用户信息
          clearUserInfo();
          // 存储新用户信息到浏览器的缓存，包含选择的角色
          const userInfo = {
            ...res.data,
            role: data.form.role
          };
          saveUserInfo(userInfo);
          // 根据角色跳转到不同页面
          if (data.form.role === 'TEACHER') {
            // 教师登录后跳转到教师工作台
            router.push('/teacher/dashboard')
          } else {
            // 学生登录后跳转到前端首页
            router.push('/student/home')
          }
        } else {
          ElMessage.error(res.msg)
        }
      })
    }
  })
}
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
  /* 改为与管理员登录页一致的绿色背景 */
  background: radial-gradient(circle at top left, #a8f5c8 0, #06b981 45%, #019267 100%);
  padding: 24px;
  box-sizing: border-box;
}

.login-page::before {
  content: "";
  position: absolute;
  inset: -24%;
  pointer-events: none;
  /* 背景高光也统一成绿色系 */
  background:
    radial-gradient(60% 60% at 20% 15%, rgba(255, 255, 255, 0.34) 0%, rgba(255, 255, 255, 0) 60%),
    radial-gradient(55% 55% at 88% 18%, rgba(16, 185, 129, 0.55) 0%, rgba(16, 185, 129, 0) 62%),
    radial-gradient(55% 55% at 70% 88%, rgba(2, 122, 74, 0.55) 0%, rgba(2, 122, 74, 0) 62%),
    linear-gradient(135deg, rgba(255, 255, 255, 0.12), rgba(255, 255, 255, 0));
  transform: translate3d(var(--px, 0px), var(--py, 0px), 0) scale(1.05);
  transform-origin: center;
  animation: bgShift 16s ease-in-out infinite alternate;
}

.login-bg {
  position: absolute;
  inset: 0;
  pointer-events: none;
  z-index: 0;
}

.bg-grid {
  position: absolute;
  inset: 0;
  opacity: 0.12;
  background-image:
    linear-gradient(to right, rgba(255, 255, 255, 0.35) 1px, transparent 1px),
    linear-gradient(to bottom, rgba(255, 255, 255, 0.25) 1px, transparent 1px);
  background-size: 54px 54px;
  transform: translate3d(var(--gpx, 0px), var(--gpy, 0px), 0);
  animation: gridDrift 26s linear infinite;
  mask-image: radial-gradient(circle at 50% 50%, rgba(0,0,0,0.8), rgba(0,0,0,0));
}

.bg-orb {
  position: absolute;
  width: 420px;
  height: 420px;
  border-radius: 999px;
  filter: blur(18px);
  opacity: 0.55;
  transform: translate3d(var(--px, 0px), var(--py, 0px), 0);
  will-change: transform;
}

.orb-1 {
  left: -140px;
  top: -150px;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.65), rgba(187, 247, 208, 0.22), rgba(255, 255, 255, 0));
  animation: orbFloat1 13s ease-in-out infinite;
}

.orb-2 {
  right: -190px;
  top: 120px;
  width: 520px;
  height: 520px;
  opacity: 0.48;
  background: radial-gradient(circle at 40% 35%, rgba(16, 185, 129, 0.75), rgba(6, 182, 212, 0.18), rgba(16, 185, 129, 0));
  animation: orbFloat2 17s ease-in-out infinite;
}

.orb-3 {
  left: 18%;
  bottom: -220px;
  width: 560px;
  height: 560px;
  opacity: 0.4;
  background: radial-gradient(circle at 40% 35%, rgba(2, 122, 74, 0.75), rgba(16, 185, 129, 0.22), rgba(2, 122, 74, 0));
  animation: orbFloat3 19s ease-in-out infinite;
}

.login-shell {
  width: 100%;
  max-width: 1050px;
  min-height: 520px;
  background-color: #ffffff;
  border-radius: 26px;
  box-shadow: 0 22px 55px rgba(2, 44, 34, 0.42);
  display: grid;
  grid-template-columns: 1.1fr 0.95fr;
  overflow: hidden;
  position: relative;
  z-index: 1;
  border: 1px solid rgba(255, 255, 255, 0.28);
  transform-style: preserve-3d;
  transform: translate3d(0, 10px, 0) scale(0.99) rotateX(var(--rx, 0deg)) rotateY(var(--ry, 0deg));
  opacity: 0;
  animation: shellIn 520ms cubic-bezier(.2,.9,.2,1) 40ms forwards;
}

.login-shell::before {
  content: "";
  position: absolute;
  inset: -1px;
  border-radius: inherit;
  padding: 1px;
  background: linear-gradient(135deg, rgba(34, 197, 94, 0.9), rgba(16, 185, 129, 0.8), rgba(45, 212, 191, 0.7));
  -webkit-mask:
    linear-gradient(#000 0 0) content-box,
    linear-gradient(#000 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0.65;
  pointer-events: none;
  animation: borderGlow 4.2s ease-in-out infinite;
}

.login-hero {
  position: relative;
  padding: 32px 40px;
  background: linear-gradient(135deg, #027a4a, #06b981);
  color: #ecfdf5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-hero::before {
  content: "";
  position: absolute;
  inset: -40%;
  background:
    radial-gradient(circle at 0% 0%, rgba(45, 212, 191, 0.35), transparent 55%),
    radial-gradient(circle at 100% 100%, rgba(34, 197, 94, 0.35), transparent 55%),
    linear-gradient(120deg, rgba(16, 185, 129, 0.25) 0%, rgba(45, 212, 191, 0) 55%);
  mix-blend-mode: screen;
  opacity: 0.85;
  pointer-events: none;
  transform: translate3d(calc(var(--px, 0px) * 0.12), calc(var(--py, 0px) * 0.12), 0);
  animation: heroSweep 12s ease-in-out infinite alternate;
}

.hero-main {
  position: relative;
  z-index: 1;
  max-width: 320px;
  text-align: center;
  transform: translateY(12px);
  opacity: 0;
  animation: fadeUp 520ms cubic-bezier(.2,.9,.2,1) 160ms forwards;
}

.hero-icon {
  width: 80px;
  height: 80px;
  border-radius: 24px;
  background: rgba(5, 150, 105, 0.95);
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  box-shadow: 0 16px 30px rgba(4, 120, 87, 0.7);
  animation: iconFloat 6.5s ease-in-out infinite;
}

.hero-icon span {
  font-size: 34px;
  font-weight: 700;
  letter-spacing: 0.2em;
}

.hero-title {
  font-size: 28px;
  font-weight: 700;
  line-height: 1.3;
  margin: 0 0 4px;
}

.hero-subtitle {
  font-size: 14px;
  opacity: 0.9;
  margin: 0 0 24px;
}

.hero-feature-list {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 999px;
  background: rgba(15, 23, 42, 0.16);
  font-size: 13px;
  justify-content: flex-start;
  opacity: 0;
  transform: translateY(8px);
  animation: fadeUp 520ms cubic-bezier(.2,.9,.2,1) 240ms forwards;
}

.feature-item:nth-child(2) {
  animation-delay: 320ms;
}

.feature-item:nth-child(3) {
  animation-delay: 400ms;
}

.feature-dot {
  width: 8px;
  height: 8px;
  border-radius: 999px;
  background: #bbf7d0;
  box-shadow: 0 0 0 0 rgba(187, 247, 208, 0.55);
  animation: dotPulse 2.6s ease-in-out infinite;
}

.login-panel {
  padding: 30px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  transform: translateY(12px);
  opacity: 0;
  animation: fadeUp 520ms cubic-bezier(.2,.9,.2,1) 220ms forwards;
  position: relative;
  border-radius: 26px;
  background:
    radial-gradient(circle at 0% 0%, rgba(240, 253, 250, 0.9), rgba(240, 253, 250, 0.96)),
    linear-gradient(145deg, rgba(255, 255, 255, 0.95), rgba(236, 252, 203, 0.92));
  box-shadow:
    0 22px 45px rgba(15, 23, 42, 0.18),
    0 0 0 1px rgba(255, 255, 255, 0.9);
  transform-style: preserve-3d;
}

.login-panel::before {
  content: "";
  position: absolute;
  inset: 0;
  border-radius: inherit;
  background:
    linear-gradient(135deg, rgba(16, 185, 129, 0.08), rgba(56, 189, 248, 0.0)),
    radial-gradient(circle at 100% 0%, rgba(52, 211, 153, 0.18), transparent 60%);
  mix-blend-mode: soft-light;
  pointer-events: none;
  opacity: 0.9;
}

.panel-header {
  margin-bottom: 16px;
}

.logo-row {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 18px;
}

.logo {
  width: 46px;
  height: 46px;
  border-radius: 14px;
  background: #eef2ff;
  padding: 6px;
  box-sizing: border-box;
  object-fit: contain;
}

.logo-text {
  display: flex;
  flex-direction: column;
}

.system-name {
  font-size: 16px;
  font-weight: 600;
  color: #111827;
}

.system-subname {
  font-size: 11px;
  color: #6b7280;
  text-transform: uppercase;
  letter-spacing: 0.06em;
}

.login-title {
  font-size: 22px;
  font-weight: 600;
  color: #111827;
  margin: 4px 0 6px;
}

.login-subtitle {
  font-size: 13px;
  color: #6b7280;
  margin: 0;
}

.login-form {
  margin-top: 12px;
  padding: 12px 4px 6px;
  border-radius: 18px;
  background: radial-gradient(circle at 0 0, rgba(240, 249, 255, 0.9), rgba(240, 253, 250, 0.97));
  box-shadow:
    0 18px 35px rgba(15, 23, 42, 0.09),
    0 0 0 1px rgba(209, 250, 229, 0.8);
}

.form-input :deep(.el-input__wrapper),
.form-input :deep(.el-select__wrapper) {
  border-radius: 10px;
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.85),
    0 0 0 1px rgba(226, 232, 240, 0.9),
    0 10px 18px rgba(15, 23, 42, 0.06);
  border: 1px solid rgba(229, 231, 235, 0.9);
  background-color: rgba(255, 255, 255, 0.98);
  transition: all 0.18s ease;
}

.form-input :deep(.el-input__wrapper.is-focus),
.form-input :deep(.el-select__wrapper.is-focused),
.form-input :deep(.el-input__wrapper:hover),
.form-input :deep(.el-select__wrapper:hover) {
  border-color: #4f46e5;
  box-shadow:
    inset 0 1px 0 rgba(255, 255, 255, 0.95),
    0 0 0 1px rgba(79, 70, 229, 0.36),
    0 14px 28px rgba(79, 70, 229, 0.28);
  transform: translateY(-1px) translateZ(8px) scale(1.01);
}

.form-extra-row {
  margin: 4px 0 16px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 13px;
  color: #6b7280;
}

.remember :deep(.el-checkbox__label) {
  font-size: 13px;
}

.forget-link {
  font-size: 13px;
  color: #9ca3af;
}

.forget-link:hover {
  color: #4f46e5;
}

.login-button {
  width: 100%;
  border-radius: 999px;
  padding: 12px 0;
  font-size: 15px;
  font-weight: 500;
  background: linear-gradient(135deg, #059669, #10b981);
  border: none;
  box-shadow: 0 12px 24px rgba(4, 120, 87, 0.4);
  margin-top: 4px;
  position: relative;
  overflow: hidden;
  transition: transform 0.18s ease, box-shadow 0.18s ease, filter 0.18s ease;
}

.login-button::before {
  content: "";
  position: absolute;
  inset: -40% -60%;
  background: linear-gradient(120deg, rgba(255, 255, 255, 0) 20%, rgba(255, 255, 255, 0.35) 45%, rgba(255, 255, 255, 0) 70%);
  transform: translateX(-30%) rotate(8deg);
  opacity: 0;
  transition: opacity 0.18s ease;
}

.login-button:hover {
  transform: translateY(-2px) translateZ(10px) scale(1.01);
  box-shadow:
    0 22px 40px rgba(4, 120, 87, 0.6),
    0 0 0 1px rgba(22, 163, 74, 0.6);
  filter: saturate(1.06);
}

.login-button:hover::before {
  opacity: 1;
  animation: sheen 1.1s ease-out infinite;
}

.login-button:active {
  transform: translateY(0px) scale(0.99);
  box-shadow: 0 10px 18px rgba(4, 120, 87, 0.42);
}

.login-footer {
  margin-top: 18px;
  font-size: 12px;
  color: #9ca3af;
  text-align: center;
}

.login-footer span {
  margin-right: 4px;
}

/* 响应式布局 */
@media (max-width: 900px) {
  .login-shell {
    max-width: 540px;
    grid-template-columns: 1fr;
  }

  .login-hero {
    display: none;
  }

  .login-panel {
    padding: 26px 22px 24px;
  }
}

@media (max-width: 480px) {
  .login-page {
    padding: 12px;
  }

  .system-name {
    font-size: 15px;
  }

  .login-title {
    font-size: 20px;
  }
}

@keyframes shellIn {
  from { opacity: 0; transform: translate3d(0, 18px, 0) scale(0.985); }
  to { opacity: 1; transform: translate3d(0, 0, 0) scale(1); }
}

@keyframes fadeUp {
  from { opacity: 0; transform: translateY(12px); }
  to { opacity: 1; transform: translateY(0px); }
}

@keyframes bgShift {
  0% { transform: translate3d(var(--px, 0px), var(--py, 0px), 0) translate(0, 0) scale(1.04); filter: saturate(1.02); }
  50% { transform: translate3d(var(--px, 0px), var(--py, 0px), 0) translate(-18px, 14px) scale(1.08); filter: saturate(1.06); }
  100% { transform: translate3d(var(--px, 0px), var(--py, 0px), 0) translate(12px, -10px) scale(1.05); filter: saturate(1.03); }
}

@keyframes gridDrift {
  0% { background-position: 0 0, 0 0; }
  100% { background-position: 300px 0, 0 300px; }
}

@keyframes borderGlow {
  0% { opacity: 0.3; box-shadow: 0 0 0 rgba(16, 185, 129, 0.0); }
  50% { opacity: 0.8; box-shadow: 0 0 32px rgba(16, 185, 129, 0.35); }
  100% { opacity: 0.45; box-shadow: 0 0 8px rgba(16, 185, 129, 0.22); }
}

@keyframes heroSweep {
  0% { transform: translate3d(-12px, 6px, 0) scale(1.02); opacity: 0.75; }
  50% { transform: translate3d(10px, -10px, 0) scale(1.05); opacity: 0.95; }
  100% { transform: translate3d(-6px, 4px, 0) scale(1.03); opacity: 0.8; }
}

@keyframes orbFloat1 {
  0% { transform: translate3d(var(--px, 0px), var(--py, 0px), 0) translate(0, 0) scale(1); }
  50% { transform: translate3d(var(--px, 0px), var(--py, 0px), 0) translate(24px, 20px) scale(1.05); }
  100% { transform: translate3d(var(--px, 0px), var(--py, 0px), 0) translate(8px, -6px) scale(1.02); }
}

@keyframes orbFloat2 {
  0% { transform: translate3d(var(--px, 0px), var(--py, 0px), 0) translate(0, 0) scale(1.02); }
  50% { transform: translate3d(var(--px, 0px), var(--py, 0px), 0) translate(-26px, 20px) scale(1.07); }
  100% { transform: translate3d(var(--px, 0px), var(--py, 0px), 0) translate(-12px, -8px) scale(1.03); }
}

@keyframes orbFloat3 {
  0% { transform: translate3d(var(--px, 0px), var(--py, 0px), 0) translate(0, 0) scale(1.01); }
  50% { transform: translate3d(var(--px, 0px), var(--py, 0px), 0) translate(18px, -22px) scale(1.05); }
  100% { transform: translate3d(var(--px, 0px), var(--py, 0px), 0) translate(-10px, -10px) scale(1.03); }
}

@keyframes iconFloat {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-6px); }
}

@keyframes dotPulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(187, 247, 208, 0.0); }
  50% { box-shadow: 0 0 0 8px rgba(187, 247, 208, 0.12); }
}

@keyframes sheen {
  0% { transform: translateX(-40%) rotate(8deg); }
  100% { transform: translateX(40%) rotate(8deg); }
}

@media (prefers-reduced-motion: reduce) {
  .login-page::before,
  .bg-grid,
  .bg-orb,
  .login-shell,
  .hero-main,
  .login-panel,
  .feature-item,
  .hero-icon,
  .feature-dot,
  .login-button:hover::before {
    animation: none !important;
    transition: none !important;
  }
}
</style>
