new Vue({
  el: "#vueApp",
  data: {
    allMenu: {},
    numOfAllMenu: 0,
    numOfSideMenu: 0,
    numOfMainMenu: 0,
  },
  created: function () {
    this.fetchMenu();
  },
  methods: {
    fetchMenu: function () {
      axios.get("/api/owner/menu").then((response) => {
        console.log(response);
        this.allMenu = response.data.data;
        console.log(this.allMenu);
        this.numOfAllMenu = response.data.data.numOfAllMenu;
        this.numOfSideMenu = response.data.data.numOfSideMenu;
        this.numOfMainMenu = response.data.data.numOfMainMenu;
      });
    },
  },
});
