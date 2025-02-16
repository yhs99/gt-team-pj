new Vue({
  el: "#vueApp",
  components: {},
  data: {
    getAllReviewList: [],
    totalReviewCount: 0,
    totalAvgScore: 0,
    totalTodayReview: 0,
    totalAvgTodayScore: 0,
    countMonthlyReview: [],
    countScore: [],
    searchCustomerName: "",
    searchStartDate: "",
    searchEndDate: "",
    searchReviewList: [],
    showModal: false,
    selectedReviewId: 0,
    currentPage: 1, //현재 페이지
    pageSize: 5, // 페이지당 표시 갯수
    totalPages: 0, // 총페이지
  },

  methods: {
    getAllReviews(orderMethod) {
      //쿼리 파라미터 설정
      const params = { sortMethod: orderMethod };
      //차트에 값을 할당하기 주기 위해서 return문 사용
      return axios.get("/api/owner/review", { params }).then((response) => {
        this.totalReviewCount = response.data.data.totalReview;
        this.totalTodayReview = response.data.data.todayReview;
        this.totalAvgScore =
          this.totalReviewCount > 0
            ? Math.round(
                (response.data.data.totalScore / this.totalReviewCount) * 100
              ) / 100
            : 0;
        this.totalAvgTodayScore =
          this.totalTodayReview > 0
            ? Math.round(
                (response.data.data.todayTotalScore / this.totalTodayReview) *
                  100
              ) / 100
            : 0;
        this.getAllReviewList = response.data.data.reviews;
        this.countMonthlyReview = response.data.data.countMonthlyReview;
        this.countScore = response.data.data.countScore;
        this.totalPages = Math.ceil(this.totalReviewCount / this.pageSize);
        this.searchReviewList = this.searchReviews(orderMethod);
      });
    },
    searchReviews(orderMethod) {
      this.searchReviewList = this.getAllReviewList.filter((review) => {
        const matchesName = review.userName.includes(this.searchCustomerName);

        let matchesDateRange = true;
        if (this.searchStartDate) {
          let startDate = new Date(this.searchStartDate);
          startDate.setHours(0, 0, 0, 0);
          matchesDateRange = new Date(review.createAt) >= startDate;
        }
        if (this.searchEndDate) {
          let endDate = new Date(this.searchEndDate);
          endDate.setHours(23, 59, 59, 999);
          matchesDateRange =
            matchesDateRange && new Date(review.createAt) <= endDate;
        }

        return matchesName && matchesDateRange;
      });
      //검색결과에 맞춘 페이지네이션
      this.totalPages = Math.ceil(this.searchReviewList.length / this.pageSize);

      if (orderMethod === "newest") {
        return this.searchReviewList.sort(
          (a, b) => new Date(b.createAt) - new Date(a.createAt)
        );
      } else if (orderMethod === "score") {
        return this.searchReviewList.sort((a, b) => b.score - a.score);
      }
    },

    changePage: function (page) {
      this.currentPage = page;
    },

    createChart() {
      var ctx4 = document.getElementById("bar-chart").getContext("2d");
      var myChart4 = new Chart(ctx4, {
        type: "bar",
        data: {
          labels: ["5점", "4점", "3점", "2점", "1점"],
          datasets: [
            {
              label: "score",
              backgroundColor: [
                "rgba(0, 156, 255, .7)",
                "rgba(0, 156, 255, .6)",
                "rgba(0, 156, 255, .5)",
                "rgba(0, 156, 255, .4)",
                "rgba(0, 156, 255, .3)",
              ],
              data: this.countScore,
            },
          ],
        },
        options: {
          responsive: true,
        },
      });

      const labels = [];
      const today = new Date();

      for (let i = 5; i >= 0; i--) {
        const month = new Date(today.getFullYear(), today.getMonth() - i, 1);
        labels.push(month.toLocaleString("default", { month: "long" }));
      }

      this.countMonthlyReview.reverse();

      var ctx3 = document.getElementById("line-chart").getContext("2d");
      var myChart3 = new Chart(ctx3, {
        type: "line",
        data: {
          labels: labels,
          datasets: [
            {
              label: "review",
              fill: false,
              backgroundColor: "rgba(0, 156, 255, .3)",
              data: this.countMonthlyReview,
            },
          ],
        },
        options: {
          responsive: true,
        },
      });
    },

    //모달창 reviewId 값 할당
    showDeleteRequestModal(reviewId) {
      this.showModal = true;
      this.selectedReviewId = reviewId;
    },

    //삭제 버튼 클릭시 리뷰 삭제 요청
    deleteReview(reviewId) {
      axios.delete(`/api/owner/review/${reviewId}`).then((response) => {
        this.showModal = false;
        this.getAllReviews("score");
      });
    },
    formatDate: function (timestamp) {
      const date = new Date(timestamp);
      return date.toLocaleString();
    },
  },
  mounted() {
    this.getAllReviews("score").then(() => {
      this.createChart();
    });
  },
  computed: {
    paginatedReviews() {
      const start = (this.currentPage - 1) * this.pageSize;
      const end = start + this.pageSize;
      return this.searchReviewList.slice(start, end);
    },
  },
});
