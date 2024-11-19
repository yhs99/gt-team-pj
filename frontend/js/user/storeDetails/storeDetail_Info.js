
new Vue({
    el: '#app',
    data() {
        return {
            storeId: null,
            restaurantData: {
                data: {}
            },
            todayDay: "",
            todayOperation: {},
            schedules: [],
            isOpen: false,
            menuData: {
                menu: []
            },
            info:{
                dateStr:""
            },
            buttons: ['홈', '메뉴', '사진', '리뷰', '매장정보'],
            activeButton: 4,
        };
    },
    computed: {
        allSchedulesOpen() {
            return this.schedules.length > 0 && this.schedules.every(schedule => !schedule.closeDay);
        },
    },
    methods: {
        async fetchRestaurantData() {
            try {
                const response = await axios.get(`/api/stores/store/${this.storeId}`);
                this.restaurantData = response.data;
                this.schedules = this.restaurantData.data.storeSchedules;
                this.operationTodayDay();
                this.kakaoMap();
                             
            } catch (error) {
                console.error('Error fetching restaurant data:', error);
            }
        },

        getTodayDay() {
            const days = ["일요일", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일"];
            const today = new Date();
            this.todayDay = days[today.getDay()];
        },
        operationTodayDay() {
            const schedule = this.restaurantData.data.storeSchedules;
            this.todayOperation = schedule.find(s => s.dayOfWeek === this.todayDay) || {};
        },
        kakaoMap() {
            const { locationLatX: lat, locationLonY: lon } = this.restaurantData.data;
            const mapContainer = document.getElementById('map');
            const mapOption = { center: new kakao.maps.LatLng(lon, lat), level: 3 };
            const map = new kakao.maps.Map(mapContainer, mapOption);
            const marker = new kakao.maps.Marker({ position: new kakao.maps.LatLng(lon, lat) });
            marker.setMap(map);
        },
        formatDate(dateString) {
            const date = new Date(dateString);
            return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`;
        },

        goToPage(url) {
            window.location.href = `/view/user/${url}`;
        },
        getClosedDays() {
            return this.schedules.filter(schedule => schedule.closeDay).map(schedule => schedule.dayOfWeek);
        },

        formatTime(datetime) {
            const date = new Date(datetime);
            const hours = String(date.getHours()).padStart(2, '0'); // 시를 두 자리로 포맷
            const minutes = String(date.getMinutes()).padStart(2, '0'); // 분을 두 자리로 포맷
            return `${hours}:${minutes}`; // 'HH:mm' 형식으로 반환
        },  
        menuBtnGotopage(index) {
            //  this.activeButton = index;
            switch (index) {
                case 0:
                    this.goToPage(`storeDetail?storeId=${this.storeId}`)
                    break;
                case 1:
                    this.goToPage(`storeDetails/menu?storeId=${this.storeId}`)
                    break;
                case 2:
                    this.goToPage(`storeDetails/pictures?storeId=${this.storeId}`)
                    break;
                case 3:
                    this.goToPage(`storeDetails/reviews?storeId=${this.storeId}`)
                    break;
                case 4:
                    this.goToPage(`#`)
                break;
                default:
                    break;
            }
    
        }
      
    },

    created() {
        this.getTodayDay();
    
    },
    mounted() {
        const queryParams = new URLSearchParams(window.location.search);
        this.storeId = queryParams.get('storeId');

        if (this.storeId) {
            this.fetchRestaurantData(); 
        } else {
            console.error('storeId가 없습니다. URL을 확인하세요.');
        }

    }
});