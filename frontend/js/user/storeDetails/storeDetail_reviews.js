new Vue({
    el: '#app',
    data: {
        restaurantData: {
            data: {
                storeImages: []
            }
        },
        reviewData: []
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
            axios.get('http://localhost/api/stores/store/146')
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
            axios.get('http://localhost/api/review/store/146')
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
            event.target.src = 'https://goott-bucket.s3.ap-northeast-2.amazonaws.com/noImage.jpg'; 
        }
    },
    mounted() {
        this.fetchRestaurantData(); // 컴포넌트가 마운트되면 데이터 fetch
    }
});