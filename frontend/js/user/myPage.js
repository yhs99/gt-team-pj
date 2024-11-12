new Vue({
  el: '#app', // Vue 인스턴스를 '#app'에 바인딩
  data: {
    currentPage: 'myInfo',
    myInfoData: {
      password:''
    },
    reserveHistoryData: [],
    bookMarkData: [],
    reviewHistoryData: [],
    uploadImageFile : null,
    previewImage : '',
    errorMsg : '',
  },
  created: function() {
    this.checkLogin();
  },
  methods: {
    changeTab: function(tabName) {
      this.currentPage = tabName;
      switch (tabName) {
        case 'myInfo':
          this.getUserInfoData();
          this.errorMsg='';
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
    }
    //======================== myInfo (유저 업데이트 끝) ===============================
  },
});