new Vue({
  el: "#app",
  data: {
    stores: [],
    groupStores: [],
    currentIndexMain: 0,
    currentIndexGroup: 0,
  },
  computed: {},
  created() {
    this.fetchAllStore();
  },
  methods: {
    fetchAllStore() {
      axios
        .get("/api/searchStores")
        .then((response) => {
          this.stores = response.data.data.storeLists;
          this.groupStores = response.data.data.storeLists.filter(
            (store) => store.maxPeoplePerReserve >= 6
          );
          console.log(this.stores);
          console.log(this.groupStores);
        })
        .catch((error) => {
          console.error(error);
        });
    },

    truncatedDescription(description) {
      const maxLength = 60;
      description = description || "";
      return description.length > maxLength
        ? description.substring(0, maxLength) + "..."
        : description;
    },
    scrollLeft(slider) {
      this.updateScroll(slider, -1);
    },
    scrollRight(slider) {
      this.updateScroll(slider, 1);
    },
    updateScroll(slider, direction) {
      const container =
        slider === "main"
          ? this.$refs.mainCardContainer
          : this.$refs.groupCardContainer;
      const currentIndex =
        slider === "main" ? "currentIndexMain" : "currentIndexGroup";
      const maxIndex =
        slider === "main" ? this.stores.length : this.groupStores.length;

      if (
        (this[currentIndex] > 0 && direction === -1) ||
        (this[currentIndex] < maxIndex - 1 && direction === 1)
      ) {
        this[currentIndex] += direction;
        const cardWidth = container.children[0].offsetWidth + 10; // 카드 너비 + 간격
        container.scrollBy({
          left: cardWidth * direction,
          behavior: "smooth",
        });
      }
    },
    showMore() {
      window.location.href = "search";
    },
  },
});
