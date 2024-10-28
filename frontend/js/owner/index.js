new Vue({
  el: '#app', // Vue 인스턴스를 '#app'에 바인딩
  data: {
    name:"",
    loginYN:false,
    reviews:[]
  },
  created: function() {
    this.fetchUserData(); // Vue 인스턴스 생성 시 데이터 요청
    this.fetchReviews();
  },
  methods: {
    fetchUserData: function() {
      axios.get('/api/status') // 서버에 /api/status 요청
      .then(response => {
        console.log(response);
        if(response.data.data.loginType == 'store') {
            this.name = response.data.data.name;
            this.loginYN=true;
        }else if(response.data.data.loginType == 'user') {
            location.href = '/';
        }
      })
      .catch(error => {
        location.href = '/';
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
    },
    fetchReviews: function() {
      axios.get('/api/owner/review')
      .then(response => {
        console.log(response);
        this.reviews = response.data.data.reviews
      })
      .catch(error => {
        console.log(error);
      })
    },
    deleteReq: function(reviewId) {
      axios.delete('/api/owner/review/'+reviewId)
      .then(response => {
        if(response.status===200) {
          alert(reviewId + "의 삭제 요청이 완료되었습니다.");
        }
      })
      .catch(error => {
        alert("에러발생");
        console.log(error);
      })
    },
    formatDate: function(timestamp) {
      // 밀리초 단위의 타임스탬프를 변환하여 날짜로 포맷
      const date = new Date(timestamp);
      return date.toLocaleString(); // 날짜와 시간을 로컬 형식으로 반환
    }
  }
});