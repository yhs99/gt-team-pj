new Vue({
  el: '#app', // Vue 인스턴스를 '#app'에 바인딩
  data: {
      userName:"",
      profileImageUrl:"",
      loginYN:false
  },
  created: function() {
      this.fetchUserData(); // Vue 인스턴스 생성 시 데이터 요청
  },
  methods: {
    fetchUserData: function() {
        axios.get('/api/status') // 서버에 /api/status 요청
            .then(response => {
            if(response.data.data.loginType == 'user' && response.status === 200) {
                this.userName = response.data.data.name;
                this.profileImageUrl = response.data.data.profileImageUrl;
                this.loginYN=true;
            }else if(response.data.data.loginType == 'store' && response.status === 200) {
                location.href = '/view/owner/index';
            }
            
            })
            .catch(error => {
                console.log("로그인 정보 없음");
            });
    }
  }
})
