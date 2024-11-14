new Vue({
    el: '#app', // Vue 인스턴스가 연결될 HTML 요소
    data() {
        return {
            restaurantData: {
                data: {
                    storeImages: []
                }
            },
            activeTab: '전체', // 기본 활성화된 탭
            menuData: {
                menu: []
            },
            reviewData: [],
           
        };
    },
    computed:{

      combinedImages() {
        // storeImages와 reviewImages, menuImages를 합치기
        const reviewImages = this.reviewData.flatMap(review => review.reviewImages); // 리뷰 이미지를 객체 배열로 변환
        const menuImages = this.menuData.menu.map(menu => ({ url: menu.menuImageUrl })).filter(image => image.url); // 메뉴 이미지를 객체 배열로 변환

        return [
            ...this.restaurantData.data.storeImages.map(image => ({ url: image.url })), // storeImages에서 URL을 객체 배열로 변환
            ...reviewImages,
            ...menuImages
        ];
      },
      reviewImages(){
        return this.reviewData.flatMap(review => review.reviewImages);
      }
    },
    methods: {
    
        fetchRestaurantData() {
            axios.get('http://localhost/api/stores/store/146') // API URL을 입력하세요
                .then(response => {
                    this.restaurantData = response.data; // 응답 데이터를 restaurantData에 저장
                    this.schedules = this.restaurantData.data.storeSchedules;
                    console.log("schedules:",this.schedules);
                    console.log(this.restaurantData); // 가져온 데이터 출력
                    console.log(this.restaurantData.data.storeImages);
                    // storeName 접근
                    if (this.restaurantData && this.restaurantData.data) {
                        this.$nextTick(() => {
                          this.fetchMenuData();
                          this.fetchReviewData();
                      });
                    } else {
                        console.error('No restaurant data found');
                    }
                })
                .catch(error => {
                    console.error('Error fetching restaurantData:', error);
                });
        },
   
       fetchMenuData() {
          axios.get('http://localhost/api/stores/menu/30')
              .then(response => {
                  this.menuData = response.data.data;
                  console.log("menuData:", this.menuData.menu[0].menuImageUrl);
              })
              .catch(error => {
                  console.error('Error fetching MenuData',error);
              })
      },
      setDefaultImage(event) {
          event.target.src = 'https://goott-bucket.s3.ap-northeast-2.amazonaws.com//noImage.jpg';
      },
      fetchReviewData(){
          axios.get('http://localhost/api/review/store/146')
          .then(response => {
              this.reviewData = response.data.data;
              console.log("reviewData:", this.reviewData);
          })
          .catch(error => {
              console.error('Error fetching ReviewData',error);
          })
      },
      setActiveTab(tab) {
        this.activeTab = tab; // 클릭된 탭으로 활성화된 탭 변경
    }
    
    },
    created() {
        this.fetchRestaurantData(); // 컴포넌트가 생성될 때 데이터 가져오기
    
       
    }
  });