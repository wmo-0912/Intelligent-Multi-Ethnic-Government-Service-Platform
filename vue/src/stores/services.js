import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useServicesStore = defineStore('services', {
  state: () => ({
    services: ref([]),
    categories: ref([
      { id: 1, name: '全部', code: 'all', icon: 'Document' },
      { id: 2, name: '民生事务', code: 'civicAffairs', icon: 'User' },
      { id: 3, name: '营业执照', code: 'businessLicense', icon: 'Briefcase' },
      { id: 4, name: '社会保障', code: 'socialSecurity', icon: 'Folder' },
      { id: 5, name: '教育服务', code: 'education', icon: 'Reading' },
      { id: 6, name: '住房服务', code: 'housing', icon: 'House' }
    ]),
    selectedCategory: ref('all')
  }),

  actions: {
    setServices(services) {
      this.services = services
    },

    setSelectedCategory(category) {
      this.selectedCategory = category
    }
  }
})
