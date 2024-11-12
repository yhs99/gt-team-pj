new Vue({
  el: "#app",
  data: {
    carts: [],
    checkedCarts: [],
    reservationName: "",
    peopleCount: 1,
    note: "",
    isModalOpen: false,
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
          console.log("응답 데이터", this.carts);
        })
        .catch((error) => {
          if (error.response && error.response.status === 401) {
            alert("로그인이 필요한 서비스입니다.");
            window.location.href = "userLogin";
          } else {
            console.error("데이터 요청 실패:", error);
          }
        });
    },
    getStoreImage(urlArray) {
      if (!Array.isArray(urlArray)) return null;
      return urlArray[0] || urlArray[1] || urlArray[2] || null;
    },
    async removeSelectedItems() {
      try {
        await Promise.all(
          this.checkedCarts.map((cart) =>
            axios.delete(`/api/cart/${cart.cartId}`)
          )
        );
        this.carts = this.carts.filter(
          (cart) => !this.checkedCarts.includes(cart)
        );
        this.checkedCarts = [];
        alert("선택된 항목이 삭제되었습니다.");
      } catch (error) {
        console.error("삭제 요청 실패:", error);
        alert("삭제 중 오류가 발생했습니다.");
      }
    },
    openReservationModal() {
      this.isModalOpen = true;
    },
    closeReservationModal() {
      this.isModalOpen = false;
    },
    makeReservation() {
      const reservationData = {
        name: this.reservationName,
        peopleCount: this.peopleCount,
        note: this.note,
        items: this.checkedCarts,
      };
      console.log("예약 정보:", reservationData);
      alert("예약이 완료되었습니다!");
      this.reservationName = "";
      this.peopleCount = 1;
      this.note = "";
      this.closeReservationModal();
    },
  },
});
