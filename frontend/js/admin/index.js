new Vue({
  el: '#app', // Vue 인스턴스를 '#app'에 바인딩
  data: {
    name:"",
    loginYN:false,
    deleteReqReviews:[],
    deleteReqReviewsCnt:0,
    summaryTitle: {},
    dailySales: [],
    monthlySales: []
  },
  created: function() {
    this.fetchUserData(); // Vue 인스턴스 생성 시 데이터 요청
    this.fetchAllDeleteRequestReviews();
  },
  mounted: function() {
    this.fetchSummary();
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
    fetchAllDeleteRequestReviews: function() { // 리뷰 삭제 요청 목록 조회
      axios.get('/api/admin/deleteReview')
      .then(response => {
        console.log(response);
        this.deleteReqReviews = response.data.data.reviewData;
        this.deleteReqReviewsCnt = response.data.data.reviewCount;
      })
      .catch(error => {
        console.log(error);
      })
    },
    deleteReview: function(reviewId) { // 삭제 요청 리뷰 삭제처리
      axios.delete('/api/admin/deleteReview/'+reviewId)
      .then(response => {
        if(response.status===200) {
          alert(reviewId + "의 리뷰가 삭제처리 되었습니다.");
        }
      })
      .catch(error => {
        alert("에러발생");
        console.log(error);
      })
      .finally(() => {
        this.fetchAllDeleteRequestReviews();
      })
    },
    cancelDeleteReview: function(reviewId) {  // 리뷰 삭제 요청 거절
      axios.post('/api/admin/cancelDeleteReqReview/'+reviewId)
      .then(response => {
        if(response.status === 200) {
          alert(reviewId + "의 리뷰 삭제 요청이 거절처리 되었습니다.");
        }
      })
      .catch(error => {
        alert("서버 오류로 인해 요청이 처리되지 않았습니다.");
      })
      .finally(() => {
        this.fetchAllDeleteRequestReviews();
      })
    },
    formatDate: function(timestamp) {
      // timestamp 타입을 Date로 변환
      const date = new Date(timestamp);
      return date.toLocaleString(); // 날짜와 시간을 로컬 형식으로 반환
    },
    fetchSummary: function() {
      axios.get('/api/admin/stats/sales')
      .then(response => {
        let summaryData = response.data.data;
        this.summaryTitle = summaryData.summaryTitle;
        this.dailySales = summaryData.dailySales;
        this.monthlySales = summaryData.monthlySales;
        this.fetchChart();
        this.fetchReserveInfoCalendar();
      })
      .catch(error => {
        console.log("서버오류로 인해 요청이 처리 되지 않았습니다.");
      })
    },
    fetchReserveInfoCalendar: function() {
      const events = this.dailySales.map((sale, index) => (
        {
          id: 'sales' + index,
          title: '매출 :: ' + (sale.value).toLocaleString() + '원',
          start: sale.label,
          end: sale.label
        }
      ));
      var calendarEl = document.getElementById('reserve-calendar');
      var calendar = new FullCalendar.Calendar(calendarEl, {
        locale: 'ko',
        // validRange: { 달력의 범위를 지정
        //   start: start,
        //   end: end
        // },
        buttonText: {
          today: '이번달',
          month: '월별',
          week: '주간별',
          day: '일별',
        },
        headerToolbar: {
          start:'prev next today',
          center: 'title',
          end:''
        },
        events: events
      });
      calendar.render();
    },
    fetchChart: function() {
    const labels = this.monthlySales.map(sale => sale.label);
    const dataValues = this.monthlySales.map(sale => sale.value);
    var ctx1 = $("#worldwide-sales").get(0).getContext("2d");
    var myChart1 = new Chart(ctx1, {
        type: "bar",
        data: {
            labels: labels,
            datasets: [{
                    label: "매출액",
                    data: dataValues,
                    backgroundColor: "rgba(0, 156, 255, .7)"
                }
            ]
            },
        options: {
            responsive: true
        }
    });
    }
  }
});