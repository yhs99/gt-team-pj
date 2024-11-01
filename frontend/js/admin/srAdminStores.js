new Vue({
  el: '#app', // Vue 인스턴스를 '#app'에 바인딩
  data: {
    stores: [],
    currentPage: 1, // 현재 페이지
    pageSize: 10, // 페이지당 표시 갯수
    totalStoreCount: 0, // 검색 총 결과수
    totalPages: 0, // 총 페이지
    categoryId: [],
    sidoCodeId: [],
    searchParam: '',
    categories: [],
    facilities: [],
    sidoCodes: [],
    showBlock: false,
    storeInfo: {},
    storeModal: null,
    updateStoreData: {}
  },
  created: function() {
    this.fetchStores();
    this.fetchFilters();
  },
  mounted() {
    this.storeModal = new bootstrap.Modal(document.getElementById("storeModal"));
  },
  watch: {    
    categoryId: 'fetchStores',
    sidoCodeId: 'fetchStores',
    showBlock: 'fetchStores',
  },
  methods: {
    fetchStores: async function() {
      await axios.get('/api/searchStores', {
        params: {
          categoryId: this.categoryId.join(','),
          sidoCodeId: this.sidoCodeId.join(','),
          searchParam: this.searchParam,
          showBlock: this.showBlock?1:0
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
    openModalForUpdate(storeId) {
      axios.get('/api/admin/store/'+storeId)
      .then(response => {
        this.storeInfo = response.data.data;
        console.log(this.updateStoreData);
        this.updateStoreData = {};
        this.storeModal.show();
      })
      .catch(error => {
        console.error(error);
      });
    },
    openAddress() {
      new daum.Postcode({
        oncomplete: (data) => {
          this.storeInfo.address =data.address;
        }
      }).open();
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