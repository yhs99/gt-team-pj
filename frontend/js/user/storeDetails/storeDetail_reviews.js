new Vue({
    el: '#app',
    data: {
        storeId: null,
        restaurantData: {
            data: {
                storeImages: []
            }
        },
        reviewData: [],
        review: {
            reviewImages: [] 
        },
        buttons: ['홈', '메뉴', '사진', '리뷰', '매장정보'],
        activeButton: 3,
    },
    computed: { 
        calculateReviewScore() {
            let scoreSum = 0;
            let scoreAvg = 0;
            for (let review of this.reviewData) {
                scoreSum += review.score || 0; // score가 undefined일 경우 0 처리
            }
            if (this.reviewData.length > 0) {
                scoreAvg = scoreSum / this.reviewData.length;
            }
            return scoreAvg;
        }
    },
    methods: {
        fetchRestaurantData() {
            axios.get(`http://localhost/api/stores/store/${this.storeId}`)
                .then(response => {
                    this.restaurantData = response.data;
                    console.log("restaurantData:", this.restaurantData);
                    this.fetchReviewData(); // 리뷰 데이터를 가져옴
                })
                .catch(error => {
                    console.error('Error fetching restaurantData:', error);
                });
        },
        fetchReviewData() {
            axios.get(`http://localhost/api/review/store/${this.storeId}`)
                .then(response => {
                    console.log("API 응답:", response.data); // 응답 로그
                    this.reviewData = response.data.data; // 데이터 구조 확인
                    console.log("reviewData:", this.reviewData);
                })
                .catch(error => {
                    console.error('Error fetching ReviewData', error);
                });
        },
        formatDate(dateString) {
            const date = new Date(dateString);
            const year = date.getFullYear();
            const month = date.getMonth() + 1; // 월은 0부터 시작하므로 +1
            const day = date.getDate();
            return `${year}.${month}.${day}`;
        },
        countScore(n) {
            return this.reviewData.filter(review => review.score === n).length;
        },
        calculateScoreRatio(n){
            return (this.countScore(n)/this.reviewData.length)*100;
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
                    this.goToPage(`storeDetails/menu?storeId=${this.storeId}`)
                    break;
                case 2:
                    this.goToPage(`storeDetails/pictures?storeId=${this.storeId}`)
                    break;
                case 3:
                    this.goToPage(`#`)
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
        console.log("Store ID:", this.storeId);

        if (this.storeId) {
            this.fetchRestaurantData(); 
        } else {
            console.error('storeId가 없습니다. URL을 확인하세요.');
        }

    }
});