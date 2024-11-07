Vue.component("header-component", {
  data() {
    return {
      loginYN: false,
      userName: "",
      profileImageUrl: "",
    };
  },
  template: `
    <header class="fixed-header">
      <div class="container-fluid d-flex align-items-center p-3">
        <!-- 로고 영역 -->
        <a class="navbar-brand fs-3 text-warning fw-bold d-flex align-items-center" href="/">
          <img src="/assets/mainIcon.png" alt="SmartReserve Logo" class="me-2" width="30" height="30" />
          SmartReserve
        </a>
        
        <!-- 검색창 -->
        <input
          type="text"
          class="form-control ms-3"
          placeholder="검색으로 식당을 찾아봅시다."
          aria-label="Search"
          style="width: 300px; cursor: pointer;"
          @click="goToSearch"
          readonly
        />

        <!-- 로그인 상태에 따른 UI 표시 -->
        <div v-if="loginYN" class="d-flex align-items-center ms-auto">
          <!-- 사용자 메시지 -->
          <img :src="profileImageUrl" alt="프로필 이미지" class="rounded-circle me-2" width="40" height="40" />
          <span class="me-3">{{ userName }}님 환영합니다!</span>
          
          <!-- 메뉴 -->
          <div class="dropdown">
            <button
              class="btn btn dropdown-toggle"
              type="button"
              id="dropdownMenuButton"
              data-bs-toggle="dropdown"
              aria-expanded="false"
            >
              <i class="fas fa-bars"></i>
            </button>
            <ul class="dropdown-menu dropdown-menu-end" aria-labelledby="dropdownMenuButton">
              <li><a class="dropdown-item" href="#">마이페이지</a></li>
              <li><a class="dropdown-item" href="reserve">예약 내역</a></li>
              <li><a class="dropdown-item" href="review">리뷰보기</a></li>
              <li class="dropdown-item" role="button" @click="logout">
                로그아웃
              </li>
            </ul>
          </div>
        </div>

        <!-- 비로그인 상태일 때 표시 -->
        <div v-else class="logNBotton">
          <button class="btn btn-outline-warning m-2" type="button" @click="goToLogin">로그인</button>
          <button class="btn btn-outline-warning m-2" type="button" @click="goToRegister">회원가입</button>
        </div>
      </div>
    </header>
  `,
  methods: {
    goToSearch() {
      window.location.href = "search";
    },
    goToLogin() {
      window.location.href = "/view/user/userLogin";
    },
    goToRegister() {
      window.location.href = "/view/user/userRegister";
    },
    checkLoginStatus() {
      axios
        .get("/api/status")
        .then((response) => {
          if (response.status === 200) {
            this.loginYN = true;
            this.response = response.data.data;
            this.userName = response.data.data.name;
            this.profileImageUrl = response.data.data.profileImageUrl;
            console.log(response);
          }
        })
        .catch((error) => {
          if (error.response && error.response.status === 401) {
            this.loginYN = false;
          }
        });
    },
    async logout() {
      const response = await axios.post("/api/logout");
      if (response.status === 200) {
        location.reload();
      } else {
        alert("로그아웃 실패");
      }
    },
  },
  mounted() {
    this.checkLoginStatus();
  },
});
