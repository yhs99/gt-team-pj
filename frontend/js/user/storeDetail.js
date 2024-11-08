new Vue({
  el: '#app', // Vue 인스턴스가 연결될 HTML 요소
  data: {
      restaurantData: {
          data: {
            storeImages:[]
          }
      },
      todayDay:"",
      todayOperation:{},
      schedules: [],
      isOpen: false,
      menuData:[],
      reviewData:[]
  },
  computed:{
    allSchedulesOpen() {
        // schedules가 비어있지 않으면 모든 closeDay가 false인지 확인
        return this.schedules.length > 0 && this.schedules.every(schedule => !schedule.closeDay);
    },
    filteredMenus() {
        // menuData.menu가 정의되어 있는지 확인
        if (!this.menuData || !this.menuData.menu) {
            return []; // menuData.menu가 없을 경우 빈 배열 반환
        }

        // 1. main 메뉴 필터링
        const mainMenus = this.menuData.menu.filter(menu => menu.main);

        if (this.menuData.numOfMainMenu > 5) {
            // 2. main 메뉴가 5개 이상일 경우, main 메뉴 중 최대 5개 출력
            return mainMenus.slice(0, 5);
        } else {
            // 3. main 메뉴가 5개 이하일 경우, main 메뉴 + 나머지 메뉴 합쳐서 최대 5개 출력
            const remainingMenus = this.menuData.menu.filter(menu => !menu.main);
            return [...mainMenus, ...remainingMenus].slice(0, 5);
        }
    },
    combinedImages() {
        // storeImages와 reviewImages를 합치기
        const reviewImages = this.reviewData.flatMap(review => review.reviewImages);
        return [...this.restaurantData.data.storeImages, ...reviewImages];
    },
    calculateReviewScore(){
        let scoreSum = 0;
        let scoreAvg = 0;
        for (let review of this.reviewData){
            scoreSum +=review.score;
        }
        if (this.reviewData.length > 0) {
            scoreAvg = scoreSum / this.reviewData.length;
        }
        return scoreAvg;
    },
    filteredReviews() {
        return this.reviewData
            .filter(review => review.score >= 4) // score가 4 이상인 리뷰만 필터링
            .sort((a, b) => b.score - a.score); // score가 높은 순으로 정렬
    }
  },
  methods: {
      makeACalendar() {
          document.addEventListener('DOMContentLoaded', function() {
              const calendarEl = document.getElementById('calendar');
              const calendar = new FullCalendar.Calendar(calendarEl, {
                  initialView: 'dayGridMonth'
              });
              calendar.render();
          });
      },

      goToPage(url) {
          const defaultUrl = '기본페이지.html';
          window.location.href = url || defaultUrl;
      },

      kakaoMap() {
          if (this.restaurantData.data.length === 0) {
              console.error('No restaurant data available for map');
              return;
          }

          let lat = this.restaurantData.data.locationLatX;
          let lon = this.restaurantData.data.locationLonY;
          console.log(lat,lon);

          var mapContainer = document.getElementById('map'); // 지도를 표시할 div 
          if (!mapContainer) {
              console.error('Map container not found');
              return;
          }

          var mapOption = { 
              center: new kakao.maps.LatLng(lon,lat), // 지도의 중심좌표
              level: 3 // 지도의 확대 레벨
          };

          var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다

          // 마커가 표시될 위치입니다 
          var markerPosition = new kakao.maps.LatLng(lon,lat); 

          // 마커를 생성합니다
          var marker = new kakao.maps.Marker({
              position: markerPosition
          });

          // 마커가 지도 위에 표시되도록 설정합니다
          marker.setMap(map);
      },
      getTodayDay() {
        const days = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
        const today = new Date(); // 현재 날짜 및 시간 가져오기
        const dayIndex = today.getDay(); // 0 (일요일)부터 6 (토요일)까지의 숫자 반환
        this.todayDay = days[dayIndex]; // 해당 인덱스에 맞는 요일 문자열 반환
    },
  
      fetchRestaurantData() {
          axios.get('http://localhost/api/stores/store/146') // API URL을 입력하세요
              .then(response => {
                  this.restaurantData = response.data; // 응답 데이터를 restaurantData에 저장
                  this.schedules = this.restaurantData.data.storeSchedules;
                  console.log("schedules:",this.schedules);
                  console.log(this.restaurantData); // 가져온 데이터 출력
                  console.log(this.restaurantData.data.storeImages);
                  // storeName 접근
                  if (this.restaurantData && this.restaurantData.data) {
                    this.operationTodayDay();
                      this.$nextTick(() => {
                        this.fetchMenuData();
                        this.fetchReviewData();
                        this.kakaoMap(); // 데이터가 로드된 후 DOM 업데이트가 완료된 후 지도를 초기화
                    });
                  } else {
                      console.error('No restaurant data found');
                  }
              })
              .catch(error => {
                  console.error('Error fetching restaurantData:', error);
              });
      },
      operationTodayDay(){
        let schedule = this.restaurantData.data.storeSchedules;
        for(i = 0; i <schedule.length; i++){
            if (this.todayDay == schedule[i].dayOfWeek){
                this.todayOperation = schedule[i];
                console.log("todayOperation:",this.todayOperation);
                return;
            } 

        }
      },
      getClosedDays() {
        // closeDay가 true인 항목의 dayOfWeek를 배열로 반환
        return this.schedules
            .filter(schedule => schedule.closeDay)
            .map(schedule => schedule.dayOfWeek);
    },
     fetchMenuData() {
        axios.get('http://localhost/api/stores/menu/30')
            .then(response => {
                this.menuData = response.data.data;
                console.log("menuData:", this.menuData);
            })
            .catch(error => {
                console.error('Error fetching MenuData',error);
            })
    },
    setDefaultImage(event) {
        event.target.src = 'https://goott-bucket.s3.ap-northeast-2.amazonaws.com//noImage.jpg';
    },
    fetchReviewData(){
        axios.get('http://localhost/api/review/store/146')
        .then(response => {
            this.reviewData = response.data.data;
            console.log("reviewData:", this.reviewData);
        })
        .catch(error => {
            console.error('Error fetching ReviewData',error);
        })
    },
    formatDate(dateString) {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = date.getMonth() + 1; // 월은 0부터 시작하므로 +1
        const day = date.getDate();

        return `${year}.${month}.${day}`;
    }
        
  },
  created: function() {
      this.getTodayDay();
      this.fetchRestaurantData(); // 컴포넌트가 생성될 때 데이터 가져오기
  
     
  },
  mounted: function() {
      this.makeACalendar(); // 캘린더 초기화
  }
});