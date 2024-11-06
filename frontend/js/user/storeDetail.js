new Vue({
  el: '#app', // Vue 인스턴스가 연결될 HTML 요소
  data: {
      restaurantData: {
          data: [] // 초기값을 빈 배열로 설정
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

          // let lat = this.restaurantData.data[0].locationLatY;
          // let lon = this.restaurantData.data[0].locationLonY;
          let lat = 127.378958
          let lon = 36.385518

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

      fetchRestaurantData() {
          // REST API 호출
          axios.get('http://localhost/api/stores/29') // API URL을 입력하세요
              .then(response => {
                  this.restaurantData = response.data; // 응답 데이터를 restaurantData에 저장
                  console.log(this.restaurantData); // 가져온 데이터 출력

                  // storeName 접근
                  if (this.restaurantData.data && this.restaurantData.data.length > 0) {
                      this.$nextTick(() => {
                        this.kakaoMap(); // 데이터가 로드된 후 DOM 업데이트가 완료된 후 지도를 초기화
                    });
                  } else {
                      console.error('No restaurant data found');
                  }
              })
              .catch(error => {
                  console.error('Error fetching data:', error);
              });
      }
  },
  created: function() {
      this.fetchRestaurantData(); // 컴포넌트가 생성될 때 데이터 가져오기
  },
  mounted: function() {
      this.makeACalendar(); // 캘린더 초기화
  }
});