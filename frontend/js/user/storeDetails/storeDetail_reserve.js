new Vue({
    el: '#app',
    data() {
        return {
            storeId:null,
            restaurantData: {
                data: {
                    storeImages: [],
                }
            },
            isOpen: false,
            menuData: {
                menu: []
            },
            reserveSlots: [],
            selectedSlot: null,
            selectedSlotIndex: null,
            isLoggedIn:false
        };
    },
   
    methods: {
        async fetchRestaurantData() {
            try {
                const response = await axios.get(`/api/stores/store/${this.storeId}`);
                this.restaurantData = response.data;
                this.checkLoginStatus();
                this.fetchMenuData();
            } catch (error) {
                console.error('Error fetching restaurant data:', error);
            }
        },
        async fetchMenuData() {
            try {
                const response = await axios.get(`/api/stores/menu/${this.storeId}`);
                this.menuData = response.data.data;
            } catch (error) {
                console.error('Error fetching menu data:', error);
            }
        },

        formatDate(dateString) {
            const date = new Date(dateString);
            return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`;
        },
        setDefaultImage(event) {
            event.target.src = 'https://goott-bucket.s3.ap-northeast-2.amazonaws.com//noImage.jpg';
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
   
        async fetchReserveSlots(date) {
            try{
                const response = await axios.get(`/api/stores/reserveSlots/${this.storeId}/${date}`)
                this.reserveSlots = response.data.data;
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
            const queryString = `?reserveTime=${this.selectedSlot}`;
            const targetUrl = `/view/user/cart${queryString}`;
            // 체크된 메뉴 항목만 필터링
            const checkedMenus = this.menuData.menu.filter(menu => menu.quantity > 0);
            
            // 콘솔에 체크된 메뉴 출력
            if (checkedMenus.length > 0 && this.selectedSlot !== null) {
                try {
                axios.get(`/api/cart`)
                .then(async response => {
                    if(response.data.data.length > 0) {
                        await Promise.all(
                            response.data.data.map(cartItem => axios.delete(`/api/cart/${cartItem.cartId}`))
                        );
                    }
                    for(const menu of checkedMenus) {
                        await this.insertMenu(menu.menuId, menu.quantity);
                    }
                    location.href = targetUrl;
                });
                } catch(e) {
                    
                }
            } else {
                alert('메뉴와 시간을 선택해주세요');
            }
            
          
        },
        
        selectSlot(time,index) {
            this.selectedSlot = time;
            this.selectedSlotIndex = index;
        },
        insertMenu: async function(idMenu,qtyMenu){
            const data = {
                storeId: this.storeId,
                menuId: idMenu,
                stock: qtyMenu  
            };
            try {
                return axios.post(`/api/cart`, data)
                .then(response => {
                    console.log(response.status === 200 ? data : '')
                })
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
                    this.isLoggedIn =true;
                }
            } catch (error) {
                if (error.response && error.response.status === 401) {
                    this.isLoggedIn = false;
                } else {
                    console.error('로그인 상태 확인 중 오류 발생:', error);
                }
            }
    },
    updateQuantity(menu) {
        menu.quantity = menu.isChecked ? 1 : 0;
    },
},

    mounted() {
        const queryParams = new URLSearchParams(window.location.search);
        this.storeId = queryParams.get('storeId');

        if (this.storeId) {
            this.fetchRestaurantData(); 
        } else {
            console.error('잘못된 매장입니다.');
            window.history.back();
        }

        this.makeACalendar();
    }
});