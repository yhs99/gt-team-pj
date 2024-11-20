new Vue({
  el: "#app",
  data: {
    reserves: [],
    monthlyTotalReserve: [],
    searchReserveList: [],
    searchReserveSlots: [],
    searchCustomerName: "",
    searchStartDate: "",
    searchEndDate: "",
    searchAvailableReserveDate: "",
    totalReserve: 0,
    totalTodayReserve: 0,
    currentPage: 1,
    pageSize: 5,
    totalPages: 0,
  },
  created: function () {
    this.fetchReserves();
  },

  methods: {
    fetchReserves: function () {
      axios.get("/api/owner/reserve").then((response) => {
        this.reserves = response.data.data.reservations;
        this.totalReserve = response.data.data.totalReserve;
        this.totalTodayReserve = response.data.data.totalTodayReserve;
        this.totalPages = Math.ceil(this.totalReserve / this.pageSize);
        this.monthlyTotalReserve = response.data.data.monthlyTotalReserve;

        this.searchReserveList = this.reserves;

        this.createChart();
      });
    },
    //예약 상태 업데이트
    updateReserveStatus: function (reserveId, status, index) {
      var statusCode = Number.parseInt(status);
      this.reserves[index].statusCodeId = statusCode;
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

    changePage: function (page) {
      this.currentPage = page;
    },
    searchReserves() {
      this.searchReserveList = this.reserves.filter((reserve) => {
        const matchesName = reserve.name.includes(this.searchCustomerName);
        let matchStartDate = true;
        let matchEndDate = true;
        if (this.searchStartDate) {
          let startDate = new Date(this.searchStartDate);
          startDate.setHours(0, 0, 0, 0);
          matchStartDate = new Date(reserve.reserveTime) >= startDate;
        }
        if (this.searchEndDate) {
          let endDate = new Date(this.searchEndDate);
          endDate.setHours(23, 59, 59, 999);
          matchEndDate = new Date(reserve.reserveTime) <= endDate;
        }

        return matchesName && matchStartDate && matchEndDate;
      });

      //검색결과에 맞춘 페이지네이션
      this.totalPages = Math.ceil(
        this.searchReserveList.length / this.pageSize
      );
    },
    searchAvailableDate() {
      if (this.searchAvailableReserveDate) {
        const dateObject = new Date(this.searchAvailableReserveDate);
        const formattedDate = dateObject.toISOString().split("T")[0];

        const params = { reserveTime: formattedDate };
        axios
          .get("/api/owner/reserve/available", { params })
          .then((response) => {
            this.searchReserveSlots = response.data.data;
          });
      } else {
        alert("날짜를 입력해주세요");
      }
    },
    createChart() {
      //Single Bar Chart
      var ctx4 = $("#reserve-chart").get(0).getContext("2d");

      const labels = [];
      const today = new Date();

      for (let i = 5; i >= 0; i--) {
        const month = new Date(today.getFullYear(), today.getMonth() - i, 1);
        labels.push(month.toLocaleString("default", { month: "long" }));
      }
      this.monthlyTotalReserve.reverse();

      var myChart4 = new Chart(ctx4, {
        type: "bar",
        data: {
          labels: labels,
          datasets: [
            {
              backgroundColor: [
                "rgba(0, 156, 255, .7)",
                "rgba(0, 156, 255, .6)",
                "rgba(0, 156, 255, .5)",
                "rgba(0, 156, 255, .4)",
                "rgba(0, 156, 255, .3)",
              ],
              data: this.monthlyTotalReserve,
            },
          ],
        },
        options: {
          responsive: true,
        },
      });
    },
  },
  computed: {
    paginatedReserves() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.searchReserveList.slice(start, end);
    },
  },
});
