Vue.component("sidebar-component", {
  data() {
    return {
      loginType: "",
      store: [],
      storeName: "",
      activeMenu: window.location.pathname,
    };
  },
  template: `<div class="sidebar pe-4 pb-3">
        <nav class="navbar bg-light navbar-light">
            <a href="index" class="navbar-brand mx-4 mb-3">
                <h3 class="text-primary">
                    <i class="fa fa-hashtag me-2"></i>smart reserve
                </h3>
            </a>
            <div class="d-flex align-items-center ms-4 mb-4">
                <div class="position-relative">
                    <img
                        class="rounded-circle"
                        src="../../assets/mainIcon.png"
                        alt=""
                        style="width: auto; height: auto;"
                    />
                    <div
                        class="bg-success rounded-circle border border-2 border-white position-absolute end-0 bottom-0 p-1"
                    ></div>
                </div>
                <div class="ms-3">
                    <h6 class="mb-0">환영합니다!</h6>
                    <span>{{storeName}} 점주님</span>
                </div>
            </div>
            <div class="navbar-nav w-100">
                <a href="/view/owner/index" :class="['nav-item', 'nav-link', isActive('/view/owner/index')]"><i class="fa fa-tachometer-alt me-2"></i>대시보드</a>
                <a href="/view/owner/storeModify" :class="['nav-item', 'nav-link', isActive('/view/owner/storeModify')]"><i class="fa fa-th me-2"></i>가게 수정</a>
                <a href="/view/owner/menu" :class="['nav-item', 'nav-link', isActive('/view/owner/menu')]"><i class="fa fa-th me-2"></i>메뉴 관리</a>
                <a href="/view/owner/coupon" :class="['nav-item', 'nav-link', isActive('/view/owner/coupon')]"><i class="fa fa-keyboard me-2"></i>쿠폰 관리</a>
                <a href="/view/owner/reserve" :class="['nav-item', 'nav-link', isActive('/view/owner/reserve')]"><i class="fa fa-table me-2"></i>예약 관리</a>
                <a href="/view/owner/review" :class="['nav-item', 'nav-link', isActive('/view/owner/review')]"><i class="fa fa-chart-bar me-2"></i>리뷰 관리</a>
            </div>
        </nav>
    </div>`,
  created() {
    this.fetchUserData();
  },
  methods: {
    fetchUserData() {
      axios
        .get("/api/status")
        .then((response) => {
          console.log(response);
          this.loginType = response.data.data.loginType;
          this.store = response.data.data;
          this.storeName = response.data.data.name;
        })
        .catch((error) => {
          console.error("There was an error fetching the user data!", error);
        });
    },
    isActive(menu) {
      return this.activeMenu === menu ? "active" : "";
    },
  },
});

Vue.component("footer-component", {
  template: `
    <div class="container-fluid pt-4 px-4">
        <div class="bg-light rounded-top p-4">
            <div class="row">
                <div class="col-12 col-sm-6 text-center text-sm-start">
                    &copy; <a href="#">smartreserve.store</a>, All Right Reserved.
                </div>
            </div>
        </div>
    </div>
`,
});

Vue.component("navbar-component", {
  data() {
    return {
      loginType: "",
      store: [],
      storeName: "",
      notifications: [],
    };
  },
  template: `<nav
          class="navbar navbar-expand bg-light navbar-light sticky-top px-4 py-0"
        >
          <a
            href="/view/owner/index"
            class="navbar-brand d-flex d-lg-none me-4"
          >
            <h2 class="text-primary mb-0"><i class="fa fa-hashtag"></i></h2>
          </a>
          <a href="#" class="sidebar-toggler flex-shrink-0">
            <i class="fa fa-bars"></i>
          </a>

          <div class="navbar-nav align-items-center ms-auto">
            <div class="nav-item dropdown">
              <a
                href="#"
                class="nav-link dropdown-toggle"
                data-bs-toggle="dropdown"
              >
                <i class="fa fa-bell me-lg-2 position-relative">
                  <span
                    v-if="notifications.some(notification => !notification.read)"
                    class="position-absolute top-0 start-100 translate-middle p-1 bg-danger rounded-circle"
                    style="width: 8px; height: 8px;"
                  ></span>

                </i>
                <span class="d-none d-lg-inline-flex">알림</span>
              </a>
              <div
                class="dropdown-menu dropdown-menu-end bg-light border-0 rounded-0 rounded-bottom m-0"
                style="max-height: 300px; overflow-y: auto;"
              >
              <div  v-for ="(notification, index) in notifications" :key="notification.alarmId">
                <a class="dropdown-item" role="button" @click="readNotification(notification.alarmId, index)" :style="{'color': notification.read ? '#888' : 'initial'}" >
                  <h6 class="fw-normal mb-0">{{notification.message}}</h6>
                  <small>{{notification.createAt}}</small>
                </a>
                <hr class="dropdown-divider" />
              </div>
               
              </div>
            </div>
            <div class="nav-item dropdown">
              <a
                href="#"
                class="nav-link dropdown-toggle"
                data-bs-toggle="dropdown"
              >
                <span class="d-none d-lg-inline-flex">{{ name }}</span>
              </a>
              <div
                class="dropdown-menu dropdown-menu-end bg-light border-0 rounded-0 rounded-bottom m-0"
              >
                <a @click="logoutProcess" href="#" class="dropdown-item"
                  >로그아웃</a
                >
              </div>
            </div>
          </div>
        </nav>`,
  created: function () {
    this.fetchUserData();
    this.spinner();
    this.getNotification();
  },
  computed: {
    // 예약 완료 상태는 보여주지 않음
    filteredReserves() {
      return this.reserves
        .filter((reserve) => reserve.statusCodeId !== 4)
        .slice(0, 5);
    },
    // 리뷰 요청 삭제 된거는 보여주지 않음
    filteredReviews() {
      return this.reviews.filter((review) => !review.deleteReq).slice(0, 5);
    },
    // 읽은 알림은 보여주지 않음
    filteredNotifications() {
      return this.notifications.slice(0, 5);
    },
  },

  methods: {
    // 알림 읽음 처리
    readNotification(alarmId, index) {
      this.notifications[index].read = true;
      const params = { alarmId: alarmId };
      axios
        .put("/api/owner/reserve/notification", null, { params })
        .then((response) => {
          console.log(response);
        });
    },
    // 알림 목록 받아오기
    getNotification() {
      axios.get("/api/owner/reserve/notification").then((response) => {
        console.log(response);
        this.notifications = response.data.data;
      });
    },
    fetchUserData: function () {
      axios
        .get("/api/status")
        .then((response) => {
          console.log("owner checked");
        })
        .catch((error) => {
          if (error.status === 401) {
            alert("로그인 정보를 확인할 수 없습니다. 다시 로그인해주세요");
          } else {
            alert("서버 오류로 인해 정보를 확인할 수 없습니다.");
          }
          location.href = "/view/user/userLogin";
        });
    },
    logoutProcess: function () {
      axios
        .post("/api/logout")
        .then((response) => {
          console.log(response);
          if (response.status === 200) {
            location.href = "/";
          }
        })
        .catch((error) => {
          console.error(error);
        });
    },
    spinner: function () {
      setTimeout(function () {
        if ($("#spinner").length > 0) {
          $("#spinner").removeClass("show");
        }
      }, 1);
    },
  },
});

Vue.component("spinner-component", {
  template: `
    <div
          id="spinner"
          class="show position-fixed translate-middle w-100 vh-100 top-50 start-50 d-flex align-items-center justify-content-center">
          <div class="spinner-border text-primary" style="width: 3rem; height: 3rem" role="status">
            <span class="sr-only">Loading...</span>
          </div>
        </div>
    `,
});
