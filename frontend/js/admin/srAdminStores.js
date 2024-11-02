new Vue({
  el: '#app', // Vue 인스턴스를 '#app'에 바인딩
  data: {
    updateFlag: true,
    message: '',scheduleMessage:'',
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
    rotations: [],
    showBlock: false,
    storeInfo: {},
    storeModal: null,
    updateStoreData: {
      storeId:0,
      ownerId:0,
      rotationId:0,
      sidoCode:'',
      sidoCodeId:0,
      storeName:'',
      address:'',
      tel:'',
      description:'',
      directionGuide:'',
      maxPeople:0,
      maxPeoplePerReserve:0,
      locationLatX:0,
      locationLonY:0
    },
    selectedCategories: [],
    selectedFacilities: [],
    geocoder : ''
  },
  created: function() {
    this.fetchStores();
    this.fetchFilters();
  },
  mounted() {
    this.storeModal = new bootstrap.Modal(document.getElementById("storeModal"));
    this.geocoder = new kakao.maps.services.Geocoder();
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
        this.rotations = response.data.data.rotations;
      })
      .catch(error => {
        console.error("Error fetching stores filters:", error);
      });
    },
    openModalForUpdate(storeId) {
      axios.get('/api/admin/store/'+storeId)
      .then(response => {
        this.storeInfo = response.data.data;
        this.selectedCategories = this.storeInfo.storeCategories.map(storeCategory => storeCategory.categoryCodeId);
        this.selectedFacilities = this.storeInfo.facilities.map(facility => facility.facilityCode);
        this.updateStoreData = {};
        this.updateFlag = true;
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
          this.geocoder.addressSearch(data.address, (result, status) => {
            if (status === kakao.maps.services.Status.OK) {
              this.storeInfo.locationLatX = parseFloat(result[0].x).toFixed(7);
              this.storeInfo.locationLonY = parseFloat(result[0].y).toFixed(7);
            }
          })
        }
      }).open();
    },
    checkReservePeople() {
      if(this.storeInfo.maxPeoplePerReserve > this.storeInfo.maxPeople) {
        this.message = '팀당 최대 인원은 예약 시간대 최대 인원보다 많을 수 없습니다.';
        updateFlag = false;
      }else {
        this.message = '';
        updateFlag = true;
      }
    },
    validateTime(index) {
      const day = this.storeInfo.storeSchedules[index];
      console.log(day.open, day.close);
      if (day.open >= day.close) {
        this.scheduleMessage = '오픈시간은 마감시간보다 빨라야 합니다.';
        this.updateFlag = false;
      } else {
        this.scheduleMessage = '';
        this.updateFlag = true;
      }
    },
    async submitUpdate() {
      try {
        this.spinnerShow(true);
        this.updateStoreData.storeId = this.storeInfo.storeId;
        this.updateStoreData.ownerId = this.storeInfo.ownerId;
        this.updateStoreData.rotationId = this.storeInfo.rotationId;
        this.updateStoreData.sidoCode = this.storeInfo.sidoCode;
        this.updateStoreData.sidoCodeId = this.storeInfo.sidoCodeId;
        this.updateStoreData.storeName = this.storeInfo.storeName;
        this.updateStoreData.address = this.storeInfo.address;
        this.updateStoreData.tel = this.storeInfo.tel;
        this.updateStoreData.description = this.storeInfo.description;
        this.updateStoreData.directionGuide = this.storeInfo.directionGuide;
        this.updateStoreData.maxPeople = this.storeInfo.maxPeople;
        this.updateStoreData.maxPeoplePerReserve = this.storeInfo.maxPeoplePerReserve;
        this.updateStoreData.locationLatX = this.storeInfo.locationLatX;
        this.updateStoreData.locationLonY = this.storeInfo.locationLonY;
        const formData = new FormData();
        formData.append('storeDTO', new Blob([JSON.stringify(this.updateStoreData)], { type: 'application/json' }));
        formData.append('storeCategoryDTO', new Blob([JSON.stringify(this.selectedCategories)], { type: 'application/json' }));
        formData.append('facilityDTO', new Blob([JSON.stringify(this.selectedFacilities)], { type: 'application/json' }));
        formData.append('scheduleDTO', new Blob([JSON.stringify(this.storeInfo.storeSchedules)], { type: 'application/json' }));
        const response = await axios.put(`/api/owner/store/${this.updateStoreData.storeId}`, formData, {
          headers: { 'Content-Type': 'multipart/form-data' }
        });
        console.log(response);
      } catch(error) {
        if(error.status === 404) {
          alert('서버가 원활하지 않습니다. 다시 시도해주세요');
        }else {
          alert('처리중 오류가 발생했습니다.');
        }
        console.error(error);
      } finally {
        this.spinnerShow(false);
      }
    },
    spinnerShow(bool) {
      bool ? $('#spinner').addClass('show') : $('#spinner').removeClass('show');
    }
  },
  computed: {
    paginatedStores() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.stores.slice(start, end);
    },
    isCheckedCategory(categoryId) {
      return this.storeInfo.storeCategories.some(category => category.categoryCodeId == categoryId);
    }
  }
});