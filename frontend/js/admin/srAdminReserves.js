new Vue({
  el: '#app',
  data: {
    updateFlag: true,
    userModal: null,
    currentPage: 1, // 현재 페이지
    pageSize: 10, // 페이지당 표시 갯수
    reserveCount: 0, // 검색 총 결과수
    totalPages: 0, // 총 페이지
    reserveStatusFilters:[],
    reserveLists: null,
    reserveInfo:{},
    selectedFilter:{
      searchQuery:'',
      statusCodeId:[]
    },
    checkedReserveIds:[],
    selectedFilterId:'가게명'
  },
  created: function() {
    this.fetchFilters();
    this.fetchReserveLists();
  },
  watch: {
    selectedFilter: 'fetchReserveLists',
    selectedFilterId: 'fetchReserveLists',
    selectedFilterQuery: 'fetchReserveLists'
  },
  methods: {
    fetchFilters: async function() {
      await axios.get('/api/admin/reserveStatusFilters')
      .then(response => {
        this.reserveStatusFilters = response.data.data;
      })
      .catch(error => {
        alert('예약 필터링 정보를 가져오는 도중 오류가 발생했습니다.');
        console.error(error);
      });
    },
    fetchReserveLists: async function() {
      let params = {}
      if(this.selectedFilterId === '가게명') {
        params.storeName = this.selectedFilter.searchQuery;
      }else {
        params.userName = this.selectedFilter.searchQuery;
      }
      params.statusCodeId=this.selectedFilter.statusCodeId.join(',');
      await axios.get('/api/admin/reserve', {params})
      .then(response => {
        console.log(response.data.data);
        this.reserveLists = response.data.data;
        this.reserveCount = this.reserveLists.length;
        this.totalStoreCount = this.reserveCount;
        this.totalPages = Math.ceil(this.reserveCount / this.pageSize);
      })
      .catch(error => {
        if(error.status === 401) {
          alert('접근 권한이 없습니다.');
        }else {
          alert('예약 정보를 가져오는 도중 오류가 발생했습니다.');
          console.error(error);
        }
      })
    },
    changePage: function(page) {
      this.currentPage = page;
    },
    menuSummary(menuInfo) {
      return menuInfo.length>1?`${menuInfo[0].menuName} 외 ${menuInfo.length - 1}건` : `${menuInfo[0].menuName}`;
    },
    badge(reserveInfo) {
      let badge = 'text-bg-primary'
      if(reserveInfo.statusCodeId == 1) {
        badge='text-bg-secondary'
      } else if(reserveInfo.statusCodeId == 2) {
        badge='text-bg-primary'
      } else if(reserveInfo.statusCodeId == 3) {
        badge='text-bg-danger'
      } else if(reserveInfo.statusCodeId == 4) {
        badge='text-bg-success'
      } else if(reserveInfo.statusCodeId == 5) {
        badge='text-bg-info'
      }
      return badge;
    },
    dateFormat(date) {
      let day = ['일', '월', '화', '수', '목', '금', '토'];

      let dateFormat = new Date(date);
      let timeFormat = '';
      if(dateFormat.getHours() >= 12) {
        timeFormat = `오후 ${dateFormat.getHours()==12?12:dateFormat.getHours()-12}시 ${dateFormat.getMinutes()}분`;
      }else {
        timeFormat = `오전 ${dateFormat.getHours()==12?12:dateFormat.getHours()}시 ${dateFormat.getMinutes()}분`;
      }
      return `${dateFormat.getFullYear()}.${dateFormat.getMonth()+1}.${dateFormat.getDate()} (${day[dateFormat.getDay()]}) ${timeFormat}`
    },
    updateStatus: async function(statusCodeId) {
      if(this.checkedReserveIds.length > 0) {
        const formData = new FormData();
        formData.append('reserveId', this.checkedReserveIds);
        formData.append('statusCodeId', statusCodeId);
        await axios.patch('/api/admin/reserve', formData)
        .then(response => {
          alert(response.data.data);
          this.fetchReserveLists();
        })
        .catch(error => {
          alert('오류발생');
          console.error(error);
        })
      }else {
        alert('수정할 매장을 하나 이상 선택하세요.');
      }
    }
  },
  computed: {
    paginatedReserves() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.reserveLists.slice(start, end);
    },
  }
})