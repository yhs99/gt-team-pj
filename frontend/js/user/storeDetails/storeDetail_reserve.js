new Vue({
    el: '#app',
    data() {
        return {
            storeId:null,
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
            reviewData: [],
            recommendData: {},
            couponData: [],
            info:{
                dateStr:""
            },
            reserveSlots: [],
            selectedSlot: null,
            selectedSlotIndex: null,
            isLoggedIn:false
        };
    },
    computed: {
        allSchedulesOpen() {
            return this.schedules.length > 0 && this.schedules.every(schedule => !schedule.closeDay);
        },
        filteredMenus() {
            if (!this.menuData?.menu) return [];

            const mainMenus = this.menuData.menu.filter(menu => menu.main);
            const remainingMenus = this.menuData.menu.filter(menu => !menu.main);

            return mainMenus.length > 5 ? mainMenus.slice(0, 5) : [...mainMenus, ...remainingMenus].slice(0, 5);
        },
        combinedImages() {
            const reviewImages = this.reviewData.flatMap(review => review.reviewImages);
            const menuImages = this.menuData.menu.map(menu => ({ url: menu.menuImageUrl })).filter(image => image.url);

            return [
                ...this.restaurantData.data.storeImages.map(image => ({ url: image.url })),
                ...reviewImages,
                ...menuImages
            ];
        },
        calculateReviewScore() {
            const scoreSum = this.reviewData.reduce((sum, review) => sum + (review.score || 0), 0);
            return this.reviewData.length > 0 ? scoreSum / this.reviewData.length : 0;
        },
        filteredReviews() {
            return this.reviewData.filter(review => review.score >= 4).sort((a, b) => b.score - a.score);
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
        }
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
                this.operationTodayDay();
                this.fetchMenuData();
                this.fetchReviewData();
                this.fetchRecommendData();
                this.fetchCouponData();
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
        async fetchReviewData() {
            try {
                const response = await axios.get(`/api/review/store/${this.storeId}`);
                this.reviewData = response.data.data;
                console.log("reviewData",this.reviewData);
            } catch (error) {
                console.error('Error fetching review data:', error);
            }
        },
        async fetchRecommendData() {
            try {
                const response = await axios.get(`/api/stores/storeInfo/${this.storeId}`);
                this.recommendData = response.data.data
                console.log("recommendData",this.recommendData);
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
            event.target.src = 'https://goott-bucket.s3.ap-northeast-2.amazonaws.com/noImage.jpg';
        },
        makeACalendar() {
            const calendarEl = document.getElementById('calendar');
            const calendar = new FullCalendar.Calendar(calendarEl, {
                locale: 'ko',
                headerToolbar: {
                    start: 'prev',
                    center: 'title',
                    end: 'next'
                },
                events: [],
                clickedDate: null,
                dateClick:(info) => {
                    const clickedDate = info.date; // 클릭된 날짜
                    const today = new Date(); // 오늘 날짜
                    today.setHours(0, 0, 0, 0); // 시간 초기화 (00:00:00)

                    // // 오늘 날짜로부터 30일 후의 날짜 계산
                    const maxDate = new Date(today);
                    maxDate.setDate(today.getDate() + 30);

                    // 클릭된 날짜가 오늘 이전인지 확인
                    if (clickedDate <= today) {
                        return; // 이전 날짜 클릭 시 아무 동작도 하지 않음
                    } else if (clickedDate > maxDate ){
                        alert('30일 이전의 날짜만 클릭할 수 있습니다.');
                        return;
                    }

                    // alert('Clicked on: ' + info.dateStr);

                    if (this.clickedDate) {
                    const previousDateEl = this.clickedDate;
                    if (previousDateEl) {
                        previousDateEl.style.border = '';
                        previousDateEl.style.backgroundColor = ''; // 기본 색으로 초기화
                    }
                    }
                    info.dayEl.style.border = '3px solid red';
                    info.dayEl.style.marginLeft = '-1px';
                    // 클릭한 날짜를 저장
                    this.clickedDate = info.dayEl;
                    this.fetchReserveSlots(info.dateStr);
                     },
                     datesSet: () => {
                        const today = new Date();
                        today.setHours(0, 0, 0, 0); // 시간 초기화 (00:00:00)
                        console.log("today"+today);
                        const maxDate = new Date(today);
                        maxDate.setDate(today.getDate() + 31); 
            

                         // 오늘 날짜의 배경색 변경
                        const todayStr = today.toISOString().split('T')[0]; // 'YYYY-MM-DD' 형식으로 변환

                        // 모든 날짜에 대해 배경색 변경
                        const dateElements = calendarEl.getElementsByClassName('fc-day');
                        for (let dateEl of dateElements) {
                            const date = new Date(dateEl.getAttribute('data-date'));
                            if (date < today || date > maxDate) {
                                dateEl.style.backgroundColor = '#d3d3d3'; 
                            }   
                        }
                   }
                });
            
            calendar.render();
            
        },
        goToPage(url) {
            window.location.href = url;
        },
        getClosedDays() {
            return this.schedules.filter(schedule => schedule.closeDay).map(schedule => schedule.dayOfWeek);
        },
        async fetchCouponData() {
            try {
                const response = await axios.get(`/api/stores/coupon/${this.storeId}`);
                this.couponData = response.data.data;
                console.log("CouponData",this.couponData);
            } catch (error) {
                console.error('Error fetching couponData:', error);
            }
        },
        async fetchReserveSlots(date) {
            console.log(date);
            try{
                const response = await axios.get(`/api/stores/reserveSlots/${this.storeId}/${date}`)
                this.reserveSlots = response.data.data;
                console.log("reserveSlots",this.reserveSlots);
            }catch(error){
                console.error('Error fetching reserveSlots:', error);
            }

        },
        formatTime(datetime) {
            const date = new Date(datetime);
            const hours = String(date.getHours()).padStart(2, '0'); // 시를 두 자리로 포맷
            const minutes = String(date.getMinutes()).padStart(2, '0'); // 분을 두 자리로 포맷
            return `${hours}:${minutes}`; // 'HH:mm' 형식으로 반환
        },
        handleReservation() {
            if(this.isLoggedIn == true){
                this.logCheckedMenus(); 
            }else{
                this.goToPage(`/view/user/userLogin`);
            }
        },
        logCheckedMenus() {

            
            // 체크된 메뉴 항목만 필터링
            const checkedMenus = this.menuData.menu.filter(menu => menu.quantity > 0);
            
            // 콘솔에 체크된 메뉴 출력
            if (checkedMenus.length > 0 && this.selectedSlot !== null) {
                console.log('체크된 메뉴:');
                checkedMenus.forEach(menu => {
                    console.log(`Menu ID: ${menu.menuId}, Quantity: ${menu.quantity}`);
                    this.insertMenu(menu.menuId,menu.quantity);

                });

                const queryString = `?reserveTime=${this.selectedSlot}`;
                const targetUrl = `http://localhost/view/user/cart${queryString}`;
    
                window.location.href = targetUrl;
            } else {
                alert('메뉴와 시간을 선택해주세요');
            }
            
          
        },
        
        selectSlot(time,index) {
            this.selectedSlot = time;
            this.selectedSlotIndex = index;
            console.log(this.selectedSlot);
        },
        insertMenu(idMenu,qtyMenu){
            const data = {
                storeId: this.storeId,
                menuId: idMenu,
                stock: qtyMenu  
            };
            try {
                const response = axios.post(`/api/cart`,data);
                response.then(res => {
                    console.log('데이터가 성공적으로 추가되었습니다:', res.data);
                }).catch(error => {
                    console.error('데이터 추가 중 오류 발생:', error);
                });
                
            } catch (error) {
                console.error('오류 발생:', error);
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
    updateQuantity(menu) {
        // 체크박스가 체크되어 있으면 quantity를 1로 설정, 아니면 0으로 설정
        menu.quantity = menu.isChecked ? 1 : 0;
    },
},
    created() {
        this.getTodayDay();
       
    },
    mounted() {
        const queryParams = new URLSearchParams(window.location.search);
        this.storeId = queryParams.get('storeId');
        console.log("Store ID:", this.storeId);

        if (this.storeId) {
            this.fetchRestaurantData(); 
        } else {
            console.error('storeId가 없습니다. URL을 확인하세요.');
        }

        this.makeACalendar();
    }
});