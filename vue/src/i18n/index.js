import { createI18n } from 'vue-i18n'
import zhCN from '../locales/zh-CN.json'
import ug from '../locales/ug.json'
import bo from '../locales/bo.json'
import mn from '../locales/mn.json'

const messages = {
  'zh-CN': zhCN,
  'ug': ug,
  'bo': bo,
  'mn': mn
}

const i18n = createI18n({
  legacy: false,
  locale: localStorage.getItem('locale') || 'zh-CN',
  fallbackLocale: 'zh-CN',
  messages
})

export default i18n
