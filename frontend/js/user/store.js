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
      } catch (error) {
        this.favoriteStoreIds = [];
      }
    },
    fetchAllStore() {
      axios
        .get("/api/searchStores")
        .then((response) => {
          const filteredStores = response.data.data.storeLists.filter(
            (store) => !store.blocked
          );
          const shuffledStores = this.shuffle(filteredStores);

          this.stores = shuffledStores.slice(0, 15).map((store) => ({
            ...store,
            isFavorite: this.favoriteStoreIds.includes(store.storeId),
          }));

          this.groupStores = this.shuffle(shuffledStores)
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

          this.filterTopReviewStores(shuffledStores);

          this.storeSchedules = response.data.data.storeLists
            .map((store) => store.storeSchedules)
            .flat();
        })
        .catch((error) => {});
    },
    filterTopReviewStores(stores) {
      this.topReviewStores = stores
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
      const maxLength = 200;

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
        } else {
          if (this.favoriteStoreIds.length >= 30) {
            alert("즐겨찾기는 최대 30개까지만 가능합니다.");
            return;
          }
          await axios.post(`/api/bookmark/${store.storeId}`);
          this.updateFavoriteStatus(store.storeId, true);
        }
      } catch (error) {
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
