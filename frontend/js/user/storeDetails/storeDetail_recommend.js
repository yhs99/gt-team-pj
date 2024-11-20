
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
            return this.recommendData.slice(0, end);
        },
      
    },
    methods: {
        async fetchRestaurantData() {
            try {
                const response = await axios.get(`/api/stores/store/${this.storeId}`);
                this.restaurantData = response.data;
                this.schedules = this.restaurantData.data.storeSchedules;
                this.checkLoginStatus();
                this.fetchRecommendData();
                
            } catch (error) {
                console.error('Error fetching restaurant data:', error);
            }
        },
        async fetchReviewData(recommendId) {
            try {
                const response = await axios.get(`/api/review/store/${recommendId}`);
                this.$set(this.reviewData, recommendId, response.data.data);
            } catch (error) {
                console.error('Error fetching review data:', error);
            }
        },
        async fetchRecommendData() {
            try {
                const response = await axios.get(`/api/stores/storeInfo/${this.storeId}`);
                const filteredData = response.data.data.filter(recommend => !recommend.blocked);
                   // 중복된 storeId를 가진 추천 데이터를 필터링
                const newRecommendData = filteredData.filter(recommend => 
                    !this.recommendData.some(existing => existing.storeId === recommend.storeId)
                );

                // 필터링된 데이터만 추가
                this.recommendData = this.recommendData.concat(newRecommendData);

                // 모든 추천 데이터에 isBookmarked 속성 초기화
                this.recommendData.forEach(recommend => {
                    this.$set(recommend, 'isBookmarked', false); // 초기값 설정
                });

                for (const recommendStore of newRecommendData) {
                    this.fetchReviewData(recommendStore.storeId);
                }

                this.getBookmarkInfo();
            } catch (error) {
                console.error('Error fetching recommend data:', error);
            }
        },
      
        async setBookmark(favoriteId) {
            try {
              const response = await axios.post(`/api/bookmark/${favoriteId}`);
              return true;
            } catch (error) {
              console.error("북마크 추가 중 오류발생:", error);
              return false;
            }
          },
      
          async deleteBookmark(favoriteId) {
            try {
              const response = await axios.delete(`/api/bookmark/${favoriteId}`);
              return true;
            } catch (error) {
              console.error("북마크 삭제 에러", error);
              return false;
            }
          },
          async toggleBookmark(storeId) {
            if (this.isLoggedIn) {
                const recommend = this.recommendData.find(recommend => recommend.storeId === storeId);
                const isBookmarked = recommend?.isBookmarked;
        
                // UI 업데이트: 북마크 상태를 즉시 반영
                this.updateBookmarkStatus(storeId, !isBookmarked);
        
                try {
                    if (isBookmarked) {
                        await this.deleteBookmark(storeId);
                    } else {
                        await this.setBookmark(storeId);
                    }
                } catch (error) {
                    // API 호출이 실패한 경우, 이전 상태로 되돌리기
                    this.updateBookmarkStatus(storeId, isBookmarked);
                    console.error("북마크 처리 중 오류 발생:", error);
                }
            } else {
                this.goToPage(`/view/user/userLogin`);
            }
        },
      
          updateBookmarkStatus(storeId, isFavorite) {
            // 추천 데이터에서 해당 storeId의 isBookmarked 속성을 업데이트
            const recommend = this.recommendData.find(recommend => recommend.storeId === storeId);
            if (recommend) {
              this.$set(recommend, 'isBookmarked', isFavorite);
            }
          },
      
          async getBookmarkInfo() {
            if (this.isLoggedIn) {
              try {
                const response = await axios.get(`/api/bookmark/`);
                this.currentBookmark = response.data.data;
                this.checkIfBookmarked();
              } catch (error) {
                console.error("북마크 가져오기 에러:", error);
              }
            }
          },
      
          checkIfBookmarked() {
            this.recommendData.forEach(recommend => {
              recommend.isBookmarked = this.currentBookmark.some(bookmark => bookmark.bookmarkDto.storeId === recommend.storeId);
            });
          },
    
        async checkLoginStatus() {
            try {
                const response = await axios.get('/api/status'); 
                if (response.status === 401) {
                    this.isLoggedIn = false;
                    
                } else if (response.status === 200) {
                    this.isLoggedIn =true;
                    this.getBookmarkInfo();

                }
            } catch (error) {
                if (error.response && error.response.status === 401) {
                    this.isLoggedIn = false;
                } else {
                    console.error('로그인 상태 확인 중 오류 발생:', error);
                }
            }
        },
    
          scrolling() {
            const scrollHeight = window.scrollY + window.innerHeight; // 현재 스크롤 위치 + 창 높이
            const totalHeight = document.body.scrollHeight; // 문서 전체 높이
            if (scrollHeight >= totalHeight - 5 && !this.isLoading) {
                this.loadMore();
            }
        },
        async loadMore() {
            if (this.page < this.totalPages && !this.isLoading) {
                this.isLoading = true; // 로딩 시작
                this.page++;
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
        goToPage(url) {
            window.location.href = url;
          },
       
},

    created() {
        window.addEventListener('scroll', this.scrolling); 
    },
    destroyed() {
        window.removeEventListener('scroll', this.scrolling); // 컴포넌트가 파괴될 때 리스너 제거
    },
    mounted() {
        const queryParams = new URLSearchParams(window.location.search);
        this.storeId = queryParams.get('storeId');

        if (this.storeId) {
            this.fetchRestaurantData(); 
            this.fetchRestaurantData();
        } else {
            console.error('storeId가 없습니다. URL을 확인하세요.');
        }

    }
});