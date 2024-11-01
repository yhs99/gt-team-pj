Vue.component('sidebar-component', {
  data() {
    return {activeMenu:window.location.pathname}
  },
  template: `
  <nav class="navbar bg-light navbar-light">
            <a href="/view/admin/srAdminIndex" class="navbar-brand mx-4 mb-3">
              <h3 class="text-primary"><i class="fa fa-hashtag me-2"></i>SmartReserve</h3>
            </a>
            <div class="navbar-nav w-100">
              <a href="/view/admin/srAdminIndex" :class="['nav-item', 'nav-link', isActive('/view/admin/srAdminIndex')]"><i class="fa fa-tachometer-alt me-2"></i>대시보드</a>
              <a href="/view/admin/srAdminStores" :class="['nav-item', 'nav-link', isActive('/view/admin/srAdminStores')]"><i class="fa fa-th me-2"></i>매장 관리</a>
              <a href="/view/admin/srAdminUsers" :class="['nav-item', 'nav-link', isActive('/view/admin/srAdminUsers')]"><i class="fa fa-keyboard me-2"></i>유저 관리</a>
              <a href="/view/admin/srAdminReserves" :class="['nav-item', 'nav-link', isActive('/view/admin/srAdminReserves')]"><i class="fa fa-table me-2"></i>예약 관리</a>
              <a href="/view/admin/srAdminReviews" :class="['nav-item', 'nav-link', isActive('/view/admin/srAdminReviews')]"><i class="fa fa-chart-bar me-2"></i>리뷰 관리</a>
              </div>
            </nav>
  `,
  methods: {
    isActive(menu) {
      return this.activeMenu === menu ? 'active' : '';
    }
  }
  });

Vue.component('footer-component', {
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
  `
  });

Vue.component('navbar-component', {
  template:
  `<nav class="navbar navbar-expand bg-light navbar-light sticky-top px-4 py-0">
  <a href="/view/owner/index" class="navbar-brand d-flex d-lg-none me-4">
    <h2 class="text-primary mb-0"><i class="fa fa-hashtag"></i></h2>
  </a>
  <a href="#" class="sidebar-toggler flex-shrink-0">
    <i class="fa fa-bars"></i>
  </a>
  <div class="navbar-nav align-items-center ms-auto">
    <div class="nav-item dropdown">
      <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown">
        <span class="d-none d-lg-inline-flex">관리자</span>
      </a>
      <div class="dropdown-menu dropdown-menu-end bg-light border-0 rounded-0 rounded-bottom m-0">
        <a @click="logoutProcess" href="#" class="dropdown-item">로그아웃</a>
      </div>
    </div>
  </div>
</nav>`,
created: function(){
  this.fetchUserData();
  this.spinner();
},
methods: {
  fetchUserData: function() {
    axios.get('/api/status')
    .then(response => {
      console.log("admin checked");
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
  spinner: function() {
    setTimeout(function () {
        if ($('#spinner').length > 0) {
            $('#spinner').removeClass('show');
        }
    }, 1);
  }
}
});
Vue.component('spinner-component', {
  template: `
  <div
        id="spinner"
        class="show bg-white position-fixed translate-middle w-100 vh-100 top-50 start-50 d-flex align-items-center justify-content-center">
        <div class="spinner-border text-primary" style="width: 3rem; height: 3rem" role="status">
          <span class="sr-only">Loading...</span>
        </div>
      </div>
  `
});

/**
 * <div class="nav-item dropdown">
                <a href="#" class="nav-link dropdown-toggle" data-bs-toggle="dropdown"
                  ><i class="fa fa-laptop me-2"></i>구성!!</a>
                <div class="dropdown-menu bg-transparent border-0">
                  <a href="/view/owner/button.html" class="dropdown-item">버튼</a>
                  <a href="typography.html" class="dropdown-item">문자디자인</a>
                  <a href="element.html" class="dropdown-item">기타 항목</a>
                </div>
              </div>
 */