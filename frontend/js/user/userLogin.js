new Vue({
  el: "#app",
  data: {
    id: "",
    password: "",
    loginGroup: 0,
    idError: "",
  },
  created: function () {
    // this.checkLogin();
  },
  methods: {
    checkLogin: function () {
      axios
        .get("/api/status")
        .then((response) => {
          if (response.data.data.loginType == "user") {
            location.href = "/";
          } else {
            location.href = "/view/owner/index";
          }
        })
        .catch((error) => {
          if (!error.status == 401) {
            console.log("로그인 정보가 없습니다.");
          }
        });
    },
    async login() {
      const formData = new FormData();
      formData.append("id", this.id);
      formData.append("password", this.password);
      formData.append("loginGroup", this.loginGroup);
      try {
        const response = await axios.post("/api/login", formData);
        if (response.status === 200) {
          if (this.loginGroup == 0) {
            location.href = "/";
          } else {
            location.href = "/view/owner/index";
          }
        } else if (response.status === 202) {
          location.href = "/view/owner/registerStore";
        }
      } catch (error) {
        console.error("API 요청 실패:", error.response.data.message);
        this.idError = error.response.data.message;
      }
    },
  },
});
