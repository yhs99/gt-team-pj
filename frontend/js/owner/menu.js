new Vue({
  el: "#vueApp",
  data: {
    allMenu: {},
    numOfAllMenu: 0,
    numOfSideMenu: 0,
    numOfMainMenu: 0,
    filteredMenu: [],
    showAddModal: false,
    showModifyModal: false,
    showDeleteModal: false,
    newMenu: {
      menuName: "",
      price: "",
      description: "",
      main: true,
    },
    modifyMenuList: {
      menuName: "",
      price: "",
      description: "",
      main: true,
    },
    file: null,
    modifyMenuId: 0,
    showMenuImageInput: false,
    deleteMenuId: 0,
  },
  created: function () {
    this.fetchMenu();
  },
  methods: {
    fetchMenu: function () {
      axios.get("/api/owner/menu").then((response) => {
        console.log(response);
        this.allMenu = response.data.data.menu;
        console.log(this.allMenu);
        this.numOfAllMenu = response.data.data.numOfAllMenu;
        this.numOfSideMenu = response.data.data.numOfSideMenu;
        this.numOfMainMenu = response.data.data.numOfMainMenu;
        this.filteredMenu = this.allMenu;
      });
    },

    fileChange(e) {
      const imgFile = e.target.files[0];
      if (imgFile) {
        const maxSize = 10 * 1024 * 1024;
        if (imgFile.size > maxSize) {
          alert("이미지는 10MB이하의 파일만 업로드 가능합니다");
          e.target.value = "";
          return;
        } else {
          this.file = imgFile;
        }
      } else {
        this.file = null;
      }
    },

    addMenu() {
      const formData = new FormData();
      console.log(this.newMenu);
      const json = JSON.stringify({
        description: this.newMenu.description,
        menuName: this.newMenu.menuName,
        price: this.newMenu.price,
        main: this.newMenu.main,
      });
      const blob = new Blob([json], { type: "application/json" });
      formData.append("menu", blob);

      if (this.file) {
        formData.append("file", this.file);
      }

      console.log(formData);
      console.log(this.file);
      axios
        .post("/api/owner/menu", formData)
        .then((response) => {
          console.log(response);
          this.showAddModal = false;
          alert("메뉴가 성공적으로 추가되었습니다.");
          location.reload();
        })
        .catch((error) => {
          console.error(error);
        });
    },
    modifyMenu(menuId) {
      const formData = new FormData();
      const json = JSON.stringify({
        description: this.modifyMenuList.description,
        menuName: this.modifyMenuList.menuName,
        price: this.modifyMenuList.price,
        main: this.modifyMenuList.main,
      });
      const blob = new Blob([json], { type: "application/json" });
      formData.append("menu", blob);

      if (this.file) {
        formData.append("file", this.file);
      }

      console.log(this.file);

      axios
        .put("/api/owner/menu/" + menuId, formData)
        .then((response) => {
          console.log(response);
          this.showModifyModal = false;
          alert("메뉴가 성공적으로 수정되었습니다.");
          location.reload();
        })
        .catch((error) => {
          console.error(error);
        });
    },
    deleteMenu() {
      console.log(this.deleteMenuId);

      axios.delete("/api/owner/menu/" + this.deleteMenuId).then((response) => {
        console.log(response);
        alert("메뉴가 성공적으로 삭제되었습니다.");
        location.reload();
      });
    },

    showAllMenu() {
      this.filteredMenu = this.allMenu;
    },
    showMainMenu() {
      this.filteredMenu = this.allMenu.filter((menu) => menu.main);
    },
    showSideMenu() {
      this.filteredMenu = this.allMenu.filter((menu) => !menu.main);
    },
    showAddMenuModal() {
      console.log("modal");
      this.showAddModal = true;
    },
    showModifyMenuModal(menuId) {
      this.showModifyModal = true;
      this.modifyMenuId = menuId;
      let menu = this.allMenu.filter((menu) => menu.menuId == menuId);
      console.log(menu);
      this.modifyMenuList.menuName = menu[0].menuName;
      this.modifyMenuList.price = menu[0].price;
      this.modifyMenuList.description = menu[0].description;
      this.modifyMenuList.main = menu[0].main;
    },
    showDeleteMenuModal(menuId) {
      this.showDeleteModal = true;
      this.deleteMenuId = menuId;
    },
  },
  mounted() {},
});
