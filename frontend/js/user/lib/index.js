new Vue({
  el: '#app',
  data: {
      userId: "",
      id: "",
      title: "",
      completed: ""
  },
  created() {
      this.fetchData(); // 컴포넌트가 생성될 때 데이터 가져오기
  },
  methods: {
      fetchData() {
          // 외부 API 호출
          axios.get('https://jsonplaceholder.typicode.com/todos/1')
                .then(response => {
                    console.log(response);
                    this.userId = response.data.userId;
                    this.id = response.data.id;
                    this.title = response.data.title;
                    this.completed = response.data.completed;
                })
                .catch(error => {
                    console.log(error);
                })
      }
  }
});

new Vue({
    el: "#sessionTest",
    data: {
      isLoggedIn: false,
    },
    methods: {
      async checkLoginStatus() {
        const sessionId = getCookie("JSESSIONID");
        console.log("쿠키에 저장된 세션 id : " + sessionId);
        if (sessionId) {
          try {
            const response = await axios.get("/api/status", {
              headers: { Cookie: `JSESSIONID=${sessionId}` },
            });
            if (response.status === 200) {
              this.isLoggedIn = true;
            // window.location.href = "/view/user/list.html"; // 로그인 되어 있으면 list.html로 리디렉션
            } else {
              // 로그인 안 되어 있으면 그대로 남아 있는다
              console.log("로그인 되어있지 않음");
            }
          } catch (error) {
            console.error("로그인 상태 체크 중 오류가 발생했습니다.");
          }
        } else {
          // 세션 ID가 없으면 현제 페이지에 그대로 남아 있는다.
          console.log("쿠기가 없다.");
        }
      },
    },
    created() {
      this.checkLoginStatus();
    },
  });
  //쿠키 값 가져오는 함수
  function getCookie(cookieName) {
    // 현재 페이지의 모든 쿠키를 가져옵니다.
    const cookies = document.cookie.split(";");
    console.log(cookies);
    // 각 쿠키를 확인하여 원하는 쿠키명이 있는지 검사합니다.
    for (let i = 0; i < cookies.length; i++) {
      let cookie = cookies[i].trim();
      // 쿠키명이 주어진 쿠키명과 일치하는지 확인합니다.
      if (cookie.startsWith(cookieName + "=")) {
        // 쿠키 값만을 추출하여 반환합니다.
        return cookie.substring(cookieName.length + 1);
      }
    }
    // 해당 쿠키명이 없으면 null을 반환합니다.
    return null;
  }
