new Vue({
        el: '#app', // Vue 인스턴스가 연결될 HTML 요소
        data: {
            title: 'Vue.js 기본 예제', // 제목
            message: '', // 사용자가 입력하는 메시지
            submittedMessage: '' // 전송된 메시지
        },
        methods: {
            makeACalendar() {
                document.addEventListener('DOMContentLoaded', function() {
                        const calendarEl = document.getElementById('calendar')
                        const calendar = new FullCalendar.Calendar(calendarEl, {
                          initialView: 'dayGridMonth'
                        })
                        calendar.render()
                      })
            },

            goToPage(url) {
                const defaultUrl = '기본페이지.html';
                window.location.href = url || defaultUrl;
           },

           kakaoMap() {
            
                  var mapContainer = document.getElementById('map'); // 지도를 표시할 div 
                  if (!mapContainer) {
                    console.error('Map container not found');
                    return;
                }

                  mapOption = { 
                      center: new kakao.maps.LatLng(33.450701, 126.570667), // 지도의 중심좌표
                      level: 3 // 지도의 확대 레벨
                  };

                  var map = new kakao.maps.Map(mapContainer, mapOption); // 지도를 생성합니다

                  // 마커가 표시될 위치입니다 
                  var markerPosition  = new kakao.maps.LatLng(33.450701, 126.570667); 

                  // 마커를 생성합니다
                  var marker = new kakao.maps.Marker({
                      position: markerPosition
                  });

                  // 마커가 지도 위에 표시되도록 설정합니다
                  marker.setMap(map);
           },
           fetchRestaurantData() {
            // REST API 호출
            axios.get('https://api.example.com/restaurant') // API URL을 입력하세요
                .then(response => {
                    this.restaurantData = response.data; // 데이터를 Vue 인스턴스의 데이터로 저장
                    console.log(this.restaurantData); // 가져온 데이터 출력
                })
                .catch(error => {
                    console.error('Error fetching data:', error);
                });
        }


        },
        created: function() {
          
        },
        mounted: function(){
          this.makeACalendar();
            // setTimeout을 사용하여 DOM이 완전히 로드된 후 호출
          setTimeout(() => {
            this.kakaoMap();
          }, 100); // 100ms 지연

        }

});




   