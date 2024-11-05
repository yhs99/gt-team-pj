Vue.component("header-component", {
  template: `
    <header class="fixed-header">
      <div class="container-fluid d-flex align-items-center p-3">
        <a class="navbar-brand fs-3 text-warning fw-bold d-flex align-items-center" href="/">
          <img src="/assets/mainIcon.png" alt="SmartReserve Logo" class="me-2" width="30" height="30" />
          SmartReserve
        </a>
        <input
         type="text"
          class="form-control ms-3"
          placeholder="검색"
          aria-label="Search"
          style="width: 300px; cursor: pointer;"
          @click="goToSearch"
          readonly
        />
        <div class="dropdown ms-auto">
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
          </ul>
        </div>
      </div>
      
    </header>
  `,
  methods: {
    goToSearch() {
      window.location.href = "search";
    },
  },
});
