Vue.component('sidebar-component', {
    data() {
        return {
            loginType: '',
            store: [],
            storeName: '',
            activeMenu: window.location.pathname
        }
    },
    template: 
    `<div class="sidebar pe-4 pb-3">
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
            axios.get('/api/status')
                .then(response => {
                    console.log(response);
                    this.loginType = response.data.data.loginType;
                    this.store = response.data.data;
                    this.storeName = response.data.data.name
                })
                .catch(error => {
                    console.error("There was an error fetching the user data!", error);
                });
        },
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

Vue.component('spinner-component', {
    template: `
    <div
          id="spinner"
          class="show position-fixed translate-middle w-100 vh-100 top-50 start-50 d-flex align-items-center justify-content-center">
          <div class="spinner-border text-primary" style="width: 3rem; height: 3rem" role="status">
            <span class="sr-only">Loading...</span>
          </div>
        </div>
    `
  });