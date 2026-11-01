<template>
  <div class="consultation-container">
    <h2 class="page-title">{{ $t('nav.consultation') }}</h2>
    <el-card class="chat-box">
      <div ref="messagesContainer" class="messages">
        <div v-for="(message, index) in messages" :key="index" :class="['message', message.type]">
          <div class="message-content">{{ message.content }}</div>
          <span class="message-time">{{ message.time }}</span>
        </div>
      </div>
      <el-divider />
      <div class="input-area">
        <el-input v-model="inputMessage" type="textarea" :rows="3" :placeholder="$t('consultation.placeholder')" @keyup.ctrl.enter="handleSend" />
        <div class="actions">
          <el-button type="primary" @click="handleSend" :disabled="!inputMessage.trim()">{{ $t('consultation.send') }}</el-button>
          <el-button @click="handleClear">{{ $t('consultation.clear') }}</el-button>
        </div>
      </div>
    </el-card>
    <div class="quick-questions">
      <h3>{{ $t('consultation.quickQuestions') }}</h3>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6" v-for="question in quickQuestions" :key="question.id">
          <el-button @click="handleQuickQuestion(question)" class="question-btn">{{ question.text }}</el-button>
        </el-col>
      </el-row>
    </div>
  </div>
</template>
<script setup>
import { ref, nextTick, onMounted, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import request from '@/utils/request'
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
const messagesContainer = ref(null)
const inputMessage = ref('')
const { t } = useI18n()
const messages = ref([
  { type: 'system', content: t('consultation.welcome'), time: getCurrentTime() }
])
const quickQuestions = ref([])

async function fetchFaqQuestions() {
  try {
    const lang = userStore.locale || 'zh-CN'
    const res = await request.get('/faq/search', { params: { keyword: '', languageType: lang } })
    // 只取前4个用于快捷常见问题
    if(Array.isArray(res.data)) {
      quickQuestions.value = res.data.slice(0, 4).map((q, idx) => ({id: q.questionId || idx, text: q.question}))
    }
  } catch(e) {}
}

function getCurrentTime() {
  const now = new Date()
  return now.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
}
const handleSend = async () => {
  if (!inputMessage.value.trim()) return
  messages.value.push({ type: 'user', content: inputMessage.value, time: getCurrentTime() })
  const userQuestion = inputMessage.value
  inputMessage.value = ''
  await nextTick(); scrollToBottom()
  let reply = '...'
  try {
    // 机器人调用真实接口
    const lang = userStore.locale || 'zh-CN'
    const params = new URLSearchParams()
    params.append('question', userQuestion)
    params.append('languageType', lang)
    const res = await request.post('/faq/bot/ask', params, {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' }
    })
    reply = res.data && res.data.answer ? res.data.answer : (res.message || '暂无相关答案')
  } catch (e) {
    reply = '获取答案失败:' + (e.message || '')
  }
  messages.value.push({ type: 'system', content: reply, time: getCurrentTime() })
  nextTick(scrollToBottom)
}
const handleQuickQuestion = async (question) => {
  inputMessage.value = question.text
  await nextTick();
  await handleSend()
}
const handleClear = () => {
  messages.value = [
    { type: 'system', content: t('consultation.welcome'), time: getCurrentTime() }
  ]
}
function scrollToBottom() {
  if (messagesContainer.value) messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
}
onMounted(() => {
  fetchFaqQuestions()
  scrollToBottom()
})
watch(() => userStore.locale, () => { fetchFaqQuestions() })
</script>
<style scoped>
.consultation-container { max-width: 1000px; margin: 0 auto; }
.page-title { font-size: 28px; margin-bottom: 24px; color: #333; }
.chat-box { margin-bottom: 30px; }
.messages { height: 500px; overflow-y: auto; padding: 20px; background-color: #f8f9fa; border-radius: 4px; }
.message { margin-bottom: 20px; display: flex; flex-direction: column; }
.message.user { align-items: flex-end; }
.message.system { align-items: flex-start; }
.message-content { max-width: 70%; padding: 12px 16px; border-radius: 8px; word-wrap: break-word; }
.message.user .message-content { background-color: #409eff; color: white; }
.message.system .message-content { background-color: white; border: 1px solid #e4e7ed; color: #333; }
.message-time { font-size: 12px; color: #999; margin-top: 4px; }
.message.user .message-time { text-align: right; }
.input-area { margin-top: 20px; }
.actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 10px; }
.quick-questions { margin-top: 30px; }
.quick-questions h3 { margin-bottom: 16px; color: #333; }
.question-btn { width: 100%; margin-bottom: 10px; white-space: normal; height: auto; padding: 12px; text-align: left; }
</style>
