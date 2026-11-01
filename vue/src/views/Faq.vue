<template>
  <div class="faq-container">
    <h2 class="page-title">常见问题与智能问答</h2>
    <el-tabs v-model="tab">
      <el-tab-pane label="FAQ搜索" name="faq">
        <el-form inline @submit.prevent="handleSearch" style="margin-bottom:16px">
          <el-form-item label="关键词">
            <el-input v-model="keyword" placeholder="请输入问题关键词"/>
          </el-form-item>
          <el-form-item label="语种">
            <el-select v-model="lang" style="width:120px">
              <el-option label="中文" value="zh-CN"/>
              <el-option label="维吾尔语" value="ug"/>
              <el-option label="藏语" value="bo"/>
              <el-option label="蒙古语" value="mn"/>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleSearch">搜索</el-button>
          </el-form-item>
        </el-form>
        <el-table :data="faqList" border v-loading="loading">
          <el-table-column prop="question" label="问题"/>
          <el-table-column prop="answer" label="答案"/>
          <el-table-column prop="languageType" label="语种" width="100"/>
          <el-table-column v-if="faqList.length>0 && faqList[0].status !== undefined" prop="status" label="状态" width="80"/>
        </el-table>
      </el-tab-pane>
      <el-tab-pane label="智能问答" name="bot">
        <el-form :inline="true" @submit.prevent="handleAsk" style="margin-bottom:16px">
          <el-form-item label="您的提问">
            <el-input v-model="question" placeholder="请输入您的问题" style="width:280px"/>
          </el-form-item>
          <el-form-item label="语种">
            <el-select v-model="botLang" style="width:120px">
              <el-option label="中文" value="zh-CN"/>
              <el-option label="维吾尔语" value="ug"/>
              <el-option label="藏语" value="bo"/>
              <el-option label="蒙古语" value="mn"/>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="handleAsk">提交</el-button>
          </el-form-item>
        </el-form>
        <el-card v-if="botAnswer" class="bot-answer-box" style="margin-top:20px">{{ botAnswer }}</el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>
<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'
const tab = ref('faq')
// FAQ搜索
const keyword = ref('')
const lang = ref('zh-CN')
const faqList = ref([])
const loading = ref(false)
const handleSearch = async () => {
  loading.value = true
  try {
    const res = await request.get('/faq/search', { params: { keyword: keyword.value, languageType: lang.value }})
    faqList.value = Array.isArray(res.data) ? res.data : []
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}
// 机器人问答
const question = ref('')
const botLang = ref('zh-CN')
const botAnswer = ref('')
const handleAsk = async () => {
  if (!question.value.trim()) { ElMessage.warning('请输入您的问题'); return }
  loading.value = true
  try {
    const res = await request.post('/faq/bot/ask', {
      question: question.value,
      languageType: botLang.value
    })
    botAnswer.value = res.data && res.data.answer ? res.data.answer : '暂无可用答案'
  } catch (e) {
    ElMessage.error(e.message)
  } finally {
    loading.value = false
  }
}
</script>
<style scoped>
.faq-container { max-width:900px; margin:0 auto; background:#fff; padding:24px; border-radius:8px; }
.page-title { font-size:28px; margin-bottom:24px; color:#333; }
.bot-answer-box { font-size:17px; line-height:1.8; color:#156;
  background:#f8fafb; min-height:52px; }
</style>
