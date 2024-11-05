new Vue({
  el: "#app",
  data: {
    carts: [],
    checkedCarts: [],
  },
  computed: {
    groupedCarts() {
      return this.carts.reduce((groups, cart) => {
        if (!groups[cart.storeName]) {
          groups[cart.storeName] = [];
        }
        groups[cart.storeName].push(cart);
        return groups;
      }, {});
    },
    totalCheckedCount() {
      return this.checkedCarts.length;
    },
    totalCheckedPrice() {
      return this.checkedCarts.reduce((total, cart) => {
        return total + cart.totalPrice;
      }, 0);
    },
    isReservationEnabled() {
      return this.checkedCarts.length > 0;
    },
  },
  created() {
    this.fetchUserCart();
  },
  methods: {
    fetchUserCart() {
      axios
        .get("/api/cart")
        .then((response) => {
          this.carts = response.data.data;
          console.log(this.carts); // 확인용
        })
        .catch((error) => {
          if (error.response && error.response.status === 401) {
            alert("로그인이 필요한 서비스입니다."); // 401 오류일 경우
            window.location.href = "userLogin"; // 로그인 페이지로 리디렉션
          } else {
            console.error("데이터 요청 실패:", error);
          }
        });
    },
    async removeSelectedItems() {
      try {
        await Promise.all(
          this.checkedCarts.map((cart) =>
            axios.delete(`/api/cart/${cart.cartId}`)
          )
        );
        // 삭제가 완료되면 `carts` 배열에서 선택된 항목 제거
        this.carts = this.carts.filter(
          (cart) => !this.checkedCarts.includes(cart)
        );
        this.checkedCarts = []; // 선택된 항목 초기화
        alert("선택된 항목이 삭제되었습니다.");
      } catch (error) {
        console.error("삭제 요청 실패:", error);
        alert("삭제 중 오류가 발생했습니다.");
      }
    },
    makeReservation() {
      // 예약 처리 로직
      alert("예약이 완료되었습니다!");
    },
  },
});
