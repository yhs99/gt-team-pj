new Vue({
  el: "#app", // Vue 인스턴스를 '#app'에 바인딩
  data: {
    name: "",
    loginYN: false,
    reviews: [],
    reserves: [],
    sales: [],
    totalPrice: 0,
    todayTotalPrice: 0,
    firstHalfPrice: 0,
    secondHalfPrice: 0,
  },

  computed: {
    //예약 완료 상태는 보여주지 않음
    filteredReserves() {
      return this.reserves
        .filter((reserve) => reserve.statusCodeId !== 4)
        .slice(0, 5);
    },
    filteredReviews() {
      return this.reviews.filter((review) => !review.deleteReq).slice(0, 5);
    },
  },
  created: function () {
    this.fetchUserData(); // Vue 인스턴스 생성 시 데이터 요청
    this.fetchReviews();
    this.getFullCalendar();
    this.fetchReserves();
    // this.getSalesInfo();
  },

  methods: {
    createChart() {
      var salesChart = document.getElementById("sales-chart").getContext("2d");
      var salesCountChart = document
        .getElementById("salesCount-chart")
        .getContext("2d");
      var myChart3 = new Chart(salesChart, {
        type: "line",
        data: {
          labels: ["1", "2"],
          datasets: [
            {
              label: "sales",
              fill: false,
              backgroundColor: "rgba(0, 156, 255, .3)",
              // data: this.countMonthlyReview,
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
          labels: ["1", "2"],
          datasets: [
            {
              label: "salesCount",
              fill: false,
              backgroundColor: "rgba(0, 156, 255, .3)",
              // data: this.countMonthlyReview,
            },
          ],
        },
        options: {
          responsive: true,
        },
      });
    },

    getSalesInfo() {
      axios.get("/api/owner/sales").then((response) => {
        console.log(response);
      });
    },
    getFullCalendar() {
      document.addEventListener("DOMContentLoaded", function () {
        var calendarEl = document.getElementById("calendar");
        var calendar = new FullCalendar.Calendar(calendarEl, {
          initialView: "dayGridMonth",
        });
        calendar.render();
      });
    },

    fetchReserves: function () {
      axios.get("/api/owner/reserve").then((response) => {
        console.log(response);
        this.reserves = response.data.data.reservations;
      });
    },

    updateReserveStatus: function (reserveId, status) {
      var statusCode = Number.parseInt(status);
      const params = { statusCode: statusCode };
      axios
        .post("/api/owner/reserve/" + reserveId, null, { params })
        .then((response) => {
          console.log(response);
          if (statusCode == 2) {
            alert(reserveId + "의 예약 승인요청이 완료 되었습니다.");
          } else if (statusCode == 3) {
            alert(reserveId + "의 예약 취소요청이 완료 되었습니다.");
          }
        });
    },

    fetchUserData: function () {
      axios
        .get("/api/status") // 서버에 /api/status 요청
        .then((response) => {
          console.log(response);
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
    fetchReviews: function () {
      axios
        .get("/api/owner/review")
        .then((response) => {
          console.log(response);
          this.reviews = response.data.data.reviews;
        })
        .catch((error) => {
          console.log(error);
        });
    },
    deleteReq: function (reviewId) {
      axios
        .delete("/api/owner/review/" + reviewId)
        .then((response) => {
          if (response.status === 200) {
            alert(reviewId + "의 삭제 요청이 완료되었습니다.");
          }
        })
        .catch((error) => {
          alert("에러발생");
          console.log(error);
        });
    },
    formatDate: function (timestamp) {
      // 밀리초 단위의 타임스탬프를 변환하여 날짜로 포맷
      const date = new Date(timestamp);
      return date.toLocaleString(); // 날짜와 시간을 로컬 형식으로 반환
    },
  },

  mounted() {
    this.createChart();
  },
});
