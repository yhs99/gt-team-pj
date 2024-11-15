new Vue({
  el: "#app",
  data: {
    categories: [],
    sidoCodes: [],
    selectedCategoryCodes: [],
    selectSidoCodes: [],
    searchQuery: "",
  },
  created() {
    this.fetchCategories();
    this.fetchSidoCodes();
  },
  methods: {
    fetchSidoCodes() {
      axios
        .get("/api/stores/storeFilters")
        .then((response) => {
          this.sidoCodes = response.data.data.sidoCodes.map((sidoCodes) => {
            return {
              ...sidoCodes,
              isActive: false,
            };
          });
          console.log(this.sidoCodes);
        })
        .catch((error) => {
          console.error(error);
        });
    },
    fetchCategories() {
      axios
        .get("/api/stores/storeFilters")
        .then((response) => {
          this.categories = response.data.data.categories.map((categories) => {
            return {
              ...categories,
              isActive: false,
            };
          });
          console.log(this.categories);
        })
        .catch((error) => {
          console.error(error);
        });
    },
    toggleSido(sido) {
      sido.isActive = !sido.isActive;
      if (sido.isActive) {
        this.selectSidoCodes.push(sido.sidoCodeId);
      } else {
        this.selectSidoCodes = this.selectSidoCodes.filter(
          (code) => code !== sido.sidoCodeId
        );
      }
    },
    toggleCategory(category) {
      category.isActive = !category.isActive;
      if (category.isActive) {
        this.selectedCategoryCodes.push(category.categoryCodeId);
      } else {
        this.selectedCategoryCodes = this.selectedCategoryCodes.filter(
          (code) => code !== category.categoryCodeId
        );
      }
    },
    search(event) {
      event.preventDefault();

      const categoryCodes = this.selectedCategoryCodes.join(",");
      const sidoCodes = this.selectSidoCodes.join(",");
      const searchQuery = this.searchQuery.trim()
        ? encodeURIComponent(this.searchQuery.trim())
        : "";

      let newUrl = "searchStores";
      if (categoryCodes) {
        newUrl +=
          (newUrl.includes("?") ? "&" : "?") + `categoryId=${categoryCodes}`;
      }
      if (sidoCodes) {
        newUrl += `${newUrl.includes("?") ? "&" : "?"}sidoCodeId=${sidoCodes}`;
      }
      if (searchQuery) {
        newUrl += `${
          newUrl.includes("?") ? "&" : "?"
        }searchParam=${searchQuery}`;
      }
      window.location.href = newUrl;
    },
  },
});
