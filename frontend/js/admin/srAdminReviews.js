new Vue({
  el: '#app',
  data: {
    checkDeleteReqOnly: false,
    currentImageIndex:0,
    userModal: null,
    currentPage: 1, // 현재 페이지
    pageSize: 10, // 페이지당 표시 갯수
    reviewCount: 0, // 검색 총 결과수
    totalPages: 0, // 총 페이지
    reviewLists: null,
    reviewImageLists: [{url:''}],
    reserveInfo:{},
    selectedFilter:{
      searchQuery:'',
      statusCodeId:[]
    },
    selectedFilterId:'가게명'
  },
  created: function() {
    this.fetchReviews();
  },
  watch: {
    selectedFilter: 'fetchReviews',
    selectedFilterId: 'fetchReviews',
    selectedFilterQuery: 'fetchReviews'
  },
  methods: {
    fetchReviews: async function() {
      const url = this.checkDeleteReqOnly?'/api/admin/deleteReview':'/api/admin/review';
      let params = {}
      if(this.selectedFilterId === '가게명' && !this.checkDeleteReqOnly) {
        params.searchBy = 'storeName';
        params.searchValue = this.selectedFilter.searchQuery;
      }else if(this.selectedFilterId === '유저명' && !this.checkDeleteReqOnly) {
        params.searchBy = 'userName';
        params.searchValue = this.selectedFilter.searchQuery;
      }
      await axios.get(url, {params})
      .then(response => {
        console.log(response.data.data);
        this.reviewLists = response.data.data.reviewData;
        this.reviewCount = response.data.data.reviewCount;
        this.totalStoreCount = this.reviewCount;
        this.totalPages = Math.ceil(this.reviewCount / this.pageSize);
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
    deleteReview: function(reviewId) { // 삭제 요청 리뷰 삭제처리
      axios.delete('/api/admin/deleteReview/'+reviewId)
      .then(response => {
        if(response.status===200) {
          alert(reviewId + "의 리뷰가 삭제처리 되었습니다.");
        }
      })
      .catch(error => {
        alert(error.response.data.message);
      })
      .finally(() => {
        this.fetchReviews();
      })
    },
    cancelDeleteReview: function(reviewId) {  // 리뷰 삭제 요청 거절
      axios.post('/api/admin/cancelDeleteReqReview/'+reviewId)
      .then(response => {
        if(response.status === 200) {
          alert(reviewId + "의 리뷰 삭제 요청이 거절처리 되었습니다.");
        }
      })
      .catch(error => {
        alert(error.response.data.message);
      })
      .finally(() => {
        this.fetchReviews();
      })
    },
    openModal(reviewLists, index) {
      this.reviewImageLists = reviewLists;
      this.currentImageIndex = index;
    },
    controlImages(command) {
      if(command==='prev' && this.currentImageIndex > 0) {
        this.currentImageIndex--;
      }else if(command==='next' && this.currentImageIndex < this.reviewImageLists.length-1) {
        this.currentImageIndex++;
      }
    }
  },
  computed: {
    paginatedReviews() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.reviewLists.slice(start, end);
    },
    currentImage() {
      return this.reviewImageLists[this.currentImageIndex].url;
    }
  }
})