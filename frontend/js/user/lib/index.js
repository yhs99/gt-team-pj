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
                console.log(response);
                if(response.data.data.loginType == 'user') {
                    this.userName = response.data.data.name;
                    this.profileImageUrl = response.data.data.profileImageUrl;
                    this.loginYN=true;
                }else if(response.data.data.loginType == 'store') {
                    location.href = '/view/owner/index';
                }
              })
              .catch(error => {
                  console.error("Error fetching user data:", error);
              });
      },
        async logout() {
            const response = await axios.post('/api/logout');
            if(response.status === 200) {
                location.reload();
            }else {
                alert("로그아웃 실패");
            }
        }
  }
})
