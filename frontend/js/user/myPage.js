new Vue({
  el: '#app', // Vue 인스턴스를 '#app'에 바인딩
  data: {
    currentPage: 'reserveHistory',
    myInfoData: {
      password:''
    },
    reserveHistoryData: [],
    bookMarkData: [],
    reviewHistoryData: [],
    reviewHistoryCount: 0,
    uploadImageFile : null,
    previewImage : '',
    errorMsg : '',
    reviewErrorMsg : '',
    selectedReserveStatus: 'plan',
    planReserveHistoryData : [],
    completeReserveHistoryData : [],
    cancelReserveHistoryData : [],
    bookMarkListData: [],
    bookMarkListCount: 0,
    reviewImageLists: [{url:''}],currentImageIndex:0,
    updateReviewData:{},
    insertReviewData:{
      content:'',
      uploadReviewImageFiles:[],
      score:0,
      uploadedFiles:[]
    }
  },
  created: function() {
    this.checkLogin();
    this.fetchReviewData();
    this.fetchReserveHistories();
    this.fetchBookMarkData();
  },
  methods: {
    changeTab: function(tabName, data, type, storeId) {
      this.currentPage = tabName;
      this.insertReviewData.uploadedFiles = [];
      switch (tabName) {
        case 'myInfo':
          this.getUserInfoData();
          this.errorMsg='';
          break;
        case 'reserveHistory':
          this.fetchReserveHistories();
          break;
        case 'bookMark':
          this.fetchBookMarkData();
          break;
        case 'reviewHistory':
          this.fetchReviewData();
          break;
        case 'reviewInsertOrUpdate':
          this.updateReviewData = null;
          this.insertReviewData = {
            content:'',
            uploadReviewImageFiles:[],
            score:0,
            uploadedFiles:[]
          }
          if(type === 0) {
            this.insertReviewData.reserveId = data;
            this.insertReviewData.storeId = storeId;
          }else {
            if(data) {
              this.updateReviewData = data?data:null;
            }
          }
          break;
      }
    },
    checkLogin: function () {
      axios.get("/api/status")
        .then(response => {
          if(response.status !== 200) {
            alert(response.data.data.message);
            location.href = response.data.data.redirect;
          }
          this.changeTab('myInfo');
        })
        .catch((error) => {
          if (error.status == 401) {
            alert('로그인이 필요한 서비스입니다.');
            location.href = '/view/user/userLogin';
          }
        });
    },
    isActive: function(tabName) { // 탭 변경 이벤트
      return this.currentPage==tabName?'active':'';
    },
    getUserInfoData: function() { // 유저 데이터 call
      axios.get("/api/user")
      .then(response => {
        this.myInfoData = response.data.data;
        this.previewImage = this.myInfoData.profileImageUrl;
      })
      .catch(error => {
        alert('유저정보 불러오는 중 에러 발생');
        console.error(error);
      })
    },
    onFileChange(event) { // 이미지 미리보기
      const file = event.target.files[0];
      if (file && file.type.startsWith("image/")) {
        this.uploadImageFile = file;
        const reader = new FileReader();

        reader.onload = (e) => {
          this.previewImage = e.target.result;
        };
        reader.readAsDataURL(file);
      } else {
        alert("jpg, png 이미지 파일을 선택하세요.");
      }
    },
    validate: function() { // 유저 정보 수정 validation
      const phoneRegex = /^(?:\d{2,3}-\d{3,4}-\d{4})$/;
      const koreanRegex = /^[가-힣]+$/;
      if(this.myInfoData.password.length !== 0 && (this.myInfoData.password !== this.myInfoData.password_re)) {
        this.errorMsg = '비밀번호가 일치 해야합니다.';
        return false;
      }else if(this.myInfoData.password.length < 8) {
        this.errorMsg = '비밀번호는 8자리 이상이어야 합니다..';
        return false;
      }else if(this.myInfoData.mobile != null && !phoneRegex.test(this.myInfoData.mobile)) {
        this.errorMsg = '휴대폰 번호는 010-1234-5678 형식으로 입력해주세요.';
        return false;
      }else if(!koreanRegex.test(this.myInfoData.name)) {
        this.errorMsg = '이름은 한글만 입력 가능합니다.';
        return false;
      }
      return true;
    },
    updateUserInfo: async function() { // 유저 정보 업데이트
      if(this.validate()) {
        const formData = new FormData();
        formData.append("name", this.myInfoData.name!=null?this.myInfoData.name:null);
        if(this.myInfoData.password != null || this.myInfoData.password != '') {
          formData.append("password", this.myInfoData.password);
        }
        formData.append("gender", this.myInfoData.gender!=null?this.myInfoData.gender:null);
        formData.append("mobile", this.myInfoData.mobile!=null?this.myInfoData.mobile:null);
        if(this.uploadImageFile != null) {
          formData.append("imageFile", this.uploadImageFile);
        }
        await axios.patch("/api/user", formData)
        .then(response => {
          if(response.status === 200) {
            alert(response.data.data);
            location.href="/view/user/userLogin";
          }
        })
        .catch(error => {
          alert("수정중 오류가 발생했습니다.");
          console.error(error);
        })
      }
    },
    //======================== myInfo (유저 업데이트 끝) ===============================
    //======================== reserveHistory 시작 ================================
    fetchReserveHistories: async function() {
      const plans = ['plan', 'complete', 'cancel'];
      for(plan of plans) {
        await axios.get(`/api/reserve?reserveType=${plan}`)
        .then(response => {
          if(plan === 'plan') {
            this.planReserveHistoryData = response.data.data;
          }else if(plan === 'complete') {
            this.completeReserveHistoryData = response.data.data;
          }else if(plan === 'cancel') {
            this.cancelReserveHistoryData = response.data.data;
          }
        })
        .catch(error => {
          alert("예약 내역을 조회중 오류가 발생했습니다.");
          console.log(error);
        })
      }
    },
    statusCodeValue: function(codeId) {
      switch (codeId) {
        case 1:
          return `매장 승인 대기중`
        case 2:
          return `방문 예정`
        case 3:
          return `취소 완료`
        case 4:
          return `방문 완료`
        case 5:
          return `방문 완료`
      }
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
    // =================== reserveHistory 종료
    //==================== bookMark 시작
    fetchBookMarkData: async function() {
      await axios.get('/api/bookmark')
      .then(response => {
        this.bookMarkListData = response.data.data;
        this.bookMarkListCount = this.bookMarkListData.length;
      })
      .catch(error => {
        alert('즐겨찾는 식당 정보 조회중 오류가 발생했습니다.');
        console.error(error);
      })
    },
    //=================== bookMark 종료
    //=================== reviewHistory 시작
    fetchReviewData: async function() {
      await axios.get('/api/review')
      .then(response => {
        this.reviewHistoryData = response.data.data;
        this.reviewHistoryCount = this.reviewHistoryData.reviewCount;
      })
      .catch(error => {
        if(error.status )
        alert("리뷰 정보 조회중 오류가 발생했습니다.");
        console.error(error);
      })
    },
    openReviewModal(reviewImageLists, index) {
      this.reviewImageLists = reviewImageLists;
      this.currentImageIndex = index;
    },
    controlImages(command) {
      if(command==='prev' && this.currentImageIndex > 0) {
        this.currentImageIndex--;
      }else if(command==='next' && this.currentImageIndex < this.reviewImageLists.length-1) {
        this.currentImageIndex++;
      }
    },
    currentImage() {
      console.log(this.reviewImageLists[this.currentImageIndex].url)
      return this.reviewImageLists[this.currentImageIndex].url;
    },
    deleteReview(reviewId) {
      if(confirm("정말 삭제하시겠습니까? 삭제후 복구가 불가능합니다.")) {
        axios.delete(`/api/review/${reviewId}`)
        .then(response => {
          if(response.status === 200) {
            alert("삭제가 완료됐습니다.");
            this.fetchReviewData();
          }
        })
        .catch(error => {
          alert("리뷰 삭제중 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
          console.error(error);
        })
      }
    },
    //====================== reviewHistory 종료
    //====================== 리뷰수정, 작성 시작
    deleteRequestImage(reviewId, imageId, index) {
      if(confirm("이미지를 삭제하시겠습니까? 삭제 후 수정 취소시 이미지 복구가 가능합니다.")) {
        axios.get(`/api/review/${reviewId}`)
        .then(response => {
          axios.delete(`/api/review/${reviewId}/${imageId}`)
          .then(response => {
            if(response.status === 200) {
              this.updateReviewData.reviewImages.splice(index, 1);
            }
          })
          .catch(error => {
            alert('이미지 삭제중 오류가 발생했습니다. 잠시후 다시 시도해주세요.');
            console.error(error);
          })
        })
        .catch(error => {
          alert('이미지 삭제중 오류가 발생했습니다. 잠시후 다시 시도해주세요.');
          console.error(error);
        })
      }
    },
    scoreEvent(e, v) { // 별점 선택시 별점 적용
      v===1?this.updateReviewData.score=e.target.getAttribute('score-value'):this.insertReviewData.score=e.target.getAttribute('score-value');
      document.querySelectorAll('.score').forEach(star => {
        if (star.getAttribute('score-value') <= e.target.getAttribute('score-value')) {
          star.classList.add('text-warning');
        } else {
            star.classList.remove('text-warning');
        }
      })
    },
    initScore(score) { // 별점 로드시 색깔 로드
      return score <= this.updateReviewData.score ? 'text-warning' : '';
    },
    uploadImageFiles(event) {
      const files = event.target.files;
      for (let i = 0; i < files.length; i++) {
          const file = files[i];
          
          // 이미지 미리보기 URL 생성
          const imageUrl = URL.createObjectURL(file);

          // reviewImages 배열에 추가
        this.insertReviewData.uploadReviewImageFiles.push({
            url: imageUrl,
        });
      }
      this.insertReviewData.uploadedFiles = files;
    },
    uploadImageFilesForUpdate(event) {
      const files = event.target.files;
      const newImages = [];
      for(const file of files) {
        const imageUrl = URL.createObjectURL(file);
        this.$set(this.updateReviewData.newImages, this.updateReviewData.newImages.length, imageUrl);
      }
      this.updateReviewData.uploadedFiles = files;
    },
    deleteInsertReviewImage(index, isUpdate) { // 리뷰 작성시 이미지 삭제
      if(confirm("삭제하시겠습니까?")) {
        if(isUpdate) {
          this.updateReviewData.newImages.splice(index, 1);
        }else {
          this.insertReviewData.uploadReviewImageFiles.splice(index, 1);
        }
      }
    },
    async uploadReview() {
      if(this.reviewValidate(this.insertReviewData)) {
        const formData = new FormData();
        const reviewData = {
          reserveId: this.insertReviewData.reserveId,
          storeId: this.insertReviewData.storeId,
          score: Number(this.insertReviewData.score),
          content: this.insertReviewData.content
        }
        formData.append(
          'review',
          new Blob([JSON.stringify(reviewData)], {type: 'application/json'})
        )
        if(this.insertReviewData.uploadedFiles.length > 0) {
          for(file of this.insertReviewData.uploadedFiles) {
            formData.append('file', file);
          }
        }
        await axios.post(`/api/review/`, formData)
        .then(response => {
          alert("리뷰 작성 완료");
          location.href = "/view/user/myPage";
        })
        .catch(error => {
          alert(error.response.data.message);
          console.error(error);
        })
      }
    },
    async updateReview() {
      console.log(this.updateReviewData)
      if(this.reviewValidate(this.updateReviewData)) {
        const formData = new FormData();
        const reviewData = {
          content: this.updateReviewData.content,
          score: Number(this.updateReviewData.score)
        }
        formData.append(
          'review',
          new Blob([JSON.stringify(reviewData)], {type: 'application/json'})
        )
        if(this.insertReviewData.uploadedFiles.length > 0) {
          for(file of this.insertReviewData.uploadedFiles) {
            formData.append('file', file);
          }
        }
        await axios.put(`/api/review/${this.updateReviewData.reviewId}`, formData)
        .then(response => {
          alert("리뷰 수정 완료");
          location.href = "/view/user/myPage";
        })
        .catch(error => {
          alert(error.response.data.message);
          console.error(error);
        })
      }
    },
    reviewValidate(data) {
      if(data.content == '' || data.content == null) {
        this.reviewErrorMsg = '리뷰 내용을 입력해주세요.';
        return false;
      }else if(data.score <= 0) {
        this.reviewErrorMsg = '리뷰 별점을 선택해주세요.';
        return false;
      }
      return true;
    }
  },
});