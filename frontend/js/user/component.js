Vue.component('header-navbar', {
  data: function() {
    return {
      loginYN : false,
      userName : '',
      profileImageUrl:'',
    }
  },
  template :
  `
  <div class="container p-3 mb-3">
      <nav class="navbar navbar-expand-lg bg-body-tertiary">
        <div class="container-fluid">
          <a class="navbar-brand fs-3 text-warning fw-bold" href="/"><img src="/assets/mainIcon.png"> SmartReserve</a>
          <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
          </button>
          <div class="collapse navbar-collapse" id="navbarSupportedContent">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
            </ul>
            <div v-if="loginYN==true">
              <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item dropdown">
                  <img :src="profileImageUrl" width="40px" height="40px" class="dropdown-toggle rounded-circle border"
                  href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                  <ul class="dropdown-menu text-center">
                    <li class="dropdown-item" role="button" @click="logout">
                      <p>로그아웃</p>
                    </li>
                    <a href="/view/user/myPage">
                      <li class="dropdown-item" role="button">
                        내 정보
                      </li>
                    </a>
                  </ul>
                </li>
                <li class="nav-item">
                    {{ userName }}님 환영합니다!
                </li>
            </ul>
            </div>
            <div v-else>
              <button class="btn btn-outline-warning m-2" type="button" onclick="location.href = '/view/user/userLogin'">로그인</button>
              <button class="btn btn-outline-warning m-2" type="button" onclick="location.href = '/view/user/register'">회원가입</button>
            </div>
          </div>
        </div>
      </nav>
    </div>
  `,
  created: function() {
    this.fetchUserData();
  },
  methods: {
    fetchUserData: function() {
      axios.get('/api/status')
      .then(response => {
        if(response.status === 200 && response.data.data.loginType=='user') {
          this.loginYN=true;
          this.profileImageUrl = response.data.data.profileImageUrl;
          this.userName = response.data.data.name;
        }else if(response.status === 200 && response.data.data.loginType=='store'){
          location.href = '/view/owner/index'
        }else if(response.status !== 200) {
          alert(response.data.data.message);
          location.href = response.data.data.redirect;
        }
      })
      .catch(error => {
        this.loginYN = false;
        this.profileImageUrl = '';
        this.userName = '';
      })
    },
    logout: function() {
      axios.post('/api/logout')
      .then(response => {
        if(response.status === 200) {
          location.href = '/';
        }
      })
    },
  }
});

Vue.component('footer-component',{
  template: `
    <div class="container-fluid bg-light">
      <div class="container">
        <div class="py-3 my-4">
          <ul class="nav justify-content-center border-bottom pb-3 mb-3">
          </ul>
          <p class="text-center text-body-secondary">문의 : tnwksqhdlf22@gmail.com</p>
          <p class="text-center text-body-secondary">© 2024 SmartReserve All rights reserved</p>
        </div>
      </div>
    </div>
  `
})
