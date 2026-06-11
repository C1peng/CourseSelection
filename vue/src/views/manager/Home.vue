<template>
  <div class="manager-page manager-home">
    <!-- 顶部欢迎区 -->
    <div class="welcome-card">
      <div>
        <div class="welcome-title">
          您好，{{ data.user?.name || '管理员' }}
        </div>
        <div class="welcome-subtitle">
          欢迎登录高校选修课学分管理系统 · 管理员工作台
        </div>
      </div>
      <div class="welcome-tag">
        <span class="dot"></span>
        <span>今日运行正常</span>
      </div>
    </div>

    <!-- 中间布局 -->
    <div class="content-grid">
      <!-- 公告 -->
      <div class="card notice-card">
        <div class="card-header">
          <div class="card-title">系统公告</div>
          <div class="card-subtitle">查看近期系统更新与通知</div>
        </div>
        <el-timeline class="notice-timeline">
          <el-timeline-item
            v-for="(item, index) in data.noticeData"
            :key="index"
            :timestamp="item.time"
          >
            {{ item.content }}
          </el-timeline-item>
        </el-timeline>
        <div v-if="!data.noticeData || data.noticeData.length === 0" class="empty-tip">
          暂无公告内容
        </div>
      </div>

      <!-- 右侧统计卡片占位（可后续接入真实数据） -->
      <div class="right-column">
        <div class="card small-card">
          <div class="small-title">今日概览</div>
          <div class="small-grid">
            <div class="small-item">
              <div class="small-value">--</div>
              <div class="small-label">新增学生</div>
            </div>
            <div class="small-item">
              <div class="small-value">--</div>
              <div class="small-label">新增教师</div>
            </div>
            <div class="small-item">
              <div class="small-value">--</div>
              <div class="small-label">开启选修</div>
            </div>
          </div>
        </div>

        <div class="card small-card">
          <div class="small-title">快捷入口</div>
          <div class="shortcut-list">
            <span class="shortcut-pill">学生管理</span>
            <span class="shortcut-pill">教师管理</span>
            <span class="shortcut-pill">课程管理</span>
            <span class="shortcut-pill">公告管理</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request.js";
import { ElMessage } from "element-plus";

const data = reactive({
  user: JSON.parse(localStorage.getItem('xm-user') || '{}'),
  noticeData: []
})

const loadNotice = () => {
  request.get('/notice/selectAll').then(res => {
    if (res.code === '200') {
      data.noticeData = res.data
    } else {
      ElMessage.error(res.msg)
    }
  })
}
loadNotice()
</script>

<style scoped>
/* 导入公共样式 */
@import "@/assets/css/layouts/page.css";
@import "@/assets/css/common/card.css";

.manager-home {
  composes: page-content--manager;
  padding: var(--space-md) var(--space-lg) var(--space-xl);
  min-height: 100%;
  box-sizing: border-box;
}

.welcome-card {
  composes: welcome-banner welcome-banner--manager;
  padding: var(--space-md) var(--space-lg);
  margin-bottom: var(--space-lg);
}

.welcome-title {
  composes: welcome-banner__title;
}

.welcome-subtitle {
  composes: welcome-banner__subtitle;
}

.welcome-tag {
  display: inline-flex;
  align-items: center;
  gap: var(--space-sm);
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  background: rgba(15, 23, 42, 0.18);
  font-size: var(--font-size-xs);
}

.welcome-tag .dot {
  width: 8px;
  height: 8px;
  border-radius: var(--radius-full);
  background: #bbf7d0;
}

.content-grid {
  composes: page-grid--2-1;
  gap: var(--space-lg);
}

.card {
  composes: card;
}

.notice-card {
  min-height: 320px;
}

.card-header {
  composes: card__header;
  margin-bottom: var(--space-sm);
}

.card-title {
  composes: card__header-text;
}

.card-subtitle {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
  margin-top: 2px;
}

.notice-timeline {
  margin-top: var(--space-sm);
  max-height: 260px;
  overflow-y: auto;
}

.empty-tip {
  margin-top: var(--space-md);
  font-size: var(--font-size-sm);
  color: var(--text-muted);
}

.right-column {
  display: flex;
  flex-direction: column;
  gap: var(--space-lg);
}

.small-card {
  composes: card--small;
}

.small-title {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  margin-bottom: var(--space-sm);
  color: var(--text-primary);
}

.small-grid {
  composes: grid grid-cols-3 gap-sm;
}

.small-item {
  background: var(--bg-gray);
  border-radius: var(--radius-md);
  padding: var(--space-sm);
  text-align: center;
}

.small-value {
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--manager-primary);
}

.small-label {
  font-size: var(--font-size-xs);
  color: var(--text-muted);
  margin-top: 2px;
}

.shortcut-list {
  composes: flex flex-wrap gap-sm;
}

.shortcut-pill {
  padding: var(--space-xs) var(--space-sm);
  border-radius: var(--radius-full);
  background: #f0fdf4;
  color: #166534;
  font-size: var(--font-size-xs);
}

/* 响应式 - 已移至 page.css */
</style>