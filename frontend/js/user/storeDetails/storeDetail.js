

new Vue({
  el: "#app",
  data() {
    return {
      storeId: null,
      restaurantData: {
        data: {
          storeImages: [],
          maxPeoplePerReserve: "",
        },
      },
      todayDay: "",
      todayOperation: {},
      schedules: [],
      isOpen: false,
      menuData: {
        menu: [],
      },
      reviewData: [],
      recommendData: {},
      storeReview: {},
      couponData: [],
      info: {
        dateStr: "",
      },
      reserveSlots: {},
      buttons: ["홈", "메뉴", "사진", "리뷰", "매장정보"],
      activeButton: 0,
      isBookmarked: false,
      currentBookmark: [],
      loginInfo: {},
      isLoggedIn: false,
      reviewCount: 0,
      recommendStoreIds: [],
      averageScoreList : []
    };
  },
  computed: {
    allSchedulesOpen() {
      return (
        this.schedules.length > 0 &&
        this.schedules.every((schedule) => !schedule.closeDay)
      );
    },
    filteredMenus() {
      if (!this.menuData?.menu) return [];

      const mainMenus = this.menuData.menu.filter((menu) => menu.main);
      const remainingMenus = this.menuData.menu.filter((menu) => !menu.main);

      return mainMenus.length > 5
        ? mainMenus.slice(0, 5)
        : [...mainMenus, ...remainingMenus].slice(0, 5);
    },
    combinedImages() {
      const reviewImages = this.reviewData.flatMap(
        (review) => review.reviewImages
      );
      const menuImages = this.menuData.menu
        .map((menu) => ({ url: menu.menuImageUrl }))
        .filter((image) => image.url);

      return [
        ...this.restaurantData.data.storeImages.map((image) => ({
          url: image.url,
        })),
        ...reviewImages,
        ...menuImages,
      ];
    },
    calculateReviewScore() {
      const scoreSum = this.reviewData.reduce(
        (sum, review) => sum + (review.score || 0),
        0
      );
      return this.reviewData.length > 0 ? scoreSum / this.reviewData.length : 0;
    },
    filteredReviews() {
      return this.reviewData
        .filter((review) => review.score >= 4)
        .sort((a, b) => new Date(b.createAt) - new Date(a.createAt));
    },
    calculateMenuPriceAvg() {
      const mainMenus = this.menuData.menu.filter((menu) => menu.main);
      const menuMainPriceSum = mainMenus.reduce(
        (sum, menu) => sum + menu.price,
        0
      );
      const menuMainPriceAvg =
        mainMenus.length > 0 ? menuMainPriceSum / mainMenus.length : 0;

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
        { min: 100000, label: "10만원 이상" },
      ];

      return priceRanges.find(
        (range) =>
          menuMainPriceAvg >= range.min &&
          (!range.max || menuMainPriceAvg < range.max)
      ).label;
    },
  },
  methods: {
    async fetchRestaurantData() {
      try {
        const response = await axios.get(`/api/stores/store/${this.storeId}`);
        this.restaurantData = response.data;
        this.schedules = this.restaurantData.data.storeSchedules;
        this.checkLoginStatus();
        this.operationTodayDay();
        this.fetchMenuData();
        this.fetchReviewData();
        this.kakaoMap();
        this.fetchRecommendData();
      } catch (error) {
        console.error("Error fetching restaurant data:", error);
      }
    },
    async fetchMenuData() {
      try {
        const response = await axios.get(`/api/stores/menu/${this.storeId}`);
        this.menuData = response.data.data;
      } catch (error) {
        console.error("Error fetching menu data:", error);
      }
    },
    async fetchReviewData() {
      try {
        const response = await axios.get(`/api/review/store/${this.storeId}`,{
          params:{
              size:500
          }
        });
        this.reviewData = response.data.data;
      } catch (error) {
        console.error("Error fetching review data:", error);
      }
    },
    async fetchRecommendData() {
      try {
        const response = await axios.get(`/api/stores/storeInfo/${this.storeId}`);
          // blocked가 false인 객체들만 필터링
          const filteredData = response.data.data.filter(recommend => !recommend.blocked);
        
          // 필터링된 데이터에서 최대 3개 가져오기
        this.recommendData = filteredData.slice(0, 3);
        this.recommendStoreIds = this.recommendData.map(recommend => recommend.storeId);
        
        await Promise.all(this.recommendStoreIds.map((recommendStoreId, index) => 
          this.fetchStoreReviewData(recommendStoreId, index)
        ));
        
        // 초기에 bookmark 상태를 확인
        this.checkRecommendBookmarked();
      } catch (error) {
        console.error("Error fetching recommend data:", error);
      }
    },
    getTodayDay() {
      const days = [
        "일요일",
        "월요일",
        "화요일",
        "수요일",
        "목요일",
        "금요일",
        "토요일",
      ];
      const today = new Date();
      this.todayDay = days[today.getDay()];
    },
    operationTodayDay() {
      const schedule = this.restaurantData.data.storeSchedules;
      this.todayOperation =
        schedule.find((s) => s.dayOfWeek === this.todayDay) || {};
    },
    kakaoMap() {
      const { locationLatX: lat, locationLonY: lon } = this.restaurantData.data;
      const mapContainer = document.getElementById("map");
      const mapOption = { center: new kakao.maps.LatLng(lon, lat), level: 3 };
      const map = new kakao.maps.Map(mapContainer, mapOption);
      const marker = new kakao.maps.Marker({
        position: new kakao.maps.LatLng(lon, lat),
      });
      marker.setMap(map);
    },
    formatDate(dateString) {
      const date = new Date(dateString);
      return `${date.getFullYear()}.${date.getMonth() + 1}.${date.getDate()}`;
    },
    setDefaultImage(event) {
      event.target.src =
        "https://goott-bucket.s3.ap-northeast-2.amazonaws.com//noImage.jpg";
    },
    makeACalendar() {
      const calendarEl = document.getElementById("calendar");
      const calendar = new FullCalendar.Calendar(calendarEl, {
        locale: "ko",
        headerToolbar: {
          start: "prev",
          center: "title",
          end: "next",
        },
        events: [],
        clickedDate: null,
        dateClick: (info) => {
          const clickedDate = info.date; // 클릭된 날짜
          const today = new Date(); // 오늘 날짜
          today.setHours(0, 0, 0, 0); // 시간 초기화 (00:00:00)

          // // 오늘 날짜로부터 30일 후의 날짜 계산
          const maxDate = new Date(today);
          maxDate.setDate(today.getDate() + 30);

          // 클릭된 날짜가 오늘 이전인지 확인
          if (clickedDate <= today) {
            return; // 이전 날짜 클릭 시 아무 동작도 하지 않음
          } else if (clickedDate > maxDate) {
            alert("30일 이전의 날짜만 클릭할 수 있습니다.");
            return;
          }
      

          if (this.clickedDate) {
            const previousDateEl = this.clickedDate;
            if (previousDateEl) {
              previousDateEl.style.border = '';
              previousDateEl.style.backgroundColor = ""; // 기본 색으로 초기화
            }
          }
          // 클릭한 날짜의 배경색 변경
          info.dayEl.style.border = "2px solid red";
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
          const todayStr = today.toISOString().split("T")[0]; // 'YYYY-MM-DD' 형식으로 변환

          // 모든 날짜에 대해 배경색 변경
          const dateElements = calendarEl.getElementsByClassName("fc-day");
          for (let dateEl of dateElements) {
            const date = new Date(dateEl.getAttribute("data-date"));
            if (date < today || date > maxDate) {
              dateEl.style.backgroundColor = "#d3d3d3";
            }
          }
        },
      });

      calendar.render();
    },
    goToPage(url) {
      window.location.href = url;
    },
    getClosedDays() {
      return this.schedules
        .filter((schedule) => schedule.closeDay)
        .map((schedule) => schedule.dayOfWeek);
    },
  
    async fetchReserveSlots(date) {
      try {
        const response = await axios.get(
          `/api/stores/reserveSlots/${this.storeId}/${date}`
        );
        this.reserveSlots = response.data.data;
      } catch (error) {
        console.error("Error fetching reserveSlots:", error);
      }
    },
    formatTime(datetime) {
      const date = new Date(datetime);
      const hours = String(date.getHours()).padStart(2, "0"); // 시를 두 자리로 포맷
      const minutes = String(date.getMinutes()).padStart(2, "0"); // 분을 두 자리로 포맷
      return `${hours}:${minutes}`; // 'HH:mm' 형식으로 반환
    },
    menuBtnGotopage(index) {
      //  this.activeButton = index;
      switch (index) {
        case 0:
          this.goToPage(`storeDetail?storeId=${this.storeId}`)
          break;
        case 1:
          this.goToPage(`storeDetails/menu?storeId=${this.storeId}`);
          break;
        case 2:
          this.goToPage(`storeDetails/pictures?storeId=${this.storeId}`);
          break;
        case 3:
          this.goToPage(`storeDetails/reviews?storeId=${this.storeId}`);
          break;
        case 4:
          this.goToPage(`storeDetails/storeInfo?storeId=${this.storeId}`);
          break;
        default:
          break;
      }
    },
    async setBookmark() {
      try {
        const response = await axios.post(`/api/bookmark/${this.storeId}`);
        this.isBookmarked = true;
      } catch (error) {
        console.error("북마크 추가 중 오류발생:", error);
      }
    },
    async deleteBookmark() {
      try {
        const response = await axios.delete(`/api/bookmark/${this.storeId}`);
        this.isBookmarked = false;
      } catch (error) {
        console.error("북마크 삭제 에러", error);
      }
    },
    toggleBookmark() {
      if (this.isLoggedIn == true) {
        if (this.isBookmarked == false) {
          this.setBookmark();
        } else {
          this.deleteBookmark();
        }
      } else {
        this.goToPage(`/view/user/userLogin`);
      }
    },
    async getBookmarkInfo() {
      if (this.isLoggedIn == true) {
        try {
          const response = await axios.get(`/api/bookmark/`);
          this.currentBookmark = response.data.data;
          this.checkIfBookmarked();
          this.checkRecommendBookmarked();
        } catch (error) {
          console.error("북마크 가져오기 에러:", error);
        }
      }
    },

    checkIfBookmarked() {
      for (bookmark of this.currentBookmark) {
        if (bookmark.bookmarkDto.storeId == this.storeId) {
          this.isBookmarked = true;
          break;
        } 
      }
    },

    async checkLoginStatus() {
      try {
        const response = await axios.get("/api/status");
        if (response.status === 401) {
          this.isLoggedIn = false;
        } else if (response.status === 200) {
          this.isLoggedIn = true;
          this.getBookmarkInfo();
        }
      } catch (error) {
        if (error.response && error.response.status === 401) {
          this.isLoggedIn = false;
        } else {
          console.error("로그인 상태 확인 중 오류 발생:", error);
        }
      }
    },
    goToReserve() {
      if (this.isLoggedIn == true) {
         this.deleteCartData();
         this.goToPage(
          `/view/user/storeDetails/reserve?storeId=${this.storeId}`
        );
      } else {
         this.goToPage(`/view/user/userLogin`);
      }
    },
    async deleteCartData(){
      try {
        const response = await axios.get("/api/cart");
        const deleteList = response.data.data.map(cartItem =>{
          const cartId = cartItem.cartId;
           axios.delete(`/api/cart/${cartId}`)
           .catch(error => {
              console.error(`카트에서 ${cartId} 를 삭제하는데 실패했습니다.`);
           });
        });

         await Promise.all(deleteList);
     
      }catch(error){
        console.log("cart 정보를 불러오지 못했습니다");
      }
    },
    async fetchStoreReviewData(recommendId,index) {
      try {
          const response = await axios.get(`/api/review/store/${recommendId}`);
          const storeReview = response.data.data;
      
           // storeReview 객체에 storeId를 키로 사용하여 저장
          this.$set(this.storeReview, recommendId, storeReview);

          const values =Object.values(this.storeReview);
          const averageScore = this.calculateRecommendScore(values);
  
          // averageScoreList에 index에 맞춰 점수 추가
          this.averageScoreList[index] = averageScore;
   
      } catch (error) {
          console.error('Error fetching review data:', error);
      }
    },
    calculateRecommendScore(values) {
      let totalScore = 0;
      let totalCount = 0;
  
      for (const value of values) {
          if (value.length > 0) {
              for (const item of value) {
                  totalScore += item.score; 
                  totalCount++;
              }
          }
      }
  
      // 평균 계산
      return totalCount > 0 ? (totalScore / totalCount) : 0; 
  },

//////////////////////////////recommend//////////////////////////////
async doBookmark(favoriteId) {
  try {
    const response = await axios.post(`/api/bookmark/${favoriteId}`);
    return true;
  } catch (error) {
    console.error("북마크 추가 중 오류발생:", error);
    return false;
  }
},

async undoBookmark(favoriteId) {
  try {
    const response = await axios.delete(`/api/bookmark/${favoriteId}`);
    return true;
  } catch (error) {
    console.error("북마크 삭제 에러", error);
    return false;
  }
},
async clickBookmark(storeId) {
  if (this.isLoggedIn) {
      const recommend = this.recommendData.find(recommend => recommend.storeId === storeId);
      const isBookmarked = recommend?.isBookmarked;

      // UI 업데이트: 북마크 상태 즉시 반영하기
      this.updateBookmark(storeId, !isBookmarked);

      try {
          if (isBookmarked) {
              await this.undoBookmark(storeId);
          } else {
              await this.doBookmark(storeId);
          }
      } catch (error) {
          // API 호출 실패시 이전 상태로 되돌리기
          this.updateBookmark(storeId, isBookmarked);
          console.error("북마크 처리 중 오류 발생:", error);
      }
  } else {
      this.goToPage(`/view/user/userLogin`);
  }
},

updateBookmark(storeId, isFavorite) {
  const recommend = this.recommendData.find(recommend => recommend.storeId === storeId);
  if (recommend) {
    this.$set(recommend, 'isBookmarked', isFavorite);
  }
},

checkRecommendBookmarked() {
  this.recommendData.forEach(recommend => {
    const isBookmarked = this.currentBookmark.some(bookmark => bookmark.bookmarkDto.storeId === recommend.storeId);
    this.$set(recommend, 'isBookmarked', isBookmarked); // Vue의 반응성을 위해 $set 사용
  });
},


  },
  created() {
    this.getTodayDay();
  },
  mounted() {
    const queryParams = new URLSearchParams(window.location.search);
    this.storeId = queryParams.get("storeId");

    if (this.storeId) {
      this.fetchRestaurantData();
    } else {
      console.error("storeId가 없습니다. URL을 확인하세요.");
    }

    this.makeACalendar();
   }
});
