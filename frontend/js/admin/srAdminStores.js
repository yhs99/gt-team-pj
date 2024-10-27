new Vue({
  el: '#app', // Vue 인스턴스를 '#app'에 바인딩
  data: {
    stores: [],
    currentPage: 1, // 현재 페이지
    pageSize: 5, // 페이지당 표시 갯수
    totalStoreCount: 0, // 검색 총 결과수
    totalPages: 0, // 총 페이지
    categoryId: [],
    sidoCodeId: [],
    searchParam: '',
    categories: [],
    facilities: [],
    sidoCodes: []
  },
  created: function() {
    this.checkAdminSession();
    this.fetchStores();
    this.fetchFilters();
  },
  watch: {    
    categoryId: 'fetchStores',
    sidoCodeId: 'fetchStores',
  },
  methods: {
    checkAdminSession: function() {
      axios.get('/api/status') // 서버에 /api/status 요청
      .then(response => {
        console.log('admin checked');
      })
      .catch(error => {
        location.href = '/';
      });
    },
    fetchStores: async function() {
      await axios.get('/api/admin/stores', {
        params: {
          categoryId: this.categoryId.join(','),
          sidoCodeId: this.sidoCodeId.join(','),
          searchParam: this.searchParam
        }
      })
      .then(response => {
        console.log(response.data.data);
        this.stores = response.data.data.storeLists;
        this.totalStoreCount = response.data.data.storeCount;
        this.totalPages = Math.ceil(this.totalStoreCount / this.pageSize);
      })
      .catch(error => {
        console.error("Error fetching stores:", error);
      });
    },
    changePage: function(page) {
      this.currentPage = page;
    },
    fetchFilters: async function() {
      await axios.get('/api/stores/storeFilters')
      .then(response => {
        this.categories = response.data.data.categories;
        this.facilities = response.data.data.facilities;
        this.sidoCodes = response.data.data.sidoCodes;
      })
      .catch(error => {
        console.error("Error fetching stores filters:", error);
      });
    },
    logoutProcess: function() {
      axios.post('/api/logout')
        .then(response => {
          console.log(response);
          if(response.status === 200) {
            location.href = '/';
          }
        }).catch(error => {
          console.error(error);
        });
    }
  },
  computed: {
    paginatedStores() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.stores.slice(start, end);
    }
  }
});