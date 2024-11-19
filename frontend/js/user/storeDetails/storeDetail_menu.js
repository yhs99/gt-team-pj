
new Vue({
    el: '#app',
    data() {
        return {
            storeId: null,
            restaurantData: {
                data: {
                }
            },
            menuData: {
                menu: []
            },
            info:{
                dateStr:""
            },
            buttons: ['홈', '메뉴', '사진', '리뷰', '매장정보'],
            activeButton: 1,

        };
    },
    methods: {
        async fetchRestaurantData() {
            try {
                const response = await axios.get(`/api/stores/store/${this.storeId}`);
                this.restaurantData = response.data;
                this.schedules = this.restaurantData.data.storeSchedules;

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
        setDefaultImage(event) {
            event.target.src = 'https://goott-bucket.s3.ap-northeast-2.amazonaws.com//noImage.jpg';
        },
        
        goToPage(url) {
            window.location.href = `/view/user/${url}`;
        },
       
        menuBtnGotopage(index) {
            //  this.activeButton = index;
            switch (index) {
                case 0:
                    this.goToPage(`storeDetail?storeId=${this.storeId}`)
                    break;
                case 1:
                    this.goToPage(`#`)
                    break;
                case 2:
                    this.goToPage(`storeDetails/pictures?storeId=${this.storeId}`)
                    break;
                case 3:
                    this.goToPage(`storeDetails/reviews?storeId=${this.storeId}`)
                    break;
                case 4:
                    this.goToPage(`storeDetails/storeInfo?storeId=${this.storeId}`)
                break;
                default:
                    break;
            }
    
        },
      
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