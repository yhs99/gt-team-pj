
new Vue({
    el: '#app',
    data() {
        return {
            storeId: null,
            restaurantData: {
                data: {
                    storeImages: [],
                    maxPeoplePerReserve:""
                }
            },
            todayDay: "",
            todayOperation: {},
            schedules: [],
            isOpen: false,
            menuData: {
                menu: []
            },
            reviewData: {},
            recommendData: [],
            page: 1,
            size: 10,
            info:{
                dateStr:""
            },
            isBookmarked: false,
            currentBookmark:[],
            loginInfo:{},
            isLoggedIn:false,
            isLoading: false,
        };
    },
    computed: {
        totalPages(){
            return Math.ceil(this.recommendData.length / this.size);
        },
        pagination(){
            const start = (this.page - 1) * this.size;
            const end = start + this.size;
            console.log("storeId Aru?",this.recommendData.slice(0,end))
            return this.recommendData.slice(0, end);
        },
        allSchedulesOpen() {
            return this.schedules.length > 0 && this.schedules.every(schedule => !schedule.closeDay);
        },
      
        calculateMenuPriceAvg() {
            const mainMenus = this.menuData.menu.filter(menu => menu.main);
            const menuMainPriceSum = mainMenus.reduce((sum, menu) => sum + menu.price, 0);
            const menuMainPriceAvg = mainMenus.length > 0 ? menuMainPriceSum / mainMenus.length : 0;

            const priceRanges = [
                { min: 0, max: 20000, label: "1만원 미만 ~ 2만원" },
                { min: 20000, max: 30000, label: "1만원 ~ 3만원" },
                { min: 30000, max: 40000, label: "2만원 ~ 4만원" },
                { min: 40000, max: 50000, label: "3만원 ~ 5만원" },
                { min: 50000, max: 60000, label: "4만원 ~ 6만원" },
                { min: 60000, max: 70000, label: "5만원 ~ 7만원" },
                { min: 70000, max: 80000, label: "6만원 ~ 8만원" },
                { min: 80000, max: 90000, label: "7만원 ~ 9만원" },
                { min: 90000, max: 100000, label: "8만원 ~ 10만원" },
                { min: 100000, label: "10만원 이상" }
            ];

            return priceRanges.find(range => menuMainPriceAvg >= range.min && (!range.max || menuMainPriceAvg < range.max)).label;
        },
    },
    methods: {
        async fetchRestaurantData() {
            try {
                const response = await axios.get(`/api/stores/store/${this.storeId}`);
                this.restaurantData = response.data;
                console.log("restaurantData",this.restaurantData);
                this.schedules = this.restaurantData.data.storeSchedules;
                console.log("schedules",this.schedules);
                this.checkLoginStatus();
                // this.operationTodayDay();
                // this.fetchMenuData();
                // this.fetchReviewData();
                this.fetchRecommendData();
                
            } catch (error) {
                console.error('Error fetching restaurant data:', error);
            }
        },
        async fetchMenuData() {
            try {
                const response = await axios.get(`/api/stores/menu/${this.storeId}`);
                this.menuData = response.data.data;
                console.log("menuData",this.menuData);
            } catch (error) {
                console.error('Error fetching menu data:', error);
            }
        },
        async fetchReviewData(recommendId) {
            try {
                console.log("recommendId",recommendId);
                const response = await axios.get(`/api/review/store/${recommendId}`);
                this.$set(this.reviewData, recommendId, response.data.data);
                console.log("reviewData",this.reviewData);
            } catch (error) {
                console.error('Error fetching review data:', error);
            }
        },
        async fetchRecommendData() {
            try {
                const response = await axios.get(`/api/stores/storeInfo/${this.storeId}`);
                this.recommendData = this.recommendData.concat(response.data.data);
                console.log("recommendData",this.recommendData);
                for(recommendStore of this.recommendData){
                    this.fetchReviewData(recommendStore.storeId);
                }
            } catch (error) {
                console.error('Error fetching recommend data:', error);
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
       
        formatDate(dateString) {
            const date = new Date(dateString);
            return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`;
        },
        setDefaultImage(event) {
            console.log("여기까지 오나요");
            event.target.src = 'https://goott-bucket.s3.ap-northeast-2.amazonaws.com//noImage.jpg';
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
      
        async setBookmark(){
           try{
               const response = await axios.post(`/api/bookmark/${this.storeId}`);
               console.log(response);
               this.isBookmarked = true;
           } catch(error){
               console.error('북마크 추가 중 오류발생:',error);
           }
            
        },
        async deleteBookmark(){
            try{
                const response = await axios.delete(`/api/bookmark/${this.storeId}`);
                console.log(response);
                this.isBookmarked = false;
            }catch(error){
                console.error('북마크 삭제 에러',error)
            }
        },
        toggleBookmark(){
          
            if(this.isLoggedIn == true){
                if(this.isBookmarked == false){
                    this.setBookmark();
                }else{
                    this.deleteBookmark();
                }
            }else{
                this.goToPage(`/view/user/userLogin`);
            }
        },
        async getBookmarkInfo(){
            console.log("enterGetBookmarkInfo",this.isLoggedIn);
            if(this.isLoggedIn == true){
                try {
                    const response = await axios.get(`/api/bookmark/`);
                    this.currentBookmark = response.data.data;
                    console.log("currentBookmark", this.currentBookmark);
                    this.checkIfBookmarked();
                } catch (error) {
                    
                        console.error('북마크 가져오기 에러:', error);
                }
            }
        },
        
        checkIfBookmarked(){
            for(bookmark of this.currentBookmark){
                console.log(bookmark.bookmarkDto.storeId,this.storeId);
                if(bookmark.bookmarkDto.storeId == this.storeId){
                    this.isBookmarked = true;
                    console.log("isBookmarked",this.isBookmarked);
                    break;
                }else{

                    console.log("isBookmarked",this.isBookmarked);
                }
            }
        },
       
        async checkLoginStatus() {
            try {
                const response = await axios.get('/api/status'); 
                if (response.status === 401) {
                    this.isLoggedIn = false;
                    
                } else if (response.status === 200) {
                    console.log("로그인 완료");
                    this.isLoggedIn =true;
                    this.getBookmarkInfo();
                    console.log("isLoggedIn", this.isLoggedIn);

                }
            } catch (error) {
                if (error.response && error.response.status === 401) {
                    this.isLoggedIn = false;
                    console.log("isLoggedIn", this.isLoggedIn);
                } else {
                    console.error('로그인 상태 확인 중 오류 발생:', error);
                }
            }
        },
    
          scrolling() {
            const scrollHeight = window.scrollY + window.innerHeight; // 현재 스크롤 위치 + 창 높이
            const totalHeight = document.body.scrollHeight; // 문서 전체 높이
            console.log("scrolling")
            if (scrollHeight >= totalHeight - 5 && !this.isLoading) {
                this.loadMore();
            }
        },
        async loadMore() {
            if (this.page < this.totalPages) {
                this.isLoading = true; // 로딩 시작
                this.page++;
                console.log(this.page);
                await this.fetchRecommendData();
                this.isLoading = false; // 로딩 종료
            }
        },
        goToStore(recStoreId) {
            window.location.href = `../storeDetail?storeId=${recStoreId}`;
          },
        calculateReviewScore(storeId) {
        const reviews = this.reviewData[storeId] || [];
        const scoreSum = reviews.reduce((sum, review) => sum + (review.score || 0), 0);
        return reviews.length > 0 ? scoreSum / reviews.length : 0;
        },
       
},

    created() {
        this.getTodayDay();
        window.addEventListener('scroll', this.scrolling); 
    },
    destroyed() {
        window.removeEventListener('scroll', this.scrolling); // 컴포넌트가 파괴될 때 리스너 제거
    },
    mounted() {
        const queryParams = new URLSearchParams(window.location.search);
        this.storeId = queryParams.get('storeId');
        console.log("Store ID:", this.storeId);

        if (this.storeId) {
            this.fetchRestaurantData(); 
            this.fetchRestaurantData();
        } else {
            console.error('storeId가 없습니다. URL을 확인하세요.');
        }

    }
});