new Vue({
  el: "#app",
  data: {
    showSchedule: false,
    stores: [],
    scheduleLists: [],
  },
  computed: {},
  created() {
    this.fetchStore();
  },
  methods: {
    fetchStore() {
      axios
        .get("/api/searchStores")
        .then((response) => {
          this.stores = response.data.data.storeLists;
          this.scheduleLists = response.data.data.storeLists.storeSchedules;
          console.log(this.stores);
        })
        .catch((error) => {
          console.error(error);
        });
    },
    toggleScheduleVisibility() {
      this.showSchedule = !this.showSchedule;
    },
  },
});
