new Vue({
  el: "#app", // Vue 인스턴스를 '#app'에 바인딩
  data: {
    name: "",
    loginYN: false,
    reviews: [],
    reserves: [],
    store: [],
    coupon: [],
    sales: [],
    notifications: [],
    countMonthlySalesCount: [],
    countMonthlySales: [],
    totalSales: 0,
    todayTotalSales: 0,
    todayTotalSalesCount: 0,
    totalSalesCount: 0,
  },

  computed: {
    // 예약 완료 상태는 보여주지 않음
    filteredReserves() {
      return this.reserves.filter(reserve => reserve.statusCodeId !== 4).slice(0, 5);
    },
    // 리뷰 요청 삭제 된거는 보여주지 않음
    filteredReviews() {
      return this.reviews.filter(review => !review.deleteReq).slice(0, 5);
    },
    // 읽은 알림은 보여주지 않음
    filteredNotifications() {
      return this.notifications.slice(0, 5);
    },
  },

  created: function () {
    this.fetchUserData(); // Vue 인스턴스 생성 시 데이터 요청
    this.fetchReviews();
    this.getFullCalendar();
    this.fetchReserves();
    this.fetchStoreData();
    this.fetchCouponData(); 
    this.getNotification();
    this.getSalesInfo();
  },

  methods: {
    getFullCalendar() {
      document.addEventListener('DOMContentLoaded', function() {
        var calendarEl = document.getElementById('calendar');
        var calendar = new FullCalendar.Calendar(calendarEl, {
          initialView: "dayGridMonth",
        });
        calendar.render();
      });
    },

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

    createChart() {
      var salesChart = document.getElementById("sales-chart").getContext("2d");
      var salesCountChart = document.getElementById("salesCount-chart").getContext("2d");

      const labels = [];
      const today = new Date();

      for (let i = 5; i >= 0; i--) {
        const month = new Date(today.getFullYear(), today.getMonth() - i, 1);
        labels.push(month.toLocaleString("default", { month: "long" }));
      }

      this.countMonthlySales.reverse();
      this.countMonthlySalesCount.reverse();

      var myChart3 = new Chart(salesChart, {
        type: "line",
        data: {
          labels: labels,
          datasets: [
            {
              label: "sales",
              fill: false,
              backgroundColor: "rgba(0, 156, 255, .3)",
              data: this.countMonthlySales,
            },
          ],
        },
        options: {
          responsive: true,
        },
      });
      var myChart4 = new Chart(salesCountChart, {
        type: "line",
        data: {
          labels: labels,
          datasets: [
            {
              label: "salesCount",
              fill: false,
              backgroundColor: "rgba(0, 156, 255, .3)",
              data: this.countMonthlySalesCount,
            },
          ],
        },
        options: {
          responsive: true,
        },
      });
    },

    // 알림 목록 받아오기
    getNotification() {
      axios.get("/api/owner/reserve/notification").then((response) => {
        console.log(response);
        this.notifications = response.data.data;
      });
    },

    // 결제, 매출 정보
    getSalesInfo() {
      axios.get("/api/owner/sales").then((response) => {
        console.log(response);
        this.totalSales = response.data.data.totalSales;
        this.totalSalesCount = response.data.data.totalSalesCount;
        this.todayTotalSales = response.data.data.todayTotalSales;
        this.todayTotalSalesCount = response.data.data.todayTotalSalesCount;
        this.countMonthlySalesCount = response.data.data.countMonthlySalesCount;
        this.countMonthlySales = response.data.data.countMonthlySales;

        this.createChart();
      });
    },

    fetchReserves() {
      axios.get('/api/owner/reserve')
        .then(response => {
          console.log(response);
          this.reserves = response.data.data.reservations;
        });
    },

    fetchStoreData() {
      axios.get(`/api/store`) // 가게 데이터 요청
        .then(response => {
          console.log(response);
          this.store = response.data.data.store; // 가게 데이터를 저장
        })
        .catch(error => {
          console.error("가게 데이터 로드 중 오류 발생:", error);
        });
    },

    fetchCouponData() {
      axios.get('/api/coupon') // 쿠폰 데이터 요청
        .then(response => {
          console.log(response);
          this.coupon = response.data.data.coupon; // 쿠폰 데이터를 저장
        })
        .catch(error => {
          console.error("쿠폰 데이터 로드 중 오류 발생:", error);
        });
    },

    updateReserveStatus(reserveId, status) {
      var statusCode = Number.parseInt(status);
      const params = { statusCode: statusCode };
      axios.post('/api/owner/reserve/' + reserveId, null, { params })
        .then(response => {
          console.log(response);
          if (statusCode == 2) {
            alert(reserveId + "의 예약 승인요청이 완료 되었습니다.");
          } else if (statusCode == 3) {
            alert(reserveId + "의 예약 취소요청이 완료 되었습니다.");
          }
        });
    },

    fetchUserData() {
      axios.get('/api/status') // 서버에 /api/status 요청
        .then(response => {
          console.log(response);
          if (response.data.data.loginType == 'store') {
            this.name = response.data.data.name;
            this.loginYN = true;
          } else if (response.data.data.loginType == 'user') {
            location.href = '/';
          }
        })
        .catch(error => {
          location.href = '/';
        });
    },

    logoutProcess() {
      axios.post('/api/logout')
        .then(response => {
          console.log(response);
          if (response.status === 200) {
            location.href = '/';
          }
        })
        .catch(error => {
          console.error(error);
        });
    },

    fetchReviews() {
      axios.get('/api/owner/review')
        .then(response => {
          console.log(response);
          this.reviews = response.data.data.reviews;
        })
        .catch(error => {
          console.log(error);
        });
    },

    deleteReq(reviewId) {
      axios.delete('/api/owner/review/' + reviewId)
        .then(response => {
          if (response.status === 200) {
            alert(reviewId + "의 삭제 요청이 완료되었습니다.");
          }
        })
        .catch(error => {
          alert("에러발생");
          console.log(error);
        });
    },

    formatDate(timestamp) {
      // 밀리초 단위의 타임스탬프를 변환하여 날짜로 포맷
      const date = new Date(timestamp);
      return date.toLocaleString(); // 날짜와 시간을 로컬 형식으로 반환
    },

    mounted() {}
}});
