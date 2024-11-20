new Vue({
    el: '#app',
    data: {
        storeId: null,
        page: 1,
        size: 10,
        sort: 'latest',
        reviewData: [],
        allReviews: [],
        review: {
            reviewImages: [] 
        },
        buttons: ['홈', '메뉴', '사진', '리뷰', '매장정보'],
        activeButton: 3,
        loading: false, 
        hasMoreData: true,
    },
    computed: { 
        calculateReviewScore() {
            let scoreSum = 0;
            let scoreAvg = 0;
            if (this.allReviews.length > 0) {
                for (let review of this.allReviews) {
                    scoreSum += review.score || 0; 
                }
                scoreAvg = scoreSum / this.allReviews.length;
            }
            return scoreAvg;
        }
    },
    methods: {
        fetchAllReviews() {
            axios.get(`/api/review/store/${this.storeId}`, {
                params: {
                    size: 500 
                }
            })
            .then(response => {
                this.allReviews = response.data.data; // 모든 리뷰 저장
                console.log("All Reviews:", this.allReviews);
            })
            .catch(error => {
                console.error('Error fetching all reviews', error);
            });
        },
        fetchReviewData() {
            if (this.loading || !this.hasMoreData) return; // 로딩 중이면 요청을 중단
            this.loading = true;

            axios.get(`/api/review/store/${this.storeId}`,{
                params: {
                    page: this.page,
                    size: this.size,
                    sort: this.sort
                }
            })
            .then(response => {
                console.log("API 응답:", response.data);
                if (response.data.data.length === 0) {
                    this.hasMoreData = false; // 더 이상 데이터가 없으면 플래그를 false로 변경
                } else {
                    this.reviewData = this.reviewData.concat(response.data.data); // 기존 데이터와 새 데이터 병합
                }
                console.log("reviewData:", this.reviewData);
            })
            .catch(error => {
                console.error('Error fetching ReviewData', error);
            })
            .finally(() => {
                this.loading = false; 
            });
        },
        updateSort(event) {
            const selectedValue = event.target.value;
            switch (selectedValue) {
                case 'latest':
                    this.sort = 'latest'; 
                    break;
                case 'high':
                    this.sort = 'score_desc'; 
                    break;
                case 'low':
                    this.sort = 'score_asc'; 
                    break;
            }
            this.page = 1; // 페이지 초기화
            this.reviewData = []; // 리뷰 데이터 초기화
            this.hasMoreData = true; 
            this.fetchReviewData(); // 정렬 변경 시 리뷰 데이터 재요청
        },
        handleScroll() {
            const scrollHeight = window.scrollY + window.innerHeight;
            const offsetHeight = document.body.offsetHeight;

            // 스크롤이 바닥에 거의 도달했을 때
            if (!this.loading && this.hasMoreData && scrollHeight >= offsetHeight - 100) {
                this.page++; // 다음 페이지로 증가
                this.fetchReviewData(); // 다음 페이지 데이터 요청
            }
        },
        formatDate(dateString) {
            const date = new Date(dateString);
            const year = date.getFullYear();
            const month = date.getMonth() + 1; // 월은 0부터 시작하므로 +1
            const day = date.getDate();
            return `${year}.${month}.${day}`;
        },
        countScore(n) {
            return this.allReviews.filter(review => review.score === n).length;
        },
        calculateScoreRatio(n){
            return (this.countScore(n)/this.allReviews.length)*100;
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

        if (this.storeId) {
            this.fetchAllReviews();
            this.fetchReviewData();
            window.addEventListener('scroll', this.handleScroll); // 스크롤 이벤트 리스너 추가
        } else {
            console.error('storeId가 없습니다. URL을 확인하세요.');
        }
    },
    beforeDestroy() {
        window.removeEventListener('scroll', this.handleScroll); // 컴포넌트가 파괴될 때 이벤트 리스너 제거
    }
});