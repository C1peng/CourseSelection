<template>
  <div class="ai-chat-container">
    <div v-if="!isOpen" class="chat-float-btn" @click="openChat">
      <el-icon><ChatDotRound /></el-icon>
      <span class="btn-text">AI 助手</span>
    </div>

    <div v-else class="chat-window">
      <div class="chat-header">
        <div class="header-title">
          <el-icon><ChatDotRound /></el-icon>
          <span>AI 智能助手</span>
        </div>
        <div class="header-actions">
          <el-icon @click="minimize"><Minus /></el-icon>
          <el-icon @click="closeChat"><Close /></el-icon>
        </div>
      </div>

      <div class="chat-messages" ref="messageList">
        <div v-if="messages.length === 0" class="empty-hint">
          <p>👋 你好，我是 AI 智能助手</p>
          <p>可以问我关于选课、学分、课程安排等问题</p>
        </div>
        <div
          v-for="(msg, index) in messages"
          :key="index"
          :class="['message-item', msg.role === 'user' ? 'user-message' : 'ai-message']"
        >
          <div class="message-avatar">
            <el-icon v-if="msg.role === 'user'"><User /></el-icon>
            <el-icon v-else><ChatDotRound /></el-icon>
          </div>
          <div class="message-content">{{ msg.content }}</div>
        </div>
        <div v-if="loading" class="message-item ai-message">
          <div class="message-avatar">
            <el-icon><ChatDotRound /></el-icon>
          </div>
          <div class="message-content loading">
            <span>AI 正在思考...</span>
          </div>
        </div>
      </div>

      <div class="chat-input">
        <el-input
          v-model="inputMessage"
          type="textarea"
          :rows="2"
          placeholder="请输入您的问题..."
          @keydown.enter.exact.prevent="sendMessage"
          resize="none"
        />
        <el-button type="primary" :loading="loading" @click="sendMessage">
          <el-icon><Promotion /></el-icon>
        </el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { ChatDotRound, User, Minus, Close, Promotion } from '@element-plus/icons-vue'
import request from '@/utils/request.js'
import { ElMessage } from 'element-plus'

const isOpen = ref(false)
const messages = ref([])
const inputMessage = ref('')
const loading = ref(false)
const messageList = ref(null)

const openChat = () => {
  isOpen.value = true
}

const minimize = () => {
  isOpen.value = false
}

const closeChat = () => {
  isOpen.value = false
}

const scrollToBottom = () => {
  nextTick(() => {
    if (messageList.value) {
      messageList.value.scrollTop = messageList.value.scrollHeight
    }
  })
}

const sendMessage = async () => {
  const text = inputMessage.value.trim()
  if (!text) return
  if (loading.value) return

  messages.value.push({ role: 'user', content: text })
  inputMessage.value = ''
  loading.value = true
  scrollToBottom()

  try {
    const res = await request.post('/chat/ask', { message: text })
    if (res.code === '200') {
      messages.value.push({ role: 'assistant', content: res.data })
    } else {
      messages.value.push({ role: 'assistant', content: res.msg || '抱歉，服务暂时不可用' })
    }
  } catch (error) {
    messages.value.push({ role: 'assistant', content: '网络连接失败，请稍后重试' })
  } finally {
    loading.value = false
    scrollToBottom()
  }
}
</script>

<style scoped>
.ai-chat-container {
  position: fixed;
  z-index: 9999;
}

.chat-float-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 60px;
  height: 60px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 50%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  color: #fff;
}

.chat-float-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 24px rgba(102, 126, 234, 0.5);
}

.chat-float-btn .el-icon {
  font-size: 24px;
}

.chat-float-btn .btn-text {
  font-size: 10px;
  margin-top: 2px;
}

.chat-window {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 380px;
  height: 520px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.15);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.header-actions .el-icon {
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: background 0.2s;
}

.header-actions .el-icon:hover {
  background: rgba(255, 255, 255, 0.2);
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  background: #f5f7fa;
}

.empty-hint {
  text-align: center;
  color: #909399;
  padding: 40px 20px;
  font-size: 14px;
  line-height: 1.8;
}

.message-item {
  display: flex;
  gap: 10px;
  margin-bottom: 16px;
}

.user-message {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.user-message .message-avatar {
  background: #409eff;
  color: #fff;
}

.ai-message .message-avatar {
  background: #67c23a;
  color: #fff;
}

.message-content {
  max-width: 70%;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.user-message .message-content {
  background: #409eff;
  color: #fff;
  border-bottom-right-radius: 4px;
}

.ai-message .message-content {
  background: #fff;
  color: #333;
  border-bottom-left-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.message-content.loading {
  color: #909399;
  font-style: italic;
}

.chat-input {
  display: flex;
  gap: 10px;
  padding: 12px 16px;
  background: #fff;
  border-top: 1px solid #ebeef5;
}

.chat-input .el-textarea {
  flex: 1;
}

.chat-input .el-button {
  align-self: flex-end;
}
</style>
