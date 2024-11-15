new Vue({
  el: "#app",
  data: {
    stores: [],
    groupStores: [],
    currentImage: "/assets/user/img/한식.png",
    topReviewStores: [],
    eventStores: [],
    currentIndexMain: 0,
    currentIndexGroup1: 0,
    currentIndexGroup2: 0,
    currentIndexGroup3: 0,
    storeSchedules: [],
  },
  computed: {},
  created() {
    this.initData();
  },
  methods: {
    async initData() {
      await this.fetchFavoriteStores();
      this.fetchAllStore();
    },
    async fetchFavoriteStores() {
      try {
        const response = await axios.get("/api/bookmark/");
        this.favoriteStoreIds = response.data.data.map(
          (item) => item.bookmarkDto.storeId
        );
        console.log("Fetched favorite store IDs:", this.favoriteStoreIds);
      } catch (error) {
        console.error("즐겨찾기 목록 가져오기 실패:", error);
        this.favoriteStoreIds = [];
      }
    },
    fetchAllStore() {
      axios
        .get("/api/searchStores")
        .then((response) => {
          const shuffledStores = this.shuffle(response.data.data.storeLists);

          this.stores = shuffledStores.slice(0, 15).map((store) => ({
            ...store,
            isFavorite: this.favoriteStoreIds.includes(store.storeId),
          }));

          this.groupStores = shuffledStores
            .filter((store) => store.maxPeoplePerReserve >= 6)
            .slice(0, 15)
            .map((store) => ({
              ...store,
              isFavorite: this.favoriteStoreIds.includes(store.storeId),
            }));

          this.eventStores = shuffledStores
            .filter((store) => store.couponCount > 0)
            .slice(0, 15)
            .map((store) => ({
              ...store,
              isFavorite: this.favoriteStoreIds.includes(store.storeId),
            }));

          this.filterTopReviewStores();

          this.storeSchedules = response.data.data.storeLists
            .map((store) => store.storeSchedules)
            .flat();

          console.log(this.storeSchedules);
          console.log(this.stores);
          console.log(this.groupStores);
          console.log("리뷰 많은 식당", this.topReviewStores);
        })
        .catch((error) => {
          console.error("데이터 요청 실패:", error);
        });
    },
    filterTopReviewStores() {
      this.topReviewStores = this.stores
        .slice()
        .sort((a, b) => b.reviewCount - a.reviewCount)
        .slice(0, 15);
    },
    shuffle(array) {
      for (let i = array.length - 1; i > 0; i--) {
        const j = Math.floor(Math.random() * (i + 1));
        [array[i], array[j]] = [array[j], array[i]];
      }
      return array;
    },

    truncatedDescription(description) {
      const maxLength = 100;
      description = description || "";
      return description.length > maxLength
        ? description.substring(0, maxLength) + "..."
        : description;
    },
    scrollLeft(refName) {
      this.updateScroll(refName, -1);
    },
    scrollRight(refName) {
      this.updateScroll(refName, 1);
    },
    updateScroll(refName, direction) {
      const container = this.$refs[refName];
      let currentIndexName;
      switch (refName) {
        case "mainCardContainer":
          currentIndexName = "currentIndexMain";
          break;
        case "groupCardContainer1":
          currentIndexName = "currentIndexGroup1";
          break;
        case "groupCardContainer2":
          currentIndexName = "currentIndexGroup2";
          break;
        case "groupCardContainer3":
          currentIndexName = "currentIndexGroup3";
          break;
        default:
          console.error(`Invalid refName: ${refName}`);
          return;
      }

      if (!container || !this.hasOwnProperty(currentIndexName)) {
        return;
      }

      const maxIndex = container.children.length;

      if (
        (this[currentIndexName] > 0 && direction === -1) ||
        (this[currentIndexName] < maxIndex - 1 && direction === 1)
      ) {
        this[currentIndexName] += direction;

        const cardWidth = container.children[0]?.offsetWidth + 10 || 0;

        if (cardWidth > 0) {
          container.scrollBy({
            left: cardWidth * direction,
            behavior: "smooth",
          });
        }
      }
    },
    showMore() {
      window.location.href = "search";
    },
    changeImage(imageUrl) {
      this.currentImage = imageUrl;
    },
    goToStore(storeId) {
      window.location.href = `storeDetail?storeId=${storeId}`;
    },
    async checkLoginStatus() {
      try {
        const response = await axios.get("/api/status");
        if (
          response.data.status === "success" &&
          response.data.data.loginType === "user"
        ) {
          this.loginYN = true;
          this.userName = response.data.data.name;
          this.profileImageUrl = response.data.data.profileImageUrl;
        } else {
          this.loginYN = false;
        }
      } catch (error) {
        this.loginYN = false;
        console.error("로그인 상태 확인 중 오류 발생:", error);
      }
    },
    async toggleFavorite(store) {
      await this.checkLoginStatus();
      if (!this.loginYN) {
        alert("즐겨찾기는 로그인 후에 가능합니다.");
        return;
      }

      try {
        if (store.isFavorite) {
          await axios.delete(`/api/bookmark/${store.storeId}`);
          this.updateFavoriteStatus(store.storeId, false);
          console.log("즐겨찾기에서 제거되었습니다.");
        } else {
          await axios.post(`/api/bookmark/${store.storeId}`);
          this.updateFavoriteStatus(store.storeId, true);
          console.log("즐겨찾기에 추가되었습니다.");
        }
      } catch (error) {
        console.error("즐겨찾기 처리 중 오류 발생:", error);
        alert("즐겨찾기 처리에 실패했습니다. 다시 시도해 주세요.");
      }
    },
    navigateTo(categoryCode) {
      window.location.href = `searchStores?categoryId=${categoryCode}`;
    },
    getStoreStatus(store) {
      const today = new Date().getDay();
      const now = new Date();

      const todaySchedule = store.storeSchedules.find(
        (schedule) => schedule.dayCodeId === today
      );

      if (todaySchedule) {
        if (todaySchedule.closeDay) {
          return "영업마감";
        }
        const [openHour, openMinute] = todaySchedule.open
          .split(":")
          .map(Number);
        const [closeHour, closeMinute] = todaySchedule.close
          .split(":")
          .map(Number);

        const openTime = new Date(
          now.getFullYear(),
          now.getMonth(),
          now.getDate(),
          openHour,
          openMinute
        );
        const closeTime = new Date(
          now.getFullYear(),
          now.getMonth(),
          now.getDate(),
          closeHour,
          closeMinute
        );

        if (now < openTime) {
          return `영업 준비 중 open:${todaySchedule.open}`;
        } else if (now >= openTime && now < closeTime) {
          return `영업중 open:${todaySchedule.open} close:${todaySchedule.close}`;
        } else {
          return `영업마감 close:${todaySchedule.close}`;
        }
      }
      return "영업 정보 없음";
    },
    updateFavoriteStatus(storeId, isFavorite) {
      this.stores.forEach((store, index) => {
        if (store.storeId === storeId) {
          Vue.set(this.stores[index], "isFavorite", isFavorite);
        }
      });

      this.groupStores.forEach((store, index) => {
        if (store.storeId === storeId) {
          Vue.set(this.groupStores[index], "isFavorite", isFavorite);
        }
      });

      this.eventStores.forEach((store, index) => {
        if (store.storeId === storeId) {
          Vue.set(this.eventStores[index], "isFavorite", isFavorite);
        }
      });
    },
  },
});
