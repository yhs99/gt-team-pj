new Vue({
  el: "#app",
  data: {
    showSchedule: false,
    stores: [],
    page: 1,
    perPage: 10,
    isLoading: false,
    hasMore: true,
    scheduleLists: [],
    selectedCategoryCodes: [],
    selectSidoCodes: [],
    searchQuery: "",
    favoriteStoreIds: [],
    loginYN: false,
    isDescriptionVisible: false,
  },
  created() {
    const urlParams = new URLSearchParams(window.location.search);
    this.selectedCategoryCodes = urlParams.get("categoryId")
      ? urlParams.get("categoryId").split(",")
      : [];
    this.selectSidoCodes = urlParams.get("sidoCodeId")
      ? urlParams.get("sidoCodeId").split(",")
      : [];
    this.searchQuery = urlParams.get("searchParam") || "";
    this.showBlock = urlParams.get("showBlock") || "";

    this.initData();
    window.addEventListener("scroll", this.handleScroll);
  },
  destroyed() {
    window.removeEventListener("scroll", this.handleScroll);
  },
  methods: {
    async initData() {
      await this.fetchFavoriteStores();
      this.fetchStore();
    },
    handleScroll() {
      const { scrollTop, clientHeight, scrollHeight } =
        document.documentElement;
      if (scrollTop + clientHeight >= scrollHeight - 10) {
        this.fetchStore();
      }
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

    async fetchStore() {
      if (this.isLoading || !this.hasMore) return;
      this.isLoading = true;

      let url = `/api/searchStores?page=${this.page}&perPage=${this.perPage}`;

      if (this.selectedCategoryCodes.length > 0) {
        const categoryCodes = this.selectedCategoryCodes.join(",");
        url += `&categoryId=${categoryCodes}`;
      }

      if (this.selectSidoCodes.length > 0) {
        const sidoCodes = this.selectSidoCodes.join(",");
        url += `&sidoCodeId=${sidoCodes}`;
      }

      if (this.searchQuery.trim()) {
        const searchQuery = encodeURIComponent(this.searchQuery.trim());
        url += `&searchParam=${searchQuery}`;
      }

      url += `&showBlock=0`;
      try {
        const response = await axios.get(url);
        const storeLists = response.data.data.storeLists;

        const startIndex = (this.page - 1) * this.perPage;
        const endIndex = startIndex + this.perPage;

        const paginatedStores = storeLists.slice(startIndex, endIndex);

        if (paginatedStores.length < this.perPage) {
          this.hasMore = false;
        }

        const uniqueStores = paginatedStores.filter(
          (store) =>
            !this.stores.some(
              (existingStore) => existingStore.storeId === store.storeId
            )
        );

        this.stores = [...this.stores, ...uniqueStores];

        this.page += 1;
      } catch (error) {
      } finally {
        this.isLoading = false;
      }
    },
    async toggleFavorite(store) {
      await this.checkLoginStatus();
      if (!this.loginYN) {
        if (
          confirm("즐겨찾기는 로그인 후에 이용 가능합니다. 로그인하시겠습니까?")
        ) {
          window.location.href = "/login";
        }
        return;
      }

      try {
        if (store.isFavorite) {
          await axios.delete(`/api/bookmark/${store.storeId}`);
          this.updateFavoriteStatus(store.storeId, false);
        } else {
          await axios.post(`/api/bookmark/${store.storeId}`);
          this.updateFavoriteStatus(store.storeId, true);
        }
      } catch (error) {
        alert("즐겨찾기 처리에 실패했습니다. 다시 시도해 주세요.");
      }
    },
    updateFavoriteStatus(storeId, isFavorite) {
      this.stores.forEach((store, index) => {
        if (store.storeId === storeId) {
          Vue.set(this.stores[index], "isFavorite", isFavorite);
        }
      });
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
          return `영업 준비 중 open: ${todaySchedule.open}`;
        } else if (now >= openTime && now < closeTime) {
          return `영업중 open: ${todaySchedule.open} close: ${todaySchedule.close}`;
        } else {
          return `영업마감 close: ${todaySchedule.close}`;
        }
      }
      return "영업 정보 없음";
    },

    goToStore(storeId) {
      window.location.href = `storeDetail?storeId=${storeId}`;
    },
    goToReviewPage(storeId) {
      window.location.href = `storeDetails/reviews?storeId=${storeId}`;
    },
  },
});
