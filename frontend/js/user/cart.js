new Vue({
  el: "#app",
  data: {
    carts: [],
    checkedCarts: [],
    reservationName: "",
    reservationNameError: "",
    peopleCount: 1,
    peopleCountError: "",
    note: "",
    isModalOpen: false,
    globalCoupons: [],
    selectedCoupon: null,
    discountAmount: 0,
    maxPeoplePerReserve: 0,
    selectedPayment: "",
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
    totalPrice() {
      return this.carts.reduce((total, cart) => total + cart.totalPrice, 0);
    },
    isReservationEnabled() {
      return this.checkedCarts.length > 0;
    },
    finalTotalPrice() {
      const discountedPrice = this.totalPrice - this.discountAmount;
      return Math.max(discountedPrice, 0);
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
          if (this.carts.length > 0) {
            this.maxPeoplePerReserve = this.carts[0].maxPeoplePerReserve || 0;
            if (this.carts[0].availableCoupons) {
              this.globalCoupons = this.carts[0].availableCoupons;
            }
          }
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
    makeReservation() {
      const urlParams = new URLSearchParams(window.location.search);
      const reserveTime = urlParams.get("reserveTime");

      this.reservationNameError = "";
      this.peopleCountError = "";

      if (!this.reservationName.trim()) {
        this.reservationNameError = "예약자명을 입력해 주세요.";
        this.$refs.reservationNameInput.focus();
        return;
      }

      if (this.peopleCount < 1 || this.peopleCount > this.maxPeoplePerReserve) {
        this.peopleCountError = `인원수는 1 이상, 최대 ${this.maxPeoplePerReserve}명까지 가능합니다.`;
        this.$refs.peopleCountInput.focus();
        return;
      }

      const reservationData = {
        reserveTime: reserveTime,
        name: this.reservationName,
        people: this.peopleCount,
        memo: this.note,
      };

      if (this.selectedCoupon && this.selectedCoupon.couponId !== null) {
        reservationData.couponId = this.selectedCoupon.couponId;
      }

      console.log("/api/reserve", reservationData);

      axios
        .post("/api/reserve", reservationData, {
          headers: { "Content-Type": "application/json" },
        })
        .then((response) => {
          console.log("예약 성공:", response.data);
          alert("예약이 완료되었습니다!");

          window.location.href = "/";
        })
        .catch((error) => {
          console.error("예약 요청 실패:", error);
          if (
            error.response &&
            error.response.data &&
            error.response.data.message
          ) {
            alert("오류 발생: " + error.response.data.message);
          } else {
            alert(
              "예약 중 알 수 없는 오류가 발생했습니다. 다시 시도해 주세요."
            );
          }
        });

      this.reservationName = "";
      this.peopleCount = 1;
      this.note = "";
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

    applyDiscount() {
      if (this.selectedCoupon && this.selectedCoupon.couponId !== null) {
        const discount = this.selectedCoupon.discount;
        this.discountAmount =
          discount < 100
            ? Math.round(this.totalPrice * (discount / 100))
            : discount;
      } else {
        this.discountAmount = 0;
      }
    },
    selectPayment(method) {
      this.selectedPayment = method;
    },
  },
});
