new Vue({
  el: "#app",
  data: {
      id: '',
      password: '',
      loginGroup:99,
      idError: '',
      loginYN: false
  },
  created: function() {
      this.checkLogin();
  },
  methods: {
      checkLogin: function() {
          axios.get('/api/status')
          .then(response => {
            location.href='/view/admin/srAdminIndex';
          }).catch(error => {
              if(error.status == 401) {
                  console.log("로그인 정보가 없습니다.");
              }
              this.loginYN = true;
          })
      },
      async login() {
        const formData = new FormData();
        formData.append('id', this.id);
        formData.append('password', this.password);
        formData.append('loginGroup', this.loginGroup);
        try {
          const response = await axios.post('/api/login', formData);
          if (response.status === 200) {
            location.href='/view/admin/srAdminIndex';
          }
        } catch (error) {
          if(error.status === 404) {
            this.idError = '서비스가 응답하지 않습니다. 잠시 후 다시 시도해주세요';
          }
            console.error('API 요청 실패:', error);
            this.idError=error.response.data.message;
        }
      }
  }
});