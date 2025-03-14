new Vue({
  el: "#app", // Vue 인스턴스를 '#app'에 바인딩
  data: {
    name: "",
    loginYN: false,
    reviews: [],
    reserves: [],
    store: [],
    coupon: [
      {
        couponName: "",
        discount: 0,
        stock: 0,
        start: "", // 빈 문자열로 초기화
        end: "", // 빈 문자열로 초기화
      },
    ],
    sales: [],
    salesByDate: [],
    reserveByDate: [],
    reviewByDate: [],
    notifications: [],
    countMonthlySalesCount: [],
    countMonthlySales: [],
    quarters: {},
    totalSales: 0,
    todayTotalSales: 0,
    todayTotalSalesCount: 0,
    totalSalesCount: 0,
  },

  computed: {
    //예약 대기 상태만 보여줌
    filteredReserves() {
      return this.reserves
        .filter((reserve) => reserve.statusCodeId == 1)
        .slice(0, 5);
    },
    sortedCoupons() {
      return this.coupon.sort((a, b) => b.couponId - a.couponId); // 쿠폰 번호 기준 내림차순 정렬
    },

    // 리뷰 요청 삭제 된거는 보여주지 않음
    filteredReviews() {
      return this.reviews.filter((review) => !review.deleteReq).slice(0, 5);
    },
  },

  created: function () {
    this.fetchUserData(); // Vue 인스턴스 생성 시 데이터 요청
    this.fetchReviews();
    this.fetchReserves();
    this.fetchStoreData();
    this.fetchCouponData();
    this.getNotification();
  },

  methods: {
    getFullCalendar() {
      document.addEventListener("DOMContentLoaded", function () {
        var calendarEl = document.getElementById("calendar");
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
        .then((response) => {});
    },

    fetchReserves: function () {
      axios.get("/api/owner/reserve").then((response) => {
        this.reserves = response.data.data.reservations;
        this.reserveByDate = response.data.data.reserveByDate;
      });
    },

    fetchReviews: function () {
      axios
        .get("/api/owner/review")
        .then((response) => {
          this.reviews = response.data.data.reviews;
          this.reviewByDate = response.data.data.reviewByDate;
          this.getFullCalendar();
        })
        .catch((error) => {
          console.log(error);
        });
    },

    createChart() {
      var salesChart = document.getElementById("sales-chart").getContext("2d");
      var salesCountChart = document
        .getElementById("salesCount-chart")
        .getContext("2d");

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
        this.notifications = response.data.data;
      });
    },

    // 결제, 매출 정보
    getSalesInfo() {
      axios.get("/api/owner/sales").then((response) => {
        this.totalSales = response.data.data.totalSales;
        this.totalSalesCount = response.data.data.totalSalesCount;
        this.todayTotalSales = response.data.data.todayTotalSales;
        this.todayTotalSalesCount = response.data.data.todayTotalSalesCount;
        this.countMonthlySalesCount = response.data.data.countMonthlySalesCount;
        this.countMonthlySales = response.data.data.countMonthlySales;
        this.salesByDate = response.data.data.salesByDate;

        this.createChart();
        this.getFullCalendar();
        this.calcuateQuarterSale();
      });
    },

    calcuateQuarterSale() {
      const sales = { Q1: 0, Q2: 0, Q3: 0, Q4: 0 };
      this.salesByDate.forEach((sale) => {
        const month = new Date(sale.date).getMonth() + 1;
        if (month >= 1 && month <= 3) {
          sales.Q1 += sale.total;
        } else if (month >= 4 && month <= 6) {
          sales.Q2 += sale.total;
        } else if (month >= 7 && month <= 9) {
          sales.Q3 += sale.total;
        } else if (month >= 10 && month <= 12) {
          sales.Q4 += sale.total;
        }
      });

      this.quarters = sales;
    },

    getFullCalendar() {
      var calendarEl = document.getElementById("calendar");
      var calendar = new FullCalendar.Calendar(calendarEl, {
        locale: "ko",
        buttonText: {
          today: "이번달",
          month: "월별",
          week: "주간별",
          day: "일별",
        },
        headerToolbar: {
          start: "prev next today",
          center: "title",
          end: "",
        },
      });
      this.salesByDate.forEach((sale, index) => {
        calendar.addEvent({
          id: "sale" + index,
          title: "일일 매출 : " + sale.total.toLocaleString() + "원",
          start: sale.date,
          end: sale.date,
        });
      });

      this.reserveByDate.forEach((reserve, index) => {
        calendar.addEvent({
          id: "reserve" + index,
          title: "일일 예약 수 : " + reserve.total,
          start: reserve.date,
          end: reserve.date,
          backgroundColor: "#4CAF50",
          borderColor: "#4CAF50",
        });
      });

      this.reviewByDate.forEach((review, index) => {
        calendar.addEvent({
          id: "review" + index,
          title: "작성된 리뷰 수 : " + review.total,
          start: review.date,
          end: review.date,
          backgroundColor: "#FF9800",
          borderColor: "#FF9800",
        });
      });

      calendar.render();
    },

    fetchStoreData() {
      axios
        .get(`/api/store`) // 가게 데이터 요청
        .then((response) => {
          this.store = response.data.data.store; // 가게 데이터를 저장
        })
        .catch((error) => {
          console.error("가게 데이터 로드 중 오류 발생:", error);
        });
    },

    fetchCouponData() {
      axios
        .get("/api/coupon") // 쿠폰 데이터 요청
        .then((response) => {
          this.coupon = response.data.data; // 쿠폰 데이터를 저장
        })
        .catch((error) => {
          console.error("쿠폰 데이터 로드 중 오류 발생:", error);
        });
    },

    updateReserveStatus(reserveId, status) {
      var statusCode = Number.parseInt(status);
      const params = { statusCode: statusCode };
      axios
        .post("/api/owner/reserve/" + reserveId, null, { params })
        .then((response) => {
          if (statusCode == 2) {
            alert(reserveId + "의 예약 승인요청이 완료 되었습니다.");
            location.reload();
          } else if (statusCode == 3) {
            alert(reserveId + "의 예약 취소요청이 완료 되었습니다.");
            location.reload();
          }
        });
    },

    fetchUserData: function () {
      axios
        .get("/api/status") // 서버에 /api/status 요청
        .then((response) => {
          if (response.data.data.loginType == "store") {
            this.name = response.data.data.name;
            this.loginYN = true;
          } else if (response.data.data.loginType == "user") {
            location.href = "/";
          }
        })
        .catch((error) => {
          location.href = "/";
        });
    },

    logoutProcess() {
      axios
        .post("/api/logout")
        .then((response) => {
          if (response.status === 200) {
            location.href = "/";
          }
        })
        .catch((error) => {
          console.error(error);
        });
    },

    deleteReq: function (reviewId) {
      axios
        .delete("/api/owner/review/" + reviewId)
        .then((response) => {
          if (response.status === 200) {
            alert(reviewId + "의 삭제 요청이 완료되었습니다.");
            location.reload();
          }
        })
        .catch((error) => {
          alert("서버 오류로 인한 에러발생");
          console.error(error);
        });
    },

    formatDateTimeStamp(timestamp) {
      // 밀리초 단위의 타임스탬프를 변환하여 날짜로 포맷
      const date = new Date(timestamp);
      return date.toLocaleString(); // 날짜와 시간을 로컬 형식으로 반환
    },
    formatDate(dateArray) {
      // 배열을 날짜로 변환하고, yyyy-MM-dd 형식으로 반환
      const date = new Date(dateArray[0], dateArray[1] - 1, dateArray[2]);
      return date.toISOString().split("T")[0]; // yyyy-MM-dd 형식
    },
  },
  mounted() {
    this.getSalesInfo();
  },
});
