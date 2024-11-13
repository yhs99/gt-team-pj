new Vue({
  el: "#app",
  data: {
    showSchedule: false,
    stores: [],
    page: 1,
    perPage: 10,
    scheduleLists: [],
    selectedCategoryCodes: [],
    selectSidoCodes: [],
    searchQuery: "",
  },
  computed: {},
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
    this.fetchStore();
  },
  methods: {
    fetchStore() {
      const categoryCodes = this.selectedCategoryCodes.join(",");
      const sidoCodes = this.selectSidoCodes.join(",");
      let searchQuery = this.searchQuery.trim();

      // 끝에 붙은 '}'를 제거합니다.
      if (searchQuery.endsWith("}")) {
        searchQuery = searchQuery.slice(0, -1);
      }

      let url = "/api/searchStores";
      let params = [];

      // 카테고리와 시도 코드 파라미터 추가
      if (categoryCodes) params.push(`categoryId=${categoryCodes}`);
      if (sidoCodes) params.push(`sidoCodeId=${sidoCodes}`);

      // 검색어가 있을 때만 인코딩하여 searchParam에 추가
      if (searchQuery) {
        params.push(`searchParam=${encodeURIComponent(searchQuery)}`);
      }

      // showBlock 파라미터 추가
      params.push("showBlock=0");

      // 최종 URL 생성
      url += "?" + params.join("&");

      // 요청 URL 로그 확인
      console.log("요청 URL: ", url);
      axios
        .get(url)
        .then((response) => {
          this.stores = response.data.data.storeLists;
          this.scheduleLists = response.data.data.storeLists.storeSchedules;
          console.log(this.stores);
        })
        .catch((error) => {
          console.error(error);
        });
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
        store.isFavorite = !store.isFavorite;
        if (store.isFavorite) {
          await axios.post(`/api/bookmark/${store.storeId}`);
          console.log("즐겨찾기 상태가 추가되었습니다.");
        } else {
          await axios.delete(`/api/bookmark/${store.storeId}`);
          console.log("즐겨찾기 상태가 삭제되었습니다.");
        }
      } catch (error) {
        store.isFavorite = !store.isFavorite;
        console.error("즐겨찾기 처리 중 오류 발생:", error);
      }
    },
  },
});
